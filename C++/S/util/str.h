#pragma once
namespace s{
    namespace str{
        char kvs_find1st(const string & kvs,const char c,bool & unfind){
            char x=' ';
            unsigned i=0;
            while(i<kvs.size() && unfind){
                char key=kvs[i];
                i++;
                char value=kvs[i];
                if(key==c){
                    unfind=false;
                    x=value;
                }
                i++;
            }
            return x;
        }
        const char trans_map_char[]={'n','\n','r','\r','t','\t'};
        const string trans_map=string(trans_map_char);
        string stringFromEscape(const string& v,const char end,const unsigned trans_time){
            const unsigned old_size=v.size();
            const unsigned size=old_size-trans_time;
            char *buff=new char[size+1];
            unsigned i=0;
            unsigned ref=0;
            while(ref<size){
                char c=v[i];
                if(c=='\\'){
                    i++;
                    c=v[i];
                    if(c=='\\'){
                        buff[ref]='\\';
                    }else
                    if(c==end){
                        buff[ref]=end;
                    }else{
                        bool unfind=true;
                        char x=kvs_find1st(trans_map,c,unfind);
                        if(unfind){
                            throw new DefinedException("非法转义"+v);
                        }else{
                            buff[ref]=x;
                        }
                    }
                }else{
                    buff[ref]=c;
                }
                ref++;
                i++;
            }
            buff[size]='\0';
            string r(buff);
            delete [] buff;
            return r;
        }
        string stringFromEscape(const string& v,const char end){
            const unsigned old_size=v.size();
            unsigned trans_time=0;
            unsigned i=0;
            while(i<old_size){
                char c=v[i];
                if(c=='\\'){
                    trans_time++;
                    i++;
                }
                i++;
            }
            if(trans_time!=0){
                return stringFromEscape(v,end,trans_time);
            }else{
                return v;
            }
        }
        /*
        其实在S-Lisp中，只需要处理斜线转义和双引号转义，其它正常输入即可。
        */
        string stringToEscape(const string& v,const char start,const char end,const string trans="")
        {
            const unsigned old_size=v.size();
            unsigned size=v.size();
            for(unsigned i=0;i<old_size;i++){
                char c=v[i];
                if(c=='\\'){
                    size+=1;
                }else
                if(c==end){
                    size+=1;
                }else
                {
                    if(trans!=""){
                        bool unfind=true;
                        kvs_find1st(trans,c,unfind);
                        if(!unfind){
                            size+=1;
                        }
                    }
                }
            }
            char *buff=new char[size+3];
            buff[0]=end;
            unsigned ref=1;
            unsigned i=0;
            while(i<old_size)
            {
                char c=v[i];
                if(c=='\\'){
                    buff[ref]='\\';
                    ref++;
                    buff[ref]='\\';
                }else
                if(c==end){
                    buff[ref]='\\';
                    ref++;
                    buff[ref]=end;
                }else
                {
                    if(trans!=""){
                        bool unfind=true;
                        char x=kvs_find1st(trans,c,unfind);
                        if(unfind){
                            buff[ref]=c;
                        }else{
                            buff[ref]='\\';
                            ref++;
                            buff[ref]=x;
                        }
                    }else{
                        buff[ref]=c;
                    }
                }
                i++;
                ref++;
            }
            buff[size+1]=end;
            buff[size+2]='\0';
            string r(buff);
            delete [] buff;
            return r;
        }
    };
};