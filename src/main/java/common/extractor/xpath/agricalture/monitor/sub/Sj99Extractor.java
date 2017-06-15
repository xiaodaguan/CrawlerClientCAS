package common.extractor.xpath.agricalture.monitor.sub;

import java.util.List;

import javax.xml.transform.TransformerException;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import common.pojos.AgricaltureData;
import common.pojos.HtmlInfo;
import common.extractor.xpath.agricalture.monitor.AgricaltureMonitorExtractorAttribute;
import common.extractor.xpath.agricalture.monitor.AgricaltureMonitorXpathExtractor;
import common.siteinfo.Component;

public class Sj99Extractor extends AgricaltureMonitorXpathExtractor implements AgricaltureMonitorExtractorAttribute {
	@Override public String parseNext(Node domtree, Component component, HtmlInfo html, String... args) throws TransformerException {
		return null;
	}

	@Override public void parseUnit(List<AgricaltureData> list, Node dom, Component component, String... args) {
		for (int i = 0; i < list.size(); i++) {
			list.get(i).setUnit("元/kg");
		}
	}

	@Override public void parsePubtime(List<AgricaltureData> list, Node dom, Component component, String... args) {

		if (component == null)
			return;
		NodeList nl = head(component.getXpath(), dom);
		if (nl == null)
			return;
		String time = nl.item(0).getTextContent();
		for (int i = 0; i < list.size(); i++) {

			list.get(i).setPubtime(time);
			list.get(i).setPubdate(timeProcess(time));
		}

	}
}
