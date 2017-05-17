package test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;

public class CharEncodingExchange {

	private final static String SOURCE_ENCODING = "GB2312";

	private final static String TARGET_ENCODING = "UTF-8";

	private static String SOURCE_DIR = "E:\\gxdws\\LabReport(sci)";

	private static String TARGET_DIR = "e://tmp";

	/**
	 * 
	 * @param args
	 */

	public static void main(String[] args) {

		try {

			exchange(SOURCE_DIR);

		} catch (Exception e) {

			e.printStackTrace();

		}

	}

	/**
	 * 
	 * exchange the character encoding from srcDir to targetDir
	 * 
	 *
	 * 
	 * @param srcDir
	 * 
	 * @param targetDir
	 */

	public static void exchange(String srcDir) {

		String absPath = "";

		if (!srcDir.equals(SOURCE_DIR)) {

			absPath = srcDir.substring(SOURCE_DIR.length());

			String targetDir = TARGET_DIR + absPath;

			File targetDirectory = new File(targetDir);

			if (targetDirectory.isDirectory() && !targetDirectory.exists()) {

				targetDirectory.mkdirs();

			}

		}

		File sourceDirectory = new File(srcDir);

		if (sourceDirectory.exists()) {

			if (sourceDirectory.isFile()) {

				String targetFilePath = TARGET_DIR + absPath;

				try {

					fileEncodingExchange(sourceDirectory, targetFilePath);

				} catch (IOException e) {

					// TODO Auto-generated catch block

					e.printStackTrace();

				}

			} else {

				File[] childs = sourceDirectory.listFiles();

				for (File child : childs)

					exchange(child.getPath());

			}

		}

	}

	private static void fileEncodingExchange(File infile,

	String targetAbsFilePath) throws IOException {

		FileInputStream fin = null;

		FileOutputStream fout = null;

		FileChannel fcin = null;

		FileChannel fcout = null;

		System.out.println(infile + " " + targetAbsFilePath);

		String tmpTargetPath = targetAbsFilePath.substring(0, targetAbsFilePath

		.lastIndexOf(File.separator));

		File tmpTargetDir = new File(tmpTargetPath);

		if (!tmpTargetDir.exists())

			tmpTargetDir.mkdirs();

		try {

			fin = new FileInputStream(infile);

			fout = new FileOutputStream(targetAbsFilePath);

			fcin = fin.getChannel();

			fcout = fout.getChannel();

			ByteBuffer buffer = ByteBuffer.allocateDirect(1024);

			while (true) {

				buffer.clear();

				int r = fcin.read(buffer);

				if (r == -1) {

					break;

				}

				buffer.flip();

				String encoding = System.getProperty("file.encoding");

				fcout.write(ByteBuffer.wrap(Charset.forName(encoding).decode(

				buffer).toString().getBytes(TARGET_ENCODING)));

			}

		} catch (FileNotFoundException e) {

			// TODO Auto-generated catch block

			e.printStackTrace();

		} catch (IOException e) {

			// TODO Auto-generated catch block

			e.printStackTrace();

		} finally {

			if (fin != null) {

				fin.close();

			}

			if (fcin != null) {

				fcin.close();

			}

			if (fout != null)

				fout.close();

			if (fcout != null)

				fcout.close();

		}

	}

}