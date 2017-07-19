package common.extractor.xpath.weixin.monitor;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import common.pojos.HtmlInfo;
import common.pojos.WeixinData;
import common.extractor.xpath.XpathExtractor;
import common.http.SimpleHttpProcess;
import common.siteinfo.CommonComponent;
import common.siteinfo.Component;
import common.siteinfo.Siteinfo;
import common.system.Systemconfig;
import common.utils.MD5Util;
import common.utils.StringUtil;
import common.utils.TimeUtil;

/**
 * 抽取实现类
 * 
 * @author gxd
 */
public class WeixinMonitorXpathExtractor extends XpathExtractor<WeixinData> implements WeixinMonitorExtractorAttribute {
	private static final Logger LOGGER = LoggerFactory.getLogger(WeixinMonitorXpathExtractor.class);

	@Override
	public void processPage(WeixinData data, Node domtree, Map<String, Component> comp, String... args) {
		this.parseSource(data, domtree, comp.get("source"));
		this.parsePubtime(data, domtree, comp.get("pubtime"));
		this.parseAuthor(data, domtree, comp.get("author"));
		this.parseContent(data, domtree, comp.get("content"));
		this.parseImgUrl(data, domtree, comp.get("imgs_url"));
//		TimeUtil.rest(10);
//		this.parseNumber(data, domtree, comp.get(""));
	}

