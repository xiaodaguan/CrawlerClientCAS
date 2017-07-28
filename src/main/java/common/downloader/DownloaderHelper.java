package common.downloader;


import common.downloader.weibo.WeiboSearchDownload;
import common.pojos.CrawlTask;
import common.system.Systemconfig;


public class DownloaderHelper {

    public static DefaultDownloader createDownloader(CrawlTask task) {
        switch (Systemconfig.crawlerType) {
            case 7:
            case 8:
                return new WeiboSearchDownload(task);
            default:
                return new DefaultDownloader(task);
        }
    }
}
