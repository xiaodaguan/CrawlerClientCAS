package common.util;

import java.io.UnsupportedEncodingException;

import org.apache.log4j.Logger;

/**
 *
 */
public class URLFilter {
    private Logger LOGGER = Logger.getLogger(URLFilter.class);
    private static BloomFilter<String> bloomFilter;

    /**
     * 过滤url容器初始化操作
     */
    public URLFilter() {
        double accpetErrRate = 0.000001;
        int elementCount = 800000000;
        LOGGER.info("初始化URL过滤容器…");
        //init
        bloomFilter = new BloomFilter<String>(accpetErrRate, elementCount);
        //todo
        LOGGER.info("初始化URL过滤容器成功完成！");

    }


    /**
     * 用于判断新加入数据表中的唯一标识（MD5）重复与否
     *
     * @param MD5 用于判断重复与否的参数
     * @return true：非重复MD5；false：重复MD5
     */
    public synchronized boolean checkNoRepeat(String MD5) {
        if (MD5 == null) return false;

        if (!bloomFilter.contains(MD5)) {
            bloomFilter.add(MD5);
            return true;
        }

        return false;
    }

    public BloomFilter<String> getBloomFilters() {
        return bloomFilter;
    }


}