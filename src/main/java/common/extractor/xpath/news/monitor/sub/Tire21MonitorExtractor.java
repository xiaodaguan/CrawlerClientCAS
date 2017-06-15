package common.extractor.xpath.news.monitor.sub;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;

import common.pojos.NewsData;
import common.extractor.xpath.news.monitor.NewsMonitorExtractorAttribute;
import common.extractor.xpath.news.monitor.NewsMonitorXpathExtractor;
import common.siteinfo.Component;

/**
 * 抽取实现类
 * 
 * @author grs
 */
public class Tire21MonitorExtractor extends NewsMonitorXpathExtractor implements NewsMonitorExtractorAttribute {
	private static final Logger LOGGER = LoggerFactory.getLogger(Tire21MonitorExtractor.class);

	@Override
	public String parseNext(Node dom, Component component, String... args) {
		String url = args[0];
		int count = 0;
		for (int i = 0; i < url.toCharArray().length; i++) {
			if (url.toCharArray()[i] == '-') {
				count++;
			}
		}

		int currPageInt = 0;
		if (count != 1) {
			String currPageStr = url.substring(url.lastIndexOf("-") + 1, url.lastIndexOf(".htm"));

			try {
				currPageInt = Integer.parseInt(currPageStr);
			} catch (Exception e) {
				LOGGER.info("截取到非数字字符.");
				e.printStackTrace();
			}
		}
		int nextPage = currPageInt + 1;

		if (count == 1) {
			url = url.substring(0, url.lastIndexOf(".htm")) + "-" + nextPage + url.substring(url.lastIndexOf(".htm"));
		} else {
			url = url.replace("-" + currPageInt + ".htm", "-" + nextPage + ".htm");
		}

		return url;
	}

	@Override
	public void parseSource(NewsData data, Node dom, Component component, String... args) {
		data.setSource("中国轮胎网");
	}
}
