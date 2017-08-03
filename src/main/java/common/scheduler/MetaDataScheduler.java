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
public class MetaDataScheduler implements Scheduler<CrawlTask> {

    private static final Logger LOGGER = LoggerFactory.getLogger(MetaDataScheduler.class);

    private Set<HostAndPort> hostAndPortSet;
    private static JedisCluster jedis;

    private static String TASK_QUEUE_META;
    private static String TASK_QUEUE_DATA;
    private static String TASK_POPPED;// 暂时不用
    private static String TASK_QUEUE_TOTAL_COUNT_META;
    private static String TASK_QUEUE_TOTAL_COUNT_DATA;

    @Override
    public void init() {
        jedis = new JedisCluster(hostAndPortSet);
        TASK_QUEUE_META = "TASK_QUEUE_META_" + Systemconfig.crawlerType;
        TASK_QUEUE_DATA = "TASK_QUEUE_DATA_" + Systemconfig.crawlerType;
        TASK_POPPED = "TASK_POPPED";
        TASK_QUEUE_TOTAL_COUNT_META = "TOTAL_TASK_COUNT_META_" + Systemconfig.crawlerType;
        TASK_QUEUE_TOTAL_COUNT_DATA = "TOTAL_TASK_COUNT_DATA_" + Systemconfig.crawlerType;


        LOGGER.info("redis init [ok].");
        LOGGER.info("meta task queue name: {}", TASK_QUEUE_META);
        LOGGER.info("data task queue name: {}", TASK_QUEUE_DATA);
    }

    public void setHostAndPortSet(Set<HostAndPort> hostAndPortSet) {
        this.hostAndPortSet = hostAndPortSet;
    }

    @Override
    public CrawlTask getTask() {
        LOGGER.info("getting task...");

        List<String> taskStr = jedis.brpop(1, TASK_QUEUE_DATA);
        if (taskStr == null||taskStr.size()==0) {
            LOGGER.info("getting data task is null");
            taskStr = jedis.brpop(0, TASK_QUEUE_META);
            if (taskStr == null||taskStr.size()==0) {
                LOGGER.info("getting meta task is null");
                return null;
            }else{
                CrawlTask task = SerializeUtil.getObjectFromString(taskStr.get(1), CrawlTask.class);
                LOGGER.info("getting meta task...[ok]. -> {}" , task);
                return task;
            }
        } else {
            CrawlTask task = SerializeUtil.getObjectFromString(taskStr.get(1), CrawlTask.class);
            LOGGER.info("getting data task...[ok]. -> {}" , task);
            return task;
        }
    }


    @Override
    public Long submitTask(CrawlTask task) {
        LOGGER.info("submitting task...");

        if(task.getCrawlerType().toLowerCase().contains("meta")){
            Long leftCount = jedis.lpush(TASK_QUEUE_META, SerializeUtil.object2String(task));
            LOGGER.info("submitting meta task...[ok].[{}]", task);
            jedis.incr(TASK_QUEUE_TOTAL_COUNT_META);
            return leftCount;
        }else {
            Long leftCount = jedis.lpush(TASK_QUEUE_DATA, SerializeUtil.object2String(task));
            LOGGER.info("submitting data task...[ok].[{}]", task);
            jedis.incr(TASK_QUEUE_TOTAL_COUNT_DATA);
            return leftCount;
        }


    }

    @Override
    public Long getLeftTaskCount() {
        Long len = 0L;
        if(jedis.llen(TASK_QUEUE_META)!=null){
            len+=jedis.llen(TASK_QUEUE_META);
        }
        if(jedis.llen(TASK_QUEUE_DATA)!=null){
            len+=jedis.llen(TASK_QUEUE_DATA);
        }
        return len;
    }

    @Override
    public Long getTotalTaskCount() {

        Long len = 0L;
        if(jedis.get(TASK_QUEUE_TOTAL_COUNT_META)!=null){
            len+=Long.valueOf(jedis.get(TASK_QUEUE_TOTAL_COUNT_META));
        }
        if(jedis.get(TASK_QUEUE_TOTAL_COUNT_DATA)!=null){
            len+=Long.valueOf(jedis.get(TASK_QUEUE_TOTAL_COUNT_DATA));
        }
        return len;
    }

    @Override
    public void removeAllTask() {
        LOGGER.info("removing all task...");
        jedis.del(TASK_QUEUE_META);
        jedis.del(TASK_QUEUE_TOTAL_COUNT_META);
        jedis.del(TASK_QUEUE_DATA);
        jedis.del(TASK_QUEUE_TOTAL_COUNT_DATA);
        LOGGER.info("removing all task...[ok].");
    }
}
