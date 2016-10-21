package common.http.sub;

import common.bean.HtmlInfo;
import common.http.SimpleHttpProcess;
import common.util.SeleniumDriverManager;
import common.util.SeleniumRequest;
import common.util.TimeUtil;
import org.apache.hadoop.hbase.util.Bytes;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;

public class WeixinHttpProcess extends SimpleHttpProcess {


    private static WebDriver driver = null;

    @Override
    public synchronized byte[] simpleGet(HtmlInfo html) throws Exception {

            if (driver == null) {
                driver = SeleniumDriverManager.getInstance().getPhantomJSDriver();
            }

        /**
         * call js
         */
//		String pageSource = SeleniumRequest.seleniumGetPageSource(html.getOrignUrl());
//
//        return pageSource.getBytes();

        /**
         * use selenium java api
         */

        driver.get(html.getOrignUrl());
        TimeUtil.rest(5);//wait for num loading
        return driver.getPageSource().getBytes();
    }


}
