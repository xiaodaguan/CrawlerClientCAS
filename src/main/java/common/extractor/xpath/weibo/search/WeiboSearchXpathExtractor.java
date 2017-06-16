package common.extractor.xpath.weibo.search;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.xml.transform.TransformerException;

import org.apache.xpath.XPathAPI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import common.pojos.HtmlInfo;
import common.pojos.WeiboData;
import common.extractor.xpath.XpathExtractor;
import common.siteinfo.CommonComponent;
import common.siteinfo.Component;
import common.siteinfo.Siteinfo;
import common.system.Systemconfig;
import common.utils.MD5Util;
import common.utils.StringUtil;

/**
 * 微博抽取实现类
 * 
 * @author grs
 */
public class WeiboSearchXpathExtractor extends XpathExtractor<WeiboData> implements WeiboSearchExtractorAttribute {
	private static final Logger LOGGER = LoggerFactory.getLogger(WeiboSearchXpathExtractor.class);

	@Override
	public void processList(List<WeiboData> list, Node domtree, Map<String, Component> comp, String... args) {
		this.parseAuthor(list, domtree, comp.get("author"));

		if (list.size() == 0)
			return;
		this.parseUrl(list, domtree, comp.get("url"));
		this.parseContent(list, domtree, comp.get("content"));
		this.parseAuthorImg(list, domtree, comp.get("author_img"));
		this.parsePubtime(list, domtree, comp.get("pubtime"));
		this.parseAuthorUrl(list, domtree, comp.get("author_url"));
		this.parseImgUrl(list, domtree, comp.get("img_url"));
		this.parseRttNum(list, domtree, comp.get("rtt_num"));
		this.parseCommentNum(list, domtree, comp.get("comment_num"));
		this.parseMid(list, domtree, comp.get("mid"));
		this.parseRttContent(list, domtree, comp.get("rtt_content"));
		this.parseUid(list, domtree, comp.get("uid"));
		this.parseSource(list, domtree, comp.get("source"));
		this.parseCommenturl(list, domtree, null);
		this.parseRtturl(list, domtree, null);
	}

	@Override
	public void processPage(WeiboData data, Node domtree, Map<String, Component> comp, String... args) {
		// this.parseAuthor(data, domtree, comp.get("dauthor"));
		// this.parseContent(data, domtree, comp.get("dcontent"));
		// this.parseAddress(data,domtree,comp.get("daddress"));
		// this.parseAuthorImg(data, domtree, comp.get("dauthor_img"));
		// this.parsePubtime(data, domtree, comp.get("dpubtime"));
		// this.parseAuthorUrl(data, domtree, comp.get("dauthor_url"));
		// this.parseImgUrl(data, domtree, comp.get("dimg_url"));
		// this.parseRttNum(data, domtree, comp.get("drtt_num"));
		// this.parseCommentNum(data, domtree, comp.get("dcomment_num"));
		// this.parseMid(data, domtree, comp.get("dmid"));
		// this.parseRttContent(data, domtree, comp.get("drtt_content"));
		// this.parseUid(data, domtree, comp.get("duid"));
		// this.parseSource(data, domtree, comp.get("dsource"));
		// this.parseCommenturl(data, domtree, null);
		// this.parseRtturl(data, domtree, null);

	}

