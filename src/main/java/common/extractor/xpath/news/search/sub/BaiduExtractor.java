package common.extractor.xpath.news.search.sub;

import common.pojos.NewsData;
import common.extractor.xpath.news.search.NewsSearchXpathExtractor;
import common.siteinfo.Component;
import common.util.StringUtil;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
/**
 * 百度新闻搜索特殊属性抽取
 * @author grs
 *
 */
public class BaiduExtractor extends NewsSearchXpathExtractor {

	@Override
	public void parseBrief(List<NewsData> list, Node dom, Component component,
			String... args) {
		if(component==null) return;
		NodeList nl = head(component.getXpath(), dom, list.size(), component.getName());
		if(nl==null) return;
		String url = "";
		for(int i = 0;i < nl.getLength();i++) {
			url = nl.item(i).getTextContent().replace("百度快照", "").replace("条相同新闻 -", "");
			list.get(i).setBrief(url);
		}
	}
	
	@Override
	public void parseSameurl(List<NewsData> list, Node dom,
			Component component, String... args) {
		if(component == null) return;
		for(int i = 0;i < list.size();i++) {
			String xpath=component.getXpath().replace("index",(i+1)+"");
			NodeList nl = commonList(xpath, dom);
			if(nl.item(0)!=null) {
				list.get(i).setSameUrl(urlProcess(component, nl.item(0)));
			}
		}
	}
	@Override
	public void parsePubtime(List<NewsData> list, Node dom, Component component, String... args) {
		if(component==null) return;
		NodeList nl = head(component.getXpath(), dom, list.size(), component.getName());
		if(nl==null) return;
		String url = "";
		for(int i = 0;i < nl.getLength();i++) {
			url = nl.item(i).getTextContent().replace("年", "-").replace("月", "-").replace("日", "");
			
			String time = StringUtil.extractMulti(url, "\\d{4}-\\d{1,2}-\\d{1,2}\\s*\\d{1,2}:\\d{1,2}");
			if(time.equals(""))
				time=url.split("  ")[1];
			list.get(i).setPubtime(time);
			list.get(i).setPubdate(timeprocess(time));
			list.get(i).setSource(StringUtil.format(nl.item(i).getTextContent().split("  ")[0]));
		}
	}
	
	/**
	 * 时间转换
	 * @param s
	 * @return
	 */
	private Date timeprocess(String s) {
		Calendar c = Calendar.getInstance();
		long num = Long.parseLong(StringUtil.extractMulti(s, "\\d"));
		if(s.contains("minute")|| s.contains("分钟前")) {
			c.set(Calendar.MINUTE, c.get(Calendar.MINUTE)-(int)num);
		} else if(s.contains("hour") || s.contains("小时前")) {
			c.set(Calendar.HOUR, c.get(Calendar.HOUR)-(int)num);
		} else if(s.contains("day") || s.contains("天前")) {
			c.set(Calendar.DAY_OF_MONTH, c.get(Calendar.DAY_OF_MONTH)-(int)num);
		} else if(s.contains("month") || s.contains("月前")) {
			c.set(Calendar.MONTH, c.get(Calendar.MONTH)-(int)num);
		} else if(s.contains("year") || s.contains("年前")) {
			c.set(Calendar.YEAR, c.get(Calendar.YEAR)-(int)num);
		} else if(s.contains("second") || s.contains("秒前")) {
			c.set(Calendar.SECOND, c.get(Calendar.SECOND)-(int)num);
		} else if(s.indexOf("年") >-1 && s.indexOf("月")>-1 && s.indexOf("日")>-1) {
			s = StringUtil.format(s.replace("年", "-").replace("月", "-").replace("日", " "));
			return timeProcess(s);//父类的时间处理
		} else if(s.indexOf(",")>-1) {
			String[] time = s.split(",");
			String t = "";
			if(time.length>1) {
				String[] md = time[0].split(" ");
				if(md.length>1)
					t = time[1]+"-"+ month(md[0])+"-"+md[1];
			}
			return timeProcess(t);//父类的时间处理
		}
		else {
			return timeProcess(s);//父类的时间处理
		}
		return c.getTime();
	}
	 private String month(String s) {
			s = s.toLowerCase();
			if(s.contains("dec")) {
				return "12";
			} else if(s.contains("nov")) {
				return "11";
			} else if(s.contains("oct")) {
				return "10";
			} else if(s.contains("sep")) {
				return "9";
			} else if(s.contains("aug")) {
				return "8";
			} else if(s.contains("jul")) {
				return "7";
			} else if(s.contains("jun")) {
				return "6";
			} else if(s.contains("may")) {
				return "5";
			} else if(s.contains("apr")) {
				return "4";
			} else if(s.contains("mar")) {
				return "3";
			} else if(s.contains("feb")) {
				return "2";
			} else if(s.contains("jan")) {
				return "1";
			} 
			return "1";
		}
	@Override
	public void parseSource(List<NewsData> list, Node dom, Component component, String... strings) {
//		if(component==null) return;
//		NodeList nl = head(component.getXpath(), dom, list.size(), component.getName());
//		if(nl==null) return;
//		String str = "";
//		for(int i = 0;i < nl.getLength();i++) {
//			str = nl.item(i).getTextContent();
//			if(str.indexOf("2")>0){
//            	str = str.substring(0, str.indexOf("2"));   	
//        	}
//			list.get(i).setSource(StringUtil.format(str.replace(" ", "")));
//		}
	}
	
	@Override
	public void parseSamenum(List<NewsData> list, Node dom,
		Component component, String... args) {
		if(component == null) return;
		for(int i = 0;i < list.size();i++) {
			String xpath=component.getXpath().replace("index",(i+1)+"");
			NodeList nl = commonList(xpath, dom);
			if(nl.item(0)!=null) {
				String s = StringUtil.extractMulti(nl.item(0).getTextContent(), "\\d+");
				list.get(i).setSamenum(Integer.parseInt(s==null?"0":s));
			}
		}
	}
	
}
