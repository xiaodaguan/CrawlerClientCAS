package common.extractor.xpath.company.monitor;

import common.bean.CompanyData;
import common.extractor.xpath.XpathExtractor;
import common.siteinfo.Component;
import common.util.StringUtil;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.List;
import java.util.Map;

/**
 * 简单抽取类，默认抽取实现
 *
 * @author grs
 */
public class CompanyMonitorXpathExtractor extends XpathExtractor<CompanyData> implements CompanyMonitorExtractorAttribute {

    @Override
    public void processPage(CompanyData data, Node domtree, Map<String, Component> comp, String... args) {
        this.parseFounDate(data, domtree, comp.get("found_date"));
        this.parseWebsite(data, domtree, comp.get("website"));

        this.parseContact(data, domtree, comp.get("contact"));
        this.parseBrief(data, domtree, comp.get("brief"));
        this.parseBriefProducts(data, domtree, comp.get("brief_products"));
        this.parseFundingExperience(data, domtree, comp.get("funding_experience"));
    }

    @Override
    public void processList(List<CompanyData> list, Node domtree, Map<String, Component> comp, String... args) {
        this.parseTitle(list, domtree, comp.get("title"));
        if (list.size() == 0) return;
        this.parseUrl(list, domtree, comp.get("url"));
        this.parseIco(list, domtree, comp.get("ico"));
        this.parseAddress(list, domtree, comp.get("address"));
        this.parseField(list, domtree, comp.get("field"));
        this.parseProducts(list, domtree, comp.get("products"));
    }

    public void parseFundingExperience(CompanyData cd, Node dom, Component component, String... args) {
        if (component == null) return;
        NodeList nl = commonList(component.getXpath(), dom);
        if (nl == null) return;
        String con = "";
        for (int i = 0; i < nl.getLength(); i++) {
            con += nl.item(i).getTextContent() + "\n";
        }
        cd.setFundingExperience(con);
    }

    public void parseBriefProducts(CompanyData cd, Node dom, Component component, String... args) {
        if (component == null) return;
        NodeList nl = commonList(component.getXpath(), dom);
        if (nl == null) return;
        String con = "";
        for (int i = 0; i < nl.getLength(); i++) {
            con += nl.item(i).getTextContent() + "\n";
        }
        cd.setBriefProducts(con);
    }

    public void parseBrief(CompanyData cd, Node dom, Component component, String... args) {
        if (component == null) return;
        NodeList nl = commonList(component.getXpath(), dom);
        if (nl == null) return;
        String con = "";
        for (int i = 0; i < nl.getLength(); i++) {
            con += nl.item(i).getTextContent() + "\n";
        }
        cd.setBrief(con);
    }

    public void parseContact(CompanyData cd, Node dom, Component component, String... args) {
        if (component == null) return;
        NodeList nl = commonList(component.getXpath(), dom);
        if (nl == null) return;
        String con = "";
        for (int i = 0; i < nl.getLength(); i++) {
            con += nl.item(i).getTextContent() + "\n";
        }
        cd.setContact(con);
    }

    public void parseWebsite(CompanyData cd, Node dom, Component component, String... args) {
        if (component == null) return;
        NodeList nl = commonList(component.getXpath(), dom);
        if (nl == null) return;
        String con = "";
        for (int i = 0; i < nl.getLength(); i++) {
            con += nl.item(i).getTextContent() + "\n";
        }
        cd.setWebsite(con);
    }


    public void parseFounDate(CompanyData cd, Node dom, Component component, String... args) {
        if (component == null) return;
        NodeList nl = commonList(component.getXpath(), dom);
        if (nl == null) return;
        String con = "";
        for (int i = 0; i < nl.getLength(); i++) {
            con += nl.item(i).getTextContent() + "\n";
        }
        cd.setFoundDate(con);
    }

    public void parseProducts(List<CompanyData> list, Node dom, Component component, String... args) {
        if (component == null) return;
        NodeList nl = head(component.getXpath(), dom, list.size(), component.getName());
        if (nl == null) return;
        for (int i = 0; i < nl.getLength(); i++) {
            list.get(i).setProducts(nl.item(i).getTextContent());
        }

    }

    public void parseField(List<CompanyData> list, Node dom, Component component, String... args) {
        if (component == null) return;
        NodeList nl = head(component.getXpath(), dom, list.size(), component.getName());
        if (nl == null) return;
        for (int i = 0; i < nl.getLength(); i++) {
            list.get(i).setField(nl.item(i).getTextContent());
        }

    }

    public void parseIco(List<CompanyData> list, Node dom, Component component, String... args) {
        if (component == null) return;
        NodeList nl = head(component.getXpath(), dom, list.size(), component.getName());
        if (nl == null) return;
        for (int i = 0; i < nl.getLength(); i++) {
            list.get(i).setIco(nl.item(i).getTextContent());
        }
    }

    public void parseAddress(List<CompanyData> list, Node dom, Component component, String... args) {
        if (component == null) return;
        NodeList nl = head(component.getXpath(), dom, list.size(), component.getName());
        if (nl == null) return;
        for (int i = 0; i < nl.getLength(); i++) {
            list.get(i).setAddress(nl.item(i).getTextContent());
        }
    }

    @Override
    public void parseUrl(List<CompanyData> list, Node dom, Component component, String... args) {
        if (component == null) return;
        NodeList nl = head(component.getXpath(), dom, list.size(), component.getName());
        if (nl == null) return;
        for (int i = 0; i < nl.getLength(); i++) {
            list.get(i).setUrl(urlProcess(component, nl.item(i)));
        }
    }

    @Override
    public void parseTitle(List<CompanyData> list, Node dom, Component component, String... args) {
        if (component == null) return;
        NodeList nl = head(component.getXpath(), dom);
        if (nl == null) return;
        for (int i = 0; i < nl.getLength(); i++) {
            CompanyData cd = new CompanyData();
            cd.setTitle(StringUtil.format(nl.item(i).getTextContent()));
            cd.setName(cd.getTitle());
            list.add(cd);
        }
    }

    @Override
    public void parseContent(CompanyData cd, Node dom, Component component, String... args) {
        if (component == null) return;
        NodeList nl = commonList(component.getXpath(), dom);
        if (nl == null) return;
        String con = "";
        for (int i = 0; i < nl.getLength(); i++) {
            con += nl.item(i).getTextContent() + "\n";
        }
        cd.setContent(con);
    }

    @Override
    public void parsePubtime(List<CompanyData> list, Node dom, Component component, String... args) {
        if (component == null) return;
        NodeList nl = head(component.getXpath(), dom, list.size(), component.getName());
        if (nl == null) return;
        for (int i = 0; i < nl.getLength(); i++) {
            list.get(i).setPubdate(timeProcess(nl.item(i).getTextContent()));
        }
    }

    @Override
    public void parsePubtime(CompanyData cd, Node dom, Component component, String... args) {
        if (component == null) return;
        NodeList nl = commonList(component.getXpath(), dom);
        if (nl == null) return;
        if (nl.item(0) != null) cd.setPubdate(timeProcess(nl.item(0).getTextContent()));
    }

}
