package common.service;

import common.task.SearchKey;
import common.mapper.BaseMapper;
import org.apache.commons.lang.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
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
    public void saveDatas(List<T> list) throws Exception {

    }

    @Override
    public abstract int saveData(T data) throws Exception;

    @Override
    public void checkData(T data) {
        Class cls = data.getClass();
        Class superCls = cls.getSuperclass();

        Field[] fields = cls.getDeclaredFields();
        Field[] commonFields = superCls.getDeclaredFields();

        Field[] allFields = (Field[]) ArrayUtils.addAll(fields,commonFields);

        for(Field field: allFields){
            field.setAccessible(true);
            try {
                Object o = field.get(data);
                if(o==null) {
                    if(field.getType() == String.class){
                        field.set(data,"NULL");
                    }

                }else{
                }

            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
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
    public int getDataCount(String tablename){
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("tablename",tablename);
        return baseMapper.getTotalCountByType(paramMap);
    }

}
