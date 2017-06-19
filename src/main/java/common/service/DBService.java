package common.service;

import common.rmi.packet.SearchKey;
import common.system.UserAttribute;

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

    public int saveData(T t) throws IOException;

    /**
     * 获得表中的md5
     *
     * @param tablename
     * @return
     */
    public List<String> getAllMd5(String tablename);

    public int getDataCount(String tablename);

    /**
     * 数据库中的检索词
     *
     * @return
     */
    public List<SearchKey> getAllSearchKeys();

    /**
     * 获得需要登录的网站的用户
     *
     * @param site
     * @return
     */
    List<UserAttribute> getLoginUsers(String site);

    /**
     * 更新微博账号valid有效字段，
     * @param userName 微博账号名称
     * @param mark     字段标识
     */
    void updateUserValid(String userName,int mark);

    void updateUserOrder(String userName);


    T getData(int id);


    int getDataCountByDay(Map<String, Object> paramMap);
}