	/**
	 * 评论数据抽取
	 * 
	 * @param list
	 * @param html
	 * @param page
	 * @param keyword
	 * @return
	 * @throws IOException
	 * @throws SAXException
	 */
	public String templateComment(List<WeiboData> list, HtmlInfo html, int page, String... keyword) throws SAXException, IOException {
		Siteinfo siteinfo = Systemconfig.allSiteinfos.get(html.getSite());
		Node domtree = getRealDOM(html);
		if (domtree == null) {
			LOGGER.info("DOM解析为NULL！！");
			return null;
		}
		CommonComponent comp = getRealComp(siteinfo, html.getType().substring(0, html.getType().indexOf(File.separator)));// 得到数据的配置组件
		parseCommentAuthor(list, domtree, comp.getComponents().get("comment_author"));
		parseCommentAuthorUrl(list, domtree, comp.getComponents().get("comment_author_url"));
		parseCommentAuthorImg(list, domtree, comp.getComponents().get("comment_author_img"));
		parseCommentTime(list, domtree, comp.getComponents().get("comment_time"));
		parseCommentContent(list, domtree, comp.getComponents().get("comment_content"));
		parseCommentUid(list, domtree, comp.getComponents().get("comment_uid"));
		for (WeiboData wd : list) {
			wd.setMd5(MD5Util.MD5(wd.getUid() + wd.getPubtime()));
			wd.setId(Integer.parseInt(keyword[0]));
			wd.setInserttime(new Date());
		}
		String nextPage = parseCommentNext(domtree, comp.getComponents().get("comment_next"));
		domtree = null;
		return nextPage;
	}

	/* 以下为评论数据抽取方法 */
	public String parseCommentNext(Node domtree, Component component, String... args) {
		NodeList nl = commonList(component.getXpath(), domtree);
		if (nl.item(0) != null) {
			return urlProcess(component, nl.item(0));
		}
		return null;
	}

	public void parseCommentContent(List<WeiboData> list, Node domtree, Component component, String... args) {
		if (component == null)
			return;
		NodeList nl = head(component.getXpath(), domtree, list.size(), component.getName());
		if (nl == null)
			return;
		for (int i = 0; i < nl.getLength(); i++) {
			list.get(i).setBrief(nl.item(i).getTextContent());
		}
	}

	public void parseCommentTime(List<WeiboData> list, Node domtree, Component component, String... args) {
		if (component == null)
			return;
		NodeList nl = head(component.getXpath(), domtree, list.size(), component.getName());
		if (nl == null)
			return;
		for (int i = 0; i < nl.getLength(); i++) {
			list.get(i).setPubtime(nl.item(i).getTextContent());
		}
	}

	public void parseCommentAuthorImg(List<WeiboData> list, Node domtree, Component component, String... args) {
		if (component == null)
			return;
		NodeList nl = head(component.getXpath(), domtree, list.size(), component.getName());
		if (nl == null)
			return;
		for (int i = 0; i < nl.getLength(); i++) {
			list.get(i).setAuthorImg(nl.item(i).getTextContent());
		}
	}

	public void parseCommentAuthorUrl(List<WeiboData> list, Node domtree, Component component, String... args) {
		if (component == null)
			return;
		NodeList nl = head(component.getXpath(), domtree, list.size(), component.getName());
		if (nl == null)
			return;
		for (int i = 0; i < nl.getLength(); i++) {
			list.get(i).setAuthorurl(urlProcess(component, nl.item(i)));
		}
	}

	public void parseCommentAuthor(List<WeiboData> list, Node domtree, Component component, String... args) {
		if (component == null)
			return;
		NodeList nl = head(component.getXpath(), domtree);
		if (nl == null)
			return;
		for (int i = 0; i < nl.getLength(); i++) {
			WeiboData data = new WeiboData();
			data.setAuthor(nl.item(i).getTextContent());
			list.add(data);
		}
	}

	public void parseCommentUid(List<WeiboData> list, Node domtree, Component component, String... args) {
		if (component == null)
			return;
		NodeList nl = head(component.getXpath(), domtree, list.size(), component.getName());
		if (nl == null)
			return;
		for (int i = 0; i < nl.getLength(); i++) {
			list.get(i).setUid(nl.item(i).getTextContent());
		}
	}

