({
    data:{
        s_lisp:"util/s-lisp/index.js"
    },
    success:function(){
        var path=mb.request.get("args");
        mb.log(path);
        var s_lisp=lib.s_lisp();
        //var path="/D:/usr/web/app/S-Lisp/JAVA/s/target/x.lisp";
        s_lisp.run(path,true);
    }
});