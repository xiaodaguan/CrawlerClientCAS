package common.download.news;

import java.util.concurrent.CountDownLatch;

import common.bean.HtmlInfo;
import common.bean.NewsData;
import common.download.GenericDataCommonDownload;
import common.rmi.packet.SearchKey;
import common.system.Systemconfig;
import common.util.StringUtil;
import common.util.TimeUtil;

/**
 * 下载详细页面
 * 
 * @author grs
 */
public class NewsDataCommonDownload extends GenericDataCommonDownload<NewsData> implements Runnable {

	public NewsDataCommonDownload(String siteFlag, NewsData vd, CountDownLatch endCount, SearchKey key) {
		super(siteFlag, vd, endCount, key);
	}

	public void process() {
		Systemconfig.sysLog.log("downloading...：[" + key.getKey() + "] " + data.getTitle() + "");
		String url = getRealUrl(data);
		if (url == null)
			return;
		// 检测是否需要代理，未来版本改进
		siteinfo.setAgent(false);
		HtmlInfo html = htmlInfo("DATA");
		if (html.getEncode().contains(";"))
			System.out.println();

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
				xpath.templateContentPage(data, html, key.getKey());

				Systemconfig.sysLog.log("关键词：[" + key.getKey() + "] " + data.getTitle() + "解析完成。。。");
				Systemconfig.dbService.saveData(data);

				/* 状态 */
				int curr = Integer.parseInt(StringUtil.regMatcher(data.getCompleteSize(), "current: ", "/"));
				int rest = Integer.parseInt(StringUtil.regMatcher(data.getCompleteSize(), "rest: ", "]"));
				double per = (double) curr / (curr + rest);
				if (data.getCompleteSize().contains("rest: 0")) {
					// 判断为结束
					Systemconfig.sysLog.log("关键词：[" + key.getKey() + "] 全部详细页面采集完成。");

				}

				Systemconfig.sysLog.log("关键词：[" + key.getKey() + "] " + data.getTitle() + "保存完成。。。");
				synchronized (key) {
					key.savedCountIncrease();
				}

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
			Systemconfig.sysLog.log("采集出现异常【" + key + "】" + url, e);
			// synchronized (key) {
			// key.savedCountDecrease();
			// }
			// Systemconfig.crawlerStatus.getTasks().get(key.getCrawlerStatusId()).setSavedCount(key.getSavedCount());
			if (data.getCompleteSize().contains("rest: 0")) {
				// 判断为结束
				Systemconfig.sysLog.log("关键词：[" + key.getKey() + "] 全部详细页面采集完成。");
			}
		} finally {
			if (count != null)
				count.countDown();
//			Systemconfig.sysLog.log("当前collect进度: " + data.getCompleteSize());
			TimeUtil.rest(1);
		}
	}

	protected void specialProcess(HtmlInfo html) {
		if (html.getSite().contains("tyrefh")) {
			html.setContent(html.getContent().replace("charset=gb2312", "charset=utf-8"));
		}
	}
}
