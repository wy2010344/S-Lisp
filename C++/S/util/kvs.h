#pragma once
namespace s{
	 //1997个，对比使用map2166
    namespace kvs{
        /*与list添加头保持一致*/
        Node * extend(String * key,Base *value,Node * kvs){
            return new Node(key,new Node(value,kvs));
        }
        Node * extend(string key,Base * value,Node * kvs){
            return extend(new String(key), value,kvs);
        }
        Base * find1st(Node * kvs,string & key){
            bool unfind=true;
            Base * b=NULL;
            while(kvs!=NULL && unfind){
                String * k =static_cast<String*>(kvs->First());
                kvs=kvs->Rest();
                if(k->StdStr()==key){
                    unfind=false;
                    b=kvs->First();
                }
                kvs=kvs->Rest();
            }
            return b;
        }
        Base * find1st(Node * kvs,String * key){
            return find1st(kvs,key->StdStr());
        }
    };
};