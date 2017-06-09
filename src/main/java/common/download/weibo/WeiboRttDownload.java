package common.download.weibo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import common.bean.HtmlInfo;
import common.bean.WeiboData;
import common.download.GenericMetaCommonDownload;
import common.extractor.xpath.XpathExtractor;
import common.extractor.xpath.weibo.search.WeiboSearchXpathExtractor;
import common.rmi.packet.SearchKey;
import common.bean.CollectDataType;
import common.siteinfo.Siteinfo;
import common.system.Systemconfig;
import common.system.UserAttribute;
import common.util.TimeUtil;

/**
 * 微博转发数据
 * @author grs
 */
public class WeiboRttDownload extends GenericMetaCommonDownload<WeiboData> {
	private UserAttribute user;
	private int id;
	public WeiboRttDownload(SearchKey key, int id, UserAttribute user) {
		super(key);
		this.id = id;
		this.user = user;
	}
	
	@Override
	public void process() {
		List<WeiboData> alllist = new ArrayList<WeiboData>();
		List<WeiboData> list = new ArrayList<WeiboData>();
		Siteinfo siteinfo = Systemconfig.allSiteinfos.get(siteFlag);
		String url = getRealUrl(siteinfo, gloaburl);
		String nexturl = url;
		HtmlInfo html = htmlInfo(CollectDataType.RTT.name());
		try {
			while(nexturl != null && !nexturl.equals("")) {
				list.clear();
				
				html.setOrignUrl(nexturl);
				
				try {
					http.getContent(html, user);
					
					nexturl = (( WeiboSearchXpathExtractor)((XpathExtractor)xpath)).templateRtt(list, html, 0, id+"", nexturl);//WeiboMonitorXpathExtractor
					
					if(list.size()==0) {
						LOGGER.info(url + "数据页面解析为空！！");
						break;
					}
					LOGGER.info(url + "数据页面解析完成。");
					
					Systemconfig.dbService.getNorepeatData(list, "");
					if(list.size()==0) break;
					alllist.addAll(list);
					
					url = nexturl;
					if(nexturl!=null) 
						TimeUtil.rest(siteinfo.getDownInterval());
					
				} catch (Exception e) {
					e.printStackTrace();
					break;
				}
			}
			Systemconfig.dbService.saveDatas(alllist);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			alllist.clear();
			list.clear();
		}
	}
	
}
