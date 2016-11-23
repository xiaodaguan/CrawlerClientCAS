package common.extractor.xpath.news.monitor;

import org.w3c.dom.Node;

import common.bean.p;
import common.extractor.ExtractorAttribute;
import common.siteinfo.Component;

public interface NewsMonitorExtractorAttribute extends ExtractorAttribute<p> {

	/**
	 * 解析内容
	 * @param list
	 * @param dom
	 * @param component
	 * @param args
	 */
	public void parseContent(p data, Node dom, Component component, String... args);

	/**
	 * 发布源解析
	 * @param list
	 * @param dom
	 * @param component
	 * @param args
	 */
	public void parseSource(p data, Node dom, Component component, String... args);
	/**
	 * 作者解析
	 * @param list
	 * @param dom
	 * @param component
	 * @param args
	 */
	public void parseAuthor(p data, Node dom, Component component, String... args);
	/**
	 * 发布时间解析
	 * @param list
	 * @param dom
	 * @param component
	 * @param args
	 */
	public void parsePubtime(p data, Node dom, Component component, String... args);
	/**
	 * 解析图片链接
	 * @param list
	 * @param dom
	 * @param component
	 * @param args
	 */
	public void parseImgUrl(p data, Node dom, Component component, String... args);
	/**
	 * 解析标题
	 * @param list
	 * @param dom
	 * @param component
	 * @param args
	 */
	public void parsePageTitle(p data, Node dom, Component component, String... args);
}
