package common.download.bbs;

import common.bean.BBSData;
import common.bean.HtmlInfo;
import common.download.GenericDataCommonDownload;
import common.rmi.packet.SearchKey;
import common.system.Systemconfig;
import common.system.UserAttribute;
import common.system.UserManager;

import java.util.concurrent.CountDownLatch;

/**
 * 下载详细页面
 *
 * @author grs
 */
public class BBSDataMonitorDownload extends GenericDataCommonDownload<BBSData> {

    public BBSDataMonitorDownload(String siteFlag, BBSData vd, CountDownLatch endCount, SearchKey key) {
        super(siteFlag, vd, endCount, key);
    }

    @Override
    public void process() {
        UserAttribute ua = null;

        if (siteinfo.getLogin())

        {
            ua = UserManager.getUser(siteFlag);
            UserManager.releaseUser(siteFlag, ua);
        }
        String url = getRealUrl(data);
        if (siteFlag.startsWith("tieba")) {
            // if(!url.contains("pid")) return;
        }
        if (url == null) return;
        HtmlInfo html = htmlInfo("DATA");
        try {
            LOGGER.info("当前url：" + url);
            if (url != null && !url.equals("")) {
                html.setOrignUrl(url);
                http.getContent(html, ua);
                // html.setContent(StringUtil.getContent("E:/grs/开源工具/crawler(_client)_0.8.1/filedown/DATA/tieba_bbs_search/dcb64a74de7c2a750f5e5cfcf0d20697.htm",
                // siteinfo.getCharset()));
                if (html.getContent() == null) {
                    return;
                }
                // 解析数据
                url = xpath.templateContentPage(data, html);

                LOGGER.info(data.getTitle() + "解析完成。。。");
                Systemconfig.dbService.saveData(data);
                LOGGER.info(data.getTitle() + "保存完成。。。");
            }
        } catch (Exception e) {
            LOGGER.info("采集出现异常" + url, e);
        } finally {
            if (count != null) count.countDown();
        }
    }

}
