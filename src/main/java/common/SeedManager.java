package common;

import common.pojos.CrawlTask;
import common.siteinfo.Siteinfo;
import common.system.AppContext;
import common.system.Systemconfig;
import common.task.CrawlerType;
import common.task.SearchKey;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class SeedManager {

    public SeedManager() {
        Systemconfig.crawlerType = 1;//无所谓，每种都可以获取serachkey
        String path = Thread.currentThread().getContextClassLoader().getResource("").getPath();
        AppContext.initAppCtx(path);
    }

    public void generate() {

        List<SearchKey> allSearchKeys = Systemconfig.dbService.getAllSearchKeys();
        List<CrawlTask> allTasks = searchKeys2Tasks(allSearchKeys);
        for (CrawlTask task : allTasks) {

            Systemconfig.scheduler.submitTask(task);

        }
    }

    private List<CrawlTask> searchKeys2Tasks(List<SearchKey> allSearchKeys) {
        List<CrawlTask> tasks = new ArrayList<>();
        Set<String> allSiteNames = Systemconfig.allSiteinfos.keySet();

        for (SearchKey searchKey : allSearchKeys) {
            List<Integer> mediaTypes = searchKey.getMediaTypeList();
            for(int mediaType: mediaTypes) {
                String mediaTypeFull = CrawlerType.getCrawlerTypeMap().get(mediaType).name();

                for (String siteInfoName : allSiteNames) {
                    if(!siteInfoName.toLowerCase().contains(mediaTypeFull.toLowerCase()))
                        continue;

                    Siteinfo siteinfo = Systemconfig.allSiteinfos.get(siteInfoName);


                    CrawlTask task = new CrawlTask();
                    task.setCrawlerType("META");// META, DATA
                    task.setOrignUrl(siteinfo.getUrl().replace("<keyword>", searchKey.getKEYWORD()));//http://news.baidu.com/ns?word=青岛 交通委 王勇&cl=2&rn=50&clk=sortbyrel
                    task.setEncode(siteinfo.getCharset());//utf-8
                    task.setSearchKey(searchKey);// CategoryCode[4], Keyword[青岛 交通委 王勇], SiteId[{2}], SiteName[null]

                    task.setSite(siteinfo.getSiteName());//baidu_news_search

                    if(task.getSite().contains("baidu")) {
                        task.setAgent(true);
                        task.setRetryTimes(3);
                        task.setInterval(1);
                    }

                    tasks.add(task);
                }
            }
        }
        return tasks;
    }

    /**
     * 删除全部待采集任务
     */
    public void clearAll(){

        Systemconfig.scheduler.removeAllTask();
    }

    public static void main(String[] args) throws InterruptedException {

        SeedManager seedManager = new SeedManager();
        while(true) {
            seedManager.clearAll();//清空
            seedManager.generate();//生成

            Thread.sleep(1000*3600*24);//等待
        }
    }
}
