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

/**
 * Servlet implementation class FriendChatSend
 */
@WebServlet("/FriendChatSend")
public class FriendChatSend extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public FriendChatSend() {
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
		Map<String, Object> result = new HashMap<>();

		String senderEmail = (String) session.getAttribute("email");
		String receiverEmail = request.getParameter("receiverEmail");
		String content = request.getParameter("sendChatContent");

		if (senderEmail == null || content == null || content.trim().isEmpty()) {
			response.setStatus(422);
			return;
		}

		String sql = "INSERT INTO messages (sender_email, receiver_email, content) VALUES (?, ?, ?)";

		try (Connection conn = DriverManager.getConnection(Constants.databaseUrl, Constants.databaseUser,
				Constants.databasePassword);
				PreparedStatement stmt = conn.prepareStatement(sql)) {
			System.out.println("SQL try to execute: " + stmt.toString());
			stmt.setString(1, senderEmail);
			stmt.setString(2, receiverEmail);
			stmt.setString(3, content);

			stmt.executeUpdate();
			result.put("status", "success");
		} catch (SQLException e) {
			result.put("status", "error");
			result.put("message", e.getMessage());
		}
		response.getWriter().write(mapper.writeValueAsString(result));
	}

}
