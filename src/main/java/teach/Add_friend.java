package teach;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Statement;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * Servlet implementation class Add_friend
 */
@WebServlet("/Add_Friend")
public class Add_friend extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public Add_friend() {
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
		String to = request.getParameter("to");

		HttpSession session = request.getSession(true);
		String from = (String) session.getAttribute("email");

		response.setContentType("text/html;charset=UTF-8");

		String msg = "";

		try {

			Class.forName("com.mysql.jdbc.Driver");

			Connection conn = DBUtil.getConnection();

			Statement statement = conn.createStatement();

			String sql = "INSERT INTO `friend_v1` (`addfrom`, `accepto`, `status`, `updateTime`) VALUES"
					+ "('" + from + "', '" + to + "',1,CURRENT_TIMESTAMP())";

			//ResultSet resultSet = statement.executeQuery(s);
			statement.executeUpdate(sql);

			conn.close();

			msg = "友達が正常に追加されました。" + "<a href='friends.html'>友達一覧を見る</a>";

		} catch (Exception e) {
			System.out.println(e);
			msg = "JDBCのロードに失敗した";
		}

		response.getWriter().append(msg);
	}

}
