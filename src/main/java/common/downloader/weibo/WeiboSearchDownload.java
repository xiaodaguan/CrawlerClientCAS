package common.downloader.weibo;

import java.io.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;


import common.Crawler;
import common.downloader.RetryInterceptor;
import common.pojos.CrawlTask;
import common.system.Systemconfig;

import common.system.UserAttribute;
import common.system.UserManager;

import common.downloader.DefaultDownloader;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * weibo下载详细页面
 * 
 * @author rzy
 */
public class WeiboSearchDownload extends DefaultDownloader {
	private static final Logger LOGGER = LoggerFactory.getLogger(WeiboSearchDownload.class);

	private static OkHttpClient httpClient;
	private static CrawlTask task;

	public WeiboSearchDownload(CrawlTask task){
		super(task);
		this.task = task;
		httpClient = new OkHttpClient().newBuilder()
				.addInterceptor(new RetryInterceptor(task.getRetryTimes()))
				.connectTimeout(15, TimeUnit.SECONDS)
				.readTimeout(300,  TimeUnit.SECONDS)
				.writeTimeout(300,  TimeUnit.SECONDS)
				.build();


	}

	@Override
	public void download() {


		Request request = new Request.Builder()
				.url(task.getOrignUrl())

				.addHeader("User-Agent",task.getUa()==null? "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/59.0.3071.115 Safari/537.36":task.getUa())
				/**
				 * 可以扩展其他header
				 */
				.build();
		Response response = null;
		try {
			response = httpClient.newCall(request).execute();
		} catch (IOException e) {
			LOGGER.error("httpClient 请求失败. url: {}",task.getOrignUrl());
			e.printStackTrace();
		}

		if(!response.isSuccessful()){
			LOGGER.error("downloader 下载失败. url: {}",task.getOrignUrl());
		}

		InputStream inputStream = response.body().byteStream();
		BufferedReader reader = null;
		String line = null;
		StringBuilder builder = new StringBuilder();
		try {
			reader = new BufferedReader(new InputStreamReader(inputStream, task.getEncode()==null? "UTF-8":task.getEncode()));
			while((line = reader.readLine())!= null){
				builder.append(line).append("\r\n");
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		task.setContent(builder.toString());
		if(task.getContent()==null)
			LOGGER.error("downloader content failure. url:{}", task.getOrignUrl());

//        response.body();
	}
	/*private UserAttribute userAttr;

	public void prePorcess() {
		InnerInfo ii = null;
		if (Systemconfig.clientinfo != null) {
			ViewInfo vi = Systemconfig.clientinfo.getViewinfos().get(Systemconfig.localAddress + "_" + siteFlag);
			ii = vi.getCrawlers().get(key.getKey());
			ii.setAlive(1);
		}
		if (!siteinfo.getLogin())// 不需要登陆
			return;

		// 每次保证只有有效用户个执行，某个任务完成后，等待的下一个任务开始执行
		UserAttribute ua = UserManager.getUser(siteFlag);
		while (ua == null) {
			Systemconfig.sysLog.log("暂时没有可用账号用于采集，等待账号中……");
			TimeUtil.rest(10);
			ua = UserManager.getUser(siteFlag);
		}
		userAttr = ua;
		if (!userAttr.getHadRun()) {
			http.monitorLogin(userAttr);
			ua.setHadRun(true);
			System.out.println("监测用户！！！" + userAttr.getName());
		}
		Systemconfig.sysLog.log("用户" + userAttr.getName() + "使用中！");
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
		CrawlTask html = htmlInfo("DATA");

		try {
			if (url != null && !url.equals("")) {
				html.setOrignUrl(url);

				http.getContent(html, userAttr);
				// html.setContent();
				if (html.getContent() == null) {
					return;
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
			Systemconfig.sysLog.log("采集出现异常" + url, e);
		} finally {
			if (count != null)
				count.countDown();
		}
	}
	*/

}
