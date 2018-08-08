package s;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import javax.script.Bindings;
import javax.script.Compilable;
import javax.script.ScriptException;

import org.apache.log4j.Logger;

import mb.Util;

public class App {
	public static void main(final String[] args) {
		String path=mb.Util.resource("../../config/js");
		final File jsx=new File(path+"/out.jsx");
		final mb.JSBridge bridge=new mb.JSBridge(path) {
			@Override
			protected void param(HashMap<String,Object> ini){
				ini.put("bridge", this);
				//ini.put("package", true);
			}
		    public HashMap<String,Object> run_map(HashMap<String,String> request,String act,Logger log){
		    	if("".equals(act)) {
		    		jsx.delete();
		    	}
		    	return super.run_map(request, act, log);
		    }
			@Override
			protected void run(boolean reload,HashMap<String,Object> ini) throws ScriptException, IOException {	
				if(jsx.exists())
				{
					Bindings scriptParams = engine.createBindings();
			        scriptParams.put("ini", ini);
			        Compilable comp = (Compilable) engine;
			        String content=Util.readTxt(jsx, "\r\n","UTF-8");
			        cScript = comp.compile(content);
			        cScript.eval(scriptParams);
				}else
				{
					super.run(reload, ini);
				}
			}
		};
		Logger log=Logger.getLogger("js");
		if(args.length!=0) {
			//执行文件
			String args_0=args[0];
			if(args.length==1) {
				//执行lisp脚本文件
				HashMap<String,String> req=new HashMap<String,String>();
				req.put("args", args_0);
				bridge.run_map(req, "index",log);
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
					HashMap<String,String> req=new HashMap<String,String>();
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
