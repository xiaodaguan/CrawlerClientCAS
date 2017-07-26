package common.other;
import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
public class ReadXML {

        public static void main(String arge[]) {

            long lasting = System.currentTimeMillis();

            try {
                String path = "accountConf/weibo.xml";
                File f = new File(path);
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();
                Document doc = builder.parse(f);
                NodeList nl = doc.getElementsByTagName("userList");

                for (int i = 0; i < nl.getLength(); i++) {
                    Node node = nl.item(i);
                    String result = node.getTextContent();

                    System.out.println(result);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
}
