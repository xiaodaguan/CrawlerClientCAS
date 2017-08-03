package common.scheduler;

import common.SeedManager;
import common.system.Systemconfig;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import redis.clients.jedis.HostAndPort;

import java.util.HashSet;
import java.util.Set;

public class SchedulerGetTest {


    @Test
    public void testMain(){
        Systemconfig.crawlerType = 1;//指定获取serachkey的爬虫类型


        ApplicationContext context = new ClassPathXmlApplicationContext("app-redis.xml");
        //Scheduler scheduler = (Scheduler) context.getBean("metaDataScheduler");
        Scheduler scheduler = (Scheduler) context.getBean("defaultScheduler");
        scheduler.init();
        System.out.println(scheduler.getTask());
    }
}
