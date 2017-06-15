package common.pojos;

import org.apache.http.HttpHost;

public class Proxy {
	private int id;
	private HttpHost hHost;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public HttpHost gethHost() {
		return hHost;
	}
	public void sethHost(HttpHost hHost) {
		this.hHost = hHost;
	}
	
}


