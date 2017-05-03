package common.bean;

@SuppressWarnings("serial")
public class VideoData extends CommonData {

	private String authorUrl;//作者url
	private int playCount;//播放次数
	//http://comments.youku.com/comments/~ajax/vpcommentContent.html?__callback=vpcommentContent_html&__ap={"videoid":"400986325","showid":"0","isAjax":1,"sid":"","page":1,"chkpgc":0,"last_modify":""}
	private String commentUrl;//评论url
	private String tags;//标签
	private String author;//作者
	private String playtime;//播放时常
	private String channel;//
	private int likeCount;
	private int dislikeCount;
	
	public String getAuthorUrl() {
		return authorUrl;
	}
	public void setAuthorUrl(String authorUrl) {
		this.authorUrl = authorUrl;
	}
	public int getPlayCount() {
		return playCount;
	}
	public void setPlayCount(int playCount) {
		this.playCount = playCount;
	}
	public String getCommentUrl() {
		return commentUrl;
	}
	public void setCommentUrl(String commentUrl) {
		this.commentUrl = commentUrl;
	}
	public String getTags() {
		return tags;
	}
	public void setTags(String tags) {
		this.tags = tags;
	}
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	public String getPlaytime() {
		return playtime;
	}
	public void setPlaytime(String playtime) {
		this.playtime = playtime;
	}
	public String getChannel() {
		return channel;
	}
	public void setChannel(String channel) {
		this.channel = channel;
	}
	public int getLikeCount() {
		return likeCount;
	}
	public void setLikeCount(int likeCount) {
		this.likeCount = likeCount;
	}
	public int getDislikeCount() {
		return dislikeCount;
	}
	public void setDislikeCount(int dislikeCount) {
		this.dislikeCount = dislikeCount;
	}
}