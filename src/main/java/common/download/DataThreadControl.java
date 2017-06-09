package common.download;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import common.bean.CommonData;
import common.extractor.xpath.frgmedia.search.sub.SinoExtractor;
import common.rmi.packet.SearchKey;
import common.system.Systemconfig;
import common.system.UserAttribute;
import common.util.TimeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 详细数据控制线程
 *
 * @author grs
 */
public class DataThreadControl {
    private static final Logger LOGGER = LoggerFactory.getLogger(DataThreadControl.class);

    protected final String siteFlag;
    protected final String unique;

    public DataThreadControl(String siteFlag, String unique) {
        this.siteFlag = siteFlag;
        this.unique = unique;
    }

    /**
     * 处理的第一种方式，列表数据采集一部分就开始采集内容页
     *
     * @param list
     * @param interval
     * @param key
     */
    public void process(List list, int interval, UserAttribute user, SearchKey key) {
        CountDownLatch endCount = new CountDownLatch(list.size());
        Iterator<CommonData> iter = list.iterator();
        int i = 0;
        while (iter.hasNext()) {
            // 提交一个url的采集任务
            CommonData vd = iter.next();
            synchronized (list) {
                iter.remove();
            }
            vd.setCompleteSize("[collect id: " + key.getId() + "| current: " + (++i) + "/ rest: " + list.size() + "]");
            Future f = Systemconfig.dataexec.get(siteFlag).submit(DownFactory.dataControl(siteFlag, vd, endCount, user, key));
            Systemconfig.tasks.put(siteFlag + "_" + key + "_" + vd.getTitle(), f);
            
            LOGGER.info(siteFlag + "_" + key + "_" + vd.getTitle()+"\tdataexec 任务添加");
            
            TimeUtil.rest(3);
        }
        try {
            endCount.await(2, TimeUnit.HOURS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }


}
