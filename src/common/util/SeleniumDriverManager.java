package common.util;

/**
 * Created by guanxiaoda on 16/8/10.
 */

import com.machinepublishers.jbrowserdriver.JBrowserDriver;
import com.machinepublishers.jbrowserdriver.Settings;
import com.machinepublishers.jbrowserdriver.Timezone;
import org.openqa.selenium.phantomjs.PhantomJSDriver;

import java.awt.*;

/**
 * Created by guanxiaoda on 6/7/16.
 * singleton
 */
public class SeleniumDriverManager {

    private static SeleniumDriverManager webDriver = new SeleniumDriverManager();


    private SeleniumDriverManager() {
    }

    public JBrowserDriver getPhantomJSDriver() throws Exception {


        JBrowserDriver driver = new JBrowserDriver(Settings.builder().timezone(Timezone.ASIA_SHANGHAI).build());
        return driver;

    }


    public static SeleniumDriverManager getInstance() {
        return webDriver;
    }
}