package common.download;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import common.extractor.xpath.client.search.ClientSearchXpathExtractor;
import common.rmi.packet.SearchKey;
import common.rmi.packet.ViewInfo;
import common.rmi.packet.ViewInfo.InnerInfo;
import common.system.Systemconfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 下载元数据
 * @author grs
 */
public abstract class GenericMetaCommonDownload<T> extends GenericCommonDownload<T> implements Runnable {
	private static final Logger LOGGER = LoggerFactory.getLogger(GenericMetaCommonDownload.class);

	protected Map<String, Integer> map = Collections.synchronizedMap(new HashMap<String, Integer>());
	
	public GenericMetaCommonDownload(SearchKey key) {
		super(key);
	}

	@Override
	public void run() {
		prePorcess();
		try {
			process();
		}catch(Exception e){
			e.printStackTrace();
		}
		finally {
		}
		postProcess();
	}
	public abstract void process();
	
	public void prePorcess() {
		
	}
	public void postProcess() {
		map.clear();
		map = null;
		LOGGER.info(siteFlag+"的"+key.getKEYWORD()+"列表页数据采集完成！！");


	}
	

}
