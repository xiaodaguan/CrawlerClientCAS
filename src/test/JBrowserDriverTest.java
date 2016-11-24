package test;

import org.openqa.selenium.WebDriver;
import com.machinepublishers.jbrowserdriver.Timezone;
import com.machinepublishers.jbrowserdriver.JBrowserDriver;
import com.machinepublishers.jbrowserdriver.Settings;

/**
 * Created by guanxiaoda on 16/11/18.
 */
public class JBrowserDriverTest {



    public static void main(String[] args) {

        JBrowserDriver jbd = new JBrowserDriver(Settings.builder().timezone(Timezone.ASIA_SHANGHAI).build());

        jbd.get("https://www.sogou.com/");

        System.out.println(jbd.getStatusCode());
        System.out.println(jbd.getPageSource());
    }
}
