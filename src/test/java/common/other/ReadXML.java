package common.other;
import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.junit.Test;


import java.io.*;
import java.util.*;
import org.dom4j.Document;
import org.dom4j.io.*;
public class ReadXML {

//    @Test
//    public  void testMain() {
//
//            long lasting = System.currentTimeMillis();
//
//            try {
//                String path = "D:\\Users\\Administrator\\ideaworkspace\\CrawlerClientCAS\\src\\main\\resources\\accountConf\\weibo.xml";
//
//
// File f = new File(path);
//                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
//                DocumentBuilder builder = factory.newDocumentBuilder();
//                Document doc = builder.parse(path);
//                NodeList nl = doc.getElementsByTagName("user");
//
//                for (int i = 0; i < nl.getLength(); i++) {
//                    Node node = nl.item(i);
//
//                    String result = node.getTextContent();
//
//                    System.out.println(result);
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }

    @Test
    public  void testMain01() {

        try {

            String path = "D:\\Users\\Administrator\\ideaworkspace\\CrawlerClientCAS\\src\\main\\resources\\accountConf\\weibo.xml";

            File f = new File(path);
            SAXReader reader = new SAXReader();
            Document doc = reader.read(f);
            Element root = doc.getRootElement();

            for (Iterator i = root.elementIterator("user"); i.hasNext();) {
                Element ele = (Element) i.next();
                String valid  = ele.elementText("valid");
                if(!valid.equals("1")){
                    continue;
                }
                String name  = ele.elementText("name");
                String passwd   = ele.elementText("passwd");
                String siteFlag  = ele.elementText("siteFlag");

                System.out.println("name    :"+name);
                System.out.println("passwd  :"+passwd);
                System.out.println("siteFlag:"+siteFlag);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
//<userList>
    //<user>
        //<account>1354805597rzy@sina.com</account>
        //<passwd>649859709</passwd>
    //</user>
//</userList>