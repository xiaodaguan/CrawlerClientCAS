package test;

import common.util.TimeUtil;

import java.text.SimpleDateFormat;
import java.util.Date;

public class testTime {
	public static String convert(long mill) {
		Date date = new Date(mill);
		String strs = "";
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			strs = sdf.format(date);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return strs;
	}

	public static void main(String[] args) {
		
//		Date date=new Date();
//		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
//		Calendar calendar=Calendar.getInstance();
//		calendar.setTime(date);
//		calendar.add(Calendar.MONTH, -1);
//		System.out.println(sdf.format(calendar.getTime()));
//		System.out.println(TimeUtil.timestamp2Str(1438766747000L, "yyyy-MM-dd hh:mm:ss"));


		System.out.println(System.currentTimeMillis());
		System.out.println(new java.util.Date().getTime());
	}

}
