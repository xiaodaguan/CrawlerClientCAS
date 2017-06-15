package common.download.weibo;

import common.pojos.HtmlInfo;
import common.pojos.WeiboData;
import common.download.GenericMetaCommonDownload;
import common.extractor.xpath.XpathExtractor;
import common.extractor.xpath.weibo.monitor.WeiboMonitorXpathExtractor;
import common.rmi.packet.SearchKey;
import common.pojos.CollectDataType;
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
                    LOGGER.error("err while downloading: " + nexturl);
                    LOGGER.error("wb url " + wbUrl);
                    break;
                }
            }
            
            for (WeiboData videoData:alllist) {
            	LOGGER.info("\n\nWeibo：");
                LOGGER.info("getAuthor:      \t"+videoData.getAuthor());
                LOGGER.info("getCategoryCode:\t"+videoData.getCategoryCode());
                LOGGER.info("getCommentUrl：     \t"+videoData.getCommentUrl());
                LOGGER.info("getCompleteSize:\t"+videoData.getCompleteSize());
                LOGGER.info("getContent:     \t"+videoData.getContent());
                LOGGER.info("getId:          \t"+videoData.getId());
                LOGGER.info("getMd5:         \t"+videoData.getMd5());
                LOGGER.info("getSearchKey:   \t"+videoData.getSearchKey());
                LOGGER.info("getSiteId:      \t"+videoData.getSiteId());
                LOGGER.info("getTitle:       \t"+videoData.getTitle());
                LOGGER.info("getUrl:         \t"+videoData.getUrl());
                LOGGER.info("getInserttime:  \t"+videoData.getInserttime());
                LOGGER.info("getPubdate:     \t"+videoData.getPubdate().toLocaleString());
                LOGGER.info("\n\n");
			}
            
            Systemconfig.dbService.saveCommentDatas(alllist);
            LOGGER.info("微博: " + wbUrl + " 评论保存完成.");
        } catch (IOException e) {
            e.printStackTrace();
            LOGGER.error("err while parsing comments:" + nexturl);
            LOGGER.error("webo url: " + wbUrl);
        } finally {
            alllist.clear();
            list.clear();
        }
    }

}
