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
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.net.MalformedURLException;
import java.net.URL;


public class WeixinHttpProcess extends SimpleHttpProcess {


    public static void manuallySetCookie(String cookies) {

    }

    @Override
    public synchronized byte[] simpleGet(HtmlInfo html) throws Exception {
        HttpClient client = HttpClients.createDefault();
        //创建httpget请求对象
        HttpGet get = null;
        //System.out.println("weixin http simpleget");
        try {
            //清空上次请求
            html.setContent(null);
            //将响应码设置为-1，如果整个过程能够顺利执行，会被设置为200,302等有效响应码


            get = new HttpGet(html.getOrignUrl().startsWith("http") ? html.getOrignUrl() : "https://" + html.getOrignUrl());



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
                //System.out.println("html content:"+result);
                html.setContent(result);
            }
        } catch(HttpHostConnectException |ClientProtocolException e)	{
            e.printStackTrace();
        } catch(Exception e)		{
            e.printStackTrace();
        }

        if(html.getContent()!=null) {
            return html.getContent().getBytes();
        }
        return null;
    }


}
