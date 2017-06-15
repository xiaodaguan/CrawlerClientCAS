package common.extractor.xpath.frgmedia.search.sub;

import java.util.List;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import common.pojos.FrgmediaData;
import common.extractor.xpath.frgmedia.search.FrgmediaSearchXpathExtractor;
import common.siteinfo.Component;

/**
 * 外媒搜索特殊属性抽取
 * 
 * @author rzy
 *
 */
public class ChinaruExtractor extends FrgmediaSearchXpathExtractor {

	//http://www.chinaru.info/UpFiles/Article/201374164437169.jpg
	@Override
	public void parseSource(FrgmediaData data, Node dom, Component component, String... strings) {
		if (component == null)
			return;
		NodeList nl = commonList(component.getXpath(), dom);
		if (nl == null)
			return;
		String str = "";
		for (int i = 0; i < nl.getLength(); i++) {
			str += nl.item(i).getTextContent().trim();
		}
		
	}
	
	@Override
	public void parsePubtime(List<FrgmediaData> list, Node dom, Component component, String... args) {
		if (component == null)
			return;
		NodeList nl = head(component.getXpath(), dom, list.size(), component.getName());
		if (nl == null)
			return;
		for (int i = 0; i < nl.getLength(); i++) {
			String time = nl.item(i).getTextContent().split("：")[1].trim();
			list.get(i).setPubtime(time);
			list.get(i).setPubdate(timeProcess(list.get(i).getPubtime().trim()));
		}
	}
}
