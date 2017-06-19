package common.http.sub;


import com.gargoylesoftware.htmlunit.*;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.javascript.JavaScriptErrorListener;
import com.gargoylesoftware.htmlunit.util.Cookie;
import common.bean.HtmlInfo;
import common.http.SimpleHttpProcess;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.util.EntityUtils;

import java.net.MalformedURLException;
import java.net.URL;


public class WeixinHttpProcess extends SimpleHttpProcess {

    //static BrowserVersion qiyu = new BrowserVersion("qiyu","qiyubrowser","Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.63 Safari/537.36 Qiyu/2.1.0.0",0);

//    private static WebClient client = new WebClient(BrowserVersion.FIREFOX_38);
    private static WebClient client = new WebClient();

//    private static WebClient client = new WebClient(BrowserVersion.EDGE);
//    private static WebClient client = new WebClient(BrowserVersion.CHROME);
//    private static WebClient client = new WebClient(BrowserVersion.FIREFOX_45);

    static{

        String ua = "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/45.0.2454.101 Safari/537.36";
        client.addRequestHeader("User-Agent",ua);



//            client.addRequestHeader("cookie","ABTEST=0|1493366356|v1; SUID=8259DF1B1F2D940A000000005902F654; SUV=009F178C1BDF59825902F656D1AF7022; SNUID=7EA524E7FCFEB5C988AF8E3AFC18D927; SUID=8259DF1B1508990A000000005902F656; JSESSIONID=aaa_KUsDFSDRafY5oSUUv; IPLOC=CN3700");

        client.getCookieManager().setCookiesEnabled(true);

        client.getOptions().setThrowExceptionOnScriptError(false);
        client.getOptions().setCssEnabled(false);
        client.setCssErrorHandler(new SilentCssErrorHandler());
        client.setIncorrectnessListener(new IncorrectnessListener() {
            @Override
            public void notify(String s, Object o) {

            }
        });
        client.setJavaScriptErrorListener(new JavaScriptErrorListener() {
            @Override
            public void scriptException(InteractivePage interactivePage, ScriptException e) {

            }

            @Override
            public void timeoutError(InteractivePage interactivePage, long l, long l1) {

            }

            @Override
            public void malformedScriptURL(InteractivePage interactivePage, String s, MalformedURLException e) {

            }

            @Override
            public void loadScriptError(InteractivePage interactivePage, URL url, Exception e) {

            }
        });
    }

    public static void manuallySetCookie(String cookies) {
        client.getCookieManager().clearCookies();
//        String cookies = "ABTEST=0|1493366356|v1; SUID=8259DF1B1F2D940A000000005902F654; SUV=009F178C1BDF59825902F656D1AF7022; SNUID=7EA524E7FCFEB5C988AF8E3AFC18D927; SUID=8259DF1B1508990A000000005902F656; JSESSIONID=aaa_KUsDFSDRafY5oSUUv; IPLOC=CN3700";
        for (String single : cookies.split(";")) {
            if (single.contains("=")) {

                client.getCookieManager().addCookie(new Cookie("N/A", single.split("=")[0], single.split("=")[1]));

            }
        }
        System.out.println("cookie updated: "+client.getCookieManager().getCookies());
    }

    @Override
    public synchronized byte[] simpleGet(HtmlInfo html) throws Exception {
        HttpClient client = null;
        //创建httpget请求对象
        HttpGet get = null;
        try {
            //清空上次请求
            html.setContent(null);
            //将响应码设置为-1，如果整个过程能够顺利执行，会被设置为200,302等有效响应码


            get = new HttpGet(html.getOrignUrl().startsWith("http") ? html.getOrignUrl() : "https://" + html.getOrignUrl());
            if (html.getOrignUrl().contains("baidu")) {
                RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(5000).setConnectTimeout(5000).build();//设置请求和传输超时时间
                get.setConfig(requestConfig);
            }

            html.setContent(null);

            if (html.getUa() != null) {
                //设置请求头 user agent
                get.setHeader("User-Agent", html.getUa());
            }
            //设置请求头 connection
            get.setHeader("Connection", "keep-alive");
            if (html.getCookie() != null) {
                //设置请求头 cookie
                get.setHeader("Cookie", html.getCookie());
            }
            if (html.getReferUrl() != null) {
                //设置请求头 referer
                get.setHeader("Referer", html.getReferUrl());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 创建请求响应的可关闭对象（手动关闭）
        HttpResponse response = null;

        try {

            //执行get请求，并返回响应对象
            response = client.execute(get);

            if (response == null) {
                System.out.println("client.execute(get)出错！：response=null");
            }
            if (response.containsHeader("Location")) {
                //如果响应头中包含了location，一般为302跳转，跳转链接提取方式如下
                String realUrl = response.getFirstHeader("Location").getValue();
                //记录302跳转的链接
                html.setRealUrl(realUrl);
            }
            if (response != null && response.getStatusLine().getStatusCode() == 200) {
                //获取http get请求的实体对象
                HttpEntity httpEntity = response.getEntity();
                String result = null;
                try {
                    //获取http 请求的内容，并指定编码格式
                    result = EntityUtils.toString(httpEntity, html.getEncode());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                response.getEntity().getContent().close();
                //记录http get请求获得的内容
                html.setContent(result);
            }
        } catch(HttpHostConnectException |ClientProtocolException e)	{

        } catch(Exception e)		{

        }

        if(html.getContent()!=null) {
            return html.getContent().getBytes();
        }
        return null;
    }


    public synchronized byte[] simpleGet_bak(HtmlInfo html) throws Exception {




        HtmlPage page = client.getPage(html.getOrignUrl());

        client.waitForBackgroundJavaScript(1000*10);
        String content = page.asText();
        String source = page.asXml();

        return source.getBytes();
    }


}
