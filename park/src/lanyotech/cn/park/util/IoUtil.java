package lanyotech.cn.park.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.jar.JarOutputStream;

import android.util.Log;

public class IoUtil {
	public static String readStringFromFile(File file) throws Exception{
		if(file.exists()){
			FileInputStream fis=new FileInputStream(file);
			ByteArrayOutputStream baos=new ByteArrayOutputStream();
			int len;
			byte[] data=new byte[1024];
			while((len=fis.read(data))>0){
				baos.write(data, 0, len);
			}
			closeIo(fis);
			closeIo(baos);
			return baos.toString();
		}
            throw new RuntimeException("文件不存在！");
	}
	
	public static String readStringFromInput(InputStream input) throws Exception{
		if(input!=null){
			ByteArrayOutputStream baos=new ByteArrayOutputStream();
			int len;
			byte[] data=new byte[1024];
			while((len=input.read(data))>0){
				baos.write(data, 0, len);
			}
			closeIo(input);
			closeIo(baos);
			return baos.toString();
		}
		throw new RuntimeException("文件不存在！");
	}
	
	public static void writeStringToFile(File file,String content) throws Exception{
		if(!file.exists()){
			if(!file.createNewFile()){
				throw new RuntimeException("文件地址非法，不能创建文件！");
			}
		}
		FileOutputStream fos=new FileOutputStream(file);
		byte[] data=content.getBytes();
		fos.write(data);
		closeIo(fos);
	//	throw new RuntimeException("文件不存在！");
	}
	
	public static File saveFile(InputStream source,String filePath){
		if(source!=null){
			File file=new File(filePath);
			if(!file.exists()){
				if(!file.getParentFile().exists()){
					if(!file.getParentFile().mkdirs()){
						return null;
					}
				}
			}
			FileOutputStream fos=null;
			try {
				fos=new FileOutputStream(file);
				byte[] data=new byte[1024];
				int len=-1;
				while((len=source.read(data))>0){
					fos.write(data, 0, len);
				}
				return file;
			} catch (IOException e) {
				e.printStackTrace();
			}finally{
				closeIo(fos);
				closeIo(source);
			}
		}
		return null;
	}
	
	public static Object readObjectFromFile(File file) throws Exception{
		if(file.exists()){
			ObjectInputStream ois=null;
			try {
				ois=new ObjectInputStream(new FileInputStream(file));
				return ois.readObject();
			} catch (IOException e) {
				e.printStackTrace();
			}finally{
				closeIo(ois);
			}
			
		}
	throw new RuntimeException("文件不存在！");
	}
	
	public static File writeObjectToFile(Object object,String filePath){
		if(object!=null){
			File file=new File(filePath);
			if(!file.exists()){
				if(!file.getParentFile().exists()){
					if(!file.getParentFile().mkdirs()){
						return null;
					}
				}
			}
			ObjectOutputStream oos=null;
			try {
				oos=new ObjectOutputStream(new FileOutputStream(file));
				oos.writeObject(object);
				return file;
			} catch (IOException e) {
				e.printStackTrace();
			}finally{
				closeIo(oos);
			}
		}
		return null;
	}
	
	
	public static void closeIo(InputStream inputStream){
		if(inputStream!=null){
			try {
				inputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void closeIo(OutputStream outStream){
		if(outStream!=null){
			try {
				outStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	
}
