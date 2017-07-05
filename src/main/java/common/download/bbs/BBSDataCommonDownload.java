package common.download.bbs;

import java.util.concurrent.CountDownLatch;

import common.pojos.BbsData;
import common.pojos.HtmlInfo;
import common.download.GenericDataCommonDownload;
import common.rmi.packet.SearchKey;
import common.system.Systemconfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 下载详细页面
 *
 * @author grs
 */
public class BBSDataCommonDownload extends GenericDataCommonDownload<BbsData> {
    private static final Logger LOGGER = LoggerFactory.getLogger(BBSDataCommonDownload.class);

    public BBSDataCommonDownload(String siteFlag, BbsData vd, CountDownLatch endCount, SearchKey key) {
        super(siteFlag, vd, endCount, key);
    }

    @Override
    public void process() {
        String url = getRealUrl(data);
        if (siteFlag.startsWith("tieba")) {
            // if(!url.contains("pid")) return;
        }
        if (url == null) return;
        HtmlInfo html = htmlInfo("DATA");
        try {
            if (url != null && !url.equals("")) {
                html.setOrignUrl(url);
                http.getContent(html);

                LOGGER.info(data.getUrl() + "下载完成。。。");

                if (html.getContent() == null || (html.getContent().contains("抱歉，您访问的贴子被隐藏") && html.getContent().contains("贴吧404"))) {
                    return;
                }
                // 解析数据
                url = xpath.templateContentPage(data, html);
       
                
                LOGGER.info(data.getTitle() + "解析完成。。。");
                if (dataCheck(data, html.getContent())) {
                    Systemconfig.dbService.saveData(data);
                    LOGGER.info(data.getTitle() + "保存完成。。。");
                } else {

                    LOGGER.info("缺失标题或pubtime，放弃保存。" + data.getUrl());
                }
            }
        } catch (Exception e) {
            LOGGER.info("采集出现异常" + data.getUrl(), e);
        } finally {
            if (count != null) count.countDown();
        }
    }

    /**
     * 验证解析属性
     * @param data
     * @param html
     * @return 是否可存储
     */
    private boolean dataCheck(BbsData data, String html) {
        if (data.getTitle() != null && data.getPubdate() != null) {
            return true;
        }
        if (html.contains("<title>贴吧404</title>")){
            LOGGER.error("帖子已被删除");
            return false;
        }
        return false;
    }
}
