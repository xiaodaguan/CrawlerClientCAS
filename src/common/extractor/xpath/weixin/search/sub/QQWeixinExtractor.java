package common.extractor.xpath.weixin.search.sub;

import common.extractor.xpath.weixin.search.WeixinSearchXpathExtractor;
import common.util.StringUtil;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by guanxiaoda on 16/9/6.
 */
public class QQWeixinExtractor extends WeixinSearchXpathExtractor{

    @Override
    public Date timeProcess(String s) {
        Date d = super.timeProcess(s);
        if (d == null) {
            Calendar c = Calendar.getInstance();
            if (s.indexOf("月") > -1 || s.indexOf("日") > -1) {
                if (s.indexOf("年") == -1) {
                    s = c.get(Calendar.YEAR) + "-" + s;
                }
                s = s.replace("年", "-").replace("月", "-").replace("日", "");
                d = super.timeProcess(s);
            }
            if (d == null) {

                int num = Integer.parseInt(StringUtil.extractMulti(s, "\\d"));
                if (s.contains("minute") || s.contains("分钟前")) {
                    c.set(Calendar.MINUTE, c.get(Calendar.MINUTE) - num);
                } else if (s.contains("hour") || s.contains("小时前")) {
                    c.set(Calendar.HOUR, c.get(Calendar.HOUR) - num);
                } else if (s.contains("今天")) {
                    s = s.replace("今天", c.get(Calendar.YEAR) + "-" + c.get(Calendar.MONTH) + "-" + c.get(Calendar.DATE) + "");
                    d = super.timeProcess(s);
                    return d;
                } else if (s.contains("day") || s.contains("天前")) {
                    c.set(Calendar.DAY_OF_MONTH, c.get(Calendar.DAY_OF_MONTH) - num);
                } else if (s.contains("month") || s.contains("月前")) {
                    c.set(Calendar.MONTH, c.get(Calendar.MONTH) - num);
                } else if (s.contains("year") || s.contains("年前")) {
                    c.set(Calendar.YEAR, c.get(Calendar.YEAR) - num);
                } else if (s.contains("second") || s.contains("秒前")) {
                    c.set(Calendar.SECOND, c.get(Calendar.SECOND) - num);
                }
                return c.getTime();
            }
        }
        return d;
    }
}
