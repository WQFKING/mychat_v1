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
 * Servlet implementation class My
 */
@WebServlet("/My")
public class My extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public My() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.getRequestDispatcher("/my.html").forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		HttpSession session = request.getSession(true);
		String email = (String) session.getAttribute("email");
		String msg = "";
		ObjectMapper mapper = new ObjectMapper();
		Map<String, Object> resultData = new HashMap<>();
		if (email == null || email.equals("")) {
			msg = "このメールアドレスが登録されていません";
			resultData.put("status", "error");
			resultData.put("message", msg);

		} else {
			String password = "";

			try {
				Class.forName("com.mysql.cj.jdbc.Driver");
				Connection conn = DriverManager.getConnection(Constants.databaseUrl, Constants.databaseUser,
						Constants.databasePassword);
				PreparedStatement pstmt = null;
				String sql = "select `email`, `password` from user_v1 where `email` = ?";
				pstmt = conn.prepareStatement(sql);
				pstmt.setString(1, email);

				System.out.println("SQL try to execute: " + pstmt.toString());
				ResultSet rs = pstmt.executeQuery();
				while (rs.next()) {
					email = rs.getString("email");
					password = rs.getString("password");
				}
				if (email == null || password == null) {
					msg = "このメールアドレスが登録されていません";
					resultData.put("status", "error");
					resultData.put("message", msg);
				} else {
					msg = email + "/" + password;
					resultData.put("status", "success");
					resultData.put("message", msg);
				}

			} catch (Exception e) {
				e.printStackTrace();
				msg = "データベースアクセスできない";
				resultData.put("status", "error");
				resultData.put("message", msg);
			}
		}
		String jsonString = mapper.writeValueAsString(resultData);
		response.getWriter().write(jsonString);

	}

}
