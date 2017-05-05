package common.http.sub;


import com.gargoylesoftware.htmlunit.*;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.javascript.JavaScriptErrorListener;
import com.gargoylesoftware.htmlunit.util.Cookie;
import common.bean.HtmlInfo;
import common.http.SimpleHttpProcess;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;


public class WeixinHttpProcess extends SimpleHttpProcess {

    static BrowserVersion qiyu = new BrowserVersion("qiyu","qiyubrowser","Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.63 Safari/537.36 Qiyu/2.1.0.0",0);

//    private static WebClient client = new WebClient(BrowserVersion.FIREFOX_38);
    private static WebClient client = new WebClient(qiyu);

//    private static WebClient client = new WebClient(BrowserVersion.EDGE);
//    private static WebClient client = new WebClient(BrowserVersion.CHROME);
//    private static WebClient client = new WebClient(BrowserVersion.FIREFOX_45);

    static{

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





        HtmlPage page = client.getPage(html.getOrignUrl());

        client.waitForBackgroundJavaScript(1000*10);
        String content = page.asText();
        String source = page.asXml();

        return source.getBytes();
    }


}
