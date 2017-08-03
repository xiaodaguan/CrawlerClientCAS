package common.http;


import java.io.*;
import java.net.HttpURLConnection;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.zip.GZIPInputStream;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.io.IOUtils;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import common.bean.HtmlInfo;
import common.system.Systemconfig;
import common.system.UserAttr;
import common.util.CharsetDetector;
import common.util.EncoderUtil;
import common.util.MD5Util;
import common.util.StringUtil;
import common.util.UserAgent;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;

/**
 * 普通http请求处理
 *
 * @author grs
 * @since 2014年1月
 */
public class SimpleHttpProcess implements HttpProcess {

    private static final String[] charsets = new String[]{"utf-8", "gbk", "gb2312", "big5"};
    protected int readTime = 180000;
    protected int connectNum = 3;
    protected int requestTime = 120000;
    protected static final String userAgent = UserAgent.getUserAgent();
    /**
     * 创建连接
     *
     * @param module
     * @return
     */
    protected static final ConcurrentHashMap<String, HttpClient> clientMap = new ConcurrentHashMap<String, HttpClient>();


    int count = 0;


    private static List<String> userAgentList = new ArrayList<>();
    private static JedisCluster redis;

    static {
        userAgentList.add("Mozilla/5.0 (compatible; WOW64; MSIE 10.0; Windows NT 6.2)");
        userAgentList.add("Mozilla/5.0 (iPhone; CPU iPhone OS 6_1_4 like Mac OS X) AppleWebKit/536.26 (KHTML, like Gecko) CriOS/27.0.1453.10 Mobile/10B350 Safari/8536.25");
        userAgentList.add("Mozilla/5.0 (Windows NT 6.2; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/27.0.1453.94 Safari/537.36");
        userAgentList.add("Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; Trident/5.0)");
        userAgentList.add("Mozilla/5.0 (iPad; CPU OS 5_0 like Mac OS X) AppleWebKit/534.46 (KHTML, like Gecko) Version/5.1 Mobile/9A334 Safari/7534.48.3");
        userAgentList.add("Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10_6_6; en-US) AppleWebKit/533.20.25 (KHTML, like Gecko) Version/5.0.4 Safari/533.20.27");
        userAgentList.add("Opera/9.80 (Macintosh; Intel Mac OS X 10.6.8; U; en) Presto/2.9.168 Version/11.52");

        Set<HostAndPort> hostAndPortSet = new HashSet<>();
        hostAndPortSet.add(new HostAndPort("localhost", 7001));
        hostAndPortSet.add(new HostAndPort("localhost", 7002));
        hostAndPortSet.add(new HostAndPort("localhost", 7003));
        hostAndPortSet.add(new HostAndPort("localhost", 8001));
        hostAndPortSet.add(new HostAndPort("localhost", 8002));
        hostAndPortSet.add(new HostAndPort("localhost", 8003));

        try {
            redis = new JedisCluster(hostAndPortSet);
        }catch (Exception e){
            Systemconfig.sysLog.log("连接redis集群失败", e);
        }
    }

    public static String getRandomUserAgent() {
        int random = new Random().nextInt(userAgentList.size());
        return userAgentList.get(random == userAgentList.size() ? random - 1 : random);
    }



    private static final String PROXY_POOL = "PROXY_POOL";

