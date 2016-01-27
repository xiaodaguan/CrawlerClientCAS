package common.extractor.xpath.blog.search.sub;

import java.util.List;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import common.bean.BlogData;
import common.extractor.xpath.blog.search.BlogSearchXpathExtractor;
import common.siteinfo.Component;
import common.util.StringUtil;

public class HexunExtractor extends BlogSearchXpathExtractor {

	@Override public String parseNext(Node dom, Component component, String... args) {

		String currUrl = args[0];
		String currPage = currUrl.substring(currUrl.indexOf("&page=") + 6);
		int currPageInt = Integer.parseInt(currPage);

		String nextUrl = currUrl.replace("&page=" + currPage, "&page=" + (currPageInt + 1));
		return nextUrl;
	}
}
