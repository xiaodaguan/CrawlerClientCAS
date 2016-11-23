package common.rmi.packet;

import java.util.HashMap;
import java.util.Map;

/**
 * 新闻搜索，新闻垂直，论坛搜索，论坛垂直，博客搜索，博客垂直，微博搜索，微博垂直，视频搜索，视频垂直，学术搜索，学术垂直
 * 电商搜索，电商垂直，微信搜索，微信垂直，相同新闻，单条微博
 * <p/>
 * 专利搜索，报告搜索，基金搜索，电子报垂直
 *
 * @author grs
 */
public enum CrawlerType {
    NOTYPE(""),
    NEWS_SEARCH("1.1.1."), NEWS_MONITOR("2.1.1."),
    BBS_SEARCH("3.1.1."), BBS_MONITOR("4.1.1."),
    BLOG_SEARCH("5.1.1."), BLOG_MONITOR("6.1.1."),
    WEIBO_SEARCH("7.1.1."), WEIBO_MONITOR("8.1.1."),
    VIDEO_SEARCH("9.1.1."), VIDEO_MONITOR("10.1.1."),
    ACADEMIC_SEARCH("11.1.1."), ACADEMIC_MONITOR("12.1.1."),
    EBUSINESS_SEARCH("13.1.1."), EBUSINESS_MONITOR("14.1.1.1"),
    WEIXIN_SEARCH("15.1.1."), WEIXIN_MONITOR("16.1.1."),
    NEWS_SAME("17.1.1."), WEIBO_SINGLE("18.1.1."),

    PATENT_SEARCH("19.1.1."), EPAPER_MONITOR("20.1.1."),
    COMPANY_REPORT_SEARCH("21.1.1."), COMPANY_REPORT_MONITOR("22.1.1."),
    FUND_SEARCH("23.1.1."),

    WXPUBLIC_SEARCH("25.1.1"),

    AGRICALTURE_SEARCH("27.1.1."), AGRICALTURE_MONITOR("28.1.1."),
    CONFERENCE_SEARCH("29.1.1"), CONFERENCE_MONITOR("30,1,1"),
    PERSON_SEARCH("31,1,1"), PERSON_MONITOR("32,1,1"),
    COMPANY_SEARCH("33,1,1"), COMPANY_MONITOR("34,1,1"),
    
    GOVAFFAIR_SEARCH("37,1,1"), GOVAFFAIR_MONITOR("38,1,1"),
    PRESS_SEARCH("39,1,1"), 	PRESS_MONITOR("40,1,1"),
    ;

    private CrawlerType() {

    }

    private String code;

    private CrawlerType(String suffix) {
        this.code = suffix;
    }

    public String getCode() {
        return code;
    }

    public static CrawlerType getType(String type) {
        for (CrawlerType ct : CrawlerType.values()) {
            if (type.equalsIgnoreCase(ct.name())) {
                return ct;
            }
        }
        return NOTYPE;
    }

    private static Map<Integer, CrawlerType> crawlerTypeMap = new HashMap<Integer, CrawlerType>();

    static {
        crawlerTypeMap.put(0, NOTYPE);
        crawlerTypeMap.put(1, NEWS_SEARCH);
        crawlerTypeMap.put(2, NEWS_MONITOR);
        crawlerTypeMap.put(3, BBS_SEARCH);
        crawlerTypeMap.put(4, BBS_MONITOR);
        crawlerTypeMap.put(5, BLOG_SEARCH);
        crawlerTypeMap.put(6, BLOG_MONITOR);
        crawlerTypeMap.put(7, WEIBO_SEARCH);
        crawlerTypeMap.put(8, WEIBO_MONITOR);
        crawlerTypeMap.put(9, VIDEO_SEARCH);
        crawlerTypeMap.put(10, VIDEO_MONITOR);
        crawlerTypeMap.put(11, ACADEMIC_SEARCH);
        crawlerTypeMap.put(12, ACADEMIC_MONITOR);
        crawlerTypeMap.put(13, EBUSINESS_SEARCH);
        crawlerTypeMap.put(14, EBUSINESS_MONITOR);
        crawlerTypeMap.put(15, WEIXIN_SEARCH);
        crawlerTypeMap.put(16, WEIXIN_MONITOR);
        crawlerTypeMap.put(17, NEWS_SAME);//相同新闻
        crawlerTypeMap.put(18, WEIBO_SINGLE);//单条微博采集
        crawlerTypeMap.put(19, PATENT_SEARCH);
        crawlerTypeMap.put(20, EPAPER_MONITOR);
        crawlerTypeMap.put(21, COMPANY_REPORT_SEARCH);
        crawlerTypeMap.put(22, COMPANY_REPORT_MONITOR);
        crawlerTypeMap.put(23, FUND_SEARCH);
        crawlerTypeMap.put(25, WXPUBLIC_SEARCH);
        crawlerTypeMap.put(27, AGRICALTURE_SEARCH);
        crawlerTypeMap.put(28, AGRICALTURE_MONITOR);
        crawlerTypeMap.put(29, CONFERENCE_SEARCH);
        crawlerTypeMap.put(30, CONFERENCE_MONITOR);
        crawlerTypeMap.put(31, PERSON_SEARCH);
        crawlerTypeMap.put(32, PERSON_MONITOR);
        crawlerTypeMap.put(33, COMPANY_SEARCH);
        crawlerTypeMap.put(34, COMPANY_MONITOR);
        crawlerTypeMap.put(37, GOVAFFAIR_SEARCH);
        crawlerTypeMap.put(38, GOVAFFAIR_MONITOR);
        crawlerTypeMap.put(39, PRESS_SEARCH);
        crawlerTypeMap.put(40, PRESS_MONITOR);
    }

    public static Map<Integer, CrawlerType> getCrawlerTypeMap() { return crawlerTypeMap; }
}
