package common.scheduler;

import common.pojos.HtmlInfo;
import common.utils.SerializeUtil;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;

import java.util.List;
import java.util.Set;

/**
 * Created by guanxiaoda on 2017/6/28.
 */
public class DefaultScheduler implements Scheduler<HtmlInfo> {

    private Set<HostAndPort> hostAndPortSet;
    private static JedisCluster jedis;

    private static String TASK_QUEUE;
    private static String TASK_POPPED;// 暂时不用
    private static String TASK_QUEUE_TOTAL_COUNT;

    @Override
    public void init() {
        jedis = new JedisCluster(hostAndPortSet);
        TASK_QUEUE = "TASK_QUEUE";
        TASK_POPPED = "TASK_POPPED";
        TASK_QUEUE_TOTAL_COUNT = "TOTAL_TASK_COUNT";
    }

    public void setHostAndPortSet(Set<HostAndPort> hostAndPortSet) {
        this.hostAndPortSet = hostAndPortSet;
    }

    @Override
    public HtmlInfo getTask() {
        List<String> taskStr = jedis.brpop(0, TASK_QUEUE);
        if (taskStr == null) {
            return null;
        } else {
            return SerializeUtil.getObjectFromString(taskStr.get(1), HtmlInfo.class);
        }
    }


    @Override
    public Long submitTask(HtmlInfo task) {

        Long leftCount = jedis.lpush(TASK_QUEUE, SerializeUtil.object2String(task));
        jedis.incr(TASK_QUEUE_TOTAL_COUNT);
        return leftCount;

    }

    @Override
    public Long getLeftTaskCount() {
        return jedis.llen(TASK_QUEUE);
    }

    @Override
    public Long getTotalTaskCount() {
        return Long.valueOf(jedis.get(TASK_QUEUE_TOTAL_COUNT));
    }

    @Override
    public void removeAllTask() {
        jedis.del(TASK_QUEUE);
    }
}
