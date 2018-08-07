package s;

import java.util.HashMap;
import java.util.Scanner;

import org.apache.log4j.Logger;

public class App {
	public static void main(final String[] args) {
		String path=mb.Util.resource("../../config/js");
		final mb.JSBridge bridge=new mb.JSBridge(path) {
			protected void param(HashMap<String,Object> ini){
				ini.put("bridge", this);
				//ini.put("package", true);
			}
		};
		if(args.length!=0) {
			//执行文件
			HashMap<String,String> req=new HashMap<String,String>();
			req.put("args", args[0]);
			bridge.run_map(req, "index", Logger.getLogger("js"));
		}else {
			//交互
			bridge.run_map(null, "shell", Logger.getLogger("js"));
		}
		/*
        Scanner in = new Scanner(System.in);
        System.out.println("What's your name?");
        String name = in.nextLine();//读取换行符为间隔的
        System.out.println("Hello "+name+".");
        System.out.println("What's your name?");
        System.out.println("Hello "+in.next()+in.next()+".");   //读取空格为间隔的
        System.out.println("How old are you?");
        int age = in.nextInt(); //读取数字
        System.out.println("Are you  "+age+"?");
        */
		for(int i=0;i<args.length;i++) {
			System.out.println(i);
			System.out.println(args[i]);
		}
	}
}
