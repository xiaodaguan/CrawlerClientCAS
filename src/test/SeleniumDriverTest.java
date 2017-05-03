package test;

import java.util.logging.Level;

import org.apache.hadoop.hbase.filter.FamilyFilter;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.Platform;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import com.machinepublishers.jbrowserdriver.JBrowserDriver;
import com.machinepublishers.jbrowserdriver.Settings;
import com.machinepublishers.jbrowserdriver.Timezone;
import com.machinepublishers.jbrowserdriver.UserAgent;
import com.machinepublishers.jbrowserdriver.UserAgent.Family;
import com.machinepublishers.jbrowserdriver.*;

/**
 * Created by guanxiaoda on 16/11/18.
 */
public class SeleniumDriverTest {



    public static void main(String[] args) {

    	DesiredCapabilities    caps = new DesiredCapabilities();      
    	caps.setJavascriptEnabled(true);          
    	caps.setCapability(PhantomJSDriverService.PHANTOMJS_PAGE_SETTINGS_PREFIX+ "userAgent", 
    			"Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/45.0.2454.101 Safari/537.36");
       
    	WebDriver jbd = new JBrowserDriver(caps);

    	
    	String url = "http://www.vegnet.com.cn/";
    	url = "http://www.baidu.com/";

    	url = "https://httpbin.org/get?show_env=1";
        jbd.get(url);
        //System.out.println(jbd.findElementsByXPath("//p").get(0).getText());
        //System.out.println(jbd.findElement(By.xpath("//p")).getText());
        System.out.println(jbd.getPageSource());
        jbd.quit();
        //jbd.close();;
    }
}
