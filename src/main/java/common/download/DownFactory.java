package common.download;

import java.util.concurrent.CountDownLatch;

import common.pojos.AgricaltureData;
import common.pojos.BBSData;
import common.pojos.BlogData;
import common.pojos.ClientData;
import common.pojos.CommonData;
import common.pojos.CompanyData;
import common.pojos.ConferenceData;
import common.pojos.FrgmediaData;
import common.pojos.GovAffairData;
import common.pojos.NewsData;
import common.pojos.PersonData;
import common.pojos.PressData;
import common.pojos.ReportData;
import common.pojos.VideoData;
import common.pojos.WeiboData;
import common.pojos.WeixinData;
import common.download.agricalture.AgricaltureDataCommonDownload;
import common.download.agricalture.AgricaltureMetaCommonDownload;
import common.download.agricalture.AgricaltureMonitorMetaCommonDownload;
import common.download.bbs.BBSDataCommonDownload;
import common.download.bbs.BBSDataMonitorDownload;
import common.download.bbs.BBSMetaCommonDownload;
import common.download.bbs.BBSMetaMonitorDownload;
import common.download.blog.BlogDataCommonDownload;
import common.download.blog.BlogMetaCommonDownload;
import common.download.client.ClientDataCommonDownload;
import common.download.client.ClientMetaCommonDownload;
import common.download.company.CompanyDataCommonDownload;
import common.download.company.CompanyMetaCommonDownload;
import common.download.conference.ConferenceDataCommonDownload;
import common.download.conference.ConferenceMetaCommonDownload;
import common.download.frgmedia.FrgmediaDataCommonDownload;
import common.download.frgmedia.FrgmediaMetaCommonDownload;
import common.download.govaffair.GovAffairDataCommonDownload;
import common.download.govaffair.GovAffairMetaCommonDownload;
import common.download.news.NewsDataCommonDownload;
import common.download.news.NewsMetaCommonDownload;
import common.download.news.NewsMonitorMetaCommonDownload;
import common.download.person.PersonDataCommonDownload;
import common.download.person.PersonMetaCommonDownload;
import common.download.press.PressDataCommonDownload;
import common.download.press.PressMetaCommonDownload;
import common.download.report.ReportDataCommonDownload;
import common.download.report.ReportMetaCommonDownload;
import common.download.video.VideoDataCommonDownload;
import common.download.video.VideoMetaCommonDownload;
import common.download.weibo.WeiboDataCommonDownload;
import common.download.weibo.WeiboSearchDataCommonDownload;
import common.download.weibo.WeiboSearchMetaCommonDownload;
import common.download.weibo.WeiboUserMonitorMetaCommonDownload;
import common.download.weixin.WeixinDataCommonDownload;
import common.download.weixin.WeixinMetaCommonDownload;
import common.rmi.packet.SearchKey;
import common.system.Systemconfig;
import common.system.UserAttribute;

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
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static GenericMetaCommonDownload metaControl(SearchKey key) {
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
		case 9:// return new SokuVideoMetaCommonDownload(key);
		case 10:
			return new VideoMetaCommonDownload(key);
		// case 11 :
		// case 12 : return new AcademicMetaCommonDownload(key);

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
		case 37://
		case 38:
			return new GovAffairMetaCommonDownload(key);
		case 39://
		case 40:
			return new PressMetaCommonDownload(key);
		case 41://
		case 42:
			return new FrgmediaMetaCommonDownload(key);

		case 45://
		case 46:
			return new ClientMetaCommonDownload(key);
		default:
			return new SimpleMetaCommonDownload(key);
		}
	}

	/**
	 * 详细数据
	 * 
	 * @param siteFlag
	 * @return
	 */
	public static <T> GenericDataCommonDownload<T> dataControl(String siteFlag, T data, CountDownLatch count,
                                                               UserAttribute user, SearchKey key) {
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
			return (GenericDataCommonDownload<T>) new WeiboSearchDataCommonDownload(siteFlag, (WeiboData) data, count,
					user, key);
		case 8:
			return (GenericDataCommonDownload<T>) new WeiboDataCommonDownload(siteFlag, (WeiboData) data, count, user,
					key);
		// return null;

		case 9:
		case 10:
			return (GenericDataCommonDownload<T>) new VideoDataCommonDownload(siteFlag, (VideoData) data, count, key);
		// case 11 :
		// case 12 : return (GenericDataCommonDownload<T>) new
		// AcademicDataCommonDownload(siteFlag, (Data) data, count);

		case 15:
		case 16:
			return (GenericDataCommonDownload<T>) new WeixinDataCommonDownload(siteFlag, (WeixinData) data, count, key);

		case 21:
		case 22:
			return (GenericDataCommonDownload<T>) new ReportDataCommonDownload(siteFlag, (ReportData) data, count, key);
		case 27:
		case 28:
			return (GenericDataCommonDownload<T>) new AgricaltureDataCommonDownload(siteFlag, (AgricaltureData) data,
					count, key);
		case 29:
		case 30:
			return (GenericDataCommonDownload<T>) new ConferenceDataCommonDownload(siteFlag, (ConferenceData) data,
					count, key);
		case 31:
		case 32:
			return (GenericDataCommonDownload<T>) new PersonDataCommonDownload(siteFlag, (PersonData) data, count, key);
		case 33:
		case 34:
			return (GenericDataCommonDownload<T>) new CompanyDataCommonDownload(siteFlag, (CompanyData) data, count,
					key);
		case 37:
		case 38:
			return (GenericDataCommonDownload<T>) new GovAffairDataCommonDownload(siteFlag, (GovAffairData) data, count,
					key);
		case 39:
		case 40:
			return (GenericDataCommonDownload<T>) new PressDataCommonDownload(siteFlag, (PressData) data, count,
					key);
		case 41:
		case 42:
			return (GenericDataCommonDownload<T>) new FrgmediaDataCommonDownload(siteFlag, (FrgmediaData) data, count,
					key);
		case 45:
		case 46:
			return (GenericDataCommonDownload<T>) new ClientDataCommonDownload(siteFlag, (ClientData) data, count,
					key);
		default:	
			return (GenericDataCommonDownload<T>) new SimpleDataCommonDownload(siteFlag, (CommonData) data, count, key);

		}
	}

}
