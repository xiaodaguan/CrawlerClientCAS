package test;

import com.gargoylesoftware.htmlunit.*;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;
import com.gargoylesoftware.htmlunit.javascript.JavaScriptErrorListener;
import com.gargoylesoftware.htmlunit.util.Cookie;
import org.junit.Test;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Administrator on 2017/6/15 0015.
 */
public class testWebClient {




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
    @Test
    public void testMain() {

        String url = "http://weixin.sogou.com/";
        HtmlPage page = null;
        try {

            page = client.getPage(url);

            //HtmlSubmitInput button = page.getelementby("搜文章");
            HtmlForm htmlform  = page.getFormByName("searchForm");
            HtmlTextInput textField = htmlform.getInputByName("query");
            textField.setValueAttribute("郭德纲");
            htmlform.getButtonByName("").click();

        }catch(Exception e){
            e.printStackTrace();
        }



        client.waitForBackgroundJavaScript(1000*10);
        String content = page.asText();
        String source = page.asXml();


    }

}
