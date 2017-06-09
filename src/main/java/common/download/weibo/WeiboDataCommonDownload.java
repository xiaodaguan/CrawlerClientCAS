package common.download.weibo;

import common.bean.HtmlInfo;
import common.bean.WeiboData;
import common.download.GenericDataCommonDownload;
import common.rmi.packet.SearchKey;
import common.bean.CollectDataType;
import common.siteinfo.Siteinfo;
import common.system.Systemconfig;
import common.system.UserAttribute;
import common.util.JsonUtil;
import common.util.StringUtil;
import common.util.TimeUtil;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 博主微博下载:列表页(采集全部属性)
 *
 * @author grs
 */
public class WeiboDataCommonDownload extends GenericDataCommonDownload<WeiboData> {

    private UserAttribute user;

    public WeiboDataCommonDownload(String siteFlag, WeiboData data, CountDownLatch count, UserAttribute user, SearchKey key) {
        super(siteFlag, data, count, key);
        this.user = user;
    }

    @Override
    public void process() {
        List<WeiboData> alllist = new ArrayList<WeiboData>();
        List<WeiboData> list = new ArrayList<WeiboData>();
        Siteinfo siteinfo = Systemconfig.allSiteinfos.get(siteFlag);
        String url = data.getUrl();
        String nexturl = url;

        LOGGER.info("downloading... " + nexturl + ". " + key.getKey());
        HtmlInfo html = htmlInfo(CollectDataType.DATA.name());
        int count = 1;
        try {
            int page = 1;
            while (nexturl != null && !nexturl.equals("")) {
                list.clear();

                html.setOrignUrl(nexturl);
                try {
                    URL url0 = new URL(nexturl);
                    URLConnection conn0 = url0.openConnection();
                    conn0.setRequestProperty("Cookie", user.getCookie());
                    conn0.connect();
                    
                    InputStream is = conn0.getInputStream();
                    html.setContent(StringUtil.readStream(is));
                    is.close();
                    // http.getContent(html, user);//下载获取用户的微博页面
                    String html1 = html.getContent();// 页面html
                    String userName = StringUtil.regMatcher(html1, "\\$CONFIG\\['onick'\\]\\s*=\\s*'", "'");
                    String uId = StringUtil.regMatcher(html1, "\\$CONFIG\\['uid'\\]\\s*=\\s*'", "'");

                    xpath.templateListPage(list, html, count, data.getId() + "", nexturl, data.getCategoryCode() + "", key.getKey(), userName);// 解析微博列表
                    for (WeiboData wd : list) {
                        wd.setUid(uId);
                    }

                    if (list.size() == 0) {
                        LOGGER.info(url + "数据页面解析为空！！");
                        break;
                    }

                    Systemconfig.dbService.getNorepeatData(list, "weibo_data");
                    if (list.size() == 0) {
                        LOGGER.info(url + "无新数据.");
                        break;
                    }
                    LOGGER.info(url + "数据页面解析完成。" + list.size());
                    alllist.addAll(list);

                    if (list.get(0).getMid() == null) {
                        LOGGER.info("err--->mid");
                    }
                    String pageId = StringUtil.regMatcher(html1, "\\$CONFIG\\['page_id'\\]\\s*=\\s*'", "'");
                    uId = StringUtil.regMatcher(html1, "\\$CONFIG\\['uid'\\]\\s*=\\s*'", "'");
                    if (uId == null) {
                        System.out.println("debug.");
                    }
                    String ajaxUrl1 = "http://weibo.com/p/aj/v6/mblog/mbloglist?ajwvr=6&" + "domain=100106&" + "is_search=0&" + "visible=0&" + "is_tag=0&" + "profile_ftype=1&" + "page=" + page + "&" + "pre_page=" + page + "&" + "max_id=&" + "end_id=" + list.get(0).getMid() + "&" + "pagebar=0&" + "filtered_min_id=&" + "pl_name=Pl_Official_MyProfileFeed__22&" + "id=" + pageId + "&" + "script_uri=/u/" + uId + "&" + "feed_type=0&" + "domain_op=100106";
                    // 下载第一次加载
                    URL url1 = new URL(ajaxUrl1);
                    URLConnection conn1 = url1.openConnection();
                    if (user != null) {
                        if (user.getCookie() != null) conn1.setRequestProperty("Cookie", user.getCookie());
                    }
                    conn1.connect();

                    String html2 = StringUtil.readStream(conn1.getInputStream());
                    // 下载第二次加载
                    String ajaxUrl2 = "http://weibo.com/p/aj/v6/mblog/mbloglist?ajwvr=6&" + "domain=100106&" + "is_search=0&" + "visible=0&" + "is_tag=0&" + "profile_ftype=1&" + "page=" + page + "&" + "pre_page=" + page + "&" + "max_id=&" + "end_id=" + list.get(0).getMid() + "&" + "pagebar=1&" + "filtered_min_id=&" + "pl_name=Pl_Official_MyProfileFeed__22&" + "id=" + pageId + "&" + "script_uri=/u/" + uId + "&" + "feed_type=0&" + "domain_op=100106";
                    URL url2 = new URL(ajaxUrl2);
                    URLConnection conn2 = url2.openConnection();
                    if (user != null) {
                        if (user.getCookie() != null) conn2.setRequestProperty("Cookie", user.getCookie());
                    }
                    conn2.connect();
                    String html3 = StringUtil.readStream(conn2.getInputStream());

                    // 拼接html
                    html2 = JsonUtil.getStringByKey(html2, "data");
                    html3 = JsonUtil.getStringByKey(html3, "data");

                    String ajaxHtml = html2 + "\n" + html3;
                    ajaxHtml = ajaxHtml.replace("\\/", "/").replace("\\\"", "\"");
                    html.setContent(StringUtil.decodeUnicode(ajaxHtml));
                    // 解析延时加载的内容
                    list.clear();
                    nexturl = xpath.templateListPage(list, html, count, data.getId() + "", nexturl, data.getCategoryCode() + "", key.getKey(), userName);// 解析微博列表
                    for (WeiboData wd : list) {
                        wd.setUid(uId);
                    }


                    if (list.size() == 0) {
                        LOGGER.info(url + "下拉加载解析为空！！");
                        break;
                    }
                    Systemconfig.dbService.getNorepeatData(list, "weibo_data");
                    if (list.size() == 0) {
                        LOGGER.info(url + "下拉加载解无新数据.");
                        break;
                    }
                    LOGGER.info(url + "下拉加载解析完成。" + list.size());

                    for (WeiboData wd : list) {
                        wd.setUid(uId);
                    }
                    alllist.addAll(list);

                    if (alllist.size() >= 500) {
                        Systemconfig.dbService.saveDatas(alllist);
                        for (int i = 0; i < alllist.size(); i++)
                            synchronized (key) {
                                key.savedCountIncrease();
                            }
                        alllist.clear();
                    }
                    url = nexturl;
                    page++;
                    if (url != null && !url.contains("&max_id=")) count++;
                    if (nexturl != null) TimeUtil.rest(siteinfo.getDownInterval());
                    if (page > siteinfo.getPage()) break;
                } catch (Exception e) {
                    e.printStackTrace();
                    break;
                }
            }

            /**
             *  根据需要采集转发评论
             */
            process(alllist);
//            Systemconfig.dbService.saveDatas(alllist);
            for (int i = 0; i < alllist.size(); i++) {
                synchronized (key) {
                    key.savedCountIncrease();
                }
            }
            LOGGER.info("saved." + alllist.size());
            alllist.clear();

//        } catch (IOException e) {
//            // 4605 6.20 pm 4:00 zhiqian
//            e.printStackTrace();
//            try {
//                Systemconfig.dbService.saveLog(siteFlag, key, 3, url + "\r\n" + e.getMessage());
//            } catch (IOException e1) {
//                e1.printStackTrace();
//            }
//        }
        } finally {
            alllist.clear();
            list.clear();
            TimeUtil.rest(siteinfo.getDownInterval());
        }
    }

