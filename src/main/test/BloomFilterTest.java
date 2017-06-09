package test;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by guanxiaoda on 2017/6/9.
 */
public class BloomFilterTest {

    private static final Set<HostAndPort> hostSet = new HashSet<>();
    static{
        hostSet.add(new HostAndPort("172.18.79.39",7000));
        hostSet.add(new HostAndPort("172.18.79.40",7000));
        hostSet.add(new HostAndPort("172.18.79.41",7000));
        hostSet.add(new HostAndPort("172.18.79.39",8000));
        hostSet.add(new HostAndPort("172.18.79.40",8000));
        hostSet.add(new HostAndPort("172.18.79.41",8000));
    }



    public static void main(String[] args) {
        JedisCluster redis = new JedisCluster(hostSet);
        redis.set("test2","wodetianna");

        String val = redis.get("test2");
        System.out.println(val);


    }
}
