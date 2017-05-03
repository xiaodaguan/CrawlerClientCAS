package test;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.HashSet;

import common.util.MD5Util;

public class testOracle2 {

	public static void main(String[] args) {
		noname3("test");
	}

	// 0B7E05FF493408FEC496436F104EAFBD
	// 0b7e05ff493408fec496436f104eafbd

	private static void noname3(String des) {

		// TODO Auto-generated method stub
		Connection conn = null;
		Statement stat = null;
		PreparedStatement exe = null;
		ResultSet rs = null;
		String url = "jdbc:oracle:thin:@172.18.79.32:1521:ORCL";
		try {

			HashMap<Integer, Long> mapOwnerId2Code = new HashMap<Integer, Long>();

			Class.forName("oracle.jdbc.driver.OracleDriver");
			conn = DriverManager.getConnection(url, "TOPSEARCH", "topsearch");
			stat = conn.createStatement();


			System.out.println("inserting...");
		
			String inserSql = "insert into "
					+ des
					+ "(id,name,md5) "
					+ "values (?,?,?)";
			
			exe = conn.prepareStatement(inserSql);
			exe.setInt(1, 2);
			exe.setString(2, "xiaoming");
			exe.setString(3, "123");
			exe.execute();

		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
					rs = null;
				}
				if (stat != null) {
					stat.close();
					stat = null;
				}
				if (conn != null) {
					conn.close();
					conn = null;
				}

			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

	}
}
