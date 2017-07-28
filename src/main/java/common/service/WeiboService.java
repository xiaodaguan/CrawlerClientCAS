package common.service;

import common.mapper.WeiboMapper;
import common.pojos.WeiboData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by rzy on 2017/7/27.
 */
public class WeiboService extends BaseService<WeiboData> {
    private static final Logger LOGGER = LoggerFactory.getLogger(WeiboService.class);

    private WeiboMapper weiboMapper;


    public void setWeiboMapper(WeiboMapper weiboMapper) {
        this.weiboMapper = weiboMapper;
    }

    @Override
    public void saveDatas(List<WeiboData> list) throws Exception {
        for (WeiboData data : list)
            saveData(data);
    }

    @Override
    public int saveData(WeiboData data) throws Exception {
        checkData(data);
        int result = 0;
        try {
            weiboMapper.create(data);
            LOGGER.info("保存完成.[{}]", data.getTitle());
        } catch (Exception e) {
            LOGGER.error("data 保存失败!");
            throw new RuntimeException(e);
        }

        return result;
    }


    @Override
    public WeiboData getData(int id) {
        return weiboMapper.read(id);
    }
}
