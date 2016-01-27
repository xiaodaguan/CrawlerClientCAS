package common.extractor.xpath.conference.monitor;

import java.io.File;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import common.bean.HtmlInfo;
import common.bean.BBSData;
import common.bean.ConferenceData;
import common.extractor.xpath.XpathExtractor;
import common.siteinfo.CommonComponent;
import common.siteinfo.Component;
import common.siteinfo.Siteinfo;
import common.system.Systemconfig;
import common.util.ExtractResult;
import common.util.MD5Util;
import common.util.StringUtil;

/**
 * 抽取实现类
 * 
 * @author grs
 */
public class ConferenceMonitorXpathExtractor extends XpathExtractor<ConferenceData> implements ConferenceMonitorExtractorAttribute {
	@Override public void processList(List<ConferenceData> list, Node domtree, Map<String, Component> comp, String... args) {
		this.parseTitle(list, domtree, comp.get("title"));

		if (list.size() == 0)
			return;

		this.parseUrl(list, domtree, comp.get("url"));

		this.parseBrief(list, domtree, comp.get("brief"));
		this.parseSource(list, domtree, comp.get("source"));
		this.parsePubtime(list, domtree, comp.get("pubtime"));
		this.parseAuthor(list, domtree, comp.get("author"));
		this.parsePlace(list, domtree, comp.get("place"));
	}

	@Override public void processPage(ConferenceData data, Node domtree, Map<String, Component> comp, String... args) {
		this.parseContent(data, domtree, comp.get("content"), args);
	}

	@Override public void parseContent(ConferenceData data, Node domtree, Component component, String... args) {
		NodeList nl = commonList(component.getXpath(), domtree);
		if (nl == null)
			return;
		String content = "";
		for (int i = 0; i < nl.getLength(); i++) {
			content += nl.item(i).getTextContent() + "\n";
		}
		data.setContent(StringUtil.format(content) != "" ? StringUtil.format(content) : "\n");
		if (data.getBrief() == null)
			data.setBrief(content);
	}

	@Override public void parseUrl(List<ConferenceData> list, Node dom, Component component, String... args) {
		if (component == null)
			return;
		NodeList nl = head(component.getXpath(), dom, list.size(), component.getName());
		if (nl == null)
			return;
		for (int i = 0; i < nl.getLength(); i++) {
			list.get(i).setUrl(urlProcess(component, nl.item(i)));
		}
	}

	@Override public void parseTitle(List<ConferenceData> list, Node dom, Component component, String... args) {
		if (component == null)
			return;
		NodeList nl = commonList(component.getXpath(), dom);
		if (nl == null)
			return;
		for (int i = 0; i < nl.getLength(); i++) {
			ConferenceData vd = new ConferenceData();
			vd.setTitle(StringUtil.format(nl.item(i).getTextContent()));
			list.add(vd);
		}
	}

	@Override public String parseNext(Node dom, Component component, String... args) {
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
	@Override public void parseBrief(List<ConferenceData> list, Node dom, Component component, String... args) {
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
	 * 来源
	 * 
	 * @param list
	 * @param dom
	 * @param component
	 * @param strings
	 */
	@Override public void parseSource(List<ConferenceData> list, Node dom, Component component, String... strings) {
		if (component == null)
			return;
		NodeList nl = head(component.getXpath(), dom, list.size(), component.getName());
		if (nl == null)
			return;
		for (int i = 0; i < nl.getLength(); i++) {
			list.get(i).setSource(nl.item(i).getTextContent());
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
	@Override public void parsePubtime(List<ConferenceData> list, Node dom, Component component, String... args) {
		if (component == null)
			return;
		NodeList nl = head(component.getXpath(), dom, list.size(), component.getName());
		if (nl == null)
			return;
		for (int i = 0; i < nl.getLength(); i++) {
			
			list.get(i).setPubtime(nl.item(i).getTextContent());
			list.get(i).setPubdate(timeProcess(list.get(i).getPubtime().trim()));
		}
	}

	@Override public void parseAuthor(List<ConferenceData> list, Node dom, Component component, String... content) {
		if (component == null)
			return;
		NodeList nl = head(component.getXpath(), dom, list.size(), component.getName());
		if (nl == null)
			return;
		for (int i = 0; i < nl.getLength(); i++) {
			list.get(i).setBlogAuthor(nl.item(i).getTextContent());
		}
	}

	@Override public void parsePlace(List<ConferenceData> list, Node dom, Component component, String... content) {
		if (component == null)
			return;
		NodeList nl = head(component.getXpath(), dom, list.size(), component.getName());
		if (nl == null)
			return;
		for (int i = 0; i < nl.getLength(); i++) {
			list.get(i).setPlace(nl.item(i).getTextContent());
		}
	}

}
