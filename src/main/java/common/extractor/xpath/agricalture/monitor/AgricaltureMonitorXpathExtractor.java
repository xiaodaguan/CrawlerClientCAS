package common.extractor.xpath.agricalture.monitor;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.transform.TransformerException;

import common.extractor.xpath.agricalture.monitor.sub.QdfpExtractor;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import common.bean.CommonData;
import common.bean.HtmlInfo;
import common.bean.AgricaltureData;
import common.bean.NewsData;
import common.extractor.xpath.XpathExtractor;
import common.siteinfo.CommonComponent;
import common.siteinfo.Component;
import common.siteinfo.Siteinfo;
import common.system.Systemconfig;
import common.util.MD5Util;
import common.util.StringUtil;

/**
 * 抽取实现类
 * 
 * @author grs
 */
public class AgricaltureMonitorXpathExtractor extends XpathExtractor<AgricaltureData> implements AgricaltureMonitorExtractorAttribute {
	private static final Logger LOGGER = LoggerFactory.getLogger(AgricaltureMonitorXpathExtractor.class);

	/**
	 * 品名
	 * 
	 * @param list
	 * @param dom
	 * @param component
	 * @param args
	 */
	public void parseName(List<AgricaltureData> list, Node dom, Component component, String... args) {

		if (component == null)
			return;
		NodeList nl = head(component.getXpath(), dom);
		if (nl == null)
			return;
		for (int i = 0; i < nl.getLength(); i++) {
			AgricaltureData vd = new AgricaltureData();
			vd.setTitle(StringUtil.format(nl.item(i).getTextContent()));
			vd.setName(vd.getTitle());
			list.add(vd);
		}

	}

	/**
	 * url
	 */
	@Override public void parseUrl(List<AgricaltureData> list, Node dom, Component component, String... args) {

		if (list.size() == 0)
			return;
		for (int i = 0; i < list.size(); i++) {
			list.get(i).setUrl(args[0]);
		}

	}

	/**
	 * 省份
	 * 
	 * @param list
	 * @param dom
	 * @param component
	 * @param args
	 */
	public void parseProvince(List<AgricaltureData> list, Node dom, Component component, String... args) {
		String url = args[0];
		if (url.contains("hz18") || url.contains("cncyms.cn") || url.contains("qddzt.com") || url.contains("99sj.com")|| url.contains("qdfp.com")) {
			for (AgricaltureData ad : list) {
				ad.setProvince("山东");

			}
		}

	}

	/**
	 * 城市
	 * 
	 * @param list
	 * @param dom
	 * @param component
	 * @param args
	 */
	public void parseCity(List<AgricaltureData> list, Node dom, Component component, String... args) {
		String url = args[0];
		if (url.contains("hz18") || url.contains("cncyms.cn") || url.contains("99sj.com")|| url.contains("qdfp.com")) {
			for (AgricaltureData ad : list) {
				ad.setCity("青岛");
			}
		}
		if (url.contains("qddzt.com")) {
			for (AgricaltureData ad : list) {
				ad.setCity("莱西市");

			}
		}

	}

	/**
	 * 区
	 * 
	 * @param list
	 * @param dom
	 * @param component
	 * @param args
	 */
	public void parseDistrict(List<AgricaltureData> list, Node dom, Component component, String... args) {

		String url = args[0];
		if (url.contains("hz18")) {
			for (AgricaltureData ad : list) {
				ad.setDistrict("李沧区");

			}
		} else if (url.contains("cncyms.cn")) {
			for (AgricaltureData ad : list) {
				ad.setDistrict("城阳区");

			}
		} else if (url.contains("qddzt.com")) {
			for (AgricaltureData ad : list) {
				ad.setDistrict("店埠镇");

			}
		} else if (url.contains("99sj.com")) {
			for (AgricaltureData ad : list) {
				ad.setDistrict("南村镇");

			}
		}else if (url.contains("qdfp.com")) {
			for (AgricaltureData ad : list) {
				ad.setDistrict("市北区");

			}
		} else {

		}
	}

