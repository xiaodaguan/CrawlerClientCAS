package test;

import java.util.logging.Level;

import org.apache.hadoop.hbase.filter.FamilyFilter;
import org.openqa.selenium.Cookie;


import com.machinepublishers.jbrowserdriver.JBrowserDriver;
import com.machinepublishers.jbrowserdriver.Settings;
import com.machinepublishers.jbrowserdriver.Timezone;
import com.machinepublishers.jbrowserdriver.UserAgent;
import com.machinepublishers.jbrowserdriver.UserAgent.Family;
import com.machinepublishers.jbrowserdriver.*;

/**
 * Created by guanxiaoda on 16/11/18.
 */
public class JBrowserDriverTest2 {



    public static void main(String[] args) {

    	
//    	FirefoxProfile profile = new FirefoxProfile();
//    		new profile
//    	profile.addAdditionalPreference("general.useragent.override","some UA string");
//
//    	WebDriver driver = new FirefoxDriver(profile);
    	
    	
    	Settings.Builder builder = Settings.builder();
    	builder.timezone(Timezone.ASIA_SHANGHAI);
    	//builder.userAgent(UserAgent.CHROME);
    	
    	
    	Family family = Family.WEBKIT;
    	//UserAgent.Family;
    	UserAgent ua = new UserAgent(family, null, "Windows NT 10.0; WOW64", null, "AppleWebKit/537.36 (KHTML, like Gecko)", 
    			"Safari/537.36");
    	builder.userAgent(ua);
    	JBrowserDriver jbd = new JBrowserDriver(builder.build());
    	//Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/538.19 (KHTML, like Gecko) JavaFX/8.0 Safari/538.19
    	//Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/538.19 (KHTML, like Gecko) JavaFX/8.0 Safari/538.19

    	
    	String url = "http://www.vegnet.com.cn/";
    	url = "http://www.baidu.com/";
    	
    	url = "file:///C:/Users/Administrator/Desktop/google/brow.html";
        jbd.get(url);
        
        
        //jbd.setLogLevel(Level.ALL);
        
        //jbd.setLogLevel(Level.INFO);
        
//        //System.out.println(jbd.getStatusCode());
//        do{
//        	try {
//				Thread.sleep(6000);
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
//        } while(jbd.getPageSource().contains("浏览器安全检查中..."));
// 
//        //System.out.println(jbd.getPageSource());
//        
//        StringBuffer cookieSb = new StringBuffer();
//        for (Cookie coo : jbd.manage().getCookies()) {
//        	if(cookieSb.length()!=0){
//        		cookieSb.append(";");
//        	}
//        	cookieSb.append(coo.getName()+"="+coo.getValue());
//			System.out.println(coo.getName()+"\t:\t"+coo.getValue());
//		}
//        
//        System.out.println(jbd.manage().);
        
        //System.out.println(jbd.getPageSource());
        System.out.println(jbd.findElementById("123").getText());
        
        System.out.println(jbd.findElementsByXPath("//p").get(0).getText());
        jbd.quit();
    }
}
