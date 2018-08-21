({
    data:{
        s_lisp:"util/s-lisp/index.js",
        shell:"util/shell.js"
    },
    delay:true,
    success:function(){
        var s_lisp=lib.s_lisp();
        return lib.shell({
            end:"``",
            shell:function(log){
                return s_lisp.shell(
		            function(v){
                        log(v);
		            },
		            '\n'
                );
            },
            toString:function(obj){
                return s_lisp.s_trans(obj).toString();
            }
        });
    }
});