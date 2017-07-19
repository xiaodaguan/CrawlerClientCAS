package common;

import common.downloader.DefaultDownloader;
import common.extractor.ExtractorHelper;
import common.extractor.xpath.XpathExtractor;
import common.pojos.CommonData;
import common.pojos.DataHelper;
import common.pojos.HtmlInfo;
import common.rmi.packet.CrawlerType;
import common.system.AppContext;
import common.system.Systemconfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
        AppContext.initAppCtx(path);

    }


    public void start() {


        //
        while (true) {

            //  getTask()
            HtmlInfo task = Systemconfig.scheduler.getTask();
            if (task.getOrignUrl() == null) {
                continue;
            }
            //  downloader()
            DefaultDownloader downloader = new DefaultDownloader(task);
            downloader.download();

            //  parse()

            List listData = DataHelper.createDataList(Systemconfig.crawlerType);

            XpathExtractor extractor = ExtractorHelper.createExtractor(task, mediaTypeFull, mediaTypePrefix);
            String nextUrl = extractor.extract(task, listData);
            //  save()/submitTasks()

            submitOrSave(task, listData, nextUrl);

            // sleep {} seconds.
            try {
                Thread.sleep(1000 * 1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }

    }

    /**
     * 如果当前任务类型是列表页，则将后续链接生成任务提交；如果是详情页，则保存数据
     *
     * @param task     当前任务
     * @param listData 如果是列表页，则为解析所得meta数据，如果是详情页，则数据存储为list的首个元素
     * @param nextUrl  解析所得后续url
     */
    private void submitOrSave(HtmlInfo task, List listData, String nextUrl) {
        if (task.getCrawlerType().equalsIgnoreCase("meta")) {
            if (nextUrl != null) {
                task.setOrignUrl(nextUrl);
                Systemconfig.scheduler.submitTask(task);
            }
            for (Object obj : listData) {
                CommonData data = (CommonData) obj;
                HtmlInfo followTask = new HtmlInfo();
                followTask.setOrignUrl(data.getUrl());
                followTask.setMediaType(task.getMediaType());
                followTask.setCrawlerType("DATA");
                followTask.setSite(task.getSite());
                followTask.setSearchKey(task.getSearchKey());
                followTask.setData(data);

                Systemconfig.scheduler.submitTask(followTask);
            }
        } else {
            try {
                Systemconfig.dbService.saveData(listData.get(0));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        Crawler crawler = new Crawler("TestNewsSearch", 1);
        crawler.start();
    }
}
