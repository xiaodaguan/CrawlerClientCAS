package common.http;

import common.bean.HtmlInfo;
import org.junit.Assert;
import org.junit.Test;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by guanxiaoda on 2017/6/20.
 */
public class SimpleHttpProcessTest {

    private static  SimpleHttpProcess shp = new SimpleHttpProcess();
    @Test
    public void testRandomUA(){
        System.out.println(shp.getRandomUserAgent());
    }

    @Test
    public void testFlushProxy(){
        SimpleHttpProcess simpleHttpProcess = new SimpleHttpProcess();
        System.out.println(simpleHttpProcess.proxies);
        Assert.assertNotEquals(0,simpleHttpProcess.proxies.size());
    }

    @Test
    public void testNewsGet(){
        HtmlInfo htmlInfo =  new HtmlInfo();


        htmlInfo.setType("META" + File.separator + "baidu_search_test");
        htmlInfo.setMaxRetryTimes(10);

        htmlInfo.setEncode("utf-8");
        htmlInfo.setAgent(false);
        htmlInfo.setOrignUrl("http://news.baidu.com/ns?word=%E9%9D%92%E5%B2%9B%E4%BA%A4%E9%80%9A&tn=news&from=news&cl=2&rn=20&ct=1");
        htmlInfo.setUa("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36");
        SimpleHttpProcess httpProcess = new SimpleHttpProcess();
        httpProcess.getContent(htmlInfo);

        System.out.println(htmlInfo.getContent());
    }

    @Test
    public void test1() throws IOException {
        URL url = new URL("http://news.baidu.com/ns?word=%E9%9D%92%E5%B2%9B%E4%BA%A4%E9%80%9A&tn=news&from=news&cl=2&rn=20&ct=1");
        URLConnection conn = url.openConnection();
        conn.addRequestProperty("User-Agent","Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36");

        conn.connect();
        InputStream is = conn.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        String line;
        StringBuilder sb = new StringBuilder();
        while((line = reader.readLine())!= null){
            sb.append(line).append("\r\n");

        }
        System.out.println(sb.toString());

    }
}
