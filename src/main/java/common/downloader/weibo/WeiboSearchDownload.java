package common.downloader.weibo;

import java.io.*;
import java.util.Date;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;


import common.Crawler;
import common.downloader.RetryInterceptor;
import common.http.sub.SinaHttpProcess;
import common.pojos.CrawlTask;
import common.siteinfo.Siteinfo;
import common.system.Systemconfig;

import common.system.UserAttribute;
import common.system.UserManager;

import common.downloader.DefaultDownloader;
import common.utils.TimeUtil;
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

	private static SinaHttpProcess  http ;

	public WeiboSearchDownload(CrawlTask task){
		super(task);
	}

	private void prePorcess() {

		Siteinfo siteinfo = Systemconfig.allSiteinfos.get(task.getSite());

		String siteFlag = task.getSite();

		// 每次保证只有有效用户个执行，某个任务完成后，等待的下一个任务开始执行
		UserAttribute userAttr_tmp = UserManager.getUser(siteFlag);
		while (userAttr_tmp == null) {
			LOGGER.info("暂时没有可用账号用于采集，等待账号中……");
			TimeUtil.rest(10);
			userAttr_tmp = UserManager.getUser(siteFlag);
		}
		http = new SinaHttpProcess();
		if ((!userAttr_tmp.getHadRun())||
				userAttr_tmp.getCookie()==null||
				!http.verify(userAttr_tmp)){
			//http.monitorLogin(userAttr_tmp);
			if(!http.login(userAttr_tmp)){
				LOGGER.info("监测用户{},登陆失败",userAttr_tmp.getName());
				return ;
			}

			userAttr_tmp.setTryCount(0);
			userAttr_tmp.setHadRun(true);
			LOGGER.info("监测用户{}",userAttr_tmp.getName());
			TimeUtil.rest(5);
		}
		//微博账号使用的时间间隔
		if(userAttr_tmp.getLastUsedTime()!=null){
			long currentTime = System.currentTimeMillis();
			long lastTime = userAttr_tmp.getLastUsedTime().getTime();
			while((currentTime-lastTime)<task.getInterval()*1000){
				currentTime = System.currentTimeMillis();
				TimeUtil.rest(5);
			}
		}
		userAttr_tmp.setLastUsedTime(new Date());
		LOGGER.info("用户{}使用中！",userAttr_tmp.getName());
		task.setUser(userAttr_tmp);
	}

	@Override
	public void download() {
		prePorcess();
		if(task.getUser()==null){
			return ;
		}
		String cookie = task.getUser().getCookie();
		String Accept = "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8";
		String Accept_Encoding = "gzip, deflate, sdch";
		String Accept_Language="zh-CN,zh;q=0.8";
		String Connection="keep-alive";
		//String Host="s.weibo.com";
		String Upgrade_Insecure_Requests="1";
		httpClient = clientBuilder.build();

		Object obj = new Request.Builder();
		((Request.Builder)obj).url(task.getOrignUrl());
		((Request.Builder)obj).addHeader("User-Agent",task.getUa()==null? "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/59.0.3071.115 Safari/537.36":task.getUa());
		((Request.Builder)obj).addHeader("Accept",Accept);
		((Request.Builder)obj).addHeader("Accept_Encoding",Accept_Encoding);
		((Request.Builder)obj).addHeader("Accept_Language",Accept_Language);
		((Request.Builder)obj).addHeader("Connection",Connection);
//		//((Request.Builder)obj).addHeader("Host",Host);
//		((Request.Builder)obj).addHeader("Upgrade_Insecure_Requests",Upgrade_Insecure_Requests);

		if(cookie!=null){
			((Request.Builder)obj).addHeader("Cookie",cookie);
		}
		Request request = ((Request.Builder)obj).build();

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
		postPorcess();
	}
	private void postPorcess() {
		UserAttribute userAttr = task.getUser();
		UserManager.releaseUser(task.getSite(),userAttr);
	}

}
