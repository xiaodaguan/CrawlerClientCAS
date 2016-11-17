package common.extractor.xpath.bbs.search.sub;

import common.bean.BBSData;
import common.bean.HtmlInfo;
import common.bean.ReplyData;
import common.extractor.xpath.bbs.search.BbsSearchXpathExtractor;
import common.siteinfo.CommonComponent;
import common.siteinfo.Component;
import common.siteinfo.Siteinfo;
import common.system.Systemconfig;
import common.util.MD5Util;
import common.util.StringUtil;
import net.sf.json.JSONObject;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class sogouExtractor extends BbsSearchXpathExtractor {

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
	public void parsePubtime(BBSData data, Node dom, Component component,
			String... args) {
		NodeList nl = commonList(component.getXpath(), dom);
		if(nl==null) return;
		if(nl.item(0)!=null) {
			String time = nl.item(0).getTextContent().replace("年", "-").replace("月", "").replace("日", "");
			time = StringUtil.extractMulti(time,"\\d+-\\d+-\\d+.\\d*.\\d*.\\d*");
			data.setPubtime(time);
			data.setPubdate(timeProcess(time));
		}else{
			String dateRegex = "(\\d{1,4}[-|\\/|年|\\.]\\d{1,2}[-|\\/|月|\\.]\\d{1,2}([日|号])?(\\s)*(\\d{1,2}([点|时])?((:)?\\d{1,2}(分)?((:)?\\d{1,2}(秒)?)?))(\\s)*(PM|AM)?)";
			String time = StringUtil.extractOne(args[0],dateRegex);
			data.setPubtime(time);
			data.setPubdate(timeProcess(time));
		}
	}
	
	
	@Override
	public String templateContentPage(BBSData data, HtmlInfo html, int page,
			String... keyword) throws SAXException, IOException {
		Siteinfo siteinfo = Systemconfig.allSiteinfos.get(html.getSite());
//		create(content);
		if (html.getContent().contains("抱歉，您访问的贴子被隐藏")) {
			return null;
		}
		Node domtree = getRealDOM(html);
		if(domtree ==null ){
			Systemconfig.sysLog.log("DOM解析为NULL！！");
			return null;
		}
		CommonComponent comp = getRealComp(siteinfo, html.getType().substring(0, html.getType().indexOf(File.separator)));//得到元数据的配置组件
		if(page == 1) {
			this.parseContent(data, domtree, comp.getComponents().get("content"), html.getContent());
			this.parseSource(data, domtree, comp.getComponents().get("source"), html.getContent());
			this.parseAuthor(data, domtree, comp.getComponents().get("author"), html.getContent());
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
//		this.parseReplyname(list, domtree, comp.getComponents().get("reply_name"), new String[]{html.getContent()});
//		
//		if(list.size()>0) {
//			this.parseReplytime(list, domtree, comp.getComponents().get("reply_time"), new String[]{html.getContent()});
//			this.parseReplycontent(list, domtree, comp.getComponents().get("reply_content"), new String[]{html.getContent()});
//		}
		
		data.setReplyList(list);
		
		String next = this.parseReplyNext(domtree, comp.getComponents().get("reply_next"));
		domtree = null;
		return next;
	}

		
		
		
		
}
