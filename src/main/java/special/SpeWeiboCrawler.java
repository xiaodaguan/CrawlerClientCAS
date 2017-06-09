package special;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.xml.transform.TransformerException;

import common.system.UserAttribute;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.xpath.XPathAPI;
import org.slf4j.Logger;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import common.bean.WeiboData;
import common.http.sub.SinaHttpProcess;
import common.util.DOMUtil;
import common.util.MD5Util;
import common.util.StringUtil;
import common.util.TimeUtil;

public class SpeWeiboCrawler
{
	private static Logger logger = org.slf4j.LoggerFactory.getLogger(SpeWeiboCrawler.class);
	/* 手动配置 */
	private static int sleepSecond = 25;// 爬取两条间隔之间的等待时间，如需采集很多页，建议20秒以上
	private static int maxCommentPage = 99999;// 最大采集的页数，不想限制则设置为9999999即可
	// private static int maxCommentPage = 99999;// 最大采集的页数，不想限制则设置为9999999即可

	private static String url = "jdbc:mysql://172.18.79.3:3306/weibo?characterEncoding=UTF-8";// mysql地址
	private static String user = "root";// mysql 用户名
	private static String password = "root";// muysql 密码

	/* 无需更改 */
	private static String driver = "com.mysql.jdbc.Driver";
	private static String cookie;// 会自动获取
	private static int resume = 421;// 只有断点采集需更改此属性，将跳过次数字之前的页码，从这个页码开始。

