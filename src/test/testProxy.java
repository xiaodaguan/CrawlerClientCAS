package test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;

public class testProxy {

	public static void main(String[] args) throws InterruptedException {
		checkIP();
		String ip0 = "172.18.79.1";
		String ip1 = "172.18.79.31";
		String ip2 = "172.18.79.32";
		String ip4 = "172.18.79.34";
		String ip5 = "172.18.79.35";
		String ip6 = "172.18.79.36";
		checkIP(ip0, 8888);
		checkIP(ip1, 8888);
		checkIP(ip2, 8888);
		checkIP(ip4, 8888);
		checkIP(ip5, 8888);
		checkIP(ip6, 8888);
	}

	public static void checkIP(String addr, int port) throws InterruptedException {
		Thread.sleep(500);
		System.out.println("内网ip: " + addr);

		long start = System.currentTimeMillis();
		long end = 0;

		Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(addr, port));
		try {
			URL url = new URL("http://www.baidu.com/s?ie=utf-8&f=8&rsv_bp=0&rsv_idx=1&tn=baidu&wd=我的ip");
			URLConnection con = url.openConnection(proxy);
			con.setConnectTimeout(5000);
			con.connect();

			BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String line = null;
			StringBuilder sBuilder = new StringBuilder();
			while ((line = reader.readLine()) != null) {
				if (line.contains("属于"))
					sBuilder.append(line).append("\r\n");
			}
			reader.close();
			end = System.currentTimeMillis();
			System.out.println("耗时：" + (end - start) + "ms");
			System.out.println(sBuilder.toString());

		} catch (MalformedURLException e) {
			System.err.println(e.getMessage());
			// e.printStackTrace();
		} catch (IOException e) {
			System.err.println(e.getMessage());
			// e.printStackTrace();
		}
	}

	public static void checkIP() throws InterruptedException {
		Thread.sleep(500);
		System.out.println("不使用代理");

		long start = System.currentTimeMillis();
		long end = 0;
		try {
			URL url = new URL("http://www.baidu.com/s?ie=utf-8&f=8&rsv_bp=0&rsv_idx=1&tn=baidu&wd=我的ip");
			URLConnection con = url.openConnection();
			con.setConnectTimeout(5000);
			con.connect();

			BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String line = null;
			StringBuilder sBuilder = new StringBuilder();
			while ((line = reader.readLine()) != null) {
				if (line.contains("属于"))
					sBuilder.append(line).append("\r\n");
			}
			reader.close();
			end = System.currentTimeMillis();
			System.out.println("耗时：" + (end - start) + "ms");
			System.out.println(sBuilder.toString());

		} catch (MalformedURLException e) {
			System.err.println(e.getMessage());
			// e.printStackTrace();
		} catch (IOException e) {
			System.err.println(e.getMessage());
			// e.printStackTrace();
		}
	}

}
