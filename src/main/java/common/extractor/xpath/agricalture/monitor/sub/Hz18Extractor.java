package common.extractor.xpath.agricalture.monitor.sub;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.transform.TransformerException;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.xpath.XPathAPI;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import common.pojos.AgricaltureData;
import common.pojos.CrawlTask;
import common.extractor.xpath.agricalture.monitor.AgricaltureMonitorExtractorAttribute;
import common.extractor.xpath.agricalture.monitor.AgricaltureMonitorXpathExtractor;
import common.siteinfo.Component;
import common.utils.StringUtil;

public class Hz18Extractor extends AgricaltureMonitorXpathExtractor implements AgricaltureMonitorExtractorAttribute {
	@Override public String parseNext(Node domtree, Component component, CrawlTask html, String... args) throws TransformerException {
		if (html.getResponseCookie() == null || html.getResponseCookie().equals(""))
			return null;
		// html.setCookie(html.getResponseCookie());
		// String url = args[0];
		String page = args[1];

		String x__EVENTARGUMENT = "//INPUT[@type='hidden' and @name='__EVENTARGUMENT']/@value";
		String x__VIEWSTATE = "//INPUT[@type='hidden' and @name='__VIEWSTATE']/@value";
		String x__EVENTVALIDATION = "//INPUT[@type='hidden' and @name='__EVENTVALIDATION']/@value";
		String x__VIEWSTATEGENERATOR = "//INPUT[@type='hidden' and @name='__VIEWSTATEGENERATOR']/@value";

		Node n__EVENTARGUMENT = XPathAPI.selectSingleNode(domtree, x__EVENTARGUMENT);
		Node n__VIEWSTATE = XPathAPI.selectSingleNode(domtree, x__VIEWSTATE);
		Node n__EVENTVALIDATION = XPathAPI.selectSingleNode(domtree, x__EVENTVALIDATION);
		Node n__VIEWSTATEGENERATOR = XPathAPI.selectSingleNode(domtree, x__VIEWSTATEGENERATOR);

		String __EVENTTARGET = "dgPrice$ctl24$ctl0" + page;
		String __EVENTARGUMENT = "";
		String __VIEWSTATE = "";
		String __EVENTVALIDATION = "";
		String __VIEWSTATEGENERATOR = "";
		if (n__EVENTARGUMENT != null)
			__EVENTARGUMENT = n__EVENTARGUMENT.getTextContent();
		if (n__VIEWSTATE != null)
			__VIEWSTATE = n__VIEWSTATE.getTextContent();
		if (n__EVENTVALIDATION != null)
			__EVENTVALIDATION = n__EVENTVALIDATION.getTextContent();
		if (n__VIEWSTATEGENERATOR != null)
			__VIEWSTATEGENERATOR = n__VIEWSTATEGENERATOR.getTextContent();

		List<NameValuePair> body = new ArrayList<NameValuePair>();
		body.add(new BasicNameValuePair("__EVENTTARGET", __EVENTTARGET));
		body.add(new BasicNameValuePair("__EVENTARGUMENT", __EVENTARGUMENT));
		body.add(new BasicNameValuePair("__VIEWSTATE", __VIEWSTATE));
		body.add(new BasicNameValuePair("__EVENTVALIDATION", __EVENTVALIDATION));
		body.add(new BasicNameValuePair("__VIEWSTATEGENERATOR", __VIEWSTATEGENERATOR));
		// Top11%24txtLoginName=&
		// Top11%24txtPassword=&
		// Top11%24Search_in1%24lstFindType=1&
		// Top11%24Search_in1%24txtFind=
		body.add(new BasicNameValuePair("Top11%24txtLoginName", ""));
		body.add(new BasicNameValuePair("Top11%24txtPassword", ""));
		body.add(new BasicNameValuePair("Top11%24Search_in1%24lstFindType", "1"));
		body.add(new BasicNameValuePair("Top11%24Search_in1%24txtFind", ""));

		HttpPost post = new HttpPost("http://www.hz18.com/Price.aspx");
		post.setHeader("Cookie", html.getResponseCookie());
		// post.setHeader("Host","www.hz18.com");
		// post.setHeader("Connection","keep-alive");
		// post.setHeader("Pragma","no-cache");
		// post.setHeader("Cache-Control","no-cache");
		 post.setHeader("Origin","http://www.hz18.com");
		 post.setHeader("Upgrade-Insecure-Requests","1");
		post.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/46.0.2490.86 Safari/537.36");
		post.setHeader("Content-Type", "application/x-www-form-urlencoded");
		post.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
		post.setHeader("X-FirePHP-Version", "0.0.6");
		post.setHeader("Referer", "http://www.hz18.com/Price.aspx");
		// post.setHeader("Accept-Encoding","gzip, deflate");
		// post.setHeader("Accept-Language","zh-CN,zh;q=0.8,en;q=0.6");
		try {
			post.setEntity(new UrlEncodedFormEntity(body));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		HttpClient client = new DefaultHttpClient();

		try {
			HttpResponse response = client.execute(post);
			// Header[] cookies = response.getHeaders("Set-Cookie");
			InputStream is = response.getEntity().getContent();
			BufferedReader reader = new BufferedReader(new InputStreamReader(is, "utf-8"));
			String line = null;
			StringBuilder sb = new StringBuilder();
			while ((line = reader.readLine()) != null) {
				sb.append(line).append("\r\n");
			}
			reader.close();
			String content = sb.toString();
			html.setContent(content);
			StringUtil.writeFile("page_" + page+".htm", content.getBytes());

			// String resCookie = "";
			// for (Header header : cookies) {
			// resCookie += header.getValue().substring(0,
			// header.getValue().indexOf(";")) + ";";
			// }
			// html.setResponseCookie(resCookie);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return "continue";
	}

	@Override public void parsePubtime(List<AgricaltureData> list, Node dom, Component component, String... args) {

		if (component == null)
			return;
		NodeList nl = head(component.getXpath(), dom);
		if (nl == null)
			return;
		String s = nl.item(0).getTextContent();
		String time = s.split("　")[1];
		time = time.replace("最新发布时间：", "");
		for (int i = 0; i < list.size(); i++) {

			list.get(i).setPubtime(time);
			list.get(i).setPubdate(timeProcess(time));
		}

	}

	@Override public void parseUnit(List<AgricaltureData> list, Node dom, Component component, String... args) {

		if (component == null)
			return;
		NodeList nl = head(component.getXpath(), dom);
		if (nl == null)
			return;
		for (int i = 0; i < nl.getLength(); i++) {
			String s = nl.item(i).getTextContent();

			String unit = s.split("　")[0];
			unit = unit.replace("单位：", "");
			list.get(i).setUnit(StringUtil.format(unit));
		}

	}
}