	public static void main(String[] args) throws SAXException, IOException
	{

		File path = new File("result");
		if (!path.exists())
			path.mkdirs();
		String config = StringUtil.getContent("config.ini").replace("\r\n", "\n") + "\n";
		// # 爬取两条间隔之间的等待时间，如需采集很多页，话题采集建议20秒以上，评论采集设置为3~5的数字均可
		// sleepSecond=25;
		// # 最大采集的页数，不想限制则设置为9999999（任意大于估计总页数的数字）即可
		// maxPage=99999;
		// # mysql地址，用户名，密码
		// DataSource=jdbc:mysql://172.18.79.3:3306/weibo?characterEncoding=UTF-8;
		// user=root;
		// password=root;
		// # 将跳过次数字之前的页码，从这个页码开始。从头开始采集则设置为1
		// resume=1;

		sleepSecond = Integer.parseInt(StringUtil.regMatcher(config, "sleepSecond=", ";"));
		maxCommentPage = Integer.parseInt(StringUtil.regMatcher(config, "maxPage=", ";"));
		url = StringUtil.regMatcher(config, "DataSource=", ";");
		user = StringUtil.regMatcher(config, "user=", ";");

		password = StringUtil.regMatcher(config, "password=", ";");

		int type = Integer.parseInt(StringUtil.regMatcher(config, "type=", ";"));

		if (type == 0)
		{
			String textHuati = StringUtil.getContent("url.txt").replace("\r\n", "\n") + "\n";
			if (textHuati.length() > 6)
			{
				String[] huatis = textHuati.split("\n");
				SpeWeiboCrawler swcHuati = new SpeWeiboCrawler();
				for (String string : huatis)
				{// 邓超微博 http://weibo.com/.....................
					if (!string.contains(" ") || string.startsWith("#"))
						continue;
					String title = string.split(" ")[0];
					String url1 = string.split(" ")[1];
					List<WeiboData> list = swcHuati.getHuatiWeibo(url1);
					swcHuati.saveCommentsToTextFile(list, "result/" + title + ".txt");
					swcHuati.saveCommentsToMysqlDB(list);

				}
			}
		} else if (type == 1)
		{
			String textComm = StringUtil.getContent("url.txt").replace("\r\n", "\n") + "\n";
			if (textComm.length() > 6)
			{
				String[] comments = textComm.split("\n");
				SpeWeiboCrawler swcComm = new SpeWeiboCrawler();
				for (String string : comments)
				{// 邓超微博 http://weibo.com/.....................
					if (!string.contains(" ") || string.startsWith("#"))
						continue;
					String title = string.split(" ")[0];
					String url2 = string.split(" ")[1];
					List<WeiboData> list = swcComm.getWeiboComment(url2, "result/" + title + ".txt");
					swcComm.saveCommentsToTextFile(list, "result/" + title + ".txt");
					swcComm.saveCommentsToMysqlDB(list);

				}
			}
		}

		/* 采集一个话题下的微博 */
		// List<WeiboData> list9 =
		// swc.getHuatiWeibo("http://weibo.com/p/10080811013b25c6a0284473c9ad2339921203?feed_sort=timeline&feed_filter=timeline#Pl_Third_App__9");
		// swc.saveCommentsToTextFile(list9, "话题.txt");
		// swc.saveCommentsToMysqlDB(list9);

		/* 采集一条微博的评论 */
		// List<WeiboData> list1 =
		// swc.getWeiboComment("http://weibo.com/1999607273/CdfzDkDGw?type=comment#_rnd1429675151175",
		// "多维度超越苹果.txt");
		// swc.saveCommentsToTextFile(list1, "多维度超越苹果.txt");
		// swc.saveCommentsToMysqlDB(list1);

		/* 采集一条微博的评论 */
		// List<WeiboData> list2 =
		// swc.getWeiboComment("http://weibo.com/1701401324/CeghazLHZ?type=comment#_rnd1429675407922",
		// "2.txt");
		// swc.saveCommentsToTextFile(list2, "2.txt");
		// swc.saveCommentsToMysqlDB(list2);

		/* 采集一条微博的评论 */
		// List<WeiboData> list3 =
		// swc.getWeiboComment("http://weibo.com/5187664653/CebDniEdm?type=comment#_rnd1429673240789",
		// "3.txt");
		// swc.saveCommentsToTextFile(list3, "3.txt");
		// swc.saveCommentsToMysqlDB(list3);

		/* 采集一条微博的评论 */
		// List<WeiboData> list4 =
		// swc.getWeiboComment("http://weibo.com/1883881851/Ce8E1z9cd?type=comment#_rnd1429675694722",
		// "4.txt");
		// swc.saveCommentsToTextFile(list4, "4.txt");
		// swc.saveCommentsToMysqlDB(list4);

		/* 采集一条微博的评论 */
		// List<WeiboData> list5 =
		// swc.getWeiboComment("http://weibo.com/2636180571/Ce6Qgiqc8?type=comment#_rnd1430732674652",
		// "5.txt");
		// swc.saveCommentsToTextFile(list5, "5.txt");
		// swc.saveCommentsToMysqlDB(list5);

		/* 采集一条微博的评论 */
		// List<WeiboData> list6 =
		// swc.getWeiboComment("http://weibo.com/1251372271/CebzNmGDw?type=comment#_rnd1429676640932",
		// "6.txt");
		// swc.saveCommentsToTextFile(list6, "6.txt");
		// swc.saveCommentsToMysqlDB(list6);

		// /* 采集一条微博的评论 */
		// // List<WeiboData> list7 =
		// //
		// swc.getWeiboComment("http://weibo.com/2819043642/Ce91JmuEw?filter=hot&type=comment#_rnd1429677006601",
		// // "7.txt");
		// // swc.saveCommentsToTextFile(list7, "7.txt");
		// // swc.saveCommentsToMysqlDB(list7);

	}

	/**
	 * login
	 */
	private void SinaLogin()
	{
		/* 登录 */
		SinaHttpProcess shp = new SinaHttpProcess();

		UserAttribute ua = new UserAttribute();
		ua.setName("test2co@126.com");
		ua.setPass("test2coo");
		shp.login(ua);
		/**/

		// HtmlInfo html = new HtmlInfo();
		// html.setEncode("utf-8");
		// html.setType("DATA/sina");
		// html.setSite("sina");
		// html.setCookie(ua.getCookie());

		cookie = ua.getCookie();
	}

