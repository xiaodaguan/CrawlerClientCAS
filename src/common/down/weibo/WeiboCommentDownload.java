package common.down.weibo;

import common.bean.HtmlInfo;
import common.bean.WeiboData;
import common.down.GenericMetaCommonDownload;
import common.extractor.xpath.XpathExtractor;
import common.extractor.xpath.weibo.monitor.WeiboMonitorXpathExtractor;
import common.rmi.packet.SearchKey;
import common.siteinfo.CollectDataType;
import common.siteinfo.Siteinfo;
import common.system.Systemconfig;
import common.system.UserAttr;
import common.util.TimeUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 微博评论下载数据
 *
 * @author grs
 */
public class WeiboCommentDownload extends GenericMetaCommonDownload<WeiboData> {
    private UserAttr user;
    private int id;

    public WeiboCommentDownload(SearchKey key, int id, UserAttr user) {
        super(key);
        this.id = id;
        this.user = user;
    }

    @Override
    public void process() {
        List<WeiboData> alllist = new ArrayList<WeiboData>();
        List<WeiboData> list = new ArrayList<WeiboData>();
        Siteinfo siteinfo = Systemconfig.allSiteinfos.get(siteFlag);
        String url = getRealUrl(siteinfo, gloaburl);
        String wbUrl = url;
        String nexturl = url;
        HtmlInfo html = htmlInfo(CollectDataType.COMM.name());
        try {
            while (nexturl != null && !nexturl.equals("")) {
                list.clear();

                html.setOrignUrl(nexturl);

                try {
                    http.getContent(html, user);
                    if(html.getContent()==null||html.getContent().equals(""))
                    {
//                        nexturl="http://m.weibo.cn/"+1435160552/3945741208568191/rcMod?format=cards&type=comment&hot=1
                    }


                    nexturl = ((WeiboMonitorXpathExtractor) ((XpathExtractor) xpath)).templateComment(list, html, 0, id + "", nexturl);

                    if (list.size() == 0) {
                        Systemconfig.sysLog.log(url + "数据页面解析为空！！");
                        break;
                    }
                    Systemconfig.sysLog.log(url + " 评论页面解析完成。");

                    Systemconfig.dbService.getNorepeatData(list, "");
                    if (list.size() == 0) break;

                    alllist.addAll(list);

                    url = nexturl;
//                    break;
                    TimeUtil.rest(5);

                } catch (Exception e) {
                    e.printStackTrace();
                    System.err.println("err while downloading: " + nexturl);
                    System.err.println("wb url " + wbUrl);
                    break;
                }
            }
            Systemconfig.dbService.saveCommentDatas(alllist);
            Systemconfig.sysLog.log("微博: " + wbUrl + " 评论保存完成.");
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("err while parsing comments:" + nexturl);
            System.err.println("webo url: " + wbUrl);
        } finally {
            alllist.clear();
            list.clear();
        }
    }

}
