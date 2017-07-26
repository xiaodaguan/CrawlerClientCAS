package common.proxy;

import common.utils.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.exceptions.JedisDataException;

import java.util.Random;
import java.util.Set;

public class ProxyPoolRedis {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProxyPoolRedis.class);

    private Set<HostAndPort> hostAndPortSet;
    private static String PROXY_POOL = "PROXY_POOL";
    private static JedisCluster redis;
    public void init(){
        LOGGER.info("proxy manager init...");
         redis = new JedisCluster(hostAndPortSet);
        LOGGER.info("proxy manager init...[ok].");


    }

    public void addOne(String proxyHostAndPort){
        String pattern = "[http[s]*://]*\\d+\\.\\d+\\.\\d+\\.\\d+:\\d+";
        if(!StringUtil.regFullMatch(proxyHostAndPort, pattern)){
            LOGGER.error("代理格式有误，添加失败！");
            return;
        }
        if(contains(PROXY_POOL, proxyHostAndPort)){
            LOGGER.error("该代理ip[{}]已在代理池中", proxyHostAndPort);
            Long count = redis.llen(PROXY_POOL);
            LOGGER.info("当前代理池中代理个数:{}", count);
            return;
        }
        LOGGER.info("add proxy {} to {}...", proxyHostAndPort, PROXY_POOL);

        Long afterAdd = redis.lpush(PROXY_POOL, proxyHostAndPort);//压入一个代理ip

        LOGGER.info("当前代理池中代理个数:{}", afterAdd);
        if(afterAdd>10){//如果代理池中ip数量超过10，则推出一个(最老的)，保持代理池中最多10个ip
            LOGGER.info("代理池已满，删除最早的代理...");
            String delProxy = redis.rpop(PROXY_POOL);
            LOGGER.info("代理池已满，删除最早的代理{}...[ok].", delProxy);
        }

    }

    private boolean contains(String proxyPool, String proxyHostAndPort) {
        for(int i=0;i<redis.llen(proxyPool);i++){
            if(proxyHostAndPort.equalsIgnoreCase(redis.lindex(proxyPool,i)))
                return true;
        }
        return false;
    }

    public String randomGetOne(){
        while(redis.llen(PROXY_POOL) == 0){

            LOGGER.info("当前代理池中无代理ip，等待重试..");
            try {
                Thread.sleep(1000*10);

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        int randomIndex =  new Random().nextInt(Math.toIntExact(redis.llen(PROXY_POOL)));
        String proxyHostAndPort = redis.lindex(PROXY_POOL, randomIndex);
        return proxyHostAndPort;
    }

    public void clearAll(){
        LOGGER.info("清空代理池...");
        redis.del(PROXY_POOL);
        LOGGER.info("清空代理池...[ok].");
    }


    public Set<HostAndPort> getHostAndPortSet() {
        return hostAndPortSet;
    }

    public void setHostAndPortSet(Set<HostAndPort> hostAndPortSet) {
        this.hostAndPortSet = hostAndPortSet;
    }
}
