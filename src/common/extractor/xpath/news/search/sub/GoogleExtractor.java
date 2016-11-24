package common.extractor.xpath.news.search.sub;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import common.bean.p;
import common.extractor.xpath.news.search.NewsSearchXpathExtractor;
import common.siteinfo.Component;
import common.util.StringUtil;

public class GoogleExtractor extends NewsSearchXpathExtractor {

	@Override
	public void parseSource(List<p> list, Node dom, Component component,
			String... strings) {
		if(component==null) return;
		NodeList nl = head(component.getXpath(), dom, list.size(), component.getName());
		if(nl==null) return;
		for(int i = 0;i < nl.getLength();i++) {
			String s = nl.item(i).getTextContent();
			if(s.indexOf("-") > -1) {
				String[] arr = s.split("-");
				list.get(i).setPubtime(arr[1].trim());
				list.get(i).setPubdate(timeprocess(list.get(i).getPubtime()));
				s = arr[0];
			}
			list.get(i).setSource(s.trim());
		}
	}

	@Override
	public void parsePubtime(List<p> list, Node dom,
			Component component, String... args) {
		if(component==null) return;
		NodeList nl = head(component.getXpath(), dom, list.size(), component.getName());
		if(nl==null) return;
		for(int i = 0;i < nl.getLength();i++) {
			list.get(i).setPubtime(nl.item(i).getTextContent());
			list.get(i).setPubdate(timeprocess(nl.item(i).getTextContent().trim()));
		}
	}
	
	private Date timeprocess(String s) {
		Calendar c = Calendar.getInstance();
		int num = Integer.parseInt(StringUtil.extractMulti(s, "\\d"));
		if(s.contains("minute")|| s.contains("分钟前")) {
			c.set(Calendar.MINUTE, c.get(Calendar.MINUTE)-num);
		} else if(s.contains("hour") || s.contains("小时前")) {
			c.set(Calendar.HOUR, c.get(Calendar.HOUR)-num);
		} else if(s.contains("day") || s.contains("天前")) {
			c.set(Calendar.DAY_OF_MONTH, c.get(Calendar.DAY_OF_MONTH)-num);
		} else if(s.contains("month") || s.contains("月前")) {
			c.set(Calendar.MONTH, c.get(Calendar.MONTH)-num);
		} else if(s.contains("year") || s.contains("年前")) {
			c.set(Calendar.YEAR, c.get(Calendar.YEAR)-num);
		} else if(s.contains("second") || s.contains("秒前")) {
			c.set(Calendar.SECOND, c.get(Calendar.SECOND)-num);
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

	
}
