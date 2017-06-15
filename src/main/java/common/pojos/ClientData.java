package common.pojos;

import java.util.List;

@SuppressWarnings("serial")
public class ClientData extends CommonData {

	private String author;
	private String brief;
	private String source;
	private String sourceUrl;
	private String column;
	private int clickCount;
	private int likeCount;
	private int dislikeCount;
	private int replyCount;
	private String pubtime;
	private String content;
	private String imgUrl;
	private List<ReplyData> replyList;
	
	
	public String getSourceUrl() {
		return sourceUrl;
	}
	public void setSourceUrl(String sourceUrl) {
		this.sourceUrl = sourceUrl;
	}
	public int getDislikeCount() {
		return dislikeCount;
	}
	public void setDislikeCount(int dislikeCount) {
		this.dislikeCount = dislikeCount;
	}
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public int getLikeCount() {
		return likeCount;
	}
	public void setLikeCount(int likeCount) {
		this.likeCount = likeCount;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	
	public int getReplyCount() {
		return replyCount;
	}
	public void setReplyCount(int replyCount) {
		this.replyCount = replyCount;
	}
	
	public String getImgUrl() {
		return imgUrl;
	}
	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}
	public List<ReplyData> getReplyList() {
		return replyList;
	}
	public void setReplyList(List<ReplyData> replyList) {
		this.replyList = replyList;
	}
	public String getBrief() {
		return brief;
	}
	public void setBrief(String brief) {
		this.brief = brief;
	}
	public void setPubtime(String time) {
		this.pubtime = time;
	}
	public String getPubtime() {
		return pubtime;
	}
	public String getColumn() {
		return column;
	}
	public void setColumn(String column) {
		this.column = column;
	}
	public int getClickCount() {
		return clickCount;
	}
	public void setClickCount(int clickCount) {
		this.clickCount = clickCount;
	}

}
