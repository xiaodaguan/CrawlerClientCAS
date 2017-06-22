package common.service;

import common.mapper.NewsMapper;
import common.mapper.WeixinMapper;
import common.pojos.NewsData;
import common.pojos.WeixinData;
import org.apache.commons.lang.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

/**
 * Created by guanxiaoda on 2017/6/16.
 */
public class NewsService extends BaseService<NewsData> {
    private static final Logger LOGGER = LoggerFactory.getLogger(NewsService.class);

    private NewsMapper newsMapper;


    public void setNewsMapper(NewsMapper newsMapper) {
        this.newsMapper = newsMapper;
    }

    @Override
    public void saveDatas(List<NewsData> list) {
        for (NewsData data : list)
            saveData(data);
    }

    @Override
    public int saveData(NewsData data) {
        checkData(data);
        return newsMapper.create(data);
    }



    @Override
    public NewsData getData(int id) {
        return newsMapper.read(id);
    }
}
