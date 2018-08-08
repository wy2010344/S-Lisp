package mb;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;

import javax.script.Bindings;
import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.apache.log4j.Logger;

public class JSBridge {
    protected static ScriptEngine engine;
    protected static CompiledScript cScript=null;
    static boolean first_run=true;//编译事件
    static String server_path;
    
    protected HashMap<String,Object> init(){
        if(engine==null){
            ScriptEngineManager manager=new ScriptEngineManager();
            engine = manager.getEngineByName("nashorn");
    		if(engine == null){
    			System.out.println("未找到引擎nashron");
    			engine=manager.getEngineByName("JavaScript");
    		}
            System.out.println(engine.getFactory().getEngineName()); 
        }
        HashMap<String,Object> ini=new HashMap<String,Object>();
        ini.put("server_path",server_path);
        ini.put("file_sp",File.separator);
        ini.put("engine_name", engine.getFactory().getEngineName());
        ini.put("me",new Helper());
        return ini;
    }
    public JSBridge(String path){
    	server_path=path;
    }
    public HashMap<String,Object> run_map(HashMap<String,String> request,String act,Logger log){
        //构造初始化
        HashMap<String,Object> ini=init();
        if(request==null) {
        	request=new HashMap<String,String>();
        }
        if(act==null) {
        	act="";
        }
        HashMap<String,Object> response=new HashMap<String,Object>();
        try {
            ini.put("log", log);
            ini.put("act", act);
            ini.put("request",request);
            ini.put("response",response);
            param(ini);
            run("".equals(act),ini);
            if("".equals(act)) {
                response.put("code", 0);
                response.put("description","刷新成功");
            }
        } catch (Exception e) {
            response.put("code", -2);
            response.put("description",Util.loadAllErr(e));
            // TODO Auto-generated catch block
            e.printStackTrace();
            log.error(Util.loadAllErr(e));
        }
		return response;
    }
    protected void run(boolean reload,HashMap<String,Object> ini) throws ScriptException, IOException {
        Bindings scriptParams = engine.createBindings();
        scriptParams.put("ini", ini);
        if(reload || first_run) {
        	try {
				reload(scriptParams);
			} catch (ScriptException e) {
				// TODO Auto-generated catch block
                System.out.println("编译时出的错");
				throw e;
			} catch (IOException e) {
				// TODO Auto-generated catch block
                System.out.println("编译时出的错");
				throw e;
			}
        }
        if(cScript!=null){
            try {
				cScript.eval(scriptParams);
			} catch (ScriptException e) {
				// TODO Auto-generated catch block
	            System.out.println("执行时时出的错");
				throw e;
			}
        }
    }
    protected void param(HashMap<String,Object> ini){
    	/*
    	 */
    }
    static void reload(Bindings scriptParams) throws ScriptException, IOException {
        //作用域保证并发线程不冲突。
        engine.eval(Util.readTxt(server_path+File.separator+"mb"+File.separator+"lib.js","\r\n","UTF-8"),scriptParams);
        String content=engine.eval("mb.compile.save();",scriptParams).toString();
        Compilable comp = (Compilable) engine;
        cScript = comp.compile(content);
        if(first_run){
            System.out.print("reload:第一次"+server_path);
        }else {
            System.out.print("reload:手动"+server_path);
        }
        System.out.println();
        first_run=false;
    }
    public static class Helper{
        public void saveText(String content,String path){
            try{
                FileOutputStream fout=new FileOutputStream(path);
                fout.write((new java.lang.String(content).getBytes()));
                fout.flush();
                fout.close();
            }catch(Exception ex){
                ex.printStackTrace();
            }
        }
        public File fileFromPath(String path){
            return new File(path);
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
                return "";
            }
        }
        public Logger getLogger(String name) {
        	return Logger.getLogger(name);
        }
    }
}
