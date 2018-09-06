package mb;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;

import org.apache.log4j.Logger;


public class Util {
	protected static Logger log = Logger.getLogger(Util.class.getName());
	public static void main(String[] arg)
	{	
		//http://ydlmlh.iteye.com/blog/1262068
		/***
		 * 使用Util.resource_path,因为有空格转%20等问题
		 */
		System.out.println("1:" + Thread.currentThread().getContextClassLoader().getResource(""));		// Class包所在路径，得到的是URL对象，用url.getPath()获取绝对路径String
		System.out.println("2:" + Util.class.getClassLoader().getResource(""));					// Class包所在路径，得到的是URL对象，用url.getPath()获取绝对路径String
		System.out.println("3:" + ClassLoader.getSystemResource(""));									// Class包所在路径，得到的是URL对象，用url.getPath()获取绝对路径String
		System.out.println("4:" + Util.class.getResource(""));									//ParamsConfig.class文件所在路径，用url.getPath()获取绝对路径String
		System.out.println("5:" + Util.class.getResource("/")); 								// Class包所在路径，得到的是URL对象，用url.getPath()获取绝对路径String
		System.out.println("6:" + new File("/").getAbsolutePath());										//根路径，如D:/
		System.out.println("7:" + System.getProperty("user.dir"));										//项目目录
		System.out.println("8:" + System.getProperty("file.encoding"));									//获取文件编码
	}
	static String _resource_path=null;
	static String _runPath_;
	public static String run_path() {
		if(_runPath_==null) {
	        URL url = Util.class.getProtectionDomain().getCodeSource().getLocation();
	        _runPath_ = "";  
	        try {  
	            _runPath_ = URLDecoder.decode(url.getPath(), "utf-8");// 转化为utf-8编码  
	        } catch (Exception e) {  
	            e.printStackTrace();  
	        }
		}
		return _runPath_;
	}
	public static boolean is_in_jar() {
		File f=new File(run_path());
		return f.isFile();
	}
	
	public static InputStream resource_stream(String path) {
		if(path==null) {
			path="/";
		}
		if(!path.startsWith("/")) {
			path="/"+path;
		}
		InputStream in = Util.class.getResourceAsStream(path);
		return in;
	}
	/**
	 * 不支持jar内啊
	 * @param path
	 * @return
	 */
	public static String resource(String path){
		if(path==null){
			path="";
		}
		if(_resource_path==null){
			if(is_in_jar()) {
				_resource_path=run_path();
			}else {
				URL url=Util.class.getClassLoader().getResource("");
				try {
					_resource_path = URLDecoder.decode(url.getPath(), "utf-8");
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					_resource_path=url.getPath().replace("%20", " ");//只转空格
				}
			}
		}
		return _resource_path+File.separator+path;
	}
    
	/**
	 * 路径
	 * @param path
	 * @param charsetName 如UTF-8
	 * @return
	 * @throws IOException
	 */
	public static String readTxt(String path,String lineSplit,String charsetName) throws IOException{
	    return readTxt(new FileInputStream(path),lineSplit,charsetName);
	}
	/**
	 * 文件
	 * @param file
	 * @param charsetName
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static String readTxt(File file,String lineSplit,String charsetName) throws FileNotFoundException, IOException{
	    return readTxt(new FileInputStream(file),lineSplit,charsetName);
	}
	
	public static void saveTxt(String path,String content,String charset) throws IOException {
        FileOutputStream fout=new FileOutputStream(path);
        fout.write(content.getBytes(charset));
        fout.flush();
        fout.close();
	}
	/**
	 * InputStream，如ftp返回的
	 * @param is
	 * @param charsetName
	 * @return
	 * @throws IOException
	 */
	public static String readTxt(InputStream is,String lineSplit,String charsetName) throws IOException{
        StringBuilder sb=new StringBuilder();
        BufferedReader br = new BufferedReader(new InputStreamReader(is, charsetName));  
        String line = null;  
        while( ( line = br.readLine() ) != null ){
            sb.append(line).append(lineSplit);  
        }
        br.close(); 
        return sb.toString();
	}
    public static String loadAllErr(Throwable e){
        StringWriter sw = null;
        PrintWriter pw = null;
        try {
            sw = new StringWriter();
            pw =  new PrintWriter(sw);
            //将出错的栈信息输出到printWriter中
            e.printStackTrace(pw);
            pw.flush();
            sw.flush();
        } finally {
            if (sw != null) {
                try {
                    sw.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            if (pw != null) {
                pw.close();
            }
        }
        return sw.toString();
    }
    
	public static boolean deleteDir(File dir) {
		log.warn("删除文件(夹):"+dir.getAbsolutePath());
		return deleteDirRe(dir);
	}
	private static boolean deleteDirRe(File dir){
		if (dir.isDirectory()) {
			String[] children = dir.list();
			// 递归删除目录中的子目录下
			for (int i = 0; i < children.length; i++) {
				boolean success = deleteDirRe(new File(dir, children[i]));
				if (!success) {
					return false;
				}
			}
		}
		// 目录此时为空，可以删除
		return dir.delete();
	}
	
	public static <T> T kvs_find1st(T[] kvs,T key) {
		T x=null;
		int i=0;
		while(i<kvs.length) {
			T k=kvs[i];
			i++;
			T v=kvs[i];
			i++;
			if(k.equals(key)) {
				x=v;
			}
		}
		return x;
	}
	public static String string_from_trans(String s,Character end,Character[] kvs,int trans_time) throws Exception {
		int i=0,size=s.length();
		StringBuilder sb=new StringBuilder();
		while(i<size) {
			Character c=s.charAt(i);
			if(c=='\\') {
				i++;
				c=s.charAt(i);
				if(c=='\\') {
					sb.append(c);
				}else
				if(c==end) {
					sb.append(end);
				}else {
		    		Character x=mb.Util.kvs_find1st(kvs,c);
		    		if(x!=null) {
		    			sb.append(x);
		    		}else {
		    			throw new Exception("非法转义字符"+c+"在字符串:"+s);
		    		}
				}
			}else {
				sb.append(c);
			}
			i++;
		}
		return sb.toString();
	}
    public static String string_to_trans(String v,Character start,Character end,Character[] trans) {
    	StringBuilder sb=new StringBuilder();
    	sb.append(start);
    	int len=v.length();
    	int i=0;
    	while(i<len) {
    		char c=v.charAt(i);
    		if(c=='\\') {
    			sb.append("\\\\");
    		}else
    		if(c==end) {
    			sb.append("\\").append(end);
    		}else
    		{
    			if(trans!=null) {
        			Character x=mb.Util.kvs_find1st(trans,c);
        			if(x!=null) {
        				sb.append("\\").append(x);
        			}else {
        				sb.append(c);
        			}
    			}else {
    				sb.append(c);
    			}
    		}
    		i++;
    	}
    	sb.append(end);
    	return sb.toString();
    }
}
