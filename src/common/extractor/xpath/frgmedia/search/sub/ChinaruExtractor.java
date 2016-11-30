package common.extractor.xpath.frgmedia.search.sub;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import common.bean.FrgmediaData;
import common.bean.HtmlInfo;
import common.extractor.xpath.frgmedia.search.FrgmediaSearchXpathExtractor;
import common.siteinfo.CommonComponent;
import common.siteinfo.Component;
import common.siteinfo.Siteinfo;
import common.system.Systemconfig;

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
