package common.download.weibo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import common.pojos.HtmlInfo;
import common.pojos.UserData;
import common.download.GenericMetaCommonDownload;
import common.extractor.xpath.XpathExtractor;
import common.extractor.xpath.weibo.monitor.WeiboMonitorXpathExtractor;
import common.rmi.packet.SearchKey;
import common.pojos.CollectDataType;
import common.system.Systemconfig;
import common.system.UserAttribute;
import common.util.TimeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 下载数据
 * @author grs
 */
public class FansCommonDownload extends GenericMetaCommonDownload<UserData> {
	private static final Logger LOGGER = LoggerFactory.getLogger(FansCommonDownload.class);

	private UserAttribute user;
	private int id;
	public FansCommonDownload(SearchKey key, int id, UserAttribute user) {
		super(key);
		this.id = id;
		this.user = user;
	}
	@Override
	public void process() {
		List<UserData> alllist = new ArrayList<UserData>();
		List<UserData> list = new ArrayList<UserData>();
		String url = getRealUrl(siteinfo, gloaburl);
		String nexturl = url;
		HtmlInfo html = htmlInfo(CollectDataType.FANS.name());
		int count = 1;
		try {
			while(nexturl != null && !nexturl.equals("")) {
				list.clear();
				
				html.setOrignUrl(nexturl);
				
				try {
					http.getContent(html, user);
//					html.setContent(common.util.StringUtil.getContent("filedown/FANS/sina/50b7702c4c3dc15a1cf1c56155b08d46.htm"));
					
					nexturl = ((WeiboMonitorXpathExtractor)((XpathExtractor)xpath)).templateRelation(list, html, count, id+"", nexturl);
					
					if(list.size()==0) {
						LOGGER.info(url + "元数据页面解析为空！！");
						break;
					}
					LOGGER.info(url + "元数据页面解析完成。");
					
					Systemconfig.dbService.filterDuplication(list);
					
					alllist.addAll(list);
					
					url = nexturl;
					count++;
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
