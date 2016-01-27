package common.extractor.xpath.news.monitor.sub;

import java.io.File;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import common.bean.HtmlInfo;
import common.bean.NewsData;
import common.extractor.xpath.XpathExtractor;
import common.extractor.xpath.news.monitor.NewsMonitorExtractorAttribute;
import common.extractor.xpath.news.monitor.NewsMonitorXpathExtractor;
import common.rmi.packet.SearchKey;
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
public class MocMonitorExtractor extends NewsMonitorXpathExtractor implements NewsMonitorExtractorAttribute {

	@Override
	public void processList(List<NewsData> list, Node domtree, Map<String, Component> comp, String... args) {
		this.parseTitle(list, domtree, comp.get("title"));

		if (list.size() == 0)
			return;

		this.parseUrl(list, domtree, comp.get("url"), args[2]);
	}

	@Override
	public void processPage(NewsData data, Node domtree, Map<String, Component> comp, String... args) {
		if (data.getTitle() == null) {
			this.parsePageTitle(data, domtree, comp.get("pageTitle"));
		}

		this.parseContent(data, domtree, comp.get("content"));
		this.parseAuthor(data, domtree, comp.get("author"));
		this.parsePubtime(data, domtree, comp.get("originalPubtime"));
		this.parseSource(data, domtree, comp.get("originalSource"));
		this.parseImgUrl(data, domtree, comp.get("imgUrl"));
	}

	@Override
	public String parseNext(Node dom, Component component, String... args) {
		// http://www.moc.gov.cn/zhuzhan/jiaotongxinwen/xinwenredian/
		// http://www.moc.gov.cn/zhuzhan/jiaotongxinwen/xinwenredian/index_1.html
		String currUrl = args[0];
		int currPage = currUrl.contains("index_") ? Integer.parseInt(currUrl.substring(currUrl.lastIndexOf("index_") + 6,
				currUrl.lastIndexOf(".html"))) : 0;

		String nextUrl = "";
		if (!currUrl.contains("index_")) {
			nextUrl = currUrl + "index_1.html";
		} else {
			nextUrl = currUrl.substring(0, currUrl.lastIndexOf("/index")) + "/index_" + (currPage + 1) + ".html";
		}

		return nextUrl;
	}

	@Override
	public void parseUrl(List<NewsData> list, Node dom, Component component, String... args) {
		if (component == null)
			return;
		NodeList nl = head(component.getXpath(), dom, list.size(), component.getName());
		if (nl == null)
			return;
		for (int i = 0; i < nl.getLength(); i++) {
			String url = nl.item(i).getTextContent();
			if (url.contains("/xxlb"))
				url = "http://www.moc.gov.cn/xinxilb" + url.replace("./", "/");
			else if (url.contains("xinwen"))
				url = "http://www.moc.gov.cn/zhuzhan/jiaotongxinwen/xinwenredian" + url.replace("./", "/");

			list.get(i).setUrl(url);
		}
	}

	@Override
	public void parsePubtime(NewsData data, Node dom, Component component, String... args) {
		String str = "";
		if (component == null)
			return;

		NodeList nl = commonList(component.getXpath(), dom);
		if (nl == null)
			return;

		if (nl.item(0) != null) {
			str = StringUtil.format(nl.item(0).getTextContent());
			while (str.contains("  "))
				str = str.replace("  ", " ");

			str = str.split(" ")[str.split(" ").length - 1];
			str = str.replace("年", "-").replace("月", "-").replace("日", "-");
			data.setPubtime(str);
			data.setPubdate(timeProcess(data.getPubtime().trim()));
		}

	}

	@Override
	public void parseSource(NewsData data, Node dom, Component component, String... args) {
		String str = "";
		if (component == null)
			return;

		NodeList nl = commonList(component.getXpath(), dom);
		if (nl == null)
			return;

		if (nl.item(0) != null) {
			str = StringUtil.format(nl.item(0).getTextContent());
			while (str.contains("  "))
				str = str.replace("  ", " ");
			str = str.split(" ")[0];
		}

		data.setSource(str);
	}

	@Override
	public void parseAuthor(NewsData data, Node dom, Component component, String... args) {
		String str = "";
		if (component == null)
			return;

		NodeList nl = commonList(component.getXpath(), dom);
		if (nl == null)
			return;

		if (nl.item(0) != null) {
			str = StringUtil.format(nl.item(0).getTextContent());
			while (str.contains("  "))
				str = str.replace("  ", " ");
			if (str.split(" ").length > 2)
				str = str.split(" ")[1];
			else {
				str = null;
			}
		}

		data.setAuthor(str);
	}
}
