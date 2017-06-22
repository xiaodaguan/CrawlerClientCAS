package common.service;

import common.mapper.WeixinMapper;
import common.pojos.WeixinData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by guanxiaoda on 2017/6/16.
 */
public class WeixinService extends BaseService<WeixinData> {
    private static final Logger LOGGER = LoggerFactory.getLogger(WeixinService.class);

    private WeixinMapper weixinMapper;



    public void setWeixinMapper(WeixinMapper weixinMapper) {
        this.weixinMapper = weixinMapper;
    }

    @Override
    public void saveDatas(List<WeixinData> list) {
        for (WeixinData data: list)
            saveData(data);
    }

    @Override
    public int saveData(WeixinData data) {
        checkData(data);
        return weixinMapper.create(data);
    }



    @Override
    public WeixinData getData(int id){
        return weixinMapper.read(id);
    }
}
