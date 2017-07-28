package common.downloader.weibo;

import java.io.*;
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



	public WeiboSearchDownload(CrawlTask task){
		super(task);
	}


	private UserAttribute userAttr;

	private void prePorcess() {



		System.out.println("#############################################");
		System.out.println("#############################################");
		System.out.println("#############################################");
		System.out.println("############# in weibo ######################");
		System.out.println("#############################################");
		System.out.println("#############################################");
		System.out.println("#############################################");


		if(true)
			return ;


		Siteinfo siteinfo = Systemconfig.allSiteinfos.get(task.getSite());

		String siteFlag = "7";

		// 每次保证只有有效用户个执行，某个任务完成后，等待的下一个任务开始执行
		UserAttribute ua = UserManager.getUser(siteFlag);
		while (ua == null) {
			LOGGER.info("暂时没有可用账号用于采集，等待账号中……");
			TimeUtil.rest(10);
			ua = UserManager.getUser(siteFlag);
		}
		userAttr = ua;


		SinaHttpProcess  http = new SinaHttpProcess();
		if (!userAttr.getHadRun()) {
			http.monitorLogin(userAttr);
			ua.setHadRun(true);
			System.out.println("监测用户！！！" + userAttr.getName());
		}
		LOGGER.info("用户{}使用中！",userAttr.getName());
		task.setUser(userAttr);
	}




	@Override
	public void download() {


		prePorcess();


		System.out.println();
		if(true)
		return ;
		String cookie = task.getUser().getCookie();
		Request request = new Request.Builder()
				.url(task.getOrignUrl())

				.addHeader("User-Agent",task.getUa()==null? "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/59.0.3071.115 Safari/537.36":task.getUa())
				.addHeader("Cookie",cookie)
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

	private void postPorcess() {





	}

}
