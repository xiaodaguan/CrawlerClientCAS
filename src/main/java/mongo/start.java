package mongo;

import common.util.StringUtil;
import net.sf.json.JSONObject;

/**
 * Created by guanxiaoda on 16/4/29.
 * 启动进程
 */
public class start {
    private static int TYPE = 0;//1 search; 2 monitor

    private static void readConf() {
//        System.out.println(System.getProperty("user.dir"));
        String text = StringUtil.getContent("./config/mongo2oracle.conf");

        JSONObject jObj = JSONObject.fromObject(text);
        TYPE = jObj.getInt("type");


    }

    /**
     * @param args
     */
    public static void main(String[] args) {

        readConf();
//        参数 --type 1: only transfer paper; 2: transfer + update collect 3: only update collect

        if(TYPE ==1 || TYPE ==2) {
            transferData m2o = new transferData();
            Thread tMove = new Thread(m2o, "transfer");
            tMove.start();
        }
        if (TYPE == 2||TYPE == 3) {
            updateCollect update = new updateCollect();
            Thread tUpdate = new Thread(update, "update");
            tUpdate.start();
        }
    }

}
