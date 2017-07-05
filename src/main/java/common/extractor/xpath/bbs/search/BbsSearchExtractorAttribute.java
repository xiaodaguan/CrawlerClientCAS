package common.extractor.xpath.bbs.search;

import java.util.List;

import org.w3c.dom.Node;

import common.pojos.BbsData;
import common.pojos.ReplyData;
import common.extractor.ExtractorAttribute;
import common.siteinfo.Component;

public interface BbsSearchExtractorAttribute extends ExtractorAttribute<BbsData> {
	/**
	 * 内容解析
	 * @param list
	 * @param dom
	 * @param component
	 * @param args
	 */
	public void parseContent(BbsData data, Node dom, Component component, String... args);

	/**
	 * 作者解析
	 * @param list
	 * @param dom
	 * @param component
	 * @param args
	 */
	public void parseAuthor(BbsData data, Node dom, Component component, String... args);
	/**
	 * 发布时间解析
	 * @param list
	 * @param dom
	 * @param component
	 * @param args
	 */
	public void parsePubtime(BbsData data, Node dom, Component component, String... args);
	/**
	 * 解析点击次数
	 * @param data
	 * @param domtree
	 * @param component
	 * @param args
	 */
	public void parseClickCount(BbsData data, Node domtree, Component component, String... ags);

	/**
	 * 解析来源
	 * @param data
	 * @param domtree
	 * @param component
	 * @param args
	 */
	public void parseSource(BbsData data, Node domtree, Component component,
                            String... args);
	/**
	 * 解析回复数量
	 * @param data
	 * @param domtree
	 * @param component
	 * @param args
	 */
	public void parseReplyCount(BbsData data, Node domtree,
                                Component component, String... ags);
	/**
	 * 解析板块
	 * @param data
	 * @param domtree
	 * @param component
	 * @param args
	 */
	public void parseColumn(BbsData data, Node domtree, Component component,
                            String... ags);
	/**
	 * 解析图片链接
	 * @param data
	 * @param domtree
	 * @param component
	 * @param args
	 */
	public void parseImgUrl(BbsData data, Node domtree, Component component,
                            String... args);
	/**
	 * 解析标题
	 * @param data
	 * @param domtree
	 * @param component
	 * @param args
	 */
	public void parsePageTitle(BbsData data, Node domtree, Component component,
                               String... args);

	/**
	 * 解析回复作者
	 * @param list
	 * @param domtree
	 * @param component
	 * @param strings
	 */
	public void parseReplyname(List<ReplyData> list, Node domtree,
			Component component, String... args);
	/**
	 * 解析回复时间
	 * @param list
	 * @param domtree
	 * @param component
	 * @param strings
	 */
	public void parseReplytime(List<ReplyData> list, Node domtree,
			Component component, String... args);
	/**
	 * 解析回复内容
	 * @param list
	 * @param domtree
	 * @param component
	 * @param strings
	 */
	public void parseReplycontent(List<ReplyData> list, Node domtree,
			Component component, String... args);
	/**
	 * 解析下一页回复链接
	 * @param list
	 * @param domtree
	 * @param component
	 * @param strings
	 */
	public String parseReplyNext(Node domtree, Component component);
	
}
