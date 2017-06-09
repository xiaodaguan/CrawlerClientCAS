package common.http;

import common.system.UserAttribute;
import org.apache.http.client.HttpClient;

import common.bean.HtmlInfo;

public interface HttpProcess {

	/**
	 * 获得请求页面内容
	 * @param html
	 */
	public void getContent(HtmlInfo html);
	
	/**
	 * 根据网页信息设置httpclient请求参数
	 * @param html
	 * @return
	 */
	public HttpClient httpClient(HtmlInfo html);
	
	/**
	 * 需要登录用户的页面内容
	 * @param html
	 * @param userAttribute
	 */
	public void getContent(HtmlInfo html, UserAttribute userAttribute);
	
	/**
	 * 登陆状态检测
	 * @param userAttribute
	 */
	public void monitorLogin(UserAttribute userAttribute);
	
	public String getJsonContent(String ownerInitUrl);
}
