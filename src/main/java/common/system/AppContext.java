package common.system;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.xml.transform.TransformerException;

import org.apache.xpath.XPathAPI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.xml.ResourceEntityResolver;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.Resource;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import common.task.CrawlerType;
import common.siteinfo.Siteinfo;
import common.utils.DOMUtil;
import common.utils.StringUtil;

/**
 * 系统初始启动
 *
 * @author grs
 */
public class AppContext {
    private static final Logger LOGGER = LoggerFactory.getLogger(AppContext.class);

    public static ApplicationContext appContext;

    /**
     * 配置文件的加载及初始化方法
     *
     * @param path 配置文件路径
     */
    public static void initAppCtx(String path) {

        //path = ""
        initEbusinessBrandCode();// 电商垂直商品编码，用于直接设置品牌
        File[] files = new File(path).listFiles();
        //File[] files = path.listFiles();
        ArrayList<String> list = new ArrayList<String>();
        for (File file : files) {
            if (file.getName().startsWith("app")) {
                list.add(file.getName());
//                list.add(path + "config" + File.separator + file.getName());
            }
        }
        String[] arry = new String[list.size()];
        list.toArray(arry);

//        appContext = new FileSystemXmlApplicationContext(arry);
        appContext = new ClassPathXmlApplicationContext(arry);

        list.clear();
        files = null;
        arry = null;

        Systemconfig.initDBService();
        Systemconfig.initUrlFilter();
        Systemconfig.initScheduler();
        Systemconfig.initProxyManager();

        if(Systemconfig.crawlerType==7){//||Systemconfig.crawlerType==8
            Systemconfig.readWeiboAccount();
        }
        readConfig();

        LOGGER.info("init. ok. ");
    }

    public static void readConfig() {// 读取配置
        readConfigFromFile();
    }

    private static String typeConfFolder = "typeConf";
    private static String xpathConfFolder = "xpathConf";
    private static Map<String, FileEntry> xpathTemplateMap = new HashMap<String, FileEntry>();

    public static Map<String, FileEntry> getXpathTemplateMap() {
        return xpathTemplateMap;
    }

    /**
     * 加载文件的属性结构
     *
     * @author grs
     */
    class FileEntry {
        String content;
        long modify;
        boolean load = true;
    }

    /**
     * 文件过滤
     *
     * @author grs
     */
    public static class MyFileFilter implements FileFilter {

        String prefix = CrawlerType.getCrawlerTypeMap().
                get(Systemconfig.crawlerType).name().toLowerCase();

        @Override
        public boolean accept(File f) {
            return f.getName().startsWith(prefix) && !f.getName().replace(xpathConfFolder, "").replace(File.separator, "").startsWith(".") && f.getName().endsWith("xml");
        }
    }

    /**
     * 从文件读取站点配置
     */
    public static void readConfigFromFile() {
        // xpathConfFolder = 'typeConfFolder'
        String path = "src/main/resources".replace("/", File.separator);
        File[] xpathFs = new File(path + File.separator + xpathConfFolder).listFiles(new MyFileFilter());
        if (xpathFs == null) {
            LOGGER.info("没有可运行配置站点");
            return;
        }
        for (File f : xpathFs) {
            String content = StringUtil.getContent(f.getAbsolutePath());

            // String name=f.getName();//ebusiness_search_taobao.xml
            // long modified=f.lastModified();//1409798260836

            //配置文件写到map   k=f.getName() value = 文件状态 
//            LOGGER.info("configSet(f.getName(), content, f.lastModified());");
//            LOGGER.info(f.getName());
//            LOGGER.info(content);
            configSet(f.getName(), content, f.lastModified());
        }
        loadSiteFromFile();
    }

    /**
     * 加载所有站点公共配置
     */
    private static void loadSiteFromFile() {
        // 读取简单配置后，处理详细配置

        String path = "src/main/resources/".replace("/", File.separator) + typeConfFolder;
        File[] fs = new File(path).listFiles(new MyFileFilter());
        if (fs == null || fs.length == 0) {
            LOGGER.info("没有采集的类型配置！");
            return;
        } else if (fs.length > 1) {
            LOGGER.info("采集的类型配置超过一个，无法指定！");
            return;
        }
        File f = fs[0];// 采集类型： config\typeConfFolder\news_monitor.xml
        // 根据map大小复制数据
        for (String s : xpathTemplateMap.keySet()) {
            //
            String name = f.getName().substring(0, f.getName().lastIndexOf(".")) + "_" + s.substring(s.lastIndexOf("_") + 1, s.length());// ebusiness_search_taobao.xml

            String content = StringUtil.getContent(f.getAbsolutePath());
            configProcess(name, content);
        }
    }

