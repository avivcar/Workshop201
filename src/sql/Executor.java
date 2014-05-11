package sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Executor {
	
	static boolean DISABLE_SQL = false;
	
	public static void run(String query) throws ClassNotFoundException, SQLException {
		if (DISABLE_SQL) return;
		Class.forName("com.mysql.jdbc.Driver");
		Connection connection = DriverManager.getConnection("jdbc:mysql://localhost/Forum?user=root&password=1234");
		Statement statement = connection.createStatement();
		try {
			statement.executeUpdate(query);
		} catch (SQLException e) {
			System.out.println("SQL Error: the query \"" + query + "\" failed. SQL Error msg: " + e.getMessage());
		}
	}
	
	public static ResultSet query(String query) throws ClassNotFoundException, SQLException {
		if (DISABLE_SQL) return null;
		Class.forName("com.mysql.jdbc.Driver");
		Connection connection = DriverManager.getConnection("jdbc:mysql://localhost/Forum?user=root&password=1234");
		Statement statement = connection.createStatement();
		ResultSet result = null;
		try {
			result = statement.executeQuery(query);
		} catch (SQLException e) {
			System.out.println("SQL Error: the query \"" + query + "\" failed. SQL Error msg: " + e.getMessage());
		}
		return result;
	}
	
}