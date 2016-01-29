package common.down.agricalture;

import common.bean.AgricaltureData;
import common.bean.HtmlInfo;
import common.down.GenericDataCommonDownload;
import common.rmi.packet.SearchKey;
import common.system.Systemconfig;
import common.util.StringUtil;
import common.util.TimeUtil;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * 下载详细页面
 * 
 * @author gxd
 */
public class AgricaltureDataCommonDownload extends GenericDataCommonDownload<AgricaltureData> implements Runnable {

	public AgricaltureDataCommonDownload(String siteFlag, AgricaltureData vd, CountDownLatch endCount, SearchKey key) {
		super(siteFlag, vd, endCount, key);
	}

	public void process() {
		Systemconfig.sysLog.log("downloading...：[" + key.getKey() + "] " + data.getTitle() + "");
		String url = getRealUrl(data);
		if (url == null)
			return;
		// 检测是否需要代理，未来版本改进
//		siteinfo.setAgent(false);
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
				// 解析数据
				xpath.templateContentPage(data, html, key.getKey());

				Systemconfig.sysLog.log("关键词：[" + key.getKey() + "] " + data.getTitle() + "解析完成。。。");
				Systemconfig.dbService.saveData(data);

				/* 状态 */
				Systemconfig.crawlerStatus.getTasks().get(key.getCrawlerStatusId()).setStatus("DOWNLOADING");
				int curr = Integer.parseInt(StringUtil.regMatcher(data.getCompleteSize(), "current: ", "/"));
				int rest = Integer.parseInt(StringUtil.regMatcher(data.getCompleteSize(), "rest: ", "]"));
				double per = (double) curr / (curr + rest);
				if (data.getCompleteSize().contains("rest: 0")) {
					// 判断为结束
					Systemconfig.sysLog.log("关键词：[" + key.getKey() + "] 全部详细页面采集完成。");
					Systemconfig.crawlerStatus.getTasks().get(key.getCrawlerStatusId()).setIfCrawled(true);
					Systemconfig.crawlerStatus.getTasks().get(key.getCrawlerStatusId()).setStatus("COMPLETE");
					Systemconfig.dbService.updateStatus(Systemconfig.crawlerStatus,
							Systemconfig.crawlerStatus.getTasks().get(key.getCrawlerStatusId()), key, 5);

					if (Systemconfig.crawlerStatus.allCrawled()) {
						Systemconfig.crawlerStatus.setStatus("COMPLETE");
						Systemconfig.dbService.updateStatus(Systemconfig.crawlerStatus, null, key, 6);
					}
				}

				Systemconfig.sysLog.log("关键词：[" + key.getKey() + "] " + data.getTitle() + "保存完成。。。");
				synchronized (key) {
					key.savedCountIncrease();
				}
				/* 状态 */
				Systemconfig.crawlerStatus.getTasks().get(key.getCrawlerStatusId()).setSavedCount(key.getSavedCount());
				Systemconfig.crawlerStatus.getTasks().get(key.getCrawlerStatusId()).setDownCount(curr);

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
				Systemconfig.crawlerStatus.getTasks().get(key.getCrawlerStatusId()).setIfCrawled(true);
				Systemconfig.crawlerStatus.getTasks().get(key.getCrawlerStatusId()).setStatus("COMPLETE");
				Systemconfig.dbService.updateStatus(Systemconfig.crawlerStatus,
						Systemconfig.crawlerStatus.getTasks().get(key.getCrawlerStatusId()), key, 5);

				if (Systemconfig.crawlerStatus.allCrawled()) {
					Systemconfig.crawlerStatus.setStatus("COMPLETE");
					Systemconfig.dbService.updateStatus(Systemconfig.crawlerStatus, null, key, 6);
				}
			}
			Systemconfig.dbService.updateStatus(Systemconfig.crawlerStatus,
					Systemconfig.crawlerStatus.getTasks().get(key.getCrawlerStatusId()), key, 4);
			try {
				Systemconfig.dbService.saveLog(siteFlag, key, 3, url + "\r\n" + e.getMessage());
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		} finally {
			if (count != null)
				count.countDown();
			/* 状态 */
			Systemconfig.dbService.updateStatus(Systemconfig.crawlerStatus,
					Systemconfig.crawlerStatus.getTasks().get(key.getCrawlerStatusId()), key, 4);
			Systemconfig.sysLog.log("当前collect进度: " + data.getCompleteSize());
			TimeUtil.rest(1);
		}
	}

}
