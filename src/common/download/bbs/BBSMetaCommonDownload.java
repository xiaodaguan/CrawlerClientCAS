package common.download.bbs;

import java.util.ArrayList;
import java.util.List;

import common.bean.BBSData;
import common.bean.HtmlInfo;
import common.download.DataThreadControl;
import common.download.GenericMetaCommonDownload;
import common.rmi.packet.SearchKey;
import common.system.Systemconfig;
import common.util.TimeUtil;

/**
 * 下载元数据
 * 
 * @author grs
 */
public class BBSMetaCommonDownload extends GenericMetaCommonDownload<BBSData> implements Runnable {
	public BBSMetaCommonDownload(SearchKey key) {
		super(key);
	}

	public void process() {
		List<BBSData> alllist = new ArrayList<BBSData>();
		List<BBSData> list = new ArrayList<BBSData>();
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
								
//				for (BBSData bbsData : list) {
//					
//	                System.out.println("searchKey: "+bbsData.getSearchKey());
//	                System.out.println("title:     "+bbsData.getTitle());
//	                System.out.println("url:       "+bbsData.getUrl());
//	                System.out.println("pubtime:   "+bbsData.getPubtime());
//	                System.out.println("content:   "+bbsData.getContent());
//	                System.out.println("column:    "+bbsData.getColumn());
//	                System.out.println("replyCount:"+bbsData.getReplyCount());
//	                System.out.println("clickCount:"+bbsData.getClickCount());
//	                System.out.println("imgUrl:    "+bbsData.getImgUrl());
//	                System.out.println("replyList: "+bbsData.getReplyList());
//	                System.out.println("replyCount:"+bbsData.getReplyCount());
//	                System.out.println("\n\n");
//				}
				
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
