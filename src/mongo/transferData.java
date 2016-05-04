package mongo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by guanxiaoda on 16/5/4.
 */
public class transferData implements Runnable {
    Logger logger = LoggerFactory.getLogger(transferData.class);


    @Override
    public void run() {

        String URL = "jdbc:oracle:thin:@172.18.79.3:1521/ORCL";
        String USERNAME = "jinrong";
        String PASSWORD = "jinrong";
        weixinDataDb wdb = new weixinDataDb(URL, USERNAME, PASSWORD);
        while (true) {
            mongo2Ora.move(wdb,"sogou_weixin_paper_info", "weixin_data");

            try {
                logger.info("1min later...");
                Thread.sleep(1000 * 60 * 1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
