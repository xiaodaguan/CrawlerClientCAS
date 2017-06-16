package common.download.news;

import java.util.concurrent.CountDownLatch;

import common.pojos.HtmlInfo;
import common.pojos.NewsData;
import common.download.GenericDataCommonDownload;
import common.rmi.packet.SearchKey;
import common.utils.TimeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 下载详细页面
 * @author grs
 */
public class NewsSameDataCommonDownload extends GenericDataCommonDownload<NewsData> implements Runnable {
	private static final Logger LOGGER = LoggerFactory.getLogger(NewsSameDataCommonDownload.class);

	public NewsSameDataCommonDownload(String siteFlag, NewsData vd,
			CountDownLatch endCount, SearchKey key) {
		super(siteFlag, vd, endCount, key);
	}

	@Override
	public void process() {
		TimeUtil.rest(siteinfo.getDownInterval());
		String url = getRealUrl(data);
		if(url==null) return;
		HtmlInfo html = htmlInfo("SAME");
		try {
			if (url != null && !url.equals("")) {
				html.setOrignUrl(url);
				
				http.getContent(html);
				if(html.getContent()==null) {
					return;
				}
				//解析数据
				xpath.templateContentPage(data, html);
				LOGGER.info("相同新闻"+data.getTitle() + "解析完成。。。");

				//todo
//				if(Systemconfig.dbService instanceof NewsOracleService)
//					((NewsOracleService)Systemconfig.dbService).saveSameData(data);
//				else if(Systemconfig.dbService instanceof NewsMysqlService)
//					((NewsMysqlService)Systemconfig.dbService).saveSameData(data);
				LOGGER.info("相同新闻"+data.getTitle() + "保存完成。。。");
			}
		} catch (Exception e) {
			LOGGER.info("采集出现异常"+url, e);
		}
	}
	
}
