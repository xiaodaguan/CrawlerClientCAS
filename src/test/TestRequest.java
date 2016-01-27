package test;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import common.util.StringUtil;

public class TestRequest {

	public static void main(String[] args) throws MalformedURLException, IOException {
		// TODO Auto-generated method stub
		
		URLConnection conn=new URL("http://weixin.sogou.com/weixin?query=%E9%9D%92%E5%B2%9B%20%E4%BA%A4%E9%80%9A%E8%BF%90%E8%BE%93%E5%A7%94%20%E7%8E%8B%E5%8B%87&type=2").openConnection();
		conn.connect();
		
		String content=StringUtil.readStream(conn.getInputStream());
		
		System.out.println(content);
		StringUtil.writeFile("temp.htm", content);

	}

}
