package common.downloader;

import common.pojos.CrawlTask;
import common.system.Systemconfig;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.concurrent.TimeUnit;

/**
 * Created by guanxiaoda on 2017/7/18.
 */
public class DefaultDownloader implements DownloaderIterface {
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultDownloader.class);

    protected static OkHttpClient httpClient;
    protected static CrawlTask task;
    protected static OkHttpClient.Builder clientBuilder;

    public DefaultDownloader(CrawlTask task) {
        this.task = task;
        clientBuilder = new OkHttpClient().newBuilder()
                .addInterceptor(new RetryInterceptor(task.getRetryTimes()))
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS)

        ;


    }

    @Override
    public void download() {

        if (task.getAgent()) {
            String hostPort = Systemconfig.proxyPoolRedis.randomGetOne();
            LOGGER.info("为本次请求设置代理[{}]",hostPort);
            clientBuilder.proxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress(hostPort.split(":")[0], Integer.parseInt(hostPort.split(":")[1]))));
        }
        httpClient = clientBuilder.build();

        Request request = new Request.Builder()
                .url(task.getOrignUrl())

                .addHeader("User-Agent", task.getUa() == null ? "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/59.0.3071.115 Safari/537.36" : task.getUa())
                /**
                 * 可以扩展其他header
                 */
                .build();
        Response response = null;
        try {
            response = httpClient.newCall(request).execute();
        } catch (IOException e) {
            LOGGER.error("httpClient 请求失败. url: {}", task.getOrignUrl());
            e.printStackTrace();
        }

        if (!response.isSuccessful()) {
            LOGGER.error("downloader 下载失败. url: {}", task.getOrignUrl());
        }

        InputStream inputStream = response.body().byteStream();
        BufferedReader reader = null;
        String line = null;
        StringBuilder builder = new StringBuilder();
        try {
            reader = new BufferedReader(new InputStreamReader(inputStream, task.getEncode() == null ? "UTF-8" : task.getEncode()));
            while ((line = reader.readLine()) != null) {
                builder.append(line).append("\r\n");
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            response.body().close();
            response.close();
        }

        task.setContent(builder.toString());
        if (task.getContent() == null)
            LOGGER.error("downloader content failure. url:{}", task.getOrignUrl());

//        response.body();
    }

}
