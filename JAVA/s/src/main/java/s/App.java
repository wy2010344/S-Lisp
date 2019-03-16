package s;

import mb.JSBridge;
import mb.Logger;

import java.util.HashMap;

public class App {
	public static void main(String[] args) {
		for(int i=0;i<args.length;i++) {
			System.out.println(i);
			System.out.println(args[i]);
		}
		//args=new String[]{"exec","smeta"};
		String path=mb.Util.resource("../../config/js/",App.class);
		final mb.JSBridge bridge=new mb.JSBridge(path,true) {
			@Override
			protected void param(HashMap<String,Object> ini){
				ini.put("bridge", this);
				ini.put("package", true);
			}
		};
		Logger log=Logger.DefaultLogger.getLogger("js");

		HashMap<String,Object> res=run(bridge,args,log);//run_lisp_file(bridge,"../a.lisp",log);
		System.out.println("退出成功");
		if(res!=null) {
			System.out.println(res.get("code")+":"+res.get("description"));
		}
	}
	
	static HashMap<String,Object> run_lisp_file(mb.JSBridge bridge, String file, Logger log,String[] args){
		HashMap<String,Object> req=new HashMap<String,Object>();
		if (file.charAt(0)=='.') {
			file=mb.Util.resource(file, App.class);
		}
		req.put("shell", file);
		req.put("args",Node.list(args));
		return bridge.run_map(req, "run",log);
	}
	static HashMap<String,Object> run(mb.JSBridge bridge, String[] args, Logger log) {
		if(args.length==0) {
			//S-Lisp交互
			return bridge.run_map(null, "shell",log);
		}else{
			//执行文件
			String args_0=args[0];
			if(args.length==1) {
				//执行lisp脚本文件
				return run_lisp_file(bridge,args_0,log,args);
			}else {
				if("exec".equals(args_0))
				{
					String args_1=args[1];
					if("reload".equals(args_1)) {
						//重新加载
						return bridge.run_map(null, "", log);
					}else
					if("js".equals(args_1)) {
						//执行js-shell
						return bridge.run_map(null, "js/shell", log);
					}else if("smeta".equals(args_1)){
						return bridge.run_map(null,"smeta/shell",log);
					}
				}else
				if("js".equals(args_0)) {
					//执行js文件
					String args_1=args[1];
					HashMap<String,Object> req=new HashMap<String,Object>();
					req.put("args",mb.Util.resource(args_1,App.class));
					return bridge.run_map(req, "js/run", log);
				}else {
					return run_lisp_file(bridge,args_0,log,args);
				}
			}
		}
		return null;
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
	}
}
