package common.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by guanxiaoda on 16/8/29.
 */
public class SeleniumRequest {

    private static Runtime runtime = Runtime.getRuntime();

    public static String seleniumGetPageSource(String url) throws IOException {
        Process p = runtime.exec("phantomjs/osx/phantomjs phantomjs/osx/codes.js " + url);
        InputStream is = p.getInputStream();
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        StringBuffer sb = new StringBuffer();
        String line = "";
        while ((line = br.readLine()) != null) {
            sb.append(line).append("\n");
        }

        return sb.toString();
    }

    public static void main(String[] args) throws IOException {


        Runtime rt = Runtime.getRuntime();
        Process p = rt.exec("phantomjs/osx/phantomjs phantomjs/osx/codes.js http://www.sogou.com");
        InputStream is = p.getInputStream();
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        StringBuffer sb = new StringBuffer();
        String line = "";
        while ((line = br.readLine()) != null) {
            sb.append(line).append("\n");
        }

        System.out.println(sb.toString());
    }
}
