package common.extractor.xpath.news.monitor;

import java.io.File;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import common.bean.CommonData;
import common.bean.HtmlInfo;
import common.bean.p;
import common.extractor.xpath.XpathExtractor;
import common.siteinfo.CommonComponent;
import common.siteinfo.Component;
import common.siteinfo.Siteinfo;
import common.system.Systemconfig;
import common.util.MD5Util;
import common.util.StringUtil;

/**
 * 抽取实现类
 * 
 * @author grs
 */
public class NewsMonitorXpathExtractor extends XpathExtractor<p> implements NewsMonitorExtractorAttribute {

	@Override
	public void processPage(p data, Node domtree, Map<String, Component> comp, String... args) {
		if (data.getTitle() == null) {
			this.parsePageTitle(data, domtree, comp.get("pageTitle"));
		}
		
		this.parseContent(data, domtree, comp.get("content"));

		// this.parsePubtime(data, domtree, comp.get("pubtime"));
		this.parseSource(data, domtree, comp.get("originalSource"));
		 this.parseImgUrl(data, domtree, comp.get("imgUrl"));
	}

	@Override
	public void processList(List<p> list, Node domtree, Map<String, Component> comp, String... args) {
		this.parseTitle(list, domtree, comp.get("title"));

		if (list.size() == 0)
			return;

		this.parseUrl(list, domtree, comp.get("url"), args[2]);
		this.parsePubtime1(list, domtree, comp.get("pubtime"));
		this.parseBrief(list, domtree, comp.get("brief"));
	}

	@Override
	public void parseUrl(List<p> list, Node dom, Component component, String... args) {
		if (component == null)
			return;
		NodeList nl = head(component.getXpath(), dom, list.size(), component.getName());
		if (nl == null)
			return;
		for (int i = 0; i < nl.getLength(); i++) {
			list.get(i).setUrl(urlProcess(component, nl.item(i)));
		}
	}

	public void parsePubtime1(List<p> list, Node dom, Component component, String... args) {
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

	/**
	 * 摘要
	 * 
	 * @param list
	 * @param dom
	 * @param component
	 * @param args
	 */
	public void parseBrief(List<p> list, Node dom, Component component, String... args) {
		if (component == null)
			return;
		NodeList nl = head(component.getXpath(), dom, list.size(), component.getName());
		if (nl == null)
			return;
		for (int i = 0; i < nl.getLength(); i++) {
			String str = nl.item(i).getTextContent();
			list.get(i).setBrief(str);
		}
	}

	@Override
	public void parseTitle(List<p> list, Node dom, Component component, String... args) {
		if (component == null)
			return;
		NodeList nl = head(component.getXpath(), dom);
		for (int i = 0; i < nl.getLength(); i++) {
			p vd = new p();
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
	 * 正文
	 * 
	 * @param list
	 * @param dom
	 * @param component
	 * @param strings
	 */
	@Override
	public void parseContent(p data, Node dom, Component component, String... args) {
		if (component == null)
			return;
		NodeList nl = commonList(component.getXpath(), dom);
		if (nl == null)
			return;
		
		String url = "";
		for (int i = 0; i < nl.getLength(); i++) {
			url += nl.item(i).getTextContent();
		}
		data.setContent(url);
	}

	/**
	 * 来源
	 * 
	 * @param list
	 * @param dom
	 * @param component
	 * @param strings
	 */
	@Override
	public void parseSource(p data, Node dom, Component component, String... args) {
		String str = "";
		if (component == null)
			return;

		NodeList nl = commonList(component.getXpath(), dom);
		if (nl == null)
			return;

		if (nl.item(0) != null)
			str = StringUtil.format(nl.item(0).getTextContent());

		data.setSource(str);
	}

	/**
	 * 作者
	 * 
	 * @param list
	 * @param dom
	 * @param component
	 * @param strings
	 */
	@Override
	public void parseAuthor(p data, Node dom, Component component, String... args) {
		String str = "";
		if (component == null)
			return;

		NodeList nl = commonList(component.getXpath(), dom);
		if (nl == null)
			return;

		if (nl.item(0) != null)
			str = StringUtil.format(nl.item(0).getTextContent());

		data.setSource(str);
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
	public void parsePubtime(p data, Node dom, Component component, String... args) {
		if (component == null)
			return;
		NodeList nl = commonList(component.getXpath(), dom);
		if (nl == null)
			return;
		if (nl.item(0) != null) {
			data.setPubtime(nl.item(0).getTextContent());
			data.setPubdate(timeProcess(data.getPubtime().trim()));
		}
	}

	@Override
	public void parseImgUrl(p data, Node dom, Component component, String... args) {
		if (component == null)
			return;
		NodeList nl = commonList(component.getXpath(), dom);
		if (nl == null)
			return;
		String url = "";
		for (int i = 0; i < nl.getLength(); i++){
			String tmp=nl.item(i).getTextContent();
			if(!tmp.startsWith("http:")){
				if(data.getUrl().contains("tirechina"))
					tmp="http://www.tirechina.net"+tmp;
			}
			url += tmp + ";";
		}

		data.setImgUrl(url);
	}

	@Override
	public void parsePageTitle(p data, Node dom, Component component, String... args) {
		if (component == null)
			return;
		NodeList nl = commonList(component.getXpath(), dom);
		if (nl == null)
			return;
		if (nl.item(0) != null)
			data.setTitle(nl.item(0).getTextContent());
	}

	@Override
	protected void attrSet(List<p> list, int siteflag, String key, int code) {
		for (p cd : list) {
			cd.setSearchKey(key);
			cd.setCategoryCode(code);
			cd.setMd5(MD5Util.MD5(cd.getUrl() + Systemconfig.crawlerType));
			cd.setSiteId(siteflag);
		}
	}

}
