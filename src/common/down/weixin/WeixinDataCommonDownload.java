package common.down.weixin;

import java.io.IOException;
import java.sql.Time;
import java.util.concurrent.CountDownLatch;

import common.bean.HtmlInfo;
import common.bean.WeixinData;
import common.down.GenericDataCommonDownload;
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
        if (url == null || url.equals("err."))
            return;
        // 检测是否需要代理，未来版本改进
        siteinfo.setAgent(false);
        HtmlInfo html = htmlInfo("DATA");
        if (data.getCrawler_cookie() != null)
            html.setCookie(data.getCrawler_cookie());
        try {
            if (url != null && !url.equals("")) {
                Systemconfig.sysLog.log("downloading: [" + data.getTitle() + "]\t[" + data.getUrl() + "]");
                html.setOrignUrl(url);

                http.getContent(html);
                // html.setContent();
                if (html.getContent() == null) {
                    return;
                }
                // 解析数据
                xpath.templateContentPage(data, html);
                Systemconfig.sysLog.log("wait 2 sec to parse public account info...");
                TimeUtil.rest(2);
                ((WeixinSearchXpathExtractor) xpath).parseGongzhong(data, html, html.getContent());
                int wait = siteinfo.getDownInterval() + (int) Math.random() * 30;
                Systemconfig.sysLog.log("wait " + wait + " sec to parse public account info...");
                TimeUtil.rest(wait);
                Systemconfig.dbService.saveData(data);

            }
            // if(data.getSameUrl()!=null && count != null && data.getId()>0) {
            // //采集链接
            // SearchKey searchKey = new SearchKey();
            // searchKey.setKey(data.getSameUrl());
            // searchKey.setId(data.getId());
            // searchKey.setSite(siteFlag);
            // TimeUtil.rest(siteinfo.getDownInterval()-10);
            // new NewsMetaCommonDownload(searchKey).process();
            // }
        } catch (Exception e) {
            Systemconfig.sysLog.log("采集出现异常" + url, e);
        } finally {
            if (count != null)
                count.countDown();
        }
    }

}
