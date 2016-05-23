package common.download.weibo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.CountDownLatch;

import common.bean.WeiboData;
import common.download.GenericDataCommonDownload;
import common.rmi.packet.SearchKey;
import common.system.UserAttr;
import common.util.TimeUtil;

public class WeiboAddressDownload extends GenericDataCommonDownload<WeiboData> {

	private UserAttr user;

	public WeiboAddressDownload(String siteFlag, WeiboData vd, CountDownLatch endCount, UserAttr user, SearchKey key) {
		super(siteFlag, vd, endCount, key);
		this.user = user;
	}

	@Override
	public void process() {

		String url = siteFlag;

		URLConnection conn;
		try {
			conn = new URL(url).openConnection();
			conn.addRequestProperty("Cookie", user.getCookie());
			conn.addRequestProperty("User-Agent", user.getUserAgent());

			conn.connect();

			BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String line = null;
			while ((line = reader.readLine()) != null) {
				if (line.contains("$CONFIG['onick']='")) {
					String address = line.replace("$CONFIG['onick']='", "").replace("';", "");

					data.setContentAddress(address);
					break;
				}
			}
			reader.close();

			// StringUtil.writeFile("tmp.htm", content.getBytes());
			TimeUtil.rest(1);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
