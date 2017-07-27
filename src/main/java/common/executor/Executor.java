package common.executor;

import common.downloader.DefaultDownloader;
import common.extractor.ExtractorHelper;
import common.extractor.xpath.XpathExtractor;
import common.pojos.CommonData;
import common.pojos.CrawlTask;
import common.pojos.DataHelper;
import common.system.Systemconfig;
import common.utils.MD5Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class Executor implements Runnable{
    private static final Logger LOGGER = LoggerFactory.getLogger(Executor.class);

    private String mediaTypeFull;
    private String mediaTypePrefix;

    public Executor(String mediaTypeFull, String mediaTypePrefix){
        this.mediaTypeFull = mediaTypeFull;
        this.mediaTypePrefix = mediaTypePrefix;
    }

    @Override
    public void run() {

        //
        while (true) {
            CrawlTask task = null;
            try {
                //  getTask
                task = Systemconfig.scheduler.getTask();
                if(Systemconfig.urlFilter.contains(MD5Util.MD5(task.getOrignUrl()))) {
                    LOGGER.info("已采集过的url[{}]，跳过", task.getOrignUrl());
                    continue;
                }

                LOGGER.info("task count: left/total[{}/{}]", Systemconfig.scheduler.getLeftTaskCount(), Systemconfig.scheduler.getTotalTaskCount());
                if (task.getOrignUrl() == null) {
                    continue;
                }
                //  download
                DefaultDownloader downloader = new DefaultDownloader(task);
                downloader.download();

                if(task.getContent() == null || task.getContent().length() < 10){
                    LOGGER.error("下载页面内容出错，跳过解析");
                    continue;
                }

                //  parse

                List listData = DataHelper.createDataList(Systemconfig.crawlerType);

                XpathExtractor extractor = ExtractorHelper.createExtractor(task, mediaTypeFull, mediaTypePrefix);
                String nextUrl = extractor.extract(task, listData);
                //  save/submitTasks and add filter

                submitOrSave(task, listData, nextUrl);


                // sleep
            } catch (Exception e) {
                e.printStackTrace();
            }finally {

                LOGGER.info("sleeping...");
                try {
                    Thread.sleep(1000 * task.getInterval());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 如果当前任务类型是列表页，则将后续链接生成任务提交；如果是详情页，则保存数据
     * 将当前url加入urlFilter
     *
     * @param task     当前任务
     * @param listData 如果是列表页，则为解析所得meta数据，如果是详情页，则数据存储为list的首个元素
     * @param nextUrl  解析所得后续url
     */
    private void submitOrSave(CrawlTask task, List listData, String nextUrl) throws Exception {
        if (task.getCrawlerType().equalsIgnoreCase("meta")) {
            if (nextUrl != null) {
                task.setOrignUrl(nextUrl);
                Systemconfig.scheduler.submitTask(task);
            }
            for (Object obj : listData) {
                CommonData data = (CommonData) obj;
                CrawlTask followTask = new CrawlTask();

                /* 继承属性 */
                followTask.setOrignUrl(data.getUrl());
                followTask.setMediaType(task.getMediaType());
                followTask.setCrawlerType("DATA");
                followTask.setSite(task.getSite());
                followTask.setSearchKey(task.getSearchKey());
                followTask.setData(data);
                followTask.setAgent(task.getAgent());
                followTask.setRetryTimes(task.getRetryTimes());
                followTask.setInterval(task.getInterval());


                Systemconfig.scheduler.submitTask(followTask);
            }
        } else {
            Systemconfig.dbService.saveData(listData.get(0));
            Systemconfig.urlFilter.add(MD5Util.MD5(task.getOrignUrl()));
        }

    }
}
