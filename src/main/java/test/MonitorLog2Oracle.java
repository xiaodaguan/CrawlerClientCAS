package test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MonitorLog2Oracle {

	public static int clientindex;

	public static void main(String[] args) {

		for (String arg : args) {

			if (arg.toLowerCase().contains("clientindex=")) {// 当前是几号爬虫
				clientindex = Integer.parseInt(arg.split("=")[1]);
			}
		}
		while (true) {

			try {
				Process proc = null;
				try {
					proc = Runtime.getRuntime().exec("sh config/count.sh");
					//proc.waitFor();
					InputStream is = proc.getInputStream();

					//InputStream is = new FileInputStream("config/test.log");
					BufferedReader br = new BufferedReader(new InputStreamReader(is));
					for (String line = br.readLine(); line != null; line = br.readLine()) {
						Log.log(line);
						ClientRecord record = new ClientRecord();
						String[] ms = line.split(":");
						if (ms.length == 2) {
							record.setDate(ms[0]);
							record.setCategory(ms[1]);
						} else if (ms.length == 3) {
							record.setDate(ms[0]);
							record.setCategory(ms[1]);
							record.setCount(Integer.parseInt(ms[2]));
						}
						record.setClientIndex(clientindex);
						if (clientindex == 0) {
							Log.log("没有正确的  clientindex : " + clientindex);
							Thread.sleep(5 * 60);
							continue;
						}
						//record.print();
						insert(record);
					}
					br.close();
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				long startTime = System.currentTimeMillis();
				while (startTime + 6 * 3600 * 1000 > System.currentTimeMillis()) {
					Log.log("beating");
					Thread.sleep(5 * 60 * 1000);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	private static void insert(ClientRecord record) {

		String des = "CRAWLER_CLIENT_RECORD";
		Connection conn = null;
		Statement stat = null;
		PreparedStatement exe = null;
		ResultSet rs = null;
		String url = "jdbc:oracle:thin:@172.18.79.32:1521:ORCL";
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			conn = DriverManager.getConnection(url, "TOPSEARCH", "topsearch");
			stat = conn.createStatement();

			String inserSql = "insert into " + des + "(CATEGORY,CLIENT_INDEX,COUNT,DATA,INSERT_TIME) "
					+ "values (?,?,?,?,?)";

			exe = conn.prepareStatement(inserSql);

			exe.setString(1, record.getCategory());
			exe.setInt(2, record.getClientIndex());
			exe.setInt(3, record.getCount());
			exe.setString(4, record.getDate());
			exe.setTimestamp(5, new Timestamp(System.currentTimeMillis()));

			// log(exe.toString());
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
class Log{
	public static void log(Object o) {

		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		String s = format.format(new Date());
		System.out.println("[" + s + "] : " + o);
	}
}
class ClientRecord {
	private String category;
	private String date;
	private int clientIndex;
	private int count;

	public void print() {
		System.out.println("");
		Log.log("category:   \t" + this.category);
		Log.log("date:       \t" + this.date);
		Log.log("clientIndex:\t" + this.clientIndex);
		Log.log("count:      \t" + this.count);
		System.out.println("");
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public int getClientIndex() {
		return clientIndex;
	}

	public void setClientIndex(int clientIndex) {
		this.clientIndex = clientIndex;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}
}
