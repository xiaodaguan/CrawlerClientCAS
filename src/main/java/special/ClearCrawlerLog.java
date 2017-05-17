package special;

import common.util.TimeUtil;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

/**
 * Created by guanxiaoda on 16/4/25.
 */
public class ClearCrawlerLog {


    private static Connection conn = null;
    private static String URL = "";
    private static String USERNAME = "";
    private static String PASSWORD = "";
    private static String TABLE = "CRAWLER_STATUS";

    static {



        try {
            Properties props = new Properties();
            InputStream is = new FileInputStream("./config/config.properties");
            props.load(is);

            URL = "jdbc:oracle:thin:@"+props.getProperty("datasourceHost")+"/ORCL";
            USERNAME = props.getProperty("datasourceUsername");
            PASSWORD = props.getProperty("datasourcePassword");

            Class.forName("oracle.jdbc.driver.OracleDriver").newInstance();
            conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {




        while (true) {
            try {
                clearTable();

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

    private static void clearTable() throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {




        Statement st = conn.createStatement();
        st.execute("truncate table " + TABLE);

        st.close();
        System.out.println("log table cleared.");
    }
}
