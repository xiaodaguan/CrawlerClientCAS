package common.other;

import common.rmi.packet.CrawlerType;
import common.service.DBService;
import common.system.AppContext;
import common.system.Systemconfig;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.FileNotFoundException;

/**
 * Created by guanxiaoda on 2017/6/23.
 */
public class AspectTest {

    @BeforeClass
    public static void before(){
        Systemconfig.crawlerType = 1;



        String path = Thread.currentThread().getContextClassLoader().getResource("").getPath();
        AppContext.initAppCtx(path);
    }

    @Test
    public void sayHiTest(){
        HelloWorld hw = (HelloWorld) AppContext.appContext.getBean("helloWorld");
        HellWorld hell = (HellWorld) AppContext.appContext.getBean("hell");
//        HelloWorld hw = new HelloWorld();
            hw.sayHi();
            hell.sayHi("gxd");


    }
}
