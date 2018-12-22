({
    data:{
        shell:"util/shell.js"
    },
    delay:true,
    success:function(){
        var serialize_fun=function(fun){
            return "<Function>";//obj.toString();
        };
        return function(){
	        var log;
	        lib.shell({
	            end:"//",
	            shell:function(log_f){
	                log=function(){
	                    for(var i=0;i<arguments.length;i++){
	                        log_f(arguments[i]);
	                        log_f("\t");
	                    }
	                };
	                return eval;
	            },
	            toString:function(obj){
	                if(obj==null){
	                    return "null";
	                }else
                    if(typeof(obj)=="function"){
                        return serialize_fun(obj);
                    }else{
	                    return JSON.stringify(
                            obj,
                            function(key,value){
                                if(typeof(value)=="function"){
                                    return serialize_fun(value);
                                }else{
                                    return value;
                                }
                            },
                            2
                        );
	                }
	            }
	        })();  
        };
    }
})