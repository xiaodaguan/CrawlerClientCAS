package common.extractor.xpath.conference.monitor.sub;

import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import common.pojos.ConferenceData;
import common.extractor.xpath.conference.monitor.ConferenceMonitorXpathExtractor;
import common.siteinfo.Component;

public class Huiyi31Extractor extends ConferenceMonitorXpathExtractor {
	@Override public void processList(List<ConferenceData> list, Node domtree, Map<String, Component> comp, String... args) {
		this.parseTitle(list, domtree, comp.get("title"));

		if (list.size() == 0)
			return;

		this.parseUrl(list, domtree, comp.get("url"));

		// this.parseBrief(list, domtree, comp.get("brief"));
		this.parseSource(list, domtree, comp.get("source"));
		this.parsePubtime(list, domtree, comp.get("pubtime"));
		this.parseAuthor(list, domtree, comp.get("author"));
		this.parsePlace(list, domtree, comp.get("place"));
	}

	@Override public void parsePubtime(List<ConferenceData> list, Node dom, Component component, String... args) {
		if (component == null)
			return;
		NodeList nl = head(component.getXpath(), dom, list.size(), component.getName());
		if (nl == null)
			return;
		for (int i = 0; i < nl.getLength(); i++) {
			String time = nl.item(i).getTextContent().split("-")[0].trim();
			Calendar cal = Calendar.getInstance();
			int year = cal.get(Calendar.YEAR);
			if (!time.contains("年"))
				time = year + "年" + time;
			time=time.replace("年", "-").replace("月", "-").replace("日", "");
			list.get(i).setPubtime(time);
			list.get(i).setPubdate(timeProcess(list.get(i).getPubtime().trim()));
		}
	}

	@Override public void parseSource(List<ConferenceData> list, Node dom, Component component, String... strings) {
		if (component == null)
			return;

		for (int i = 0; i < list.size(); i++) {

			String xpath = component.getXpath().replace("index", "" + (i + 1));
			NodeList nl = commonList(xpath, dom);
			if (nl == null)
				continue;
			if (nl.getLength() == 0)
				continue;
			list.get(i).setSource(nl.item(0).getTextContent());
		}
	}
}
