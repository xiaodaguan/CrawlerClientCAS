package common.http;

import common.bean.HtmlInfo;
import common.system.Systemconfig;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by guanxiaoda on 2017/6/20.
 */
public class SimpleHttpProcessTest {

    @BeforeClass
    public static void beforeAll() {
        Systemconfig.sysLog.start();
    }

    private static SimpleHttpProcess shp = new SimpleHttpProcess();

    @Test
    public void testRandomUA() {
        System.out.println(shp.getRandomUserAgent());
    }


    @Test
    public void testGet() {
        HtmlInfo htmlInfo = new HtmlInfo();


        htmlInfo.setType("META" + File.separator + "test");
        htmlInfo.setMaxRetryTimes(10);

        htmlInfo.setEncode("utf-8");
        htmlInfo.setAgent(false);
//        htmlInfo.setAgent(true);
//        htmlInfo.setOrignUrl("http://www.whatismyip.com.tw/");
        htmlInfo.setOrignUrl("http://weixin.sogou.com/weixin?type=2&query=%E9%9D%92%E5%B2%9B%E4%BA%A4%E9%80%9A");
//        htmlInfo.setOrignUrl("http://weixin.sogou.com");
        htmlInfo.setUa("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36");
        SimpleHttpProcess httpProcess = new SimpleHttpProcess();

        int right = 0;
        int block = 0;
        for (int i = 0; i < 10; i++) {
            httpProcess.getContent(htmlInfo);
            if (htmlInfo.getContent().contains("的相关微信公众号文章"))
                right++;
            if (htmlInfo.getContent().contains("您的访问过于频繁"))
                block++;


        }
        System.out.println(right);
        System.out.println(block);
        Assert.assertEquals(10, right);
        Assert.assertEquals(0,block);
    }

    @Test
    public void test1() throws IOException {
//        URL url = new URL("http://news.baidu.com/ns?word=%E9%9D%92%E5%B2%9B%E4%BA%A4%E9%80%9A&tn=news&from=news&cl=2&rn=20&ct=1");
        URL url = new URL("http://www.whatismyip.com.tw/");
        URLConnection conn = url.openConnection();
        conn.addRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36");

        conn.connect();
        InputStream is = conn.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        String line;
        StringBuilder sb = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            if(line.contains("IP位址"))
            sb.append(line).append("\r\n");

        }
        System.out.println(sb.toString());

    }
}
