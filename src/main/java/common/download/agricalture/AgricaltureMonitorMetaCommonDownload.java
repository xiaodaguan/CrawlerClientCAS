package common.download.agricalture;

import common.bean.AgricaltureData;
import common.bean.HtmlInfo;
import common.download.DataThreadControl;
import common.download.GenericMetaCommonDownload;
import common.rmi.packet.SearchKey;
import common.system.Systemconfig;
import common.util.TimeUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 下载元数据
 * 
 * @author gxd
 */
public class AgricaltureMonitorMetaCommonDownload extends GenericMetaCommonDownload<AgricaltureData> {

	public AgricaltureMonitorMetaCommonDownload(SearchKey key) {
		super(key);
	}

	@Override public void process() {
		List<AgricaltureData> alllist = new ArrayList<AgricaltureData>();
		List<AgricaltureData> list = new ArrayList<AgricaltureData>();
		String url = getRealUrl(siteinfo, key.getId() > 0 ? key.getKey() : gloaburl);
		int page = getRealPage(siteinfo);
		String keyword = key.getKey();
		map.put(keyword, 1);
		String nexturl = url;
		DataThreadControl dtc = new DataThreadControl(siteFlag, keyword);
		HtmlInfo html = htmlInfo("META");
		String last = null;
		int totalCount = 0;
		while (nexturl != null && !nexturl.equals("")) {
			list.clear();

			html.setOrignUrl(nexturl);
			html.setAgent(false);
			html.setEncode(Systemconfig.allSiteinfos.get(siteFlag).getCharset());

			try {
				if (!url.contains("continue"))
					http.getContent(html);

				if (url.contains("stop"))
					break;
				if (last != null)
					if (html.getContent().equals(last))
						break;
				last = html.getContent();
				// html.setContent(common.util.StringUtil.getContent("filedown/META/baidu_news_search/6f962c1b7d205db4faf80453362b648e.htm"));

				nexturl = xpath.templateListPage(list, html, map.get(keyword), keyword, nexturl, key.getRole() + "");

				if (list.size() == 0) {
					if (!nexturl.contains("continue"))
						LOGGER.info(url + "元数据页面解析为空！！");
					TimeUtil.rest(siteinfo.getDownInterval());
					if (!nexturl.contains("continue"))
						break;
				}
				LOGGER.info(url + "元数据页面解析完成。");
				totalCount += list.size();
				Systemconfig.dbService.getNorepeatData(list, "");
				if (list.size() == 0) {
					if (!nexturl.contains("continue"))
						LOGGER.info(url + "无新数据。");
					if (alllist.size() == 0)
						TimeUtil.rest(siteinfo.getDownInterval());
					if (!nexturl.contains("continue"))
						break;
				}
				alllist.addAll(list);

				map.put(keyword, map.get(keyword) + 1);
				if (map.get(keyword) > page)
					break;
				url = nexturl;
				if (nexturl != null)
					TimeUtil.rest(siteinfo.getDownInterval());

			} catch (Exception e) {
				e.printStackTrace();
				break;
			}
		}
		for (AgricaltureData ad : alllist) {
			try {
				Systemconfig.dbService.saveData(ad);
				LOGGER.info(ad.getTitle() + " saved. ");

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		// dtc.process(alllist, siteinfo.getDownInterval(),null,key);
	}

}
