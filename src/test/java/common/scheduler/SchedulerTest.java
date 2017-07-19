package common.scheduler;

import common.pojos.HtmlInfo;
import common.system.Systemconfig;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by guanxiaoda on 2017/6/29.
 */
public class SchedulerTest {

    private static Scheduler scheduler;
    @BeforeClass
    public static void beforeAll(){

        Systemconfig.crawlerType = 1;
        ApplicationContext context = new ClassPathXmlApplicationContext("app-redis.xml");
        scheduler = (Scheduler) context.getBean("defaultScheduler");
        scheduler.init();
    }




    @Test
    public void submitTest(){
        HtmlInfo task = new HtmlInfo();
        task.setCrawlerType("TEST");
        task.setOrignUrl("guanxiaoda.cn");


        long reply = scheduler.submitTask(task);
        System.out.println("current:"+reply);
        Assert.assertNotEquals(0,reply);
    }

    @Test
    public void getTaskTest(){
        HtmlInfo task = scheduler.getTask();
        System.out.println(task.getOrignUrl());
        Assert.assertEquals("guanxiaoda.cn", task.getOrignUrl());

    }


    @Test
    public void getLeftTest(){
        Long left = scheduler.getLeftTaskCount();
        Assert.assertNotEquals(Long.valueOf(0), left);
    }

    @Test
    public void getTtoalTest(){
        Long total = scheduler.getTotalTaskCount();
        System.out.println(total);
        Assert.assertNotEquals(Long.valueOf(0),total);
    }

    @Test
    public void removeTest(){
         scheduler.removeAllTask();
    }
}
