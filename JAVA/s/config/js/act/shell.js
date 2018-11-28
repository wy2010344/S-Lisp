({
    data:{
        S_Lisp:"util/S-Lisp/index.js",
        shell:"util/shell.js"
    },
    delay:true,
    success:function(){
        return function(){
	        var S_Lisp=lib.S_Lisp();
	        lib.shell({
	            end:"``",
	            shell:function(log){
	                return S_Lisp.shell(
			            function(v){
	                        log(v);
			            },
			            '\n'
	                );
	            },
	            toString:function(obj){
	                return S_Lisp.toString(obj,true).toString();
	            }
	        })();
        };
    }
});