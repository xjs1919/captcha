package com.github.xjs.captcha.util;

import java.io.*;
import java.nio.file.Files;

public class IOUtil {
	
	public static void closeQuietly(Closeable... closeables){
		if(closeables == null || closeables.length <= 0){
			return;
		}
		for(Closeable closeable : closeables){
			if(closeable != null){
				try{
					closeable.close();
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		}
	}
	
	public static byte[] readBytes(InputStream in, int totalSize)  {
        byte[] bytes = new byte[totalSize];  
        int leftSize = totalSize;  
        int readSize = 0;  
        int index = 0;// start from zero 
        try{
            while ((readSize = in.read(bytes, index, leftSize)) != -1) {  
            	leftSize -= readSize;  
                if (leftSize <= 0) {  
                    break;  
                }  
                index = index + readSize;  
            }  
        }catch(Exception e){
        	e.printStackTrace();
        }finally{
        	closeQuietly(in);
        }
        return bytes;  
    }  
	
	public static byte[] readInputStream(InputStream in) {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try{
			int len = 0;
			byte[] buff = new byte[10*1024];
			while((len = in.read(buff)) != -1){
				out.write(buff, 0 ,len);
			}
		}catch(IOException e){
			e.printStackTrace();
		}finally{
			closeQuietly(in, out);
		}
		return out.toByteArray();
	}
	
	public static void saveBytes(byte[] in, OutputStream out) throws IOException{
		saveInputStream(new ByteArrayInputStream(in), out);
	}
	
	public static void saveInputStream(InputStream in, OutputStream out) throws IOException{
		byte[] buff = new byte[1024 * 10];
		int len = 0;
		while((len = in.read(buff)) >= 0){
			out.write(buff, 0, len);
		}
		closeQuietly(in, out);
	}
	
	public static void saveInputStream(InputStream in, File file) throws IOException{
		FileOutputStream fout = new FileOutputStream(file);
		saveInputStream(in, fout);
	}

	public static byte[] getFileBytes(File file){
		try{
			return Files.readAllBytes(file.toPath());
		}catch(Exception e){
			throw new RuntimeException("读取文件出错："+file.getAbsolutePath(), e);
		}
	}


}
