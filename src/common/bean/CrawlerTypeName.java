package common.bean;

import java.util.HashMap;
import java.util.Map;

public class CrawlerTypeName {
	public static Map<Integer, String> map = new HashMap<Integer, String>();
	static {
		map.put(0, "NOTYPE");
		map.put(1, "NEWS_SEARCH");
		map.put(2, "NEWS_MONITOR");
		map.put(3, "BBS_SEARCH");
		map.put(4, "BBS_MONITOR");
		map.put(5, "BLOG_SEARCH");
		map.put(6, "BLOG_MONITOR");
		map.put(7, "WEIBO_SEARCH");
		map.put(8, "WEIBO_MONITOR");
		map.put(9, "VIDEO_SEARCH");
		map.put(10, "VIDEO_MONITOR");
		map.put(11, "ACADEMIC_SEARCH");
		map.put(12, "ACADEMIC_MONITOR");
		map.put(13, "EBUSINESS_SEARCH");
		map.put(14, "EBUSINESS_MONITOR");
		map.put(15, "WEIXIN_SEARCH");
		map.put(16, "WEIXIN_MONITOR");
		map.put(17, "NEWS_SAME");// 相同新闻
		map.put(18, "WEIBO_SINGLE");// 单条微博采集
		map.put(19, "PATENT_SEARCH");
		map.put(20, "EPAPER_MONITOR");
		map.put(21, "COMPANY_REPORT_SEARCH");
		map.put(22, "COMPANY_REPORT_MONITOR");
		map.put(23, "FUND_SEARCH");
		
		map.put(37, "GOVAFFAIR_SEARCH");
		map.put(38, "GOVAFFAIR_MONITOR");
		map.put(39, "PRESS_SEARCH");
		map.put(40, "PRESS_MONITOR");
		map.put(39, "FRGMEDIA_SEARCH");
		map.put(40, "FRGMEDIA_MONITOR");
	}
}
