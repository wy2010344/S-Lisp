package s;

public class Library {
	public static abstract class Console{
		public abstract void write(String text);
		public abstract String read(String title,String init);
	}
    
    /*结合成列表*/
    public static Node make_list(Object ...args) {
    	Node r=null;
    	for(int i=args.length-1;i>-1;i--) {
    		r=new Node(args[i],r);
    	}
    	return r;
    }
    /*反转*/
    public static Node reverse(Node node) {
    	Node r=null;
    	for(Node t=node;t!=null;t=t.Rest()) {
    		r=new Node(t.First(),r);
    	}
    	return r;
    }
    public static void log(Node node,Console c) {
    	for(Node tmp=node;tmp!=null;tmp=tmp.Rest()) {
    		c.write(tmp.First().toString());
    		c.write(" ");
    	}
    	c.write("\n");
    }
    
    //直接用kvs作字典，性能更高
    public static Object kvs_find1st(Node kvs,String key) {
    	if(kvs==null) {
    		return null;
    	}else {
    		Node r_kvs=kvs.Rest();
    		if(r_kvs==null) {
    			System.out.println("as");
    		}
    		if(key.equals(kvs.First())) {
    			return r_kvs.First();
    		}else {
    			return kvs_find1st(r_kvs.Rest(),key);
    		}
    	}
    }
    public static Node kvs_extend(String key,Object value,Node kvs) {
    	return new Node(key,new Node(value,kvs));
    }
    /*
    //寻找第一个定义
    public static Object findFirstDef(Node map,String key) {
    	if(map!=null) {
    		Node kv=(Node) map.First();
    		if(key.equals(kv.First())) {
    			return kv.Rest().First();
    		}else {
    			return findFirstDef(map.Rest(),key);
    		}
    	}else {
    		return null;
    	}
    }
    //增加定义
    public static Node extendDef(Node node,Object key,Object value) {
    	return new Node(makelist(key,value),node);
    }
    //列表转字典
    public static Node map_from_kvs(Node value) throws Exception{
    	if(value==null) {
    		return null;
    	}
		if(value.Length()%2==1) {
			throw new Exception("列表应该为偶数个");
		}
		Node map=null;
		while(value!=null) {
			Object key=value.First();
			value=value.Rest();
			map=extendDef(map,key,value.First());
			value=value.Rest();
		}
		return reverse(map);//顺序反转，保证最前的是最新的。
    }
    */
	
    Node m=null;
    Library def(String key,Object value) {
    	m=kvs_extend(key,value,m);
    	return this;
    }
}
