package common.system;

import common.download.DownFactory;
import common.rmi.packet.CrawlerType;
import common.rmi.packet.SearchKey;
import common.rmi.packet.ViewInfo;
import common.rmi.packet.ViewInfo.InnerInfo;
import common.siteinfo.Siteinfo;
import common.utils.StringUtil;
import common.utils.TimeUtil;
import crawlerlog.log.CLog;
import crawlerlog.log.CLogFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Job {

    private static final Logger LOGGER = LoggerFactory.getLogger(Job.class);

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
    public final static Map<String, ExecutorService> META_THREAD_POOL_MAP = new HashMap<String, ExecutorService>();

    public final static Map<String, ExecutorService> DATA_THREAD_POOL_MAP = new HashMap<String, ExecutorService>();

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
                    LOGGER.info("beat...");
                    try {
                        Thread.sleep(60 * 1000);
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
            LOGGER.info("loop start...");

            if (Systemconfig.crawlerType == 39) {
                String[] dailies = {"人民日报", "人们日报海外版", "新华每日电讯", "解放军报", "求是", "光明日报",
                        "经济日报", "科技日报", "工人日报", "中国青年报", "农民日报", "人民日报", "人民日报海外版",
                        "光明日报", "经济日报", "解放军报", "中国青年报", "参考消息"};

                List<SearchKey> prekeys = null;
                try {
                    prekeys = Systemconfig.dbService.getAllSearchKeys();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                keys = new LinkedList<SearchKey>();
                for (String dialy : dailies) {
                    for (SearchKey searchKey : prekeys) {
                        SearchKey sk = new SearchKey();
                        sk.setKEYWORD(searchKey.getKEYWORD() + " " + dialy);
                        sk.setCATEGORY_CODE(searchKey.getCATEGORY_CODE());
                        sk.setSITE_ID(searchKey.getSITE_ID());
                        keys.add(sk);
                    }
                }
            } else {
                try {
                    keys = Systemconfig.dbService.getAllSearchKeys();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            LOGGER.info(keys.size() + "个关键词将采集:");
            out:
            for (SearchKey sk : keys) {

                for (String site : Systemconfig.allSiteinfos.keySet()) {

                    Siteinfo siteinfo = Systemconfig.allSiteinfos.get(site);
                    sk.setSITE_NAME(site);

                    createThreadPool(site, siteinfo);

                    String taskName = sk.getSITE_NAME() + sk.getKEYWORD();
                    if (Systemconfig.finish.get(taskName) == null
                            || Systemconfig.finish.get(taskName)) {

                        job.submitSearchKey(sk);

                        Systemconfig.finish.put(taskName, false);
                        LOGGER.info(taskName + "     任务提交");

                    } else {
                        LOGGER.info(taskName + "     任务未提交，或已完成！！！");
                    }
                }
            }

            long start = System.currentTimeMillis();

            while (!ifAllFinished()) {

                Thread.currentThread().sleep(60 * 1000);
                if (start + 1000 * 3600 * 24 < System.currentTimeMillis()) {
                    //单循环最大24h
                    LOGGER.info("single loop time out, stopping...");

                    Set<String> taskNames = Systemconfig.taskResultMap.keySet();
                    //接收的任务
                    for (String taskName : taskNames) {
                        if (!Systemconfig.taskResultMap.get(taskName).isDone()) {
                            LOGGER.info(taskName + "任务被强制停止");
                            Systemconfig.taskResultMap.get(taskName).cancel(true);
                            if (!Systemconfig.taskResultMap.get(taskName).isDone()) {
                                Systemconfig.taskResultMap.get(taskName).cancel(true);
                                LOGGER.info(taskName + "任务第一次强制停止失败，再次被强制停止");
                            }
                        }
                    }
                    Systemconfig.finish.clear();
                    Systemconfig.taskResultMap.clear();
                    LOGGER.info("all taskResultMap stopped. ");

                    break;
                }
            }
            cLogger.stop();
            LOGGER.info("loop stop");

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
            LOGGER.info("loop start...");

            keys = Systemconfig.dbService.getAllSearchKeys();
            LOGGER.info(keys.size() + "个关键词将采集:");

            LOGGER.info(">>keys: \n" + keys);
            LOGGER.info(">>crawler map: \n" + CrawlerType.getCrawlerTypeMap().get(Systemconfig.crawlerType));
            out:
            for (SearchKey sk : keys) {
                String site = sk.getSITE_NAME() + "_" + CrawlerType.getCrawlerTypeMap().get(Systemconfig.crawlerType).name().toLowerCase();
                Siteinfo siteinfo = Systemconfig.allSiteinfos.get(site);
                if (siteinfo == null) {
                    LOGGER.info("siteinfo is null");
                    continue;
                }
                sk.setSITE_NAME(site);
                createThreadPool(site, siteinfo);
                String taskName = sk.getSITE_NAME() + sk.getKEYWORD();
                if (Systemconfig.finish.get(taskName) == null || Systemconfig.finish.get(taskName)) {
                    job.submitSearchKey(sk);
                    Systemconfig.finish.put(taskName, false);
                }
            }
            long start = System.currentTimeMillis();
            while (!ifAllFinished()) {
                Thread.currentThread().sleep(60 * 1000);
                if (start + 1000 * 3600 * 24 < System.currentTimeMillis()) {
                    //单循环最大24h
                    LOGGER.info("single loop time out, stopping...");
                    Set<String> taskNames = Systemconfig.taskResultMap.keySet();

                    //接收的任务
                    for (String taskName : taskNames) {
                        if (Systemconfig.taskResultMap.containsKey(taskName)) {
                            Systemconfig.taskResultMap.get(taskName).cancel(true);
                        }
                    }
                    LOGGER.info("all taskResultMap stopped. ");

                    Systemconfig.finish.clear();
                    Systemconfig.taskResultMap.clear();
                    //Systemconfig.finish = new HashMap<>();
                    //Systemconfig.taskResultMap = new ConcurrentHashMap<>();
                    //Systemconfig.DATA_THREAD_POOL_MAP = new ConcurrentHashMap<>();
                    break;
                }
            }
            cLogger.stop();
            LOGGER.info("loop stop");

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
        Set<String> taskNames = Systemconfig.taskResultMap.keySet();

        for (String taskName : taskNames) {
            if (Systemconfig.taskResultMap.containsKey(taskName)) {
                if (Systemconfig.taskResultMap.get(taskName).isDone()) {
                    Systemconfig.finish.put(taskName, true);
                } else {
                    runningTaskCount++;
                }
            }
        }
        if (runningTaskCount > 0) allFinished = false;
        else {
            Systemconfig.finish.clear();
            Systemconfig.taskResultMap.clear();
        }
        LOGGER.info("Systemconfi.task stat: ");
        LOGGER.info("running task count: " + runningTaskCount + " / total task count: " + Systemconfig.taskResultMap.size());

        return allFinished;
    }


    /**
     * 获取 每一轮(爬虫启动)之间等待时间
     *
     * @return
     */
    private static int calCycleWaitTime() {
        int waitTime = 20 * 60;
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

        if (Job.getMetaThreadPoolMap().get(site) == null) {
            Job.getMetaThreadPoolMap().put(site, Executors.newFixedThreadPool(siteinfo.getThreadNum()));
        }


        return false;
    }

    public void submitSearchKey(SearchKey sk) {
        Future<?> f = META_THREAD_POOL_MAP.get(sk.getSITE_NAME()).submit(DownFactory.metaControl(sk));
        Systemconfig.taskResultMap.put(sk.getSITE_NAME() + "_" + sk.getKEYWORD(), f);
    }


    public static Map<String, ExecutorService> getMetaThreadPoolMap() {
        return META_THREAD_POOL_MAP;
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