	/**
	 * 街道
	 * 
	 * @param list
	 * @param dom
	 * @param component
	 * @param args
	 */
	public void parseStreet(List<AgricaltureData> list, Node dom, Component component, String... args) {
		String url = args[0];
		if (url.contains("hz18")) {
			for (AgricaltureData ad : list) {
				ad.setStreet("重庆路");
			}
		} else if (url.contains("cncyms.cn")) {
			for (AgricaltureData ad : list) {
				ad.setStreet("和阳路");
			}
		} else if (url.contains("qddzt.com")) {
			for (AgricaltureData ad : list) {
				ad.setStreet("兴店路");
			}
		} else if (url.contains("99sj.com")) {
			for (AgricaltureData ad : list) {
				ad.setStreet("姜家埠");
			}
		}else if (url.contains("qdfp.com")) {
			for (AgricaltureData ad : list) {
				ad.setStreet("抚顺路");
			}
		} else {

		}
	}

	/**
	 * 发布时间
	 * 
	 * @param list
	 * @param dom
	 * @param component
	 * @param args
	 */
	public void parsePubtime(List<AgricaltureData> list, Node dom, Component component, String... args) {

		if (component == null)
			return;
		NodeList nl = head(component.getXpath(), dom);
		if (nl == null)
			return;

		for (int i = 0; i < list.size(); i++) {
			String time = nl.item(i).getTextContent();
			list.get(i).setPubtime(time);
			list.get(i).setPubdate(timeProcess(time));
		}

	}

	/**
	 * 信息来源
	 * 
	 * @param list
	 * @param dom
	 * @param component
	 * @param args
	 */
	public void parseSource(List<AgricaltureData> list, Node dom, Component component, String... args) {

		if (component == null)
			return;
		NodeList nl = head(component.getXpath(), dom);
		if (nl == null)
			return;
		for (int i = 0; i < nl.getLength(); i++) {
			String s = nl.item(i).getTextContent();
			list.get(i).setSource(StringUtil.format(s));
		}

	}

	/**
	 * 高价
	 * 
	 * @param list
	 * @param dom
	 * @param component
	 * @param args
	 */
	public void parseHighPrice(List<AgricaltureData> list, Node dom, Component component, String... args) {
		if (component == null)
			return;
		NodeList nl = head(component.getXpath(), dom);
		if (nl == null)
			return;
		for (int i = 0; i < nl.getLength(); i++) {
			String s = nl.item(i).getTextContent();
			list.get(i).setHighPrice(StringUtil.format(s));
		}
	}

	/**
	 * 低价
	 * 
	 * @param list
	 * @param dom
	 * @param component
	 * @param args
	 */
	public void parseLowPrice(List<AgricaltureData> list, Node dom, Component component, String... args) {
		if (component == null)
			return;
		NodeList nl = head(component.getXpath(), dom);
		if (nl == null)
			return;
		for (int i = 0; i < nl.getLength(); i++) {
			String s = nl.item(i).getTextContent();
			list.get(i).setLowPrice(StringUtil.format(s));
		}

	}

	/**
	 * 均价
	 * 
	 * @param list
	 * @param dom
	 * @param component
	 * @param args
	 */
	public void parseAverPrice(List<AgricaltureData> list, Node dom, Component component, String... args) {

		if (component == null)
			return;
		NodeList nl = head(component.getXpath(), dom);
		if (nl == null)
			return;
		for (int i = 0; i < nl.getLength(); i++) {
			String s = nl.item(i).getTextContent();
			list.get(i).setAveragePrice(StringUtil.format(s));
		}

	}

