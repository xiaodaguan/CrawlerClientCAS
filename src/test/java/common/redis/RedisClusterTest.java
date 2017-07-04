package common.redis;

import common.system.AppContext;
import common.system.Systemconfig;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import redis.clients.jedis.JedisCluster;

/**
 * Created by guanxiaoda on 2017/6/28.
 */
public class RedisClusterTest {

    @BeforeClass
    public static void beforeAll(){
        Systemconfig.crawlerType = 1;

        String path = Thread.currentThread().getContextClassLoader().getResource("").getPath();
        AppContext.initAppCtx(path);

    }


}
