({
    data:{
       library:"./library.js",
       s:"./s.js"
    },
    success:function(){
        var path=ini.get("server_path")+"/../";
        var QueueRun=Java.type("s.QueueRun");
        var Load=Java.type("s.library.Load");
        var library=lib.library();
        
        var line_split=mb.charAt("\n",0);
        var scope=library.library;//在js中定义的库
        var scope_extend=Load.run_e(library.S_Root+"index.lisp",scope,line_split);
        scope_extend=lib.s.reverse(scope_extend);
        for(var t=scope_extend;t!=null;t=t.Rest()){
            var value=t.First();
            t=t.Rest();
            var key=t.First();
            scope=lib.s.kvs_extend(key,value,scope);
        }
        
        return {
            run:function(x_path){
                var o=Load.run_e(x_path,scope,line_split);
                if(o){
                    o.exec(null);
                }else{
                    mb.log(x_path);
                }
            },
            shell:function(log,split){
                var qr=new QueueRun(
	                lib.s.kvs_extend(
	                    "log",
	                    library.buildFunc(
                            "log",
	                        library.log_factory(log)
	                    ),
                        scope
	                )
	            );
                return function(str){
                    return qr.exec(str,line_split);
                }
            },
            toString:library.toString
        }
    }
})