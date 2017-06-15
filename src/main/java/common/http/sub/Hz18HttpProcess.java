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

import common.pojos.HtmlInfo;
import common.http.SimpleHttpProcess;
import common.util.EncoderUtil;

public class Hz18HttpProcess extends SimpleHttpProcess {

	@Override protected byte[] simpleGet(HtmlInfo html) {

		HttpClient hc = httpClient(html);

		HttpGet get = new HttpGet(EncoderUtil.encodeKeyWords(html.getOrignUrl(), "utf-8"));
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

				Header[] cookies = response.getHeaders("Set-Cookie");
				String resCookie = "";
				for (Header header : cookies) {
					resCookie += header.getValue().substring(0, header.getValue().indexOf(";")) + ";";
				}
				// LOGGER.info(resCookie);
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
