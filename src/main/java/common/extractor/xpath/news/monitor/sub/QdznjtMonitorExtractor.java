package common.extractor.xpath.news.monitor.sub;

import common.pojos.NewsData;
import common.extractor.xpath.news.monitor.NewsMonitorExtractorAttribute;
import common.extractor.xpath.news.monitor.NewsMonitorXpathExtractor;
import common.siteinfo.Component;
import common.util.StringUtil;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.List;
import java.util.Map;

/**
 * 抽取实现类
 * 
 * @author grs
 */
public class QdznjtMonitorExtractor extends NewsMonitorXpathExtractor implements NewsMonitorExtractorAttribute {

	@Override
	public void processList(List<NewsData> list, Node domtree, Map<String, Component> comp, String... args) {
		this.parseTitle(list, domtree, comp.get("title"));

		if (list.size() == 0)
			return;

		this.parseUrl(list, domtree, comp.get("url"), args[2]);
	}

	@Override
	public void processPage(NewsData data, Node domtree, Map<String, Component> comp, String... args) {
		if (data.getTitle() == null) {
			this.parsePageTitle(data, domtree, comp.get("pageTitle"));
		}

		this.parseContent(data, domtree, comp.get("content"));
		this.parseAuthor(data, domtree, comp.get("author"));
		this.parsePubtime(data, domtree, comp.get("originalPubtime"));
		this.parseSource(data, domtree, comp.get("originalSource"));
		this.parseImgUrl(data, domtree, comp.get("imgUrl"));
	}

	@Override
	public String parseNext(Node dom, Component component, String... args) {
        //http://www.qdznjt.com/index/trafficAnnouncementIndex?currentPageNum=1
		String currUrl = args[0];

		int currPage = Integer.parseInt(currUrl.substring(currUrl.lastIndexOf("currentPageNum=") + 15));

		String nextUrl = "";

			nextUrl = currUrl.replace("currentPageNum="+currPage,"currentPageNum="+(currPage+1));


		return nextUrl;
	}

	@Override
	public void parseUrl(List<NewsData> list, Node dom, Component component, String... args) {
		if (component == null)
			return;
		NodeList nl = head(component.getXpath(), dom, list.size(), component.getName());
		if (nl == null)
			return;
		for (int i = 0; i < nl.getLength(); i++) {
			String url = nl.item(i).getTextContent();
			url=url.replace("javascript:window.open('","").replace("');","");

			list.get(i).setUrl(url);
		}
	}

    @Override
    public void parseAuthor(NewsData data, Node dom, Component component, String... args) {
        if (component == null)
            return;
        NodeList nl = commonList(component.getXpath(), dom);
        if (nl == null)
            return;
        if (nl.item(0) != null)
            data.setAuthor(StringUtil.format(nl.item(0).getTextContent()));
    }



    @Override
    public void parsePubtime(NewsData data, Node dom, Component component, String... args) {
        if (component == null)
            return;
        NodeList nl = commonList(component.getXpath(), dom);
        if (nl == null)
            return;
        if (nl.item(0) != null) {
            data.setPubtime(nl.item(0).getTextContent());
            data.setPubdate(timeProcess(data.getPubtime().trim()));
        }
    }

    @Override
    public void parseSource(NewsData data, Node dom, Component component, String... strings) {
        if (component == null)
            return;
        NodeList nl = commonList(component.getXpath(), dom);
        if (nl == null)
            return;
        if (nl.item(0) != null)
            data.setSource(StringUtil.format(nl.item(0).getTextContent()));
    }

    @Override
    public void parseContent(NewsData data, Node dom, Component component, String... strings) {
        if (component == null)
            return;
        NodeList nl = commonList(component.getXpath(), dom);
        if (nl == null)
            return;
        String str = "";
        for (int i = 0; i < nl.getLength(); i++) {
            if (i < nl.getLength() - 1)
                str += nl.item(i).getTextContent() + "\r\n";
            else
                str += nl.item(0).getTextContent();
        }
        data.setContent(str);
    }


//	@Override
//	public void parsePubtime(NewsData data, Node dom, Component component, String... args) {
//		String str = "";
//		if (component == null)
//			return;
//
//		NodeList nl = commonList(component.getXpath(), dom);
//		if (nl == null)
//			return;
//
//		if (nl.item(0) != null) {
//			str = StringUtil.format(nl.item(0).getTextContent());
//			while (str.contains("  "))
//				str = str.replace("  ", " ");
//
//			str = str.split(" ")[0].replace("时间：", "");
//			str = str.replace("年", "-").replace("月", "-").replace("日", "-");
//			data.setPubtime(str);
//			data.setPubdate(timeProcess(data.getPubtime().trim()));
//		}
//
//	}

//	@Override
//	public void parseSource(NewsData data, Node dom, Component component, String... args) {
//		String str = "";
//		if (component == null)
//			return;
//
//		NodeList nl = commonList(component.getXpath(), dom);
//		if (nl == null)
//			return;
//
//		if (nl.item(0) != null) {
//			str = StringUtil.format(nl.item(0).getTextContent());
//			while (str.contains("  "))
//				str = str.replace("  ", " ");
//			str = str.split(" ")[1].replace("来源：", "");
//		}
//
//		data.setSource(str);
//	}

//	@Override
//	public void parseAuthor(NewsData data, Node dom, Component component, String... args) {
//		String str = "";
//		if (component == null)
//			return;
//
//		NodeList nl = commonList(component.getXpath(), dom);
//		if (nl == null)
//			return;
//
//		if (nl.item(0) != null) {
//			str = StringUtil.format(nl.item(0).getTextContent());
//			while (str.contains("  "))
//				str = str.replace("  ", " ");
//
//				str = str.split(" ")[2].replace("作者：", "");
//
//		}
//
//		data.setAuthor(str);
//	}
}
