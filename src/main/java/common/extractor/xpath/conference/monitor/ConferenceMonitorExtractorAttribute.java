package common.extractor.xpath.conference.monitor;

import java.util.List;

import org.w3c.dom.Node;

import common.pojos.ConferenceData;
import common.extractor.ExtractorAttribute;
import common.siteinfo.Component;

public interface ConferenceMonitorExtractorAttribute extends ExtractorAttribute<ConferenceData> {

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
	 * 解析作者
	 * @param list
	 * @param domtree
	 * @param component
	 * @param content
	 */
	public void parseAuthor(List<ConferenceData> list, Node domtree, Component component, String... args);/**
	 * 解析会议地点
	 * @param list
	 * @param domtree
	 * @param component
	 * @param content
	 */
	public void parsePlace(List<ConferenceData> list, Node domtree, Component component, String... args);
	/**
	 * 解析会议正文，若无简介，同时将正文赋给简介
	 * @param data
	 * @param domtree
	 * @param component
	 * @param args
	 */
	void parseContent(ConferenceData data, Node domtree, Component component, String[] args);
}
