package common;

import common.download.DownFactory;
import common.download.GenericCommonDownload;
import common.download.GenericMetaCommonDownload;
import common.pojos.HtmlInfo;
import common.rmi.packet.CrawlerType;
import common.rmi.packet.SearchKey;
import common.system.AppContext;
import common.system.Systemconfig;
import common.utils.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by guanxiaoda on 2017/7/4.
 */
public class Crawler {
    public static final Logger LOGGER = LoggerFactory.getLogger(Crawler.class);

    public static String mediaTypeFull = null;// e.g., "news_search"
    public static String mediaTypePrefix = null; // e.g., "news"

    static {
        // 读取配置
        String path = Thread.currentThread().getContextClassLoader().getResource("").getPath();
        AppContext.initAppCtx(path);
    }

    public Crawler(String crawlerName, int mediaType) {
        if (mediaType == 0) {
            LOGGER.error("未定义爬虫媒体类型");
            return;
        }
        CrawlerType mediatype = CrawlerType.getCrawlerTypeMap().get(mediaType);
        if (mediatype == null) {
            LOGGER.error("错误的爬虫媒体类型");
            return;
        }
        Systemconfig.crawlerType = mediaType;
        mediaTypeFull = mediatype.name();
        mediaTypePrefix = mediaTypeFull.substring(0, mediaTypeFull.indexOf("_"));


    }


    public void start() {


        // 是否初始化url队列
        //
        while (true) {
            //  getTask()
            HtmlInfo task = Systemconfig.scheduler.getTask();
            if (task.getOrignUrl() == null) {
                continue;
            }
            //  download()
            // 先用同步阻塞方式处理
            GenericCommonDownload downloader = null;
            if (task.getCrawlerType().equalsIgnoreCase("META")) {
                 downloader = DownFactory.metaControl(task.getSearchKey());
            } else if (task.getCrawlerType().equalsIgnoreCase("DATA")) {
                String dataClsName = StringUtil.upperFirstLetter(mediaTypePrefix.toLowerCase())+"Data";
                downloader = DownFactory.dataControl(task.getSite(),, 1, )
            }
            //  parse()
            //  save()/submitTasks()
            //  handleException()
        }

    }

    public static void main(String[] args) {
        Crawler crawler = new Crawler("TestNewsSearch", 1);
        crawler.start();
    }
}
