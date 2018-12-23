package s;

import java.util.HashMap;

import org.apache.log4j.Logger;

public class App {
	public static void main(final String[] args) {
		String path=mb.Util.resource("../../config/js");
		final mb.JSBridge bridge=new mb.JSBridge(path,false) {
			@Override
			protected void param(HashMap<String,Object> ini){
				ini.put("bridge", this);
				//ini.put("package", true);
			}
		};
		Logger log=Logger.getLogger("js");
		if(args.length!=0) {
			//执行文件
			String args_0=args[0];
			if(args.length==1) {
				//执行lisp脚本文件
				HashMap<String,Object> req=new HashMap<String,Object>();
				req.put("args", args_0);
				bridge.run_map(req, "run",log);
			}else {
				if("exec".equals(args_0))
				{
					String args_1=args[1];
					if("reload".equals(args_1)) {
						//重新加载
						bridge.run_map(null, "", log);
					}else
					if("js".equals(args_1)) {
						//执行js-shell
						bridge.run_map(null, "js/shell", log);
					}
				}else
				if("js".equals(args_0)) {
					//执行js文件
					String args_1=args[1];
					HashMap<String,Object> req=new HashMap<String,Object>();
					req.put("args", args_1);
					bridge.run_map(req, "js/run", log);
				}
			}
		}else {
			//S-Lisp交互
			bridge.run_map(null, "shell",log);
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
