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

public class Sj99Extractor extends AgricaltureMonitorXpathExtractor implements AgricaltureMonitorExtractorAttribute {
	@Override public String parseNext(Node domtree, Component component, HtmlInfo html, String... args) throws TransformerException {
		return null;
	}

	@Override public void parseUnit(List<AgricaltureData> list, Node dom, Component component, String... args) {
		for (int i = 0; i < list.size(); i++) {
			list.get(i).setUnit("元/kg");
		}
	}

	@Override public void parsePubtime(List<AgricaltureData> list, Node dom, Component component, String... args) {

		if (component == null)
			return;
		NodeList nl = head(component.getXpath(), dom);
		if (nl == null)
			return;
		String time = nl.item(0).getTextContent();
		for (int i = 0; i < list.size(); i++) {

			list.get(i).setPubtime(time);
			list.get(i).setPubdate(timeProcess(time));
		}

	}
}
