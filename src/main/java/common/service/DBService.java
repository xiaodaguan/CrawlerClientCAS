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
     * 更新微博账号valid有效字段，
     * @param userName 微博账号名称
     * @param mark     字段标识
     */
    void updateUserValid(String userName,int mark);

    void updateUserOrder(String userName);

    public int saveGongzhongData(WxpublicData wpd);


    public void saveCommentDatas(List<T> list) throws IOException;

    public void saveCommentData(T t) throws IOException;
}