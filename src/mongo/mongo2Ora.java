package mongo;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.sun.tools.internal.xjc.reader.xmlschema.bindinfo.BIConversion;
import common.bean.WeixinData;
import net.sf.json.JSONObject;
import org.bson.Document;
import com.mongodb.Block;
import com.mongodb.client.FindIterable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;


public class mongo2Ora implements Runnable {

    static Logger logger = LoggerFactory.getLogger(mongo2Ora.class);

    static final List<String> faikedIds = new ArrayList<String>();
    static MongoClient client = new MongoClient("172.18.79.31:27017");
    static final MongoDatabase db = client.getDatabase("wechatdb");


    @Override
    public void run() {
        while (true) {
            move("sogou_weixin_paper_info", "weixin_data");

            try {
                logger.info("1min later...");
                Thread.sleep(1000 * 60 * 1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void move(String collName, final String tableName) {
        move(collName, tableName, null);
    }

    /**
     * ��mongodb��ȡָ��collectionȫ��items,ת��or���µ�oracle��
     *
     * @param collName
     * @param tableName
     */
    public static void move(String collName, final String tableName, String beginAndEnd) {


        MongoCollection coll = db.getCollection(collName);

        FindIterable<Document> iter = beginAndEnd == null ? coll.find() : coll.find(Filters.and(Filters.gt("pubtime", beginAndEnd.split("~")[0]), Filters.lt("pubtime", beginAndEnd.split("~")[1])));

        logger.info("start moving items from mongodb to oracle...");

        final int[] updateCount = {0};
        final int[] insertCount = {0};


        String URL = "jdbc:oracle:thin:@172.18.79.3:1521/ORCL";
        String USERNAME = "jinrong";
        String PASSWORD = "jinrong";
        final mongo.db<WeixinData> wxDb = new weixinDataDb(URL, USERNAME, PASSWORD);
        final List<String> crawledMd5s = wxDb.getCrawled(tableName);
        try {
            iter.forEach(new Block<Document>() {
                             @Override
                             public void apply(Document document) {
//                System.out.println(document);
//                    logger.info("document: {}", document.get("_id"));


                                 JSONObject jObjs = JSONObject.fromObject(document.toJson());
                                 WeixinData wd = json2Weixin(jObjs);

                                 if (wd.getPubtime() == null) {
                                     JSONObject obj = (JSONObject) jObjs.get("_id");
                                     faikedIds.add(obj.getString("$oid"));
                                     logger.error("pubtime null : {}", obj.getString("$oid"));
                                 }


                                 if (wd.getInserttime() == null) {
                                     wd.setInserttime(new Timestamp(System.currentTimeMillis()));
                                 }


                                 int status = wxDb.saveOrUpdateData(wd, tableName);


                                 if (status > 0) {
                                     if (status == 2) {
                                         logger.debug("updated {}", wd.getTitle());
                                         updateCount[0]++;
                                     } else if (status == 1) {
                                         logger.debug("inserted {}", wd.getTitle());
                                         insertCount[0]++;
                                     } else ;


                                     if (wd.getReadNum() > 0) {
                                         JSONObject obj = (JSONObject) jObjs.get("_id");

                                     }

                                 } else {
                                     logger.error("not inserted.");
                                     if (status == -2) ;

                                 }

                             }
                         }// iter
            );
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        logger.info("updated {}", updateCount[0] + " " + collName + " " + tableName + " " + beginAndEnd);
        logger.info("insertd {}", insertCount[0] + " " + collName + " " + tableName + " " + beginAndEnd);
    }


    private static WeixinData json2Weixin(JSONObject jObjs) {
        WeixinData wd = new WeixinData();
        Iterator<String> iter = jObjs.keys();
        wd.setCategoryCode(0);
        wd.setGongzhongId(0);
        wd.setReadNum(0);
        wd.setPraiseNum(0);
        while (iter.hasNext()) {
            String key = iter.next();

            if (key.equals("brief")) {
                wd.setBrief(jObjs.getString(key));
            } else if (key.equals("title")) {
                wd.setTitle(jObjs.getString(key));
            } else if (key.equals("weixin_name")) {
                wd.setAuthor(jObjs.getString(key));
                wd.setSource(jObjs.getString(key));
            } else if (key.equals("pubtime")) {
                wd.setPubtime(jObjs.getString(key));
                wd.setPubdate(parseTime(jObjs.getString(key)));
            } else if (key.equals("url")) {
                wd.setUrl(jObjs.getString(key));
            } else if (key.equals("search_keyword")) {
                wd.setSearchKey(jObjs.getString(key));
            } else if (key.equals("insertTime")) {
                wd.setInserttime(parseTime(jObjs.getString(key)));
            } else if (key.equals("md5")) {
                wd.setMd5(jObjs.getString(key));
            } else if (key.equals("content")) {
                wd.setContent(jObjs.getString(key));
            } else if (key.equals("img_url")) {
                wd.setImgUrl(jObjs.getString(key));
            } else if (key.contains("read_num")) {
                wd.setReadNum(jObjs.getInt(key) > wd.getReadNum() ? jObjs.getInt(key) : wd.getReadNum());
            } else if (key.contains("like_num")) {
                wd.setPraiseNum(jObjs.getInt(key) > wd.getPraiseNum() ? jObjs.getInt(key) : wd.getPraiseNum());
            } else if (key.equals("gongzhong_id")) {
                wd.setGongzhongId(jObjs.getInt(key));
            } else if (key.equals("category_code")) {
                wd.setCategoryCode(jObjs.getInt(key));
                wd.setCategory1(jObjs.getInt(key));
            }


        }
        return wd;
    }


    public static Date parseTime(String strTime) {

        if (strTime == null || strTime.equals("null")) {
            return null;
        }
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd hhmmss");
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        SimpleDateFormat sdf3 = new SimpleDateFormat("yyyy-MM-dd");

        try {
            return sdf1.parse(strTime);
        } catch (ParseException e) {
            try {
                return sdf2.parse(strTime);
            } catch (ParseException e1) {
                try {
                    return sdf3.parse(strTime);
                } catch (ParseException e2) {
                    e2.printStackTrace();
                }
            }
        }
        return null;
    }


}
