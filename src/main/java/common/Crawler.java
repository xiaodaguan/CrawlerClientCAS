package common;

import common.executor.Executor;
import common.siteinfo.Siteinfo;
import common.task.CrawlerType;
import common.system.AppContext;
import common.system.Systemconfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by guanxiaoda on 2017/7/4.
 */
public class Crawler {
    public static final Logger LOGGER = LoggerFactory.getLogger(Crawler.class);

    public static String mediaTypeFull = null;// e.g., "news_search"
    public static String mediaTypePrefix = null; // e.g., "news"

    /**
     * 初始化，加载配置
     *
     * @param crawlerName 爬虫名称，用处自定
     * @param mediaType   媒体类型: 1->NewsSearch, 2->NewsMonitor
     */
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
        mediaTypeFull = mediatype.name();
        mediaTypePrefix = mediaTypeFull.substring(0, mediaTypeFull.indexOf("_"));

        Systemconfig.crawlerType = mediaType;

        String path = Thread.currentThread().getContextClassLoader().getResource("").getPath();
        LOGGER.info("path:{}",path);
        AppContext.initAppCtx(path);
    }

    public void start(int threadNum) {
        ExecutorService threadPool = Executors.newFixedThreadPool(threadNum);
        for (int i = 0; i < threadNum; i++)
            threadPool.submit(new Executor(mediaTypeFull, mediaTypePrefix));

    }

    public static void main(String[] args) {
        //Crawler crawler = new Crawler("BaiduNewsSearch", 1);
        String crawlerName = "";//WeiboSearchSina
        int mediaType=0;


        for(String arg:args){
            LOGGER.info("arg:{}",arg);
            if(arg.toLowerCase().contains("crawlername")){
                crawlerName = arg.split("=")[1].trim();
            }else if(arg.toLowerCase().contains("mediatype")){
                String mediaType_tmp = arg.split("=")[1].trim();
                mediaType = Integer.parseInt(mediaType_tmp);
            }
        }
        Crawler crawler = new Crawler(crawlerName, mediaType);
        Map<String,Siteinfo> siteinfoMap = Systemconfig.allSiteinfos;
        Set<String> siteinfoKeys = siteinfoMap.keySet();
        for(String siteinfokey:siteinfoKeys){
            Siteinfo siteinfo = siteinfoMap.get(siteinfokey);
            if(siteinfo.getSiteFlag()==mediaType){
                crawler.start(siteinfo.getThreadNum());
                break;
            }
        }
    }
}
