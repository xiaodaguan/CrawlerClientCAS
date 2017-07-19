package common.extractor.xpath.bbs.monitor.sub;

import common.pojos.BbsData;
import common.pojos.HtmlInfo;
import common.pojos.ReplyData;
import common.extractor.xpath.bbs.monitor.BbsMonitorXpathExtractor;
import common.siteinfo.CommonComponent;
import common.siteinfo.Component;
import common.siteinfo.Siteinfo;
import common.system.Systemconfig;
import common.utils.MD5Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class BandaoQdExtractor extends BbsMonitorXpathExtractor {
    private static final Logger LOGGER = LoggerFactory.getLogger(BandaoQdExtractor.class);

    @Override
    public void processList(List<BbsData> list, Node domtree, Map<String, Component> components, String... args) {
        this.parseTitle(list, domtree, components.get("title"));

        if (list.size() == 0) return;

        this.parseUrl(list, domtree, components.get("url"));
        this.parseAuthor(list, domtree, components.get("author_l"));
        this.parseReplyCount(list, domtree, components.get("reply_count_l"));
        this.parseClickCount(list, domtree, components.get("click_count_l"));
        this.parsePubtime(list, domtree, components.get("pubtime_l"));
    }

    @Override
    public String templateContentPage(BbsData data, HtmlInfo html, int page, String... keyword) throws SAXException, IOException {
        Siteinfo siteinfo = Systemconfig.allSiteinfos.get(html.getSite());
//		create(content);
        Node domtree = getRealDOM(html);
        if (domtree == null) {
            LOGGER.info("DOM解析为NULL！！");
            return null;
        }
        CommonComponent comp = getRealComp(siteinfo, html.getCrawlerType().substring(0, html.getCrawlerType().indexOf(File.separator)));//得到元数据的配置组件
        if (page == 1) {
            this.parseContent(data, domtree, comp.getComponents().get("content"), html.getContent());
//			this.parseBrief(data, domtree, comp.getComponents().get("brief"), html.getContent());
//            this.parseSource(data, domtree, comp.getComponents().get("source"), html.getContent());
//            this.parseAuthor(data, domtree, comp.getComponents().get("author"), html.getContent());
//            this.parsePubtime(data, domtree, comp.getComponents().get("pubtime"), html.getContent());
//            this.parseClickCount(data, domtree, comp.getComponents().get("click_count"), new String[]{html.getContent()});
//            this.parseReplyCount(data, domtree, comp.getComponents().get("reply_count"), new String[]{html.getContent()});
//            this.parseColumn(data, domtree, comp.getComponents().get("column"), new String[]{html.getContent()});
//            this.parseImgUrl(data, domtree, comp.getComponents().get("img_url"), new String[]{html.getContent()});
            if (data.getPubdate() == null && data.getPubtime() != null) data.setPubdate(timeProcess(data.getPubtime().trim()));

            data.setInserttime(new Date());
            data.setSiteId(siteinfo.getSiteFlag());
            if (data.getMd5() == null) data.setMd5(MD5Util.MD5(data.getUrl()));

            if (data.getTitle() == null) {
                this.parsePageTitle(data, domtree, comp.getComponents().get("pageTitle"), html.getContent());
            }
        }

        //回复列表
        List<ReplyData> list = new ArrayList<ReplyData>();
        this.parseReplyname(list, domtree, comp.getComponents().get("reply_name"), new String[]{html.getContent()});

        if (list.size() > 0) {
            this.parseReplytime(list, domtree, comp.getComponents().get("reply_time"), new String[]{html.getContent()});
            this.parseReplycontent(list, domtree, comp.getComponents().get("reply_content"), new String[]{html.getContent()});
        }

        data.setReplyList(list);

        String next = this.parseReplyNext(domtree, comp.getComponents().get("reply_next"));
        domtree = null;
        return next;
    }

    public void parsePubtime(List<BbsData> list, Node dom, Component component, String... args) {
        NodeList nl = head(component.getXpath(), dom, list.size(), component.getName());
        if (nl == null) return;
        for (int i = 0; i < nl.getLength(); i++) {
            list.get(i).setPubtime(nl.item(i).getTextContent());
            list.get(i).setPubdate(timeProcess(list.get(i).getPubtime()));
        }
    }

    public void parseClickCount(List<BbsData> list, Node dom, Component component, String... args) {
        NodeList nl = head(component.getXpath(), dom, list.size(), component.getName());
        if (nl == null) return;
        for (int i = 0; i < nl.getLength(); i++) {
            list.get(i).setClickCount(Integer.parseInt(nl.item(i).getTextContent()));
        }
    }

    public void parseReplyCount(List<BbsData> list, Node dom, Component component, String... args) {
        NodeList nl = head(component.getXpath(), dom, list.size(), component.getName());
        if (nl == null) return;
        for (int i = 0; i < nl.getLength(); i++) {
            list.get(i).setReplyCount(Integer.parseInt(nl.item(i).getTextContent()));
        }
    }

    public void parseAuthor(List<BbsData> list, Node dom, Component component, String... args) {
        NodeList nl = head(component.getXpath(), dom, list.size(), component.getName());
        if (nl == null) return;
        for (int i = 0; i < nl.getLength(); i++) {
            list.get(i).setAuthor(nl.item(i).getTextContent());
        }
    }

}