	/**
	 * 转发数据抽取
	 * 
	 * @param list
	 * @param html
	 * @param page
	 * @param keyword
	 * @return
	 * @throws IOException
	 * @throws SAXException
	 */
	public String templateRtt(List<WeiboData> list, HtmlInfo html, int page, String... keyword) throws SAXException, IOException {
		Siteinfo siteinfo = Systemconfig.allSiteinfos.get(html.getSite());
		Node domtree = getRealDOM(html);
		if (domtree == null) {
			LOGGER.info("DOM解析为NULL！！");
			return null;
		}
		CommonComponent comp = getRealComp(siteinfo, html.getType().substring(0, html.getType().indexOf(File.separator)));// 得到数据的配置组件
		parseRttAuthor(list, domtree, comp.getComponents().get("rtt_author"));
		parseRttAuthorImg(list, domtree, comp.getComponents().get("rtt_author_img"));
		parseRttAuthorUrl(list, domtree, comp.getComponents().get("rtt_author_url"));
		parseRttTime(list, domtree, comp.getComponents().get("rtt_time"));
		parseRttContent(list, domtree, comp.getComponents().get("rtt_content"));
		parseRttUrl(list, domtree, comp.getComponents().get("rtt_url"));
		parseRttUid(list, domtree, comp.getComponents().get("rtt_uid"));

		for (WeiboData wd : list) {
			wd.setMd5(MD5Util.MD5(wd.getUrl()));
			wd.setId(Integer.parseInt(keyword[0]));
			wd.setInserttime(new Date());
		}

		String nextPage = parseRttNext(domtree, comp.getComponents().get("rtt_next"));
		domtree = null;
		return nextPage;
	}

	/* 以下为转发数据抽取方法 */
	public String parseRttNext(Node domtree, Component component, String... args) {
		NodeList nl = head(component.getXpath(), domtree);
		if (nl.item(0) != null) {
			return urlProcess(component, nl.item(0));
		}
		return null;
	}

	public void parseRttUid(List<WeiboData> list, Node domtree, Component component, String... args) {
		if (component == null)
			return;
		NodeList nl = head(component.getXpath(), domtree, list.size(), component.getName());
		if (nl == null)
			return;
		for (int i = 0; i < nl.getLength(); i++) {
			list.get(i).setUid(nl.item(i).getTextContent());
		}
	}

	public void parseRttUrl(List<WeiboData> list, Node domtree, Component component, String... args) {
		if (component == null)
			return;
		NodeList nl = head(component.getXpath(), domtree, list.size(), component.getName());
		if (nl == null)
			return;
		for (int i = 0; i < nl.getLength(); i++) {
			list.get(i).setUrl(urlProcess(component, nl.item(i)));
		}
	}

	public void parseRttTime(List<WeiboData> list, Node domtree, Component component, String... args) {
		if (component == null)
			return;
		NodeList nl = head(component.getXpath(), domtree, list.size(), component.getName());
		if (nl == null)
			return;
		for (int i = 0; i < nl.getLength(); i++) {
			list.get(i).setPubtime(nl.item(i).getTextContent());
		}
	}

	public void parseRttAuthorImg(List<WeiboData> list, Node domtree, Component component, String... args) {
		if (component == null)
			return;
		NodeList nl = head(component.getXpath(), domtree, list.size(), component.getName());
		if (nl == null)
			return;
		for (int i = 0; i < nl.getLength(); i++) {
			list.get(i).setAuthorImg(nl.item(i).getTextContent());
		}
	}

	public void parseRttAuthorUrl(List<WeiboData> list, Node domtree, Component component, String... args) {
		if (component == null)
			return;
		NodeList nl = head(component.getXpath(), domtree, list.size(), component.getName());
		if (nl == null)
			return;
		for (int i = 0; i < nl.getLength(); i++) {
			list.get(i).setAuthorurl(urlProcess(component, nl.item(i)));
		}
	}

	public void parseRttAuthor(List<WeiboData> list, Node domtree, Component component, String... args) {
		if (component == null)
			return;
		NodeList nl = commonList(component.getXpath(), domtree);
		for (int i = 0; i < nl.getLength(); i++) {
			WeiboData data = new WeiboData();
			data.setAuthor(nl.item(i).getTextContent());
			list.add(data);
		}
	}

