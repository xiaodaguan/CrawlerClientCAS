package common.download.person;

import java.util.concurrent.CountDownLatch;

import common.pojos.PersonData;
import common.pojos.HtmlInfo;
import common.download.GenericDataCommonDownload;
import common.rmi.packet.SearchKey;
import common.system.Systemconfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 下载详细页面
 * 
 * @author grs
 */
public class PersonDataCommonDownload extends GenericDataCommonDownload<PersonData> {
	private static final Logger LOGGER = LoggerFactory.getLogger(PersonDataCommonDownload.class);

	public PersonDataCommonDownload(String siteFlag, PersonData vd, CountDownLatch endCount, SearchKey key) {
		super(siteFlag, vd, endCount, key);
	}

	public void process() {
		String url = getRealUrl(data);
		if (url == null)
			return;
		HtmlInfo html = htmlInfo("DATA");
		try {
			if (url != null && !url.equals("")) {
				html.setOrignUrl(url);
				html.setAgent(false);
				http.getContent(html);
				// html.setContent();
				if (html.getContent() == null) {
					return;
				}
				// 解析数据
				xpath.templateContentPage(data, html);

				LOGGER.info(data.getTitle() + "解析完成。。。");
				Systemconfig.dbService.saveData(data);
				synchronized (key) {
					key.savedCountIncrease();
				}
				LOGGER.info(data.getTitle() + "保存完成。。。");
			}
		} catch (Exception e) {
			LOGGER.info("采集出现异常" + url, e);
		} finally {
			if (count != null)
				count.countDown();
		}
	}

}
