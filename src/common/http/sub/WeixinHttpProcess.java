package common.http.sub;

import common.bean.HtmlInfo;
import common.http.NeedCookieHttpProcess;
import common.http.SimpleHttpProcess;
import common.system.Systemconfig;
import common.system.UserAttr;
import common.util.EncoderUtil;
import common.util.JsonUtil;
import common.util.StringUtil;
import common.util.TimeUtil;
import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class WeixinHttpProcess extends SimpleHttpProcess {

	@Override
	public byte[] simpleGet(HtmlInfo html) {
		{

			HttpClient hc = httpClient(html);
			if (html.getAgent()) {
				Systemconfig.sysLog.log("本次请求使用代理："
						+ hc.getParams().getParameter(
								"http.route.default-proxy"));
				String proxyInfo = hc.getParams()
						.getParameter("http.route.default-proxy").toString();
				Systemconfig.dbService.updateProxyOrder(proxyInfo + ":"
						+ html.getSiteId());
			}
			// http.route.default-proxy=http://172.18.79.31:8888
			HttpGet get = new HttpGet(EncoderUtil.encodeKeyWords(
					html.getOrignUrl(), "utf-8"));
			if (html.getCookie() != null) {
				get.setHeader("Cookie", html.getCookie());
			}
			if (html.getReferUrl() != null) {
				get.setHeader("Referer", html.getReferUrl());
			}
			if (html.getUa() != null) {
				get.setHeader("User-Agent", html.getUa());
			}
			if (html.getAcceptEncoding() != null) {
				get.setHeader("Accept-Encoding", html.getAcceptEncoding());
			}

			InputStream in = null;
			HttpEntity responseEntity = null;
			try {
				HttpResponse response = hc.execute(get);
				// if(response.getStatusLine().getStatusCode() == 200) {
				responseEntity = response.getEntity();
				if (responseEntity != null) {
					in = response.getEntity().getContent();
					Header h = response.getFirstHeader("Content-Type");
					if (h != null && h.getValue().indexOf("charset") > -1) {
						html.setEncode(h
								.getValue()
								.substring(h.getValue().indexOf("charset=") + 8)
								.replace(";", ""));
						html.setFixEncode(true);
					}

					Header[] cookies = response.getHeaders("Set-Cookie");
					String resCookie = "";
					for (Header header : cookies) {
						resCookie += header.getValue().substring(0,
								header.getValue().indexOf(";"))
								+ ";";
					}
					// System.out.println(resCookie);
					html.setResponseCookie(resCookie);

					return EntityUtils.toByteArray(responseEntity);
				}
				// }
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					EntityUtils.consume(responseEntity);
				} catch (IOException e) {
					// e.printStackTrace();
				}
				IOUtils.closeQuietly(in);
				get.abort();
			}
			return null;
		}
	}
}
