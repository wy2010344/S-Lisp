({
    success:function(p){
        var Scanner=Java.type("java.util.Scanner");
        var System=Java.type("java.lang.System");
        var console=new Scanner(System["in"]);
        var shell=p.shell(function(v){
            System.out.print(v);
        });
        var in_sym="<=";
        var out_sym="=>";
        var circle=function(){
            var str_in="";
            System.out.print(in_sym);
            var tmp=console.nextLine();
            if(tmp==p.end){
                //多行
                tmp="";
	            while(tmp!=p.end){
	                tmp=console.nextLine();
	                str_in+=tmp+"\n";
	            }
            }else{
                //单行
                str_in=tmp;
            }
            if(str_in!="exit"){
	            var str_out="";
	            try{
	                var obj=shell(str_in,"\n");
	                str_out=p.toString(obj);
	            }catch(e){
	                if(e.getMessage){
	                    str_out=e.getMessage();
	                }else{
	                    str_out=e.toString();
	                }
	            }
	            System.out.println(out_sym+str_out);
	            System.out.println("");
	            circle();
            }
        };
        return circle;
    }
})