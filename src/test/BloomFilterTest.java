package test;

import common.urlFilter.BloomFilterRedis;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

/**
 * Created by guanxiaoda on 2017/6/9.
 */
public class BloomFilterTest {



    static{
//        hostSet.add(new HostAndPort("172.18.79.39",7000));
//        hostSet.add(new HostAndPort("172.18.79.40",7000));
//        hostSet.add(new HostAndPort("172.18.79.41",7000));
//        hostSet.add(new HostAndPort("172.18.79.39",8000));
//        hostSet.add(new HostAndPort("172.18.79.40",8000));
//        hostSet.add(new HostAndPort("172.18.79.41",8000));

    }

    @Test
    public void containTest(){

        ApplicationContext ctx = new FileSystemXmlApplicationContext("config/redis-cluster-conf.xml");

        BloomFilterRedis bloomFilterRedis = (BloomFilterRedis) ctx.getBean("bloomFilterRedis");


        bloomFilterRedis.init();

        Assert.assertEquals(false, bloomFilterRedis.contains("www.zheshigejiaurl.com"));

    }





//        BloomFilterTest bft = new BloomFilterTest();
//
////        redis.set("test2","wodetianna");
//
//        bf.bind(redis,"redis-bloom");
//        String[] urls = {"http://www.baidu.com","http://www.sogou.com" , "http://www.guanxiaoda.cn", "https://www.google.com","http://www.facebook.com"};
//
//
//
//        if(bf.contains("http://smzdm.com"))
//            System.err.println("wrong!");
//        else
//            System.out.println("right!");
//




}
