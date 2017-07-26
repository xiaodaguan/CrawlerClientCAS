package common.proxy;

import common.scheduler.Scheduler;
import common.system.Systemconfig;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class ProxyPoolTest {

    private static ProxyPoolRedis proxyPool;
    @BeforeClass
    public static void beforeAll(){

        Systemconfig.crawlerType = 1;
        ApplicationContext context = new ClassPathXmlApplicationContext("app-redis.xml");
        proxyPool = (ProxyPoolRedis) context.getBean("proxyPoolRedis");
        proxyPool.init();
    }

    @Test
    public void addTest(){
        proxyPool.addOne("https://192.168.1.8:8888");
        proxyPool.addOne("https://192.168.1.9:8888");
        proxyPool.addOne("https://192.168.1.10:8888");
        proxyPool.addOne("https://192.168.1.11:8888");
    }


    @Test
    public void getTest(){
        String random = proxyPool.randomGetOne();
        System.out.println(random);
    }

    @Test
    public void clearTest(){
        proxyPool.clearAll();
    }


}
