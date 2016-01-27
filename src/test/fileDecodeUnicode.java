package test;

import common.util.StringUtil;


public class fileDecodeUnicode {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String content=StringUtil.getContent("/Users/guanxiaoda/Desktop/那个页.html");
		String decode=StringUtil.decodeUnicode(content);
		StringUtil.writeFile("/Users/guanxiaoda/Desktop/new.html", decode);
		System.out.println("ok.");
	}

}
