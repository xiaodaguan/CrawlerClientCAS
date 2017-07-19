package common.extractor.xpath.frgmedia.search.sub;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import common.pojos.FrgmediaData;
import common.pojos.HtmlInfo;
import common.extractor.xpath.frgmedia.search.FrgmediaSearchXpathExtractor;
import common.siteinfo.CommonComponent;
import common.siteinfo.Component;
import common.siteinfo.Siteinfo;
import common.system.Systemconfig;

/**
 * 外媒搜索特殊属性抽取
 * 
 * @author rzy
 *
 */
public class SinoExtractor extends FrgmediaSearchXpathExtractor {
	private static final Logger LOGGER = LoggerFactory.getLogger(SinoExtractor.class);

	@Override // templateContentPage
	public String templateContentPage(FrgmediaData data, HtmlInfo html, int page, String... keyword) {
		
		Siteinfo siteinfo = Systemconfig.allSiteinfos.get(html.getSite());
		// create(content);
		Node domtree = null;
		try {
			domtree = getRealDOM(html);
		} catch (SAXException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (domtree == null) {
			LOGGER.info("DOM解析为NULL！！");
			return null;
		}
		CommonComponent comp = getRealComp(siteinfo,
				html.getCrawlerType().substring(0, html.getCrawlerType().indexOf(File.separator)));// 得到元数据的配置组件
		if (domtree == null | comp == null)
			return null;
		this.parseContent(data, domtree, comp.getComponents().get("content"), html.getContent());
		this.parseImgUrl(data, domtree, comp.getComponents().get("imgUrl"), html.getContent());
		this.parseSource(data, domtree, comp.getComponents().get("source"), html.getContent());
		this.parseAuthor(data, domtree, comp.getComponents().get("author"), html.getContent());

		if (data.getContent().length()==0&&html.getContent().contains("var flash =[];flash[0]={")) {
			String content = html.getContent();
			String[] lines = content.split("\n");

			int mark = 0;
			for (String line : lines) {
				if (line.contains("flash[")) {
					mark = 1;
				}
				if (mark > 0 && line.contains("image_url")) {
					line = line.split("\"image_url\":\"")[1].split("\"};")[0];
					data.setImgUrl(data.getImgUrl()+line+";");
					mark = 0;
				} else if (mark > 0&&!line.contains("title")) {
					Pattern p = Pattern.compile(
							"[\u4e00-\u9fa5]|[\u3002\uff1b\uff0c\uff1a\u201c\u201d\uff08\uff09\u3001\uff1f\u300a\u300b]");
					Matcher m = p.matcher(line);
					
					StringBuffer sb = new StringBuffer();
					while (m.find()) {
						sb.append(m.group().trim());
					}
					data.setContent(data.getContent()+sb.toString());
				}
			}
		}
		return null;
	}

	public void parseContent(FrgmediaData data, Node dom, Component component, String... strings) {
		if (component == null)
			return;
		NodeList nl = commonList(component.getXpath(), dom);
		if (nl == null)
			return;
		String str = "";
		for (int i = 0; i < nl.getLength(); i++) {
			if (nl.item(i).getTextContent().length() > 1) {
				str += nl.item(i).getTextContent().trim() + "\r\n";
			}
		}
		str.replace("/* [id1038] 社会新闻详细页 Admaru */", "");
		str.replace("OA_show();", "");
		str.replace("// ]]> // <![CDATA[", "");
		str.replace("/* [id885] 社会详情页文包图广告 */", "");
		str.replace("OA_show(885);", "");
		str.replace("// ]]>", "");

		String[] lines = str.split("\n");
		str = "";
		for (String line : lines) {
			line = line.trim();
			if (line.length() > 2) {
				str += line;
			}
		}
		data.setContent(str);
	}

	public void parseSource(FrgmediaData data, Node dom, Component component, String... strings) {
		if (component == null)
			return;
		NodeList nl = commonList(component.getXpath(), dom);
		if (nl == null)
			return;
		String str = "";
		for (int i = 0; i < nl.getLength(); i++) {
			str += nl.item(i).getTextContent().trim();
		}
		str = str.replace(" ", "").split("来源:")[1].split("查看")[0].replace("|", "");
		if (str.length() == 0)
			data.setSource("美国中文网");
		else
			data.setSource(str);
	}
}
