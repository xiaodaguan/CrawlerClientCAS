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
import common.siteinfo.CommonComponent;
import common.siteinfo.Component;
import common.siteinfo.Siteinfo;
import common.system.Systemconfig;
import common.util.MD5Util;

public class ZakerExtractor extends ClientSearchXpathExtractor {
	private static final Logger LOGGER = LoggerFactory.getLogger(ZakerExtractor.class);


	@Override
	public void processList(List<ClientData> list, Node domtree,
			Map<String, Component> components, String... args) {
		this.parseTitle(list, domtree, components.get("title"));

		if (list.size() == 0) return;
		this.parseUrl(list, domtree, components.get("url"));
		this.parsePubtime(list, domtree, components.get("pubtime_lp"));
		this.parseSource(list, domtree, components.get("source_lp"));
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
		if(page == 1) {
			this.parseContent(data, domtree, comp.getComponents().get("content"), html.getContent());
			this.parseClickCount(data, domtree, comp.getComponents().get("click_count"), new String[]{html.getContent()});
			this.parseReplyCount(data, domtree, comp.getComponents().get("reply_count"), new String[]{html.getContent()});
			this.parseColumn(data, domtree, comp.getComponents().get("column"), new String[]{html.getContent()});
			this.parseImgUrl(data, domtree, comp.getComponents().get("img_url"), new String[]{html.getContent()});
			this.parseSourceUrl(data, domtree, comp.getComponents().get("source_url"), new String[]{html.getContent()});
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
			this.parseReplyimg(list, domtree, comp.getComponents().get("reply_img"), new String[]{html.getContent()});
		}
		
		data.setReplyList(list);
		
		String next = this.parseReplyNext(domtree, comp.getComponents().get("reply_next"));
		domtree = null;
		return next;
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
	@Override
	public void parseReplyimg(List<ReplyData> list, Node dom,
			Component component, String... strings) {
		NodeList nl = head(component.getXpath(), dom);
		if(nl==null)return ;
		int styleCount = -1;
		for(int i = 0;i < nl.getLength();i++) {
			String line = nl.item(i).getTextContent();
			//comment_avatar
			//3	:	background-image:
			if(line.contains("comment_avatar")){
				styleCount++;
			}else{
				list.get(styleCount).setImgUrl(line.split("url\\(")[1].split("\\);")[0]);
			}
			
		}
	}

}
