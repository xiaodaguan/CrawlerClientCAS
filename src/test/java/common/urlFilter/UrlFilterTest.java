package common.urlFilter;

import common.system.AppContext;
import common.system.Systemconfig;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class UrlFilterTest {


    @BeforeClass
    public static void beforeAll(){
        Systemconfig.crawlerType = 1;

        String path = Thread.currentThread().getContextClassLoader().getResource("").getPath();
        AppContext.initAppCtx(path);

        System.out.println("url filter已初始化[ok].");
    }

    @Test
    public void addTest(){
        System.out.println("添加url [http://已添加的url]");
        Systemconfig.urlFilter.add("http://已添加的url");
        System.out.println("添加url [http://已添加的url] 成功.");
    }

    @Test
    public void containTest(){
        String url1 = "http://不存在的url";
        String url2 = "http://已添加的url";
        boolean bool1 = Systemconfig.urlFilter.contains(url1);

        boolean bool2 = Systemconfig.urlFilter.contains(url2);

        System.out.println("url1: "+url1);
        System.out.println("url2: "+url2);

        System.out.println("url1是否已存在:" +bool1);
        System.out.println("url2是否已存在:" +bool2);
        Assert.assertFalse(bool1);
        Assert.assertTrue(bool2);

    }

}
