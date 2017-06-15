package common.extractor.xpath.weixin.search;

import common.pojos.HtmlInfo;
import common.pojos.Proxy;
import common.pojos.WeixinData;
import common.pojos.WxpublicData;
import common.extractor.xpath.XpathExtractor;
import common.siteinfo.CommonComponent;
import common.siteinfo.Component;
import common.siteinfo.Siteinfo;
import common.system.AppContext;
import common.system.Systemconfig;
import common.util.*;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.xpath.XPathAPI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.transform.TransformerException;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 抽取实现类
 *
 * @author gxd
 */
public class WeixinSearchXpathExtractor extends XpathExtractor<WeixinData> implements WeixinSearchExtractorAttribute {
    private static final Logger LOGGER = LoggerFactory.getLogger(WeixinSearchXpathExtractor.class);


    public void parseUrl1(List<WeixinData> list, Node dom, Component component, String... args) {

        if (component == null) return;
        NodeList nl = head(component.getXpath(), dom, list.size(), component.getName());
        if (nl == null) return;
        for (int i = 0; i < nl.getLength(); i++) {
            String url = urlProcess(component, nl.item(i));
            list.get(i).setUrl(url);
        }


    }

    @Override
    public void parseUrl(List<WeixinData> list, Node dom, Component component, String... args) {
        /**
         * args: 0 cookie 1 referer 2 domain id
         */
        if (args[0] == null || args[0] == "") return;

        if (component == null) return;
        NodeList nl = head(component.getXpath(), dom, list.size(), component.getName());
        if (nl == null) return;

        String cookie = args[0];
        String referer = args[1];
        for (int i = 0; i < nl.getLength(); i++) {
            String tmpUrl = "http://weixin.sogou.com" + urlProcess(component, nl.item(i));

            Proxy p = Systemconfig.dbService.getProxy(Integer.parseInt(args[2]));

            java.net.Proxy proxy = null;
            if (p != null) proxy = new java.net.Proxy(java.net.Proxy.Type.HTTP, new InetSocketAddress(p.gethHost().getHostName(), p.gethHost().getPort()));

            String loc = null;
            try {
                LOGGER.info("url：" + tmpUrl);
                LOGGER.info("本次请求通过代理：" + proxy);
                HttpURLConnection conn = null;
                if (p != null) conn = (HttpURLConnection) new URL(tmpUrl).openConnection(proxy);
                else {
                    conn = (HttpURLConnection) new URL(tmpUrl).openConnection();
                }
                Systemconfig.dbService.updateProxyOrder(p.gethHost().getHostName() + ":" + p.gethHost().getPort() + ":" + args[2]);

                conn.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; rv:38.0) Gecko/20100101 Firefox/38.0");
                conn.setRequestProperty("Cookie", cookie);
                conn.setRequestProperty("Referer", referer);
                HttpURLConnection.setFollowRedirects(false);
                conn.setFollowRedirects(false);
                conn.connect();
                loc = conn.getHeaderField("Location");
                if (loc != null) {
                    LOGGER.info(conn.getResponseMessage());
                    if (loc.contains("antispider")) {
                        LOGGER.info("ip被屏蔽，请手动验证！@详情页");
                        System.in.read();
                        i--;
                        continue;
                    }
                }
                LOGGER.info("real url: " + loc);

                int sleepTime = 30 + (int) (Math.random() * 30);
                // int sleepTime = 2;
                LOGGER.info("sleep..." + sleepTime);
                TimeUtil.rest(sleepTime);

            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            list.get(i).setUrl(loc == null ? "err." : loc);
        }

    }

