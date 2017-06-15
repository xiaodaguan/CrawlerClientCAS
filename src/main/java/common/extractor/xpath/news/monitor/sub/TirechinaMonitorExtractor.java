package common.extractor.xpath.news.monitor.sub;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import common.pojos.NewsData;
import common.extractor.xpath.news.monitor.NewsMonitorExtractorAttribute;
import common.extractor.xpath.news.monitor.NewsMonitorXpathExtractor;
import common.siteinfo.Component;
import common.util.StringUtil;

/**
 * 抽取实现类
 * 
 * @author grs
 */
public class TirechinaMonitorExtractor extends NewsMonitorXpathExtractor implements NewsMonitorExtractorAttribute {

	@Override
	public void parseSource(NewsData data, Node dom, Component component, String... args) {
		String str = "";
		if (component == null)
			return;

		NodeList nl = commonList(component.getXpath(), dom);
		if (nl == null)
			return;

		if (nl.item(0) != null)
			str = StringUtil.format(nl.item(0).getTextContent());
		str=str.replace("来源：", "");
		if (str.equals(""))
			str = "中国轮胎商务网";
		str = str.length()>20? "中国轮胎商务网" : str;
		data.setSource(str);
	}
	
	@Override
	public void parseImgUrl(NewsData data, Node dom, Component component, String... args) {
		if (component == null)
			return;
		NodeList nl = commonList(component.getXpath(), dom);
		if (nl == null)
			return;
		String url = "";
		for (int i = 0; i < nl.getLength(); i++){
			String tmp=nl.item(i).getTextContent();
			if(!tmp.startsWith("http:")){
				if(data.getUrl().contains("tirechina"))
					tmp="http://www.tirechina.net"+tmp;
			}
			url += tmp + ";";
		}

		data.setImgUrl(url);
	}
}
