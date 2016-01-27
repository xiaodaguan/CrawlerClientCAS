package common.extractor.xpath.agricalture.monitor;

import org.w3c.dom.Node;

import common.bean.AgricaltureData;
import common.extractor.ExtractorAttribute;
import common.siteinfo.Component;

public interface AgricaltureMonitorExtractorAttribute extends ExtractorAttribute<AgricaltureData> {

	/**
	 * 解析内容
	 * @param list
	 * @param dom
	 * @param component
	 * @param args
	 */
	public void parseContent(AgricaltureData data, Node dom, Component component, String... args);

	/**
	 * 发布源解析
	 * @param list
	 * @param dom
	 * @param component
	 * @param args
	 */
	public void parseSource(AgricaltureData data, Node dom, Component component, String... args);
	/**
	 * 作者解析
	 * @param list
	 * @param dom
	 * @param component
	 * @param args
	 */
	public void parseAuthor(AgricaltureData data, Node dom, Component component, String... args);
	/**
	 * 发布时间解析
	 * @param list
	 * @param dom
	 * @param component
	 * @param args
	 */
	public void parsePubtime(AgricaltureData data, Node dom, Component component, String... args);
	/**
	 * 解析图片链接
	 * @param list
	 * @param dom
	 * @param component
	 * @param args
	 */
	public void parseImgUrl(AgricaltureData data, Node dom, Component component, String... args);
	/**
	 * 解析标题
	 * @param list
	 * @param dom
	 * @param component
	 * @param args
	 */
	public void parsePageTitle(AgricaltureData data, Node dom, Component component, String... args);
}
