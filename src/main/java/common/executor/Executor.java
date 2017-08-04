package common.executor;

import common.downloader.DefaultDownloader;
import common.downloader.DownloaderHelper;
import common.extractor.ExtractorHelper;
import common.extractor.xpath.XpathExtractor;
import common.pojos.*;
import common.system.Systemconfig;
import common.utils.MD5Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;

public class Executor implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(Executor.class);

    private String mediaTypeFull;
    private String mediaTypePrefix;

    public Executor(String mediaTypeFull, String mediaTypePrefix) {
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
                if (Systemconfig.urlFilter.contains(MD5Util.MD5(task.getOrignUrl()))) {
                    LOGGER.info("已采集过的url[{}]，跳过", task.getOrignUrl());
                    continue;
                }
                LOGGER.info("task count: left/total[{}/{}]", Systemconfig.scheduler.getLeftTaskCount(), Systemconfig.scheduler.getTotalTaskCount());
                if (task.getOrignUrl() == null) {
                    continue;
                }
                //  download
                DownloaderHelper.createDownloader(task).download();

                if (task.getContent() == null || task.getContent().length() < 5) {
                    LOGGER.error("下载页面内容出错，跳过解析");
                    continue;
                }
                List listData = DataHelper.createDataList(Systemconfig.crawlerType);
                //  parse
                XpathExtractor extractor = ExtractorHelper.createExtractor(task, mediaTypeFull, mediaTypePrefix);
                String nextUrl = extractor.extract(task, listData);
                //  save/submitTasks and add filter
                submitOrSave(task, listData, nextUrl);
                // sleep
            } catch (Exception e) {
                if (e instanceof SQLIntegrityConstraintViolationException)
                    LOGGER.error("插入错误：违反数据库约束");
                else if (e instanceof SocketTimeoutException)
                    LOGGER.error("请求错误：隧道连接超时");
                else
                    e.printStackTrace();
            } finally {
                LOGGER.info("sleeping...");
                try {
                    Thread.sleep(1000*task.getInterval());
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

            for (Object obj : listData) {
                CommonData data = (CommonData) obj;
                if (Systemconfig.urlFilter.contains(MD5Util.MD5(task.getOrignUrl()))) {
                    listData.remove(obj);
                    LOGGER.info("已采集过的url[{}]，跳过", task.getOrignUrl());
                    continue;
                }
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
            if (nextUrl != null && listData.size()>0) {
                task.setOrignUrl(nextUrl);
                Systemconfig.scheduler.submitTask(task);
            }else{
                LOGGER.info("当前页面已采集完成或者next is null:{}",nextUrl);
            }
        } else {
            Systemconfig.dbService.saveData(listData.get(0));
            LOGGER.info("一条信息采集并保存完成");
            Systemconfig.urlFilter.add(MD5Util.MD5(task.getOrignUrl()));
        }
    }
}
