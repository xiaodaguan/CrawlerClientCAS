package test;

import java.io.File;
import java.io.IOException;

public class testFile {
	
	
	public static void main(String[] args) throws IOException {
		
//		File file = new File("123");
//		
//		//file.mkdir();
//		
//		
//		if(file.isDirectory()){
//			String []sublistfile = file.list();
//			for (String string : sublistfile) {
//				System.out.println(file.getCanonicalPath()
//						+File.separator+string);
//			}
//		}
//		file.delete();
		deleteFile("123");
//		while(true){
//			String path = "123";
//			File file = new File(path);
//			
//			if(file.exists()){
//				deleteFile(path);
//			}else{
//				break;
//			}
//		}
	}
	
	
	public static void deleteFile(String path){
		File file = new File(path);
		while(file.exists())
		{
			if(file.isDirectory()){
				String []sublistfile = file.list();
				for (String s : sublistfile) {
					String currFile = file.getAbsolutePath()
							+File.separator+s;
					
					System.out.println(currFile);
					deleteFile(currFile);
				}
			}
			file.delete();
		}
	}
}