    @Override
    public void processList(List<WeixinData> list, Node domtree, Map<String, Component> comp, String... args) {
        /**
         * args: 0 html content 1 siteflag 2 cookie 3 ... 4 search url 5 search
         * keyword 6 domain id
         */


//        comp.get("title").setXpath("//DIV[@class='txt-box']/H3/A[contains(@id,'title_')]");
        this.parseTitle(list, domtree, comp.get("title"));
//        this.parseTitle(list, domtree, comp.get("title"));

        if (list.size() == 0) return;

//        this.parseUrl(list, domtree, comp.get("url"), args[2], args[4], args[6]);
//        comp.get("url").setXpath("//DIV[@class='txt-box']/H3/A[contains(@id,'title_')]/@href");
        this.parseUrl1(list, domtree, comp.get("url"), args[2], args[4], args[6]);
//        this.parseUrl1(list, domtree, comp.get("url"), args[2], args[4], args[6]);


//        comp.get("brief").setXpath("//DIV[@class='txt-box']/P[contains(@id,'summary_index')]");
        this.parseBrief(list, domtree, comp.get("brief"));

        //div[contains(@id,'sogou_vr_')]/div/div[@class='s-p']/span
//        this.parseBrief(list, domtree, comp.get("brief"));
//        comp.get("pubtime").setXpath("//LI[contains(@id,'box_')]/DIV[@class='txt-box']/DIV[@class='s-p']/SPAN[@class='s2']");
        this.parsePubtime(list, domtree, comp.get("pubtime"));
//        this.parsePubtime(list, domtree, comp.get("pubtime_l"));
//        this.parseImg_brief(list, domtree, comp.get("//*[contains(@id,'sogou_vr_')]/IMG"));
        this.parseImg_brief(list, domtree, comp.get("img_brief"));

    }

    @Override
    public String templateListPage(List<WeixinData> list, HtmlInfo html, int page, String... keyword) throws SAXException, IOException {
        list.clear();
        LOGGER.info("parsing list page: " + html.getOrignUrl());
        /**
         * keyword 0: search_keyword 1: search_url(list) 2: ... 3: cookies
         */
        Siteinfo siteinfo = Systemconfig.allSiteinfos.get(html.getSite());
        Node domtree = getRealDOM(html);
        if (domtree == null) {
            LOGGER.info("DOM解析为NULL！！");
            return null;
        }
        CommonComponent comp = getRealComp(siteinfo, html.getType().substring(0, html.getType().indexOf(File.separator)));// 得到元数据的配置组件

        processList(list, domtree, comp.getComponents(), html.getContent(), String.valueOf(siteinfo.getSiteFlag()), keyword[3], keyword[2], keyword[1], keyword[0], siteinfo.getDomainId() + "");
        if (list.size() == 0) return null;
        attrSet(list, siteinfo.getSiteFlag(), keyword[0], Integer.parseInt(keyword[2]), keyword[3]);
        return parseNext(domtree, comp.getComponents().get("next"), new String[]{keyword[1], page + ""});
    }

    public static void main(String args[]) throws SAXException {
        AppContext.initAppCtx("");
        WeixinSearchXpathExtractor extractor = new WeixinSearchXpathExtractor();
        WeixinData wd = new WeixinData();
        wd.setWeixinName("tianjinluntan");
        wd.setAuthor("天津论坛");
        wd.setSource("天津论坛");
        wd.setUrl("http://mp.weixin.qq.com/s?__biz=MzA3NjQwNDgwMQ==&mid=206615025&idx=3&sn=5d7138f96701fe22a7f3260ee94a68fb&3rd=MzA3MDU4NTYzMw==&scene=6#rd");

        HtmlInfo html = new HtmlInfo();
        html.setEncode("utf-8");
        html.setType("DATA");
        html.setCookie("ABTEST=0|1428050026|v1; IPLOC=CN1200; SUID=2BB1E29FE518920A00000000551E506A; SUID=2BB1E29F2524920A00000000551E506A; SUV=008864049FE2B12B551E506A6FCAF436; weixinIndexVisited=1; SUIR=1428052952; SNUID=FF65364BD3D6C11DAF2665E6D40EF01C; sct=2; wapsogou_qq_nickname=");
        extractor.parseGongzhong(wd, html, "");
    }

