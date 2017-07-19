package common.downloader;

import common.pojos.HtmlInfo;
import org.junit.Test;

/**
 * Created by guanxiaoda on 2017/7/18.
 */
public class DownloaderTest {



    @Test
    public void DownTest(){
        HtmlInfo task = new HtmlInfo();
        task.setOrignUrl("http://guanxiaoda.cn");
        task.setRetryTimes(3);
        DefaultDownloader downloader = new DefaultDownloader(task);
        downloader.download();

        System.out.println(task.getContent());
    }
}
