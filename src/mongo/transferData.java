package mongo;

import common.util.JsonUtil;
import common.util.StringUtil;
import net.sf.json.JSON;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by guanxiaoda on 16/5/4.
 * 用于转移搜索得到的文章
 */
public class transferData implements Runnable {
    Logger logger = LoggerFactory.getLogger(transferData.class);

    String ORACLE_URL = "";
    String ORACLE_USERNAME = "";
    String ORACLE_PASSWORD = "";


    String MONGODB_COLLECTION = "";
    String ORACLE_TABLE = "";
    String ORACLE_TASK_TABLE = "";

    private void readConf() {
//        System.out.println(System.getProperty("user.dir"));
        String text = StringUtil.getContent("./config/mongo2oracle.conf");

        JSONObject jObj = JSONObject.fromObject(text);
        JSONObject jOra = (JSONObject) jObj.get("oracle");
        JSONObject jMon = (JSONObject) jObj.get("mongodb");
        assert jOra != null && jMon != null;
        ORACLE_URL = jOra.getString("url");
        ORACLE_USERNAME = jOra.getString("user");
        ORACLE_PASSWORD = jOra.getString("passwd");
        ORACLE_TABLE = jOra.getString("paper_table");
        ORACLE_TASK_TABLE = jOra.getString("task_table");

        MONGODB_COLLECTION = jMon.getString("collection");


    }

    @Override
    public void run() {


        while (true) {
            readConf();
            weixinDataDb wdb = new weixinDataDb(ORACLE_URL, ORACLE_USERNAME, ORACLE_PASSWORD);

            mongo2Ora.move(wdb, MONGODB_COLLECTION, ORACLE_TABLE);

            try {
                int SLEEP_BETWEEN_CYCLE = 1;
                logger.info(SLEEP_BETWEEN_CYCLE + " min before NEXT RUN...");
                Thread.sleep(1000 * 60 * SLEEP_BETWEEN_CYCLE);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
