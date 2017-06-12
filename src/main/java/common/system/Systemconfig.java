package common.system;

import common.rmi.packet.Clientinfo;
import common.rmi.packet.CrawlerType;
import common.rmi.packet.TaskStatus;
import common.service.DBFactory;
import common.service.DBService;
import common.siteinfo.Siteinfo;
import common.urlFilter.BloomFilterRedis;
import common.util.HtmlExtractor;
import org.apache.log4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.*;

/**
 * 系统相关配置
 *
 * @author grs
 * @since 0.5
 */
public class Systemconfig {
    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(Systemconfig.class);


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
     * 初始化站点线程
     */
    public static Map<String, ExecutorService> metaexec = new HashMap<String, ExecutorService>();//没有用
    public static Map<String, ExecutorService> dataexec = new HashMap<String, ExecutorService>();
    /**
     * 任务是否完成
     */
    public static Map<String, Boolean> finish = new HashMap<String, Boolean>();
    /**
     * 自动抽取
     */
    public static HtmlExtractor extractor = new HtmlExtractor();
    public static BloomFilterRedis urlFilter;
    /**
     * 系统运行日志
     */
    public static LoggerManager sysLog = new LoggerManager(Logger.getLogger("system"));
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
    public static int md5NearbyDay;
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
    public static ConcurrentHashMap<String, Future<?>> tasks = new ConcurrentHashMap<>();
    /**
     * 任务状态
     */
    public static Map<String, TaskStatus> taskStatusManager = new HashMap<String, TaskStatus>();

    private static boolean distribute;// 是否使用分布式启动
    /**
     * 客户端信息
     */
    public static Clientinfo clientinfo;
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
    public static DBFactory dbFactory;
    /**
     * 爬虫类型
     */
    public static int crawlerType;
    /**
     * 一共需要部署多少台爬虫
     */
    public static int crawlerCount=0;
    /**
     * 当前爬虫的编号
     */
    public static int crawlerNum=0;
    /**
     * 爬虫索引(第几个爬虫)
     */
    private static int clientIndex;
    /**
     * 用户管理
     */
    public static HashMap<String, List<UserAttribute>> users;

    /**
     * 运行模式 test/run
     */
    public static String mode;

    /**
     * 配置加载完成后，系统初始化操作
     */
    
    public void initialSys(){
    	Properties props = new Properties();
        InputStream is=null;
		try {
			is = new FileInputStream("./config/config.properties");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        try {
			props.load(is);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        md5NearbyDay = Integer.parseInt(props.getProperty("md5NearbyDay"));
        try {
			is.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    public void initial() {
        value();
        initialSys();
        sysLog.start();
        extractor.init();
        dbService = dbFactory.dbService();
        if (dbService == null) {
            sysLog.log("没有找到相应的数据库服务，系统退出！！");
            System.exit(-1);
        }
        
        
        
        urlFilter.init();

    }

    /**
     * 创建数据采集线程池
     */
    public static void createThreadPool() {
        for (String site : allSiteinfos.keySet()) {
            int num = allSiteinfos.get(site).getThreadNum();
            dataexec.put(site, Executors.newFixedThreadPool(num > 5 ? 5 : num));
        }
    }

    private void value() {
        CrawlerType ct = CrawlerType.getCrawlerTypeMap().get(crawlerType);
        if (ct != null) {
            RUN_PREFIX = ct.name() + "_";
            table = table == null ? "" : table;
            String str = ct.name().substring(0, ct.name().indexOf("_")).toLowerCase() + "_data";
            if ((crawlerType + 1) / 2 == 11) table += str.equals("company_data") ? "company_report" : str;
            else table += str;
        } else {
            sysLog.log("没有找到相应的采集类型，系统退出！！");
            System.exit(-1);
        }
    }

    private String rmiName;
    private String serverAddress;

    public int start() {
        return clientinfo.getDataStart();
    }

    public int end() {
        return clientinfo.getDataEnd();
    }

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

    public void setDbFactory(DBFactory factory) {
        Systemconfig.dbFactory = factory;
    }

    public void setKeywords(String keywords) {
        Systemconfig.keywords = keywords;
    }

    public void setTable(String table) {
        Systemconfig.table = table;
    }

    public void setExtractor(HtmlExtractor extractor) {
        Systemconfig.extractor = extractor;
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
