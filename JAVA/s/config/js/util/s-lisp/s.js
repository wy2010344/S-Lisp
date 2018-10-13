({
    delay:true,
    success:function(){
        var Node=Java.type("s.Node");
        return {
            reverse:function(v){
                return Node.reverse(v);
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
                    r=Node.kvs_extend(k,v,r);
                });
                return r;
            },
            kvs_find1st:function(kvs,key){
                return Node.kvs_find1st(kvs,key);
            },
            kvs_extend:function(k,v,scope){
	            return Node.kvs_extend(k,v,scope);
	        },
            extend:function(v,vs){
                return Node.extend(v,vs);
            },
            list:function(){
                var r=null;
                for(var i=arguments.length-1;i>-1;i--){
                    r=Node.extend(arguments[i],r);
                }
                return r;
            }
        }
    }
});