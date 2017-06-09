package common.extractor.xpath.agricalture.monitor.sub;

import common.bean.AgricaltureData;
import common.bean.HtmlInfo;
import common.extractor.xpath.agricalture.monitor.AgricaltureMonitorExtractorAttribute;
import common.extractor.xpath.agricalture.monitor.AgricaltureMonitorXpathExtractor;
import common.siteinfo.CommonComponent;
import common.siteinfo.Component;
import common.siteinfo.Siteinfo;
import common.system.Systemconfig;
import common.util.StringUtil;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.xpath.XPathAPI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.transform.TransformerException;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class QdfpExtractor extends AgricaltureMonitorXpathExtractor implements AgricaltureMonitorExtractorAttribute {

	private static final Logger LOGGER = LoggerFactory.getLogger(QdfpExtractor.class);

	@Override public String templateListPage(List<AgricaltureData> list, HtmlInfo html, int page, String... keyword) throws SAXException, IOException {
		list.clear();
		Siteinfo siteinfo = Systemconfig.allSiteinfos.get(html.getSite());
		Node domtree = getRealDOM(html);
		CommonComponent comp = getRealComp(siteinfo, html.getType().substring(0, html.getType().indexOf(File.separator)));// 得到元数据的配置组件
		if (page != 1) {
			/**
			 * keyword 0: search_keyword 1: search_url(list) 2: ... 3: cookies
			 */

			if (domtree == null) {
				LOGGER.info("DOM解析为NULL！！");
				return null;
			}

			processList(list, domtree, comp.getComponents(), args(html.getContent(), String.valueOf(siteinfo.getSiteFlag()), keyword));
			if (list.size() == 0)
				return null;
			attrSet(list, siteinfo.getSiteFlag(), keyword[0], Integer.parseInt(keyword[2]));
		}
		try {
			return parseNext(domtree, comp.getComponents().get("next"), html, new String[] { keyword[1], page + "" });
		} catch (TransformerException e) {
			e.printStackTrace();
		}
		return null;
	}

	private String[] args(String content, String siteflag, String... keyword) {
		String arr[] = new String[keyword.length + 1];
		arr[0] = content;
		arr[1] = siteflag;
		for (int i = 2; i < keyword.length; i++) {
			arr[i] = keyword[i - 2];
		}
		return arr;
	}

	@Override public String parseNext(Node domtree, Component component, HtmlInfo html, String... args) throws TransformerException {
		// if (html.getResponseCookie() == null ||
		// html.getResponseCookie().equals(""))
		// return null;
		// html.setCookie(html.getResponseCookie());
		String currUrl = args[0];
		if (currUrl.contains("continue"))
			currUrl = currUrl.split(";")[1];

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String page = args[1];// 传入page应该是当前页，需要请求的是下一页，应+1；但由于从首页进入时首页为1，故后续页需要-1，因此抵消。

		String x__EVENTTARGET = "//A[@id='ctl00_WebPartManagerPanel1_WebPartManager1_gwpPriceView1_PriceView1_LinkButton1']/@href|//A[contains(.,'下一页')]/@href";
		String x__VIEWSTATE = "//INPUT[@type='hidden'][@name='__VIEWSTATE']/@value";
		String x__PREVIOUSPAGE = "//INPUT[@type='hidden'][@name='__PREVIOUSPAGE']/@value";
		String x__EVENTVALIDATION = "//INPUT[@type='hidden'][@name='__EVENTVALIDATION']/@value";

		Node n__EVENTTARGET = XPathAPI.selectSingleNode(domtree, x__EVENTTARGET);
		Node n__VIEWSTATE = XPathAPI.selectSingleNode(domtree, x__VIEWSTATE);
		Node n__PREVIOUSPAGE = XPathAPI.selectSingleNode(domtree, x__PREVIOUSPAGE);
		Node n__EVENTVALIDATION = XPathAPI.selectSingleNode(domtree, x__EVENTVALIDATION);

		String __WPPS = "s";// 固定
		String __EVENTTARGET = "";
		String __EVENTARGUMENT = "";// 固定为空
		String __VIEWSTATE = "";
		String __VIEWSTATEENCRYPTED = "";// 固定为空
		String __PREVIOUSPAGE = "";
		String __EVENTVALIDATION = "";
		String __LASTFOCUS = "";// 固定空

		String menuId = "";

		// 首页
		String ctl00$WebPartManagerPanel1$WebPartManager1$gwpSearchPart1$SearchPart1$txtKey = "";
		String ctl00$WebPartManagerPanel1$WebPartManager1$gwpSearchPart1$SearchPart1$SearchType = "TitleOnly";
		// 共同
		String ctl00$WebPartManagerPanel1$WebPartManager1$gwpM_MemberLogin1$M_MemberLogin1$Login1$UserName = "";
		String ctl00$WebPartManagerPanel1$WebPartManager1$gwpM_MemberLogin1$M_MemberLogin1$Login1$Password = "";
		String ctl00$WebPartManagerPanel1$WebPartManager1$gwpM_MemberLogin1$M_MemberLogin1$Login1$txtVerifyCode = "";
		// 价格页
		String ctl00$WebPartManagerPanel1$WebPartManager1$gwpPriceList1$PriceList1$calDatePublished$dateTextBox = sdf.format(new Date());// 日期
		String ctl00$WebPartManagerPanel1$WebPartManager1$gwpPriceList1$PriceList1$WebDataPage1$ddlSelectPage = page;// 页数

		if (n__EVENTTARGET != null) {
			String tmp = n__EVENTTARGET.getTextContent();
			if (page.equals("1")) {
				__EVENTTARGET = tmp.substring(tmp.indexOf("ctl00$"), tmp.indexOf("\","));
				menuId = tmp.substring(tmp.indexOf("PriceList.aspx?"), tmp.lastIndexOf("\","));
			} else {
				__EVENTTARGET = tmp.substring(tmp.indexOf("ctl00$"), tmp.indexOf("\',"));
			}
		}
		if (n__VIEWSTATE != null)
			__VIEWSTATE = n__VIEWSTATE.getTextContent();
		if (n__PREVIOUSPAGE != null)
			__PREVIOUSPAGE = n__PREVIOUSPAGE.getTextContent();
		if (n__EVENTVALIDATION != null)
			__EVENTVALIDATION = n__EVENTVALIDATION.getTextContent();

		List<NameValuePair> body = new ArrayList<NameValuePair>();

		if (page.equals("1")) {
			body.add(new BasicNameValuePair("__WPPS", __WPPS));
			body.add(new BasicNameValuePair("__EVENTTARGET", __EVENTTARGET));
			body.add(new BasicNameValuePair("__EVENTARGUMENT", __EVENTARGUMENT));
			body.add(new BasicNameValuePair("__VIEWSTATE", __VIEWSTATE));
			body.add(new BasicNameValuePair("ctl00$WebPartManagerPanel1$WebPartManager1$gwpSearchPart1$SearchPart1$txtKey",
					ctl00$WebPartManagerPanel1$WebPartManager1$gwpSearchPart1$SearchPart1$txtKey));
			body.add(new BasicNameValuePair("ctl00$WebPartManagerPanel1$WebPartManager1$gwpSearchPart1$SearchPart1$SearchType",
					ctl00$WebPartManagerPanel1$WebPartManager1$gwpSearchPart1$SearchPart1$SearchType));
			body.add(new BasicNameValuePair("ctl00$WebPartManagerPanel1$WebPartManager1$gwpM_MemberLogin1$M_MemberLogin1$Login1$UserName",
					ctl00$WebPartManagerPanel1$WebPartManager1$gwpM_MemberLogin1$M_MemberLogin1$Login1$UserName));
			body.add(new BasicNameValuePair("ctl00$WebPartManagerPanel1$WebPartManager1$gwpM_MemberLogin1$M_MemberLogin1$Login1$Password",
					ctl00$WebPartManagerPanel1$WebPartManager1$gwpM_MemberLogin1$M_MemberLogin1$Login1$Password));
			body.add(new BasicNameValuePair("ctl00$WebPartManagerPanel1$WebPartManager1$gwpM_MemberLogin1$M_MemberLogin1$Login1$txtVerifyCode",
					ctl00$WebPartManagerPanel1$WebPartManager1$gwpM_MemberLogin1$M_MemberLogin1$Login1$txtVerifyCode));
			body.add(new BasicNameValuePair("__PREVIOUSPAGE", __PREVIOUSPAGE));
			body.add(new BasicNameValuePair("__EVENTVALIDATION", __EVENTVALIDATION));
		} else {
			body.add(new BasicNameValuePair("__WPPS", __WPPS));
			body.add(new BasicNameValuePair("__EVENTTARGET", __EVENTTARGET));
			body.add(new BasicNameValuePair("__EVENTARGUMENT", __EVENTARGUMENT));
			body.add(new BasicNameValuePair("__LASTFOCUS", __LASTFOCUS));
			body.add(new BasicNameValuePair("__VIEWSTATE", __VIEWSTATE));
			body.add(new BasicNameValuePair("ctl00$WebPartManagerPanel1$WebPartManager1$gwpSearchPart1$SearchPart1$txtKey",
					ctl00$WebPartManagerPanel1$WebPartManager1$gwpSearchPart1$SearchPart1$txtKey));
			body.add(new BasicNameValuePair("ctl00$WebPartManagerPanel1$WebPartManager1$gwpM_MemberLogin1$M_MemberLogin1$Login1$UserName",
					ctl00$WebPartManagerPanel1$WebPartManager1$gwpM_MemberLogin1$M_MemberLogin1$Login1$UserName));
			body.add(new BasicNameValuePair("ctl00$WebPartManagerPanel1$WebPartManager1$gwpM_MemberLogin1$M_MemberLogin1$Login1$Password",
					ctl00$WebPartManagerPanel1$WebPartManager1$gwpM_MemberLogin1$M_MemberLogin1$Login1$Password));
			body.add(new BasicNameValuePair("ctl00$WebPartManagerPanel1$WebPartManager1$gwpM_MemberLogin1$M_MemberLogin1$Login1$txtVerifyCode",
					ctl00$WebPartManagerPanel1$WebPartManager1$gwpM_MemberLogin1$M_MemberLogin1$Login1$txtVerifyCode));
			body.add(new BasicNameValuePair("ctl00$WebPartManagerPanel1$WebPartManager1$gwpPriceList1$PriceList1$calDatePublished$dateTextBox",
					ctl00$WebPartManagerPanel1$WebPartManager1$gwpPriceList1$PriceList1$calDatePublished$dateTextBox));
			body.add(new BasicNameValuePair("ctl00$WebPartManagerPanel1$WebPartManager1$gwpPriceList1$PriceList1$WebDataPage1$ddlSelectPage",
					ctl00$WebPartManagerPanel1$WebPartManager1$gwpPriceList1$PriceList1$WebDataPage1$ddlSelectPage));
			body.add(new BasicNameValuePair("__VIEWSTATEENCRYPTED", __VIEWSTATEENCRYPTED));
			body.add(new BasicNameValuePair("__PREVIOUSPAGE", __PREVIOUSPAGE));
			body.add(new BasicNameValuePair("__EVENTVALIDATION", __EVENTVALIDATION));

		}

		String nextUrl = page.equals("1") ? "http://www.qdfp.com/" + menuId : currUrl;
		HttpPost post = new HttpPost(nextUrl);

		// if (html.getResponseCookie() != null &&
		// !html.getResponseCookie().equals(""))
		// post.setHeader("Cookie", html.getResponseCookie());
		if (page.equals("1")) {
			post.setHeader("Accept-Encoding", "gzip, deflate");
			post.setHeader("Accept-Language", "zh-CN,zh;q=0.8,en;q=0.6");
			post.setHeader("Upgrade-Insecure-Requests", "1");
			post.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/46.0.2490.86 Safari/537.36");

			post.setHeader("Content-Type", "application/x-www-form-urlencoded");

			post.setHeader("Origin", "http://www.qdfp.com");
			post.setHeader("Referer", currUrl);

		} else {
			post.setHeader("Accept-Encoding", "gzip, deflate");
			post.setHeader("Accept-Language", "zh-CN,zh;q=0.8,en;q=0.6");
			post.setHeader("Upgrade-Insecure-Requests", "1");
			post.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/46.0.2490.86 Safari/537.36");

			post.setHeader("Cookie", html.getResponseCookie());

			post.setHeader("Content-Type", "application/x-www-form-urlencoded");

			post.setHeader("Origin", "http://www.qdfp.com");
			post.setHeader("Referer", currUrl);
		}

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
			StringUtil.writeFile("page_" + page + ".htm", content.getBytes());

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

		return "continue;" + nextUrl;

	}

}
