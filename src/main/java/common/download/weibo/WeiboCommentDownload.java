package common.download.weibo;

import common.bean.HtmlInfo;
import common.bean.WeiboData;
import common.download.GenericMetaCommonDownload;
import common.extractor.xpath.XpathExtractor;
import common.extractor.xpath.weibo.monitor.WeiboMonitorXpathExtractor;
import common.rmi.packet.SearchKey;
import common.bean.CollectDataType;
import common.siteinfo.Siteinfo;
import common.system.Systemconfig;
import common.system.UserAttribute;
import common.util.TimeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 微博评论下载数据
 *
 * @author grs
 */
public class WeiboCommentDownload extends GenericMetaCommonDownload<WeiboData> {
    private static final Logger LOGGER = LoggerFactory.getLogger(WeiboCommentDownload.class);

    private UserAttribute user;
    private int id;

    public WeiboCommentDownload(SearchKey key, int id, UserAttribute user) {
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
                    	break;
//                        nexturl="http://m.weibo.cn/"+1435160552/3945741208568191/rcMod?format=cards&type=comment&hot=1
                    }


                    nexturl = ((WeiboMonitorXpathExtractor) ((XpathExtractor) xpath)).templateComment(list, html, 0, id + "", nexturl);

                    if (list.size() == 0) {
                        LOGGER.info(url + "数据页面解析为空！！");
                        break;
                    }
                    LOGGER.info(url + " 评论页面解析完成。");

                    Systemconfig.dbService.filterDuplication(list);
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
            
            for (WeiboData videoData:alllist) {
            	System.out.println("\n\nWeibo：");	
                System.out.println("getAuthor:      \t"+videoData.getAuthor());
                System.out.println("getCategoryCode:\t"+videoData.getCategoryCode());
                System.out.println("getCommentUrl：     \t"+videoData.getCommentUrl());
                System.out.println("getCompleteSize:\t"+videoData.getCompleteSize());
                System.out.println("getContent:     \t"+videoData.getContent());
                System.out.println("getId:          \t"+videoData.getId());
                System.out.println("getMd5:         \t"+videoData.getMd5());
                System.out.println("getSearchKey:   \t"+videoData.getSearchKey());
                System.out.println("getSiteId:      \t"+videoData.getSiteId());
                System.out.println("getTitle:       \t"+videoData.getTitle());
                System.out.println("getUrl:         \t"+videoData.getUrl());
                System.out.println("getInserttime:  \t"+videoData.getInserttime());
                System.out.println("getPubdate:     \t"+videoData.getPubdate().toLocaleString());	
                System.out.println("\n\n");	
			}
            
            Systemconfig.dbService.saveCommentDatas(alllist);
            LOGGER.info("微博: " + wbUrl + " 评论保存完成.");
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
