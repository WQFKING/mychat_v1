package wechat;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Servlet implementation class FriendGroupMessage
 */
@WebServlet("/FriendGroupMessage")
public class FriendGroupMessage extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public FriendGroupMessage() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType("application/json;charset=utf-8");
		HttpSession session = request.getSession();
		ObjectMapper mapper = new ObjectMapper();

		// 1. セッションからログインユーザーのメールアドレスを取得
		String loginUserEmail = (String) session.getAttribute("email");
		if (loginUserEmail == null) {
			response.setStatus(401); // 未認証エラー
			return;
		}

		// JSONとして返却するメインの投稿リスト
		List<Map<String, Object>> messages = new ArrayList<>();

		// 2. メインの投稿（TimeLine）を取得するSQL
		String sql = "SELECT id, sender_email, friend_group_content, created_at " +
				"FROM friend_group_message " +
				"WHERE sender_email = ? " +
				"   OR sender_email IN (" +
				"       SELECT CASE WHEN from_user = ? THEN to_user ELSE from_user END " +
				"       FROM friend " +
				"       WHERE from_user = ? OR to_user = ?" +
				"   ) " +
				"ORDER BY created_at DESC";
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		try (Connection conn = DriverManager.getConnection(Constants.databaseUrl, Constants.databaseUser,
				Constants.databasePassword);
				PreparedStatement stmt = conn.prepareStatement(sql)) {

			// 4つのプレースホルダにメールアドレスをバインド
			stmt.setString(1, loginUserEmail);
			stmt.setString(2, loginUserEmail);
			stmt.setString(3, loginUserEmail);
			stmt.setString(4, loginUserEmail);

			try (ResultSet rs = stmt.executeQuery()) {
				while (rs.next()) {
					Map<String, Object> msg = new HashMap<>();
					msg.put("id", rs.getInt("id"));
					msg.put("sender_email", rs.getString("sender_email"));
					msg.put("friend_group_content", rs.getString("friend_group_content"));
					msg.put("created_at", rs.getTimestamp("created_at").toString());
					// コメント用の一時的な空リストを追加しておく
					msg.put("comments", new ArrayList<>());
					messages.add(msg);
				}
			}

			// 3. 投稿が存在する場合のみ、紐づくコメントを取得
			if (!messages.isEmpty()) {
				// 全ての投稿IDをリスト化
				List<Integer> messageIds = messages.stream()
						.map(m -> (Integer) m.get("id"))
						.collect(Collectors.toList());

				// IN (?, ?, ...) のプレースホルダを動的に生成
				String placeholders = String.join(",", Collections.nCopies(messageIds.size(), "?"));

				String commentSql = "SELECT id, friend_group_message_id, sender_email, " +
						"friend_group_message_comment, created_at " +
						"FROM friend_group_message_comment " +
						"WHERE friend_group_message_id IN (" + placeholders + ") " +
						"ORDER BY created_at DESC";

				try (PreparedStatement commentStmt = conn.prepareStatement(commentSql)) {
					// 動的プレースホルダにIDをバインド
					for (int i = 0; i < messageIds.size(); i++) {
						commentStmt.setInt(i + 1, messageIds.get(i));
					}

					// コメントを投稿IDごとにグループ化するためのMap
					Map<Integer, List<Map<String, Object>>> commentsMap = new HashMap<>();

					try (ResultSet commentRs = commentStmt.executeQuery()) {
						while (commentRs.next()) {
							int msgId = commentRs.getInt("friend_group_message_id");

							Map<String, Object> comment = new HashMap<>();
							comment.put("id", commentRs.getInt("id"));
							comment.put("friend_group_message_id", msgId);
							comment.put("sender_email", commentRs.getString("sender_email"));
							comment.put("friend_group_message_comment",
									commentRs.getString("friend_group_message_comment"));
							comment.put("created_at", commentRs.getTimestamp("created_at").toString());

							// Mapに投稿IDごとのリストを作って格納
							commentsMap.computeIfAbsent(msgId, k -> new ArrayList<>()).add(comment);
						}
					}

					// 4. 各投稿データに該当するコメントリストをマージ
					for (Map<String, Object> msg : messages) {
						int msgId = (Integer) msg.get("id");
						if (commentsMap.containsKey(msgId)) {
							msg.put("comments", commentsMap.get(msgId));
						}
					}
				}
			}

			// 5. Jacksonを使ってListをJSON文字列に変換し、レスポンスに出力
			mapper.writeValue(response.getWriter(), messages);

		} catch (SQLException e) {
			response.setStatus(500);
			Map<String, String> errorResult = new HashMap<>();
			errorResult.put("status", "error");
			errorResult.put("message", e.getMessage());
			mapper.writeValue(response.getWriter(), errorResult);
		}
	}

}
