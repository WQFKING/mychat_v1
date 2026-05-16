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

@WebServlet("/FriendGroupMessageCommentAdd")
public class FriendGroupMessageCommentAdd extends HttpServlet {

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		// 1. 设置响应格式和对象映射
		response.setContentType("application/json;charset=utf-8");
		HttpSession session = request.getSession();
		ObjectMapper mapper = new ObjectMapper();
		Map<String, Object> result = new HashMap<>();

		// 2. 获取 Session 中的发送者邮箱
		String senderEmail = (String) session.getAttribute("email");

		// 3. 获取 POST 请求参数
		String messageIdStr = request.getParameter("friendGroupMessageId");
		String commentContent = request.getParameter("friendGroupMessageComment");

		// 4. 数据非空与合法性校验
		if (senderEmail == null || commentContent == null || commentContent.trim().isEmpty()
				|| messageIdStr == null || messageIdStr.trim().isEmpty()) {
			response.setStatus(422); // 参数不合法或未登录
			return;
		}

		// 5. 将动态 ID 转为整数（对应数据库的 INT 类型）
		int friendGroupMessageId;
		try {
			friendGroupMessageId = Integer.parseInt(messageIdStr);
		} catch (NumberFormatException e) {
			response.setStatus(422); // ID 格式不是数字
			return;
		}

		// 6. SQL 插入语句（使用刚刚为你生成的评论表结构）
		String sql = "INSERT INTO friend_group_message_comment " +
				"(friend_group_message_id, sender_email, friend_group_message_comment) " +
				"VALUES (?, ?, ?)";

		// 7. 连接数据库并执行操作
		try (Connection conn = DriverManager.getConnection(Constants.databaseUrl, Constants.databaseUser,
				Constants.databasePassword);
				PreparedStatement stmt = conn.prepareStatement(sql)) {

			System.out.println("SQL try to execute: " + stmt.toString());

			// 注意这里第一个参数是 Integer 类型类型，对应 PHP 的 "i"
			stmt.setInt(1, friendGroupMessageId);
			stmt.setString(2, senderEmail);
			stmt.setString(3, commentContent);

			stmt.executeUpdate();
			result.put("status", "success");

		} catch (SQLException e) {
			// 数据库操作失败时的异常捕获（如：由于外键约束，评论了一条不存在的动态）
			result.put("status", "error");
			result.put("message", e.getMessage());
			response.setStatus(500);
		}

		// 8. 将结果 Map 序列化为 JSON 并写入响应流
		mapper.writeValue(response.getWriter(), result);
	}
}