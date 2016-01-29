package common.down;

import java.util.concurrent.CountDownLatch;

import common.bean.*;
import common.down.agricalture.AgricaltureDataCommonDownload;
import common.down.agricalture.AgricaltureMetaCommonDownload;
import common.down.agricalture.AgricaltureMonitorMetaCommonDownload;
import common.down.bbs.BBSDataCommonDownload;
import common.down.bbs.BBSDataMonitorDownload;
import common.down.bbs.BBSMetaCommonDownload;
import common.down.bbs.BBSMetaMonitorDownload;
import common.down.blog.BlogDataCommonDownload;
import common.down.blog.BlogMetaCommonDownload;
import common.down.company.CompanyDataCommonDownload;
import common.down.company.CompanyMetaCommonDownload;
import common.down.conference.ConferenceDataCommonDownload;
import common.down.conference.ConferenceMetaCommonDownload;
import common.down.ebusiness.EbusinessDataCommonDownload;
import common.down.ebusiness.EbusinessMetaCommonDownload;
import common.down.news.NewsDataCommonDownload;
import common.down.news.NewsMetaCommonDownload;
import common.down.news.NewsMonitorMetaCommonDownload;
import common.down.person.PersonDataCommonDownload;
import common.down.person.PersonMetaCommonDownload;
import common.down.report.ReportDataCommonDownload;
import common.down.report.ReportMetaCommonDownload;
import common.down.video.VideoDataCommonDownload;
import common.down.video.VideoMetaCommonDownload;
import common.down.weibo.WeiboDataCommonDownload;
import common.down.weibo.WeiboMonitorMetaCommonDownload;
import common.down.weibo.WeiboSearchDataCommonDownload;
import common.down.weibo.WeiboSearchMetaCommonDownload;
import common.down.weibo.WeiboUserMonitorMetaCommonDownload;
import common.down.weixin.WeixinDataCommonDownload;
import common.down.weixin.WeixinMetaCommonDownload;
import common.rmi.packet.SearchKey;
import common.system.Systemconfig;
import common.system.UserAttr;
import sun.net.www.content.text.Generic;

/**
 * 下载线程的工厂方法 可处理特殊的站点下载
 * 
 * @author grs
 * 
 */
public class DownFactory {
	/**
	 * 元数据
	 * 
	 * @param siteFlag
	 * @param type
	 * @return
	 */
	@SuppressWarnings("rawtypes") public static GenericMetaCommonDownload metaControl(SearchKey key) {
		switch (Systemconfig.crawlerType) {
		case 1:
			return new NewsMetaCommonDownload(key);
		case 2:
			return new NewsMonitorMetaCommonDownload(key);
		case 3:
			return new BBSMetaCommonDownload(key);
		case 4:
			return new BBSMetaMonitorDownload(key);
		case 5:
		case 6:
			return new BlogMetaCommonDownload(key);
		case 7:
			return new WeiboSearchMetaCommonDownload(key);
		case 8:
			return new WeiboUserMonitorMetaCommonDownload(key);
		case 9:
		case 10:
			return new VideoMetaCommonDownload(key);
		// case 11 :
		// case 12 : return new AcademicMetaCommonDownload(key);
		case 13:
		case 14:
			return new EbusinessMetaCommonDownload(key);
		case 15:
		case 16:
			return new WeixinMetaCommonDownload(key);
		case 21:
		case 22:
			return new ReportMetaCommonDownload(key);

		case 27:
			return new AgricaltureMetaCommonDownload(key);
		case 28:
			return new AgricaltureMonitorMetaCommonDownload(key);

		case 29:
		case 30:
			return new ConferenceMetaCommonDownload(key);
		case 31:
		case 32:
			return new PersonMetaCommonDownload(key);
			case 33:
			case 34:
				return new CompanyMetaCommonDownload(key);

		default:
			return new SimpleMetaCommonDownload(key);
		}
	}

	/**
	 * 详细数据
	 * 
	 * @param siteFlag
	 * @param type
	 * @return
	 */
	public static <T> GenericDataCommonDownload<T> dataControl(String siteFlag, T data, CountDownLatch count, UserAttr user, SearchKey key) {
		switch (Systemconfig.crawlerType) {
		case 1:
		case 2:
			return (GenericDataCommonDownload<T>) new NewsDataCommonDownload(siteFlag, (NewsData) data, count, key);
		case 3:
			return (GenericDataCommonDownload<T>) new BBSDataCommonDownload(siteFlag, (BBSData) data, count, key);
		case 4:
			return (GenericDataCommonDownload<T>) new BBSDataMonitorDownload(siteFlag, (BBSData) data, count, key);
		case 5:
		case 6:
			return (GenericDataCommonDownload<T>) new BlogDataCommonDownload(siteFlag, (BlogData) data, count, key);
		case 7:
			return (GenericDataCommonDownload<T>) new WeiboSearchDataCommonDownload(siteFlag, (WeiboData) data, count, user, key);
		case 8:
			return (GenericDataCommonDownload<T>) new WeiboDataCommonDownload(siteFlag, (WeiboData) data, count, user, key);
		// return null;

		case 9:
		case 10:
			return (GenericDataCommonDownload<T>) new VideoDataCommonDownload(siteFlag, (VideoData) data, count, key);
		// case 11 :
		// case 12 : return (GenericDataCommonDownload<T>) new
		// AcademicDataCommonDownload(siteFlag, (Data) data, count);
		case 13:
		case 14:
			return (GenericDataCommonDownload<T>) new EbusinessDataCommonDownload(siteFlag, (EbusinessData) data, count, key);
		case 15:
		case 16:
			return (GenericDataCommonDownload<T>) new WeixinDataCommonDownload(siteFlag, (WeixinData) data, count, key);

		case 21:
		case 22:
			return (GenericDataCommonDownload<T>) new ReportDataCommonDownload(siteFlag, (ReportData) data, count, key);
		case 27:
		case 28:
			return (GenericDataCommonDownload<T>) new AgricaltureDataCommonDownload(siteFlag, (AgricaltureData) data, count, key);
		case 29:
		case 30:
			return (GenericDataCommonDownload<T>) new ConferenceDataCommonDownload(siteFlag, (ConferenceData) data, count, key);
		case 31:
		case 32:
			return (GenericDataCommonDownload<T>) new PersonDataCommonDownload(siteFlag, (PersonData) data, count, key);
			case 33:
			case 34:
				return (GenericDataCommonDownload<T>) new CompanyDataCommonDownload(siteFlag,(CompanyData)data,count,key);
		default:
			return (GenericDataCommonDownload<T>) new SimpleDataCommonDownload(siteFlag, (CommonData) data, count, key);

		}
	}

}
