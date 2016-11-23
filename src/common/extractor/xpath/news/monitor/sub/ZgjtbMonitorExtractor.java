package common.extractor.xpath.news.monitor.sub;

import java.io.File;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import common.bean.HtmlInfo;
import common.bean.p;
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
public class ZgjtbMonitorExtractor extends NewsMonitorXpathExtractor implements NewsMonitorExtractorAttribute {

	@Override
	public void processList(List<p> list, Node domtree, Map<String, Component> comp, String... args) {
		this.parseTitle(list, domtree, comp.get("title"));

		if (list.size() == 0)
			return;

		this.parseUrl(list, domtree, comp.get("url"), args[2]);
	}

	@Override
	public void processPage(p data, Node domtree, Map<String, Component> comp, String... args) {
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
		int count = StringUtil.countStr(currUrl, "_");
		int currPage = count > 1 ? Integer.parseInt(currUrl.substring(currUrl.lastIndexOf("_") + 1, currUrl.lastIndexOf(".htm"))) : 1;

		String nextUrl = "";
		if (currPage == 1) {
			nextUrl = currUrl.replace(".htm", "_" + (currPage + 1) + ".htm");
		} else {
			nextUrl = currUrl.replace("_" + currPage + ".htm", "_" + (currPage + 1) + ".htm");
		}

		return nextUrl;
	}

	@Override
	public void parseUrl(List<p> list, Node dom, Component component, String... args) {
		if (component == null)
			return;
		NodeList nl = head(component.getXpath(), dom, list.size(), component.getName());
		if (nl == null)
			return;
		for (int i = 0; i < nl.getLength(); i++) {
			String url = nl.item(i).getTextContent();
			if (args[0].contains("zhitong"))
				url = "http://www.zgjtb.com/zhitong/" + url;
			else
				url = "http://www.zgjtb.com/" + url;

			list.get(i).setUrl(url);
		}
	}

	@Override
	public void parsePubtime(p data, Node dom, Component component, String... args) {
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

			str = str.split(" ")[0].replace("时间：", "");
			str = str.replace("年", "-").replace("月", "-").replace("日", "-");
			data.setPubtime(str);
			data.setPubdate(timeProcess(data.getPubtime().trim()));
		}

	}

	@Override
	public void parseSource(p data, Node dom, Component component, String... args) {
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
			str = str.split(" ")[1].replace("来源：", "");
		}

		data.setSource(str);
	}

	@Override
	public void parseAuthor(p data, Node dom, Component component, String... args) {
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
			
				str = str.split(" ")[2].replace("作者：", "");
			
		}

		data.setAuthor(str);
	}
}
