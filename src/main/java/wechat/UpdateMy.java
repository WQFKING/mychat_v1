package wechat;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * Servlet implementation class UpdateMy
 */
@WebServlet("/UpdateMy")
public class UpdateMy extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public UpdateMy() {
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
		String email = request.getParameter("new_email");
		String password = request.getParameter("new_password");

		//System.out.println(email+" | "+password);

		response.setContentType("text/html;charset=UTF-8");

		String msg = "";

		HttpSession session = request.getSession(true);

		String old_email = (String) session.getAttribute("email");

		try {

			Connection conn = DriverManager.getConnection(Constants.databaseUrl, Constants.databaseUser,
					Constants.databasePassword);

			Statement statement = conn.createStatement();

			String sql = "UPDATE `user_v1` SET `email`='" + email + "' ,password='" + password + "' WHERE `email`='"
					+ old_email + "'";

			statement.executeUpdate(sql);
			//int num = statement.executeUpdate(sql);

			conn.close();

			msg = "更新しました。";

		} catch (Exception e) {
			System.out.println(e);
			msg = "JDBCのロードに失敗した";
		}

		response.getWriter().append(msg);
	}

}
