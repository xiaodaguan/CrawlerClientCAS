package common.extractor.xpath.agricalture.search;

import java.util.List;

import org.w3c.dom.Node;

import common.bean.AgricaltureData;
import common.extractor.ExtractorAttribute;
import common.siteinfo.Component;

public interface AgricaltureSearchExtractorAttribute extends ExtractorAttribute<AgricaltureData> {

	/**
	 * 进入相同新闻的链接解析
	 * @param list
	 * @param dom
	 * @param component
	 * @param args
	 */
	public void parseSameurl(List<AgricaltureData> list, Node dom,
			Component component, String... args);
	/**
	 * 相同新闻数量解析
	 * @param list
	 * @param dom
	 * @param component
	 * @param args
	 */
	public void parseSamenum(List<AgricaltureData> list, Node dom,
			Component component, String... args);
	/**
	 * 摘要解析
	 * @param list
	 * @param dom
	 * @param component
	 * @param args
	 */
	public void parseBrief(List<AgricaltureData> list, Node dom, Component component, String... args);

	/**
	 * 发布源解析
	 * @param list
	 * @param dom
	 * @param component
	 * @param args
	 */
	public void parseSource(List<AgricaltureData> list, Node dom, Component component, String... args);
	/**
	 * 发布时间解析
	 * @param list
	 * @param dom
	 * @param component
	 * @param args
	 */
	public void parsePubtime(List<AgricaltureData> list, Node dom, Component component, String... args);
}
