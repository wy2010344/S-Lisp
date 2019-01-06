({
    data:{
        S_Lisp:"util/S-Lisp/index.js"
    },
    success:function(req,res){
        var path=""+req.get("args");
        path=path.replace(/\\/g,'/');
        var S_Lisp=lib.S_Lisp();
        S_Lisp.run(path,true);
    }
});