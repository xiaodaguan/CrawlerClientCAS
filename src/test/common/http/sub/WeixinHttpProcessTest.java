package common.http.sub;

import common.bean.HtmlInfo;
import common.http.SimpleHttpProcess;
import common.siteinfo.Siteinfo;
import common.system.AppContext;
import common.system.Systemconfig;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;

/**
 * Created by guanxiaoda on 2017/6/20.
 */
public class WeixinHttpProcessTest {

    @BeforeClass
    public static void beforeAll(){
        Systemconfig.crawlerType = 15;
        AppContext.initAppCtx("");//初始化
    }


    @Test
    public void getTest() throws Exception {

        HtmlInfo htmlInfo =  new HtmlInfo();


        htmlInfo.setType("META" + File.separator + "weixin_search_weixin");
        htmlInfo.setMaxRetryTimes(10);

        htmlInfo.setEncode("gb2312");
        htmlInfo.setAgent(true);
        htmlInfo.setOrignUrl("http://1212.ip138.com/ic.asp");
        SimpleHttpProcess httpProcess = new SimpleHttpProcess();

        for(int i=0;i<100;i++) {
            httpProcess.getContent(htmlInfo);

            System.out.println(htmlInfo.getContent());
        }

    }
}
