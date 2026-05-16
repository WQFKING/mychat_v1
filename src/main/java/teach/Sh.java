package teach;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class Sh
 */
@WebServlet("/Sh")
public class Sh extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public Sh() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		request.getRequestDispatcher("/sh.html").forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String email = request.getParameter("email");

		response.setContentType("text/html;charset=UTF-8");

		String msg = "";

		try {

			Connection conn = DBUtil.getConnection();

			Statement statement = conn.createStatement();

			String sql = "SELECT `email` FROM `user_v1` WHERE email='" + email + "'";

			ResultSet resultSet = statement.executeQuery(sql);
			//int num = statement.executeUpdate(sql);

			int count = 0;

			while (resultSet.next()) {
				//var email1=resultSet.getString("email");
				//var name=resultSet.getString("student_name");
				//System.out.println(email1);
				count = count + 1;
			}

			conn.close();

			if (count == 0) {

				msg = "メールが存在しません。";

			} else {

				msg = "<span id='to'>" + email + "</span> <button onclick='add_Friend()'>友達になる</button>";

			}

		} catch (Exception e) {
			System.out.println(e);
			msg = "JDBCのロードに失敗した";
		}

		response.getWriter().append(msg);
	}

}
