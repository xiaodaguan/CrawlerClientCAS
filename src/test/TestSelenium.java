package test;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.BrowserVersionFeatures;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.util.Cookie;

public class TestSelenium {

	public static void main(String[] args) {
		

		WebClient client = new WebClient(BrowserVersion.FIREFOX_38);
		client.addRequestHeader("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.10; rv:38.0) Gecko/20100101 Firefox/38.0");
		String[] cs = "SUB=_2A254cT13DeTxGedM6FUS-SfKzzuIHXVbmkM_rDV6PUJbvNBeLXDMkW2KNeYpCYyK9FV6qNRUvzqXdYXR5g..; SUBP=0033WrSXqPxfM725Ws9jqgMF55529P9D9WFDQIeIr.on1mTobDA9GY-E5JpX5K-t; SUHB=0YhQzJI8Yipc_f; SSOLoginState=1433750823; _T_WM=2a1a8d9f67e629f3b6268d3d2c5886a8; M_WEIBOCN_PARAMS=featurecode%3D20000181%26oid%3D3847887295439795%26luicode%3D20000061%26lfid%3D3847887295439795%26fid%3D1005052527981377%26uicode%3D10000011"
				.split(";");
		for (String c : cs) {
			String css[] = c.split("=");
			Cookie cookie = null;
			if (css.length == 1) {
				cookie = new Cookie(".weibo.cn", css[0], "");
			} else {
				cookie = new Cookie(".weibo.cn", css[0], css[1]);
			}
			client.getCookieManager().addCookie(cookie);
		}
		
		try {
			HtmlPage page = client.getPage("http://m.weibo.cn/page/tpl?containerid=1005052527981377_-_WEIBO_SECOND_PROFILE_WEIBO&itemid=&title=%E5%85%A8%E9%83%A8%E5%BE%AE%E5%8D%9A");
			page.save(new File("aaa.htm"));
			String cont=page.asText();
			System.out.println(page.getTitleText());
			System.out.println(cont);
			
			
		} catch (FailingHttpStatusCodeException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
