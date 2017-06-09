package common.extractor.xpath.blog.search.sub;

import common.bean.BlogData;
import common.bean.HtmlInfo;
import common.extractor.xpath.blog.search.BlogSearchXpathExtractor;
import common.siteinfo.Component;
import common.system.Systemconfig;
import common.util.ExtractResult;
import common.util.StringUtil;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;


public class SogouExtractor extends BlogSearchXpathExtractor {

	@Override
	public String templateContentPage(BlogData data, HtmlInfo html, int page, String... keyword) {
		
		ExtractResult result = null;
		try {
			result = Systemconfig.extractor.extract(html.getContent(), html.getEncode(), data.getUrl());
		} catch (Exception e) {
			LOGGER.info("出错url：" + html.getOrignUrl());
			e.printStackTrace();
		}
		String title = data.getTitle() == null ? result.getTitle() : data.getTitle();
		// 标题需要处理
		// 搜索词里面带有特殊符号的情况
		boolean spe = false;
		if (data.getSearchKey().indexOf("_") > -1 || data.getSearchKey().indexOf("-") > -1) {
			spe = true;
			title = title.replace(data.getSearchKey(), "{temp}");
		}
		// 其他情况
		if (title.indexOf("_") > -1) {
			title = title.split("_")[0].trim();
		}
		// if(title.indexOf("-") > -1) {
		// title = title.split("-")[0].trim();
		// }
		if (spe)
			title = title.replace("{temp}", data.getSearchKey());
		data.setTitle(title);
		data.setContent(result.getContent());
		data.setImgUrl(result.getImgs());
		//data.setInserttime(new Timestamp(System.currentTimeMillis()));
		return null;
	}

	@Override
	public void parseAuthor(List<BlogData> list, Node dom,
			Component component, String... content) {
		if(component == null) return;
		for(int i = 0;i < list.size();i++) {//DIV[@class='vrwrap']
			NodeList nl = commonList("//DIV[@class='vrwrap']["+(i+1)+"]"+component.getXpath(), dom);
			if(nl.item(0)!=null) {
				list.get(i).setBlogAuthor(nl.item(0).getTextContent());
			}
		}
//		if(component == null) return;
//		for(int i = 0;i < list.size();i++) {
//			NodeList nl = commonList("//DIV["+(i+1)+component.getXpath(), dom);
//			if(nl.item(0)!=null) {
//				String[] con = nl.item(0).getTextContent().split("　");
//				if(con.length>2) {
//					list.get(i).setBlogName(con[0].replace("博客名称：", ""));
//					list.get(i).setBlogAuthor(con[1].replace("作者：", ""));
//					if(list.get(i).getPubtime()==null || list.get(i).getPubtime().equals(""))
//						list.get(i).setPubtime(con[2]);
//				}
//			}
//		}
	}
	@Override
	public void parseSource(List<BlogData> list, Node dom,
			Component component, String... args) {
		if(component == null) return;
		NodeList nl = head(component.getXpath(), dom, list.size(), component.getName());
		if(nl==null) return;
		for(int i = 0;i < nl.getLength();i++) {
			String arr[] = nl.item(i).getTextContent().split("- ");
			list.get(i).setSource(arr[0]);
			if(arr.length>2) {
				list.get(i).setPubtime(StringUtil.extractMulti(arr[2], "[12]\\d{1,3}-\\d{1,2}-\\d{1,2}"));
			} else {
				list.get(i).setPubtime(StringUtil.extractMulti(nl.item(i).getTextContent(), "[12]\\d{1,3}-\\d{1,2}-\\d{1,2}"));
			}
			list.get(i).setPubdate(timeProcess(list.get(i).getPubtime()));
		}
	}
	@Override
	public void parseTitle(List<BlogData> list, Node dom, Component component, String... args) {
		if (component == null)
			return;
		NodeList anl = head("//DIV[@class='vrwrap']", dom);
		for (int i = 0; i < anl.getLength(); i++) {
			list.add(new BlogData());
		}
		for(int i = 0;i < list.size();i++) {//DIV[@class='vrwrap']
			NodeList nl = commonList("//DIV[@class='vrwrap']["+(i+1)+"]"+component.getXpath(), dom);
			if(nl.item(0)!=null) {
				list.get(i).setTitle(nl.item(0).getTextContent());
			}
		}
	}
	@Override
	public void parseBrief(List<BlogData> list, Node dom, Component component,
			String... args) {
		if(component == null) return;
		for(int i = 0;i < list.size();i++) {//DIV[@class='vrwrap']
			NodeList nl = commonList("//DIV[@class='vrwrap']["+(i+1)+"]"+component.getXpath(), dom);
			if(nl.item(0)!=null) {
				list.get(i).setBrief(nl.item(0).getTextContent());
			}
		}
	}
	@Override
	public void parseUrl(List<BlogData> list, Node dom, Component component, String... args) {
		if (component == null)	return;

		for(int i = 0;i < list.size();i++) {
			NodeList nl = head("//DIV[@class='vrwrap']["+(i+1)+"]"+component.getXpath(), dom);
			if(nl.item(0)!=null) {
				list.get(i).setUrl(nl.item(0).getTextContent());
			}
		}
	}
	
	
	@Override
	public void parsePubtime(List<BlogData> list, Node dom, Component component, String... args) {
	
		if (component == null)	{
			
			System.out.println("pubtime component is null !!!"+component);
			return;
		}
		for(int i = 0;i < list.size();i++) {
			NodeList nl = head("//DIV[@class='vrwrap']["+(i+1)+"]"+component.getXpath(), dom);
			if(nl.item(0)!=null) {
				String time = (nl.item(0).getTextContent());
				list.get(i).setPubtime(time);
				list.get(i).setPubdate(timeProcess(time.trim()));
			}
		}
	}
}
