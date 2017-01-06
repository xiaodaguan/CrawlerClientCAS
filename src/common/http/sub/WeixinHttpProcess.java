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
        Process p = runtime.exec("phantomjs/drivers/osx/phantomjs phantomjs/get.js "+html.getOrignUrl());
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
