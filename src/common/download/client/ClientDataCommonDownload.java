package common.download.client;

import java.util.concurrent.CountDownLatch;

import common.bean.ClientData;
import common.bean.HtmlInfo;
import common.bean.ReplyData;
import common.download.GenericDataCommonDownload;
import common.rmi.packet.SearchKey;
import common.system.Systemconfig;

/**
 * 下载详细页面
 *
 * @author rzy
 */
public class ClientDataCommonDownload extends GenericDataCommonDownload<ClientData> {

    public ClientDataCommonDownload(String siteFlag, ClientData vd, CountDownLatch endCount, SearchKey key) {
        super(siteFlag, vd, endCount, key);
    }

    @Override
    public void process() {
        String url = getRealUrl(data);
        if (url == null) return;
        HtmlInfo html = htmlInfo("DATA");
        try {
            if (url != null && !url.equals("")) {
                html.setOrignUrl(url);
                http.getContent(html);

                Systemconfig.sysLog.log(data.getUrl() + "下载完成。。。");
                // html.setContent(StringUtil.getContent("E:/grs/开源工具/crawler(_client)_0.8.1/filedown/DATA/tieba_bbs_search/dcb64a74de7c2a750f5e5cfcf0d20697.htm",
                // siteinfo.getCharset()));
                if (html.getContent() == null ) {
                    return;
                }
                // 解析数据                       
                url = xpath.templateContentPage(data, html);
                Systemconfig.sysLog.log(data.getTitle() + "解析完成。。。");
                
                Systemconfig.dbService.saveData(data);
                Systemconfig.sysLog.log(data.getTitle() + "保存完成。。。");
                synchronized (key) {
                    key.savedCountIncrease();
                }
                
            }
        } catch (Exception e) {
            Systemconfig.sysLog.log("采集出现异常" + data.getUrl(), e);
        } finally {
            if (count != null) count.countDown();
        }
    }

}
