package common.http.sub;

import com.machinepublishers.jbrowserdriver.JBrowserDriver;
import com.machinepublishers.jbrowserdriver.Settings;
import com.machinepublishers.jbrowserdriver.Timezone;
import com.sun.xml.internal.ws.handler.HandlerException;
import common.bean.HtmlInfo;
import common.http.SimpleHttpProcess;
import common.system.Systemconfig;
import common.util.SeleniumDriverManager;
import common.util.SeleniumRequest;
import common.util.TimeUtil;
import org.apache.hadoop.hbase.util.Bytes;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;

public class WeixinHttpProcess extends SimpleHttpProcess {


    JBrowserDriver driver = null;

    @Override
    public synchronized byte[] simpleGet(HtmlInfo html) throws Exception {

//        driver.reset();
        Systemconfig.sysLog.log("initializing selenium driver...");
            driver=new JBrowserDriver(Settings.builder().timezone(Timezone.AMERICA_NEWYORK).build());
        Systemconfig.sysLog.log("selenium driver ok.");
//        try {

        Systemconfig.sysLog.log("selenium driver requesting ... "+ html.getOrignUrl());
        try {
            driver.get(html.getOrignUrl());

            Systemconfig.sysLog.log("selenium driver request ok. "+ html.getOrignUrl());

            String htmlSource = driver.getPageSource();
            return htmlSource.getBytes();

        }catch(Exception e){
            Systemconfig.sysLog.log("selenium driver request failed. ");
            System.err.println("selenium driver request failed. ");
        }finally {
            driver.quit();
            Systemconfig.sysLog.log("selenium driver quited.");
            driver=null;
        }

        return null;

//        }catch(Exception e){
////            e.printStackTrace();
//            driver=null;
//            driver = new JBrowserDriver(Settings.builder().saveMedia(false).timezone(Timezone.ASIA_SHANGHAI).build());
//            driver.get(html.getOrignUrl());
//        }



//        TimeUtil.rest(5);//wait for num loading


//        driver.close();// no not use!

    }


}
