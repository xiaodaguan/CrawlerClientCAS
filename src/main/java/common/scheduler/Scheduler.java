package common.scheduler;

import common.pojos.HtmlInfo;

/**
 * Created by guanxiaoda on 2017/6/28.
 */
public interface Scheduler<T> {
    public void init();
    public HtmlInfo getTask();
    public Long submitTask(T task);
    public Long getLeftTaskCount();
    public Long getTotalTaskCount();
    public void removeAllTask();
}
