package common.extractor;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.xml.sax.SAXException;

import common.bean.HtmlInfo;
/**
 * 数据抽取类
 * @author grs
 *
 * @param <T>
 */
public abstract class AbstractExtractor<T> implements Extractor<T> {
	protected static final SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	protected static final SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	protected static final SimpleDateFormat sdf3 = new SimpleDateFormat("yyyy-MM-dd");
	protected static final SimpleDateFormat sdf4 = new SimpleDateFormat("yyyy-MM");
	protected static final SimpleDateFormat sdf5 = new SimpleDateFormat("yyyy-");
	
	@Override
	public abstract String templateListPage(List<T> list, HtmlInfo html,
			int page, String... keyword) throws SAXException, IOException;

	@Override
	public abstract String templateContentPage(T data, HtmlInfo html, int page,
			String... keyword) throws SAXException, IOException;
	
	@Override
	public abstract String templateContentPage(T data, HtmlInfo html,
			String... keyword) throws SAXException, IOException;
	/**
	 * 时间格式规范化
	 * @param time
	 * @return
	 */
	protected Date timeProcess(String time) {
		Date d = null;
		try {
			d = sdf1.parse(time);
		} catch (ParseException e) {
			try {
				d = sdf2.parse(time);
			} catch (ParseException e1) {
				try {
					d = sdf3.parse(time);
				} catch (ParseException e2) {
					try {
						d = sdf4.parse(time);
					} catch (ParseException e3) {
						try {
							d = sdf5.parse(time);
						} catch (ParseException e4) {
							d = null;
						}					}
				}
			}
		}
		return d;
	}

}
