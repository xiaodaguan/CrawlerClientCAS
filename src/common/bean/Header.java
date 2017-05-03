package common.bean;



/**
 * 网页信息
 * @author grs
 *
 */
public class Header {

	
	private int id;
	
	/** headers */
	private String cookie;//页面cookie
	private String userAgent;//user agent
	private String referer;
	private String host;
	private String accept;
	private String acceptLanguage;
	private String acceptEncoding;
	private String connection;
	private String cacheControl;
	
	
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getCookie() {
		return cookie;
	}
	public void setCookie(String cookie) {
		this.cookie = cookie;
	}
	public String getUserAgent() {
		return userAgent;
	}
	public void setUserAgent(String userAgent) {
		this.userAgent = userAgent;
	}
	public String getReferer() {
		return referer;
	}
	public void setReferer(String referer) {
		this.referer = referer;
	}
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public String getAccept() {
		return accept;
	}
	public void setAccept(String accept) {
		this.accept = accept;
	}
	public String getAcceptLanguage() {
		return acceptLanguage;
	}
	public void setAcceptLanguage(String acceptLanguage) {
		this.acceptLanguage = acceptLanguage;
	}
	public String getAcceptEncoding() {
		return acceptEncoding;
	}
	public void setAcceptEncoding(String acceptEncoding) {
		this.acceptEncoding = acceptEncoding;
	}
	public String getConnection() {
		return connection;
	}
	public void setConnection(String connection) {
		this.connection = connection;
	}
	public String getCacheControl() {
		return cacheControl;
	}
	public void setCacheControl(String cacheControl) {
		this.cacheControl = cacheControl;
	}
	
	
	
	
	
}
