package test;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import org.mozilla.intl.chardet.HtmlCharsetDetector;
import org.mozilla.intl.chardet.nsDetector;
import org.mozilla.intl.chardet.nsICharsetDetectionObserver;
import org.mozilla.intl.chardet.nsPSMDetector;

import common.util.CharsetDetector;

public class testDetect {

	public static void main(String[] argv) throws IOException {
		// Initalize the nsDetector() ;
//		int lang = (argv.length == 2) ? Integer.parseInt(argv[1]) : nsPSMDetector.ALL;
//		nsDetector det = new nsDetector(lang);
//
//		// Set an observer...
//		// The Notify() will be called when a matching charset is found.
//
//		det.Init(new nsICharsetDetectionObserver() {
//			public void Notify(String charset) {
//				HtmlCharsetDetector.found = true;
//				System.out.println("CHARSET = " + charset);
//			}
//		});
//
//		URL url = new URL("http://news.163.com/15/0429/14/AOCISPT900014AED.html");
//		URLConnection conn = url.openConnection();
//		conn.setRequestProperty("Accept-Encoding", "gzip, deflate");
//		conn.connect();
//
//		BufferedInputStream imp = new BufferedInputStream(conn.getInputStream());
		CharsetDetector charDect = new CharsetDetector();
        URL url = new URL("http://news.163.com/15/0429/14/AOCISPT900014AED.html");
        URLConnection conn = url.openConnection();
		conn.setRequestProperty("Accept-Encoding", "gzip, deflate");
		conn.connect();

		BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(),"windows-1252"));
        
		String lineString=null;
		
		while((lineString=reader.readLine())!=null){
			System.out.println(lineString);
		}
		
//        String[] probableSet = charDect.detectChineseCharset(imp);
//        for (String charset : probableSet)
//        {
//            System.out.println(charset);
//        }
        
        
	}

}
