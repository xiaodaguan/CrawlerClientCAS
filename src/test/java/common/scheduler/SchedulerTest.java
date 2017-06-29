package common.scheduler;

import common.pojos.HtmlInfo;
import common.system.AppContext;
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

        ApplicationContext context = new ClassPathXmlApplicationContext("app-redis.xml");
        scheduler = (Scheduler) context.getBean("defaultScheduler");
        scheduler.init();
    }




    @Test
    public void submitTest(){
        HtmlInfo task = new HtmlInfo();
        task.setType("TEST");
        task.setOrignUrl("guanxiaoda.cn");


        long reply = scheduler.submitTask(task);
        System.out.println(reply);
        Assert.assertNotEquals(0,reply);
    }

    @Test
    public void getTaskTest(){
        HtmlInfo task = scheduler.getTask();
        System.out.println(task.getOrignUrl());
        Assert.assertEquals("guanxiaoda.cn", task.getOrignUrl());

    }
}
