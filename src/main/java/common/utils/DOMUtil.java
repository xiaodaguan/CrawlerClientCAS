package common.utils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.html.dom.HTMLDocumentImpl;
import org.apache.log4j.Logger;
import org.cyberneko.html.parsers.DOMFragmentParser;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * dom初始化类
 * 
 * @author grs
 * @since 2011年7月
 */
public class DOMUtil {
	private static Logger LOGGER = Logger.getLogger(DOMUtil.class);

	/**
	 * 为进行Xpath解析初始化数据
	 * 
	 * @param content
	 * @param charset
	 * @throws IOException
	 * @throws SAXException
	 */
	public DocumentFragment ini(String content, String charset) throws SAXException, IOException {
		if (content == null)
			return null;
		charset = charset == null ? "utf-8" : charset;
		byte[] byt = null;
		try {
			byt = content.replaceAll("[\\x00-\\x08\\x0b-\\x0c\\x0e-\\x1f]", "").getBytes(charset);
		} catch (UnsupportedEncodingException e) {
			LOGGER.error("获得字节失败，检查编码是否存在", e);
			byt = null;
			return null;
		}
		InputSource source = new InputSource(new ByteArrayInputStream(byt));
		source.setEncoding(charset);
		DOMFragmentParser parser = new DOMFragmentParser();
		DocumentFragment domtree = new HTMLDocumentImpl().createDocumentFragment();
		// try {
		// 是否允许增补缺失的标签。如果要以XML方式操作HTML文件，此值必须为真
		parser.setFeature("http://cyberneko.org/html/features/balance-tags", true);
		// 是否剥掉<script>元素中的<!-- -->等注释符
		parser.setFeature("http://cyberneko.org/html/features/scanner/script/strip-comment-delims", true);
		parser.parse(source, domtree);
		return domtree;
		// } catch (Exception e) {
		// LOGGER.error("Dom解析失败，网页数据有误！", e);
		// return domtree;
		// }
		// finally {
		// byt = null;
		// source = null;
		// parser = null;
		// }
	}

	public static String dom2Html(Node dom) {

		DOMSource domSource = new DOMSource(dom);
		StringWriter writer = new StringWriter();
		StreamResult result = new StreamResult(writer);
		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer transformer = null;
		try {
			transformer = tf.newTransformer();
			transformer.transform(domSource, result);
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		}

		String str = writer.toString();
		return str;
	}
}