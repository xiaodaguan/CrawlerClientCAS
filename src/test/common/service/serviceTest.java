package common.service;

import common.pojos.NewsData;
import common.rmi.packet.CrawlerType;
import common.rmi.packet.SearchKey;
import common.system.Systemconfig;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * Created by guanxiaoda on 2017/6/15.
 */
public class serviceTest {

    private static DBService dbService;
    private static ApplicationContext  context;
    @BeforeClass
    public static void beforeClass() throws ClassNotFoundException {

        Systemconfig.crawlerType = 1;
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
        NewsData data = (NewsData) dbService.getData(583);
        System.out.println(data.getTitle());
        System.out.println(data.getUrl());
        Assert.assertNotNull(data);
    }

    @Test
    public void createTest() throws IOException {
        NewsData data = new NewsData();
        // (id, title, content, md5, inserttime, category_code, search_keyword, url, pubtime, brief, source, same_url, same_num, img_url)

        data.setTitle("test title");
        data.setUrl("www.test.url");
        data.setContent("test contentttttt");
        data.setAuthor("tester");
        data.setSearchKey("test");
        data.setMd5("testjdskalfjdklmd5");
        data.setPubdate(new Date());
        data.setSource("testsour");
        data.setBrief("testbbbb");
        data.setImgUrl("testjau");
        data.setInserttime(new Date());
        data.setCategoryCode(3);
        data.setSameNum(25);
        data.setImgUrl("www.img.url");
        data.setSameUrl("www.same.url");


        int i = dbService.saveData(data);
        System.out.println("status:"+i);
        System.out.println("data id:"+data.getId());
        Assert.assertNotEquals(0,i);
    }

}
