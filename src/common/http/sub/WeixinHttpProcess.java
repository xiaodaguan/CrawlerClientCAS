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


    private static JBrowserDriver driver = new JBrowserDriver(Settings.builder().connectTimeout(1000*120).timezone(Timezone.AMERICA_NEWYORK).build());


    private static int count = 0;
    private static int MAX_COUNT=10;
    @Override
    public synchronized byte[] simpleGet(HtmlInfo html) throws Exception {



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
//            driver.reset();
            System.out.println(count);
            if(count++ >= MAX_COUNT){
                Systemconfig.sysLog.log("selenium driver reboot... ");
                driver.quit();
                count = 0;
                Thread.sleep(1000*5);
                driver = new JBrowserDriver(Settings.builder().connectTimeout(1000*120).timezone(Timezone.AMERICA_NEWYORK).build());

                Systemconfig.sysLog.log("selenium driver reboot ok. ");

            }
        }

        return null;



    }


}
