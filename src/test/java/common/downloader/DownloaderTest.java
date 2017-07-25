package common.downloader;

import common.pojos.CrawlTask;
import common.system.AppContext;
import common.system.Systemconfig;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Created by guanxiaoda on 2017/7/18.
 */
public class DownloaderTest {

    @BeforeClass
    public static void beforeAll(){
        Systemconfig.crawlerType = 1;

        String path = Thread.currentThread().getContextClassLoader().getResource("").getPath();
        AppContext.initAppCtx(path);
    }


    @Test
    public void DownTest(){
//        CrawlTask task = new CrawlTask();
//        task.setOrignUrl("http://guanxiaoda.cn");
//        task.setRetryTimes(3);

        CrawlTask task = Systemconfig.scheduler.getTask();
        DefaultDownloader downloader = new DefaultDownloader(task);
        downloader.download();

        System.out.println(task.getContent());
    }
}
