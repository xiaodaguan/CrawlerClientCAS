package common.download.news;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.concurrent.CountDownLatch;

import common.pojos.HtmlInfo;
import common.pojos.NewsData;
import common.download.GenericDataCommonDownload;
import common.rmi.packet.SearchKey;
import common.system.Systemconfig;
import common.utils.StringUtil;
import common.utils.TimeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 下载详细页面
 *
 * @author grs
 */
public class NewsDataCommonDownload extends GenericDataCommonDownload<NewsData> implements Runnable {
	private static final Logger LOGGER = LoggerFactory.getLogger(NewsDataCommonDownload.class);

	public NewsDataCommonDownload(String siteFlag, NewsData vd, CountDownLatch endCount, SearchKey key) {
		super(siteFlag, vd, endCount, key);
	}

	public void process() {
		LOGGER.info("downloading...：[" + key.getKEYWORD() + "] " + data.getTitle() + "");
		String url = getRealUrl(data);
		if (url == null)
			return;
		// 检测是否需要代理，未来版本改进
		siteinfo.setAgent(false);
		HtmlInfo html = htmlInfo("DATA");
		if (html.getEncode().contains(";"))
			LOGGER.info("");

		try {
			if (url != null && !url.equals("")) {
				html.setOrignUrl(url);
				if (url.contains("163.com")) {
					html.setEncode("gb2312");
					html.setAcceptEncoding("gzip, deflate");
				}
				http.getContent(html);

				// html.setContent();
				if (html.getContent() == null) {
					return;
				}
				specialProcess(html);
				// 解析数据
				xpath.templateContentPage(data, html, key.getKEYWORD());

				LOGGER.info("关键词：[" + key.getKEYWORD() + "] " + data.getTitle() + "解析完成。。。");
				Systemconfig.dbService.saveData(data);

				LOGGER.info("关键词：[" + key.getKEYWORD() + "] " + data.getTitle() + "保存完成。。。");
			}

		} catch (Exception e) {
			LOGGER.info("采集出现异常【" + key + "】" + url, e);

		} finally {
			if (count != null)
				count.countDown();
			TimeUtil.rest(1);
		}
	}

	protected void specialProcess(HtmlInfo html) {
		if (html.getSite().contains("tyrefh")) {
			html.setContent(html.getContent().replace("charset=gb2312", "charset=utf-8"));
		}
	}
}
