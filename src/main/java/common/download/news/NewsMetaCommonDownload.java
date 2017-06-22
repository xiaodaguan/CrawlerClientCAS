package common.download.news;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import common.http.SimpleHttpProcess;
import common.pojos.HtmlInfo;
import common.pojos.NewsData;
import common.download.DataThreadControl;
import common.download.GenericMetaCommonDownload;
import common.rmi.packet.SearchKey;
import common.system.Systemconfig;
import common.utils.TimeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

/**
 * 下载元数据
 *
 * @author grs
 */
public class NewsMetaCommonDownload extends GenericMetaCommonDownload<NewsData> {
    private static final Logger LOGGER = LoggerFactory.getLogger(NewsMetaCommonDownload.class);

    public NewsMetaCommonDownload(SearchKey key) {
        super(key);
    }

    @Override
    public void process() {

		/* 状态 */

        List<NewsData> alllist = new ArrayList<NewsData>();
        List<NewsData> list = new ArrayList<NewsData>();
        String url = gloaburl;
        int page = getRealPage(siteinfo);
        String keyword = key.getKEYWORD();
        map.put(keyword, 1);
        String nexturl = url;
        DataThreadControl dtc = new DataThreadControl(siteFlag, keyword);
        HtmlInfo html = htmlInfo("META");
        int totalCount = 0;
        while (nexturl != null && !nexturl.equals("")) {
            list.clear();

            html.setOrignUrl(nexturl);
            try {

                if (nexturl.contains("163.com"))
                    html.setAcceptEncoding("Accept-Encoding: gzip, deflate");
                http.getContent(html);
            } catch (Exception e) {
                LOGGER.error("列表页下载异常", e);
//				throw new RuntimeException(e);
            }

            try {
                // html.setContent(common.utils.StringUtil.getContent("filedown/META/baidu_news_search/6f962c1b7d205db4faf80453362b648e.htm"));
                nexturl = xpath.templateListPage(list, html, map.get(keyword), keyword, nexturl);

            } catch (SAXException e) {

                LOGGER.error("列表页解析异常", e);
//				throw new RuntimeException(e);
            } catch (IOException e) {

                LOGGER.error("列表页解析异常", e);
//				throw new RuntimeException(e);
            }

            if (list.size() == 0) {
                LOGGER.info("关键词：[" + key.getKEYWORD() + "] " + url + "元数据页面解析为空！！");
                TimeUtil.rest(siteinfo.getDownInterval());
                break;
            }
            LOGGER.info("关键词：[" + key.getKEYWORD() + "] " + url + "元数据页面解析完成。");
            totalCount += list.size();
            try {
                Systemconfig.urlFilter.filterDuplication(list);
            } catch (Exception e) {

                LOGGER.error("列表页去重异常", e);
                throw new RuntimeException(e);
            }
            if (list.size() == 0) {
                LOGGER.info("关键词：[" + key.getKEYWORD() + "] " + url + " no new data.");
                if (alllist.size() == 0)
                    TimeUtil.rest(siteinfo.getDownInterval());
                break;
            }
            dtc.process(list, siteinfo.getDownInterval() - 5, null, key);
            alllist.addAll(list);

            map.put(keyword, map.get(keyword) + 1);
            if (map.get(keyword) > page)
                break;
            url = nexturl;
            if (nexturl != null)
                TimeUtil.rest(siteinfo.getDownInterval());


        }
        // //ID大于0表示有相同新闻需要采集
        // if(key.getSITE_ID() != null) {
        // for(int i = 0;i< alllist.size();i++) {
        // list.get(i).setId(key.getSITE_ID());
        // new NewsSameDataCommonDownload(siteFlag,list.get(i), null).run();
        // }
        // } else {
        // dtc.process(alllist, siteinfo.getDownInterval()-5);
        // }

        if (alllist.size() == 0) {
            return;
        }


        LOGGER.info("关键词：[" + key.getKEYWORD() + "] 列表页检索完成，不重复数据：" + alllist.size() + "条。");
    }

    @Override
    protected void specialHtmlInfo(HtmlInfo html) {
        if (html.getSite().equals("baidu_news_search")) {
            html.setUa(SimpleHttpProcess.getRandomUserAgent());
        }

    }

}
