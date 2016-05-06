package mongo;

import common.util.StringUtil;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

/**
 * Created by guanxiaoda on 16/4/29.
 * 用于转移公众号监控采集到的文章
 */
public class updateCollect implements Runnable {
    static Logger logger = LoggerFactory.getLogger(updateCollect.class);


    String ORACLE_URL = "";
    String ORACLE_USERNAME = "";
    String ORACLE_PASSWORD = "";
    String TASK_TABLE_NAME = "collect";
    String ORACLE_TABLE = "article";

    String MONGODB_COLLECTION = "sogou_weixin_wxpublic_info";


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


    }

    @Override
    public void run() {

        readConf();

        while (true) {
            weixinDataDb wdd = new weixinDataDb(ORACLE_URL, ORACLE_USERNAME, ORACLE_PASSWORD);
            HashMap<Integer, String> tasks = wdd.getItemsToCollect(TASK_TABLE_NAME);
            for (int id : tasks.keySet()) {
                String range = tasks.get(id);
                mongo2Ora.move(wdd, MONGODB_COLLECTION, ORACLE_TABLE, range);
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
