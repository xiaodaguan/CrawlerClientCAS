package common.service;

import common.bean.*;
import common.rmi.packet.SearchKey;
import common.system.SiteTemplateAttr;
import common.system.UserAttr;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * mysql数据库操作
 *
 * @author Administrator
 */
public interface DBService<T> {

    /**
     * @param cs
     * @param cts
     * @param key
     * @param tpye: 1job2meta3data
     */
    public void updateStatus(CrawlerStatus cs, CrawlerTaskStatus cts, SearchKey key, int tpye);

    /**
     * 将日志保存到数据库，共有4种类型: 1启动 2采集 3异常 4完成
     *
     * @param siteFlag
     * @param sk
     * @param logType
     * @param info:    type 2, info0 检索数据条数 info1 新数据条数; type 3, info0 异常信息; type 4, info0 入库条数
     * @throws IOException
     */
    public void saveLog(String siteFlag, SearchKey sk, int logType, String... info) throws IOException;

    /**
     * 保存数据
     *
     * @param list
     * @throws IOException
     */
    public void saveDatas(List<T> list) throws IOException;

    public void saveData(T t) throws IOException;

    /**
     * 删除表中重复的数据
     *
     * @param url
     * @param table
     */
    public void deleteReduplicationUrls(List<String> url, String table);

    /**
     * 获得表中的md5
     *
     * @param string
     * @param map
     * @return
     */
    public int getAllMd5(String string, Map<String, List<String>> map);

    /**
     * 处理异常数据
     *
     * @param md5
     * @param table
     */
    public void exceptionData(String md5, String table);

    /**
     * 过滤重复数据
     *
     * @param list
     * @param table
     * @return
     */
    List<? extends CommonData> getNorepeatData(List<? extends CommonData> list, String table);

    /**
     * 数据库中的检索词
     *
     * @return
     */
    public List<SearchKey> searchKeys();

    /**
     * 获得需要登录的网站的用户
     *
     * @param site
     * @return
     */
    List<UserAttr> getLoginUsers(String site);

    /**
     * 根据crawlerType过滤，获得需采集的站点xpath配置
     *
     * @return
     */
    Map<String, SiteTemplateAttr> getXpathConfig();

    /**
     * 根据crawlerType过滤，获得采集类型配置
     *
     * @return
     */
    String getTypeConfig();

    /**
     * 随机返回一个header(inserttime 在7天内)
     */
    Header randomHeaderFromDB();

    void updateUserOrder(String userName);

    Proxy getProxy(int siteId);

    /**
     * 更新代理使用时间
     *
     * @param proxyInfo: ip:port:domainId
     */
    public void updateProxyOrder(String proxyInfo);

    public int saveGongzhongData(WxpublicData wpd);


    public void saveCommentDatas(List<T> list) throws IOException;

    public void saveCommentData(T t) throws IOException;
}