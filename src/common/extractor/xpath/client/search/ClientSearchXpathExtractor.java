package common.extractor.xpath.client.search;


import common.bean.ClientData;
import common.bean.HtmlInfo;
import common.bean.ReplyData;
import common.extractor.xpath.XpathExtractor;
import common.siteinfo.CommonComponent;
import common.siteinfo.Component;
import common.siteinfo.Siteinfo;
import common.system.Systemconfig;
import common.util.MD5Util;
import common.util.StringUtil;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 论坛抽取实现类
 * @author rzy
 */
public class ClientSearchXpathExtractor extends XpathExtractor<ClientData> implements ClientSearchExtractorAttribute {
	
	
	@Override
	public void processList(List<ClientData> list, Node domtree,
			Map<String, Component> components, String... args) {
		this.parseTitle(list, domtree, components.get("title"));

		if (list.size() == 0) return;
		this.parseUrl(list, domtree, components.get("url"));
	}

	@Override
	public void parseUrl(List<ClientData> list, Node dom, Component component, String... args) {
		NodeList nl = head(component.getXpath(), dom, list.size(), component.getName());
		if(nl==null) return;
		for(int i = 0;i < nl.getLength();i++) {
			list.get(i).setUrl(urlProcess(component, nl.item(i)));
		}
	}
	

	public void parseReplyimg(List<ReplyData> list, Node dom,
			Component component, String... strings) {
		NodeList nl = head(component.getXpath(), dom, list.size(), component.getName());
		for(int i = 0;i < nl.getLength();i++){	
			list.get(i).setImgUrl(nl.item(i).getTextContent());;
		}
	}
	@Override
	public void parseTitle(List<ClientData> list, Node dom, Component component, String... args) {
		if(component==null) return;
		NodeList nl = head(component.getXpath(), dom);
		for(int i = 0;i < nl.getLength();i++) {
			ClientData vd = new ClientData();
			vd.setTitle(StringUtil.format(nl.item(i).getTextContent()));
			list.add(vd);
		}
	}
	@Override
	public String parseNext(Node dom, Component component, String... args) {
		NodeList nl = commonList(component.getXpath(), dom);
		if(nl==null)return null;
		if(nl.item(0)!=null) {
			return urlProcess(component, nl.item(0));
		}
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
	public void parseContent(ClientData data, Node dom, Component component,
			String... args) {
		NodeList nl = commonList(component.getXpath(), dom);
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
	public void parseAuthor(ClientData data, Node dom, Component component,
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
	public void parsePubtime(ClientData data, Node dom, Component component,
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
	public void parseClickCount(ClientData data, Node domtree,
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
	public void parseSource(ClientData data, Node domtree, Component component,
			String... args) {
		NodeList nl = commonList(component.getXpath(), domtree);
		if(nl==null) return;
		if(nl.item(0)!=null) {
			String time = nl.item(0).getTextContent();
			data.setSource(time);
		}
	}
	@Override
	public void parseReplyCount(ClientData data, Node domtree,
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
	public void parseColumn(ClientData data, Node domtree, Component component,
			String... ags) {
		NodeList nl = commonList(component.getXpath(), domtree);
		if(nl==null) return;
		if(nl.item(0)!=null) {
			String time = nl.item(0).getTextContent();
			data.setColumn(time);
		}
	}
	@Override
	public void parseImgUrl(ClientData data, Node domtree, Component component,
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
	public void parsePageTitle(ClientData data, Node domtree, Component component,
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
		if(nl!=null)
		for(int i = 0;i < nl.getLength();i++) {
			ReplyData vd = new ReplyData();
			vd.setName(nl.item(i).getTextContent());
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
		if(nl==null)return null;
		if(nl.item(0)!=null) {
			return urlProcess(component, nl.item(0));
		}
		return null;
	}

	public void parseSourceUrl(ClientData data, Node domtree,Component component, 
			String... ags) {
		NodeList nl = commonList(component.getXpath(), domtree);
		if(nl==null) return;
		if(nl.item(0)!=null) {
			data.setSourceUrl(nl.item(0).getTextContent());
		}
	}
	@Override
	public void processPage(ClientData data, Node domtree,
			Map<String, Component> map, String... args) {
	}	
}
	