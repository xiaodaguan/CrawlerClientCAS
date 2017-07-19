package common.extractor.xpath.bbs.search;

import common.pojos.BbsData;
import common.pojos.HtmlInfo;
import common.pojos.ReplyData;
import common.extractor.xpath.XpathExtractor;
import common.siteinfo.CommonComponent;
import common.siteinfo.Component;
import common.siteinfo.Siteinfo;
import common.system.Systemconfig;
import common.utils.MD5Util;
import common.utils.StringUtil;
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

/**
 * 论坛抽取实现类
 * @author grs
 */
public class BbsSearchXpathExtractor extends XpathExtractor<BbsData> implements BbsSearchExtractorAttribute {

	private static final Logger LOGGER = LoggerFactory.getLogger(BbsSearchXpathExtractor.class);

	@Override
	public void processList(List<BbsData> list, Node domtree,
                            Map<String, Component> components, String... args) {
		this.parseTitle(list, domtree, components.get("title"));

		if (list.size() == 0) return;
		this.parseUrl(list, domtree, components.get("url"));
	}

	@Override
	public void parseUrl(List<BbsData> list, Node dom, Component component, String... args) {
		NodeList nl = head(component.getXpath(), dom, list.size(), component.getName());
		if(nl==null) return;
		for(int i = 0;i < nl.getLength();i++) {
			list.get(i).setUrl(urlProcess(component, nl.item(i)));
		}
	}
	@Override
	public void parseTitle(List<BbsData> list, Node dom, Component component, String... args) {
		if(component==null) return;
		NodeList nl = head(component.getXpath(), dom);
		for(int i = 0;i < nl.getLength();i++) {
			BbsData vd = new BbsData();
			vd.setTitle(StringUtil.format(nl.item(i).getTextContent()));
			list.add(vd);
		}
	}
	@Override
	public String parseNext(Node dom, Component component, String... args) {
		NodeList nl = commonList(component.getXpath(), dom);
		if(nl.item(0)!=null) {
			return urlProcess(component, nl.item(0));
		}
		return null;
	}
	
	@Override
	public String templateContentPage(BbsData data, HtmlInfo html, int page,
                                      String... keyword) throws SAXException, IOException {
		Siteinfo siteinfo = Systemconfig.allSiteinfos.get(html.getSite());
//		create(content);
		if (html.getContent().contains("抱歉，您访问的贴子被隐藏")) {
			return null;
		}
		Node domtree = getRealDOM(html);
		if(domtree ==null ){
			LOGGER.info("DOM解析为NULL！！");
			return null;
		}
		CommonComponent comp = getRealComp(siteinfo, html.getCrawlerType().substring(0, html.getCrawlerType().indexOf(File.separator)));//得到元数据的配置组件
		if(page == 1) {
			this.parseContent(data, domtree, comp.getComponents().get("content"), html.getContent());
			this.parseSource(data, 	domtree, comp.getComponents().get("source"), html.getContent());
			this.parseAuthor(data, 	domtree, comp.getComponents().get("author"), html.getContent());
			this.parsePubtime(data, domtree, comp.getComponents().get("pubtime"), html.getContent());
			this.parseClickCount(data, domtree, comp.getComponents().get("click_count"), new String[]{html.getContent()});
			this.parseReplyCount(data, domtree, comp.getComponents().get("reply_count"), new String[]{html.getContent()});
			this.parseColumn(data, domtree, comp.getComponents().get("column"), new String[]{html.getContent()});
			this.parseImgUrl(data, domtree, comp.getComponents().get("img_url"), new String[]{html.getContent()});
			if(data.getPubdate()==null && data.getPubtime()!=null)
				data.setPubdate(timeProcess(data.getPubtime().trim()));
			
			data.setInserttime(new Date());
			data.setSiteId(siteinfo.getSiteFlag());
			if(data.getMd5()==null)
				data.setMd5(MD5Util.MD5(data.getUrl()));
			
			if(data.getTitle()==null) {
				this.parsePageTitle(data, domtree, comp.getComponents().get("pageTitle"), html.getContent());
			}
		}
		
		//回复列表
		List<ReplyData> list = new ArrayList<ReplyData>();
		this.parseReplyname(list, domtree, comp.getComponents().get("reply_name"), new String[]{html.getContent()});
		
		if(list.size()>0) {
			this.parseReplytime(list, domtree, comp.getComponents().get("reply_time"), new String[]{html.getContent()});
			this.parseReplycontent(list, domtree, comp.getComponents().get("reply_content"), new String[]{html.getContent()});
		}
		
		data.setReplyList(list);
		
		String next = this.parseReplyNext(domtree, comp.getComponents().get("reply_next"));
		domtree = null;
		return next;
	}


