package common.extractor.xpath.video.search;

import java.util.List;

import org.w3c.dom.Node;

import common.bean.AgricaltureData;
import common.bean.VideoData;
import common.extractor.ExtractorAttribute;
import common.siteinfo.Component;

public interface VideoSearchExtractorAttribute extends ExtractorAttribute<VideoData> {
	/**
	 * 解析内容
	 * @param list
	 * @param dom
	 * @param component
	 * @param args
	 */
	
	public void parseContent(VideoData data, Node dom, Component component, String... args);

	
//	protected String md5;
	public void parseMd5(VideoData data, Node dom, Component component, String... args);

//	protected int categoryCode;
//	protected Date pubdate;
	public void parsePubdate(List<VideoData> list, Node dom, Component component, String... args); 

//	protected Date inserttime;
//	protected String content;
//	protected int siteId;
//	protected String searchKey;
	
//	protected String url;
	public void parseUrl(List<VideoData> list, Node dom, Component component, String... args);

//	protected String title;
	public void parseTitle(List<VideoData> list, Node dom, Component component, String... args);


//	protected String completeSize;
//	private String authorUrl;//作者url
	public void parseAuthorUrl(List<VideoData> list, Node dom, Component component, String... args);

//	private int playCount;//播放次数
	public void parsePlayCount(List<VideoData> list, Node dom, Component component, String... args);

//	//http://comments.youku.com/comments/~ajax/vpcommentContent.html?__callback=vpcommentContent_html&__ap={"videoid":"400986325","showid":"0","isAjax":1,"sid":"","page":1,"chkpgc":0,"last_modify":""}
//	private String commentUrl;//评论url
	public void parseCommentUrl(List<VideoData> list, Node dom, Component component, String... args);

//	private String tags;//标签
	public void parseTags(List<VideoData> list, Node dom, Component component, String... args);

//	private String author;//作者
	public void parseAuthor(List<VideoData> list, Node dom, Component component, String... args);

//	private String playtime;//播放时常
	public void parsePlaytime(List<VideoData> list, Node dom, Component component, String... args);

//	private String channel;//
	public void parseChannel(List<VideoData> list, Node dom, Component component, String... args);

//	private int likeCount;
	public void parseLikeCount(List<VideoData> list, Node dom, Component component, String... args);

//	private int dislikeCount;
	public void parseDislikeCount(List<VideoData> list, Node dom, Component component, String... args);

}
