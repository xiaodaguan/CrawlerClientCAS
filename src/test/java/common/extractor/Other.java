package common.extractor;

import common.extractor.xpath.SimpleXpathExtractor;
import common.extractor.xpath.weibo.search.sub.SinaExtractor;
import org.junit.Test;

import java.util.Calendar;
import java.util.Date;

public class Other {

    @Test
    public void time(){
        SinaExtractor extractor = new SinaExtractor();
        Date data = extractor.timeProcess("今天14:13");
        Calendar c = Calendar.getInstance();
        System.out.println(data.toLocaleString());
    }
}
