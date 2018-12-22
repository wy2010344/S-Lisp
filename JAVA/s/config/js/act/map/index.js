({
    data:{},
    success:function(map){
        
	    var request=(function(){
	        var get=function(key){
	            return map.get("request").get(key);
	        };
	        return {
	            get:function(key){
	                var ret=get(key);
	                if(ret==null){
	                    return "";
	                }else{
	                    return ""+ret;
	                }
	            },
	            json:function(key){
	                var ret=get(key);
	                if(ret!=null){
	                    return JSON.parse(ret);
	                }else{
	                    return null;
	                }
	            }
	        };
	    })();
	    var response=(function(){
	        var write=function(code,description,obj){
	            var res=map.get("response");
	            res.put("code",code);
	            res.put("description",description);
	            res.put("data",obj);
	        };
	        return {
	            object:write,
	            json:function(obj){
	                write(0,"操作成功",JSON.stringify(obj));
	            },
	            success:function(msg){
	                write(0,""+(msg||"操作成功"));
	            },
	            error:function(msg,code){
	                write(code||-1,""+msg);
	            }
	        };
	    })();
	    
	    /**
	     * 传统的间接调用JS
	     */
	
	    var act=""+map.get("act");
	    if(act!=""){
	        var path=("act/map/"+act+".js").replace(/>/g,"/");
	        var action=mb.getLib(path);
	        var e=null;
	        if(action){
	            try{
	                action(request,response);
	            }catch(ex){
	                e=ex;
	            }
	        }else{
	            e="未找到指定页面"+path;
	        }
	        if(e){
	            var log=map.get("log");
	            if(log){
	                log.error(act+"出错"+e);
	            }
	            response.error(e,404);
	        }
	    }
    }
})