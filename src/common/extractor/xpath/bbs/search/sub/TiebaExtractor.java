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

	
	
	@Override
	public void parseReplyname(List<ReplyData> list, Node domtree,
			Component component, String... strings) {
		NodeList nl = head(component.getXpath(), domtree);

		for(int i = 0;i < nl.getLength();i++) {
			ReplyData vd = new ReplyData();
			vd.setName(nl.item(i).getTextContent());
			list.add(vd);
		}
	}
	
	@Override
	public void parseReplytime(List<ReplyData> list, Node dom, Component component, String... strings) {
		NodeList nl = commonList(component.getXpath(), dom);
		if (nl == null)
			return;
		//judge(list.size(), nl.getLength(), "replytime");
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
