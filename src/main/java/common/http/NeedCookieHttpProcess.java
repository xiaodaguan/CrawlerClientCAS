package common.http;

import common.system.UserAttribute;

/**
 * 需要cookie的http请求处理
 * @author grs
 * @since 2014年8月
 */
public abstract class NeedCookieHttpProcess extends SimpleHttpProcess {
	protected String cookie = "";
//	protected UserAttribute user;
	
	public abstract boolean login(UserAttribute user) ;
	
	public abstract boolean verify(UserAttribute user) throws Exception;
	
	public void setCookie(String cookie) {
		this.cookie = cookie;
	}
	public String getCookie() {
		return cookie;
	}
//	public UserAttribute getUser() {
//		return user;
//	}
//	public void setUser(UserAttribute user) {
//		this.user = user;
//	}
//	
}
