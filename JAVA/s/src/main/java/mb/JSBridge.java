package mb;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;

import javax.script.Bindings;
import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
public class JSBridge {
    final ScriptEngine engine;
    final String server_path;
    final boolean first_from_cache;
    final File jsx_file;
    
    JSMethod method;/*编译后的方法*/
    boolean first_run=true;//编译事件
    /**
     * @param path 最后不需要有分割符
     * @param from_cache
     */
    public JSBridge(String path,boolean from_cache){
    	if(path==null) {
    		path="";
    	}
    	path=path.replace('\\', '/');
    	if(!path.endsWith("/")) {
    		path=path+"/";
    	}
    	
    	server_path=path;
    	first_from_cache=from_cache;
    	jsx_file=new File(server_path+"/out.jsx");
    	/*engine*/
        ScriptEngineManager manager=new ScriptEngineManager();
		engine=manager.getEngineByName("JavaScript");
        System.out.println(engine.getFactory().getEngineName()); 
        /*主动、第一次生成方法*/
        reloadMethod();
    }
    public HashMap<String,Object> run_map(HashMap<String,Object> request,String act,Logger log){
        if(request==null) {
        	request=new HashMap<String,Object>();
        }
        if(act==null) {
        	act="";
        }
        HashMap<String,Object> response=new HashMap<String,Object>();
        if("".equals(act)) {
        	reloadMethod();
            response.put("code", 0);
            response.put("description","刷新成功");
        	return response;
        }else {
            response.put("code", 0);
            response.put("description","操作成功");
            HashMap<String,Object> map=new HashMap<String,Object>();
            map.put("act", act);
            map.put("log", log);
            map.put("request", request);
            map.put("response", response);
            map.put("type", "map");
            try {
            	getMethod().run(map);
            }catch(Throwable e) {
        		String err=Util.loadAllErr(e);
                response.put("code", -2);
                response.put("description",err);
                // TODO Auto-generated catch block
                e.printStackTrace();
                log.error(err);
            }
            return response;
        }
    }
    protected void param(HashMap<String,Object> ini){
    	/*
    	 */
    }
    Bindings scriptParamsFromIni() {
        HashMap<String,Object> ini=new HashMap<String,Object>();
        ini.put("server_path",server_path);
        ini.put("engine_name", engine.getFactory().getEngineName());
        ini.put("me",new Helper(this));
    	ini.put("jsx_path",jsx_file.getPath());
        param(ini);
        Bindings scriptParams = engine.createBindings();
        scriptParams.put("ini", ini);
        return scriptParams;
    }
    
    protected JSMethod getMethod() throws Exception {
    	if(method==null) {
    		reloadMethod();
    	}
    	if(method==null) {
    		throw new Exception("生成method失败");
    	}else {
    		return method;
    	}
    }
    protected void reloadMethod(){
        //构造初始化
        Bindings bindings=scriptParamsFromIni();
    	StringBuilder msg=new StringBuilder();
    	String content="";
    	if(first_run){
    		first_run=false;
        	msg.append("第一次:");
        	if(jsx_file.exists() && first_from_cache) {
        		content=load_from_jsx(msg);
        	}else {
        		content=load_from_package(bindings,msg);
        	}
    	}else {
    		msg.append("手动：");
    		content=load_from_package(bindings,msg);
    	}
    	if(!"".equals(content)) {
    		method=load_cScript(content,bindings,msg);
    	}else {
    		msg.append("加载method失败");
    	}
        System.out.println(msg.toString());
    }
    
    String load_from_package(Bindings scriptParams,StringBuilder msg){
		try {
			String txt = Util.readTxt(server_path+"/mb/"+"lib.js","\r\n","UTF-8");
	        try {
				engine.eval(txt,scriptParams);
				try {
			        String content=engine.eval("mb.compile.save();",scriptParams).toString();
			        msg.append("重新打包");
			        return content;
				}catch(ScriptException e) {
					e.printStackTrace();
					msg.append("执行mb.compile.save失败");
				}
			} catch (ScriptException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				msg.append("执行lib.js文件失败");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			msg.append("读取lib.js文件失败");
		}
		return null;
    }
    String load_from_jsx(StringBuilder msg){
		try {
			String content= Util.readTxt(jsx_file, "\r\n", "UTF-8");
	        msg.append("加载jsx");
	        return content;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			msg.append(jsx_file.getPath()+"文件不存在");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			msg.append("读取"+jsx_file.getPath()+"失败");
		}
		return null;
    }
    JSMethod load_cScript(String content,Bindings scriptParams,StringBuilder msg){
        Compilable comp = (Compilable) engine;
        
		try {
			CompiledScript cScript = comp.compile(content);
			try {
		        JSMethod method=(JSMethod)(cScript.eval(scriptParams));
		        msg.append(":");
		        msg.append(server_path);
		        return method;
			}catch (ScriptException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				msg.append("执行编译后脚本失败!");
			}
		} catch (ScriptException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			msg.append("编译脚本失败!");
		}
		return null;
    }
    public static interface JSMethod{
    	void run(HashMap<String,Object> map);
    }
    /*可重写*/
	public Logger getLogger(String name) {
		return Logger.DefaultLogger.getLogger(name);
	}
    public static class Helper{
		private final JSBridge jsBridge;
		public Helper(JSBridge jsBridge) {
			this.jsBridge=jsBridge;
		}

		public Logger getLogger(String name){
			return jsBridge.getLogger(name);
		}
		public Character charAt(String string, int index) {
    		return string.charAt(index);
    	}
        public File fileFromPath(String path){
            return new File(path);
        }
        public void saveText(String path,String content){
            try{
            	mb.Util.saveTxt(path, content, "UTF-8");
            }catch(Exception ex){
                ex.printStackTrace();
                System.out.println("保存"+path+"出错:"+ex.getMessage());
            }
        }
        public String readTxt(File file){
            return readTxt(file.getPath());
        }
        public String readTxt(String path )
        {
            try {
                return Util.readTxt(path, "\r\n","UTF-8");
            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
                System.out.println("加载"+path+"出错:"+e1.getMessage());
                return null;
            }
        }
    }
}
