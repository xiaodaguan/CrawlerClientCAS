package common.extractor.xpath.frgmedia.search;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import common.pojos.FrgmediaData;
import common.pojos.HtmlInfo;
import common.extractor.xpath.XpathExtractor;
import common.siteinfo.CommonComponent;
import common.siteinfo.Component;
import common.siteinfo.Siteinfo;
import common.system.Systemconfig;
import common.utils.StringUtil;

/**
 * 抽取实现类
 *
 * @author rzy
 */
public class FrgmediaSearchXpathExtractor extends XpathExtractor<FrgmediaData>
        implements FrgmediaSearchExtractorAttribute {
    private static final Logger LOGGER = LoggerFactory.getLogger(FrgmediaSearchXpathExtractor.class);

    @Override // templateContentPage
    public String templateContentPage(FrgmediaData data, HtmlInfo html, int page, String... keyword) {

        Siteinfo siteinfo = Systemconfig.allSiteinfos.get(html.getSite());
        // create(content);
        Node domtree = null;
        try {
            domtree = getRealDOM(html);
        } catch (SAXException | IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if (domtree == null) {
            LOGGER.info("DOM解析为NULL！！");
            return null;
        }
        CommonComponent comp = getRealComp(siteinfo,
                html.getType().substring(0, html.getType().indexOf(File.separator)));// 得到元数据的配置组件
        if (domtree == null | comp == null)
            return null;
        this.parseContent(data, domtree, comp.getComponents().get("content"), html.getContent());
        this.parseImgUrl(data, domtree, comp.getComponents().get("imgUrl"), html.getContent());
        this.parseSource(data, domtree, comp.getComponents().get("source"), html.getContent());
        this.parseAuthor(data, domtree, comp.getComponents().get("author"), html.getContent());

        return null;
    }

    public void parseAuthor(FrgmediaData data, Node dom, Component component, String... strings) {
        if (component == null)
            return;
        NodeList nl = commonList(component.getXpath(), dom);
        if (nl == null)
            return;
        String str = "";
        for (int i = 0; i < nl.getLength(); i++) {
            str += nl.item(i).getTextContent();
        }
        data.setAuthor(str.trim());
    }

    public void parseContent(FrgmediaData data, Node dom, Component component, String... strings) {
        if (component == null)
            return;
        NodeList nl = commonList(component.getXpath(), dom);
        if (nl == null)
            return;
        String str = "";
        for (int i = 0; i < nl.getLength(); i++) {
            if (nl.item(i).getTextContent().length() > 1) {
                str += nl.item(i).getTextContent().trim() + "\r\n";
            }
        }
        data.setContent(str);
    }

    public void parseImgUrl(FrgmediaData data, Node dom, Component component, String... strings) {
        if (component == null)
            return;
        NodeList nl = commonList(component.getXpath(), dom);
        if (nl == null)
            return;
        String str = "";
        for (int i = 0; i < nl.getLength(); i++) {
            str += nl.item(i).getTextContent() + ";";
        }
        data.setImgUrl(str);
    }

    public void parseSource(FrgmediaData data, Node dom, Component component, String... strings) {
        if (component == null)
            return;
        NodeList nl = commonList(component.getXpath(), dom);
        if (nl == null)
            return;
        String str = "";
        for (int i = 0; i < nl.getLength(); i++) {
            str += nl.item(i).getTextContent().trim();
        }
        data.setSource(str);
    }

    @Override
    public void processList(List<FrgmediaData> list, Node domtree, Map<String, Component> comp, String... args) {
        this.parseTitle(list, domtree, comp.get("title"));

        if (list.size() == 0)
            return;

        this.parseUrl(list, domtree, comp.get("url"));
        this.parseBrief(list, domtree, comp.get("brief"));
        this.parseSource(list, domtree, comp.get("source"));
        this.parsePubtime(list, domtree, comp.get("pubtime"));

    }

    @Override
    public void parseUrl(List<FrgmediaData> list, Node dom, Component component, String... args) {
        if (component == null)
            return;
        NodeList nl = head(component.getXpath(), dom, list.size(), component.getName());
        if (nl == null)
            return;
        for (int i = 0; i < nl.getLength(); i++) {
            list.get(i).setUrl(urlProcess(component, nl.item(i)));
        }
    }

    @Override
    public void parseTitle(List<FrgmediaData> list, Node dom, Component component, String... args) {
        if (component == null)
            return;
        NodeList nl = head(component.getXpath(), dom);
        for (int i = 0; i < nl.getLength(); i++) {
            FrgmediaData vd = new FrgmediaData();
            vd.setTitle(StringUtil.format(nl.item(i).getTextContent()));
            list.add(vd);
        }
    }

    @Override
    public String parseNext(Node dom, Component component, String... args) {
        if (component == null)
            return null;
        NodeList nl = commonList(component.getXpath(), dom);
        if (nl == null)
            return null;
        if (nl.item(0) != null) {
            return urlProcess(component, nl.item(0));
        }
        return null;
    }

    /**
     * 摘要
     *
     * @param list
     * @param dom
     * @param component
     * @param strings
     */
    @Override
    public void parseBrief(List<FrgmediaData> list, Node dom, Component component, String... args) {
        if (component == null)
            return;
        NodeList nl = head(component.getXpath(), dom, list.size(), component.getName());
        if (nl == null)
            return;
        for (int i = 0; i < nl.getLength(); i++) {
            list.get(i).setBrief(nl.item(i).getTextContent());
        }
    }

    /**
     * 发布时间
     *
     * @param list
     * @param dom
     * @param component
     * @param strings
     */
    @Override
    public void parsePubtime(List<FrgmediaData> list, Node dom, Component component, String... args) {
        if (component == null)
            return;
        NodeList nl = head(component.getXpath(), dom, list.size(), component.getName());
        if (nl == null)
            return;
        for (int i = 0; i < nl.getLength(); i++) {
            list.get(i).setPubtime(nl.item(i).getTextContent().trim());
            list.get(i).setPubdate(timeProcess(list.get(i).getPubtime().trim()));
        }
    }

    @Override
    public void parseSamenum(List<FrgmediaData> list, Node dom, Component component, String... args) {
        if (component == null)
            return;
        NodeList nl = head(component.getXpath(), dom, list.size(), component.getName());
        if (nl == null)
            return;
        for (int i = 0; i < nl.getLength(); i++) {
            list.get(i).setSource(nl.item(i).getTextContent());
        }
    }

    @Override
    public void parseSameurl(List<FrgmediaData> list, Node dom, Component component, String... args) {
        if (component == null)
            return;
        NodeList nl = head(component.getXpath(), dom, list.size(), component.getName());
        if (nl == null)
            return;
        for (int i = 0; i < nl.getLength(); i++) {
            list.get(i).setSource(nl.item(i).getTextContent());
        }
    }

    @Override
    public void parseSource(List<FrgmediaData> list, Node dom, Component component, String... args) {
        if (component == null)
            return;
        NodeList nl = head(component.getXpath(), dom, list.size(), component.getName());
        if (nl == null)
            return;
        for (int i = 0; i < nl.getLength(); i++) {
            list.get(i).setSource(nl.item(i).getTextContent());
        }
    }

    @Override
    public void processPage(FrgmediaData data, Node domtree, Map<String, Component> map, String... args) {
        // TODO Auto-generated method stub

    }

}