    @Override
    public void processPage(WeixinData data, Node domtree, Map<String, Component> comp, String... args) {
        LOGGER.info("parsing detail page: " + data.getTitle());
        this.parseSource(data, domtree, comp.get("source"));
        this.parsePubtime(data, domtree, comp.get("pubtime"), args[0]);
        this.parseAuthor(data, domtree, comp.get("author"));
        this.parseContent(data, domtree, comp.get("content"));
        this.parseImgUrl(data, domtree, comp.get("imgs_url"));

//        comp.get("read_num").setXpath("//*[@id='sg_readNum3']/text()");
        this.parseReadNumber(data, domtree, comp.get("read_num"));
//        comp.get("like_num").setXpath("//*[@id='sg_likeNum3']/text()");
        this.parseLikeNumber(data, domtree, comp.get("like_num"));

        this.parseWeixinName(data, domtree, comp.get("account"));
    }

    public void parseReadNumber(WeixinData data, Node domtree, Component component) {
        if (component == null) return;
        NodeList nl = commonList(component.getXpath(), domtree);
        if (nl == null) return;
        if (nl.item(0) != null) {
            String num = nl.item(0).getTextContent().replace("+","").trim();
            if(num!=null&&!num.equals(""))
            data.setReadNum(Integer.parseInt(num));
        }
    }

    public void parseLikeNumber(WeixinData data, Node domtree, Component component) {
        if (component == null) return;
        NodeList nl = commonList(component.getXpath(), domtree);
        if (nl == null) return;
        if (nl.item(0) != null) {
            String num = nl.item(0).getTextContent().replace("+","").trim();
            if(num!=null&&!num.equals(""))
            data.setPraiseNum(Integer.parseInt(num));
        }
    }

    public void parseGongzhong(WeixinData data, HtmlInfo html, String content) throws SAXException {
        LOGGER.info("正在解析文章：" + data.getTitle() + "的公众号信息.");
        String url = "http://weixin.sogou.com/weixin?type=1&query=" + data.getAuthor() + "&page=1";
        int page = 1;
        if (url != null)// 只在第一页搜索
        {
            LOGGER.info(page++ + "\t" + url);
            HttpClient client = new HttpClient();
            url = EncoderUtil.encodeKeyWords(url, "utf-8");

            GetMethod get = new GetMethod(url);
            if (html.getCookie() != null) {
                get.addRequestHeader("cookie", data.getCrawler_cookie());
            }

            try {
                client.executeMethod(get);
                get.getResponseBodyAsStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(get.getResponseBodyAsStream()));
                String line = null;
                StringBuilder sb = new StringBuilder();
                while ((line = reader.readLine()) != null) sb.append(line).append("\r\n");
                String text = sb.toString();
                // StringUtil.writeFile("tmp.htm", text.getBytes());
                DOMUtil dom = new DOMUtil();
                Node root = dom.ini(text, "utf-8");
                Node n = XPathAPI.selectSingleNode(root, "//DIV[@href and contains(.,'" + data.getWeixinName() + "')]/@href");
                if (n != null) {
                    String xBrief = "(//DIV[@href and contains(.,'" + data.getWeixinName() + "')]//SPAN[@class='sp-txt'])[1]";
                    String xUserIcon = "//DIV[@href and contains(.,'" + data.getWeixinName() + "')]//DIV[@class='img-box']/IMG/@src";
                    String xPosIcon = "//DIV[@href and contains(.,'" + data.getWeixinName() + "')]//DIV[@class='pos-ico']/DIV/IMG/@src";
                    String xVerify = "(//DIV[@href and contains(.,'" + data.getWeixinName() + "')]//SPAN[@class='sp-txt'])[2]";
                    // 找到匹配的公众号
                    String gongzhongUrl = n.getTextContent();
                    String brief = XPathAPI.selectSingleNode(root, xBrief).getTextContent();
                    String userIcon = XPathAPI.selectSingleNode(root, xUserIcon).getTextContent();
                    String posIcon = XPathAPI.selectSingleNode(root, xPosIcon).getTextContent();
                    String verify = XPathAPI.selectSingleNode(root, xVerify).getTextContent();

                    String openid = gongzhongUrl.substring(gongzhongUrl.indexOf("openid=") + 7);

                    String md5 = MD5Util.MD5(gongzhongUrl);

                    WxpublicData wpd = new WxpublicData();
                    wpd.setName(data.getAuthor());
                    wpd.setWeixinName(data.getWeixinName());
                    wpd.setUrl(gongzhongUrl);
                    wpd.setBrief(brief);
                    wpd.setUserIcon(userIcon);
                    wpd.setPosIcon(posIcon);
                    wpd.setVerify(verify);
                    wpd.setOpenId(openid);
                    wpd.setMd5(md5);
                    wpd.setFromAriticle(1);
                    wpd.setCustomizeId(data.getCustomizeId());

                    List<WxpublicData> list = new ArrayList<WxpublicData>();
                    list.add(wpd);
                    // Systemconfig.dbService.filterDuplication(list,
                    // "vip_temp_gongzhong");
                    if (list.size() > 0) {
                        int gongzhongid = Systemconfig.dbService.saveGongzhongData(wpd);
                        data.setGongzhongId(gongzhongid);
                        LOGGER.info("文章：" + data.getTitle() + "的公众号：" + wpd.getName() + "保存完成.");
                        return;
                    } else {
                        LOGGER.info("无新公众号。");
                    }

                } // end if 解析到dom节点

            } catch (HttpException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (TransformerException e) {
                e.printStackTrace();
            }
        } // end if
        LOGGER.info("没有找到匹配公众号。");
    }

