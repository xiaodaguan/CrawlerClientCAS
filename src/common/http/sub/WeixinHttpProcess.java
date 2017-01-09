package common.http.sub;

import common.bean.HtmlInfo;
import common.http.SimpleHttpProcess;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class WeixinHttpProcess extends SimpleHttpProcess {



    @Override
    public synchronized byte[] simpleGet(HtmlInfo html) throws Exception {

        Runtime runtime = Runtime.getRuntime();
        Process p = null;


        String os = System.getProperty("os.name").toLowerCase();//linux, windows xx, mac os x


        if (os.contains("mac")) runtime.exec("phantomjs/drivers/osx/phantomjs phantomjs/get.js "+html.getOrignUrl());
        else if (os.contains("windows"))
            runtime.exec("phantomjs/drivers/win/phantomjs.exe phantomjs/get.js "+html.getOrignUrl());
        else if (os.contains("linux")) {
            String version = System.getProperty("os.arch").toLowerCase();
            if (version.contains("64"))
                runtime.exec("phantomjs/drivers/linux/64/phantomjs phantomjs/get.js "+html.getOrignUrl());
            else
                runtime.exec("phantomjs/drivers/linux/32/phantomjs phantomjs/get.js "+html.getOrignUrl());
        }



        InputStream is = p.getInputStream();
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String line = null;
        StringBuilder sb = new StringBuilder();
        while(( line = br.readLine())!=null){
            sb.append(line).append("\n");
        }
        String pageSource = sb.toString();

        return pageSource.getBytes();
    }


}
