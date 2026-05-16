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
 * Servlet implementation class MyFriends
 */
@WebServlet("/MyFriends")
public class MyFriends extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public MyFriends() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		response.setContentType("application/json; charset=utf-8");
		ObjectMapper mapper = new ObjectMapper();
		Map<String, Object> result = new HashMap<>();

		HttpSession session = request.getSession();
		String loginUserEmail = (String) session.getAttribute("email");

		if (loginUserEmail == null || loginUserEmail.isEmpty()) {
			response.setStatus(422);
			result.put("message", "user not login");
			response.getWriter().write(mapper.writeValueAsString(result));
			return;
		}

		String sql = "SELECT `from_user`, `to_user` FROM `friend` WHERE `from_user` = ? OR `to_user` = ?";

		try (Connection conn = DriverManager.getConnection(Constants.databaseUrl, Constants.databaseUser,
				Constants.databasePassword);
				PreparedStatement stmt = conn.prepareStatement(sql)) {

			stmt.setString(1, loginUserEmail);
			stmt.setString(2, loginUserEmail);

			try (ResultSet rs = stmt.executeQuery()) {
				System.out.println("SQL try to execute: " + stmt.toString());
				List<String> friendList = new ArrayList<>();
				boolean hasData = false;

				while (rs.next()) {
					hasData = true;
					String fromUser = rs.getString("from_user");
					String toUser = rs.getString("to_user");

					if (toUser.equals(loginUserEmail)) {
						friendList.add(fromUser);
					} else {
						friendList.add(toUser);
					}
				}

				if (hasData) {
					result.put("message", friendList);
				} else {
					result.put("message", "fail");
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
			response.setStatus(500);
			result.put("message", "database error");
		}

		response.getWriter().write(mapper.writeValueAsString(result));
	}

}
