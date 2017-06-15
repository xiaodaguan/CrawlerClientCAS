package common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * 时间处理帮助类
 *
 * @author grs
 * @since 2011年7月
 */
public class TimeUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(TimeUtil.class);

    public static void main(String[] args) {
        LOGGER.info("", str2Timestamp("2016-3-16", "yyyy-MM-dd") / 1000);
    }

    public static String timestamp2Str(long mill, String format) {
        Date date = new Date(mill);
        String strs = "";
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            strs = sdf.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return strs;
    }

    public static long str2Timestamp(String datetime, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        Date date = null;
        try {
            date = sdf.parse(datetime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date.getTime();
    }

    public static long changeTimestampAcc(long mill, String desFormat) {
        String parse = timestamp2Str(mill, desFormat);
        long result = str2Timestamp(parse, desFormat);
        return result;
    }

    /**
     * 休息间隔
     */
    public static void rest(int num) {
        try {
            TimeUnit.SECONDS.sleep(num);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

}
