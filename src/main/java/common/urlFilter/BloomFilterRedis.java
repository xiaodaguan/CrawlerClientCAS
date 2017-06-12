package common.urlFilter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;

import java.util.Set;

/**
 * Created by guanxiaoda on 2017/6/12.
 */
public class BloomFilterRedis {
    private static Logger LOGGER = LoggerFactory.getLogger(BloomFilterRedis.class);

    private BloomFilter<String> bloomFilter;
    private Set<HostAndPort> hostAndPortSet;
    private JedisCluster jedisCluster;

    public void init(){
        JedisCluster redis = new JedisCluster(hostAndPortSet);
        bloomFilter.bind(redis,"redis-bloom");
        LOGGER.info("redis bloom filter init succeed.");
    }

    public boolean contains(String element){
        return bloomFilter.contains(element);
    }

    public void add(String element){
        bloomFilter.add(element);
    }


    public BloomFilter<String> getBloomFilter() {
        return bloomFilter;
    }

    public void setBloomFilter(BloomFilter<String> bloomFilter) {
        this.bloomFilter = bloomFilter;
    }

    public Set<HostAndPort> getHostAndPortSet() {
        return hostAndPortSet;
    }

    public void setHostAndPortSet(Set<HostAndPort> hostAndPortSet) {
        this.hostAndPortSet = hostAndPortSet;
    }

}
