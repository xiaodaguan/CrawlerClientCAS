package common.download.weibo;

import java.util.concurrent.CountDownLatch;

import common.pojos.HtmlInfo;
import common.pojos.WeiboData;
import common.download.GenericDataCommonDownload;
import common.rmi.packet.SearchKey;
import common.rmi.packet.ViewInfo;
import common.rmi.packet.ViewInfo.InnerInfo;
import common.system.Systemconfig;
import common.system.UserAttribute;
import common.system.UserManager;
import common.util.TimeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 下载详细页面
 * 
 * @author grs
 */
public class WeiboSearchDataCommonDownload extends GenericDataCommonDownload<WeiboData> implements Runnable {
	private static final Logger LOGGER = LoggerFactory.getLogger(WeiboSearchDataCommonDownload.class);

	private UserAttribute userAttribute;

	public WeiboSearchDataCommonDownload(String siteFlag, WeiboData data, CountDownLatch endCount, UserAttribute user, SearchKey key) {
		super(siteFlag, data, endCount, key);

	}
	
	@Override
	public void prePorcess() {
		InnerInfo ii = null;
		if (!siteinfo.getLogin())// 不需要登陆
			return;

		// 每次保证只有有效用户个执行，某个任务完成后，等待的下一个任务开始执行
		UserAttribute ua = UserManager.getUser(siteFlag);
		while (ua == null) {
			LOGGER.info("暂时没有可用账号用于采集，等待账号中……");
			TimeUtil.rest(10);
			ua = UserManager.getUser(siteFlag);
		}
		userAttribute = ua;
		if (!userAttribute.getHadRun()) {
			http.monitorLogin(userAttribute);
			ua.setHadRun(true);
			LOGGER.info("监测用户！！！" + userAttribute.getName());
		}
		LOGGER.info("用户" + userAttribute.getName() + "使用中！");
		if (ii != null) {
			ii.setAccountId(ua.getId());
			ii.setAccount(ua.getName());
			ii.setAccountTip("账号使用中！");
		}
	}

	public void process() {
		String url = getRealUrl(data);
		if (url == null)
			return;
		// 检测是否需要代理，未来版本改进
		siteinfo.setAgent(false);
		HtmlInfo html = htmlInfo("DATA");

		try {
			if (url != null && !url.equals("")) {
				html.setOrignUrl(url);

				http.getContent(html, userAttribute);
				// html.setContent();
				if (html.getContent() == null) {
					return;
				}
				// 解析数据
				xpath.templateContentPage(data, html);

				LOGGER.info(data.getTitle() + "解析完成。。。");
				Systemconfig.dbService.saveData(data);
				LOGGER.info(data.getTitle() + "保存完成。。。");
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
			LOGGER.info("采集出现异常" + url, e);
		} finally {
			if (count != null)
				count.countDown();
		}
	}

}