    static Map<String, SiteTemplateAttribute> siteConfigs = null;

    /**
     * 配置数据结构属性设置，公用
     *
     * @param name      配置名称   video_search_soku
     * @param content   配置内容
     * @param timestamp 最新修改日期
     */
    public static void configSet(String name, String content, long timestamp) {
        if (!xpathTemplateMap.containsKey(name)) {
            FileEntry fe = new AppContext().new FileEntry();
            fe.content = content;
            fe.modify = timestamp;
            xpathTemplateMap.put(name, fe);
        } else {
            FileEntry fe = xpathTemplateMap.get(name);
            if (fe.modify != timestamp) {
                fe.content = content;
                fe.modify = timestamp;
                fe.load = true;
            }
        }
    }

    /**
     * 公有配置处理，公用
     *
     * @param name    配置名称
     * @param content 配置内容
     */
    public static void configProcess(String name, String content) {
        if (xpathTemplateMap.containsKey(name)) {
            FileEntry fe = xpathTemplateMap.get(name);
            if (!fe.load) return;

            DOMUtil dom = new DOMUtil();
            Node domtree = null;
            try {
                domtree = dom.ini(fe.content, "utf-8");
            } catch (SAXException e1) {
                e1.printStackTrace();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            NodeList nameList = null;
            NodeList valueList = null;
            try {
                nameList = XPathAPI.selectNodeList(domtree, "/SITE/PROP/@name");
                valueList = XPathAPI.selectNodeList(domtree, "/SITE/PROP/@value");
            } catch (TransformerException e) {
                e.printStackTrace();
            }
            for (int i = 0; i < nameList.getLength(); i++) {
                content = content.replace("${" + nameList.item(i).getTextContent() + "}",
                        filterCode(valueList.item(i).getTextContent()));
            }
            // 暂时需要特殊处理boolean型属性
            content = content.replace("${agent}", "false").replace("${login}", "false");

            String path = Thread.currentThread().getContextClassLoader().getResource("").getPath();
            String tmp = typeConfFolder + File.separator + name + ".temp";
            StringUtil.writeFile(path+ tmp, content);

            loadDynamicBean(tmp);
            fe.load = false;
        }
    }

    private static String filterCode(String str) {
        return str.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("“", "&quot;");
    }

    private static synchronized void loadDynamicBean(String file) {

        LOGGER.info("ini:" + file);
        XmlBeanDefinitionReader beanReader = new XmlBeanDefinitionReader(((BeanDefinitionRegistry)
                ((ConfigurableApplicationContext) appContext).getBeanFactory()));
        beanReader.setResourceLoader(appContext);
        beanReader.setEntityResolver(new ResourceEntityResolver(appContext));
        try {
            Resource[] resources = appContext.getResources(file);
            beanReader.loadBeanDefinitions(resources);
            resources = null;
            String substring = file.substring(file.lastIndexOf(File.separator) + 1, file.indexOf("."));// .xml改成了.

            Siteinfo si = (Siteinfo) (appContext.getBean(substring));
            //验证站点信息数据是否完整,成功后添加站点
            Systemconfig.allSiteinfos.put(si.getSiteName(), si);
            if (siteConfigs != null && siteConfigs.get(si.getSiteName()) != null) {
                si.setSiteFlag(siteConfigs.get(si.getSiteName()).getId());
            }
            File f = new File(file);
            if (!f.delete()) {
                if(f.exists()) {
                    LOGGER.error(f + "没有被删除");
                }
            }
            LOGGER.info("系统初始化站点：" + si);
        } catch (BeansException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void initEbusinessBrandCode() {
        Systemconfig.ebusinessBrandCode.put("20000%3A3227275&", "米其林");
        Systemconfig.ebusinessBrandCode.put("20000%3A20842&", "邓禄普");
        Systemconfig.ebusinessBrandCode.put("20000%3A52914076&", "三角");
        Systemconfig.ebusinessBrandCode.put("20000%3A46110&", "朝阳");
        Systemconfig.ebusinessBrandCode.put("20000%3A53715&", "玲珑");
        Systemconfig.ebusinessBrandCode.put("20000%3A3227276&", "普利司通");
        Systemconfig.ebusinessBrandCode.put("20000%3A3227284&", "德国马牌");
        Systemconfig.ebusinessBrandCode.put("20000%3A3227277&", "固特异");

    }

}
