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


    void checkData(T data);

    T getData(int id);


}