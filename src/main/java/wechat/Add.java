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

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Servlet implementation class Add
 */
@WebServlet("/Add")
public class Add extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public Add() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		//response.getWriter().append("Served at: ").append(request.getContextPath());
		request.getRequestDispatcher("/add.html").forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");

		ObjectMapper mapper = new ObjectMapper();
		Map<String, Object> resultData = new HashMap<>();

		String email = request.getParameter("email");
		String password = request.getParameter("password");

		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection conn = DriverManager.getConnection(Constants.databaseUrl, Constants.databaseUser,
					Constants.databasePassword);
			PreparedStatement pstmt = null;
			String sql = "INSERT INTO user_v1 (email, password, `NAME`, "
					+ "`birthday`,`gender`, `STATUS`, `updateTime`) VALUES "
					+ "(?, ?, null,null,null,null,CURRENT_TIMESTAMP)";

			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, email);
			pstmt.setString(2, password);

			System.out.println("SQL try to execute: " + pstmt.toString());
			int rows = pstmt.executeUpdate();

			if (rows > 0) {
				resultData.put("status", "success");
				resultData.put("message", "ユーザー登録成功しました。" + "<a herf='/login.html'></a>");
			} else {
				resultData.put("status", "fail");
				resultData.put("message", "failed to register");
			}
			conn.close();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			resultData.put("status", "error");
			resultData.put("message", "データベース接続できない");
		} catch (SQLException e) {
			e.printStackTrace();

			if (e.getErrorCode() == 1062) {
				resultData.put("status", "error");
				resultData.put("message", "このユーザーは友達です");
			} else {
				resultData.put("status", "error");
				resultData.put("message", "データベース接続できない");
			}

		}

		String jsonString = mapper.writeValueAsString(resultData);
		response.getWriter().write(jsonString);

	}

}
