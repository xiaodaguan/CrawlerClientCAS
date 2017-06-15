package common.mapper;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * Created by guanxiaoda on 2017/6/14.
 */
@Repository
public interface UserMapper<T> {


    public List<String> getAllMD5ByType(Map<String, Object> paramMap);

    public int getTotalCountByType(Map<String, Object> paramMap);

    public List<T> getAllDataByType(Map<String, Object> paramMap);

}
