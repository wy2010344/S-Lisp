({
    data:{
        S_Lisp:"util/S-Lisp/index.js"
    },
    success:function(){
        var path=mb.request.get("args");
        var separator=Java.type("java.io.File").separator;
        path=path.replace(/\\/g,'/');
        var S_Lisp=lib.S_Lisp();
        //var path="/D:/usr/web/app/S-Lisp/JAVA/s/target/x.lisp";
        
        S_Lisp.run(path,true);
    }
});