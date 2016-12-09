package common.download.weixin;

import java.util.concurrent.CountDownLatch;

import common.bean.HtmlInfo;
import common.bean.WeixinData;
import common.download.GenericDataCommonDownload;
import common.extractor.xpath.weixin.search.WeixinSearchXpathExtractor;
import common.rmi.packet.SearchKey;
import common.system.Systemconfig;
import common.util.TimeUtil;

/**
 * 下载详细页面
 *
 * @author grs
 */
public class WeixinDataCommonDownload extends GenericDataCommonDownload<WeixinData> implements Runnable {

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
                Systemconfig.sysLog.log("downloading: [" + data.getTitle() + "]\t[" + data.getUrl() + "]");
                html.setOrignUrl(url);

                http.getContent(html);
                // html.setContent();
                if (html.getContent() == null) {
                    Systemconfig.sysLog.log("get html content failed.");
                    return;
                }
                // 解析数据
                xpath.templateContentPage(data, html);
//                Systemconfig.sysLog.log("wait 2 sec to parse public account info...");

//                ((WeixinSearchXpathExtractor) xpath).parseGongzhong(data, html, html.getContent());
//

                Systemconfig.dbService.saveData(data);
                Systemconfig.sysLog.log("data: " + data.getTitle() + " saved.");

            }
        } catch (Exception e) {
            Systemconfig.sysLog.log("采集出现异常" + url, e);
        } finally {
            if (count != null) count.countDown();

            int wait = siteinfo.getDownInterval() + (int) Math.random() * 30;
            Systemconfig.sysLog.log("wait " + wait + " sec to parse public account info...");
            TimeUtil.rest(wait);
        }
    }

}
