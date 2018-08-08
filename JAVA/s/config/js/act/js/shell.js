({
    data:{
        shell:"util/shell.js"
    },
    delay:true,
    success:function(){
        var log;
        return lib.shell({
            end:"//",
            shell:function(log_f){
                log=function(){
                    for(var i=0;i<arguments.length;i++){
                        log_f(arguments[i]);
                        log_f("\t");
                    }
                };
                return function(str,split){
                    return eval(str);
                };
            },
            toString:function(obj){
                if(obj==null){
                    return "null";
                }else{
                    return JSON.stringify(obj,"",2);
                }
            }
        });
    }
})