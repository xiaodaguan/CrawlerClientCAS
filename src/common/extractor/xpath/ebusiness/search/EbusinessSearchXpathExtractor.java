package common.extractor.xpath.ebusiness.search;

import common.bean.EbusinessData;
import common.extractor.xpath.XpathExtractor;
import common.siteinfo.Component;
import org.w3c.dom.Node;

import java.util.List;
import java.util.Map;

/**
 * 电商抽取实现类
 * 
 * @author grs
 */
public class EbusinessSearchXpathExtractor extends XpathExtractor<EbusinessData> implements
		EbusinessSearchExtractorAttribute {


	@Override
	public void processPage(EbusinessData data, Node domtree, Map<String, Component> map, String... args) {

	}

	@Override
	public void processList(List<EbusinessData> list, Node domtree, Map<String, Component> components, String... args) {

	}

	@Override
	public void parseUrl(List<EbusinessData> list, Node dom, Component component, String... args) {

	}

	@Override
	public void parseTitle(List<EbusinessData> list, Node dom, Component component, String... args) {

	}

	@Override
	public void parsePrice(EbusinessData ebd, Node dom, Component component, String... args) {

	}

	@Override
	public void parseImgs_product(EbusinessData ebd, Node dom, Component component, String... args) {

	}

	@Override
	public void parseTransation(EbusinessData ebd, Node dom, Component component, String... args) {

	}

	@Override
	public void parseInfo_code(EbusinessData ebd, Node dom, Component component, String... args) {

	}

	@Override
	public void parseInfo_pubtime(EbusinessData ebd, Node dom, Component component, String... args) {

	}

	@Override
	public void parseInfo_type(EbusinessData ebd, Node dom, Component component, String... args) {

	}

	@Override
	public void parseParams(EbusinessData ebd, Node dom, Component component, String... args) {

	}

	@Override
	public String getOwnerInitUrl(String info_code) {
		return null;
	}

	@Override
	public int getCommentCount(String content) {
		return 0;
	}

	@Override
	public String templateCommentPage(EbusinessData ed, int commentPage, String... args) {
		return null;
	}
}