	private List<WeiboData> getHuatiWeibo(String url) throws SAXException, IOException
	{
		String searchkey = url;
		this.SinaLogin();
		ArrayList<WeiboData> all = new ArrayList<WeiboData>();
		String id = StringUtil.regMatcher(url, "/p/", "feed_sort").replace("?", "");

		int k = 0;
		String lastHtml = null;
		String since = "";// 翻页必须
		while (url != null)
		{
			if (k < resume)
			{
				logger.info("skip 第{}页", k);
				k++;
				continue;
			}
			int prePage = (k / 3) + 1;
			int page = (k / 3) + 1;
			int currPage = k;

			logger.info("正在解析第:" + page + "页...第:" + currPage + "节...");

			if (k % 3 != 0)// 小节
				url =
						"http://weibo.com/p/aj/v6/mblog/mbloglist?" + "ajwvr=6&" + "domain=100808&" + "feed_sort=timeline&feed_filter=timeline&" + "pre_page="
								+ prePage + "&" + "page=" + page + "&" + "pl_name=Pl_Third_App__9&" + "id=" + id + "&" + "feed_type=1&" + "current_page="
								+ currPage + "";
			else
			{// 大页
				url =
						"http://weibo.com/p/" + id + "/home?" + "feed_filter=timeline&" + "feed_sort=timeline&" + "current_page=" + currPage + "&" + "page="
								+ page + "" + "#Pl_Third_App__9";
			}
			if (!since.equals(""))
				url += "&since_id=" + since;

			logger.info("downloading...\t" + url);
			// http://weibo.com/p/aj/v6/mblog/mbloglist?ajwvr=6&domain=100808&feed_sort=timeline&feed_filter=timeline&pre_page=1&page=1&pl_name=Pl_Third_App__9&id=100808cd5d9557fd71e6f6f1edb8c073d8b21a&feed_type=1&current_page=1
			// http://weibo.com/p/aj/v6/mblog/mbloglist?ajwvr=6&domain=100808&from=faxian_huati&pre_page=1&page=1&pl_name=Pl_Third_App__9&id=100808cd5d9557fd71e6f6f1edb8c073d8b21a&feed_type=1&current_page=1
			k++;

			HttpClient client = new HttpClient();
			GetMethod get = new GetMethod(url);
			get.addRequestHeader("Cookie", cookie);
			String huatiHtml = null;
			try
			{
				int status = client.executeMethod(get);
				huatiHtml = get.getResponseBodyAsString();
				if (huatiHtml.contains("Sina Visitor System"))
				{
					System.err.println("cookie失效。");
					return null;
				}

			} catch (HttpException e1)
			{
				e1.printStackTrace();
			} catch (IOException e1)
			{
				e1.printStackTrace();
			}

			if (lastHtml != null)
			{
				if (huatiHtml.equals(lastHtml))
				{
					logger.info("已达最后一页");

					break;
				} else
				{
					lastHtml = huatiHtml;
				}
			}

			String pageContent = huatiHtml;

			// 获取lazyload scienceid
			if (pageContent.contains("lazyload"))
				since = StringUtil.regMatcher(pageContent, "since_id=", "\"").replace("\\", "");

			if (pageContent.contains("Pl_Third_App__9"))
			{
				logger.info("get.");
			}

			//
			String content = null;
			if (currPage % 3 == 0)
			{
				String[] lines = pageContent.split("\n");

				for (String line : lines)
				{
					if (line.startsWith("<script>FM.view({\"ns\":\"pl.content.homeFeed.index\",\"domid\":\"Pl_Third_App__9\""))
						content = line;
				}
				content = content.substring(content.indexOf("\"html\":\"") + 8, content.lastIndexOf("\"})</script>"));
			} else
			{
				content = pageContent;
				content = content.substring(content.indexOf("\"data\":\"") + 8, content.lastIndexOf("\"}"));
			}
			if (content == null)
				break;

			content = content.replace("\\\"", "\"");
			content = content.replace("\\/", "/");
			// logger.info(content);

			ArrayList<WeiboData> list = new ArrayList<WeiboData>();
			Node root = new DOMUtil().ini(content, "utf-8");
			try
			{
				NodeList nl = XPathAPI.selectNodeList(root, "//DIV[@class='WB_feed_detail clearfix']");
				// NodeList nlAu=XPathAPI.selectNodeList(root,
				// "(//DIV[@class='WB_feed_detail clearfix'])[<index>]/DIV[@class='WB_detail']/DIV[@class='WB_info']/A[@nick-name]");

				if (nl != null)
				{
					if (nl.getLength() != 0)
					{
						for (int i = 0; i < nl.getLength(); i++)
						{
							String xpathAu =
									"(//DIV[@class='WB_feed_detail clearfix'])[" + (i + 1) + "]/DIV[@class='WB_detail']/DIV[@class='WB_info']/A[@nick-name]";
							logger.info(i + "");
							String str = nl.item(i).getTextContent();
							str = StringUtil.decodeUnicode(str);

							str = str.replace("\\r\\n", "").replace("\\n", "").replace("\\t", "").trim();
							str = str.replace("\r\\n", "").replace("\n", "").replace("\t", "").trim();
							str = StringUtil.format(str);

							WeiboData wd = new WeiboData();
							wd.setContent(str);
							wd.setSearchKey(searchkey);
							// NodeList nlAu = XPathAPI.selectNodeList(root,
							// xpathAu);
							// if (nlAu != null) {
							// if (nl.getLength() != 0) {
							// String au = nl.item(0).getTextContent();
							// au = StringUtil.decodeUnicode(au);
							//
							// au = au.replace("\\r\\n", "").replace("\\n",
							// "").replace("\\t", "").trim();
							// au = au.replace("\r\\n", "").replace("\n",
							// "").replace("\t", "").trim();
							// au = StringUtil.format(au);
							// wd.setAuthor(au);
							// System.out.print("@" + au + "\t>>>");
							// }
							//
							// }

							logger.info(str);

							logger.info("----------");

							if (!all.contains(wd))
								list.add(wd);
						}
					}
				}

				all.addAll(list);
				if (currPage > maxCommentPage)
				{
					logger.info("已达最大页.");
					break;
				}

				logger.info("等待" + sleepSecond + "秒...");
				TimeUtil.rest(sleepSecond);
				if (list.size() == 0)
				{
					logger.info("列表页为空.检索结束");
					break;
				}
			} catch (TransformerException e)
			{
				e.printStackTrace();
			}

		}// end while

		return all;
	}

