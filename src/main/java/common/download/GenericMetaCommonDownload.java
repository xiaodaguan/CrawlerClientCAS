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
	private final CountDownLatch endCount = new CountDownLatch(1);
	@Override
	public void run() {
		prePorcess();
		try {
			process();
		}catch(Exception e){
			e.printStackTrace();
		}
		finally {
			release();
		}
		postProcess();
	}
	public abstract void process();
	
	public void prePorcess() {
		
	}
	public void postProcess() {
		map.clear();
		map = null;
		try {
			endCount.await(5, TimeUnit.HOURS);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
		LOGGER.info(siteFlag+"的"+key.getKEYWORD()+"数据采集完成！！");

		
		String taskName =   siteFlag+key.getKEYWORD();
		LOGGER.info( taskName + "   任务已完成        postProcess  ");
		
		Systemconfig.finish.put(siteFlag+key.getKEYWORD(), true);
	}
	
	public void release() {
		endCount.countDown();
	}
	
}
