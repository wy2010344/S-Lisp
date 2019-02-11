({
    data:{
        shell:"util/shell.js"
    },
    success:function(){
        var QueueRun=Java.type("meta.QueueRun");
        var scope=Java.type("meta.Library").buildScope();
        var qr=new QueueRun(scope);
        lib.shell({
            end:"``",
            shell:function(log){
                return function(str){
                    return qr.run(str);
                };
            },
            toString:function(obj){
                if(obj){
                    return obj.toString();
                }else{
                    return "null";
                }
            }
        })();
    }
})