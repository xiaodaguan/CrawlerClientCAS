package test;


import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;

import java.io.IOException;

public class test {


    public static void main(String[] args) throws IOException {

        HttpClient client = new HttpClient();
        HttpMethod method = new GetMethod("http://s.weibo.com/weibo/%25E7%25BD%2591%25E7%25BA%25A2%25E5%2588%25B6%25E9%2580%25A0%25E6%25B5%2581%25E6%25B0%25B4%25E7%25BA%25BF?topnav=1&wvr=6&Refer=top_button");
        method.addRequestHeader("Cookie", "SINAGLOBAL=6544355924316.716.1469421115980; un=13569438457; wvr=6; SCF=AlUai2wQnigMTaS624KqoImABidEP9TCyRhgztGD_GClOhEFG8JmuafNwCH0vNhGQxsCx5oAyTCVo2HTRCu_L0o.; SUB=_2A256kq4LDeTxGeNH4lET8ynOwj-IHXVZ6ZjDrDV8PUNbmtANLW_tkW8KZLV-ZQ0Eiqdsp4XhHpzvRuABVw..; SUBP=0033WrSXqPxfM725Ws9jqgMF55529P9D9W5Zfkn6abD1AvZmT.uSWPBT5JpX5KMhUgL.Fo-41KeEe0ME1Ke2dJLoI0YLxKBLBonL1h5LxKML1-zLBoBLxK-L12BL1K-LxKnLB.2LB-zLxKML1-2L1hBLxKqLBoBL12zLxKnL1K2LBo2t; SUHB=0P18ldBnqY8OlI; ALF=1501041113; SSOLoginState=1469505115; _s_tentry=login.sina.com.cn; Apache=1709943026642.8418.1469505119410; ULV=1469505119431:3:3:3:1709943026642.8418.1469505119410:1469501179585; SWB=usrmdinst_3; UOR=,,login.sina.com.cn; WBStore=8ca40a3ef06ad7b2|undefined");

        while(true) {

            client.executeMethod(method);

            String body = method.getResponseBodyAsString();
            System.out.println(body);
            System.out.println("===");
            System.out.println("===");
            System.out.println("===");
            System.out.println("===");
            System.out.println("===");
            System.out.println("===");
            System.out.println("===");
        }
    }
}
