package mongo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by guanxiaoda on 16/5/4.
 * 用于转移搜索得到的文章
 */
public class transferData implements Runnable {
    Logger logger = LoggerFactory.getLogger(transferData.class);


    @Override
    public void run() {

        String ORACLE_URL = "jdbc:oracle:thin:@172.18.79.3:1521/ORCL";
        String ORACLE_USERNAME = "jinrong";
        String ORACLE_PASSWORD = "jinrong";
        String ORACLE_TABLE = "weixin_data";

        String MONGODB_COLLECTION = "sogou_weixin_paper_info";

        weixinDataDb wdb = new weixinDataDb(ORACLE_URL, ORACLE_USERNAME, ORACLE_PASSWORD);
        while (true) {
            mongo2Ora.move(wdb,MONGODB_COLLECTION, ORACLE_TABLE);

            try {
                logger.info("1min later...");
                Thread.sleep(1000 * 60 * 1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
