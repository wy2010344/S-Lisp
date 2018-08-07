({
    delay:true,
    success:function(){
        var Node=Java.type("s.Node");
        var Library=Java.type("s.Library");
        return {
            reverse:function(v){
                return Library.reverse(v);
	        },
            o_from_kvs:function(kvs){
                var o={};
                for(var t=kvs;t!=null;t=t.Rest()){
                    var key=t.First();
                    t=t.Rest();
                    var value=t.First();
                    o[key]=value;
                }
                return o;
            },
            kvs_from_o:function(o){
                var r=null;
                mb.Object.forEach(o,function(v,k){
                    r=new Node(k,new Node(v,r));
                });
                return r;
            },
            kvs_find1st:function(kvs,key){
                return Library.kvs_find1st(kvs,key);
            }   
        }
    }
});