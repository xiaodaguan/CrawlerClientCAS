package common.mapper;

import common.pojos.NewsData;
import common.pojos.WeiboData;
import org.apache.ibatis.annotations.Param;

/**
 * Created by guanxiaoda on 2017/6/15.
 */
public interface WeiboMapper extends BaseMapper<WeiboData>{


    public int create(WeiboData data);

    public WeiboData read(@Param("id") int id);

    public void update(WeiboData data);

    public void delete(int id);
}
