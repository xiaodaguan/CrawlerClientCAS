package common.extractor.xpath.video.search;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import common.bean.AgricaltureData;
import common.bean.HtmlInfo;
import common.bean.VideoData;
import common.extractor.xpath.XpathExtractor;
import common.siteinfo.CommonComponent;
import common.siteinfo.Component;
import common.siteinfo.Siteinfo;
import common.system.Systemconfig;
import common.util.MD5Util;
import common.util.StringUtil;

/**
 * 微博抽取实现类
 * @author grs
 */
public class VideoSearchXpathExtractor extends XpathExtractor<VideoData> implements VideoSearchExtractorAttribute {
	@Override
	public void parseUrl(List<VideoData> list, Node dom, Component component, String... args) {
		if(component==null) return;
		NodeList nl = commonList(component.getXpath(), dom);
		if(nl==null) return;
		judge(list.size(), nl.getLength(), "url");
		for(int i = 0;i < nl.getLength();i++) {
			String url = nl.item(i).getTextContent();
			if(!url.startsWith("http://")) {
				if(component.getPrefix()!=null && !component.getPrefix().startsWith("${"))
					url = component.getPrefix() + url;
			}
			if(component.getPostfix()!=null && !component.getPostfix().startsWith("${"))
				url = url + component.getPostfix() ;
			list.get(i).setUrl(url);
		}
	}
	@Override
	public void parseTitle(List<VideoData> list, Node dom, Component component, String... args) {
		if(component==null) return;
		NodeList nl = commonList(component.getXpath(), dom);
		if(nl==null) return;
		judge(list.size(), nl.getLength());
		for(int i = 0;i < nl.getLength();i++) {
			VideoData vd = new VideoData();
			vd.setTitle(StringUtil.format(nl.item(i).getTextContent()));
			list.add(vd);
		}
	}
	@Override
	public String parseNext(Node dom, Component component, String... args) {
		if(component==null) return null;
		NodeList nl = commonList(component.getXpath(), dom);
		if(nl==null) return null;
		String url = null;
		if(nl.item(0)!=null) {
			url = nl.item(0).getTextContent();
			if(!url.startsWith("http://")) {
				if(component.getPrefix()!=null && !component.getPrefix().startsWith("${"))
					url = component.getPrefix() + url;
			}
			if(component.getPostfix()!=null && !component.getPostfix().startsWith("${"))
				url = url + component.getPostfix() ;
		}
		return url;
	}
	
	@Override
	public String templateListPage(List<VideoData> list, HtmlInfo html,
			int page, String... keyword) throws SAXException, IOException {
		list.clear();
		Siteinfo siteinfo = Systemconfig.allSiteinfos.get(html.getSite());
		Node domtree = getRealDOM(html);
		if(domtree == null ){
			Systemconfig.sysLog.log("DOM解析为NULL！！");
			return null;
		}
		//System.out.println("class : "+this.getClass());
		CommonComponent comp = getRealComp(siteinfo, html.getType().substring(0, html.getType().indexOf(File.separator)));//得到元数据的配置组件
		this.parseTitle(list, domtree, comp.getComponents().get("title"));
		if (list.size() == 0) return null;
		this.parseUrl(list, domtree, comp.getComponents().get("url"), html.getContent());
//		this.parseContent(vd, domtree, comp.getComponents().get("content"));
		this.parsePubdate(list, domtree, comp.getComponents().get("pubtime"));
		this.parseAuthorUrl(list, domtree, comp.getComponents().get("authorUrl"));
		this.parsePlayCount(list, domtree, comp.getComponents().get("playCount"));
		this.parseCommentUrl(list, domtree, comp.getComponents().get("commentUrl"));
		this.parseTags(list, domtree, comp.getComponents().get("tags"));
		this.parseAuthor(list, domtree, comp.getComponents().get("author"));
		this.parsePlaytime(list, domtree, comp.getComponents().get("playtime"));
		this.parseLikeCount(list, domtree, comp.getComponents().get("likeCount"));
		this.parseDislikeCount(list, domtree, comp.getComponents().get("dislikeCount"));
		this.parseChannel(list, domtree, comp.getComponents().get("Channel"));
		for(VideoData vd : list) {
			vd.setSearchKey(keyword[0]);
			vd.setCategoryCode(Integer.parseInt(keyword[2]));
			vd.setMd5(MD5Util.MD5(vd.getUrl()));
			vd.setSiteId(siteinfo.getSiteFlag());
		}
		String nextPage = parseNext(domtree, comp.getComponents().get("next"), new String[]{keyword[1], page+""});
		domtree = null;
		return nextPage;
	}
	
