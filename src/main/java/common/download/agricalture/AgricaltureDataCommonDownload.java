package common.download.agricalture;

import common.pojos.AgricaltureData;
import common.pojos.HtmlInfo;
import common.download.GenericDataCommonDownload;
import common.rmi.packet.SearchKey;
import common.system.Systemconfig;
import common.utils.StringUtil;
import common.utils.TimeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CountDownLatch;

/**
 * 下载详细页面
 * 
 * @author gxd
 */
public class AgricaltureDataCommonDownload extends GenericDataCommonDownload<AgricaltureData> implements Runnable {
	private static final Logger LOGGER = LoggerFactory.getLogger(AgricaltureDataCommonDownload.class);

	public AgricaltureDataCommonDownload(String siteFlag, AgricaltureData vd, CountDownLatch endCount, SearchKey key) {
		super(siteFlag, vd, endCount, key);
	}

	public void process() {
		LOGGER.info("downloading...：[" + key.getKEYWORD() + "] " + data.getTitle() + "");
		String url = getRealUrl(data);
		if (url == null)
			return;
		// 检测是否需要代理，未来版本改进
//		siteinfo.setAgent(false);
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
				// 解析数据
				xpath.templateContentPage(data, html, key.getKEYWORD());

				LOGGER.info("关键词：[" + key.getKEYWORD() + "] " + data.getTitle() + "解析完成。。。");
				Systemconfig.dbService.saveData(data);

				/* 状态 */
				int curr = Integer.parseInt(StringUtil.regMatcher(data.getCompleteSize(), "current: ", "/"));
				int rest = Integer.parseInt(StringUtil.regMatcher(data.getCompleteSize(), "rest: ", "]"));
				double per = (double) curr / (curr + rest);
				if (data.getCompleteSize().contains("rest: 0")) {
					// 判断为结束
					LOGGER.info("关键词：[" + key.getKEYWORD() + "] 全部详细页面采集完成。");

				}

				LOGGER.info("关键词：[" + key.getKEYWORD() + "] " + data.getTitle() + "保存完成。。。");

			}
			// if(data.getSameUrl()!=null && count != null && data.getId()>0) {
			// //采集链接
			// SearchKey searchKey = new SearchKey();
			// searchKey.setKey(data.getSameUrl());
			// searchKey.setId(data.getId());
			// searchKey.setSite(siteFlag);
			// TimeUtil.rest(siteinfo.getDownInterval()-10);
			// new NewsMetaCommonDownload(searchKey).process();
			// }
		} catch (Exception e) {
			LOGGER.info("采集出现异常【" + key + "】" + url, e);
			if (data.getCompleteSize().contains("rest: 0")) {
				// 判断为结束
				LOGGER.info("关键词：[" + key.getKEYWORD() + "] 全部详细页面采集完成。");

			}
		} finally {
			if (count != null)
				count.countDown();
			LOGGER.info("当前collect进度: " + data.getCompleteSize());
			TimeUtil.rest(1);
		}
	}

}
