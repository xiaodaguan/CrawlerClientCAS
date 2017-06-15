package common.extractor.xpath.govaffair.search.sub;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import common.pojos.GovAffairData;
import common.pojos.HtmlInfo;
import common.extractor.xpath.govaffair.search.GovAffairSearchXpathExtractor;
import common.siteinfo.CommonComponent;
import common.siteinfo.Component;
import common.siteinfo.Siteinfo;
import common.system.Systemconfig;


/**
 * 政务搜索特殊属性抽取
 * @author rzy
 *
 */
public class GovwebExtractor extends GovAffairSearchXpathExtractor {
	private static final Logger LOGGER = LoggerFactory.getLogger(GovwebExtractor.class);

	@Override
	public String templateContentPage(GovAffairData data, HtmlInfo html, int page, String... keyword) {
		{
			Siteinfo siteinfo = Systemconfig.allSiteinfos.get(html.getSite());
	//		create(content);
	        Node domtree = null;
			try {
				domtree = getRealDOM(html);
			} catch (SAXException | IOException e) {	
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        if (domtree == null) {
	            LOGGER.info("DOM解析为NULL！！");
	            return null;
	        }
	        CommonComponent comp = getRealComp(siteinfo, html.getType().substring(0, html.getType().indexOf(File.separator)));//得到元数据的配置组件
	        if(domtree==null|comp==null)return null;
	        //comp.getComponents().get("content")
	        //comp.getComponents().get("content").setXpath("//DIV//P");
	        //comp.getComponents().get("imgUrl").setXpath("//DIV//P/IMG/@src");
	        //div class="editor"
	        comp.getComponents().get("author").setXpath("//DIV[@class='editor']");
	        this.parseContent(data, domtree,comp.getComponents().get("content"), html.getContent());
	        this.parseImgUrl(data, domtree, comp.getComponents().get("imgUrl"), html.getContent());
	        this.parseSource(data, domtree, comp.getComponents().get("source"), html.getContent());
	        this.parseAuthor(data, domtree, comp.getComponents().get("author"), html.getContent());
		}
//        {
//        	ExtractResult result = null;
//    		try {
//    			result = Systemconfig.extractor.extract(html.getContent(), html.getEncode(), data.getUrl());
//    		} catch (Exception e) {
//    			LOGGER.info("出错url：" + html.getOrignUrl());
//    			e.printStackTrace();
//    		}
//    		//data.setTitle(title);
//    		//data.setContent(result.getContent());
//    		data.setImgUrl(result.getImgs());
//    		//data.setInserttime(new Timestamp(System.currentTimeMillis()));
//        }
		return null;
	}

	@Override
	public void parsePubtime(List<GovAffairData> list, Node dom, Component component, String... args) {
		if(component==null) return;
		NodeList nl = head(component.getXpath(), dom, list.size(), component.getName());
		if(nl==null) return;
		for(int i = 0;i < nl.getLength();i++) {
			String time = nl.item(i).getTextContent().split("：")[1]
					.replace(".", "-").replace(".", "-").replace(".", "");			
			list.get(i).setPubtime(time);
			list.get(i).setPubdate(timeProcess(list.get(i).getPubtime()));
		}
	}
	@Override
	public void parseSource(GovAffairData data, Node dom, Component component, String... strings) {
		if (component == null)
			return;
		NodeList nl = commonList(component.getXpath(), dom);
		if (nl == null)
			return;
		String str = "";
		for (int i = 0; i < nl.getLength(); i++) {
			str += nl.item(i).getTextContent();
		}
		data.setSource(str.split("：")[1].trim());
	}
	@Override
	public void parseAuthor(GovAffairData data, Node dom, Component component, String... strings) {
		if (component == null)
			return;
		NodeList nl = commonList(component.getXpath(), dom);
		if (nl == null)
			return;
		String str = "";
		for (int i = 0; i < nl.getLength(); i++) {
			str += nl.item(i).getTextContent();
		}
		data.setAuthor(str.split("：")[1].trim());
	}
	@Override
	public void parseSource(List<GovAffairData> list, Node dom, Component component, String... strings) {
		if(component==null) return;
		NodeList nl = head(component.getXpath(), dom, list.size(), component.getName());
		if(nl==null) return;
		for(int i = 0;i < nl.getLength();i++) {
			String source = nl.item(i).getTextContent();
			list.get(i).setSource(source);
		}
	}
	
}
