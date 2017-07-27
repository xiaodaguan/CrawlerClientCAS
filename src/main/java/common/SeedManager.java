package common;

import common.pojos.CrawlTask;
import common.siteinfo.Siteinfo;
import common.system.AppContext;
import common.system.Systemconfig;
import common.task.CrawlerType;
import common.task.SearchKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class SeedManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(SeedManager.class);

    public SeedManager() {
        Systemconfig.crawlerType = 1;//无所谓，每种都可以获取serachkey
        String path = Thread.currentThread().getContextClassLoader().getResource("").getPath();
        AppContext.initAppCtx(path);
    }

    public void generate() {

        LOGGER.info("从数据库获取关键词...");
        List<SearchKey> allSearchKeys = Systemconfig.dbService.getAllSearchKeys();
        LOGGER.info("获取{}个关键词", allSearchKeys.size());
        LOGGER.info("生成种子列表并提交...");
        generateAndSubmit(allSearchKeys);
    }

    private void generateAndSubmit(List<SearchKey> allSearchKeys) {
        Set<String> allSiteNames = Systemconfig.allSiteinfos.keySet();

        /**
         * 三层嵌套组合：关键词+类型+站点
         * 例如：xxxxx + news_search + baidu
         */
        for (SearchKey searchKey : allSearchKeys) {
            List<Integer> mediaTypes = searchKey.getMediaTypeList();
            for (int mediaType : mediaTypes) {
                CrawlerType crawlerType = CrawlerType.getCrawlerTypeMap().get(mediaType);
                if (crawlerType == null)
                    continue;
                String mediaTypeFull = crawlerType.name();

                for (String siteInfoName : allSiteNames) {
                    if (!siteInfoName.toLowerCase().contains(mediaTypeFull.toLowerCase()))
                        continue;

                    Siteinfo siteinfo = Systemconfig.allSiteinfos.get(siteInfoName);


                    CrawlTask task = new CrawlTask();
                    task.setCrawlerType("META");// META, DATA
                    task.setOrignUrl(siteinfo.getUrl().replace("<keyword>", searchKey.getKEYWORD()));//http://news.baidu.com/ns?word=青岛 交通委 王勇&cl=2&rn=50&clk=sortbyrel
                    task.setEncode(siteinfo.getCharset());//utf-8
                    task.setSearchKey(searchKey);// CategoryCode[4], Keyword[青岛 交通委 王勇], SiteId[{2}], SiteName[null]

                    task.setSite(siteinfo.getSiteName());//baidu_news_search

                    if (task.getSite().contains("baidu")) {
                        task.setAgent(true);
                        task.setRetryTimes(3);
                        task.setInterval(1);
                    }

                    Systemconfig.scheduler.submitTask(task);

                    try {
                        LOGGER.info("等待提交下一个关键词...");
                        Thread.sleep(1000 * 30);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * 删除全部待采集任务
     */
    public void clearAll() {

        Systemconfig.scheduler.removeAllTask();
    }

    public static void main(String[] args) throws InterruptedException {

        SeedManager seedManager = new SeedManager();
        while (true) {
            seedManager.clearAll();//清空
            seedManager.generate();//生成

            Thread.sleep(1000 * 60 * 5);//等待
        }
    }
}