    /**
     * 微信账号
     *
     * @param data
     * @param dom
     * @param component
     * @param args
     */
    public void parseWeixinName(WeixinData data, Node dom, Component component, String... args) {
        // // var user_name = "gh_558fe111f4e3";
        // String weixinName = StringUtil.regMatcher(content,
        // "var user_name = \"", "\";").trim();
        // if (weixinName != null)
        // data.setWeixinName(weixinName);

        if (component == null) return;
        NodeList nl = commonList(component.getXpath(), dom);
        if (nl == null) return;
        if (nl.item(0) != null) data.setWeixinName(nl.item(0).getTextContent().trim());

    }

    @Override
    public void parseImgUrl(WeixinData data, Node dom, Component component, String... args) {
        if (component == null) return;
        NodeList nl = commonList(component.getXpath(), dom);
        if (nl == null) return;
        String imgs = "";
        for (int i = 0; i < nl.getLength(); i++) {
            imgs += StringUtil.format(nl.item(i).getTextContent()) + ";";
        }
        data.setImgUrl(imgs);
    }

    @Override
    public void parseAuthor(WeixinData data, Node dom, Component component, String... args) {
        if (component == null) return;
        NodeList nl = commonList(component.getXpath(), dom);
        if (nl == null) return;
        if (nl.item(0) != null) data.setAuthor(StringUtil.format(nl.item(0).getTextContent()));
    }

    /**
     * 简介图片
     *
     * @param list
     * @param dom
     * @param component
     */
    public void parseImg_brief(List<WeixinData> list, Node dom, Component component, String... args) {
        if (component == null) return;
        NodeList nl = head(component.getXpath(), dom, list.size(), component.getName());
        if (nl == null) return;
        for (int i = 0; i < nl.getLength(); i++) {
            list.get(i).setBrief(nl.item(i).getTextContent());
        }
    }

    @Override
    public void parseTitle(List<WeixinData> list, Node dom, Component component, String... args) {
        if (component == null) return;
        NodeList nl = head(component.getXpath(), dom);
        for (int i = 0; i < nl.getLength(); i++) {
            WeixinData vd = new WeixinData();
            vd.setTitle(StringUtil.format(nl.item(i).getTextContent()));
            list.add(vd);
        }
    }

    @Override
    public String parseNext(Node dom, Component component, String... args) {
        if (component == null) return null;
        NodeList nl = commonList(component.getXpath(), dom);
        if (nl == null) return null;
        if (nl.item(0) != null) {
            return urlProcess(component, nl.item(0));
        }
        return null;
    }

