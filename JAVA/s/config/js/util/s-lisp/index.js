({
    data:{
       library:"./library.js",
       util:"./util.js"
    },
    success:function(){
        var path=ini.get("server_path")+"/../";
        var Eval=Java.type("s.Eval");
        var Node=Java.type("s.Node");
        var QueueRun=Java.type("s.QueueRun");
        var library=lib.library();
        var scope=library.library;//在js中定义的库
        var scope_extend=Eval.run(path+"/lisp/mb/index.lisp",scope);//在lisp中定义的库
        scope_extend=lib.util.reverse(scope_extend);
        for(var t=scope_extend;t!=null;t=t.Rest()){
            var value=t.First();
            t=t.Rest();
            var key=t.First();
            scope=library.kvs_extend(key,value,scope);
        }
        
        return {
            run:function(x_path,bool){
                if(!bool){
                    x_path=path+"/lisp/act/"+x_path;
                }
                return Eval.run(x_path,scope);
            },
            shell:function(log,split){
                var qr=new QueueRun(
	                library.kvs_extend(
	                    "log",
	                    library.buildFunc(
	                        library.log_factory(log)
	                    ),
                        scope
	                )
	            );
                return function(str){
                    return Eval.run(str,qr,split);
                }
            }
        }
    }
})