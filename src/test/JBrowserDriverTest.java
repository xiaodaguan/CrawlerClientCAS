package test;

import com.machinepublishers.jbrowserdriver.JBrowserDriver;
import com.machinepublishers.jbrowserdriver.Settings;
import com.machinepublishers.jbrowserdriver.Timezone;

/**
 * Created by guanxiaoda on 16/11/18.
 */
public class JBrowserDriverTest {


    private static JBrowserDriver jbd = new JBrowserDriver(Settings.builder().timezone(Timezone.ASIA_SHANGHAI).build());
    
    
	public static void main(String[] args) {

       
        
        String url = "file:///C:/Users/Administrator/Desktop/google"
        		+ "/Google%20map.html?lat=39.9087677478&log=116.3975780499";
        
        url = "file:///C:/Users/Administrator/Desktop/google/googleMap.html?lat=39.9087677478&log=116.3975780499";
        jbd.get(url);
        
        System.out.println(jbd.getStatusCode());
        
        System.out.println(jbd.getStatusCode());
        System.out.println(jbd);
        System.out.println(jbd.getPageSource());
        
        

        
        
      
        
        url = "file:///C:/Users/Administrator/Desktop/google/googleMap.html?"
        		+ "lat=36.9087677478&log=116.3975780499";
        jbd.get(url);
        
        System.out.println(jbd.getStatusCode());
        
        System.out.println(jbd.getStatusCode());
        System.out.println(jbd);
        System.out.println(jbd.getPageSource());
        
        

        
        url = "file:///C:/Users/Administrator/Desktop/google/googleMap.html?"
        		+ "lat=36.9087677478&log=116.3975780499";
        jbd.get(url);
        
        System.out.println(jbd.getStatusCode());
        
        System.out.println(jbd.getStatusCode());
        System.out.println(jbd);
        System.out.println(jbd.getPageSource());
        
        
        url = "file:///C:/Users/Administrator/Desktop/google/googleMap.html?"
        		+ "lat=31.9087677478&log=116.3975780499";
        jbd.get(url);
        
        System.out.println(jbd.getStatusCode());
        
        System.out.println(jbd.getStatusCode());
        System.out.println(jbd);
        System.out.println(jbd.getPageSource());
       
        
        System.out.println("hello world");
        jbd.quit();
    }
    public static void a1(String[] args) {

        JBrowserDriver jbd = new JBrowserDriver(Settings.builder().timezone(Timezone.ASIA_SHANGHAI).build());

        jbd.get("http://www.vegnet.com.cn/");
        
        System.out.println(jbd.getStatusCode());
        
        
        do{
        	try {
				Thread.sleep(6000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
        } while(jbd.getPageSource().contains("浏览器安全检查中..."));
        
        System.out.println(jbd.getStatusCode());
        
        
        System.out.println(jbd);
        
        System.out.println(jbd.getPageSource());
        
        
        jbd.quit();
    }
}
