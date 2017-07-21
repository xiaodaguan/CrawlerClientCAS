package common.scheduler;

import common.pojos.CrawlTask;
import common.system.Systemconfig;
import common.utils.SerializeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;

import java.util.List;
import java.util.Set;

/**
 * Created by guanxiaoda on 2017/6/28.
 */
public class DefaultScheduler implements Scheduler<CrawlTask> {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultScheduler.class);

    private Set<HostAndPort> hostAndPortSet;
    private static JedisCluster jedis;

    private static String TASK_QUEUE;
    private static String TASK_POPPED;// 暂时不用
    private static String TASK_QUEUE_TOTAL_COUNT;

    @Override
    public void init() {
        jedis = new JedisCluster(hostAndPortSet);
        TASK_QUEUE = "TASK_QUEUE_" + Systemconfig.crawlerType;
        TASK_POPPED = "TASK_POPPED";
        TASK_QUEUE_TOTAL_COUNT = "TOTAL_TASK_COUNT_" + Systemconfig.crawlerType;

        LOGGER.info("redis init [ok].");
        LOGGER.info("task queue name: {}", TASK_QUEUE);
    }

    public void setHostAndPortSet(Set<HostAndPort> hostAndPortSet) {
        this.hostAndPortSet = hostAndPortSet;
    }

    @Override
    public CrawlTask getTask() {
        LOGGER.info("getting task...");
        List<String> taskStr = jedis.brpop(0, TASK_QUEUE);

        if (taskStr == null) {
            return null;
        } else {
            CrawlTask task = SerializeUtil.getObjectFromString(taskStr.get(1), CrawlTask.class);
            LOGGER.info("getting task...[ok]. -> {}" , task);
            return task;
        }
    }


    @Override
    public Long submitTask(CrawlTask task) {
        LOGGER.info("submitting task...");
        Long leftCount = jedis.lpush(TASK_QUEUE, SerializeUtil.object2String(task));
        LOGGER.info("submitting task...[ok].[{}]", task.getOrignUrl());
        jedis.incr(TASK_QUEUE_TOTAL_COUNT);
        return leftCount;

    }

    @Override
    public Long getLeftTaskCount() {
        Long result = jedis.llen(TASK_QUEUE);
        return result;
    }

    @Override
    public Long getTotalTaskCount() {
        return Long.valueOf(jedis.get(TASK_QUEUE_TOTAL_COUNT));
    }

    @Override
    public void removeAllTask() {
        LOGGER.info("removing all task...");
        jedis.del(TASK_QUEUE);
        jedis.del(TASK_QUEUE_TOTAL_COUNT);
        LOGGER.info("removing all task...[ok].");
    }
}