	@Override
	public String templateContentPage(VideoData data, HtmlInfo html, int page,
			String... keyword) {
		return null;
	}
	@Override
	public void processPage(VideoData data, Node domtree,
			Map<String, Component> map, String... args) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void processList(List<VideoData> list, Node domtree,
			Map<String, Component> components, String... args) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void parseMd5(VideoData data, Node dom, Component component, String... args) {
		
		if(component==null) return;
		NodeList nl = commonList(component.getXpath(), dom);
		if(nl==null) return;
		data.setMd5(StringUtil.format(nl.item(0).getTextContent()));	
	}
	
	@Override
	public void parseContent(VideoData data, Node dom, Component component, String... args) {
		
		NodeList nl = commonList(component.getXpath(), dom);
		if(nl==null) return;
		data.setContent(StringUtil.format(nl.item(0).getTextContent()));
	}
	@Override
	public void parsePubdate(List<VideoData> list, Node dom, Component component, String... args) {
	}
	@Override
	public void parseAuthorUrl(List<VideoData> list, Node dom, Component component, String... args) {
		if(component==null) return;
		NodeList nl = commonList(component.getXpath(), dom);
		if(nl==null) return;
		for(int i = 0;i < nl.getLength();i++) {
			VideoData vd = list.get(i);
			vd.setAuthorUrl(StringUtil.format(nl.item(i).getTextContent()));
		}
	}
	@Override
	public void parsePlayCount(List<VideoData> list, Node dom, Component component, String... args) {
		if(component==null) return;
		NodeList nl = commonList(component.getXpath(), dom);
		if(nl==null) return;
		for(int i = 0;i < nl.getLength();i++) {
			VideoData vd = list.get(i);
			String s = nl.item(i).getTextContent().replace(",", "");
			vd.setPlayCount(Integer.parseInt(StringUtil.format(s)));
		}
	}
	@Override
	public void parseCommentUrl(List<VideoData> list, Node dom, Component component, String... args) {
		if(component==null) return;
		NodeList nl = commonList(component.getXpath(), dom);
		if(nl==null) return;
		for(int i = 0;i < nl.getLength();i++) {
			VideoData vd = list.get(i);
			vd.setCommentUrl(StringUtil.format(nl.item(i).getTextContent()));
		}
	}
	@Override
	public void parseTags(List<VideoData> list, Node dom, Component component, String... args) {
		if(component==null) return;
		NodeList nl = commonList(component.getXpath(), dom);
		if(nl==null) return;
		for(int i = 0;i < nl.getLength();i++) {
			VideoData vd = list.get(i);
			vd.setTags(StringUtil.format(nl.item(i).getTextContent()));
		}
	}
	@Override
	public void parseAuthor(List<VideoData> list, Node dom, Component component, String... args) {
		if(component==null) return;
		NodeList nl = commonList(component.getXpath(), dom);
		if(nl==null) return;
		for(int i = 0;i < nl.getLength();i++) {
			VideoData vd = list.get(i);
			vd.setAuthor(StringUtil.format(nl.item(i).getTextContent()));
		}
	}
	@Override
	public void parsePlaytime(List<VideoData> list, Node dom, Component component, String... args) {
		if(component==null) return;
		NodeList nl = commonList(component.getXpath(), dom);
		if(nl==null) return;
		for(int i = 0;i < nl.getLength();i++) {
			VideoData vd = list.get(i);
			vd.setPlaytime(StringUtil.format(nl.item(i).getTextContent()));
		}
	}
	@Override
	public void parseChannel(List<VideoData> list, Node dom, Component component, String... args) {
		if(component==null) return;
		NodeList nl = commonList(component.getXpath(), dom);
		if(nl==null) return;
		for(int i = 0;i < nl.getLength();i++) {
			VideoData vd = list.get(i);
			vd.setChannel(StringUtil.format(nl.item(i).getTextContent()));
		}
	}
	@Override
	public void parseLikeCount(List<VideoData> list, Node dom, Component component, String... args) {
		if(component==null) return;
		NodeList nl = commonList(component.getXpath(), dom);
		if(nl==null) return;
		for(int i = 0;i < nl.getLength();i++) {
			VideoData vd = list.get(i);
			vd.setLikeCount(Integer.parseInt(StringUtil.format(nl.item(i).getTextContent())));
		}
	}
	@Override
	public void parseDislikeCount(List<VideoData> list, Node dom, Component component, String... args) {
		if(component==null) return;
		NodeList nl = commonList(component.getXpath(), dom);
		if(nl==null) return;
		for(int i = 0;i < nl.getLength();i++) {
			VideoData vd = list.get(i);
			vd.setDislikeCount(Integer.parseInt(StringUtil.format(nl.item(i).getTextContent())));
		}
	}
	
	
}
	