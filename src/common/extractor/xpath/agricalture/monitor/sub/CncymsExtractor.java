package common.extractor.xpath.agricalture.monitor.sub;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.transform.TransformerException;

import org.apache.http.Header;
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

import common.bean.AgricaltureData;
import common.bean.HtmlInfo;
import common.extractor.xpath.agricalture.monitor.AgricaltureMonitorExtractorAttribute;
import common.extractor.xpath.agricalture.monitor.AgricaltureMonitorXpathExtractor;
import common.siteinfo.Component;
import common.util.StringUtil;

public class CncymsExtractor extends AgricaltureMonitorXpathExtractor implements AgricaltureMonitorExtractorAttribute {
	@Override public String parseNext(Node domtree, Component component, HtmlInfo html, String... args) throws TransformerException {
		String currPage = args[1];
		int curr = Integer.parseInt(currPage);

		return "http://www.cncyms.cn/ms_document/newsdatabase/Order_food2.asp?class_name=%CA%DF%B2%CB&d_text=&page=" + (curr + 1);
	}

	@Override public void parsePubtime(List<AgricaltureData> list, Node dom, Component component, String... args) {

		if (component == null)
			return;
		NodeList nl = head(component.getXpath(), dom);
		if (nl == null)
			return;

		for (int i = 0; i < nl.getLength(); i++) {
			String time = nl.item(i).getTextContent().replace("/", "-");
			time = StringUtil.format(time).replace("  ", "").replace("  ", "");
			list.get(i).setPubtime(time);
			list.get(i).setPubdate(timeProcess(time));
		}

	}
}
