package common.download.news;

import common.bean.HtmlInfo;
import common.bean.p;
import common.download.DataThreadControl;
import common.download.GenericMetaCommonDownload;
import common.rmi.packet.SearchKey;
import common.system.Systemconfig;
import common.util.TimeUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 下载元数据
 * 
 * @author grs
 */
public class NewsMonitorMetaCommonDownload extends GenericMetaCommonDownload<p> {

	public NewsMonitorMetaCommonDownload(SearchKey key) {
		super(key);
	}

	@Override
	public void process() {
		List<p> alllist = new ArrayList<p>();
		List<p> list = new ArrayList<p>();
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
				http.getContent(html);
				if (last != null)
					if (html.getContent().equals(last))
						break;
				// html.setContent(common.util.StringUtil.getContent("filedown/META/baidu_news_search/6f962c1b7d205db4faf80453362b648e.htm"));
				nexturl = xpath.templateListPage(list, html, map.get(keyword), keyword, nexturl, key.getRole() + "");
				last = html.getContent();
				if (list.size() == 0) {
					Systemconfig.sysLog.log(url + "元数据页面解析为空！！");
					TimeUtil.rest(siteinfo.getDownInterval());
					break;
				}
				Systemconfig.sysLog.log(url + "元数据页面解析完成。");
				totalCount += list.size();
				Systemconfig.dbService.getNorepeatData(list, "");
				if (list.size() == 0) {
					Systemconfig.sysLog.log(url + "无新数据。");
					if (alllist.size() == 0)
						TimeUtil.rest(siteinfo.getDownInterval());
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
		dtc.process(alllist, siteinfo.getDownInterval(),null,key);
	}

}
