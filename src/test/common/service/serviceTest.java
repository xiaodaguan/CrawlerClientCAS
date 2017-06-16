package common.service;

import common.pojos.WeixinData;
import common.rmi.packet.CrawlerType;
import common.rmi.packet.SearchKey;
import common.system.Systemconfig;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.IOException;
import java.util.List;

/**
 * Created by guanxiaoda on 2017/6/15.
 */
public class serviceTest {

    private static DBService dbService;
    private static ApplicationContext  context;
    @BeforeClass
    public static void beforeClass() throws ClassNotFoundException {

        Systemconfig.crawlerType = 15;
        context = new ClassPathXmlApplicationContext("app-sysconfig.xml");
        String typeName = CrawlerType.getCrawlerTypeMap().get(Systemconfig.crawlerType).name().toLowerCase();

        String serviceName = typeName.substring(0,typeName.indexOf("_"))+"Service";
        dbService = (DBService) context.getBean(serviceName);
    }

    @Test
    public void getAllSearchKeyTest(){

        List<SearchKey> keys = dbService.getAllSearchKeys();
        Assert.assertNotEquals(0,keys.size());
    }

    @Test
    public void getDataCountTest(){

        int c = dbService.getDataCount("weixin_data");
        Assert.assertNotEquals(0, c);
    }

    @Test
    public void readTest(){
        WeixinData data = (WeixinData) dbService.read(1454392);
        System.out.println(data.getTitle());
        System.out.println(data.getUrl());
        Assert.assertNotNull(data);
    }


}
