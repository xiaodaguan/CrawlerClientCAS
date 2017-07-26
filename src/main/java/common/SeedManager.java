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
                    task.setCrawlerType("META");
                    task.setOrignUrl(siteinfo.getUrl().replace("<keyword>", searchKey.getKEYWORD()));
                    task.setEncode(siteinfo.getCharset());
                    task.setSearchKey(searchKey);
                    task.setSite(siteinfo.getSiteName());

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
