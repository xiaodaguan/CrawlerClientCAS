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


    String ORACLE_TABLE = "weixin_data";
    String MONGODB_COLLECTION = "sogou_weixin_paper_info";

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


        weixinDataDb wdb = new weixinDataDb(ORACLE_URL, ORACLE_USERNAME, ORACLE_PASSWORD);
        while (true) {
            mongo2Ora.move(wdb, MONGODB_COLLECTION, ORACLE_TABLE);

            try {
                logger.info("1min later...");
                Thread.sleep(1000 * 60 * 1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
