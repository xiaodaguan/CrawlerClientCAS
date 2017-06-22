package common.http.sub;

import common.bean.HtmlInfo;
import common.http.SimpleHttpProcess;
import common.siteinfo.Siteinfo;
import common.system.AppContext;
import common.system.Systemconfig;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;

/**
 * Created by guanxiaoda on 2017/6/20.
 */
public class WeixinHttpProcessTest {

    @BeforeClass
    public static void beforeAll() {
//        Systemconfig.crawlerType = 15;
//        AppContext.initAppCtx("");//初始化
        Systemconfig.sysLog.start();
    }


    @Test
    public void getTest() throws Exception {

        HtmlInfo htmlInfo = new HtmlInfo();


        htmlInfo.setType("META" + File.separator + "weixin_search_weixin");
        htmlInfo.setMaxRetryTimes(10);

        htmlInfo.setEncode("gb2312");
        htmlInfo.setAgent(true);
        htmlInfo.setOrignUrl("http://weixin.sogou.com/weixin?type=2&query=%E9%9D%92%E5%B2%9B%E4%BA%A4%E9%80%9A");
        SimpleHttpProcess httpProcess = new SimpleHttpProcess();

//        htmlInfo.setCookie("ABTEST=4|1498036244|v1; IPLOC=CN3401; SUID=8229BF3D1F2D940A00000000594A3814; PHPSESSID=3era4tc65c37o7bb8ns1vfisb0; SUIR=1498036244; SUV=00CA47B13DBF2982594A381475F8C366; SUID=8229BF3D3108990A00000000594A3816; SNUID=2C861193AEAAFCDB6F004F62AFAA4585; seccodeRight=success; successCount=1|Wed, 21 Jun 2017 09:16:26 GMT; refresh=1");
        int right = 0;
        int block = 0;
        for (int i = 0; i < 100; i++) {
            System.out.println(i+"\t");
            httpProcess.getContent(htmlInfo);
//            if (htmlInfo.getContent().contains("搜狗微信搜索_订阅号及文章内容独家收录，一搜即达")) {
//                right++;
//                System.out.println("yeah!");
//            }
            if (htmlInfo.getContent().contains("您的访问过于频繁")) {
                block++;
                System.err.println("fuck...");
            }else if(htmlInfo.getContent().contains("相关微信公众号文章")){
                right++;
                System.out.println("yeah!!!");
            }

            Thread.sleep(1000*3);
        }
        System.out.println("right:"+right);
        System.out.println("block:"+block);
    }

    @Test
    public void testGetCookie() {
        SimpleHttpProcess httpProcess = new SimpleHttpProcess();
    }
}
