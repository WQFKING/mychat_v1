package wechat;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;

@WebServlet("/Friend/Add/Search")
public class FriendAddSearch extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public FriendAddSearch() {
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

		String email = request.getParameter("friendEmail");

		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection conn = DriverManager.getConnection(Constants.databaseUrl, Constants.databaseUser,
					Constants.databasePassword);
			PreparedStatement pstmt = null;
			String sql = "select `email` from user_v1 where `email` = ?";

			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, email);

			System.out.println("SQL try to execute: " + pstmt.toString());
			ResultSet rs = pstmt.executeQuery();

			if (rs.next()) {
				resultData.put("status", "success");
				resultData.put("email", email);
				resultData.put("message", "success");

			} else {
				resultData.put("status", "fail");
				resultData.put("message", "fail");
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
