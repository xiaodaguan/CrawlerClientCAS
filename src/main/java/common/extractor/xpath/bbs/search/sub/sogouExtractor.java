package common.extractor.xpath.bbs.search.sub;

import common.pojos.BBSData;
import common.pojos.HtmlInfo;
import common.pojos.ReplyData;
import common.extractor.xpath.bbs.search.BbsSearchXpathExtractor;
import common.siteinfo.CommonComponent;
import common.siteinfo.Component;
import common.siteinfo.Siteinfo;
import common.system.Systemconfig;
import common.utils.ExtractResult;
import common.utils.MD5Util;
import common.utils.StringUtil;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class sogouExtractor extends BbsSearchXpathExtractor {
	private static final Logger LOGGER = LoggerFactory.getLogger(sogouExtractor.class);

	public  String delHTMLTag(String htmlStr) {
		final String regEx_script = "<script[^>]*?>[\\s\\S]*?<\\/script>"; // 定义script的正则表达式
		final String regEx_style = "<style[^>]*?>[\\s\\S]*?<\\/style>"; // 定义style的正则表达式
		final String regEx_html = "<[^>]+>"; // 定义HTML标签的正则表达式
		final String regEx_space = "\\s*|\t|\r|\n";// 定义空格回车换行符

		Pattern p_script = Pattern.compile(regEx_script, Pattern.CASE_INSENSITIVE);
		Matcher m_script = p_script.matcher(htmlStr);
		htmlStr = m_script.replaceAll(""); // 过滤script标签

		Pattern p_style = Pattern.compile(regEx_style, Pattern.CASE_INSENSITIVE);
		Matcher m_style = p_style.matcher(htmlStr);
		htmlStr = m_style.replaceAll(""); // 过滤style标签

		Pattern p_html = Pattern.compile(regEx_html, Pattern.CASE_INSENSITIVE);
		Matcher m_html = p_html.matcher(htmlStr);
		htmlStr = m_html.replaceAll(""); // 过滤html标签

		Pattern p_space = Pattern.compile(regEx_space, Pattern.CASE_INSENSITIVE);
		Matcher m_space = p_space.matcher(htmlStr);
		htmlStr = m_space.replaceAll("").trim(); // 过滤空格回车标签
		htmlStr = htmlStr.replaceAll("&nbsp;", "");
		htmlStr = htmlStr.substring(0, htmlStr.indexOf("。") + 1);
		return htmlStr;
	}

	@Override
	public void parseReplytime(List<ReplyData> list, Node dom, Component component, String... strings) {
		NodeList nl = commonList(component.getXpath(), dom);
		if (nl == null)
			return;
		judge(list.size(), nl.getLength(), "replytime");
		for (int i = 0; i < nl.getLength(); i++) {
			JSONObject jo = JSONObject.fromObject(nl.item(i).getTextContent());
			jo = jo.getJSONObject("content");
			list.get(i).setTime(jo.getString("date"));
			list.get(i).setPubdate(timeProcess(jo.getString("date")));
		}
	}

	@Override
	public void parseReplyCount(BBSData data, Node domtree, Component component, String... ags) {
		NodeList nl = commonList(component.getXpath(), domtree);
		if (nl == null)
			return;
		if (nl.item(0) != null) {
			String time = StringUtil.extractMulti(nl.item(0).getTextContent().split("回复")[0], "\\d");
			if (time == null || time.equals(""))
				data.setReplyCount(0);
			else
				data.setReplyCount(Integer.parseInt(time));
		}
	}

	@Override
	public void parsePubtime(BBSData data, Node dom, Component component, String... args) {
		// NodeList nl = commonList(component.getXpath(), dom);
		// if(nl==null) return;
		// if(nl.item(0)!=null) {
		// String time = nl.item(0).getTextContent().replace("年",
		// "-").replace("月", "").replace("日", "");
		// time = StringUtil.extractMulti(time,"\\d+-\\d+-\\d+.\\d*.\\d*.\\d*");
		// data.setPubtime(time);
		// data.setPubdate(timeProcess(time));
		// }else
		{
			String dateRegex = "(20\\d{2}[-|\\/|年|\\.][0|1|2]?\\d{1}[-|\\/|月|\\.][0|1|2|3]?\\d{1}([日|号])?(\\s+)?"
					+ "([0|1|2]?\\d([点|时])?((:)?\\d{1,2}(分)?((:)?\\d{1,2}(秒)?)?))(\\s)*(PM|AM)?)";

			String time = StringUtil.extractOne(args[0], dateRegex);
			String[] times = time.split("-");
			if (times.length == 3) {
				if (times[0].length() == 2) {
					time = "20" + time;
				}
			}
			data.setPubtime(time);
			data.setPubdate(timeProcess(time));
		}
	}

	@Override
	public String templateContentPage(BBSData data, HtmlInfo html, int page, String... keyword)
			throws SAXException, IOException {
		{
			Siteinfo siteinfo = Systemconfig.allSiteinfos.get(html.getSite());

			// create(content);
			if (html.getContent().contains("抱歉，您访问的贴子被隐藏")) {
				return null;
			}
			Node domtree = getRealDOM(html);
			if (domtree == null) {
				LOGGER.info("DOM解析为NULL！！");
				return null;
			}
			CommonComponent comp = getRealComp(siteinfo,
					html.getType().substring(0, html.getType().indexOf(File.separator)));// 得到元数据的配置组件
			if (page == 1) {
				//this.parseContent(data, domtree, comp.getComponents().get("content"), html.getContent());
				this.parseSource(data, domtree, comp.getComponents().get("source"), html.getContent());
				this.parseAuthor(data, domtree, comp.getComponents().get("author"), html.getContent());
				this.parsePubtime(data, domtree, comp.getComponents().get("pubtime"), html.getContent());
				this.parseClickCount(data, domtree, comp.getComponents().get("click_count"),
						new String[] { html.getContent() });
				this.parseReplyCount(data, domtree, comp.getComponents().get("reply_count"),
						new String[] { html.getContent() });
				this.parseColumn(data, domtree, comp.getComponents().get("column"), new String[] { html.getContent() });
				this.parseImgUrl(data, domtree, comp.getComponents().get("img_url"),
						new String[] { html.getContent() });
				if (data.getPubdate() == null && data.getPubtime() != null)
					data.setPubdate(timeProcess(data.getPubtime().trim()));

				data.setInserttime(new Date());
				data.setSiteId(siteinfo.getSiteFlag());
				if (data.getMd5() == null)
					data.setMd5(MD5Util.MD5(data.getUrl()));

				if (data.getTitle() == null) {
					this.parsePageTitle(data, domtree, comp.getComponents().get("pageTitle"), html.getContent());
				}

				templateContentPage1(data, html, page);
			}

			// 回复列表
			List<ReplyData> list = new ArrayList<ReplyData>();
			this.parseReplyname(list, domtree, comp.getComponents().get("reply_name"),
					new String[] { html.getContent() });

			if (list.size() > 0) {
				this.parseReplytime(list, domtree, comp.getComponents().get("reply_time"),
						new String[] { html.getContent() });
				this.parseReplycontent(list, domtree, comp.getComponents().get("reply_content"),
						new String[] { html.getContent() });
			}

			data.setReplyList(list);

			String next = this.parseReplyNext(domtree, comp.getComponents().get("reply_next"));
			domtree = null;
		}
		if (data.getContent() == null) {
			data.setContent(delHTMLTag(html.getContent()));
		}
		return null;

	}

	public String templateContentPage1(BBSData data, HtmlInfo html, int page, String... keyword) {

		ExtractResult result = null;
		try {
			result = Systemconfig.htmlAutoExtractor.extract(html.getContent(), html.getEncode(), data.getUrl());
		} catch (Exception e) {
			LOGGER.info("出错url：" + html.getOrignUrl());
			e.printStackTrace();
		}
		String title = data.getTitle() == null ? result.getTitle() : data.getTitle();
		// 标题需要处理
		// 搜索词里面带有特殊符号的情况
		boolean spe = false;
		if (data.getSearchKey().indexOf("_") > -1 || data.getSearchKey().indexOf("-") > -1) {
			spe = true;
			title = title.replace(data.getSearchKey(), "{temp}");
		}
		// 其他情况
		if (title.indexOf("_") > -1) {
			title = title.split("_")[0].trim();
		}
		// if(title.indexOf("-") > -1) {
		// title = title.split("-")[0].trim();
		// }
		if (spe)
			title = title.replace("{temp}", data.getSearchKey());
		data.setTitle(title);
		data.setContent(result.getContent());
		data.setImgUrl(result.getImgs());
		// data.setInserttime(new Timestamp(System.currentTimeMillis()));
		return null;
	}

	@Override
	public void parseContent(BBSData data, Node dom, Component component, String... args) {
	}

	@Override
	public void parseAuthor(BBSData data, Node dom, Component component, String... args) {

	}

	@Override
	public void parseClickCount(BBSData data, Node domtree, Component component, String... ags) {

	}

	@Override
	public void parseSource(BBSData data, Node domtree, Component component, String... args) {

	}

	@Override
	public void parseColumn(BBSData data, Node domtree, Component component, String... ags) {

	}

	@Override
	public void parseImgUrl(BBSData data, Node domtree, Component component, String... args) {

	}

	@Override
	public void parsePageTitle(BBSData data, Node domtree, Component component, String... args) {

	}

	@Override
	public void parseReplyname(List<ReplyData> list, Node domtree, Component component, String... strings) {

	}

	@Override
	public void parseReplycontent(List<ReplyData> list, Node dom, Component component, String... strings) {

	}

	@Override
	public String parseReplyNext(Node domtree, Component component) {

		return null;
	}

	@Override
	public void processPage(BBSData data, Node domtree, Map<String, Component> map, String... args) {
	}

}
