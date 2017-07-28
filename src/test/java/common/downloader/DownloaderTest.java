package common.downloader;

import common.pojos.CrawlTask;
import common.system.AppContext;
import common.system.Systemconfig;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.*;
import java.util.concurrent.TimeUnit;

/**
 * Created by guanxiaoda on 2017/7/18.
 */
public class DownloaderTest {

    @BeforeClass
    public static void beforeAll(){
        Systemconfig.crawlerType = 1;

        String path = Thread.currentThread().getContextClassLoader().getResource("").getPath();
        AppContext.initAppCtx(path);
    }


    @Test
    public void DownTest() throws IOException {
//        CrawlTask task = new CrawlTask();
//        task.setOrignUrl("http://guanxiaoda.cn");
//        task.setRetryTimes(3);

        CrawlTask task = Systemconfig.scheduler.getTask();
        DefaultDownloader downloader = new DefaultDownloader(task);
        downloader.download();

        System.out.println(task.getContent());
    }

    @Test
    public void proxyDownTest() throws IOException {
        CrawlTask task = new CrawlTask();
        task.setOrignUrl("http://www.whatismyip.com.tw/");
        task.setAgent(true);
        task.setEncode("utf-8");
        task.setCrawlerType("data");
        task.setSite("test");
        task.setMediaType(1);
        DefaultDownloader downloader = new DefaultDownloader(task);
        downloader.download();
        System.out.println(task.getContent());
    }
    @Test
    public void okHttpTest() throws IOException {



        String url = "http://s.weibo.com/weibo/%25E4%25B8%25AD%25E5%259B%25BD%25E6%2589%258B%25E6%259C%25BA%25E5%25A5%25BD%25E7%2594%25A8%25E5%2588%25B0%25E9%259A%25BE%25E4%25BB%25A5%25E7%25BD%25AE%25E4%25BF%25A1?topnav=1&wvr=6&Refer=top_hot";


        String cookie = "SINAGLOBAL=716716251336.0381.1500174700637; UM_distinctid=15d5ef29e23f1-09069fb84-722e3659-140000-15d5ef29e2478; un=15841920324; UOR=,,login.sina.com.cn; un=1354805597rzy@sina.com; wvr=6; SSOLoginState=1501225072; SCF=AnuvTe-9uSaubZg2U2czIFcCMEm7i5K58zGkG1HnYMrB9ur_9sULjW6E_K1Bny6f9X7hv8Fz0v8Q0G6xiiVAyNg.; SUB=_2A250fpAgDeRhGeRK61UZ8SbIzz2IHXVXDYborDV8PUNbmtBeLULRkW8aq2fz8EREMyhiW3YFINHAN-iUbg..; SUBP=0033WrSXqPxfM725Ws9jqgMF55529P9D9WFLaUa9E2iGSFaS52z1uGc35JpX5KMhUgL.FozXehMReKnXSh22dJLoIpjLxKqLBozLBKnLxK-LB-BL1K5LxK.LBo2LB.et; SUHB=0nlvn8Tz0qbNFt; ALF=1532761070; _s_tentry=-; Apache=5692819065880.03.1501225079464; ULV=1501225079508:16:16:6:5692819065880.03.1501225079464:1501118360031";
        String Accept = "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8";
        String Accept_Encoding = "gzip, deflate, sdch";
        String Accept_Language="zh-CN,zh;q=0.8";
        String Connection="keep-alive";
        String Host="s.weibo.com";
        String Referer="http://weibo.com/ziyue246/home?wvr=5&uut=fin&from=reg";

        String ua = "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/45.0.2454.101 Safari/537.36 QIHU 360SE";
        String Upgrade_Insecure_Requests="1";

        System.out.println("cookie:"+cookie);
        Request request = new Request.Builder()
                .url(url)

                .addHeader("User-Agent",ua)
                .addHeader("Accept",Accept)
                .addHeader("Accept_Encoding",Accept_Encoding)
                .addHeader("Accept_Language",Accept_Language)
                .addHeader("Connection",Connection)
                .addHeader("Host",Host)
                .addHeader("Upgrade_Insecure_Requests",Upgrade_Insecure_Requests)
                .addHeader("Cookie",cookie)
                /**
                 * 可以扩展其他header
                 */
                .build();



        Object obj = new Request.Builder();
        ((Request.Builder)obj).url(url);
        ((Request.Builder)obj).addHeader("User-Agent",ua);
        ((Request.Builder)obj).addHeader("Accept",Accept);
        ((Request.Builder)obj).addHeader("Accept_Encoding",Accept_Encoding);
        ((Request.Builder)obj).addHeader("Accept_Language",Accept_Language);
        ((Request.Builder)obj).addHeader("Connection",Connection);
        ((Request.Builder)obj).addHeader("Host",Host);
        ((Request.Builder)obj).addHeader("Upgrade_Insecure_Requests",Upgrade_Insecure_Requests);

        if(cookie!=null){
            ((Request.Builder)obj).addHeader("Cookie",cookie);
        }
        //Request request = ((Request.Builder)obj).build();



        System.out.println("request"+request.headers());

        Response response = null;

        OkHttpClient httpClient = null;
        OkHttpClient.Builder clientBuilder;

        clientBuilder = new OkHttpClient().newBuilder()
                .addInterceptor(new RetryInterceptor(1231))
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS);
        httpClient = clientBuilder.build();
        try {
            response = httpClient.newCall(request).execute();
        } catch (IOException e) {

            e.printStackTrace();
        }



        InputStream inputStream = response.body().byteStream();
        BufferedReader reader = null;
        String line = null;
        StringBuilder builder = new StringBuilder();
        try {
            reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            while((line = reader.readLine())!= null){
                builder.append(line).append("\r\n");
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
