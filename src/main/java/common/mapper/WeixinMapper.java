package common.mapper;

import common.pojos.WeixinData;
import org.apache.ibatis.annotations.Param;

/**
 * Created by guanxiaoda on 2017/6/15.
 */
public interface WeixinMapper extends BaseMapper<WeixinData>{


    public int create(WeixinData data);

    public WeixinData read(@Param("id") int id);

    public void update(WeixinData data);

    public void delete(int id);
}
