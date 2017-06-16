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


    static{
        LOGGER.debug("weixin service ........................");
    }

    public void setWeixinMapper(WeixinMapper weixinMapper) {
        this.weixinMapper = weixinMapper;
    }

    @Override
    public void saveDatas(List<WeixinData> list) {

    }

    @Override
    public void saveData(WeixinData data) {

    }

    @Override
    public WeixinData read(int id){
        return weixinMapper.read(id);
    }
}
