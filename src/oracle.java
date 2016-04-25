import common.util.TimeUtil;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by guanxiaoda on 16/4/25.
 */
public class oracle {


    private static Connection conn = null;
    private static String URL = "jdbc:oracle:thin:@172.18.79.3:1521/ORCL";
    private static String USERNAME = "jinrong";
    private static String PASSWORD = "jinrong";
    private static String TABLE = "crawler_status";


    public static void main(String[] args) {
        while (true) {
            try {
                Class.forName("oracle.jdbc.driver.OracleDriver").newInstance();

                conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);

                Statement st = conn.createStatement();
                st.execute("truncate table " + TABLE);

                st.close();
                conn.close();
                System.out.println("log table cleared.");

            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (SQLException e) {
                e.printStackTrace();
            }

            TimeUtil.rest(3600);
        }
    }
}
