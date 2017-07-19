package common.system;

import common.rmi.packet.SearchKey;
import common.rmi.packet.ViewInfo;
import common.siteinfo.Siteinfo;
import crawlerlog.log.CLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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


    public static void simpleRun() throws UnknownHostException, InterruptedException {}

    /**
     * 普通搜索采集
     *
     * @throws Exception
     */   //  
    @SuppressWarnings("unchecked")
    private static void runSearch() throws UnknownHostException, InterruptedException {}



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
