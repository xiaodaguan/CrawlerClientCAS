package common.http.sub;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;

import common.bean.HtmlInfo;
import common.http.SimpleHttpProcess;
import common.system.Systemconfig;
import common.util.EncoderUtil;

public class HexunPersonHttpProcess extends SimpleHttpProcess {
	/**
	 * 对url需使用gb2312编码
	 */
	@Override protected byte[] simpleGet(HtmlInfo html) {

		HttpClient hc = httpClient(html);
		LOGGER.info("url: " + html.getOrignUrl());
		if (html.getAgent())
			LOGGER.info("本次请求使用代理：" + hc.getParams().getParameter("http.route.default-proxy"));

		HttpGet get = new HttpGet(EncoderUtil.encodeKeyWords(html.getOrignUrl(), "gb2312"));
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
					html.setEncode(h.getValue().substring(h.getValue().indexOf("charset=") + 8).replace(";", ""));
					html.setFixEncode(true);
				}
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
