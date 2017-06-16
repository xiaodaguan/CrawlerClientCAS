package common.extractor.xpath.client.search.sub;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import common.pojos.ClientData;
import common.pojos.HtmlInfo;
import common.pojos.ReplyData;
import common.extractor.xpath.client.search.ClientSearchXpathExtractor;
import common.http.SimpleHttpProcess;
import common.siteinfo.CommonComponent;
import common.siteinfo.Component;
import common.siteinfo.Siteinfo;
import common.system.Systemconfig;
import common.utils.MD5Util;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class YidianExtractor extends ClientSearchXpathExtractor {
	private static final Logger LOGGER = LoggerFactory.getLogger(YidianExtractor.class);


	@Override
	public void processList(List<ClientData> list, Node domtree,
			Map<String, Component> components, String... args) {
		this.parseTitle(list, domtree, components.get("title"));

		if (list.size() == 0) return;
		this.parseUrl(list, domtree, components.get("url"));
		this.parsePubtime(list, domtree, components.get("pubtime_lp"));
		this.parseSource(list, domtree, components.get("source_lp"));
		this.parseLikeCount(list, domtree, components.get("like_count_lp"));
		this.parseReplyCount(list, domtree, components.get("reply_count_lp"));
	}
	
	public void parsePubtime(List<ClientData> list, Node dom, Component component, String... args) {
		NodeList nl = head(component.getXpath(), dom, list.size(), component.getName());
		if(nl==null) return;
		for(int i = 0;i < nl.getLength();i++) {
			String time = Calendar.getInstance().get(Calendar.YEAR) +"-" + nl.item(i).getTextContent(); 
			list.get(i).setPubtime(time);
			list.get(i).setPubdate(timeProcess(time));
		}
	}
	public void parseSource(List<ClientData> list, Node dom, Component component, String... args) {
		NodeList nl = head(component.getXpath(), dom, list.size(), component.getName());
		if(nl==null) return;
		for(int i = 0;i < nl.getLength();i++) {
			list.get(i).setSource(nl.item(i).getTextContent());
		}
	}
	public void parseLikeCount(List<ClientData> list, Node dom, Component component, String... args) {
		NodeList nl = head(component.getXpath(), dom, list.size(), component.getName());
		if(nl==null) return;
		for(int i = 0;i < nl.getLength();i++) {
			int count = 0 ;
			String line = nl.item(i).getTextContent().trim();
			if(line.length()>0)
				count = Integer.parseInt(line);
			list.get(i).setLikeCount(count);
		}
	}
	public void parseReplyCount(List<ClientData> list, Node dom, Component component, String... args) {
		NodeList nl = head(component.getXpath(), dom, list.size(), component.getName());
		if(nl==null) return;
		for(int i = 0;i < nl.getLength();i++) {
			int count = 0 ;
			String line = nl.item(i).getTextContent().trim();
			if(line.length()>0)
				count = Integer.parseInt(line);
			list.get(i).setReplyCount(count);
		}
	}
	
	@Override
	public String templateContentPage(ClientData data, HtmlInfo html, int page,
			String... keyword) throws SAXException, IOException {
		Siteinfo siteinfo = Systemconfig.allSiteinfos.get(html.getSite());
		Node domtree = getRealDOM(html);
		if(domtree ==null ){
			LOGGER.info("DOM解析为NULL！！");
			return null;
		}
		CommonComponent comp = getRealComp(siteinfo, html.getType().substring(0, html.getType().indexOf(File.separator)));//得到元数据的配置组件
		
		this.parseContent(data, domtree, comp.getComponents().get("content"), html.getContent());
		this.parseImgUrl(data, domtree, comp.getComponents().get("img_url"), new String[]{html.getContent()});
					
		data.setInserttime(new Date());
		data.setSiteId(siteinfo.getSiteFlag());
		if(data.getMd5()==null)
			data.setMd5(MD5Util.MD5(data.getUrl()));
		
		if(data.getTitle()==null) {
			this.parsePageTitle(data, domtree, comp.getComponents().get("pageTitle"), html.getContent());
		}
		
		List<ReplyData> list = new ArrayList<ReplyData>();
		parseReplyList(list,html);
		data.setReplyList(list);
		domtree = null;
		return null;
	}
	public void parseReplyList(List<ReplyData> list,HtmlInfo html) {
		String url = html.getOrignUrl();
		String commentUrl = "http://www.yidianzixun.com/api/q/?path=contents/comments&version=999999&docid=<id>&count=1000 ";
		commentUrl = commentUrl.replace("<id>", url.split("&id=")[1].split("&up")[0]);
		html.setOrignUrl(commentUrl);
		SimpleHttpProcess http = new SimpleHttpProcess();
		http.getContent(html);
		String content = html.getContent();

		JSONArray jarray = JSONObject.fromObject(content)//.getJSONObject("comments")
				.getJSONArray("comments");
		for (Object obj : jarray) {
			JSONObject  jobj = (JSONObject)obj;
			String name 	= jobj.getString("nickname").trim();
			String nameurl 	= jobj.getString("profile").trim();
			String comment  = jobj.getString("comment").trim();
			String pubtime  = jobj.getString("createAt").trim();
			ReplyData replyData = new ReplyData();
			replyData.setName(name);
			replyData.setImgUrl(nameurl);
			replyData.setContent(comment);
			replyData.setTime(pubtime);
			replyData.setPubdate(timeProcess(pubtime));
			replyData.setMd5(MD5Util.MD5(name+comment+pubtime));
			list.add(replyData);
		}	
	}
	
	@Override
	public void parseReplyCount(ClientData data, Node domtree,
			Component component, String... ags) {
		NodeList nl = commonList(component.getXpath(), domtree);
		if(nl==null) return;
		data.setReplyCount(nl.getLength());
	}
	
	@Override
	public void parseReplytime(List<ReplyData> list, Node dom,
			Component component, String... strings) {
		NodeList nl = head(component.getXpath(), dom, list.size(), component.getName());
		for(int i = 0;i < nl.getLength();i++) {
			list.get(i).setTime(Calendar.getInstance().get(Calendar.YEAR) +"-" + nl.item(i).getTextContent().trim().replace("时间：", ""));
			list.get(i).setPubdate(timeProcess(list.get(i).getTime()));
		}
	}
}
