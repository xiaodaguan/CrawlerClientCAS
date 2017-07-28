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

	private static SinaHttpProcess  http ;

	public WeiboSearchDownload(CrawlTask task){
		super(task);
	}


	//site[sina_weibo_search],
	// url[http://s.weibo.com/weibo/购车人&nodup=1&page=1],
	// crawlerType[META],
	// mediaType[0],
	// searchkey[
		// CategoryCode[0],
		// Keyword[购车人],
		// SiteId[{test}],
		// SiteName[null]
	// ]

	/**
	 *
	 */
	private void prePorcess() {

		System.out.println("#############################################");
		System.out.println("############# in weibo ######################");
		System.out.println("#############################################");

		Siteinfo siteinfo = Systemconfig.allSiteinfos.get(task.getSite());

		String siteFlag = task.getSite();

		// 每次保证只有有效用户个执行，某个任务完成后，等待的下一个任务开始执行
		UserAttribute userAttr_tmp = UserManager.getUser(siteFlag);
		while (userAttr_tmp == null) {
			LOGGER.info("暂时没有可用账号用于采集，等待账号中……");
			TimeUtil.rest(10);
			userAttr_tmp = UserManager.getUser(siteFlag);
		}


		if ((!userAttr_tmp.getHadRun())||userAttr_tmp.getCookie()==null){
			http = new SinaHttpProcess();
			//http.monitorLogin(userAttr_tmp);
			http.login(userAttr_tmp);
			userAttr_tmp.setHadRun(true);
			LOGGER.info("监测用户{}",userAttr_tmp.getName());
		}
		LOGGER.info("用户{}使用中！",userAttr_tmp.getName());
		task.setUser(userAttr_tmp);
	}

	@Override
	public void download() {

		prePorcess();

		String cookie = task.getUser().getCookie();
		//cookie = "SINAGLOBAL=716716251336.0381.1500174700637; UM_distinctid=15d5ef29e23f1-09069fb84-722e3659-140000-15d5ef29e2478; un=15841920324; UOR=,,login.sina.com.cn; un=1354805597rzy@sina.com; wvr=6; SSOLoginState=1501225072; SCF=AnuvTe-9uSaubZg2U2czIFcCMEm7i5K58zGkG1HnYMrB9ur_9sULjW6E_K1Bny6f9X7hv8Fz0v8Q0G6xiiVAyNg.; SUB=_2A250fpAgDeRhGeRK61UZ8SbIzz2IHXVXDYborDV8PUNbmtBeLULRkW8aq2fz8EREMyhiW3YFINHAN-iUbg..; SUBP=0033WrSXqPxfM725Ws9jqgMF55529P9D9WFLaUa9E2iGSFaS52z1uGc35JpX5KMhUgL.FozXehMReKnXSh22dJLoIpjLxKqLBozLBKnLxK-LB-BL1K5LxK.LBo2LB.et; SUHB=0nlvn8Tz0qbNFt; ALF=1532761070; _s_tentry=-; Apache=5692819065880.03.1501225079464; ULV=1501225079508:16:16:6:5692819065880.03.1501225079464:1501118360031";

		String Accept = "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8";
		String Accept_Encoding = "gzip, deflate, sdch";
		String Accept_Language="zh-CN,zh;q=0.8";
		String Connection="keep-alive";
		String Host="s.weibo.com";
		String Referer="http://weibo.com/ziyue246/home?wvr=5&uut=fin&from=reg";
		String Upgrade_Insecure_Requests="1";

		System.out.println("cookie:"+cookie);

		httpClient = clientBuilder.build();


		Object obj = new Request.Builder();
		((Request.Builder)obj).url(task.getOrignUrl());
		((Request.Builder)obj).addHeader("User-Agent",task.getUa()==null? "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/59.0.3071.115 Safari/537.36":task.getUa());
		((Request.Builder)obj).addHeader("Accept",Accept);
		((Request.Builder)obj).addHeader("Accept_Encoding",Accept_Encoding);
		((Request.Builder)obj).addHeader("Accept_Language",Accept_Language);
		((Request.Builder)obj).addHeader("Connection",Connection);
		((Request.Builder)obj).addHeader("Host",Host);
		((Request.Builder)obj).addHeader("Upgrade_Insecure_Requests",Upgrade_Insecure_Requests);

		if(cookie!=null){
			((Request.Builder)obj).addHeader("Cookie",cookie);
		}
		Request request = ((Request.Builder)obj).build();



		System.out.println("request：\n"+request.headers());

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
