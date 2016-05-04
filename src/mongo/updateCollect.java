package mongo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

/**
 * Created by guanxiaoda on 16/4/29.
 */
public class updateCollect implements Runnable {
    static Logger logger = LoggerFactory.getLogger(updateCollect.class);

    public static void main(String[] args) {

    }

    @Override
    public void run() {

        String URL = "jdbc:oracle:thin:@172.18.79.3:1521/ORCL";
        String USERNAME = "ashi";
        String PASSWORD = "ashi";
        while (true) {
            weixinDataDb wdd = new weixinDataDb(URL, USERNAME, PASSWORD);
            HashMap<Integer, String> tasks = wdd.getItemsToCollect("collect");
            for (int id : tasks.keySet()) {
                String range = tasks.get(id);
                mongo2Ora.move(wdd,"sogou_weixin_wxpublic_info", "article", range);
                if (wdd.updateCollectStatus("collect", id)) logger.info("updated collect id:" + id);

                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            try {
                logger.info("wait 5 min...");
                Thread.sleep(1000 * 60 * 5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }
}
