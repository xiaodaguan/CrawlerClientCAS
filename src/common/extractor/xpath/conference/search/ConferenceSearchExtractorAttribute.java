package common.extractor.xpath.conference.search;

import java.util.List;

import org.w3c.dom.Node;

import common.bean.ConferenceData;
import common.extractor.ExtractorAttribute;
import common.siteinfo.Component;

public interface ConferenceSearchExtractorAttribute extends ExtractorAttribute<ConferenceData> {

	/**
	 * 摘要解析
	 * @param list
	 * @param dom
	 * @param component
	 * @param args
	 */
	public void parseBrief(List<ConferenceData> list, Node dom, Component component, String... args);

	/**
	 * 发布源解析
	 * @param list
	 * @param dom
	 * @param component
	 * @param args
	 */
	public void parseSource(List<ConferenceData> list, Node dom, Component component, String... args);
	/**
	 * 发布时间解析
	 * @param list
	 * @param dom
	 * @param component
	 * @param args
	 */
	public void parsePubtime(List<ConferenceData> list, Node dom, Component component, String... args);

	/**
	 * 解析博文作者
	 * @param list
	 * @param domtree
	 * @param component
	 * @param content
	 */
	public void parseAuthor(List<ConferenceData> list, Node domtree, Component component, String... content);
}
