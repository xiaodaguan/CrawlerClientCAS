package common.download.client;

import java.util.ArrayList;
import java.util.List;

import common.bean.BBSData;
import common.bean.ClientData;
import common.bean.HtmlInfo;
import common.download.DataThreadControl;
import common.download.GenericMetaCommonDownload;
import common.rmi.packet.SearchKey;
import common.system.Systemconfig;
import common.util.TimeUtil;

/**
 * 下载元数据
 * 
 * @author rzy
 */
public class ClientMetaCommonDownload extends GenericMetaCommonDownload<ClientData> implements Runnable {
	public ClientMetaCommonDownload(SearchKey key) {
		super(key);
	}

	public void process() {
		List<ClientData> alllist = new ArrayList<ClientData>();
		List<ClientData> list = new ArrayList<ClientData>();
		String url = getRealUrl(siteinfo, gloaburl);
		int page = getRealPage(siteinfo);
		String keyword = key.getKey();
		map.put(keyword, 1);
		String nexturl = url;
		DataThreadControl dtc = new DataThreadControl(siteFlag, keyword);
		HtmlInfo html = htmlInfo("META");
		int totalCount = 0;
		while (nexturl != null && !nexturl.equals("")) {
			
			page =4;
			list.clear();

			html.setOrignUrl(nexturl);

			try {
				
				http.getContent(html);
				// html.setContent(common.util.StringUtil.getContent("filedown/META/baidu/37b30f2108ed06501ad6a769ca8cedc8.htm"));

				nexturl = xpath.templateListPage(list, html, map.get(keyword), keyword, nexturl, key.getRole() + "");
								

				
				totalCount += list.size();
				if (list.size() == 0) {
					Systemconfig.sysLog.log(url + "元数据页面解析为空！！");
					TimeUtil.rest(siteinfo.getDownInterval());
					break;
				}
				Systemconfig.sysLog.log(url + "元数据页面解析完成。");

				Systemconfig.dbService.getNorepeatData(list, "");
				if (list.size() == 0) {
					TimeUtil.rest(siteinfo.getDownInterval());
					// break;
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


		dtc.process(alllist, siteinfo.getDownInterval(),null, key);

		// String siteFlag, SearchKey sk, int logType, String... info

	}

}
