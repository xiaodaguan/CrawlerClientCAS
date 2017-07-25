package common.scheduler;

import common.pojos.CrawlTask;
import common.task.SearchKey;
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
    public void removeTest(){
        scheduler.removeAllTask();
        Long left = scheduler.getLeftTaskCount();
        System.out.println("当前队列中任务总数：" + left);
    }
    @Test
    public void submitTest(){
        CrawlTask task = new CrawlTask();
        task.setCrawlerType("META");
        task.setOrignUrl("http://news.baidu.com/ns?word=%E9%9D%92%E5%B2%9B%E4%BA%A4%E9%80%9A&tn=news&from=news&cl=2&rn=20&ct=1");
        task.setEncode("utf-8");

        SearchKey sk = new SearchKey();
        sk.setKEYWORD("青岛 交通");
        sk.setCATEGORY_CODE(0);
        sk.setSITE_ID("baidu");
        sk.setSITE_NAME("baidu");

        task.setSearchKey(sk);



        task.setSite("baidu_news_search");

//        CommonData data = new NewsData();
//        data.setTitle("关晓炟");
//        data.setUrl("http://guanxiaoda.cn");
//        data.setPubdate(new Date());
//
//        task.setData(data);


        long reply = scheduler.submitTask(task);
        System.out.println("当前队列中任务总数:"+reply);
        Assert.assertNotEquals(0,reply);
    }

    @Test
    public void getTaskTest(){
        CrawlTask task = scheduler.getTask();
        System.out.println(task.getOrignUrl());
        Assert.assertEquals("http://www.baidu.com", task.getOrignUrl());

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

}
