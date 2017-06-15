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
public class TireworldMonitorExtractor extends NewsMonitorXpathExtractor implements NewsMonitorExtractorAttribute {

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
		str=str.replace("文章来源：", "");
		if (str.equals(""))
			str = "轮胎世界网";
		str = str.length()>20? "轮胎世界网" : str;
		data.setSource(str);
	}

}
