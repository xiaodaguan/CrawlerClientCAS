package common.download.bbs;

import java.util.concurrent.CountDownLatch;

import common.bean.BBSData;
import common.bean.HtmlInfo;
import common.bean.ReplyData;
import common.download.GenericDataCommonDownload;
import common.rmi.packet.SearchKey;
import common.system.Systemconfig;

/**
 * 下载详细页面
 *
 * @author grs
 */
public class BBSDataCommonDownload extends GenericDataCommonDownload<BBSData> {

    public BBSDataCommonDownload(String siteFlag, BBSData vd, CountDownLatch endCount, SearchKey key) {
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

                Systemconfig.sysLog.log(data.getUrl() + "下载完成。。。");
                // html.setContent(StringUtil.getContent("E:/grs/开源工具/crawler(_client)_0.8.1/filedown/DATA/tieba_bbs_search/dcb64a74de7c2a750f5e5cfcf0d20697.htm",
                // siteinfo.getCharset()));
                if (html.getContent() == null || (html.getContent().contains("抱歉，您访问的贴子被隐藏") && html.getContent().contains("贴吧404"))) {
                    return;
                }
                // 解析数据
                url = xpath.templateContentPage(data, html);
       
                BBSData bbsData = data;
                System.out.println("\n\n一条论坛信息：");
                System.out.println("searchKey: "+bbsData.getSearchKey());
                System.out.println("title:     "+bbsData.getTitle());
                System.out.println("url:       "+bbsData.getUrl());
                System.out.println("pubtime:   "+bbsData.getPubtime());
                //System.out.println("pubdate:   "+bbsData.getPubdate().toLocaleString());
                System.out.println("content:   "+bbsData.getContent());
                System.out.println("column:    "+bbsData.getColumn());
                System.out.println("replyCount:"+bbsData.getReplyCount());
                System.out.println("clickCount:"+bbsData.getClickCount());
                System.out.println("imgUrl:    "+bbsData.getImgUrl());
                System.out.println("replyList: "+bbsData.getReplyList());
                for ( ReplyData relay : bbsData.getReplyList()) {
                	System.out.println("replyList name   : "+relay.getName());
                	System.out.println("replyList time   : "+relay.getTime());
                	System.out.println("replyList content: "+relay.getContent());
                	System.out.println();
                }
                System.out.println("\n\n");
                
                
                Systemconfig.sysLog.log(data.getTitle() + "解析完成。。。");
                if (dataCheck(data, html.getContent())) {
                    //Systemconfig.dbService.saveData(data);
                    Systemconfig.sysLog.log(data.getTitle() + "保存完成。。。");
                    synchronized (key) {
                        key.savedCountIncrease();
                    }
                } else {

                    Systemconfig.sysLog.log("缺失标题或pubtime，放弃保存。" + data.getUrl());
                }
            }
        } catch (Exception e) {
            Systemconfig.sysLog.log("采集出现异常" + data.getUrl(), e);
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
    private boolean dataCheck(BBSData data, String html) {
        if (data.getTitle() != null && data.getPubdate() != null) {
            return true;
        }
        if (html.contains("<title>贴吧404</title>")){
            System.err.println("帖子已被删除");
            return false;
        }
        return false;
    }
}
