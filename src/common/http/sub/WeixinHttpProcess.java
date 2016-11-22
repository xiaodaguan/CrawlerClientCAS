package common.http.sub;

import com.machinepublishers.jbrowserdriver.JBrowserDriver;
import com.machinepublishers.jbrowserdriver.Settings;
import com.machinepublishers.jbrowserdriver.Timezone;
import common.bean.HtmlInfo;
import common.http.SimpleHttpProcess;
import common.util.TimeUtil;
import org.openqa.selenium.WebDriver;

public class WeixinHttpProcess extends SimpleHttpProcess {




    @Override
    public byte[] simpleGet(HtmlInfo html) throws Exception {
        JBrowserDriver driver = new JBrowserDriver(Settings.builder().timezone(Timezone.ASIA_SHANGHAI).build());
        driver.get(html.getOrignUrl());
        String htmlSource = driver.getPageSource();
        byte[] bytes = htmlSource.getBytes();

        driver.quit();

        return bytes;

    }


}
