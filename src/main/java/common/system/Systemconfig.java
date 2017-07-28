package common.system;

import common.proxy.ProxyPoolRedis;
import common.task.CrawlerType;
import common.scheduler.Scheduler;
import common.service.DBService;
import common.siteinfo.Siteinfo;
import common.urlFilter.BloomFilterRedis;
import common.utils.HtmlExtractor;
import common.utils.UserAgent;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.*;
import java.util.concurrent.*;

/**
 * 系统相关配置
 *
 * @author grs
 * @since 0.5
 */
public class Systemconfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(Systemconfig.class);


    /**
     * 电商品牌代码
     */
    public static Map<String, String> ebusinessBrandCode = new HashMap<String, String>();

    /**
     * 注册的站点信息，包括各子站点，字符串标识
     */
    public static Map<String, Siteinfo> allSiteinfos = new HashMap<String, Siteinfo>();
    /**
     * 注册的下载类
     */
    public static Map<String, String> siteHttpClass = new HashMap<String, String>();
    /**
     * 注册的抽取类
     */
    public static Map<String, String> siteExtractClass = new HashMap<String, String>();

    /**
     * 任务是否完成
     */
    public static Map<String, Boolean> finish = new HashMap<String, Boolean>();
    /**
     * 自动抽取
     */
    public static HtmlExtractor htmlAutoExtractor = new HtmlExtractor();
    /**
     * url 去重
     */
    public static BloomFilterRedis urlFilter;
    public static int force_init_bf;
    /**
     * 调度器
     */
    public static Scheduler scheduler;
    /**
     * 文件存储路径
     */
    public static String localAddress;
    public static String filePath;
    public static String agentIp;
    public static int agentPort;
    public static boolean createFile;
    public static boolean createPic;
    public static String keywords;
    public static String table;
    /**
     * 读取配置类型，0文件读取，1数据库读取
     */
    public static int readConfigType;//

    public static String remote;// HDFS路径
    public static int upThreadNum;// 上传线程数
    public static boolean delLoaclFile;// 删除本地文件
    private int upInterval;// 上传时间间隔(时)
    private boolean needUp;// 是否需要上传

    /**
     * 系统运行前缀
     */
    public static String RUN_PREFIX;
    /**
     * 运行的任务
     */
    public static ConcurrentHashMap<String, Future<?>> taskResultMap = new ConcurrentHashMap<>();

    private static boolean distribute;// 是否使用分布式启动

    /**
     * 心跳线程
     */
    private ScheduledExecutorService heatBeat;
    /**
     * 交互线程
     */
    private ScheduledExecutorService copyConfig;

    /**
     * 数据库服务
     */
    public static DBService dbService;
    /**
     * 爬虫类型
     */
    public static int crawlerType;
    /**
     * 一共需要部署多少台爬虫
     */
    public static int crawlerCount = 0;
    /**
     * 当前爬虫的编号
     */
    public static int crawlerNum = 0;
    /**
     * 爬虫索引(第几个爬虫)
     */
    private static int clientIndex;
    /**
     * 用户管理
     */
    public static HashMap<String, List<UserAttribute>> users = new HashMap<String, List<UserAttribute>>();

    /**
     * 运行模式 test/run
     */
    public static String mode;

    /**
     * 代理池管理
     */
    public static ProxyPoolRedis proxyPoolRedis;


    public void initial() {
        value();//运行前缀和表名
//        initialSys();
        htmlAutoExtractor.init();


    }

    public static void initDBService() {

        String typeName = CrawlerType.getCrawlerTypeMap().get(Systemconfig.crawlerType).name().toLowerCase();

        String serviceName = typeName.substring(0, typeName.indexOf("_")) + "Service";
        dbService = (DBService) AppContext.appContext.getBean(serviceName);
        String typename = CrawlerType.getCrawlerTypeMap().get(Systemconfig.crawlerType).name().toLowerCase();
        String tablename = typename.substring(0, typename.indexOf("_")) + "_data";
        int c = dbService.getDataCount(tablename);
        LOGGER.info("DB service init succeed, {} has {} items.", tablename, c);
    }

    public static void initUrlFilter() {

        urlFilter = (BloomFilterRedis) AppContext.appContext.getBean("bloomFilterRedis");
        if (urlFilter == null) {
            LOGGER.error("redis 配置有误，系统退出！！");
            System.exit(-1);
        }
        urlFilter.init();
    }

    public static void initScheduler() {
        scheduler = (Scheduler) AppContext.appContext.getBean("defaultScheduler");
        if(scheduler == null){
            LOGGER.error("scheduler 配置有误，系统退出！！");
            System.exit(-1);
        }
        scheduler.init();
    }
    public static void readWeiboAccount() {
        try {
            String path = "src/main/resources/accountConf/weibo.xml";
            SAXReader reader = new SAXReader();
            Document doc = reader.read(new File(path));
            Element root = doc.getRootElement();
            List<UserAttribute> list = new ArrayList<UserAttribute>();
            for (Iterator i = root.elementIterator("user"); i.hasNext();) {
                Element ele = (Element) i.next();
                String valid  = ele.elementText("valid");
                if(!valid.equals("1")){
                    continue;
                }
                String name      = ele.elementText("name");
                String passwd    = ele.elementText("passwd");
                String siteFlag  = ele.elementText("siteFlag");

                UserAttribute user = new UserAttribute();
                user.setName(name);
                user.setPass(passwd);
                user.setSiteFlag(siteFlag);
                user.setUsed(0);
                user.setValid(true);
                user.setTryCount(0);
                user.setRunStatus(false);
                user.setAgentIndex(UserAgent.getUserAgentIndex());
                list.add(user);
            }
            Systemconfig.users.put("sina_weibo_search",list);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void initProxyManager() {
        proxyPoolRedis = (ProxyPoolRedis)AppContext.appContext.getBean("proxyPoolRedis");
        if(proxyPoolRedis ==null){
            LOGGER.error("代理池配置有误，系统退出！！");
            System.exit(-1);
        }
        proxyPoolRedis.init();
    }
    /**
     * 设置系统运行前缀 Systemconfig.RUN_PREFIX(e.g., "news_search_"c)
     * 设置表名 Systemconfig.table
     */
    private void value() {
        CrawlerType ct = CrawlerType.getCrawlerTypeMap().get(crawlerType);
        if (ct != null && !ct.name().equalsIgnoreCase("NOTYPE")) {
            RUN_PREFIX = ct.name() + "_";
            table = table == null ? "" : table;
            String str = ct.name().substring(0, ct.name().indexOf("_")).toLowerCase() + "_data";
            if ((crawlerType + 1) / 2 == 11) table += str.equals("company_data") ? "company_report" : str;
            else table += str;
        } else {
            LOGGER.error("没有找到相应的采集类型，系统退出！！");
            System.exit(-1);
        }
    }

    private String rmiName;
    private String serverAddress;


    public void setSiteExtractClass(Map<String, String> sitesClassName) {
        Systemconfig.siteExtractClass = sitesClassName;
    }

    public void setSiteHttpClass(Map<String, String> siteDownClass) {
        Systemconfig.siteHttpClass = siteDownClass;
    }

    public void setFilePath(String filePath) {
        Systemconfig.filePath = filePath;
    }

    public void setAgentIp(String agentIp) {
        Systemconfig.agentIp = agentIp;
    }

    public void setAgentPort(int agentPort) {
        Systemconfig.agentPort = agentPort;
    }

    public void setCreateFile(boolean createFile) {
        Systemconfig.createFile = createFile;
    }

    public void setCreatePic(boolean createPic) {
        Systemconfig.createPic = createPic;
    }


    public void setKeywords(String keywords) {
        Systemconfig.keywords = keywords;
    }

    public void setTable(String table) {
        Systemconfig.table = table;
    }

    public void setExtractor(HtmlExtractor extractor) {
        Systemconfig.htmlAutoExtractor = extractor;
    }

    public void setUpThreadNum(int upThreadNum) {
        Systemconfig.upThreadNum = upThreadNum;
    }

    public void setDelLoaclFile(boolean delLoaclFile) {
        Systemconfig.delLoaclFile = delLoaclFile;
    }

    public void setUpInterval(int upInterval) {
        this.upInterval = upInterval;
    }

    public void setNeedUp(boolean needUp) {
        this.needUp = needUp;
    }

    public void setRemote(String remote) {
        Systemconfig.remote = remote;
    }

    public void setLocalAddress(String localAddress) {
        Systemconfig.localAddress = localAddress;
    }

    public void setServerAddress(String serverAddress) {
        this.serverAddress = serverAddress;
    }

    public void setRmiName(String rmiName) {
        this.rmiName = rmiName;
    }

    public void setCrawlerType(int crawlerType) {
        Systemconfig.crawlerType = crawlerType;
    }


    public void setDistribute(boolean isDistribute) {
        Systemconfig.distribute = isDistribute;
    }

    public static boolean getDistribute() {
        return distribute;
    }

    public void setClientIndex(int clientIndex) {
        Systemconfig.clientIndex = clientIndex;
    }

    public static int getClientIndex() {
        return clientIndex;
    }

    public void setReadConfigType(int readConfigType) {
        Systemconfig.readConfigType = readConfigType;
    }

    public void setMode(String mode) {
        Systemconfig.mode = mode;
    }



}
