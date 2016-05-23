package common.download.weibo;

import common.bean.HtmlInfo;
import common.bean.UserData;
import common.bean.WeiboData;
import common.download.GenericMetaCommonDownload;
import common.extractor.xpath.XpathExtractor;
import common.extractor.xpath.weibo.monitor.WeiboMonitorXpathExtractor;
import common.rmi.packet.SearchKey;
import common.rmi.packet.ViewInfo;
import common.rmi.packet.ViewInfo.InnerInfo;
import common.service.mysql.WeiboMysqlService;
import common.service.oracle.WeiboOracleService;
import common.siteinfo.CollectDataType;
import common.system.Systemconfig;
import common.system.UserAttr;
import common.system.UserManager;
import common.util.TimeUtil;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 微博账号监控列表页下载
 *
 * @author grs
 */
public class WeiboUserMonitorMetaCommonDownload extends GenericMetaCommonDownload<WeiboData> implements Runnable {
    private ExecutorService fansexec = Executors.newFixedThreadPool(1);
    private ExecutorService followexec = Executors.newFixedThreadPool(1);
    private ExecutorService weiboexec = Executors.newFixedThreadPool(1);

    public WeiboUserMonitorMetaCommonDownload(SearchKey key) {
        super(key);
    }

    private UserAttr userAttr;

    @Override
    public void prePorcess() {
        InnerInfo ii = null;
        if (Systemconfig.clientinfo != null) {
            ViewInfo vi = Systemconfig.clientinfo.getViewinfos().get(Systemconfig.localAddress + "_" + siteFlag);
            ii = vi.getCrawlers().get(key.getKey());
            ii.setAlive(1);
        }
        if (!siteinfo.getLogin())
            return;

        // 每次保证只有有效用户个执行，某个任务完成后，等待的下一个任务开始执行
        UserAttr ua = UserManager.getUser(siteFlag);
        while (ua == null) {
            Systemconfig.sysLog.log("暂时没有可用账号用于采集，等待账号中……");
            TimeUtil.rest(10);
            ua = UserManager.getUser(siteFlag);
        }
        userAttr = ua;
        if (!userAttr.getHadRun()) {
            http.monitorLogin(userAttr);
            ua.setHadRun(true);
            System.out.println("监测用户！！！" + userAttr.getName());
        }
        if (ii != null) {
            ii.setAccountId(ua.getId());
            ii.setAccount(ua.getName());
            ii.setAccountTip("账号使用中！");
        }
    }

    @Override
    public void process() {
        String url = getRealUrl(siteinfo, gloaburl);
        String nexturl = url;

        HtmlInfo html = htmlInfo(CollectDataType.USER.name());
        UserData userData = new UserData();
        try {
            if (nexturl != null && !nexturl.equals("")) {
                html.setOrignUrl(nexturl);
                /**
                 * 先解析博主
                 */
                try {
                    http.getContent(html, userAttr);
                    // html.setContent(common.util.StringUtil.getContent("filedown/USER/sina_weibo_monitor/51d4cea4821e13b750088647e44f2543.htm"));
                    ((WeiboMonitorXpathExtractor) ((XpathExtractor) xpath)).templateUser(userData, html, true,
                            key.getRole() + "");
                    if (userData != null && userData.getInfoUrl() != null && !userData.getInfoUrl().equals("")) {
                        html.setOrignUrl(userData.getInfoUrl());
                        html.setType(CollectDataType.USERINFO.name() + File.separator + siteFlag);
                        http.getContent(html, userAttr);
                        // html.setContent(common.util.StringUtil.getContent("filedown/USERINFO/sina_weibo_monitor/5f5af5a1fe1cf1bc7ed8b0933fd814f0.htm"));
                        ((WeiboMonitorXpathExtractor) ((XpathExtractor) xpath)).templateUser(userData, html, false,
                                key.getRole() + "");
                        if (Systemconfig.dbService instanceof WeiboMysqlService)
                            ((WeiboMysqlService) Systemconfig.dbService).saveUser(userData);
                        else if (Systemconfig.dbService instanceof WeiboOracleService)
                            ((WeiboOracleService) Systemconfig.dbService).saveUser(userData);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            // SearchKey fansKey = new SearchKey();
            // fansKey.setSite(siteFlag);
            // fansKey.setKey(user.getFansUrl());
            // Future<?> fans = fansexec.submit(new FansCommonDownload(fansKey,
            // user.getId(), userAttr));
            // SearchKey followKey = new SearchKey();
            // followKey.setSite(siteFlag);
            // followKey.setKey(user.getFansUrl());
            // Future<?> follow = followexec.submit(new
            // FollowCommonDownload(followKey, user.getId(), userAttr));

            /**
             * 再采集博主的微博
             */
            WeiboData data = new WeiboData();
            if (userData.getWeiboUrl() == null) {
                data.setUrl(nexturl);
            } else
                data.setUrl(userData.getWeiboUrl());
            data.setId(userData.getId());
            data.setCategoryCode(userData.getCategoryCode());
//            Future<?> weibo = weiboexec.submit(new WeiboDataCommonDownload(siteFlag, data, null, userAttr, key));

            WeiboDataCommonDownload wdcd=new WeiboDataCommonDownload(siteFlag,data,null,userAttr,key);
            wdcd.process();

            // try {
            // fans.get();
            // } catch (Exception e) {
            // e.printStackTrace();
            // try {            // }

            // follow.get();
            // } catch (Exception e) {
            // e.printStackTrace();
            // }
//            try {
//                weibo.get();
//            } catch (Exception e) {
//                e.printStackTrace();
//
//            }
        } finally {
            UserManager.releaseUser(siteFlag, userAttr);
        }

    }


    @Override
    protected void specialHtmlInfo(HtmlInfo html) {

        html.setUa("Mozilla/5.0 (Linux; U; Android 4.4.4; Nexus 5 Build/KTU84P) AppleWebkit/534.30 (KHTML, like Gecko) Version/4.0 Mobile Safari/534.30");

    }

    ;

    @Override
    public void postProcess() {
        // HtmlInfo html = new HtmlInfo();
        // html.setSite(siteFlag);
        // html.setEncode("utf-8");
        // html.setType("USER"+File.separator+siteFlag);
        // HashMap<String, String> urls = new HashMap<String, String>();
        // for(WeiboData wd : list) {
        // if(wd.getUid()==null) continue;
        // html.setOrignUrl("http://weibo.com/aj/user/newcard?usercardkey=weibo_mp&type=1&id="+wd.getUid());
        // if(urls.containsKey(html.getOrignUrl())) {
        // wd.setAddress(urls.get(html.getOrignUrl()));
        // } else {
        // try {
        // http.getContent(html);
        // xpath.templateUser(html, wd);
        // urls.put(html.getOrignUrl(), wd.getAddress());
        // } catch (Exception e) {
        // e.printStackTrace();
        // }
        // TimeUtil.rest(5);
        // }
        // }
    }

}
