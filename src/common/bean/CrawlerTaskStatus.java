package common.bean;

import java.util.Date;
import java.util.HashMap;

/**
 * meta or data download, 对应一个关键词+一个站点
 * 
 * @author guanxiaoda
 *
 */
public class CrawlerTaskStatus {
	private int dbID;// 数据库中id

	public int getDbID() {
		return dbID;
	}

	public void setDbID(int dbID) {
		this.dbID = dbID;
	}

	private String id;// 关键词+空格+站点+空格+开始时间
	private Date startTime;
	private boolean ifCrawled;// 是否已经采集
	private String keyword;
	private int fetchCount;// 检索到的条数
	private int downCount;// 采集的条数
	private int savedCount;// 入库条数
	private int threadNum;
	private int interval;

	private String status;// 状态

	public CrawlerTaskStatus(String id, Date startTime, String keyword, int interval, int threadNum, boolean ifCrawled, int fetchCount,
			int downCount, int savedCount, String status) {
		this.id = id;
		this.startTime = startTime;
		this.keyword = keyword;
		this.interval = interval;
		this.threadNum = threadNum;
		this.ifCrawled = ifCrawled;
		this.fetchCount = fetchCount;
		this.downCount = downCount;
		this.savedCount = savedCount;
		this.status = status;
	}

	public int getThreadNum() {
		return threadNum;
	}

	public void setThreadNum(int threadNum) {
		this.threadNum = threadNum;
	}

	public int getInterval() {
		return interval;
	}

	public void setInterval(int interval) {
		this.interval = interval;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public boolean isIfCrawled() {
		return ifCrawled;
	}

	public void setIfCrawled(boolean ifCrawled) {
		this.ifCrawled = ifCrawled;
	}

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	public int getFetchCount() {
		return fetchCount;
	}

	public void setFetchCount(int fetchCount) {
		this.fetchCount = fetchCount;
	}

	public int getDownCount() {
		return downCount;
	}

	public void setDownCount(int downCount) {
		this.downCount = downCount;
	}

	public int getSavedCount() {
		return savedCount;
	}

	public void setSavedCount(int savedCount) {
		this.savedCount = savedCount;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

}
