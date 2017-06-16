package common.service;

import common.rmi.packet.SearchKey;
import common.mapper.BaseMapper;
import common.system.UserAttribute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by guanxiaoda on 2017/6/15.
 */
public abstract class BaseService<T> implements DBService<T> {
    private static final Logger LOGGER = LoggerFactory.getLogger(BaseService.class);

    private BaseMapper<T> baseMapper;


    public void setBaseMapper(BaseMapper baseMapper) {
        this.baseMapper = baseMapper;
    }

    @Override
    public void saveDatas(List<T> list){

    }

    @Override
    public int saveData(T data){
        return 0;
    }


    @Override
    public T getData(int id){
        return null;
    }


    /* common methods */

    @Override
    public List<String> getAllMd5(String tablename) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("tablename",tablename);
        return baseMapper.getAllMD5ByType(paramMap);
    }

    @Override
    public List<SearchKey> getAllSearchKeys() {
        return baseMapper.selectAllSearchKeyword();
    }

    @Override
    public List<UserAttribute> getLoginUsers(String site) {
        return null;
    }

    @Override
    public void updateUserValid(String userName, int mark) {

    }

    @Override
    public void updateUserOrder(String userName) {

    }


    @Override
    public int getDataCount(String tablename){
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("tablename",tablename);
        return baseMapper.getTotalCountByType(paramMap);
    }
}
