({
    data:{
        S_Lisp:"util/back-s-lisp/index.js"
    },
    success:function(req,res){
        var path=""+req.get("shell");
        path=path.replace(/\\/g,'/');
        var S_Lisp=lib.S_Lisp();
        S_Lisp.run(path,req.get("args"));
    }
});