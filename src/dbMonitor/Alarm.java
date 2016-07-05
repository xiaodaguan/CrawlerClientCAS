package dbMonitor;

import com.guilhermechapiewski.fluentmail.email.EmailMessage;
import com.guilhermechapiewski.fluentmail.transport.EmailTransportConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import java.sql.DriverManager;

/**
 * Created by guanxiaoda on 7/5/16.
 */
public class Alarm {
    private static Logger logger = LoggerFactory.getLogger(Alarm.class);
    //    private final static String DB_URL = "jdbc:oracle:thin:@172.18.79.3:1521/orcl";
//    private final static String USER = "jinrong";
//    private final static String PASS = "jinrong";
    private final static String DB_URL = "jdbc:oracle:thin:@117.132.15.89:1521/orcl";
    private final static String USER = "qdtramgr";
    private final static String PASS = "qdtramgr";

    enum type {
        news,
        bbs,
        weibo,
        weixin
    }

    public static void main(String[] args) throws InterruptedException {
        EmailTransportConfiguration.configure("smtp.126.com", true, false, "darkslayer27@126.com", "xg327AZJ");

        DriverManagerDataSource dataSource = new DriverManagerDataSource(DB_URL, USER, PASS);
        JdbcTemplate temp = new JdbcTemplate(dataSource);

        while (true) {
            scanAndAlarm(temp);
            Thread.sleep(1000 * 3600 * 1);
        }

    }

    private static void scanAndAlarm(JdbcTemplate temp) {
        String sql = "";
        for (type t : type.values()) {
            String tabName = t.name();
            switch (tabName) {
                case "news":
                    sql = "SELECT count(*) FROM " + tabName + "_data WHERE inserttime >= sysdate - 3/24";
                    break;
                case "bbs":
                    sql = "SELECT count(*) FROM " + tabName + "_data WHERE insert_time >= sysdate - 3/24";
                    break;
                case "weibo":
                    sql = "SELECT count(*) FROM " + tabName + "_data WHERE insert_time >= sysdate - 3/24";
                    break;
                case "weixin":
                    sql = "SELECT count(*) FROM " + tabName + "_data WHERE inserttime >= sysdate - 3/24";
                    break;
                default:
                    return;
            }
            int count = temp.queryForInt(sql);
            if (count > 0) {
                logger.info("type:{} count {}", tabName, count);
            } else {
                logger.error("{} no new data in 3 hours!", tabName);
                String msg = "[db] " + DB_URL + " --" + tabName + " -- had no new data inserted for 3 hours!";

                new EmailMessage().from("darkslayer27@126.com").to("506703298@qq.com").withSubject("spider alarm!!!").withBody(msg).send();
            }
        }
    }
}
