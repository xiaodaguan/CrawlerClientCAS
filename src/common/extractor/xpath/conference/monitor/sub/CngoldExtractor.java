package common.extractor.xpath.conference.monitor.sub;

import org.w3c.dom.Node;

import common.extractor.xpath.conference.monitor.ConferenceMonitorXpathExtractor;
import common.siteinfo.Component;

public class CngoldExtractor extends ConferenceMonitorXpathExtractor {

	@Override public String parseNext(Node dom, Component component, String... args) {

		String currUrl = args[0];
		String currPage = currUrl.substring(currUrl.indexOf("index_") + 6, currUrl.indexOf(".html"));
		int currPageInt = Integer.parseInt(currPage);

		String nextUrl = currUrl.replace("index_" + currPage + ".html", "index_" + (currPageInt + 1) + ".html");
		return nextUrl;
	}

}
