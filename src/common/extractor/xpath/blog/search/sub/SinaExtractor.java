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


public class SinaExtractor extends BlogSearchXpathExtractor {

	@Override
	public void parseSource(List<BlogData> list, Node dom,
			Component component, String... args) {
		
	}
	
}
