package wechat;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import com.fasterxml.jackson.databind.ObjectMapper;

@WebServlet("/Friend/Add")
public class FriendAdd extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public FriendAdd() {
		super();

	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");

		ObjectMapper mapper = new ObjectMapper();
		Map<String, Object> resultData = new HashMap<>();

		String friendEmail = request.getParameter("friendEmail");
		HttpSession session = request.getSession();

		String myEmail = (String) session.getAttribute("email");

		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection conn = DriverManager.getConnection(Constants.databaseUrl, Constants.databaseUser,
					Constants.databasePassword);
			PreparedStatement pstmt = null;
			String sql = "INSERT INTO `friend` (`from_user`, `to_user`) "
					+ "SELECT ?, ? "
					+ "WHERE NOT EXISTS ( "
					+ "    SELECT 1 FROM `friend` WHERE "
					+ "    (`from_user` = ? AND `to_user` = ?) OR (`from_user` = ? AND `to_user` = ?)"
					+ ")";

			pstmt = conn.prepareStatement(sql);

			pstmt.setString(1, myEmail);
			pstmt.setString(2, friendEmail);

			pstmt.setString(3, myEmail);
			pstmt.setString(4, friendEmail);
			pstmt.setString(5, friendEmail);
			pstmt.setString(6, myEmail);

			System.out.println("SQL try to execute: " + pstmt.toString());
			int rows = pstmt.executeUpdate();

			if (rows > 0) {
				resultData.put("status", "success");
				resultData.put("message", "フレンド登録成功しました。");
			} else {
				resultData.put("status", "fail");
				resultData.put("message", "このユーザーは既に友達です");
			}
			conn.close();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			resultData.put("status", "error");
			resultData.put("message", "データベース接続できない");
		} catch (SQLException e) {
			e.printStackTrace();
			resultData.put("status", "error");
			resultData.put("message", "データベース接続できない");
		}

		String jsonString = mapper.writeValueAsString(resultData);
		response.getWriter().write(jsonString);
	}

}
