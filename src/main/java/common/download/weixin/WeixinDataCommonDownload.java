package common.download.weixin;

import java.util.concurrent.CountDownLatch;

import common.pojos.HtmlInfo;
import common.pojos.WeixinData;
import common.download.GenericDataCommonDownload;
import common.rmi.packet.SearchKey;
import common.system.Systemconfig;
import common.utils.TimeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 下载详细页面
 *
 * @author grs
 */
public class WeixinDataCommonDownload extends GenericDataCommonDownload<WeixinData> implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(WeixinDataCommonDownload.class);

    public WeixinDataCommonDownload(String siteFlag, WeixinData data, CountDownLatch endCount, SearchKey key) {
        super(siteFlag, data, endCount, key);
    }

    public void process() {
        String url = getRealUrl(data);
        if (url == null || url.equals("err.")) return;
        // 检测是否需要代理，未来版本改进
        siteinfo.setAgent(false);
        HtmlInfo html = htmlInfo("DATA");
        if (data.getCrawler_cookie() != null) html.setCookie(data.getCrawler_cookie());
        try {
            if (url != null && !url.equals("")) {
                LOGGER.info("downloading: [" + data.getTitle() + "]\t[" + data.getUrl() + "]");
                html.setOrignUrl(url);

                http.getContent(html);
                // html.setContent();
                if (html.getContent() == null) {
                    LOGGER.info("get html content failed.");
                    return;
                }
                // 解析数据
                xpath.templateContentPage(data, html);
//                LOGGER.info("wait 2 sec to parse public account info...");

//                ((WeixinSearchXpathExtractor) xpath).parseGongzhong(data, html, html.getContent());
//
                LOGGER.info(data.getTitle() + "解析完成。。。");
                Systemconfig.dbService.saveData(data);
                LOGGER.info(data.getTitle() + "保存完成。。。");

            }
        } catch (Exception e) {
            LOGGER.info("采集出现异常" + url, e);
        } finally {
            if (count != null) count.countDown();

            int wait = siteinfo.getDownInterval() + (int) Math.random() * 30;
            LOGGER.info("wait " + wait + " secs to download next...");
            TimeUtil.rest(wait);
        }
    }

}