    public synchronized static String getRandomProxyFromPool() {

            while(redis.llen(PROXY_POOL) == 0){

                Systemconfig.sysLog.log("当前代理池中无代理ip，等待重试..");
                try {
                    Thread.sleep(1000*10);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            int randomIndex =  new Random().nextInt(Math.toIntExact(redis.llen(PROXY_POOL)));
            String proxyHostAndPort = redis.lindex(PROXY_POOL, randomIndex);
            return proxyHostAndPort;
    }

    public synchronized static String getRandomProxyFromProvider() {
        try {
            java.net.URL url = new java.net.URL("http://117.132.15.89:8090/proxy");
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setConnectTimeout(3000);
            connection = (HttpURLConnection)url.openConnection();

            InputStream raw = connection.getInputStream();
            InputStream in = new BufferedInputStream(raw);
            byte[] data = new byte[in.available()];
            int bytesRead = 0;
            int offset = 0;
            while(offset < data.length) {
                bytesRead = in.read(data, offset, data.length - offset);
                if(bytesRead == -1) {
                    break;
                }
                offset += bytesRead;
            }
            in.close();
            raw.close();
            String[] res = new String(data, "UTF-8").split("\n");
            List ipList = new ArrayList<>();
            for (String ip : res) {
                try {
                    if(!ip.contains(","))
                        ip+=",66666";
                    String[] parts = ip.split(",");
                    if (Integer.parseInt(parts[1]) > 0) {
                        ipList.add(parts[0]);
                    }
                } catch (Exception e) {
                }
            }
            if (ipList.size() > 0) {
//                Systemconfig.sysLog.log("获取动态IP:" + ipList );
                return (String) ipList.get(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(">>>>>>>>>>>>>>获取IP出错");
        }
        return null;
    }

    /**
     * 普通的get请求
     *
     * @return
     */
    protected byte[] simpleGet(HtmlInfo html) throws Exception {

        HttpClient httpClient = httpClient(html);


        HttpGet get = new HttpGet(EncoderUtil.encodeKeyWords(html.getOrignUrl(), "utf-8"));

        if (html.getCookie() != null) {
            get.setHeader("Cookie", html.getCookie());
        }
        if (html.getReferUrl() != null) {
            get.setHeader("Referer", html.getReferUrl());
        }
        if (html.getUa() != null) {
            get.setHeader("User-Agent", html.getUa());
        }
        if (html.getAcceptEncoding() != null) {
            get.setHeader("Accept-Encoding", html.getAcceptEncoding());
        }

        InputStream in = null;
        HttpEntity responseEntity = null;
        try {
            HttpResponse response = null;

            while (response == null) {
                if (html.getAgent()) {
                    if(html.getChangeProxy()||html.getProxy()==null) {
                        String ip_port = getRandomProxyFromProvider();
                        String proxyHost = ip_port.split(":")[0];
                        int proxyPort = Integer.parseInt(ip_port.split(":")[1]);
                        HttpHost proxy = new HttpHost(proxyHost, proxyPort);
                        RequestConfig conf = RequestConfig.custom()
                                .setProxy(proxy)
                                .setSocketTimeout(6000)
                                .setConnectTimeout(6000)
                                .setConnectionRequestTimeout(6000)
                                .build();


                        get.setConfig(conf);
                        Systemconfig.sysLog.log("proxy[" + ip_port + "] -> " + html.getOrignUrl());
                        html.setProxy(ip_port);
                    }

//                    Systemconfig.sysLog.log("Cookie: " + html.getCookie());
//                    Systemconfig.sysLog.log("User-Agent: " + html.getUa());
//                    Systemconfig.sysLog.log("Referer: " + html.getReferUrl());

                }
                try {
                    response = httpClient.execute(get); //执行get请求，并返回响应对象
                } catch (Exception e) {
                    Systemconfig.sysLog.log("请求出错，如果使用了代理，可能是代理失效，否则请确认url正确。["+html.getRetryTimes()+"]");
                    if (html.getRetryTimes() >= html.getMaxRetryTimes()) {
                        Systemconfig.sysLog.log("重试次数太多(当前最大重试次数：" + html.getMaxRetryTimes() + ")，跳过当前url，请检查url");
                        html.setRetryTimes(0);
                        break;
                    }
                    html.increaseRetryTimes();

//                throw new RuntimeException(e);
                }
            }
            // if(response.getStatusLine().getStatusCode() == 200) {
            responseEntity = response.getEntity();
            if (responseEntity != null) {
                in = response.getEntity().getContent();
                Header h = response.getFirstHeader("Content-Type");
                if (h != null && h.getValue().indexOf("charset") > -1) {
                    html.setEncode(h.getValue().substring(h.getValue().indexOf("charset=") + 8).replace(";", ""));
                    html.setFixEncode(true);
                }
                // 头的处理
                // ...
                html.setReferUrl(html.getOrignUrl());
                //
                return EntityUtils.toByteArray(responseEntity);
            }
            // }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                EntityUtils.consume(responseEntity);
            } catch (IOException e) {
                // e.printStackTrace();
            }
            IOUtils.closeQuietly(in);
            get.abort();
        }
        return null;
    }

    public void manuallySetCookie(String cookies) {

    }

    @Override
    public HttpClient httpClient(HtmlInfo html) {
        String key = html.getType();
        if (clientMap.containsKey(key)) {
            if (count++ > 100) {
                HttpClient client = HttpClients.createDefault();

                clientMap.putIfAbsent(key, client);
                count = 0;
            }

            return clientMap.get(key);
        } else {
            HttpClient client = HttpClients.createDefault();

            clientMap.putIfAbsent(key, client);
            return client;
        }
    }

    @Override
    public void getContent(HtmlInfo html, UserAttr userAttr) {
        byte[] fromURL = null;
        try {
            fromURL = userAttr == null ? simpleGet(html) : simpleGet(html, userAttr);
            if (fromURL == null) {
                Systemconfig.sysLog.log("没有抓取到内容，建议暂停采集！请检查网络链接或URL：" + html.getOrignUrl() + "是否正确。");
                html.setContent(null);
                return;
            }
            if (html.getFileType().equals(".htm")) {
                htm(html, fromURL);
            } else {
                pdf(html, fromURL);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            fromURL = null;
        }
    }

    private void htm(HtmlInfo html, byte[] fromURL) {
        String con = null;
        try {
            // con = new String(fromURL, "gb2312");//神奇的bug

            con = new String(fromURL, html.getEncode().replace(";", ""));// 神奇的bug
            // con = new String(fromURL, cs);
            if (!html.getFixEncode()) {// header中无charset参数，需要更换encode，检测页面内容的charset
                String head = StringUtil.regMatcher(con, "<head>", "</head>");

                int charsetSuc = 0;
                if (head != null) {
                    // 从网页源码获取charset声明
                    String charset = StringUtil.regMatcher(head, "charset=\"?", "[\"/]");
                    // charset = cs.toString();
                    if (charset != null && charset.length() < 11 && charset.length() > 2) {
                        charset = charset.trim().toLowerCase();
                        if (!html.getEncode().equals(charset)) {
                            for (String s : charsets) {
                                if (charset.contains(s)) {
                                    html.setEncode(s);
                                    break;
                                }
                            }
                            con = null;
                            con = new String(fromURL, html.getEncode());
                            charsetSuc = 1;
                        }
                    }
                }
                if (charsetSuc == 0) {
                    // 使用jchardet自动检测
                    CharsetDetector charDect = new CharsetDetector();
                    InputStream is = new ByteArrayInputStream(fromURL);
                    String[] probableSet = charDect.detectChineseCharset(is);
                    if (probableSet != null) {
                        if (probableSet.length > 0) {
                            String cs = probableSet[0];
                            con = null;
                            con = new String(fromURL, cs);
                        }
                    }
                }
            }
        } catch (UnsupportedEncodingException e) {
            Systemconfig.sysLog.log("请检查并输入正确字符集！", e);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        html.setContent(con);
        if (Systemconfig.createFile) {
            String tmp = Systemconfig.filePath + File.separator + html.getType() + File.separator
                    + MD5Util.MD5(html.getOrignUrl()) + html.getFileType();
            if (html.getAddHead()) {
                StringUtil.writeFile(tmp, html.getOrignUrl() + "\r\n" + html.getContent(), html.getEncode());
            } else {
                StringUtil.writeFile(tmp, html.getContent(), html.getEncode());
            }
        }
        con = null;
    }

    private void pdf(HtmlInfo html, byte[] fromURL) {
        FileOutputStream fos = null;
        try {
            // fos = new FileOutputStream(Systemconfig.filePath + File.separator
            // + html.getType() + File.separator
            // + MD5Util.MD5(html.getOrignUrl()) + html.getFileType());
            // fos.write(fromURL);
            String folderName = Systemconfig.filePath + File.separator + html.getType();
            if (!new File(folderName).exists())
                new File(folderName).mkdirs();
            String fileName = folderName + File.separator + MD5Util.MD5(html.getOrignUrl()) + html.getFileType();
            fos = new FileOutputStream(fileName);
            fos.write(fromURL);
            Systemconfig.sysLog.log("pdf保存至： " + fileName);
        } catch (IOException e) {
            Systemconfig.sysLog.log("文件无法下载。", e);
        } finally {
            if (fos != null)
                try {
                    fos.close();

                } catch (IOException e) {
                    fos = null;
                }
        }

    }

    protected byte[] simpleGet(HtmlInfo html, UserAttr user) throws Exception {
        return simpleGet(html);
    }

    @Override
    public void getContent(HtmlInfo html) {
        getContent(html, null);
    }


    protected HttpParams httpParams(boolean agent) {
        HttpParams params = new BasicHttpParams();
        params.setParameter(CoreProtocolPNames.USER_AGENT, userAgent);
        params.setParameter(ClientPNames.ALLOW_CIRCULAR_REDIRECTS, true);
        params.setParameter(ClientPNames.MAX_REDIRECTS, connectNum);
        params.setParameter(ClientPNames.COOKIE_POLICY, CookiePolicy.BROWSER_COMPATIBILITY);
        params.setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, requestTime);
        params.setParameter(CoreConnectionPNames.SO_TIMEOUT, readTime);
        if (agent) {
            HttpHost proxy = new HttpHost(Systemconfig.agentIp, Systemconfig.agentPort);
            params.setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
        }
        return params;
    }

    protected HttpParams httpParams(boolean agent, int siteId) {
        HttpParams params = new BasicHttpParams();
        params.setParameter(CoreProtocolPNames.USER_AGENT, userAgent);
        params.setParameter(ClientPNames.ALLOW_CIRCULAR_REDIRECTS, true);
        params.setParameter(ClientPNames.MAX_REDIRECTS, connectNum);
        params.setParameter(ClientPNames.COOKIE_POLICY, CookiePolicy.BROWSER_COMPATIBILITY);
        params.setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, requestTime);
        params.setParameter(CoreConnectionPNames.SO_TIMEOUT, readTime);

        return params;
    }

    /**
     * 创建安全连接协议httpclient
     *
     * @param client
     */
    public HttpClient tlsClient(HttpClient client) {
        try {
            // TLS单项认证，SSL双向认证
            SSLContext ctx = SSLContext.getInstance(SSLSocketFactory.TLS);
            X509TrustManager tm = new X509TrustManager() {
                public void checkClientTrusted(X509Certificate[] xcs, String string) throws CertificateException {
                }

                public void checkServerTrusted(X509Certificate[] xcs, String string) throws CertificateException {
                }

                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
            };
            X509HostnameVerifier verifier = new X509HostnameVerifier() {
                @Override
                public void verify(String string, SSLSocket ssls) throws IOException {
                }

                @Override
                public void verify(String string, X509Certificate xc) throws SSLException {
                }

                @Override
                public void verify(String string, String[] strings, String[] strings1) throws SSLException {
                }

                @Override
                public boolean verify(String string, SSLSession ssls) {
                    return true;
                }
            };
            ctx.init(null, new TrustManager[]{tm}, null);
            SSLSocketFactory ssf = new SSLSocketFactory(ctx, verifier);
            ClientConnectionManager ccm = client.getConnectionManager();
            ccm.getSchemeRegistry().register(new Scheme("https", 443, ssf));
            return new DefaultHttpClient(ccm, client.getParams());
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    protected byte[] data(HttpEntity entity) {
        byte[] buffer = new byte[2048];
        byte[] byteArray = null;
        java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
        int count = -1;
        InputStream responseBodyAsStream = null;
        try {
            responseBodyAsStream = entity.getContent();
            Header contentEncodingHeader = entity.getContentEncoding();
            if (contentEncodingHeader != null) {
                String contentEncoding = contentEncodingHeader.getValue();
                if (contentEncoding.toLowerCase(Locale.US).indexOf("gzip") != -1) {
                    responseBodyAsStream = new GZIPInputStream(responseBodyAsStream);
                }
            }
            while ((count = responseBodyAsStream.read(buffer, 0, buffer.length)) > -1) {
                baos.write(buffer, 0, count);
            }
            byteArray = baos.toByteArray();
            return byteArray;
        } catch (IOException e) {
        } finally {
            buffer = null;
            try {
                baos.close();
                responseBodyAsStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    public void monitorLogin(UserAttr user) {
    }

    public String getJsonContent(String url, String charSet) {
        String code = StringUtil.regMatcher(url, "/p-", "-s-0-t-3-p-0");
        String refer = "http://item.jd.com/" + code + ".html";
        String content1 = "";
        String content2 = "";
        try {
            content1 = getJsonContentGb2312(url, refer);
            content2 = getJsonContentUtf8(url, refer);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String content = "";
        if (charSet.equals("gbk") || charSet.equals("gb2312"))
            content = content1;
        else if (charSet.equals("utf-8"))
            content = content2;
        else {
            content = content2;
        }
        // if (StringUtil.regMatcher(content1, "\\(", "\\)") != null){
        // Systemconfig.sysLog.log("encoding: gb2312");
        // return content1;
        // }
        // if (StringUtil.regMatcher(content2, "\\(", "\\)") != null){
        // Systemconfig.sysLog.log("encoding: utf8");
        // return content2;
        // }
        if (content == null || !content.contains("{"))
            return null;
        content = content.startsWith("{") ? content
                : content.substring(content.indexOf("{"), content.lastIndexOf("}") + 1);
        return content;
        // return content1.length() > content2.length() ? content1 : content2;
    }

    public String getJsonContentUtf8(String url, String... args) {
        HtmlInfo html = new HtmlInfo();
        html.setType("DATA");
        html.setOrignUrl(url);
        html.setEncode("utf-8");
        html.setReferUrl(args[0]);
        getContent(html);

        return html.getContent();
    }

    public String getJsonContentGb2312(String url, String... args) {
        HtmlInfo html = new HtmlInfo();
        html.setType("DATA");
        html.setOrignUrl(url);
        html.setEncode("gb2312");
        html.setReferUrl(args[0]);
        getContent(html);

        return html.getContent();
    }

    @Override
    public String getJsonContent(String ownerInitUrl) {
        // TODO Auto-generated method stub
        return null;
    }

}
