package sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Executor {
	
	public static Object[] run(String query) throws ClassNotFoundException, SQLException {
		System.out.println(query);
		if (true) return null;
		Class.forName("com.mysql.jdbc.Driver");
		Connection connection = DriverManager.getConnection("jdbc:mysql://localhost/Forum?user=root&password=1234");
		Statement statement = connection.createStatement();
		ResultSet resultSet = statement.executeQuery(query);
		return new Object[]{};
	}
	
}