package common.extractor.xpath.video.search.sub;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import common.bean.VideoData;
import common.extractor.xpath.video.search.VideoSearchExtractorAttribute;
import common.extractor.xpath.video.search.VideoSearchXpathExtractor;
import common.siteinfo.Component;
import common.util.DOMUtil;
import net.sf.json.JSONObject;

/**
 * 搜酷
 * 
 * @author rzy
 */
public class SokuVideoSearchXpathExtractor extends VideoSearchXpathExtractor implements VideoSearchExtractorAttribute {
	
	@Override
	public String parseNext(Node dom, Component component, String... args) {
		String pageUrl = args[0].split("page=")[0];
		int pageNum = Integer.parseInt(args[1]);
		return pageUrl+"page="+(pageNum+1);
	}
	private String decodeUnicode(String str) {
		Charset set = Charset.forName("UTF-16");
		Pattern p = Pattern.compile("\\\\u([0-9a-fA-F]{4})");
		Matcher m = p.matcher(str);
		int start = 0;
		int start2 = 0;
		StringBuffer sb = new StringBuffer();
		while (m.find(start)) {
			start2 = m.start();
			if (start2 > start) {
				String seg = str.substring(start, start2);
				sb.append(seg);
			}
			String code = m.group(1);
			int i = Integer.valueOf(code, 16);
			byte[] bb = new byte[4];
			bb[0] = (byte) ((i >> 8) & 0xFF);
			bb[1] = (byte) (i & 0xFF);
			ByteBuffer b = ByteBuffer.wrap(bb);
			sb.append(String.valueOf(set.decode(b)).trim());
			start = m.end();
		}
		start2 = str.length();
		if (start2 > start) {
			String seg = str.substring(start, start2);
			sb.append(seg);
		}
		return sb.toString();
	}
	private String getHtmlContent(String url, String param) {

		HttpClient client = new HttpClient();
		
		GetMethod getMethod = new GetMethod(url);
		//String cookie = "SINAGLOBAL=9283362622372.807.1472706398371; un=13582688545; wvr=6; TC-Page-G0=4c4b51307dd4a2e262171871fe64f295; SCF=AhJy3b4rgQhnHF-HCAdDQmxuNLGdGPXe6Fh89BnNIXCDaGPdR4ZUuEyANPhXW1VYF4rGmvEPfPO_57mUswY-KmU.; SUB=_2A2564Pr5DeTxGeNH7FEZ9yvOyT2IHXVZlGsxrDV8PUNbmtANLXGhkW9tE7cBcTs7y7WvkCg1Ketku3gNMg..; SUBP=0033WrSXqPxfM725Ws9jqgMF55529P9D9WWeGpHGUYqImqyd9yXpEySQ5JpX5KMhUgL.Fo-4S0eRS0-Eeo22dJLoI0qLxKqL1h-L1K-LxK-LB--LBo.LxK-L1K2L1hqLxK-L1KqL1hBLxK-L1-zLBKzLxKML1-zLB--t; SUHB=0jB4qJL3yLgqum; ALF=1506131497; SSOLoginState=1474595497; YF-V5-G0=a5a264208a5b5a42590274f52e6c7304; YF-Ugrow-G0=ea90f703b7694b74b62d38420b5273df; _s_tentry=login.sina.com.cn; Apache=9566982616670.43.1474595501713; ULV=1474595501775:7:7:4:9566982616670.43.1474595501713:1474335714920; UOR=www.vegnet.com.cn,widget.weibo.com,login.sina.com.cn; YF-Page-G0=27b9c6f0942dad1bd65a7d61efdfa013";
		
		String User_Agent = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/45.0.2454.101 Safari/537.36";
		//SystemCommon.User_Agent;
				//"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/55.0.2859.0 Safari/537.36";
		//getMethod.getParams().setCookiePolicy(CookiePolicy.BROWSER_COMPATIBILITY);
		//getMethod.setRequestHeader("Cookie", SystemCommon.currentCookie);
		getMethod.setRequestHeader("Accept", "*/*");
		getMethod.setRequestHeader("Connection", "keep-alive");
		getMethod.setRequestHeader("User-Agent", User_Agent);

		StringBuffer sb = new StringBuffer();
		try {
			int statusCode = client.executeMethod(getMethod);
			//System.out.println("statusCode:" + statusCode);
			if (statusCode != HttpStatus.SC_OK) {
				System.err.println("Method failed:" + getMethod.getStatusLine());
				return null;
			}
			BufferedReader br = new BufferedReader(
					new InputStreamReader(getMethod.getResponseBodyAsStream(), "utf-8"));//gb2312
			
			for (String line = br.readLine(); line != null; line = br.readLine()) {
				line = decodeUnicode(line);
				sb.append(line+"\n");
			}
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(sb.toString());
		}
		return sb.toString();
	}
	@SuppressWarnings("deprecation")
	@Override
	public void parseChannel(List<VideoData> list, Node dom, Component component, String... args) {
	
		for (VideoData data : list) {
			
			String url = data.getUrl();
			System.out.println(url);
			String content = this.getHtmlContent(url,null);
			//System.out.println("content1："+content);
			try {
				Node node = (new DOMUtil()).ini(content,null);
				String xpath = "//H1[@class='title']/A";
				NodeList nl = commonList(xpath, node);
				
				if(nl.getLength()>0){
					String channel = nl.item(0).getTextContent();
					data.setChannel(channel);
				}
			} catch (SAXException | IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			String vid = content.split("videoId:\"")[1].split("\",")[0];
			String showid = content.split("showid:\"")[1].split("\",")[0];
			long timestamp = System.currentTimeMillis();
			String updownUrl = "http://v.youku.com/action/getVideoPlayInfo?beta&"
					+ "timestamp=<timestamp>&"
					+ "vid=<vid>&"
					+ "showid=<showid>&param%5B%5D=share&param%5B%5D=favo&param%5B%5D=download&param%5B%5D=phonewatch&param%5B%5D=updown&callback=tuijsonp2";
			String realUpdownUrl = updownUrl.replace("<timestamp>", timestamp+"").
					replace("<vid>", vid).replace("<showid>", showid);
			content = this.getHtmlContent(realUpdownUrl,null);
			System.out.println("JSONObject updownUrl:"+content);
			content = content.split("\\(")[1];
			JSONObject obj = JSONObject.fromObject(content).getJSONObject("data").getJSONObject("updown");
			String up = obj.getString("up").replace(",", "");
			String down = obj.getString("down").replace(",", "");
			data.setLikeCount(Integer.parseInt(up));
			data.setDislikeCount(Integer.parseInt(down));
			String commentUrl = "http://comments.youku.com/comments/~ajax/vpcommentContent.html?"
					+ "__callback=vpcommentContent_html&__ap={"
					+ "\"videoid\":\"<videoid>\","
					+ "\"showid\":\"<showid>\",\"isAjax\":1,\"sid\":\"\",\"page\":1,\"chkpgc\":0,\"last_modify\":\"\"}";
			String realCommentUrl = commentUrl.replace("<vid>", vid).replace("<showid>", showid);;
			try {
				//realCommentUrl = URLEncoder.encode(realCommentUrl, "UTF-8");
				System.out.println("URLEncoder : "+realCommentUrl);
				data.setCommentUrl(realCommentUrl);
			} catch (Exception e1){
				e1.printStackTrace();
			}
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	@Override
	public void parsePubdate(List<VideoData> list, Node dom, Component component, String... args) {
		if(component==null) return;
		NodeList nl = commonList(component.getXpath(), dom);
		if(nl==null) return;
		for(int i = 0;i < nl.getLength();i++) {
			VideoData vd = list.get(i);
			String s = nl.item(i).getTextContent();
			Calendar calendar = Calendar.getInstance();  
			//System.out.println(x);
			if(s.contains("年前")){
				int year = Integer.parseInt(s.split("年前")[0]);
				calendar.add(Calendar.YEAR, -year);
			}else if(s.contains("月前")){
				int month = Integer.parseInt(s.split("月前")[0]);
				calendar.add(Calendar.MONTH, -month);
			}else if(s.contains("天前")){
				int day = Integer.parseInt(s.split("天前")[0]);
				calendar.add(Calendar.DAY_OF_YEAR, -day);
			}else if(s.contains("小时前")){
				int hour = Integer.parseInt(s.split("小时前")[0]);
				calendar.add(Calendar.HOUR, -hour);
			}else if(s.contains("分钟前")){
				int minute = Integer.parseInt(s.split("小时前")[0]);
				calendar.add(Calendar.MINUTE, -minute);
			}
			vd.setPubdate(calendar.getTime());
		}
	}
	
//	@Override
//	public void parseLikeCount(List<VideoData> list, Node dom, Component component, String... args) {
//		if(component==null) return;
//		NodeList nl = commonList(component.getXpath(), dom);
//		if(nl==null) return;
//		for(int i = 0;i < nl.getLength();i++) {
//			VideoData vd = list.get(i);
//			vd.setLikeCount(Integer.parseInt(StringUtil.format(nl.item(i).getTextContent())));
//		}
//	}
//	@Override
//	public void parseDislikeCount(List<VideoData> list, Node dom, Component component, String... args) {
//		if(component==null) return;
//		NodeList nl = commonList(component.getXpath(), dom);
//		if(nl==null) return;
//		for(int i = 0;i < nl.getLength();i++) {
//			VideoData vd = list.get(i);
//			vd.setDislikeCount(Integer.parseInt(StringUtil.format(nl.item(i).getTextContent())));
//		}
//	}
}
