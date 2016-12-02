package common.extractor.xpath.press.search;

import java.util.List;

import org.w3c.dom.Node;

import common.bean.PressData;
import common.bean.NewsData;
import common.extractor.ExtractorAttribute;
import common.siteinfo.Component;

public interface PressSearchExtractorAttribute extends ExtractorAttribute<PressData> {

	/**
	 * 进入相同新闻的链接解析
	 * @param list
	 * @param dom
	 * @param component
	 * @param args
	 */
	public void parseSameurl(List<PressData> list, Node dom,
			Component component, String... args);
	/**
	 * 相同新闻数量解析
	 * @param list
	 * @param dom
	 * @param component
	 * @param args
	 */
	public void parseSamenum(List<PressData> list, Node dom,
			Component component, String... args);
	/**
	 * 摘要解析
	 * @param list
	 * @param dom
	 * @param component
	 * @param args
	 */
	public void parseBrief(List<PressData> list, Node dom, Component component, String... args);

	/**
	 * 发布源解析
	 * @param list
	 * @param dom
	 * @param component
	 * @param args
	 */
	public void parseSource(List<PressData> list, Node dom, Component component, String... args);
	/**
	 * 发布时间解析
	 * @param list
	 * @param dom
	 * @param component
	 * @param args
	 */
	public void parsePubtime(List<PressData> list, Node dom, Component component, String... args);
}