	public void parseNumber(WeixinData data, Node dom, Component component, String... args) {
		// http://mp.weixin.qq.com/s?__biz=MjM5ODE1NTMxMQ==&mid=201653867&idx=1&sn=6f3445a3640eb09ce7cfa5a49509f165&3rd=MzA3MDU4NTYzMw==&scene=6#rd

		String biz = "";
		String mid = "";
		String uin = "";
		String key = "";
		String fromFile = StringUtil.getContent("config/WeixinKey/WeixinKey.txt");
		try {
			biz = StringUtil.regMatcher(data.getUrl(), "__biz=", "&");
			mid = StringUtil.regMatcher(data.getUrl(), "mid=", "&");
			for (String string : fromFile.split("&")) {
				if (string.contains("uin"))
					uin = string.split("=")[1].trim();
				if (string.contains("key"))
					key = string.split("=")[1].trim();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		String url = "http://mp.weixin.qq.com" + "/mp/getappmsgext?" + "__biz=" + biz + "&mid=" + mid + "&uin=" + uin
				+ "&key=" + key
				// +
				// "&pass_ticket=b3hV91xTLYZxRGKemRNz%2FAi4VKElPnwHYUNtoV8w4dE%3D"

				+ "";

		HtmlInfo html = new HtmlInfo();

		String charSet = "UTF-8";
		html.setCrawlerType("DATA");
		html.setEncode(charSet);
		html.setOrignUrl(url);
		html.setCookie("Set-Cookie: wxuin=20156425; Path=/; Expires=Fri, 02-Jan-1970 00:00:00 GMT");
		html.setUa("Mozilla/5.0 (iPhone; CPU iPhone OS 8_0 like Mac OS X) AppleWebKit/600.1.3 (KHTML, like Gecko) Version/8.0 Mobile/12A4345d Safari/600.1.4");
		SimpleHttpProcess shp = new SimpleHttpProcess();
		shp.getContent(html);
		String content = html.getContent();

		int retry = 0;
		while (!content.contains("read_num")) {
			if (retry++ > 3)
				break;
			LOGGER.info("请获取key后输入任意内容回车继续...输入c忽略(很可能无法继续采集，不推荐)");
			LOGGER.error("请获取key后输入任意内容回车继续...输入c忽略(很可能无法继续采集，不推荐)");
			Scanner input = new Scanner(System.in);
			String s = input.next();
			if (s.equals("c") || s.equals("C"))
				break;

			fromFile = StringUtil.getContent("config/WeixinKey/WeixinKey.txt");
			try {
				for (String string : fromFile.split("&")) {
					if (string.contains("uin"))
						uin = string.split("=")[1].trim();
					if (string.contains("key"))
						key = string.split("=")[1].trim();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			url = "http://mp.weixin.qq.com" + "/mp/getappmsgext?" + "__biz=" + biz + "&mid=" + mid + "&uin=" + uin
					+ "&key=" + key;
			html = new HtmlInfo();

			charSet = "UTF-8";
			html.setCrawlerType("DATA");
			html.setEncode(charSet);
			html.setOrignUrl(url);
			html.setCookie("Set-Cookie: wxuin=20156425; Path=/; Expires=Fri, 02-Jan-1970 00:00:00 GMT");
			html.setUa("Mozilla/5.0 (iPhone; CPU iPhone OS 8_0 like Mac OS X) AppleWebKit/600.1.3 (KHTML, like Gecko) Version/8.0 Mobile/12A4345d Safari/600.1.4");
			shp = new SimpleHttpProcess();
			shp.getContent(html);
			content = html.getContent();
		}

		String readNumStr = StringUtil.regMatcher(content, "\"read_num\":", ",");
		String praiseNumStr = StringUtil.regMatcher(content, "\"like_num\":", ",");

		try {
			if (readNumStr != null)
				data.setReadNum(Integer.parseInt(readNumStr));

			if (praiseNumStr != null)
				data.setPraiseNum(Integer.parseInt(praiseNumStr));

		} catch (Exception e) {
			e.printStackTrace();

		}
	}

	@Override
	public void parseImgUrl(WeixinData data, Node dom, Component component, String... args) {
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
	
	@Override
	public String templateListPage(List<WeixinData> list, HtmlInfo html, int page, String... keyword) throws SAXException, IOException {
		list.clear();
		
		
		/**
		 * keyword
		 * 0: search_keyword
		 * 1: search_url(list)
		 * 2: ...
		 * 3: cookies
		 */
		
		String cookie=keyword[3];
		
		Siteinfo siteinfo = Systemconfig.allSiteinfos.get(html.getSite());
		Node domtree = getRealDOM(html);
		if (domtree == null) {
			LOGGER.info("DOM解析为NULL！！");
			return null;
		}
		CommonComponent comp = getRealComp(siteinfo, html.getCrawlerType()
				.substring(0, html.getCrawlerType().indexOf(File.separator)));// 得到元数据的配置组件
		
		
		processList(list, domtree, comp.getComponents(),
				args(html.getContent(), cookie, String.valueOf(siteinfo.getSiteFlag()), keyword));
		if (list.size() == 0)
			return null;
		attrSet(list, siteinfo.getSiteFlag(), keyword[0], Integer.parseInt(keyword[2]));
		return parseNext(domtree, comp.getComponents().get("next"), new String[] { keyword[1], page + "" });
	}

	@Override
	public void processList(List<WeixinData> list, Node domtree, Map<String, Component> comp, String... args) {
		String content = args[0];
		String cookie = args[1];
		this.parseTitle(list, domtree, comp.get("title"), content);

		if (list.size() == 0)
			return;

		this.parseUrl(list, domtree, comp.get("url"), content, cookie);

		this.parseBrief(list, domtree, comp.get("brief"), content);

	}
	/**
	 * 0: content 1: siteflag ...
	 * 
	 * @param content
	 * @param siteflag
	 * @param keyword
	 * @return
	 */
	private String[] args(String content, String cookie, String siteflag, String... keyword) {
		String arr[] = new String[keyword.length + 1];
		arr[0] = content;
		arr[1] = cookie;
		arr[2] = siteflag;
		for (int i = 3; i < keyword.length; i++) {
			arr[i] = keyword[i - 3];
		}
		return arr;
	}

	@Override
	public void parseUrl(List<WeixinData> list, Node dom, Component component, String... args) {
		if (args[0] == null || args[0] == ""||args[1] == null || args[1] == "")
			return;
		String cookie = args[1];
//		String referer = args[1];
		
		List<String> results = StringUtil.regMatches(args[0], "<url>", "/url", true);
		for (int i = 0; i < results.size(); i++) {

			
		
			String tmpUrl =  results.get(i);
			tmpUrl="http://weixin.sogou.com" +tmpUrl.substring(tmpUrl.indexOf("CDATA[")+6,tmpUrl.lastIndexOf("]]>"));
			

			String loc = null;
			try {
				HttpURLConnection conn = (HttpURLConnection) new URL(tmpUrl).openConnection();
				conn.addRequestProperty("User-Agent",
						"Mozilla/5.0 (Windows NT 6.1; rv:38.0) Gecko/20100101 Firefox/38.0");
				conn.setRequestProperty("Cookie", cookie);
//				conn.setRequestProperty("Referer", referer);
				HttpURLConnection.setFollowRedirects(false);
				conn.setFollowRedirects(false);
				conn.connect();
				loc = conn.getHeaderField("Location");
				if (loc != null)
					LOGGER.info(conn.getResponseMessage());

				LOGGER.info("real url: " + loc);
				int sleepTime = 30 + (int) (Math.random() * 20);
				LOGGER.info("sleep..." + sleepTime);
				TimeUtil.rest(sleepTime);

			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			list.get(i).setUrl(loc == null ? "err." : loc);
		}
	}

	@Override
	public void parseTitle(List<WeixinData> list, Node dom, Component component, String... args) {
		if (args[0] == null || args[0] == "")
			return;
		List<String> results = StringUtil.regMatches(args[0], "title>", "/title", true);
		for (int i = 0; i < results.size(); i++) {

			String tmp = results.get(i);
			String result = StringUtil.regMatcher(tmp, "CDATA\\[", "\\]");
			WeixinData vd = new WeixinData();
			vd.setTitle(result);
			list.add(vd);
		}
	}

	@Override
	public String parseNext(Node dom, Component component, String... args) {
		String currUrl = args[0];
		String currPageStr = currUrl.substring(currUrl.lastIndexOf("page=") + 5);

		int currPageInt = currPageStr != null ? Integer.parseInt(currPageStr) : -1;

		if (currPageInt == -1)
			return null;
		int nextPageInt = currPageInt + 1;
		String nextUrl = currUrl.replace("page=" + currPageStr, "page=" + nextPageInt);

		return nextUrl;
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
	public void parseBrief(List<WeixinData> list, Node dom, Component component, String... args) {
		if (args[0] == null || args[0] == "")
			return;
		List<String> results = StringUtil.regMatches(args[0], "content168>", "/content168", true);
		for (int i = 0; i < results.size(); i++) {

			String tmp = results.get(i);
			String result = StringUtil.regMatcher(tmp, "CDATA\\[", "\\]");
			list.get(i).setBrief(result);
		}
	}


    @Override
    public void parseAuthor(WeixinData data, Node dom, Component component, String... args) {
        if (component == null)
            return;
        NodeList nl = commonList(component.getXpath(), dom);
        if (nl == null)
            return;
        if (nl.item(0) != null)
            data.setAuthor(StringUtil.format(nl.item(0).getTextContent()));
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
	public void parseSource(List<WeixinData> list, Node dom, Component component, String... strings) {
		if (component == null)
			return;
		NodeList nl = head(component.getXpath(), dom, list.size(), component.getName());
		if (nl == null)
			return;
		for (int i = 0; i < nl.getLength(); i++) {
			list.get(i).setSource(StringUtil.format(nl.item(i).getTextContent()));
		}
	}

	@Override
	public void parsePubtime(WeixinData data, Node dom, Component component, String... args) {
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
	public void parseSource(WeixinData data, Node dom, Component component, String... strings) {
		if (component == null)
			return;
		NodeList nl = commonList(component.getXpath(), dom);
		if (nl == null)
			return;
		if (nl.item(0) != null)
			data.setSource(StringUtil.format(nl.item(0).getTextContent()));
	}

	@Override
	public void parseContent(WeixinData data, Node dom, Component component, String... strings) {
		if (component == null)
			return;
		NodeList nl = commonList(component.getXpath(), dom);
		if (nl == null)
			return;
		String str = "";
		for (int i = 0; i < nl.getLength(); i++) {
			if (i < nl.getLength() - 1)
				str += nl.item(i).getTextContent() + "\r\n";
			else
				str += nl.item(0).getTextContent();
		}
		data.setContent(str);
	}

	/**
	 * 共有属性设置
	 * 
	 * @param list
	 * @param siteflag
	 * @param key
	 * @param code
	 */
	@Override
	protected void attrSet(List<WeixinData> list, int siteflag, String key, int code) {
		for (WeixinData data : list) {
			WeixinData cd = (WeixinData) data;
			cd.setSearchKey(key);
			cd.setCategoryCode(code);
			cd.setMd5(MD5Util.MD5(cd.getUrl()) + Systemconfig.crawlerType);
			cd.setSiteId(siteflag);
		}
	}

}
