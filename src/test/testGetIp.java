package test;

import java.net.InetAddress;

public class testGetIp {	
	public static void main(String[] args) throws Exception {
		
		
		System.out.println(InetAddress.getLoopbackAddress());
		
		System.out.println(InetAddress.getLocalHost());
		
		System.out.println(InetAddress.getLocalHost().getHostAddress());
		
		
		
	}
}
