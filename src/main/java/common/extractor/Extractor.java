package common.extractor;

import java.io.IOException;
import java.util.List;

import org.xml.sax.SAXException;

import common.pojos.HtmlInfo;

public interface Extractor<T> {

	/**
	 * 列表页解析
	 * @param list	解析的列表
	 * @param html	页面信息
	 * @param page	采集页码
	 * @param siteFlag	站点标识
	 * @param collectFlag	采集标识
	 * @param keyword	可扩展的关键词
	 * @return	下一页地址
	 * @throws IOException 
	 * @throws SAXException 
	 */
	public String templateListPage(List<T> list, HtmlInfo html, int page, String... keyword) throws SAXException, IOException;
	
	/**
	 * 内容页解析
	 * @param data
	 * @param html
	 * @param page
	 * @param keyword
	 * @return
	 * @throws IOException 
	 * @throws SAXException 
	 */
	public String templateContentPage(T data, HtmlInfo html, int page, String... keyword) throws SAXException, IOException;
	
	public String templateContentPage(T data, HtmlInfo html, String... keyword) throws SAXException, IOException;

}