	/**
	 * 采集一条微博的评论
	 * 
	 * @param url
	 *            微博评论url
	 * @return
	 * @throws IOException 
	 * @throws SAXException 
	 */
	private List<WeiboData> getWeiboComment(String url, String filename) throws SAXException, IOException
	{
		String searchkey = url;
		this.SinaLogin();

		HttpClient clientFirst = new HttpClient();
		GetMethod getFirst = new GetMethod(url);
		getFirst.addRequestHeader("Cookie", cookie);
		getFirst.addRequestHeader("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.10; rv:36.0) Gecko/20100101 Firefox/36.0");
		String content = "";
		try
		{
			int statusFirst = clientFirst.executeMethod(getFirst);
			content = getFirst.getResponseBodyAsString();
		} catch (Exception e)
		{
			e.printStackTrace();
		}

		/* 获取评论url */

		String mid = StringUtil.regMatcher(content, "qid=heart&mid=", "&");
		logger.info("get mid {}", mid);

		// 一页

		String commentUrl = "http://weibo.com/aj/v6/comment/big?id=" + mid;
		int currPage = 0;
		ArrayList<WeiboData> all = new ArrayList<WeiboData>();
		while (commentUrl != null)
		{
			if (currPage < resume)
			{

				logger.info("跳过,页码:" + currPage);

				currPage++;

				continue;
			}

			if (!commentUrl.contains("&page="))
				commentUrl += "&page=" + currPage;

			String commentHtml = null;
			HttpClient client = new HttpClient();
			GetMethod get = new GetMethod(commentUrl);
			get.addRequestHeader("Cookie", cookie);
			get.addRequestHeader("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.10; rv:36.0) Gecko/20100101 Firefox/36.0");
			try
			{
				int status = client.executeMethod(get);
				commentHtml = get.getResponseBodyAsString();

				StringUtil.writeFile("tmp.htm", commentHtml);

				if (commentHtml.contains("Sina Visitor System"))// 遇到登录页面，说明cookie已
				{
					System.err.println("cookie失效。");
					SinaLogin();
					TimeUtil.rest(sleepSecond);
					continue;
				}

			} catch (HttpException e1)
			{
				e1.printStackTrace();
			} catch (IOException e1)
			{
				e1.printStackTrace();
			}

			if (commentHtml == null)
				return null;
			currPage++;

			int totalPage =
					StringUtil.regMatcher(commentHtml, "totalpage\":", ",") == null ? 0 : Integer.parseInt(StringUtil.regMatcher(commentHtml, "totalpage\":",
							","));

			logger.info("共：" + totalPage + "页, 当前采集:" + currPage + "页..." + commentUrl);
			logger.info("============================================================================================================================");
			try
			{
				commentHtml = commentHtml.substring(commentHtml.indexOf("\"html\":\"") + 8, commentHtml.lastIndexOf("\",\"page\":"));
			} catch (Exception e)
			{
				e.printStackTrace();
				logger.info(commentUrl);
			}
			commentHtml = commentHtml.replace("\\\"", "\"");
			commentHtml = commentHtml.replace("\\/", "/");

			StringUtil.writeFile("filedown/COMM/sina/" + MD5Util.MD5(url) + "decode.htm", StringUtil.decodeUnicode(commentHtml));
			org.w3c.dom.Node root = new DOMUtil().ini(commentHtml, "utf-8");

			ArrayList<WeiboData> list = new ArrayList<WeiboData>();

			try
			{
				/* author */
				NodeList nl = org.apache.xpath.XPathAPI.selectNodeList(root, "//DIV[@class='WB_text']/A[1]");
				if (nl != null)
				{
					if (nl.getLength() != 0)
					{
						for (int i = 0; i < nl.getLength(); i++)
						{
							String str = nl.item(i).getTextContent();

							WeiboData wd = new WeiboData();

							wd.setAuthor(StringUtil.decodeUnicode(str));
							wd.setSearchKey(searchkey);
							list.add(wd);
						}
					}
				}
				logger.info("本页共:" + list.size() + "个评论作者");
				/* content */
				String xpathContent = "(//DIV[@class='WB_text'])[index]/text()|(//DIV[@class='WB_text'])[index]/IMG/@title";

				for (int i = 0; i < list.size(); i++)
				{

					NodeList nlContent = XPathAPI.selectNodeList(root, xpathContent.replace("index", (i + 1) + ""));
					String textAndImg = "";

					if (nlContent != null)
					{
						for (int j = 0; j < nlContent.getLength(); j++)
						{
							textAndImg += nlContent.item(j).getTextContent();
						}
					}
					textAndImg = StringUtil.decodeUnicode(textAndImg).replace("\\n", "").replace("\n", "").trim();

					list.get(i).setContent(textAndImg);

				}

				// if (list.size() == 0)
				// {
				// currPage--;
				// logger.error("将重试");
				// // SinaLogin();
				// TimeUtil.rest(sleepSecond);
				// continue;
				// }

				int k = 0;
				for (WeiboData wd : list)
				{

					logger.info(k++ + "");
					logger.info(wd.getAuthor());
					logger.info(wd.getContent());
					logger.info("------");

				}
				logger.info("第" + currPage + "页采集完成.共:" + list.size() + "个评论");
				all.addAll(list);
				saveCommentsToTextFile(list, filename, true);

				if (currPage < totalPage && currPage < maxCommentPage)
				{
					if (commentUrl.contains("&page="))
						commentUrl = commentUrl.substring(0, commentUrl.lastIndexOf("&page=")) + "&page=" + (currPage + 1);
					else
					{
						commentUrl += "&page=2";// 第一页
					}
					logger.info("等待" + sleepSecond + "秒...");
					TimeUtil.rest(sleepSecond);
				} else
				{
					if ((totalPage == 0 || totalPage == 1) && currPage > 1)
					{

						if (commentUrl.contains("&page="))
							commentUrl = commentUrl.substring(0, commentUrl.lastIndexOf("&page=")) + "&page=" + (currPage + 1);
						else
						{
							commentUrl += "&page=2";// 第一页
						}
						logger.info("等待" + sleepSecond + "秒...");
						TimeUtil.rest(sleepSecond);
					}

					else
					{

						commentUrl = null;
					}

				}

			} catch (TransformerException e)
			{
				e.printStackTrace();
				System.err.println(commentUrl);
			}
		}

		logger.info("当前微博全部采集完成. 共采集评论" + all.size() + "条.");
		return all;

	}

