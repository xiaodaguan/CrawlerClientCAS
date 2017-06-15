package common.extractor.xpath.frgmedia.search.sub;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import common.pojos.FrgmediaData;
import common.extractor.xpath.frgmedia.search.FrgmediaSearchXpathExtractor;
import common.siteinfo.Component;


/**
 * 外媒搜索特殊属性抽取
 * @author rzy
 *
 */
public class ZaobaoExtractor extends FrgmediaSearchXpathExtractor {
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
		if(str.length()==0)
			data.setSource("联合早报");
		else
			data.setSource(str);
	}
}
