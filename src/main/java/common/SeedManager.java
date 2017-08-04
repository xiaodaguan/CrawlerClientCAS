package common;

import common.pojos.CrawlTask;
import common.siteinfo.Siteinfo;
import common.system.AppContext;
import common.system.Systemconfig;
import common.task.CrawlerType;
import common.task.SearchKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class SeedManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(SeedManager.class);

    public SeedManager() {

        String //path = Thread.currentThread().getContextClassLoader().getResource("").getPath();
        path = "src/main/resources";
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
                    }else{
                        task.setAgent(false);
                        task.setRetryTimes(1);
                    }
                    Systemconfig.scheduler.submitTask(task);
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
    public static void run(int crawlerType) {
        Systemconfig.crawlerType = crawlerType;//指定获取serachkey的爬虫类型
        SeedManager seedManager = new SeedManager();
        seedManager.clearAll();//清空
        seedManager.generate();//生成
        LOGGER.info("本轮所有任务提交完成，提交总的任务数为：{}",Systemconfig.scheduler.getTotalTaskCount());
    }
    private static String read(String fileName) {
        if (!(new File(fileName).exists())) {
            LOGGER.info("文件不存在:{}",fileName);
            return null;
        }
        try {
            StringBuffer sb = new StringBuffer();
            BufferedReader br = new BufferedReader(new FileReader(new File(fileName)));
            for (String line = br.readLine(); line != null; line = br.readLine()) {
                sb.append(line.trim() + "\n");
            }
            br.close();
            return sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    private static List<Integer> getCrawlerTypeList(){
        String path = "src/main/resources/seedConf/seed.txt";
        String content = read(path);
        String []lines = content.split("\n");

        List<Integer> crawlerTypeList = new ArrayList<>();
        for(String line:lines){
            if(!line.startsWith("#")){
                int crawlerType = Integer.parseInt(line.split("#")[1]);
                crawlerTypeList.add(crawlerType);
            }
        }
        return crawlerTypeList;
    }
    public static void seedMain() {
        while(true) {
            List<Integer> list = getCrawlerTypeList();
            for(int crawlerType:list){
                run(crawlerType);
            }
            try {
                Thread.sleep(1000 * 60 * 60 * 24);//等待
            }catch (InterruptedException e){
                e.printStackTrace();
            }
        }

    }
}