	@Override
	public void parseUrl(List<WeiboData> list, Node dom, Component component, String... args) {

		for (int i = 0; i < list.size(); i++) {
			String xpath = component.getXpath();
			xpath = xpath.replace("[index]", "[" + (i + 1) + "]");
			NodeList nl = commonList(xpath, dom);
			if (nl.item(0) != null)
				list.get(i).setUrl(nl.item(0).getTextContent());
		}
	}

	@Override
	public void parseTitle(List<WeiboData> list, Node dom, Component component, String... args) {
	}

	@Override
	public String parseNext(Node dom, Component component, String... args) {
		if (component == null)
			return null;
		NodeList nl = head(component.getXpath(), dom);
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
	@Override
	public void parseContent(List<WeiboData> list, Node dom, Component component, String... args) {
		// if (component == null)
		// return;
		// NodeList nl = head(component.getXpath(), dom, list.size(),
		// component.getName());
		// String xWeizhi = "(" + component.getXpath() +
		// ")[index]/A[@class='W_btn_c6']/@href";
		// if (nl == null)
		// return;
		// for (int i = 0; i < nl.getLength(); i++) {
		// String url = nl.item(i).getTextContent();
		// list.get(i).setContent(url == null ? "" : url);
		//
		// try {
		// Node node = XPathAPI.selectSingleNode(dom, xWeizhi.replace("index",
		// (i + 1) + ""));
		// if (node != null) {
		// String weizhi = node.getTextContent();
		// list.get(i).setContentAddressUrl(weizhi);
		// }
		// } catch (TransformerException e) {
		// e.printStackTrace();
		// }
		//
		// }

		for (int i = 0; i < list.size(); i++) {

			String xpath = component.getXpath();
			xpath = xpath.replace("[index]", "[" + (i + 1) + "]");
			String xWeizhi = "(" + component.getXpath() + ")[index]/A[@class='W_btn_c6']/@href";
			NodeList nl = commonList(xpath, dom);
			StringBuilder cont = new StringBuilder();
			for(int j=0;j<nl.getLength();j++){
				cont.append(nl.item(j).getTextContent()).append("\n");
			}
			if(nl.getLength()!=0){
				list.get(i).setContent(cont.toString());
			}
			try {
				Node node = XPathAPI.selectSingleNode(dom, xWeizhi.replace("index", (i + 1) + ""));
				if (node != null) {
					String weizhi = node.getTextContent();
					list.get(i).setContentAddressUrl(weizhi);
				}
			} catch (TransformerException e) {
				e.printStackTrace();
			}
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
	@Override
	public void parseSource(List<WeiboData> list, Node dom, Component component, String... strings) {

		for (int i = 0; i < list.size(); i++) {
			String xpath = component.getXpath();
			xpath = xpath.replace("[index]", "[" + (i + 1) + "]");
			NodeList nl = commonList(xpath, dom);
			if (nl.item(0) != null)
				list.get(i).setSource(nl.item(0).getTextContent());
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
	@Override
	public void parsePubtime(List<WeiboData> list, Node dom, Component component, String... args) {

		for (int i = 0; i < list.size(); i++) {
			String xpath = component.getXpath();
			xpath = xpath.replace("[index]", "[" + (i + 1) + "]");
			NodeList nl = commonList(xpath, dom);
			if (nl.item(0) != null) {

				list.get(i).setPubtime(nl.item(0).getTextContent());
				list.get(i).setPubdate(timeProcess(list.get(i).getPubtime().trim()));
			}
		}
	}

	public void parseAuthor(WeiboData data, Node dom, Component component, String... args) {
		if (component == null)
			return;
		NodeList nl = head(component.getXpath(), dom);
		if (nl == null)
			return;
		if (nl.getLength() == 0)
			return;
		String str = nl.item(0).getTextContent();
		data.setAuthor(str);

	}

	public void parseContent(WeiboData data, Node dom, Component component, String... args) {
		if (component == null)
			return;
		NodeList nl = head(component.getXpath(), dom);
		if (nl == null)
			return;
		if (nl.getLength() == 0)
			return;
		String str = nl.item(0).getTextContent();
		data.setContent(str);

	}

	public void parseAddress(WeiboData data, Node dom, Component component, String... args) {
		if (component == null)
			return;
		NodeList nl = head(component.getXpath(), dom);
		if (nl == null)
			return;
		if (nl.getLength() == 0)
			return;
		String str = nl.item(0).getTextContent();
		data.setAddress(str);

	}

	public void parseAuthorImg(WeiboData data, Node dom, Component component, String... args) {
		if (component == null)
			return;
		NodeList nl = head(component.getXpath(), dom);
		if (nl == null)
			return;
		if (nl.getLength() == 0)
			return;
		String str = nl.item(0).getTextContent();
		data.setAuthorImg(str);

	}

	public void parsePubtime(WeiboData data, Node dom, Component component, String... args) {
		if (component == null)
			return;
		NodeList nl = head(component.getXpath(), dom);
		if (nl == null)
			return;
		if (nl.getLength() == 0)
			return;
		String str = nl.item(0).getTextContent();
		data.setPubtime(str);

	}

	public void parseAuthorUrl(WeiboData data, Node dom, Component component, String... args) {
		if (component == null)
			return;
		NodeList nl = head(component.getXpath(), dom);
		if (nl == null)
			return;
		if (nl.getLength() == 0)
			return;
		String str = nl.item(0).getTextContent();
		data.setAuthorurl(str);

	}

	public void parseImgUrl(WeiboData data, Node dom, Component component, String... args) {
		if (component == null)
			return;
		NodeList nl = head(component.getXpath(), dom);
		if (nl == null)
			return;
		if (nl.getLength() == 0)
			return;
		String str = nl.item(0).getTextContent();
		data.setImgUrl(str);

	}

	public void parseRttNum(WeiboData data, Node dom, Component component, String... args) {
		if (component == null)
			return;
		NodeList nl = head(component.getXpath(), dom);
		if (nl == null)
			return;
		if (nl.getLength() == 0)
			return;
		String str = nl.item(0).getTextContent();
		data.setRttNum(Integer.parseInt(str));

	}

	public void parseCommentNum(WeiboData data, Node dom, Component component, String... args) {
		if (component == null)
			return;
		NodeList nl = head(component.getXpath(), dom);
		if (nl == null)
			return;
		if (nl.getLength() == 0)
			return;
		String str = nl.item(0).getTextContent();
		data.setCommentNum(Integer.parseInt(str));

	}

	public void parseMid(WeiboData data, Node dom, Component component, String... args) {
		if (component == null)
			return;
		NodeList nl = head(component.getXpath(), dom);
		if (nl == null)
			return;
		if (nl.getLength() == 0)
			return;
		String str = nl.item(0).getTextContent();
		data.setMid(str);

	}

	public void parseRttContent(WeiboData data, Node dom, Component component, String... args) {
		if (component == null)
			return;
		NodeList nl = head(component.getXpath(), dom);
		if (nl == null)
			return;
		if (nl.getLength() == 0)
			return;
		String str = nl.item(0).getTextContent();
		data.setMid(data.getContent() + "\n\\\n" + str);

	}

	public void parseUid(WeiboData data, Node dom, Component component, String... args) {
		if (component == null)
			return;
		NodeList nl = head(component.getXpath(), dom);
		if (nl == null)
			return;
		if (nl.getLength() == 0)
			return;
		String str = nl.item(0).getTextContent();
		data.setUid(str);

	}

	public void parseSource(WeiboData data, Node dom, Component component, String... args) {

		if (component == null)
			return;
		NodeList nl = head(component.getXpath(), dom);
		if (nl == null)
			return;
		if (nl.getLength() == 0)
			return;
		String str = nl.item(0).getTextContent();
		data.setSource(str);

	}

	public void parseCommenturl(WeiboData data, Node dom, Component component, String... args) {
		data.setCommentUrl("http://weibo.com/aj/comment/big?id=" + data.getMid());

	}

	public void parseRtturl(WeiboData data, Node dom, Component component, String... args) {
		data.setRttUrl("http://weibo.com/aj/mblog/info/big?id=" + data.getMid());

	}

	@Override
	public void parseCommenturl(List<WeiboData> list, Node dom, Component component, String... args) {
	}

	@Override
	public void parseRtturl(List<WeiboData> list, Node dom, Component component, String... args) {
	}

	@Override
	public void parseUid(List<WeiboData> list, Node domtree, Component component, String... args) {

	}

	@Override
	public void parseImgUrl(List<WeiboData> list, Node dom, Component component, String... args) {

		for (int i = 0; i < list.size(); i++) {
			String xpath = component.getXpath();
			xpath = xpath.replace("[index]", "[" + (i + 1) + "]");
			NodeList nl = commonList(xpath, dom);
			if (nl.item(0) != null)
				list.get(i).setImgUrl(nl.item(0).getTextContent());
		}
	}

	@Override
	public void parseMid(List<WeiboData> list, Node dom, Component component) {
		for (int i = 0; i < list.size(); i++) {
			String xpath = component.getXpath();
			xpath = xpath.replace("[index]", "[" + (i + 1) + "]");
			NodeList nl = commonList(xpath, dom);
			if (nl.item(0) != null)
				list.get(i).setMid(nl.item(0).getTextContent().trim());
		}
	}

	@Override
	public void parseRttContent(List<WeiboData> list, Node dom, Component component, String... content) {
	}

	@Override
	public void parseCommentNum(List<WeiboData> list, Node dom, Component component) {
		for (int i = 0; i < list.size(); i++) {
			String xpath = component.getXpath();
			xpath = xpath.replace("[index]", "[" + (i + 1) + "]");
			NodeList nl = commonList(xpath, dom);
			if (nl.item(0) != null) {
				String s = StringUtil.extractMulti(nl.item(0).getTextContent(), "\\d+");
				if (s.equals(""))
					list.get(i).setCommentNum(0);
				else
					list.get(i).setCommentNum(Integer.parseInt(s));
			}
		}
	}

	@Override
	public void parseRttNum(List<WeiboData> list, Node dom, Component component) {
		for (int i = 0; i < list.size(); i++) {
			String xpath = component.getXpath();
			xpath = xpath.replace("[index]", "[" + (i + 1) + "]");
			NodeList nl = commonList(xpath, dom);
			if (nl.item(0) != null) {

				String s = StringUtil.extractMulti(nl.item(0).getTextContent(), "\\d+");
				if (s.equals(""))
					list.get(i).setRttNum(0);
				else
					list.get(i).setRttNum(Integer.parseInt(s));
			}
		}
	}

	@Override
	public void parseAuthorUrl(List<WeiboData> list, Node dom, Component component, String... content) {

		for (int i = 0; i < list.size(); i++) {
			String xpath = component.getXpath();
			xpath = xpath.replace("[index]", "[" + (i + 1) + "]");
			NodeList nl = commonList(xpath, dom);
			if (nl.item(0) != null)
				list.get(i).setAuthorurl(nl.item(0).getTextContent());
		}
	}

	@Override
	public void parseAuthorImg(List<WeiboData> list, Node dom, Component component, String... args) {
		for (int i = 0; i < list.size(); i++) {
			String xpath = component.getXpath();
			xpath = xpath.replace("[index]", "[" + (i + 1) + "]");
			NodeList nl = commonList(xpath, dom);
			if (nl.item(0) != null)
				list.get(i).setAuthorImg(nl.item(0).getTextContent());
		}
	}

	@Override
	public void parseAuthor(List<WeiboData> list, Node dom, Component component) {
		if (component == null)
			return;
		NodeList nl = head(component.getXpath(), dom);
		if (nl == null)
			return;
		for (int i = 0; i < nl.getLength(); i++) {
			WeiboData vd = new WeiboData();
			vd.setAuthor(nl.item(i).getTextContent());
			list.add(vd);
		}
	}

}