    /**
     * 摘要
     *
     * @param list
     * @param dom
     * @param component
     */
    @Override
    public void parseBrief(List<WeixinData> list, Node dom, Component component, String... args) {
        if (component == null) return;

        for (int i = 0; i < list.size(); i++) {
            NodeList nl = head(component.getXpath().replace("index",i+""), dom, list.size(), component.getName());
            if (nl == null) return;
            if(nl.getLength()!=0)
                list.get(i).setBrief(nl.item(0).getTextContent());
        }
    }

    public void parsePubtime(List<WeixinData> list, Node dom, Component component, String... args) {
        if (component == null) return;
        NodeList nl = head(component.getXpath(), dom, list.size(), component.getName());
        if (nl == null) return;
        for (int i = 0; i < nl.getLength(); i++) {
            String time = nl.item(i).getTextContent();

            String timeStamp = StringUtil.extractOne(time,"\\'\\d+\\'");
            if(timeStamp.length()>0) {
                list.get(i).setPubtime(time);
                list.get(i).setPubdate(new Date(Long.parseLong(timeStamp.replace("'","")+"000")));
            }

//            list.get(i).setBrief(nl.item(i).getTextContent());
        }
    }


    /**
     * 来源
     *
     * @param list
     * @param dom
     * @param component
     * @param strings
     */
    @Override
    public void parseSource(List<WeixinData> list, Node dom, Component component, String... strings) {
        if (component == null) return;
        NodeList nl = head(component.getXpath(), dom, list.size(), component.getName());
        if (nl == null) return;
        for (int i = 0; i < nl.getLength(); i++) {
            list.get(i).setSource(StringUtil.format(nl.item(i).getTextContent()));
        }
    }

    @Override
    public void parsePubtime(WeixinData data, Node dom, Component component, String... args) {
        // if (component == null)
        // return;
        // NodeList nl = commonList(component.getXpath(), dom);
        // if (nl == null)
        // return;
        // if (nl.item(0) != null) {
        // data.setPubtime(nl.item(0).getTextContent());
        // data.setPubdate(timeProcess(data.getPubtime().trim()));
        // }
        String content = args[0];
        String pubtime = StringUtil.regMatcher(content, "var ct = \"", "\";");
        data.setPubtime(pubtime);
    }

    @Override
    public void parseSource(WeixinData data, Node dom, Component component, String... strings) {
        if (component == null) return;
        NodeList nl = commonList(component.getXpath(), dom);
        if (nl == null) return;
        if (nl.item(0) != null) data.setSource(StringUtil.format(nl.item(0).getTextContent()));
    }

    @Override
    public void parseContent(WeixinData data, Node dom, Component component, String... strings) {
        if (component == null) return;
        NodeList nl = commonList(component.getXpath(), dom);
        if (nl == null) return;
        String str = "";// 正文文本
        String html = "";// 正文html
        for (int i = 0; i < nl.getLength(); i++) {
            // html
            html += DOMUtil.dom2Html(nl.item(i)) + "\r\n";
            // 文本
            if(!str.contains(nl.item(i).getTextContent()))
                str += nl.item(i).getTextContent() + "\r\n";
        }
        data.setContent(str);

        // 去掉html代码的根节点
        String tmp = html.replace("<div class=\"rich_media_content\" id=\"js_content\">", "");
        if (tmp.length() < html.length()) html = tmp.substring(0, html.lastIndexOf("</div>"));
        data.setContentHtml(html);

    }

    /**
     * 共有属性设置
     *
     * @param list
     * @param siteflag
     * @param key
     * @param code
     */
    protected void attrSet(List<WeixinData> list, int siteflag, String key, int code, String cookie) {
        for (WeixinData wd : list) {
            wd.setSearchKey(key);
            wd.setCategoryCode(code);
            wd.setCustomizeId(code);
//            String urlWithoutTimestamp = wd.getUrl().replace(StringUtil.extractOne(wd.getUrl(), "timestamp=\\d+"), "");
            wd.setMd5(MD5Util.MD5(wd.getTitle()+wd.getAuthor()));
            wd.setSiteId(siteflag);
            wd.setCrawler_cookie(cookie);
        }
    }

}