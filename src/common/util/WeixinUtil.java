package common.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.script.Bindings;
import javax.script.Invocable;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.log4j.PropertyConfigurator;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import common.bean.AccessKey;
import common.bean.HtmlInfo;
import common.http.SimpleHttpProcess;
import common.system.Systemconfig;

public class WeixinUtil {

	public static String SogouAES(String openid, String k, String v) {
		String result = "";

		/* java 直接调用js */
		ScriptEngine engine = new ScriptEngineManager()
				.getEngineByName("javascript");
		Bindings bind = engine.createBindings();
		bind.put("factor", 2); // 这里绑定一个factor的值为2.
		engine.setBindings(bind, ScriptContext.ENGINE_SCOPE);

		try {
			engine.eval(new FileReader("config/aes.js"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (ScriptException e) {
			e.printStackTrace();
		}
		if (engine instanceof Invocable) {
			Invocable in = (Invocable) engine;

			try {
				result = (String) in.invokeFunction("cal", openid, k, v);
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			} catch (ScriptException e) {
				e.printStackTrace();
			}
		}
		return result;

	}

	public static String getCookies() throws IOException {

		String cookies = "";
		HttpClient client = new HttpClient();

		String url = "http://weixin.sogou.com/";

		GetMethod get = new GetMethod(url);
		get.setRequestHeader(
				"User-Agent",
				"Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2272.101 Safari/537.36");
		int statusCode = -1;
		try {
			statusCode = client.executeMethod(get);
		} catch (IOException e) {
			e.printStackTrace();
		}

		InputStream in = null;
		try {
			in = get.getResponseBodyAsStream();
		} catch (IOException e) {
			e.printStackTrace();
		}

		Header[] responseHeaders = get.getResponseHeaders("Set-Cookie");

		for (int i = 0; i < responseHeaders.length; i++) {
			Header header = responseHeaders[i];
			if (header.getName().equals("Set-Cookie")) {
				cookies += header.getValue().substring(0,
						header.getValue().indexOf(";"))
						+ ";";

			}
		}
		Date date = new Date();
		cookies += "SUV=" + (date.getTime()) * 1000
				+ Math.round(Math.random() * 1000)
				+ ";SNUID=970D5F22BDB9A8D3E101B069BD653834";
		return cookies;
	}

//	public static void main(String[] args) {
//
//		String path = "";
//		PropertyConfigurator.configure(path + "./config/log4j.properties");
//		File[] files = new File(path + "config").listFiles();
//		ArrayList<String> list = new ArrayList<String>();
//		for (File file : files) {
//			if (file.getName().startsWith("app")) {
//				list.add(path + "config" + File.separator + file.getName());
//			}
//		}
//		String[] arry = new String[list.size()];
//		list.toArray(arry);
//		new FileSystemXmlApplicationContext(arry);
//		System.out
//				.println(getReadNum("http://mp.weixin.qq.com/s?__biz=MzAwNDI0MDM2NQ==&mid=202640010&idx=2&sn=0d0c4b4fd2557637f30a20e0f8cf6d36&3rd=MzA3MDU4NTYzMw==&scene=6#rd"));
//	}

	/**
	 * 获取微信阅读数和点赞数
	 * 
	 * @param paperUrl
	 *            文章url
	 * @return 阅读数+赞数，格式："7:1","-1:-1"可能是key失效，"-2:-2"可能是抽取错误
	 */
//	public static String getReadNum(String paperUrl) {
//
//		TimeUtil.rest(5);// 等待5秒再请求
//		AccessKey ak = Systemconfig.dbService.accessKey();
//		int retryCount = 0;
//		while (ak == null) {
//			System.out.println("暂时没有key，等待60秒重试..." + retryCount++);
//			TimeUtil.rest(60);
//			ak = Systemconfig.dbService.accessKey();
//		}
//
//		String url = paperUrl.replace("/s?", "/mp/getappmsgext?").replace(
//				"/mp/appmsg/show?", "/mp/getappmsgext?");// 阅读/赞数json文件url格式
//		// 添加参数
//		if (ak != null) {
//			url += "&key=" + ak.getKey() + "&uin=" + ak.getUin()
//					+ "&pass_ticket=" + ak.getPassTicket();
//		}
//
//		HtmlInfo html = new HtmlInfo();
//
//		String charSet = "UTF-8";
//		html.setType("DATA");
//		html.setEncode(charSet);
//		html.setOrignUrl(url);
//		html.setCookie("Set-Cookie: wxuin=20156425; Path=/; Expires=Fri, 02-Jan-1970 00:00:00 GMT");
//		html.setUa("Mozilla/5.0 (iPhone; CPU iPhone OS 8_0 like Mac OS X) AppleWebKit/600.1.3 (KHTML, like Gecko) Version/8.0 Mobile/12A4345d Safari/600.1.4");
//		SimpleHttpProcess shp = new SimpleHttpProcess();
//		shp.getContent(html);
//		String content = html.getContent();
//
//		String readNumStr = StringUtil
//				.regMatcher(content, "\"read_num\":", ",");
//		String praiseNumStr = StringUtil.regMatcher(content, "\"like_num\":",
//				",");
//		int readNum = -1;
//		int likeNum = -1;
//		try {
//			if (readNumStr != null)
//				readNum = Integer.parseInt(readNumStr);
//
//			if (praiseNumStr != null)
//				likeNum = Integer.parseInt(praiseNumStr);
//
//		} catch (Exception e) {
//			e.printStackTrace();
//			return "-2:-2";
//		}
//		// if (readNum == -1 && likeNum == -1)
//		// try {
//		// Systemconfig.dbService.DumpAccessKey();
//		// } catch (Exception e) {
//		// // TODO: handle exception
//		// return "-3:-3";
//		// }
//
//		return readNum + ":" + likeNum;
//	}

	/**
	 * 从文件获取参数:key+uin+pass_ticket
	 * 
	 * @param path
	 * @return
	 */
	public static String getKeyFromHtml(String path) {
		// 来自网页
		File[] files = new File(path).listFiles();
		for (File file : files) {
			if (file.getName().contains(".htm")) {
				System.out.println(file.getAbsolutePath());
				String content = StringUtil.getContent(file.getAbsolutePath());

				String uin = StringUtil.regMatcher(content, "var uin = \"",
						"\";").trim();
				String key = StringUtil.regMatcher(content, "var key = \"",
						"\";").trim();
				String pass_ticket = StringUtil.regMatcher(content,
						"var pass_ticket = \"", "\";").trim();
				// System.out.println(content);
				String params = "&uin=" + uin + "&key=" + key + "&pass_ticket="
						+ pass_ticket;
				return params;
			}

		}

		return null;
	}
}
