package common;

import common.http.SimpleHttpProcess;
import common.system.Systemconfig;
import common.util.StringUtil;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;

import java.util.HashSet;
import java.util.Set;

public class ProxyPool {

    private static JedisCluster redis=null;

    static {

    }

    private static final String PROXY_POOL = "PROXY_POOL";


    public static void main(String[] args) throws InterruptedException {

        System.out.println("开始....");
        // connect redis
        connect();
        System.out.println("连接到redis集群.");
        // clear
        clearAll();

        //
        while(true) {

            try {
                // get a proxy
                String ip_host = getFromProvider();
                System.out.println("获取ip： "+ip_host);

                // add to pool
                addPool(ip_host);
                System.out.println("push 成功.");


            }catch(Exception e ){
                e.printStackTrace();
            }finally {
                Thread.sleep(1000 * 5);
            }
        }
    }

    private static void connect() {

        Set<HostAndPort> hostAndPortSet= new HashSet<>();
        hostAndPortSet.add(new HostAndPort("192.168.1.103", 7001));
        hostAndPortSet.add(new HostAndPort("192.168.1.103", 7002));
        hostAndPortSet.add(new HostAndPort("192.168.1.103", 7003));
        hostAndPortSet.add(new HostAndPort("192.168.1.103", 8001));
        hostAndPortSet.add(new HostAndPort("192.168.1.103", 8002));
        hostAndPortSet.add(new HostAndPort("192.168.1.103", 8003));

//        conf.setti


        redis = new JedisCluster(hostAndPortSet);
        System.out.println("集群节点："+redis.getClusterNodes());

    }

    private static void addPool(String proxyHostAndPort) {

        String pattern = "[http[s]*://]*\\d+\\.\\d+\\.\\d+\\.\\d+:\\d+";
        if (!StringUtil.regFullMatch(proxyHostAndPort, pattern)) {
            Systemconfig.sysLog.log("代理格式有误，添加失败！");
            return;
        }






            if (contains(PROXY_POOL, proxyHostAndPort)) {
                Systemconfig.sysLog.log("该代理ip["+proxyHostAndPort+"]已在代理池中");
                Long count = redis.llen(PROXY_POOL);
                Systemconfig.sysLog.log("当前代理池中代理个数:"+count);
                return;
            }

        Systemconfig.sysLog.log("adding proxy "+proxyHostAndPort+" to "+PROXY_POOL+"..." );

        Long afterAdd = redis.lpush(PROXY_POOL, proxyHostAndPort);//压入一个代理ip

        Systemconfig.sysLog.log("当前代理池中代理个数:"+afterAdd);
        if (afterAdd > 10) {//如果代理池中ip数量超过10，则推出一个(最老的)，保持代理池中最多10个ip
            Systemconfig.sysLog.log("代理池已满，删除最早的代理...");
            String delProxy = redis.rpop(PROXY_POOL);
            Systemconfig.sysLog.log("代理池已满，删除最早的代理["+delProxy+"]...[ok].");
        }
    }

    private static String getFromProvider() {
        return SimpleHttpProcess.getRandomProxyFromProvider();
    }

    private static boolean contains(String proxyPool, String proxyHostAndPort) {
        for (int i = 0; i < redis.llen(proxyPool); i++) {
            if (proxyHostAndPort.equalsIgnoreCase(redis.lindex(proxyPool, i)))
                return true;
        }
        return false;
    }

    public static void clearAll() {
        Systemconfig.sysLog.log("清空代理池...");
        redis.del(PROXY_POOL);
        Systemconfig.sysLog.log("清空代理池...[ok].");
    }


}