	@Override
	public void parseContent(BbsData data, Node dom, Component component,
                             String... args) {
		NodeList nl = commonList(component.getXpath(), dom);
		if(nl==null) return;
		String brief = "";
		for(int i = 0;i < nl.getLength();i++) {
			brief += nl.item(i).getTextContent();
		}
		data.setContent(StringUtil.format(brief)!=""?StringUtil.format(brief):"  \n");
	}
	@Override
	public void parseAuthor(BbsData data, Node dom, Component component,
                            String... args) {
		NodeList nl = commonList(component.getXpath(), dom);
		if(nl==null) return;
		String author = "";
		for(int i = 0;i < nl.getLength();i++) {
			author += nl.item(i).getTextContent();
			if(i < nl.getLength()-1) 
				author+=";";
		}
		data.setAuthor(StringUtil.format(author));
	}
	@Override
	public void parsePubtime(BbsData data, Node dom, Component component,
                             String... args) {
		NodeList nl = commonList(component.getXpath(), dom);
		if(nl==null) return;
		if(nl.item(0)!=null) {
			String time = nl.item(0).getTextContent().replace("年", "-").replace("月", "").replace("日", "");
			time = StringUtil.extractMulti(time,"\\d+-\\d+-\\d+.\\d*.\\d*.\\d*");
			data.setPubtime(time);
			data.setPubdate(timeProcess(time));
		}else{
			//if not found.
			String time = StringUtil.extractOne(args[0],"\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}");
			data.setPubtime(time);
			data.setPubdate(timeProcess(time));
		}
	}
	@Override
	public void parseClickCount(BbsData data, Node domtree,
                                Component component, String... ags) {
		NodeList nl = commonList(component.getXpath(), domtree);
		if(nl==null) return;
		if(nl.item(0)!=null) {
			String time = StringUtil.extractMulti(nl.item(0).getTextContent(), "\\d");
			if(time==null || time.equals(""))
				data.setClickCount(0);
			else
				data.setClickCount(Integer.parseInt(time));
		}
	}
	@Override
	public void parseSource(BbsData data, Node domtree, Component component,
                            String... args) {
		NodeList nl = commonList(component.getXpath(), domtree);
		if(nl==null) return;
		if(nl.item(0)!=null) {
			String time = nl.item(0).getTextContent();
			data.setPubfrom(time);
		}
	}
	@Override
	public void parseReplyCount(BbsData data, Node domtree,
                                Component component, String... ags) {
		NodeList nl = commonList(component.getXpath(), domtree);
		if(nl==null) return;
		if(nl.item(0)!=null) {
			String time = StringUtil.extractMulti(nl.item(0).getTextContent(), "\\d");
			if(time==null || time.equals(""))
				data.setReplyCount(0);
			else
				data.setReplyCount(Integer.parseInt(time));
		}
	}
	@Override
	public void parseColumn(BbsData data, Node domtree, Component component,
                            String... ags) {
		NodeList nl = commonList(component.getXpath(), domtree);
		if(nl==null) return;
		if(nl.item(0)!=null) {
			String time = nl.item(0).getTextContent();
			data.setColumn(time);
		}
	}
	@Override
	public void parseImgUrl(BbsData data, Node domtree, Component component,
                            String... args) {
		NodeList nl = commonList(component.getXpath(), domtree);
		if(nl==null) return;
		String url = "";
		for(int i = 0;i < nl.getLength();i++) {
			url += nl.item(i).getTextContent().trim();
			if(i < nl.getLength()-1)
				url += ";";
		}
		data.setImgUrl(url);
	}
	@Override
	public void parsePageTitle(BbsData data, Node domtree, Component component,
                               String... args) {
		NodeList nl = commonList(component.getXpath(), domtree);
		if(nl==null) return;
		if(nl.item(0)!=null) {
			data.setTitle(nl.item(0).getTextContent());
		}
	}
	@Override
	public void parseReplyname(List<ReplyData> list, Node domtree,
			Component component, String... strings) {
		NodeList nl = head(component.getXpath(), domtree);
//		LOGGER.info(domtree.getTextContent());
		for(int i = 0;i < nl.getLength();i++) {
			ReplyData vd = new ReplyData();
			vd.setName(StringUtil.format(nl.item(i).getTextContent()));
			list.add(vd);
		}
	}
	@Override
	public void parseReplytime(List<ReplyData> list, Node dom,
			Component component, String... strings) {
		NodeList nl = head(component.getXpath(), dom, list.size(), component.getName());
		for(int i = 0;i < nl.getLength();i++) {
			list.get(i).setTime(nl.item(i).getTextContent().trim().replace("时间：", ""));
			list.get(i).setPubdate(timeProcess(list.get(i).getTime()));
		}
	}
	@Override
	public void parseReplycontent(List<ReplyData> list, Node dom,
			Component component, String... strings) {
		NodeList nl = head(component.getXpath(), dom, list.size(), component.getName());
		for(int i = 0;i < nl.getLength();i++) {
			list.get(i).setContent(nl.item(i).getTextContent().trim());
			list.get(i).setMd5(MD5Util.MD5(list.get(i).getName()+list.get(i).getContent()+list.get(i).getTime()));
		}
	}
	@Override
	public String parseReplyNext(Node domtree, Component component) {
		if(component==null) return null;
		NodeList nl = commonList(component.getXpath(), domtree);
		if(nl.item(0)!=null) {
			return urlProcess(component, nl.item(0));
		}
		return null;
	}

	@Override
	public void processPage(BbsData data, Node domtree,
                            Map<String, Component> map, String... args) {
	}
	
}
	