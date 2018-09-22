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

import org.apache.log4j.Logger;

public class JSBridge {
    protected static ScriptEngine engine;
    protected static CompiledScript cScript=null;
    static boolean first_run=true;//编译事件
    static String server_path;
    static boolean first_from_cache;
    static String jsx_path;
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
    	ini.put("jsx_path",jsx_path);
        return ini;
    }
    public JSBridge(String path,boolean from_cache){
    	server_path=path;
    	first_from_cache=from_cache;
    	jsx_path=server_path+"/out.jsx";
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
            
            Bindings scriptParams=scriptParamsFromIni(ini);
            if("".equals(act)) {
            	//手动刷新，重新加载
            	StringBuilder msg=new StringBuilder("手动:");
            	load_from_package(scriptParams,msg);
            	
                response.put("code", 0);
                response.put("description","刷新成功");
            }else {
            	//执行具体方法
            	run(scriptParams);
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
    protected void param(HashMap<String,Object> ini){
    	/*
    	 */
    }
    protected Bindings scriptParamsFromIni(HashMap<String,Object> ini) {
        param(ini);
        Bindings scriptParams = engine.createBindings();
        scriptParams.put("ini", ini);
        return scriptParams;
    }
    /**
     * 执行具体方法
     * @param scriptParams
     * @throws FileNotFoundException
     * @throws IOException
     * @throws ScriptException
     */
    protected void run(Bindings scriptParams) throws FileNotFoundException, IOException, ScriptException {
    	if(first_run) {
        	//第一次
        	first_run=false;
        	StringBuilder msg=new StringBuilder("第一次:");
        	File jsx=new File(jsx_path);
        	if(jsx.exists() && first_from_cache) {
        		load_from_jsx(jsx,msg);
        	}else {
        		load_from_package(scriptParams,msg);
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
    
    static void load_from_package(Bindings scriptParams,StringBuilder msg) throws ScriptException, IOException {
        engine.eval(Util.readTxt(server_path+File.separator+"mb"+File.separator+"lib.js","\r\n","UTF-8"),scriptParams);
        String content=engine.eval("mb.compile.save();",scriptParams).toString();

        msg.append("重新打包");
        load_cScript(content,msg);
    }
    static void load_from_jsx(File jsx,StringBuilder msg) throws FileNotFoundException, IOException, ScriptException {
    	String content=Util.readTxt(jsx, "\r\n", "UTF-8");
    	
        msg.append("加载jsx");
    	load_cScript(content,msg);
    }
    static void load_cScript(String content,StringBuilder msg) throws ScriptException {
        Compilable comp = (Compilable) engine;
        cScript = comp.compile(content);
        
        msg.append(":");
        msg.append(server_path);
        System.out.println(msg.toString());
    }
    public static class Helper{
    	public Character charAt(String string,int index) {
    		return string.charAt(index);
    	}
        public void saveText(String content,String path){
            try{
            	mb.Util.saveTxt(path, content, "UTF-8");
            }catch(Exception ex){
                ex.printStackTrace();
                System.out.println("保存"+path+"出错:"+ex.getMessage());
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
                return null;
            }
        }
        public Logger getLogger(String name) {
        	return Logger.getLogger(name);
        }
    }
}
