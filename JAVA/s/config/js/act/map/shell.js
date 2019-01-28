({
    data:{
        S_Lisp:"util/back-s-lisp/index.js",
        shell:"util/shell.js"
    },
    success:function(){
        var S_Lisp=lib.S_Lisp();
        lib.shell({
            end:"``",
            shell:function(log){
                return S_Lisp.shell(log,'\n');
            },
            toString:function(obj){
                return S_Lisp.toString(obj,true).toString();
            }
        })();
    }
});