package common.mapper;

import common.task.SearchKey;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * Created by guanxiaoda on 2017/6/14.
 */
@Repository
public interface BaseMapper<T> {


    public List<String> getAllMD5ByType(Map<String, Object> paramMap);

    public int getTotalCountByType(Map<String, Object> paramMap);

    public List<SearchKey> selectAllSearchKeyword();

    public int create(T t);

    public T read(long id);

    public void update(T t);

    public void delete(long id);

    public int getCountByDay(Map<String, Object> paramMap);
}
