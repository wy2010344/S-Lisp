({
    data:{
        s_lisp:"util/s-lisp/index.js"
    },
    delay:true,
    success:function(){
        var s_lisp=lib.s_lisp();
        var Scanner=Java.type("java.util.Scanner");
        var System=Java.type("java.lang.System");
        var console=new Scanner(System["in"]);
        var str_out="";
        var str_in="";
        var shell=s_lisp.shell(
            function(v){
                str_out+=v;
            },
            '\n'
        );
        var circle=function(){
            str_out="";
            str_in="";
	        System.out.println(">>");
	        var tmp="";
	        while(tmp!="``"){
	            tmp=console.next();
	            str_in+=tmp+"\n";
	        }
	        str_out+="\n";
	        try{
	            var obj=shell(str_in,"\n");
	            str_out+=(obj?obj.toString():"");
	        }catch(e){
                if(e.getMessage){
                    str_out+=e.getMessage();
                }else{
	               str_out+=e.toString();
                }
	        }
            System.out.println("<<");
	        System.out.println(str_out);
	        circle();
        };
        return circle;
    }
});