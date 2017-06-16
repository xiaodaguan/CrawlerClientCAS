package common.mapper;

import common.pojos.NewsData;
import common.pojos.WeixinData;
import org.apache.ibatis.annotations.Param;

/**
 * Created by guanxiaoda on 2017/6/15.
 */
public interface NewsMapper extends BaseMapper<NewsData>{


    public int create(NewsData data);

    public NewsData read(@Param("id") int id);

    public void update(NewsData data);

    public void delete(int id);
}