	/**
	 * 单位
	 * 
	 * @param list
	 * @param dom
	 * @param component
	 * @param args
	 */
	public void parseUnit(List<AgricaltureData> list, Node dom, Component component, String... args) {

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

	/**
	 * 规格
	 * 
	 * @param list
	 * @param dom
	 * @param component
	 * @param args
	 */
	public void parseSpec(List<AgricaltureData> list, Node dom, Component component, String... args) {

		if (component == null)
			return;
		NodeList nl = head(component.getXpath(), dom);
		if (nl == null)
			return;
		for (int i = 0; i < nl.getLength(); i++) {
			String s = nl.item(i).getTextContent();
			list.get(i).setSpec(StringUtil.format(s));
		}

	}

	@Override public void processPage(AgricaltureData data, Node domtree, Map<String, Component> comp, String... args) {

	}

	@Override public void processList(List<AgricaltureData> list, Node domtree, Map<String, Component> comp, String... args) {
		// this.parseUrl(list, domtree, comp.get("url"), args);
		String cont = args[0];
		String siteTemplateId = args[1];
		String url = args[2];
		// String domStr = domtree.getTextContent();

		this.parseName(list, domtree, comp.get("name"));
		this.parseProvince(list, domtree, comp.get("province"), url);
		this.parseCity(list, domtree, comp.get("city"), url);
		this.parseDistrict(list, domtree, comp.get("district"), url);
		this.parseStreet(list, domtree, comp.get("street"), url);
		this.parsePubtime(list, domtree, comp.get("pubtime"));
		this.parseSource(list, domtree, comp.get("source"));
		this.parseHighPrice(list, domtree, comp.get("highPrice"));
		this.parseLowPrice(list, domtree, comp.get("lowPrice"));
		this.parseAverPrice(list, domtree, comp.get("averPrice"));
		this.parseUnit(list, domtree, comp.get("unit"));
		this.parseSpec(list, domtree, comp.get("spec"));

	}

	@Override public String templateListPage(List<AgricaltureData> list, HtmlInfo html, int page, String... keyword) throws SAXException, IOException {
		list.clear();
		/**
		 * keyword 0: search_keyword 1: search_url(list) 2: ... 3: cookies
		 */
		Siteinfo siteinfo = Systemconfig.allSiteinfos.get(html.getSite());

		if (html.getOrignUrl().contains("hz18"))
			html.setEncode("gb2312");
		Node domtree = getRealDOM(html);
		if (domtree == null) {
			LOGGER.info("DOM解析为NULL！！");
			return null;
		}
		CommonComponent comp = getRealComp(siteinfo, html.getType().substring(0, html.getType().indexOf(File.separator)));// 得到元数据的配置组件
		processList(list, domtree, comp.getComponents(), args(html.getContent(), String.valueOf(siteinfo.getSiteFlag()), keyword));
		if (list.size() == 0)
			return null;
		attrSet(list, siteinfo.getSiteFlag(), keyword[0], Integer.parseInt(keyword[2]));
		try {
			return parseNext(domtree, comp.getComponents().get("next"), html, new String[] { keyword[1], page + "" });
		} catch (TransformerException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 解析列表页的下一页
	 * 
	 * @param domtree
	 * @param component
	 * @param args
	 *            : 0 url 1 page No.
	 * @return
	 * @throws TransformerException
	 */
	public String parseNext(Node domtree, Component component, HtmlInfo html, String... args) throws TransformerException {

		NodeList nl = commonList(component.getXpath(), domtree);
		if (nl.item(0) != null) {
			return urlProcess(component, nl.item(0));
		}
		return null;

	}

	/**
	 * 0: content 1: siteflag ...
	 * 
	 * @param content
	 * @param siteflag
	 * @param keyword
	 * @return
	 */
	protected void attrSet(List<AgricaltureData> list, int siteflag, String key, int code) {
		for (AgricaltureData t : list) {
			CommonData cd = (CommonData) t;
			cd.setSearchKey(key);
			cd.setCategoryCode(code);
			cd.setMd5(MD5Util.MD5(cd.getTitle() + cd.getPubdate()));
			cd.setSiteId(siteflag);
		}
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

	@Override public void parseTitle(List<AgricaltureData> list, Node dom, Component component, String... args) {
		// TODO Auto-generated method stub

	}

	@Override public void parseContent(AgricaltureData data, Node dom, Component component, String... args) {
		// TODO Auto-generated method stub

	}

	@Override public void parseSource(AgricaltureData data, Node dom, Component component, String... args) {
		// TODO Auto-generated method stub

	}

	@Override public void parseAuthor(AgricaltureData data, Node dom, Component component, String... args) {
		// TODO Auto-generated method stub

	}

	@Override public void parsePubtime(AgricaltureData data, Node dom, Component component, String... args) {
		// TODO Auto-generated method stub

	}

	@Override public void parseImgUrl(AgricaltureData data, Node dom, Component component, String... args) {
		// TODO Auto-generated method stub

	}

	@Override public void parsePageTitle(AgricaltureData data, Node dom, Component component, String... args) {
		// TODO Auto-generated method stub

	}

}
