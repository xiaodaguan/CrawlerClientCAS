package common.extractor.xpath.bbs.search.sub;

import common.bean.BBSData;
import common.bean.ReplyData;
import common.extractor.xpath.bbs.search.BbsSearchXpathExtractor;
import common.siteinfo.Component;
import common.util.StringUtil;
import net.sf.json.JSONObject;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.List;

public class TiebaExtractor extends BbsSearchXpathExtractor {

//	@Override
//	public void parsePubtime(BBSData videoData, Node dom, Component component, String... args) {
//		// component.setXpath("//LI/SPAN[@class='j_reply_data']");
//		NodeList nl = commonList(component.getXpath(), dom);
//		if (nl == null)
//			return;
//
//		if (nl.item(0) != null) {
//			String text = nl.item(0).getTextContent();
//			String time = "";
//			try {
//				JSONObject jo = JSONObject.fromObject(text);
//				jo = jo.getJSONObject("content");
//				time = jo.getString("date");
//			} catch (Exception e) {
//				System.out.println("time spec..");
//				String oldXpath=component.getXpath();
//				component.setXpath("//DIV[@class='post-tail-wrap']/SPAN[@class='tail-info'][3]");
//
//				nl = commonList(component.getXpath(), dom);
//				if (nl == null)
//					return;
//
//				if (nl.item(0) != null) {
//					time=nl.item(0).getTextContent();
//				}
//				component.setXpath(oldXpath);
//			}
//			videoData.setPubtime(time);
//			videoData.setPubdate(timeProcess(videoData.getPubtime()));
//		}
//	}

	@Override
	public void parseReplytime(List<ReplyData> list, Node dom, Component component, String... strings) {
		NodeList nl = commonList(component.getXpath(), dom);
		if (nl == null)
			return;
		judge(list.size(), nl.getLength(), "replytime");
		for (int i = 0; i < nl.getLength(); i++) {
			JSONObject jo = JSONObject.fromObject(nl.item(i).getTextContent());
			jo = jo.getJSONObject("content");
			list.get(i).setTime(jo.getString("date"));
			list.get(i).setPubdate(timeProcess(jo.getString("date")));
		}
	}

	@Override
	public void parseReplyCount(BBSData data, Node domtree, Component component, String... ags) {
		NodeList nl = commonList(component.getXpath(), domtree);
		if (nl == null)
			return;
		if (nl.item(0) != null) {
			String time = StringUtil.extractMulti(nl.item(0).getTextContent().split("回复")[0], "\\d");
			if (time == null || time.equals(""))
				data.setReplyCount(0);
			else
				data.setReplyCount(Integer.parseInt(time));
		}
	}

}