	private void saveCommentsToTextFile(List<WeiboData> list, String filename, boolean append)
	{
		if (append)
		{
			try
			{
				BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filename, append), "utf-8"));
				for (WeiboData weiboData : list)
				{
					writer.write(weiboData.getAuthor() + "\t" + weiboData.getContent() + "\r\n");
					writer.flush();

				}
				writer.close();
			} catch (FileNotFoundException e)
			{
				e.printStackTrace();
			} catch (UnsupportedEncodingException e)
			{
				e.printStackTrace();
			} catch (IOException e)
			{
				e.printStackTrace();
			}
		} else
		{
			saveCommentsToTextFile(list, filename);
		}
	}

	private void saveCommentsToTextFile(List<WeiboData> list, String filename)
	{
		logger.info("to file... [" + filename + "]");
		StringBuilder sb = new StringBuilder();
		for (WeiboData weiboData : list)
		{
			sb.append(weiboData.getAuthor() + "\t" + weiboData.getContent()).append("\r\n");

		}
		StringUtil.writeFile(filename, sb.toString());
		logger.info("ok.");
	}

	/**
	 * save to mysql database
	 * 
	 * @param list
	 */
	private void saveCommentsToMysqlDB(List<WeiboData> list)
	{

		Connection conn = null;
		try
		{
			Class.forName(driver);

			conn = DriverManager.getConnection(url, user, password);

			if (!conn.isClosed())
				logger.info("Succeeded connecting to the Database!");

			String create =
					"create table if not exists weibo(" + "id int(20) not null PRIMARY KEY auto_increment," + "author VARCHAR(500)," + "content VARCHAR(1500),"
							+ "inserttime datetime," + "weibo_url VARCHAR(4000)" + ")";
			Statement createstmt = conn.createStatement();
			createstmt.execute(create);

			int k = 0;
			logger.info("saving...");
			for (WeiboData wd : list)
			{
				k++;
				if (k % 100 == 0)
					System.out.print(k + " ");
				if (k % 2000 == 0)
					System.out.println();
				String sql = "insert into weibo(author,content,inserttime,weibo_url) values (?,?,?,?)";
				PreparedStatement ps = conn.prepareStatement(sql);
				ps.setString(1, wd.getAuthor());
				ps.setString(2, wd.getContent().replace("'", ""));
				ps.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
				ps.setString(4, wd.getSearchKey());
				ps.execute();
			}
			logger.info("all saved.(" + k + ")");

		} catch (ClassNotFoundException e)
		{

			logger.info("Sorry,can`t find the Driver!");
			e.printStackTrace();

		} catch (Exception e)
		{

			e.printStackTrace();

		} finally
		{
			try
			{
				conn.close();
			} catch (SQLException e)
			{
				e.printStackTrace();
			}
		}
	}

}
