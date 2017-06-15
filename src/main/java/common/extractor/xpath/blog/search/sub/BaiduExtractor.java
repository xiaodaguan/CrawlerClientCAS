package common.extractor.xpath.blog.search.sub;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import common.pojos.BlogData;
import common.pojos.HtmlInfo;
import common.extractor.xpath.blog.search.BlogSearchXpathExtractor;
import common.siteinfo.CommonComponent;
import common.siteinfo.Component;
import common.siteinfo.Siteinfo;
import common.system.Systemconfig;
import common.util.StringUtil;

public class BaiduExtractor extends BlogSearchXpathExtractor {
	private static final Logger LOGGER = LoggerFactory.getLogger(BaiduExtractor.class);

	@Override
	public String templateListPage(List<BlogData> list, HtmlInfo html, int page, String... keyword)
			throws SAXException, IOException {
		list.clear();
		/**
		 * keyword 0: search_keyword 1: search_url(list) 2: ... 3: cookies
		 */
		Siteinfo siteinfo = Systemconfig.allSiteinfos.get(html.getSite());
		if (page == 1) {
			Node domtree = getRealDOM(html);
			if (domtree == null) {
				LOGGER.info("DOM解析为NULL！！");
				return null;
			}
			CommonComponent comp = getRealComp(siteinfo,
					html.getType().substring(0, html.getType().indexOf(File.separator)));// 得到元数据的配置组件
			processList(list, domtree, comp.getComponents(),
					args(html.getContent(), String.valueOf(siteinfo.getSiteFlag()), keyword));
			if (list.size() == 0)
				return null;
			attrSet(list, siteinfo.getSiteFlag(), keyword[0], Integer.parseInt(keyword[2]));
			return parseNext(domtree, comp.getComponents().get("next"), new String[] { keyword[1], page + "" });
		}else{

			JSONArray jarray = JSONObject.fromObject(html.getContent())
					.getJSONObject("data").getJSONArray("list");
			for (Object obj : jarray) {
				JSONObject  jobj = (JSONObject)obj;
				String title = jobj.getString("m_title").trim();
				String url = jobj.getString("m_display_url");
				String brief = jobj.getString("m_summary").trim();
				String source = null;
				String pubtime = jobj.getString("m_create_time").trim();
				BlogData vd = new BlogData();
				vd.setTitle(title);
				vd.setUrl(url);
				vd.setBrief(brief);
				vd.setSource(source);
				int year = Calendar.getInstance().get(Calendar.YEAR);
				String time = year + "-" + pubtime;
				vd.setPubtime(time);
				vd.setPubdate(timeProcess(time.trim()));// timeProcess(list.get(i).getPubtime().trim())
				list.add(vd);
				attrSet(list, siteinfo.getSiteFlag(), keyword[0], Integer.parseInt(keyword[2]));
			}	
			return parseNext(null, null, new String[] { keyword[1], page + ""});
		}
	}
	
	public void parseTitle(List<BlogData> list, Node dom, Component component, String... args) {
		if (component == null)
			return;
		NodeList nl = head(component.getXpath(), dom);
		for (int i = 0; i < nl.getLength(); i++) {
			BlogData vd = new BlogData();
			vd.setTitle(StringUtil.format(nl.item(i).getTextContent()));
			list.add(vd);
		}
	}
	
	@Override
	public String parseNext(Node dom, Component component, String... args) {
		//word=%E5%8D%97%E6%B5%B7%E9%97%AE%E9%A2%98%E5%8F%91%E5%B8%83%E4%BC%9A
		String tempUrl="http://baijia.baidu.com/ajax/searcharticle?page=<page>&pagesize=20&word=<keyword>";
		String keyword = args[0].split("word=")[1];
		int page = Integer.parseInt(args[1]);
		String url = tempUrl.replace("<page>", (page+1)+"").replace("<keyword>", keyword);
		return url;
	}
	private String[] args(String content, String siteflag, String... keyword) {
		String arr[] = new String[keyword.length + 1];
		arr[0] = content;
		arr[1] = siteflag;
		for (int i = 2; i < keyword.length; i++) {
			arr[i] = keyword[i - 2];
		}
		return arr;
	}

	@Override
	public void parsePubtime(List<BlogData> list, Node dom, Component component, String... args) {
		if (component == null)
			return;
		NodeList nl = head(component.getXpath(), dom, list.size(), component.getName());
		if (nl == null)
			return;
		for (int i = 0; i < nl.getLength(); i++) {
			int year = Calendar.getInstance().get(Calendar.YEAR);
			String time = year + "-" + nl.item(i).getTextContent();
			list.get(i).setPubtime(time);
			list.get(i).setPubdate(timeProcess(list.get(i).getPubtime().trim()));// timeProcess(list.get(i).getPubtime().trim())
		}
	}
}
