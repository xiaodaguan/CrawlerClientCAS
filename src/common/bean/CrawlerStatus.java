package common.bean;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * job开始时
 * 
 * @author guanxiaoda
 *
 */
public class CrawlerStatus {

	private int id;//数据库中的id
	
	/** 爬虫启动时初始化，全局唯一 */
	private String crawlerName;
	private int crawlerType;
	private Date startTime;

	private String ip;
	private HashMap<String, CrawlerTaskStatus> tasks;// 该爬虫采集的全部任务，id：关键词+空格+站点+空格+开始时间
	private List<String> startKeywordSet;// 初始关键词集合，即全部要采集的关键词
	private String status;// 状态: FETCHING, DOWNLOADING, COMPLETE, INIT

	
	/**
	 * 获取已采集的关键词数
	 * @return
	 */
	public int getCrawedCount(){
		int count=0;
		for (String taskName : tasks.keySet()) {
			if (tasks.get(taskName).isIfCrawled())
				count++;
		}
		return count;
	}
	
	/**
	 * 是否全部任务采集完成
	 * @return
	 */
	public boolean allCrawled() {
		for (String taskName : tasks.keySet()) {
			if (!tasks.get(taskName).isIfCrawled())
				return false;
		}
		return true;
	}

	
	/**
	 * 初始化
	 * @param crawlerName
	 * @param crawlerType
	 * @param startTime
	 * @param ip
	 * @param status
	 */
	public CrawlerStatus(String crawlerName, int crawlerType, Date startTime, String ip, String status) {
		this.crawlerName = crawlerName;
		this.crawlerType = crawlerType;
		this.startTime = startTime;
		this.ip = ip;
		this.status = status;
		tasks = new HashMap<String, CrawlerTaskStatus>();
	}

	/**
	 * 获取剩余所有task的keyword
	 * @return
	 */
	public List<String> getAllKeywords() {

		List<String> keywords = new ArrayList<String>();
		for (String taskName : tasks.keySet()) {
			keywords.add(tasks.get(taskName).getKeyword());
		}
		return keywords;
	}

	
	
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public List<String> getStartKeywordSet() {
		return startKeywordSet;
	}

	public void setStartKeywordSet(List<String> startKeywordSet) {
		this.startKeywordSet = startKeywordSet;
	}

	public HashMap<String, CrawlerTaskStatus> getTasks() {
		return tasks;
	}

	public void setTasks(HashMap<String, CrawlerTaskStatus> tasks) {
		this.tasks = tasks;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getCrawlerName() {
		return crawlerName;
	}

	public void setCrawlerName(String crawlerName) {
		this.crawlerName = crawlerName;
	}

	public int getCrawlerType() {
		return crawlerType;
	}

	public void setCrawlerType(int crawlerType) {
		this.crawlerType = crawlerType;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

}
