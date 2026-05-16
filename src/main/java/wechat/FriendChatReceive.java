package wechat;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Servlet implementation class FriendChatReceive
 */
@WebServlet("/FriendChatReceive")
public class FriendChatReceive extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public FriendChatReceive() {
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
		String myEmail = (String) session.getAttribute("email");
		String friendEmail = request.getParameter("friendEmail");

		// 存放消息的列表
		List<Map<String, Object>> messages = new ArrayList<>();

		String sql = "SELECT sender_email, content FROM messages " +
				"WHERE (sender_email = ? AND receiver_email = ?) " +
				"OR (sender_email = ? AND receiver_email = ?) ORDER BY created_at ASC";

		try (Connection conn = DriverManager.getConnection(Constants.databaseUrl, Constants.databaseUser,
				Constants.databasePassword);
				PreparedStatement stmt = conn.prepareStatement(sql)) {

			stmt.setString(1, myEmail);
			stmt.setString(2, friendEmail);
			stmt.setString(3, friendEmail);
			stmt.setString(4, myEmail);

			try (ResultSet rs = stmt.executeQuery()) {
				System.out.println("SQL try to execute: " + stmt.toString());
				while (rs.next()) {
					Map<String, Object> msg = new HashMap<>();
					msg.put("content", rs.getString("content"));
					// 判断这条消息是不是我发的，用于前端气泡左右对齐
					msg.put("isMine", rs.getString("sender_email").equals(myEmail));
					messages.add(msg);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		// 使用 Jackson 将 List 转为 JSON
		new ObjectMapper().writeValue(response.getWriter(), Map.of("messages", messages));
	}

}
