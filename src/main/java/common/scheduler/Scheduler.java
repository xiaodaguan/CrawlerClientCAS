package common.scheduler;

import common.pojos.CrawlTask;

/**
 * Created by guanxiaoda on 2017/6/28.
 */
public interface Scheduler<T> {
    public void init();
    public CrawlTask getTask();
    public Long submitTask(T task);
    public Long getLeftTaskCount();
    public Long getTotalTaskCount();
    public void removeAllTask();
}
