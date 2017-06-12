package common.download.press;

import java.util.ArrayList;
import java.util.List;

import common.bean.HtmlInfo;
import common.bean.PressData;
import common.download.DataThreadControl;
import common.download.GenericMetaCommonDownload;
import common.rmi.packet.SearchKey;
import common.system.Systemconfig;
import common.util.TimeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 下载元数据
 * 
 * @author grs
 */
public class PressMonitorMetaCommonDownload extends GenericMetaCommonDownload<PressData> {
	private static final Logger LOGGER = LoggerFactory.getLogger(PressMonitorMetaCommonDownload.class);

	public PressMonitorMetaCommonDownload(SearchKey key) {
		super(key);
	}

	@Override
	public void process() {
		List<PressData> alllist = new ArrayList<PressData>();
		List<PressData> list = new ArrayList<PressData>();
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
					LOGGER.info(url + "元数据页面解析为空！！");
					TimeUtil.rest(siteinfo.getDownInterval());
					break;
				}
				LOGGER.info(url + "元数据页面解析完成。");
				totalCount += list.size();
				Systemconfig.dbService.filterDuplication(list);
				if (list.size() == 0) {
					LOGGER.info(url + "无新数据。");
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
