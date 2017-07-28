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
 * Created by rzy on 2017/7/27.
 */
public abstract class AbastractDownloader implements DownloaderIterface {

    protected static OkHttpClient httpClient;
    protected static CrawlTask task;
    protected static OkHttpClient.Builder clientBuilder;



    @Override
    abstract public void download() throws IOException ;

}
