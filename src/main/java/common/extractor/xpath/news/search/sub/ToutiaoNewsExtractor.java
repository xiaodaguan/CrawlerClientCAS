package common.extractor.xpath.news.search.sub;

import common.bean.NewsData;
import common.bean.HtmlInfo;

import common.extractor.xpath.news.search.NewsSearchXpathExtractor;

import common.siteinfo.CommonComponent;

import common.siteinfo.Siteinfo;
import common.system.Systemconfig;
import common.util.MD5Util;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ToutiaoNewsExtractor extends NewsSearchXpathExtractor {

	@Override
	public String templateListPage(List<NewsData> list, HtmlInfo html, int page, String... keyword)
			throws SAXException, IOException {
		Siteinfo siteinfo = Systemconfig.allSiteinfos.get(html.getSite());

		JSONArray jarray = JSONObject.fromObject(html.getContent()).getJSONArray("data");
		for (Object obj : jarray) {
			JSONObject jobj = (JSONObject) obj;
			// title
			NewsData data = new NewsData();

			if (jobj.containsKey("title")) {
				String title = jobj.getString("title");
				data.setTitle(title);
			} else {
				continue;
			}
			if (jobj.containsKey("display_url")) {
				String s = jobj.getString("display_url");
				data.setUrl(s);
			}
			if (jobj.containsKey("abstract")) {
				String brief = jobj.getString("abstract");
				data.setBrief(brief);
			}
			if (jobj.containsKey("source")) {
				String source = jobj.getString("source");
				data.setSource(source);
			}
			if (jobj.containsKey("datetime")) {
				String pubtime = jobj.getString("datetime");
				data.setPubtime(pubtime);
				data.setPubdate(timeProcess(pubtime));
			}
			if (data.getTitle() != null)
				list.add(data);
		}
		if (list.size() == 0)
			return null;
		attrSet(list, siteinfo.getSiteFlag(), keyword[0], Integer.parseInt(keyword[2]));
		return null;
	}

	@Override
	public String templateContentPage(NewsData data, HtmlInfo html, int page, String... keyword)
			throws IOException, SAXException {
			Siteinfo siteinfo = Systemconfig.allSiteinfos.get(html.getSite());

			Node domtree = getRealDOM(html);
			if (domtree == null) {
				Systemconfig.sysLog.log("DOM解析为NULL！！");
				return null;
			}
			CommonComponent comp = getRealComp(siteinfo,
					html.getType().substring(0, html.getType().indexOf(File.separator)));// 得到元数据的配置组件

			if (html.getOrignUrl().contains("http://toutiao.com")) {
				try {
					this.parseContent(data, domtree, comp.getComponents().get("content"), html.getContent());


					this.parseImgUrl(data, domtree, comp.getComponents().get("img_url").getXpath(), html.getContent());
				}catch (Exception e){
					e.printStackTrace();
				}

			} else {
				if (html.getOrignUrl().contains("m.yfcnn.com")) {
					// SimpleHttpProcess http = new SimpleHttpProcess();
					// String encode = html.getEncode();
					// html.setEncode("gbk");
					// http.getContent(html);
					// html.setEncode("encode");
					// domtree = getRealDOM(html);
					String xpathContent = "//DIV[@class='art_co sau']//P";
					this.parseContent(data, domtree, xpathContent, html.getContent());
					String xpathImg = "//DIV[@class='art_co sau']//P/IMG/@src";
					this.parseImgUrl(data, domtree, xpathImg, html.getContent());
				} else {
					String xpathContent = "DIV[contains(@class,'content')]//P|"
							+ "//ARTICLE//P|//DIV[contains(@id,'ext')]//P|" + "//FIGURE/FIGCAPTION";
					String xpathImg = "DIV[contains(@class,'content')]//P//IMG/@*|"
							+ "//ARTICLE//P//IMG/@*|//DIV[contains(@id,'ext')]//P//IMG/@*|" + "//FIGURE//IMG/@*";
					this.parseContent(data, domtree, xpathContent, html.getContent());
					this.parseImgUrl(data, domtree, xpathImg, html.getContent());
				}
			}
			data.setInserttime(new Date());
			data.setSiteId(siteinfo.getSiteFlag());
			if (data.getMd5() == null)
				data.setMd5(MD5Util.MD5(data.getUrl()));
			return null;
	}

	public void parseContent(NewsData data, Node dom, String xpath, String... args) {
		NodeList nl = commonList(xpath, dom);
		if (nl == null)
			return;
		String content = "";
		for (int i = 0; i < nl.getLength(); i++) {
			String line = nl.item(i).getTextContent();
			if (line.length() < 2)
				continue;
			content += line + "\n";
		}
		data.setContent(content);
	}

	//public void parseImgUrl(NewsData data, Node dom, String xpath, String... args)
	public void parseImgUrl(NewsData data, Node dom, String xpath, String... args) {
		NodeList nl = commonList(xpath, dom);
		if (nl == null||nl.getLength()==0)
			return;
		String url = "";
		for (int i = 0; i < nl.getLength(); i++) {
			String line = nl.item(i).getTextContent();
			if (line.contains("http") && line.indexOf("http") > -1) {
				url += line + ";";
			}
		}
		data.setImgUrl(url);
	}
	public static String extractOne(String str, String pattern) {
		Pattern p = Pattern.compile(pattern);
		Matcher m = p.matcher(str);
		StringBuffer sb = new StringBuffer();
		while (m.find()) {
			sb.append(m.group().trim());
			break;
		}
		return sb.toString();
	}
}
