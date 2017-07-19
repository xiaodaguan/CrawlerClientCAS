package common.pojos;

import common.system.Systemconfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by guanxiaoda on 2017/7/19.
 */
public class DataHelper {
    private static final Logger LOGGER = LoggerFactory.getLogger(DataHelper.class);

    public static List createDataList(int crawlerType) {
        switch (crawlerType) {
            case 1:
            case 2:
                return new ArrayList<NewsData>();
            case 3:
            case 4:
                return new ArrayList<BbsData>();
            case 5:
            case 6:
                return new ArrayList<BlogData>();
            case 7:
            case 8:
                return new ArrayList<WeiboData>();
            case 15:
            case 16:
                return new ArrayList<WeixinData>();


            default:
                LOGGER.error("未定义crawlerType:{} 对应的list类型。");
                return null;

        }
    }
}
