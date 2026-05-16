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

@WebServlet("/FriendGroupMessageAdd")
public class FriendGroupMessageAdd extends HttpServlet {

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		// 1. 设置响应格式和对象映射
		response.setContentType("application/json;charset=utf-8");
		HttpSession session = request.getSession();
		ObjectMapper mapper = new ObjectMapper();
		Map<String, Object> result = new HashMap<>();

		// 2. 获取 Session 中的发送者邮箱和请求中的动态内容
		String senderEmail = (String) session.getAttribute("email");
		String friendGroupContent = request.getParameter("friendGroupContent");

		// 3. 数据非空校验
		if (senderEmail == null || friendGroupContent == null || friendGroupContent.trim().isEmpty()) {
			response.setStatus(422);
			return;
		}

		// 4. SQL 插入语句
		String sql = "INSERT INTO friend_group_message (sender_email, friend_group_content) VALUES (?, ?)";

		// 5. 连接数据库并执行操作
		try (Connection conn = DriverManager.getConnection(Constants.databaseUrl, Constants.databaseUser,
				Constants.databasePassword);
				PreparedStatement stmt = conn.prepareStatement(sql)) {

			System.out.println("SQL try to execute: " + stmt.toString());
			stmt.setString(1, senderEmail);
			stmt.setString(2, friendGroupContent);

			stmt.executeUpdate();
			result.put("status", "success");

		} catch (SQLException e) {
			// 数据库操作失败时的异常捕获
			result.put("status", "error");
			result.put("message", e.getMessage());
			response.setStatus(500);
		}

		// 6. 将结果 Map 序列化为 JSON 并写入响应流
		mapper.writeValue(response.getWriter(), result);
	}
}