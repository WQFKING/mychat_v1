package wechat;

import jakarta.servlet.http.HttpServlet;

public class Constants extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public static final String databaseUrl = "jdbc:mysql://localhost:3306/wechat_v1?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
	public static final String databaseUser = "root";
	public static final String databasePassword = "123456";

	private Constants() {

	}

}
