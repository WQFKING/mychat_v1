package wechat;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Servlet implementation class Login
 */
@WebServlet("/Login")
public class Login extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public Login() {
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
			String sql = "select `email`, `password` from user_v1 where `email` = ? and `password` = ?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, email);
			pstmt.setString(2, password);

			System.out.println("SQL try to execute: " + pstmt.toString());
			ResultSet rs = pstmt.executeQuery();

			if (rs.next()) {
				resultData.put("status", "success");
				resultData.put("email", email);
				resultData.put("message", "User login successfully");
				HttpSession session = request.getSession(true);
				session.setAttribute("email", email);
			} else {
				resultData.put("status", "fail");
				resultData.put("message", "incorrect password or mail address");
			}

		} catch (Exception e) {
			e.printStackTrace();
			resultData.put("status", "error");
			resultData.put("message", e.getMessage());
		}
		String jsonString = mapper.writeValueAsString(resultData);
		response.getWriter().write(jsonString);

	}

}
