package common.download.weibo;

import common.pojos.HtmlInfo;
import common.pojos.UserData;
import common.pojos.WeiboData;
import common.download.GenericMetaCommonDownload;
import common.extractor.xpath.XpathExtractor;
import common.extractor.xpath.weibo.monitor.WeiboMonitorXpathExtractor;
import common.rmi.packet.SearchKey;
import common.rmi.packet.ViewInfo.InnerInfo;
import common.pojos.CollectDataType;
import common.system.UserAttribute;
import common.system.UserManager;
import common.utils.TimeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 微博账号监控列表页下载
 *
 * @author grs
 */
public class WeiboUserMonitorMetaCommonDownload extends GenericMetaCommonDownload<WeiboData> implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(WeiboUserMonitorMetaCommonDownload.class);

    private ExecutorService fansexec = Executors.newFixedThreadPool(1);
    private ExecutorService followexec = Executors.newFixedThreadPool(1);
    private ExecutorService weiboexec = Executors.newFixedThreadPool(1);

    public WeiboUserMonitorMetaCommonDownload(SearchKey key) {
        super(key);
    }

    private UserAttribute userAttribute;

    @Override
    public void prePorcess() {
        InnerInfo ii = null;
        if (!siteinfo.getLogin())
            return;

        // 每次保证只有有效用户个执行，某个任务完成后，等待的下一个任务开始执行
        UserAttribute ua = UserManager.getUser(siteFlag);
        while (ua == null) {
            LOGGER.info("暂时没有可用账号用于采集，等待账号中……");
            TimeUtil.rest(10);
            ua = UserManager.getUser(siteFlag);
        }
        userAttribute = ua;
        if (!userAttribute.getHadRun()) {
            http.monitorLogin(userAttribute);
            ua.setHadRun(true);
            LOGGER.info("监测用户！！！" + userAttribute.getName());
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
                    http.getContent(html, userAttribute);
                    // html.setContent(common.utils.StringUtil.getContent("filedown/USER/sina_weibo_monitor/51d4cea4821e13b750088647e44f2543.htm"));
                    ((WeiboMonitorXpathExtractor) ((XpathExtractor) xpath)).templateUser(userData, html, true
                            );
                    if (userData != null && userData.getInfoUrl() != null && !userData.getInfoUrl().equals("")) {
                        html.setOrignUrl(userData.getInfoUrl());
                        html.setType(CollectDataType.USERINFO.name() + File.separator + siteFlag);
                        http.getContent(html, userAttribute);
                        // html.setContent(common.utils.StringUtil.getContent("filedown/USERINFO/sina_weibo_monitor/5f5af5a1fe1cf1bc7ed8b0933fd814f0.htm"));
                        ((WeiboMonitorXpathExtractor) ((XpathExtractor) xpath)).templateUser(userData, html, false
                                );
//                        if (Systemconfig.dbService instanceof WeiboMysqlService)
//                            ((WeiboMysqlService) Systemconfig.dbService).saveUser(userData);
//                        else if (Systemconfig.dbService instanceof WeiboOracleService)
//                            ((WeiboOracleService) Systemconfig.dbService).saveUser(userData);
                        //todo
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            // SearchKey fansKey = new SearchKey();
            // fansKey.setSite(siteFlag);
            // fansKey.setKey(user.getFansUrl());
            // Future<?> fans = fansexec.submit(new FansCommonDownload(fansKey,
            // user.getId(), userAttribute));
            // SearchKey followKey = new SearchKey();
            // followKey.setSite(siteFlag);
            // followKey.setKey(user.getFansUrl());
            // Future<?> follow = followexec.submit(new
            // FollowCommonDownload(followKey, user.getId(), userAttribute));

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
//            Future<?> weibo = weiboexec.submit(new WeiboDataCommonDownload(siteFlag, data, null, userAttribute, key));

            WeiboDataCommonDownload wdcd=new WeiboDataCommonDownload(siteFlag,data,null, userAttribute,key);
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
            UserManager.releaseUser(siteFlag, userAttribute);
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
