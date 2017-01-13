package common.http.sub;


import com.gargoylesoftware.htmlunit.*;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.javascript.JavaScriptErrorListener;
import common.bean.HtmlInfo;
import common.http.SimpleHttpProcess;

import java.net.MalformedURLException;
import java.net.URL;


public class WeixinHttpProcess extends SimpleHttpProcess {

    private static WebClient client = new WebClient(BrowserVersion.FIREFOX_38);


    @Override
    public synchronized byte[] simpleGet(HtmlInfo html) throws Exception {

        {
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



        HtmlPage page = client.getPage(html.getOrignUrl());

        client.waitForBackgroundJavaScript(1000*10);
        String content = page.asText();
        String source = page.asXml();

        return source.getBytes();
    }


}
