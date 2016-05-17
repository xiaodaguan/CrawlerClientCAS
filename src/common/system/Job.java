package common.system;

import common.bean.CrawlerStatus;
import common.bean.CrawlerTaskStatus;
import common.bean.CrawlerTypeName;
import common.down.DownFactory;
import common.rmi.packet.CrawlerType;
import common.rmi.packet.SearchKey;
import common.rmi.packet.ViewInfo;
import common.rmi.packet.ViewInfo.InnerInfo;
import common.siteinfo.Siteinfo;
import common.util.StringUtil;
import common.util.TimeUtil;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Job {
    /**
     * 线程池管理
     */
    private final static Map<String, ExecutorService> execMap = new HashMap<String, ExecutorService>();

    private static Job job = new Job();
    static Map<String, ViewInfo> first = new HashMap<String, ViewInfo>();
    public static List<SearchKey> keys = null;

    /**
     * true 则跳过不采集
     *
     * @param site
     * @return
     */
    private static boolean filter(String site) {
        if (site.contains(""))// site
            return false;
        return true;
    }

    public static Map<String, ViewInfo> getFirst() {
        return first;
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
                if (Job.getExecMap().get(site) == null) Job.getExecMap().put(site, Executors.newFixedThreadPool(list.size()));
            }
        } else {
            if (Job.getExecMap().get(site) == null) Job.getExecMap().put(site, Executors.newFixedThreadPool(si.getThreadNum()));
        }
    }

    /**
     * 运行站点的某个搜索词或垂直网址
     *
     * @param si
     * @param sk
     * @param set
     * @param vi
     */
    public static void runSearchKey(Siteinfo si, SearchKey sk, ViewInfo vi) {
		/* 状态 */
        String taskId = sk.getKey() + " " + sk.getSite() + " " + new Date().toString();
        sk.setCrawlerStatusId(taskId);
        CrawlerTaskStatus cts = new CrawlerTaskStatus(taskId, new Date(), sk.getKey(), si.getDownInterval(), si.getThreadNum(), false, 0, 0, 0, "INIT");
        Systemconfig.crawlerStatus.getTasks().put(taskId, cts);

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

            job.listSearchKey(sk);

            Systemconfig.finish.put(searchKey, false);
        }
    }

    /**
     * 普通搜索采集
     *
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    private static void simpleSearchRun() {
        Job job = new Job();
        // if (filterKw.contains("_")) {// 测试一个关键词
        // keys = new ArrayList();
        // if (filterKw.contains(";")) {
        // System.out.println("测试n个关键词：" + filterKw);
        // String[] kws = filterKw.split(";");
        // for (String string : kws) {
        // SearchKey sk1 = new SearchKey();
        // sk1.setKey(string.split("_")[0]);
        // sk1.setSite(string.split("_")[1]);
        // keys.add(sk1);
        // }
        // } else {
        // System.out.println("测试一个关键词：" + filterKw);
        // SearchKey sk1 = new SearchKey();
        // sk1.setKey(filterKw.split("_")[0]);
        // sk1.setSite(filterKw.split("_")[1]);
        // keys.add(sk1);
        // }
        // } else {
        // keys = Systemconfig.dbService.searchKeys();
        // }

		/* 状态 */
        String name = "standalone_search_" + CrawlerTypeName.map.get(Systemconfig.crawlerType);
        String ip = "127.0.0.1";
        try {
            ip = InetAddress.getLocalHost().getHostAddress().toString();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        Systemconfig.crawlerStatus = new CrawlerStatus(name, Systemconfig.crawlerType, new Date(), ip, "INIT");

        while (true) {
            keys = Systemconfig.dbService.searchKeys();
            Systemconfig.sysLog.log(keys.size() + "个关键词将采集:");
            Systemconfig.sysLog.log("当前execmap状态:");
            Systemconfig.sysLog.log(execMap.toString());
//            for (SearchKey sk : keys) {
//                Systemconfig.sysLog.log(sk.getKey() + ", ");
//            }
            ArrayList<String> listRunning = new ArrayList<String>();
            out:
            for (SearchKey sk : keys) {


                for (String site : Systemconfig.allSiteinfos.keySet()) {

                    Siteinfo si = Systemconfig.allSiteinfos.get(site);
                    sk.setSite(site);
                    if (si.getLogin()) {
                        // login
                        if (Systemconfig.users == null) Systemconfig.users = new HashMap<String, List<UserAttr>>();
                        if (Systemconfig.users.get(site) == null) {
                            List<UserAttr> list = Systemconfig.dbService.getLoginUsers(site);
                            Systemconfig.users.put(site, list);
                            if (list.size() == 0) {
                                Systemconfig.sysLog.log("没有可用账号！本轮采集退出");
                                break out;
                            }
                            if (Job.getExecMap().get(site) == null) Job.getExecMap().put(site, Executors.newFixedThreadPool(list.size()));
                        }
                    } else {
                        //
                        if (Job.getExecMap().get(site) == null) Job.getExecMap().put(site, Executors.newFixedThreadPool(si.getThreadNum()));
                    }
                    String ss = sk.getSite() + sk.getKey();
                    if (Systemconfig.finish.get(ss) == null || Systemconfig.finish.get(ss)) {
                        job.listSearchKey(sk);
                        listRunning.add(ss);

						/* 状态 */
                        String taskId = sk.getKey() + " " + sk.getSite() + " " + new Date().toString();
                        sk.setCrawlerStatusId(taskId);
                        CrawlerTaskStatus cts = new CrawlerTaskStatus(taskId, new Date(), sk.getKey(), si.getDownInterval(), si.getThreadNum(), false, 0, 0, 0, "INIT");
                        Systemconfig.crawlerStatus.getTasks().put(taskId, cts);

                        Systemconfig.finish.put(ss, false);
                    }
                }
            }
            if (listRunning.size() == 0) {
                System.out.println("nothing running.");

            } else {
//                System.out.println(listRunning);
            }
			/* 状态 */
            Systemconfig.crawlerStatus.setStartKeywordSet(Systemconfig.crawlerStatus.getAllKeywords());
            Systemconfig.dbService.updateStatus(Systemconfig.crawlerStatus, null, null, 1);

            if (Systemconfig.crawlerType == CrawlerType.EBUSINESS_SEARCH.ordinal() || Systemconfig.crawlerType == CrawlerType.EBUSINESS_MONITOR.ordinal())
                TimeUtil.rest(30 * 24 * 60 * 60);
            else if (Systemconfig.crawlerType == CrawlerType.NEWS_SEARCH.ordinal() || Systemconfig.crawlerType == CrawlerType.NEWS_MONITOR.ordinal())
                TimeUtil.rest(2 * 60 * 60);
            else TimeUtil.rest(6 * 60 * 60);

            if (Systemconfig.readConfigType == 0) AppContext.readConfigFromFile();// 每一轮后重新加载一次配置
            else AppContext.readConfigFromDB();
        }

    }

    /**
     * 普通垂直采集
     */
    @SuppressWarnings("unchecked")
    private static void simpleMonitorRun() {
        Job job = new Job();
		/* 状态 */
        String name = "standalone_monitor_" + CrawlerTypeName.map.get(Systemconfig.crawlerType);
        String ip = "127.0.0.1";
        try {
            ip = InetAddress.getLocalHost().getHostAddress().toString();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        Systemconfig.crawlerStatus = new CrawlerStatus(name, Systemconfig.crawlerType, new Date(), ip, "INIT");


        keys = Systemconfig.dbService.searchKeys();
        Systemconfig.sysLog.log(keys.size() + "个关键词将采集:");
        ArrayList<String> listRunning = new ArrayList<String>();
        out:
        for (SearchKey sk : keys) {


            String site = sk.getSite() + "_" + CrawlerType.getMap().get(Systemconfig.crawlerType).name().toLowerCase();

            Siteinfo si = Systemconfig.allSiteinfos.get(site);

            if (si == null) continue;

            sk.setSite(site);
            if (si.getLogin()) {
                if (Systemconfig.users == null) Systemconfig.users = new HashMap<String, List<UserAttr>>();
                if (Systemconfig.users.get(site) == null) {
                    List<UserAttr> list = Systemconfig.dbService.getLoginUsers(site);
                    Systemconfig.users.put(site, list);
                    if (list.size() == 0) {
                        Systemconfig.sysLog.log("没有可用账号！本轮采集退出");
                        break out;
                    }
                    if (Job.getExecMap().get(site) == null) Job.getExecMap().put(site, Executors.newFixedThreadPool(list.size()));
                }
            } else {
                if (Job.getExecMap().get(site) == null) Job.getExecMap().put(site, Executors.newFixedThreadPool(si.getThreadNum()));
            }
            String ss = sk.getSite() + sk.getKey();
            if (Systemconfig.finish.get(ss) == null || Systemconfig.finish.get(ss)) {
                job.listSearchKey(sk);
                listRunning.add(ss);
					/* 状态 */
                String taskId = sk.getKey() + " " + sk.getSite() + " " + new Date().toString();
                CrawlerTaskStatus cts = new CrawlerTaskStatus(taskId, new Date(), sk.getKey(), si.getDownInterval(), si.getThreadNum(), false, 0, 0, 0, "INIT");
                sk.setCrawlerStatusId(taskId);
                Systemconfig.crawlerStatus.getTasks().put(taskId, cts);

                Systemconfig.finish.put(ss, false);
                TimeUtil.rest(1);
            }
        }
			/* 状态 */
        Systemconfig.crawlerStatus.setStartKeywordSet(Systemconfig.crawlerStatus.getAllKeywords());
        Systemconfig.dbService.updateStatus(Systemconfig.crawlerStatus, null, null, 1);

        if (listRunning.size() == 0) {
            System.out.println("nothing running.");

        } else {
//            System.out.println(listRunning);
        }


    }

    public void list(String site, String key) {
        SearchKey skey = new SearchKey();
        skey.setKey(key);
        skey.setSite(site);
        Future<?> f = execMap.get(site).submit(DownFactory.metaControl(skey));
        Systemconfig.tasks.put(site + "_" + key, f);
    }

    public void listSearchKey(SearchKey sk) {
        Future<?> f = execMap.get(sk.getSite()).submit(DownFactory.metaControl(sk));
        Systemconfig.tasks.put(sk.getSite() + "_" + sk.getKey(), f);

    }



    public static void simpleRun() {
        if (Systemconfig.crawlerType % 2 == 1) simpleSearchRun();
        else simpleMonitorRun();
    }
    public static Map<String, ExecutorService> getExecMap() {
        return execMap;
    }

    public static Job getJob() {
        return job;
    }
}
