package common;

import common.service.DBService;
import common.system.AppContext;
import common.system.Systemconfig;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by guanxiaoda on 2017/6/19.
 */
public class DataCountGetter {

    private static DBService dbService;


    public static void main(String[] args) {
        Systemconfig.crawlerType = 1;//随便
        String path = Thread.currentThread().getContextClassLoader().getResource("").getPath();
        System.out.println(path);
        AppContext.initAppCtx(path);
        dbService = (DBService) AppContext.appContext.getBean("newsService");//随便


        String tableName = "news_data";
        String colName = "inserttime";
        int offset = 0;
        int c = getCByTBColAndOffset(tableName, colName, offset);
        String day = offset == 0 ? "今日" : offset + "天前";
        System.out.println("table: " + tableName + "\t " + day + ":" + c);
        offset = 1;
        c = getCByTBColAndOffset(tableName, colName, offset);
        day = offset == 0 ? "今日" : offset + "天前";
        System.out.println("table: " + tableName + "\t " + day + ":" + c);
        offset = 2;
        c = getCByTBColAndOffset(tableName, colName, offset);
        day = offset == 0 ? "今日" : offset + "天前";
        System.out.println("table: " + tableName + "\t " + day + ":" + c);
        offset = 3;
        c = getCByTBColAndOffset(tableName, colName, offset);
        day = offset == 0 ? "今日" : offset + "天前";
        System.out.println("table: " + tableName + "\t " + day + ":" + c);

        tableName = "bbs_data";
        colName = "insert_time";
        offset = 0;
        c = getCByTBColAndOffset(tableName, colName, offset);
        day = offset == 0 ? "今日" : offset + "天前";
        System.out.println("table: " + tableName + "\t " + day + ":" + c);
        offset = 1;
        c = getCByTBColAndOffset(tableName, colName, offset);
        day = offset == 0 ? "今日" : offset + "天前";
        System.out.println("table: " + tableName + "\t " + day + ":" + c);
        offset = 2;
        c = getCByTBColAndOffset(tableName, colName, offset);
        day = offset == 0 ? "今日" : offset + "天前";
        System.out.println("table: " + tableName + "\t " + day + ":" + c);
        offset = 3;
        c = getCByTBColAndOffset(tableName, colName, offset);
        day = offset == 0 ? "今日" : offset + "天前";
        System.out.println("table: " + tableName + "\t " + day + ":" + c);

    }

    public static int getCByTBColAndOffset(String tableName, String colName, int offset) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("tablename", tableName);
        paramMap.put("insertColName", colName);
        paramMap.put("offset", offset);
        return dbService.getDataCountByDay(paramMap);
    }
}
