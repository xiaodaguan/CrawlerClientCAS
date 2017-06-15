package common.extractor.xpath.conference.search;

import java.util.List;
import java.util.Map;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import common.pojos.ConferenceData;
import common.extractor.xpath.XpathExtractor;
import common.siteinfo.Component;
import common.util.StringUtil;

/**
 * 抽取实现类
 * 
 * @author grs
 */
public class ConferenceSearchXpathExtractor extends XpathExtractor<ConferenceData> implements ConferenceSearchExtractorAttribute {

	@Override public void processPage(ConferenceData data, Node domtree, Map<String, Component> comp, String... args) {

		this.parseContent(data, domtree, comp.get("content"));
		this.parseImgUrl(data, domtree, comp.get("imgs_url"));
		this.parseAuthor(data, domtree, comp.get("author"));

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
		NodeList nl = head(component.getXpath(), dom);
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

	public void parseAuthor(ConferenceData data, Node dom, Component component, String... args) {
		if (component == null)
			return;
		NodeList nl = commonList(component.getXpath(), dom);
		if (nl == null)
			return;
		if (nl.item(0) != null)
			data.setBlogAuthor(StringUtil.format(nl.item(0).getTextContent()));
	}

	private void parseContent(ConferenceData data, Node domtree, Component component, String... strings) {
		if (component == null)
			return;
		NodeList nl = commonList(component.getXpath(), domtree);
		if (nl == null)
			return;
		String str = "";// 正文文本
		for (int i = 0; i < nl.getLength(); i++) {
			str += nl.item(i).getTextContent() + "\r\n";
		}
		data.setContent(str);
	}

	public void parseImgUrl(ConferenceData data, Node dom, Component component, String... args) {
		if (component == null)
			return;
		NodeList nl = commonList(component.getXpath(), dom);
		if (nl == null)
			return;
		String imgs = "";
		for (int i = 0; i < nl.getLength(); i++) {
			imgs += StringUtil.format(nl.item(i).getTextContent()) + ";";
		}
		data.setImgUrl(imgs);
	}

	@Override public void processList(List<ConferenceData> list, Node domtree, Map<String, Component> comp, String... args) {
		this.parseTitle(list, domtree, comp.get("title"));

		if (list.size() == 0)
			return;

		this.parseUrl(list, domtree, comp.get("url"));

		this.parseBrief(list, domtree, comp.get("brief"));
		this.parseSource(list, domtree, comp.get("source"));
		this.parsePubtime(list, domtree, comp.get("pubtime"));
		this.parseAuthor(list, domtree, comp.get("author"));
	}

	@Override public void parseSource(List<ConferenceData> list, Node dom, Component component, String... args) {
		// TODO Auto-generated method stub

	}

	@Override public void parseAuthor(List<ConferenceData> list, Node domtree, Component component, String... content) {
		// TODO Auto-generated method stub

	}

}
