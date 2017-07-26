package common.utils;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 字符串处理类
 * 
 * @author grs
 * @since 2012.5
 */
public class StringUtil {
    /**
     * \r\n
     * default charset: utf-8
     *
     * @param is
     * @return
     */
    public static String readStream(InputStream is) throws IOException {
        return readStream(is, "utf-8");
    }


    /**
     * \r\n
     *
     * @param is
     * @param charSet
     * @return
     */
    public static String readStream(InputStream is, String charSet) throws IOException {
        BufferedReader reader = null;
        reader = new BufferedReader(new InputStreamReader(is, charSet));
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null) {
            sb.append(line).append("\r\n");
        }
        reader.close();


        return sb.toString();
    }
	/**
	 * 判断str1中包含str2的个数
	 * 
	 * @param str1
	 * @param str2
	 * @return counter
	 */
	private static int counter = 0;
	public static int countStr(String str1, String str2) {
		
		if (str1.indexOf(str2) == -1) {
			return 0;
		} else if (str1.indexOf(str2) != -1) {
			counter++;
			countStr(str1.substring(str1.indexOf(str2) + str2.length()), str2);
			return counter;
		}
		return 0;
	}

	/**
	 * 解码 Unicode \\uXXXX
	 * 
	 * @param str
	 * @return
	 */
	public static String decodeUnicode(String str) {
		Charset set = Charset.forName("UTF-16");
		Pattern p = Pattern.compile("\\\\u([0-9a-fA-F]{4})");
		Matcher m = p.matcher(str);
		int start = 0;
		int start2 = 0;
		StringBuffer sb = new StringBuffer();
		while (m.find(start)) {
			start2 = m.start();
			if (start2 > start) {
				String seg = str.substring(start, start2);
				sb.append(seg);
			}
			String code = m.group(1);
			int i = Integer.valueOf(code, 16);
			byte[] bb = new byte[4];
			bb[0] = (byte) ((i >> 8) & 0xFF);
			bb[1] = (byte) (i & 0xFF);
			ByteBuffer b = ByteBuffer.wrap(bb);
			sb.append(String.valueOf(set.decode(b)).trim());
			start = m.end();
		}
		start2 = str.length();
		if (start2 > start) {
			String seg = str.substring(start, start2);
			sb.append(seg);
		}
		return sb.toString();
	}

	/**
	 * 抽取匹配内容
	 * 
	 * @param str
	 * @param pattern
	 * @return
	 */
	public static String extractMulti(String str, String pattern) {
		Pattern p = Pattern.compile(pattern);
		Matcher m = p.matcher(str);
		StringBuffer sb = new StringBuffer();
		while (m.find()) {
			sb.append(m.group().trim());
		}
		return sb.toString();
	}
	/**
	 * 抽取匹配内容
	 *
	 * @param str
	 * @param pattern
	 * @return
	 */
	public static String extractOne(String str, String pattern) {
		Pattern p = Pattern.compile(pattern);
		Matcher m = p.matcher(str);
		StringBuffer sb = new StringBuffer();
		while (m.find()) {
			sb.append(m.group().trim());
			break;
		}
		return sb.toString();
	}
	/**
	 * 匹配起始和结束位置之间的内容
	 * 
	 * @param content
	 * @param start
	 * @param end
	 * @return
	 */
	public static String regMatcher(String content, String start, String end) {
		return regMatcher(content, start, end, true);
	}

	/**
	 * 匹配起始和结束位置之间的内容，是否贪婪匹配，默认为否
	 * 
	 * @param content
	 * @param start
	 * @param end
	 * @param is
	 * @return
	 */
	public static String regMatcher(String content, String start, String end, boolean is) {
		String mat = null;
		if (is) {
			mat = start + "([\\s\\S]+?)(\\s)?" + end;
		} else {
			mat = start + "([\\s\\S]+)(\\s)?" + end;
		}
		Pattern p = Pattern.compile(mat);
		Matcher m = p.matcher(content);
		if (m.find()) {
			return m.group(1);
		}
		return null;
	}


	public static boolean regFullMatch(String target, String pattern){
		Pattern p = Pattern.compile("^"+pattern+"$");
		Matcher m = p.matcher(target);
		return m.find();
	}


	/**
	 * 匹配起始和结束位置之间的内容，匹配全部，返回列表
	 * 
	 * @param content
	 * @param start
	 * @param end
	 * @param is
	 * @return
	 */
	@SuppressWarnings("static-access")
	public static List<String> regMatches(String content, String start, String end, boolean is) {
		String mat = null;
		if (is) {
			mat = start + "([\\s\\S]+?)(\\s)?" + end;
		} else {
			mat = start + "([\\s\\S]+)(\\s)?" + end;
		}
		Pattern p = Pattern.compile(mat);
		Matcher m = p.matcher(content);
		List<String> resutls = new ArrayList<String>();

		while (m.find()) {
			resutls.add(m.group(0));
			// LOGGER.info(m.group(0));
		}
		return resutls;
	}

	/**
	 * 格式化字符串
	 * 
	 * @param s
	 * @return
	 */
	public static String formatRetain1Space(String s) {
		String result = s;
		if (s != null && s.trim().length() > 0) {
			while (result.indexOf("\r") != -1)
				result = result.replaceAll("\r", " ");
			while (result.indexOf("\n") != -1)
				result = result.replaceAll("\n", " ");
			while (result.indexOf("\t") != -1)
				result = result.replaceAll("\t", " ");
			while (result.indexOf("  ") != -1)
				result = result.replaceAll("  ", " ");
			return result.trim();
		}
		return "";
	}

	/**
	 * 格式化字符串
	 * 
	 * @param s
	 * @return
	 */
	public static String format(String s) {
		String result = s;
		if (s != null && s.trim().length() > 0) {
			while (result.indexOf("\r") != -1)
				result = result.replaceAll("\r", "");
			while (result.indexOf("\n") != -1)
				result = result.replaceAll("\n", "");
			while (result.indexOf("\t") != -1)
				result = result.replaceAll("\t", "");
			while (result.indexOf("  ") != -1)
				result = result.replaceAll("  ", " ");
			while (result.indexOf("　") != -1)
				result = result.replaceAll("　", "");
			return result.trim();
		}
		return "";
	}

	/**
	 * 判断是否有中文字符
	 * 
	 * @param str
	 * @return
	 */
	public static boolean haveChinese(String str) {
		boolean judge = false;
		if (str == null || str.equals(""))
			return judge;
		char[] ch = str.toCharArray();
		for (int i = 0; i < ch.length; i++) {
			if (isChinese(ch[i])) {
				judge = true;
				break;
			}
		}
		ch = null;
		return judge;
	}

	private static boolean isChinese(char c) {
		Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
		if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
				|| ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION
				|| ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {
			return true;
		}
		return false;
	}

	public static String getContent(String filename, String encode) {
		String s = "";
		StringBuffer sb = new StringBuffer();
		java.io.BufferedReader br = null;
		java.io.FileInputStream fis = null;
		java.io.InputStreamReader in = null;
		try {
			fis = new java.io.FileInputStream(filename);
			in = new java.io.InputStreamReader(fis, encode);
			br = new java.io.BufferedReader(in);
			while ((s = br.readLine()) != null)
				sb.append(s).append("\r\n");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					br = null;
				}
			}
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					in = null;
				}
			}
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
					fis = null;
				}
			}
		}
		return sb.toString();
	}

	public static String getContent(String filename) {
		String s = "";
		StringBuffer sb = new StringBuffer();
		java.io.BufferedReader br = null;
		try {
			br = new java.io.BufferedReader(new java.io.FileReader(new java.io.File(filename)));
			while ((s = br.readLine()) != null)
				sb.append(s).append("\r\n");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					br = null;
				}
			}
		}
		return sb.toString();
	}

	public static List<String> contentList(String filename) {
		String s = "";
		List<String> sb = new ArrayList<String>();
		java.io.BufferedReader br = null;
		try {
			br = new java.io.BufferedReader(new java.io.FileReader(new java.io.File(filename)));
			while ((s = br.readLine()) != null)
				sb.add(s);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					br = null;
				}
			}
		}
		return sb;
	}

	public static void writeFile(String filename, String content) {
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(filename);
			fos.write(content.getBytes("utf-8"));
		} catch (FileNotFoundException e) {
			if (filename.indexOf(File.separator) > -1) {
				File f = new File(filename.substring(0, filename.lastIndexOf(File.separator)));
				if (!f.exists()) {
					f.mkdirs();
				}
				writeFile(filename, content, "utf-8");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static void writeFile(String filename, String content, String encode) {
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(filename);
			fos.write(content.getBytes(encode));
		} catch (FileNotFoundException e) {
			if (filename.indexOf(File.separator) > -1) {
				File f = new File(filename.substring(0, filename.lastIndexOf(File.separator)));
				if (!f.exists()) {
					f.mkdirs();
				}
				writeFile(filename, content, encode);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static String getHost(String url) {
		if (url == null) {
			return null;
		}
		url = url.replaceAll("^https?://", "").replaceAll("([^/]+).*", "$1");
		return url;
	}

	public static String getContent(File f) {
		String s = "";
		StringBuffer sb = new StringBuffer();
		java.io.BufferedReader br = null;
		try {
			br = new java.io.BufferedReader(new java.io.FileReader(f));
			while ((s = br.readLine()) != null)
				sb.append(s).append("\r\n");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					br = null;
				}
			}
		}
		return sb.toString();
	}

	public static void writeFile(String filename, byte[] bytes) {
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(filename);
			fos.write(bytes);
		} catch (FileNotFoundException e) {
			if (filename.indexOf(File.separator) > -1) {
				File f = new File(filename.substring(0, filename.lastIndexOf(File.separator)));
				if (!f.exists()) {
					f.mkdirs();
				}
				writeFile(filename, bytes);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * please input
	 * @param str
	 * @return
	 */
	public static String upperFirstLetter(String str){
		char[] cs = str.toCharArray();
		cs[0]-=32;
		return String.valueOf(cs);
	}


}
