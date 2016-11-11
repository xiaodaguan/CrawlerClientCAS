package common.system;

import common.download.DownFactory;
import common.rmi.packet.CrawlerType;
import common.rmi.packet.SearchKey;
import common.rmi.packet.ViewInfo;
import common.rmi.packet.ViewInfo.InnerInfo;
import common.siteinfo.Siteinfo;
import common.util.StringUtil;
import common.util.TimeUtil;
import crawlerlog.log.CLog;
import crawlerlog.log.CLogFactory;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Job {


    /**
     * crawler log config
     **/
    private static String host = "http://www.sklmccs.ia.ac.cn:8080/crawlerlogserver";
    private static String cid = "";// from crawler start
    private static String ip = "";

    private static String cType = "";// from crawler start
    private static String project = "";// from crawler start

    private static String crawlerNameOrCMD = "";// from crawler start
    private static String note = "";
    private static CLog cLogger = null;
    /**
     * 线程池管理
     */
    private final static Map<String, ExecutorService> EXECUTOR_SERVICE_MAP = new HashMap<String, ExecutorService>();

    private static Job job = new Job();
    static Map<String, ViewInfo> first = new HashMap<String, ViewInfo>();
    public static List<SearchKey> keys = null;


    public static void simpleRun() throws UnknownHostException, InterruptedException {

        ip = String.valueOf(InetAddress.getLocalHost());
        cLogger = CLogFactory.getLogger(host, cid, ip, cType, project);

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {

                    cLogger.beat();
                    Systemconfig.sysLog.log("beat...");
                    try {
                        Thread.sleep(30 * 1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
        if (Systemconfig.crawlerType % 2 == 1) runSearch();
        else runMonitor();
    }

    /**
     * 普通搜索采集
     *
     * @throws Exception
     */   //  
    @SuppressWarnings("unchecked")
    private static void runSearch() throws UnknownHostException, InterruptedException {

        while (true) {
            cLogger.start(crawlerNameOrCMD, crawlerNameOrCMD);//name, note
            Systemconfig.sysLog.log("loop start...");

            keys = Systemconfig.dbService.searchKeys();
            Systemconfig.sysLog.log(keys.size() + "个关键词将采集:");
            out:
            for (SearchKey sk : keys) {
            	
                for (String site : Systemconfig.allSiteinfos.keySet()) {

                    Siteinfo siteinfo = Systemconfig.allSiteinfos.get(site);
                    sk.setSite(site);
                    
                    createThreadPool(site, siteinfo);
                    
                    String taskName = sk.getSite() + sk.getKey();
                    if (Systemconfig.finish.get(taskName) == null 
                    		|| Systemconfig.finish.get(taskName)) {

                        job.submitSearchKey(sk);
                        
                        Systemconfig.finish.put(taskName, false);
                    }
                }
            }


            Long start = System.currentTimeMillis();
            while (!ifAllFinished()) {
                Thread.currentThread().sleep(10 * 1000);
                if(start+1000*3600*10 < System.currentTimeMillis()){
                    //单循环最大5h
                        Systemconfig.sysLog.log("single loop time out, stop...");
                        Set<String> taskNames = Systemconfig.tasks.keySet();

                        for (String taskName : taskNames) {
                            if(Systemconfig.tasks.containsKey(taskName)){
                                Systemconfig.tasks.get(taskName).cancel(true);
                            }
                    }
                    Systemconfig.sysLog.log("all tasks stopped.");
                    break;
                }
            }

            cLogger.stop();
            Systemconfig.sysLog.log("loop stop");

            TimeUtil.rest(calCycleWaitTime());

            AppContext.readConfig();
        }
    }

    /**
     * 普通垂直采集
     */
    @SuppressWarnings("unchecked")
    private static void runMonitor() throws UnknownHostException, InterruptedException {

        while (true) {

            cLogger.start(crawlerNameOrCMD, crawlerNameOrCMD);//name, note
            Systemconfig.sysLog.log("loop start...");

            keys = Systemconfig.dbService.searchKeys();
            Systemconfig.sysLog.log(keys.size() + "个关键词将采集:");
            out:
            for (SearchKey sk : keys) {


                String site = sk.getSite() + "_" + CrawlerType.getCrawlerTypeMap().get(Systemconfig.crawlerType).name().toLowerCase();

                Siteinfo siteinfo = Systemconfig.allSiteinfos.get(site);

                if (siteinfo == null) continue;

                sk.setSite(site);
                createThreadPool(site, siteinfo);
                String taskName = sk.getSite() + sk.getKey();
                if (Systemconfig.finish.get(taskName) == null || Systemconfig.finish.get(taskName)) {
                    job.submitSearchKey(sk);

                    Systemconfig.finish.put(taskName, false);
                }
            }

            Long start = System.currentTimeMillis();
            while (!ifAllFinished()) {
                Thread.currentThread().sleep(10 * 1000);
                if(start+1000*3600*10 < System.currentTimeMillis()){
                    //单循环最大5h
                    Systemconfig.sysLog.log("single loop time out, stop...");
                    Set<String> taskNames = Systemconfig.tasks.keySet();

                    for (String taskName : taskNames) {
                        if(Systemconfig.tasks.containsKey(taskName)){
                            Systemconfig.tasks.get(taskName).cancel(true);
                        }
                    }
                    Systemconfig.sysLog.log("all tasks stopped.");
                    break;
                }
            }

            cLogger.stop();
            Systemconfig.sysLog.log("loop stop");


            TimeUtil.rest(calCycleWaitTime());


            AppContext.readConfig();
        }

    }

    /**
     * 判断systemconfi.tasks中的future是否全部完成
     *
     * @return
     */
    private static boolean ifAllFinished() {
        boolean allFinished = true;
        int runningTaskCount = 0;
        Set<String> taskNames = Systemconfig.tasks.keySet();

        for (String taskName : taskNames) {
            if (Systemconfig.tasks.containsKey(taskName)) {
                if (Systemconfig.tasks.get(taskName).isDone()) {
                } else {
                    runningTaskCount++;
                }
            }
        }
        if (runningTaskCount > 0) allFinished = false;
        Systemconfig.sysLog.log("remaining task count: " + runningTaskCount + " / total task count: " + Systemconfig.tasks.size());

        return allFinished;
    }


    /**
     * 运行某个站点的所有检索词或所属的垂直网址
     *
     * @param si
     */
    public static void runSite(Siteinfo si) {
        runSite(si, job);
    }

    /**
     * 指定job运行站点所有内容
     *
     * @param si
     * @param job
     */
    public static void runSite(Siteinfo si, Job job) {
        String key = Systemconfig.localAddress + "_" + si.getSiteName();
        ViewInfo vi = first.get(key);
        if (vi == null) {
            vi = new ViewInfo();
            // 初始化
            runInit(si, vi);
            first.put(key, vi);
        }
        // 对每个关键词处理，搜索采集searckey不设置site属性
        Iterator<SearchKey> iter = keys.iterator();
        while (iter.hasNext()) {
            SearchKey sk = iter.next();
            if (sk.getIp() == null) sk.setIp(Systemconfig.localAddress);
            // 只执行指定为当前IP的数据
            if (!Systemconfig.localAddress.equals(sk.getIp())) {
                synchronized (keys) {
                    iter.remove();
                }
                continue;
            }
            runSearchKey(si, sk, vi);
            TimeUtil.rest(1);
        }
    }

    /**
     * 爬虫线程运行初始化
     *
     * @param si
     * @param vi
     */
    public static void runInit(Siteinfo si, ViewInfo vi) {
        String site = si.getSiteName();

        String key = Systemconfig.localAddress + "_" + site;
        int n = Systemconfig.crawlerType / 2;
        int le = 1;
        if (Systemconfig.crawlerType % 2 != 0) {
            n++;
            le--;
        }
        vi.setBuildType(n);// 类型：
        vi.setStyle(le);// 方式：搜索
        vi.setIp(Systemconfig.localAddress);
        HashMap<String, InnerInfo> crawlers = new HashMap<String, InnerInfo>();// 每个关键词或网址作为一个子爬虫
        vi.setCrawlers(crawlers);

        if (Systemconfig.clientinfo.getViewinfos().get(key) == null) {
            Systemconfig.clientinfo.getViewinfos().put(key, vi);
        }

        if (si.getLogin()) {
            if (Systemconfig.users == null) Systemconfig.users = new HashMap<String, List<UserAttr>>();
            if (Systemconfig.users.get(site) == null) {
                List<UserAttr> list = Systemconfig.dbService.getLoginUsers(site);
                Systemconfig.users.put(site, list);
                if (list.size() == 0) {
                    Systemconfig.sysLog.log("没有可用账号！本轮采集退出");
                    return;
                }
                if (Job.getExecutorServiceMap().get(site) == null) Job.getExecutorServiceMap().put(site, Executors.newFixedThreadPool(list.size()));
            }
        } else {
            if (Job.getExecutorServiceMap().get(site) == null) Job.getExecutorServiceMap().put(site, Executors.newFixedThreadPool(si.getThreadNum()));
        }
    }


    /**
     * 运行站点的某个搜索词或垂直网址
     *
     * @param si
     * @param sk
     * @param vi
     */
    public static void runSearchKey(Siteinfo si, SearchKey sk, ViewInfo vi) {
        /* 状态 */
        String taskId = sk.getKey() + " " + sk.getSite() + " " + new Date().toString();
        sk.setCrawlerStatusId(taskId);

        String site = si.getSiteName();
        sk.setSite(site);
        sk.setType(Systemconfig.crawlerType);
        // 每个站点属性值设置一次
        if (vi != null) {
            vi.setName(site);
            String type = site.substring(site.indexOf("_") + 1);// 采集类型
            String name = site.substring(0, site.indexOf("_"));// 站点名
            File f = new File("site" + File.separator + type + "_" + name + ".xml");
            if (f.exists()) vi.setFile(StringUtil.getContent(f));
            vi.setThreadNum(si.getThreadNum());
            vi.setInterval(si.getDownInterval());
            vi.setCrawlerCycle(si.getCycleTime());
        }

        String searchKey = sk.getSite() + sk.getKey();
        // 该爬虫是初次运行和完成后才会再次执行
        if (Systemconfig.finish.get(searchKey) == null || Systemconfig.finish.get(searchKey)) {
            // 爬虫名和爬虫地址
            InnerInfo ii = new ViewInfo().new InnerInfo();
            ii.setSearchKey(sk);
            ii.setAlive(0);
            vi.getCrawlers().put(sk.getKey(), ii);

            job.submitSearchKey(sk);

            Systemconfig.finish.put(searchKey, false);
        }
    }

    /**
     * 获取 每一轮(爬虫启动)之间等待时间
     *
     * @return
     */
    private static int calCycleWaitTime() {
        int waitTime = 60*10;
//        if (Systemconfig.crawlerType == CrawlerType.EBUSINESS_SEARCH.ordinal() || Systemconfig.crawlerType == CrawlerType.EBUSINESS_MONITOR.ordinal())
//            waitTime = 30 * 24 * 60 * 60;
//        else if (Systemconfig.crawlerType == CrawlerType.NEWS_SEARCH.ordinal() || Systemconfig.crawlerType == CrawlerType.NEWS_MONITOR.ordinal())
//            waitTime = 30 * 60;
//        else if (Systemconfig.crawlerType == CrawlerType.BBS_SEARCH.ordinal() || Systemconfig.crawlerType == CrawlerType.BBS_MONITOR.ordinal())
//            waitTime = 30 * 60;
//        else if (Systemconfig.crawlerType == CrawlerType.WEIBO_SEARCH.ordinal() || Systemconfig.crawlerType == CrawlerType.WEIBO_MONITOR.ordinal())
//            waitTime = 4 * 60 * 60;
//        else waitTime = 3 * 60 * 60;

        return waitTime;
    }


    /**
     * 如果需要登录,则根据可用的账号数创建线程池,否则按照siteinfo.getThreadNum创建线程池
     *
     * @param site
     * @param siteinfo
     * @return
     */
    private static boolean createThreadPool(String site, Siteinfo siteinfo) {
        if (siteinfo.getLogin()) {

            if (Systemconfig.users == null) Systemconfig.users = new HashMap<String, List<UserAttr>>();
            if (Systemconfig.users.get(site) == null) {
                List<UserAttr> list = Systemconfig.dbService.getLoginUsers(site);
                Systemconfig.users.put(site, list);
                if (list.size() == 0) {
                    Systemconfig.sysLog.log("没有可用账号！本轮采集退出");
                    return true;
                }
//                if (Job.getExecutorServiceMap().get(site) == null) Job.getExecutorServiceMap().put(site, Executors.newFixedThreadPool(list.size()));
            }
        }
        if (Job.getExecutorServiceMap().get(site) == null) Job.getExecutorServiceMap().put(site, Executors.newFixedThreadPool(siteinfo.getThreadNum()));
        Systemconfig.sysLog.log("thread pool created, fixed size: "+siteinfo.getThreadNum());
        return false;

        /**
         *2016/11/08 16:14:00
         */
// if (siteinfo.getLogin()) {
//
//            if (Systemconfig.users == null) Systemconfig.users = new HashMap<String, List<UserAttr>>();
//            if (Systemconfig.users.get(site) == null) {
//                List<UserAttr> list = Systemconfig.dbService.getLoginUsers(site);
//                Systemconfig.users.put(site, list);
//                if (list.size() == 0) {
//                    Systemconfig.sysLog.log("没有可用账号！本轮采集退出");
//                    return true;
//                }
//                if (Job.getExecutorServiceMap().get(site) == null) Job.getExecutorServiceMap().put(site, Executors.newFixedThreadPool(list.size()));
//            }
//        } else {
//            if (Job.getExecutorServiceMap().get(site) == null) Job.getExecutorServiceMap().put(site, Executors.newFixedThreadPool(siteinfo.getThreadNum()));
//        }
//        return false;
    }

    public void submitSearchKey(String site, String key) {
        SearchKey skey = new SearchKey();
        skey.setKey(key);
        skey.setSite(site);
        Future<?> f = EXECUTOR_SERVICE_MAP.get(site).submit(DownFactory.metaControl(skey));
        Systemconfig.tasks.put(site + "_" + key, f);
    }

    public void submitSearchKey(SearchKey sk) {
        Future<?> f = EXECUTOR_SERVICE_MAP.get(sk.getSite()).submit(DownFactory.metaControl(sk));
        Systemconfig.tasks.put(sk.getSite() + "_" + sk.getKey(), f);
    }


    public static Map<String, ExecutorService> getExecutorServiceMap() {
        return EXECUTOR_SERVICE_MAP;
    }

    public static Map<String, ViewInfo> getFirst() {
        return first;
    }

    public static Job getJob() {

        return job;
    }

    public static String getHost() {
        return host;
    }

    public static void setHost(String host) {
        Job.host = host;
    }

    public static String getCid() {
        return cid;
    }

    public static void setCid(String cid) {
        Job.cid = cid;
    }

    public static String getIp() {
        return ip;
    }

    public static void setIp(String ip) {
        Job.ip = ip;
    }

    public static String getcType() {
        return cType;
    }

    public static void setcType(String cType) {
        Job.cType = cType;
    }

    public static String getProject() {
        return project;
    }

    public static void setProject(String project) {
        Job.project = project;
    }

    public static String getCrawlerNameOrCMD() {
        return crawlerNameOrCMD;
    }

    public static void setCrawlerNameOrCMD(String crawlerNameOrCMD) {
        Job.crawlerNameOrCMD = crawlerNameOrCMD;
    }


    public static String getNote() {
        return note;
    }

    public static void setNote(String note) {
        Job.note = note;
    }
}