    private ExecutorService comes = Executors.newFixedThreadPool(5);
    private ExecutorService rttes = Executors.newFixedThreadPool(5);

    private void process(List<WeiboData> list) {

        for (WeiboData wd : list) {
            /**
             * 采集评论
             */
            LOGGER.info("开始采集微博: " + wd.getUrl() + " 的评论...");
//            if (wd.getCommentNum() > 0) {
//                key.setKey(wd.getCommentUrl());
//                Future<?> com = comes.submit(new WeiboCommentDownload(key, wd.getId(), user));
//                try {
//                    com.get();
//                } catch (InterruptedException e) {
//                    com.cancel(true);
//                } catch (ExecutionException e) {
//                    com.cancel(true);
//                }
//            }
            if (wd.getCommentNum() > 0) {

                if (wd.getUid() == null) {
                    System.err.println("uid is missing.");
                    continue;
                }
                if (wd.getMid() == null) {
                    System.err.println("mid is missing.");
                    continue;
                }
                String commUrl = "http://m.weibo.cn/" + wd.getUid() + "/" + wd.getMid() + "/rcMod?format=cards&type=comment&page=1";
                key.setKey(commUrl);

                WeiboCommentDownload wcd = new WeiboCommentDownload(key, wd.getId(), user);
                wcd.process();
            }else
            {
                LOGGER.info("微博:"+wd.getUrl()+" 无评论");
            }
            try {
                Systemconfig.dbService.saveData(wd);
                LOGGER.info("微博: " + wd.getUrl() + " 保存完成.");
            } catch (IOException e) {
                e.printStackTrace();
            }


            /**
             * 采集转发
             */
//            if (wd.getRttNum() > 0) {
//                key.setKey(wd.getRttUrl());
//                Future<?> rtt = rttes.submit(new WeiboRttDownload(key, wd.getId(), user));
//                try {
//                    rtt.get();
//                } catch (InterruptedException e) {
//                    rtt.cancel(true);
//                } catch (ExecutionException e) {
//                    rtt.cancel(true);
//                }
//            }
        }

    }

}
