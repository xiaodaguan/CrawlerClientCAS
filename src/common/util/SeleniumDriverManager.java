package common.util;

/**
 * Created by guanxiaoda on 16/8/10.
 */

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

    public PhantomJSDriver getPhantomJSDriver() throws Exception {
        String os = System.getProperty("os.name").toLowerCase();//linux, windows xx, mac os x

        if (os.contains("mac")) System.setProperty("phantomjs.binary.path", "phantomjs/osx/phantomjs");
        else if (os.contains("windows")) System.setProperty("phantomjs.binary.path", "phantomjs/win/phantomjs.exe");
        else if (os.contains("linux")) {
            String version = System.getProperty("os.version").toLowerCase();
            if (version.contains("x86_64")) System.setProperty("phantomjs.binary.path", "phantomjs/linux/64/phantomjs");
            else System.setProperty("phantomjs.binary.path", "phantomjs/linux/32/phantomjs");
        }


        PhantomJSDriver driver = new PhantomJSDriver();
        return driver;
//        DesiredCapabilities caps = new DesiredCapabilities();
//        caps.setCapability(PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY, "phantomjs/osx/phantomjs");
//        PhantomJSDriver driver = null;
//        try {
//            driver = new PhantomJSDriver(caps);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        driver.manage().window().setSize(new Dimension(1920, 1080));
//        return driver;
    }


    public static SeleniumDriverManager getInstance() {
        return webDriver;
    }
}