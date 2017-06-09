package common.extractor.xpath.academic.search;

import common.bean.AgricaltureData;
import common.bean.HtmlInfo;
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
import java.util.List;
import java.util.Map;

/**
 * 论坛抽取实现类
 * @author grs
 */
public class AcademicSearchXpathExtractor extends XpathExtractor<AgricaltureData> implements AcademicSearchExtractorAttribute {
	@Override
	public String templateListPage(List<AgricaltureData> list, HtmlInfo html,
			int page, String... keyword) throws SAXException, IOException {
		list.clear();
		Siteinfo siteinfo = Systemconfig.allSiteinfos.get(html.getSite());
		Node domtree = getRealDOM(html);
		if(domtree == null ){
			LOGGER.info("DOM解析为NULL！！");
			return null;
		}
		CommonComponent comp = getRealComp(siteinfo, html.getType().substring(0, html.getType().indexOf(File.separator)));//得到元数据的配置组件
		this.parseTitle(list, domtree, comp.getComponents().get("title"));
		
		if (list.size() == 0) return null;
		
		this.parseUrl(list, domtree, comp.getComponents().get("url"));
		
		for(AgricaltureData vd : list) {
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
	public void parseUrl(List<AgricaltureData> list, Node dom, Component component, String... args) {
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
				url = url + component.getPostfix();
			list.get(i).setUrl(url);
		}
	}
	@Override
	public void parseTitle(List<AgricaltureData> list, Node dom, Component component, String... args) {
		if(component==null) return;
		NodeList nl = commonList(component.getXpath(), dom);
		if(nl==null) return;
		judge(list.size(), nl.getLength());
		for(int i = 0;i < nl.getLength();i++) {
			AgricaltureData vd = new AgricaltureData();
			vd.setTitle(StringUtil.format(nl.item(i).getTextContent()));
			list.add(vd);
		}
	}
	@Override
	public String parseNext(Node dom, Component component, String... args) {
		if(component==null) return null;
		NodeList nl = commonList(component.getXpath(), dom);
		if(nl==null) return null;
		if(nl.item(0)!=null) {
			String url = nl.item(0).getTextContent();
			if(!url.startsWith("http://")) {
				if(component.getPrefix()!=null && !component.getPrefix().startsWith("${"))
					url = component.getPrefix() + url;
			}
			if(component.getPostfix()!=null && !component.getPostfix().startsWith("${"))
				url = url + component.getPostfix();
			return url;
		}
		return null;
	}
	
	@Override
	public String templateContentPage(AgricaltureData data, HtmlInfo html, int page,
			String... keyword) {
		return null;
	}
	@Override
	public void processPage(AgricaltureData data, Node domtree,
			Map<String, Component> map, String... args) {

	}
	@Override
	public void processList(List<AgricaltureData> list, Node domtree,
			Map<String, Component> components, String... args) {

	}

}
	