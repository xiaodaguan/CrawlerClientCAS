package common.service;

import common.pojos.NewsData;
import common.task.CrawlerType;
import common.system.AppContext;
import common.system.Systemconfig;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Created by guanxiaoda on 2017/6/19.
 */
public class ServiceTest {

    private static DBService dbService;

    @BeforeClass
    public static void before(){
        Systemconfig.crawlerType = 1;

        String crawlerTypeName = CrawlerType.getCrawlerTypeMap().get(Systemconfig.crawlerType).name().toLowerCase();
        String typeName = crawlerTypeName.substring(0, crawlerTypeName.indexOf("_"));
        String serviceName = typeName+"Service";


        String path = Thread.currentThread().getContextClassLoader().getResource("").getPath();
        AppContext.initAppCtx(path);
        dbService = (DBService) AppContext.appContext.getBean(serviceName);
    }

    @Test
    public void createTest(){
        NewsData data = new NewsData();

    }

    @Test
    public void checkTest(){
        NewsData data = new NewsData();
        data.setTitle("test");
        data.setUrl("testurl");

        dbService.checkData(data);
        System.out.println("ok.");
    }

}
