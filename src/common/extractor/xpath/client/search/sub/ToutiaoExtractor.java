package common.extractor.xpath.client.search.sub;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import common.bean.ClientData;
import common.bean.HtmlInfo;
import common.bean.ReplyData;
import common.extractor.xpath.client.search.ClientSearchXpathExtractor;
import common.http.SimpleHttpProcess;
import common.siteinfo.CommonComponent;
import common.siteinfo.Component;
import common.siteinfo.Siteinfo;
import common.system.Systemconfig;
import common.util.MD5Util;
import common.util.StringUtil;
import common.util.TimeUtil;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class ToutiaoExtractor extends ClientSearchXpathExtractor {
	
	@Override
	public String templateListPage(List<ClientData> list, HtmlInfo html, int page, String... keyword) throws SAXException, IOException {
		Siteinfo siteinfo = Systemconfig.allSiteinfos.get(html.getSite());
		
		System.out.println("html.getContent():"+html.getContent());
		JSONArray jarray = JSONObject.fromObject(html.getContent()).getJSONArray("data");
		for (Object obj : jarray) {
			JSONObject  jobj = (JSONObject)obj;
			String title 		= jobj.getString("title");
			String titleUrl 	= jobj.getString("display_url");
			String brief		= jobj.getString("abstract");
			String source 		= jobj.getString("source");
			String sourceUrl 	= jobj.getString("article_url");
			String likeCount  	= jobj.getString("digg_count");
			String dislikeCount = jobj.getString("bury_count");
			String replyCount 	= jobj.getString("comment_count");
			String pubtime 		= jobj.getString("datetime");
			
			ClientData data = new ClientData();
			data.setTitle(title);
			data.setUrl(titleUrl);
			data.setSource(source);
			data.setSourceUrl(sourceUrl);
			data.setBrief(brief);
			data.setPubtime(pubtime);
			data.setPubdate(timeProcess(pubtime));
			data.setLikeCount(Integer.parseInt(likeCount));
			data.setDislikeCount(Integer.parseInt(dislikeCount));
			data.setReplyCount(Integer.parseInt(replyCount));
			list.add(data);
		}	
		if (list.size() == 0)
			return null;
		attrSet(list, siteinfo.getSiteFlag(), keyword[0], Integer.parseInt(keyword[2]));
		return null;
	}

	@Override
	public String templateContentPage(ClientData data, HtmlInfo html, int page,
			String... keyword) throws SAXException, IOException {
		Siteinfo siteinfo = Systemconfig.allSiteinfos.get(html.getSite());
		
		Node domtree = getRealDOM(html);
		if(domtree ==null ){
			Systemconfig.sysLog.log("DOM解析为NULL！！");
			return null;
		}
		CommonComponent comp = getRealComp(siteinfo, html.getType().substring(0, html.getType().indexOf(File.separator)));//得到元数据的配置组件
		if(page == 1) {
			
			this.parseContent(data, domtree, comp.getComponents().get("content"), html.getContent());
			if(data.getContent()==null){
				this.parseContent(data, domtree, "//P", html.getContent());
			}
			this.parseImgUrl(data, domtree, comp.getComponents().get("img_url"), new String[]{html.getContent()});
			this.parseImgUrl(data, domtree, comp.getComponents().get("column"), new String[]{html.getContent()});
			
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
//		if(list.size()>0) {
//			this.parseReplytime(list, domtree, comp.getComponents().get("reply_time"), new String[]{html.getContent()});
//			this.parseReplycontent(list, domtree, comp.getComponents().get("reply_content"), new String[]{html.getContent()});
//			this.parseReplyimg(list, domtree, comp.getComponents().get("reply_img"), new String[]{html.getContent()});
//		}
		if(data.getReplyCount()>0&&html.getOrignUrl().contains("http://toutiao.com"))
			parseRelaylist(data,html,list);//http://toutiao.com/group/6353814625155023105/
		data.setReplyList(list);
		domtree = null;
		return null;
	}
	public void parseContent(ClientData data, Node dom, String xpath,
			String... args) {
		NodeList nl = commonList(xpath, dom);
		if(nl==null) return;
		String content = "";
		for(int i = 0;i < nl.getLength();i++) {
			String line = nl.item(i).getTextContent();
			if(line.length()<1)continue;
			content += line+"\n";
		}
		data.setContent(StringUtil.format(content)!=""?StringUtil.format(content):"  \n");
	}
	@Override
	public void parseReplyimg(List<ReplyData> list, Node dom,
			Component component, String... strings) {
		NodeList nl = head(component.getXpath(), dom, list.size(), component.getName());
		for(int i = 0;i < nl.getLength();i++){	
			list.get(i).setImgUrl("http://www.toutiao.com"+nl.item(i).getTextContent().trim());;
		}
	}
	public void parseRelaylist(ClientData data,HtmlInfo html,List<ReplyData> list){
		String url = html.getOrignUrl();
		String content = html.getContent();
		String group_id = extractOne(url,"\\d+");
		//String  item = content.split("item_id:")[1].split("\n")[0];
		//String item_id = extractOne(item, "\\d+");

		//String commentUrl = "http://www.toutiao.com/api/comment/list/?group_id=<group_id>&item_id=<item_id>&offset=0&count=<count>"; 
		String commentUrl = "http://www.toutiao.com/group/<group_id>/comments/?count=<count>&page=<page>&offset=<offset>&item_id=0&format=json";
		
		int commentCount = data.getReplyCount();
		int count = 100;
		commentUrl = commentUrl.replace("<group_id>", group_id).replace("<count>", count+"");
				//replace("<item_id>", item_id).replace("<count>",commentCount+"");
		SimpleHttpProcess http = new SimpleHttpProcess();
		
		for (int i = 0; i < commentCount/count ; i++) {
			int page=i+2;
			int offset=i*count;
			String  churl = commentUrl.replace("<page>", page+"").replace("<offset>",offset+"");
			System.out.println("churl:"+churl);
			html.setOrignUrl(churl);
			http.getContent(html);
			content = html.getContent();
			JSONArray jarray = JSONObject.fromObject(content).getJSONObject("data")
					.getJSONArray("comments");
			for (Object obj : jarray) {
				JSONObject  jobj 	= (JSONObject)obj;
				String 	name = null;
				String uimgUrl = null;
				if(content.contains("user_name")){
					name 	= jobj.getString("user_name");
			 		uimgUrl = jobj.getString("user_profile_image_url");
				}else{
			 		name 	= jobj.getJSONObject("user").getString("name");
			 		uimgUrl = jobj.getJSONObject("user").getString("avatar_url");
				}
				String time = jobj.getString("create_time");
				String comment = jobj.getString("text");
				String formats = "yyyy-MM-dd HH:mm:ss";
				Long timestamp = Long.parseLong(time)*1000;    
				String pubtime = new java.text.SimpleDateFormat(formats).format(new java.util.Date(timestamp));
				ReplyData replyData = new ReplyData();
				replyData.setName(name);
				replyData.setImgUrl(uimgUrl);
				replyData.setContent(comment);
				replyData.setTime(pubtime);
				replyData.setPubdate(timeProcess(pubtime));
				replyData.setMd5(MD5Util.MD5(name+comment+pubtime));
				list.add(replyData);
			}
			TimeUtil.rest(5);
		}
	}
	public void parseRelaylist2(ClientData data,HtmlInfo html,List<ReplyData> list){
		String url = html.getOrignUrl();
		String content = html.getContent();
		String group_id = extractOne(url,"\\d+");
		String  item = content.split("item_id:")[1].split("\n")[0];
		String item_id = extractOne(item, "\\d+");

		//String commentUrl = "http://www.toutiao.com/api/comment/list/?group_id=<group_id>&item_id=<item_id>&offset=0&count=<count>"; 
		String commentUrl = "http://www.toutiao.com/group/6357916266116153602/comments/?count=100&page=2&offset=0&item_id=0&format=json";
		
		int commentCount = data.getReplyCount();
		commentUrl = commentUrl.replace("<group_id>", group_id).
				replace("<item_id>", item_id).replace("<count>",commentCount+"");
		SimpleHttpProcess http = new SimpleHttpProcess();
		html.setOrignUrl(commentUrl);
		http.getContent(html);
		content = html.getContent();
		
		JSONArray jarray = JSONObject.fromObject(content).getJSONObject("data")
				.getJSONArray("comments");
		
		for (Object obj : jarray) {
			JSONObject  jobj 	= (JSONObject)obj;
			String 		name 	= jobj.getJSONObject("user").getString("name");
			String 		uimgUrl = jobj.getJSONObject("user").getString("avatar_url");
			String time = jobj.getString("create_time");
			String comment = jobj.getString("text");
			
			String formats = "yyyy-MM-dd HH:mm:ss";
			Long timestamp = Long.parseLong(time)*1000;    
			String pubtime = new java.text.SimpleDateFormat(formats).format(new java.util.Date(timestamp));
			ReplyData replyData = new ReplyData();
			replyData.setName(name);
			replyData.setImgUrl(uimgUrl);
			replyData.setContent(comment);
			replyData.setTime(pubtime);
			replyData.setPubdate(timeProcess(pubtime));
			replyData.setMd5(MD5Util.MD5(name+comment+pubtime));
			list.add(replyData);
		}	
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
