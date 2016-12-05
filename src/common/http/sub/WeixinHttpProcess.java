package common.http.sub;

import com.machinepublishers.jbrowserdriver.JBrowserDriver;
import com.machinepublishers.jbrowserdriver.Settings;
import com.machinepublishers.jbrowserdriver.Timezone;
import common.bean.HtmlInfo;
import common.http.SimpleHttpProcess;
import common.util.SeleniumDriverManager;
import common.util.SeleniumRequest;
import common.util.TimeUtil;
import org.apache.hadoop.hbase.util.Bytes;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;

public class WeixinHttpProcess extends SimpleHttpProcess {


    JBrowserDriver driver = new JBrowserDriver(Settings.builder().timezone(Timezone.ASIA_SHANGHAI).build());

    @Override
    public byte[] simpleGet(HtmlInfo html) throws Exception {

        driver.get(html.getOrignUrl());
        String htmlSource = driver.getPageSource();



        driver.get(html.getOrignUrl());
        TimeUtil.rest(5);//wait for num loading
        return driver.getPageSource().getBytes();
    }


}
