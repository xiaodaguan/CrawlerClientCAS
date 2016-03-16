package special;

import common.util.DOMUtil;
import common.util.TimeUtil;
import org.apache.xpath.XPathAPI;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.transform.TransformerException;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by guanxiaoda on 16/3/16.
 */
public class BaiduEverydayCount {


    /**
     * 获取当天的相关新闻数量(百度新闻搜索)
     */
    public static int getDayCount(String keyword, Date date) throws IOException, SAXException, TransformerException {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String dateStr = sdf.format(date);
//        System.out.println(dateStr);


        String url = "http://news.baidu.com/ns?from=news" +
                "&cl=2" +
                "&q1=" + keyword + "" +
                "&submit=百度一下" +
                "&begin_date=" + dateStr + "" +
                "&end_date=" + dateStr + "" +
                "&tn=newsdy&ct1=1&ct=1&rn=20";
        url += "&bt=" + TimeUtil.str2Timestamp(dateStr, "yyyy-MM-dd") / 1000;
        url += "&et=" + (TimeUtil.str2Timestamp(dateStr, "yyyy-MM-dd") / 1000 + 24 * 3600 - 1);
        System.out.println(url);
        URL u = new URL(url);
        URLConnection conn = u.openConnection();
        String cont = "";
        BufferedReader r = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String line = null;
        while ((line = r.readLine()) != null) {
            cont += line + "\r\n";
        }
        r.close();

        String xpath = "//SPAN[@class='nums'][contains(.,'相关新闻')]";
        Node dom = new DOMUtil().ini(cont, "utf-8");
        Node node = XPathAPI.selectSingleNode(dom, xpath);
        if (node == null) return 0;
        else {
            return Integer.parseInt(getNumbers(node.getTextContent().replace(",", "")));
        }
    }

    //截取数字
    public static String getNumbers(String content) {
        Pattern pattern = Pattern.compile("\\d+");
        Matcher matcher = pattern.matcher(content);
        while (matcher.find()) {
            return matcher.group(0);
        }
        return null;
    }

    public static void main(String[] args) throws IOException {

        Calendar c = Calendar.getInstance();
        System.out.println(c.getTime());

        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("result.txt")));


        while (true) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String dateStr = sdf.format(c.getTime());
            if (dateStr.contains("2015-")) break;//只采集2016年的数据
            try {
                int dayCount1 = getDayCount("引力波", c.getTime());
                int dayCount2 = getDayCount("人机围棋", c.getTime());
                System.out.println(dateStr + ":" + dayCount1 + ":" + dayCount2);

                writer.write(dateStr + "\t" + dayCount1 + "\t" + dayCount2 + "\r\n");
                writer.flush();

                Thread.sleep(1000 * 5);

            } catch (IOException e) {
                e.printStackTrace();
            } catch (SAXException e) {
                e.printStackTrace();
            } catch (TransformerException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            c.add(Calendar.DAY_OF_MONTH, -1);

        }
        writer.close();

    }
}
