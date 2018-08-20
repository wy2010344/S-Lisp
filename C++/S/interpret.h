#pragma once
#include "./library/system.h"
namespace s{
    Base *interpret(Exp* e,Node * scope);
    //为了支持控制台
    class QueueRun{
        bool iswait(const string & x){
            if(x.size()>3){
                if (x[0]=='.' && x[1]=='.' && x[2]=='.') {
                    return true;
                }else{
                    return false;
                }
            }else{
                return false;
            }
        }
        Node* bracket_match(Node * scope,Exp * key,Node *vas){
            BracketExp * sq=static_cast<BracketExp *>(key);
            Node *xs=sq->Cache();
            while (xs!=NULL) {
                Exp * x=static_cast<Exp *>(xs->First());
                //必须是ID类型的
                Base *v=NULL;
                const string &vk=x->Value();
                if (xs->Rest()==NULL && iswait(vk))
                {
                    string subvk=vk.substr(3,vk.size());//不能是引用，只能是复制
                    if(isValidKey(subvk))
                    {
                        scope=kvs::extend(subvk,vas,scope);
                    }else{
                        throw subvk+"不是合法的key";
                    }
                }else
                {
                    if(vas!=NULL){
                        v=vas->First();
                        vas=vas->Rest();
                    }
                    if(isValidKey(vk)){
                        scope=kvs::extend(vk,v,scope);
                    }else{
                        throw vk+"不是合法的key";
                    }
                }
                xs=xs->Rest();
            }
            return scope;
        }
        /*如果要考虑性能，可能这里，酌情添加*/
        Node* kvs_match(Node *scope,string & id,Node *kvs)
        {
            Node *svk=list::reverse(kvs);//必须先倒置
            scope=kvs::extend(id,new library::MatchFunc(kvs),scope);
            svk->retain();
            Node *svt=svk;
            while (svt!=NULL) {
                Base *sv=svt->First();
                svt=svt->Rest();
                string & k=static_cast<String*>(svt->First())->StdStr();//如果不是string，肯定会报错
                if(isValidKey(k)){
                    scope=kvs::extend(id+"."+k, sv,scope);
                }else{
                    //忽略
                }
                svt=svt->Rest();
            }
            svk->release();
            return scope;
        }
        bool isValidKey(const string & key)
        {
            if(key[0]=='.' || key[key.size()-1]=='.'){
                //为了模拟js中多层字典，第一与最后不能是.
                return false;
            }else{
                bool ret=true;
                for(int i=0;i<key.size();i++)
                {
                    char c=key[i];
                    if(c=='*')//c=='.' || 为了模拟js中多层字典访问，还是把.这个限制给去掉了。
                    {
                        ret=false;
                    }else
                    if(token::isBlank(c))
                    {
                        ret=false;
                    }
                }
                return ret;
            }
        }
        Node * & scope;
    public:
        QueueRun(Node * & scope):scope(scope){}
        Base* run(Exp * e){
            if(e->Type()==parse::Type::Small){
                BracketExp *be=static_cast<BracketExp *>(e);
                if (be->Cache()!=NULL) {
                    Exp *t=static_cast<Exp *>(be->Cache()->First());
                    if (t->Type()==parse::Type::Id && t->Value()=="let") {
                        Node *rst=be->Cache()->Rest();
                        while (rst!=NULL) {
                            Exp *key=static_cast<Exp*>(rst->First());
                            rst=rst->Rest();
                            Exp *value=static_cast<Exp*>(rst->First());
                            rst=rst->Rest();
                            
                            Base * vas=interpret(value, scope);
                            if (key->Type()==parse::Type::Id) {
                                string id=key->Value();
                                if (id[id.size()-1]=='*') {
                                    id=id.substr(0,id.size()-1);//去掉星号
                                    if(isValidKey(id)){
                                        //字典增加前缀
                                        vas->retain();
                                        scope=kvs_match(scope,id,(Node*)vas);
                                        vas->release();
                                    }else{
                                        throw id+"不是合法的key";
                                    }
                                }else{
                                    //单值匹配
                                    if(isValidKey(id)){
                                        scope=kvs::extend(key->Value(),vas,scope);
                                    }else{
                                        throw id+"不是合法的key";
                                    }
                                }
                            }else{
                                if (key->Type()==parse::Type::Small) {
                                    //括号匹配
                                    vas->retain();
                                    scope=bracket_match(scope,key,static_cast<Node *>(vas));
                                    vas->release();
                                }
                            }
                        }
                        return NULL;
                    }
                    else{
                        return interpret(e, scope);
                    }
                }
                else{
                    return interpret(e, scope);
                }
                
            }
            else{
                return interpret(e, scope);
            }
        }
    };
    class UserFunction:public Function{
    public:
        UserFunction(BracketExp * exp,Node * parentScope):Function(){
            this->exp=exp;
            this->exp->retain();
            this->parentScope=parentScope;
            //作用域必须有，所以不需要检查空指针。
            this->parentScope->retain();
        }
        virtual Base *exec(Node *args)
        {
            Node * scope=kvs::extend("args",args,parentScope);
            scope=kvs::extend("this",this,scope);
            /*
            Base *ret;
            try {
                ret=run(args,scope);
            } catch (...) {
                cout<<"出现异常"<<endl;
            }
             */
            Base *ret=run(args, scope);
            if (ret!=NULL) {
                ret->retain();
            }
            //不能直接delete，可能被自定义函数闭包引用
            scope->retain();
            scope->release();
            //delete scope;
            return ret;
        }
        virtual ~UserFunction(){
            this->exp->release();
            this->parentScope->release();
        }
        Function_type ftype(){
            return Function_type::fUser;
        }
        string toString(){
            return exp->toString();
        }
    protected:
        Node * parentScope;
        BracketExp * exp;
        Base * run(Node * args,Node * & scope){
            Base * ret=NULL;
            QueueRun qr(scope);
            for (Node * tmp=exp->Cache(); tmp!=NULL; tmp=tmp->Rest()) {
                if(ret!=NULL)
                {
                    //上一次的计算结果，未加到作用域
                    ret->retain();
                    ret->release();
                }
                Exp *e=static_cast<Exp *>(tmp->First());
                ret=qr.run(e);
            }
            return ret;
        }
    };
    Node * calNode(Node * list,Node * scope)
    {
        Node * r=NULL;
        for(Node * x=list;x!=NULL;x=x->Rest())
        {
            Exp *xe=static_cast<Exp *>(x->First());
            Base *xv=interpret(xe,scope);
            r=new Node(xv,r);
        }
        return list::reverseAndDelete(r);
    }
    LocationException call_exception(string msg,BracketExp * exp,Node * children,Node * scope)
    {
        msg=msg+"\n"+exp->toString()+"\n"+children->toString()+"\n";
        //cout<<"出现异常:"<<msg<<"在位置:"<<exp->Index()<<endl;
        children->release();
        /*
        scope->retain();
        scope->release();
        */
        return LocationException(msg,exp->Index());
    }
    Base * exec(Function* func,Node *rst,Node *children){
        Base *b=func->exec(rst);
        children->release();
        if (b!=NULL) {
            //在计算结果时伪销毁。
            b->eval_release();
        }
        return b;
    }
    Base *interpret(Exp* e,Node * scope)
    {
        if(e->isBracket())
        {
            /*
             *scope可能作为父作用域，避免被销毁
             */
            scope->retain();
            Base *b;
            BracketExp * be=static_cast<BracketExp*>(e);
            if(be->Type()==parse::Type::Small)
            {
                //小括号
                Node * children=calNode(be->Cache(),scope);
                children->retain();
                Base* first=children->First();
                Function * func=static_cast<Function*>(first);
                Node * rst=children->Rest();
#ifdef DEBUG
                /*
                    深思熟虑，能正常执行，
                    不处理异常，测试时有异常有内存泄漏，
                    测试过了是没有的，
                    异常处理方方面面，比较麻烦
                */
                if(func==NULL)
                {
                    //没法销毁函数内的引用计数
                    throw call_exception("未找到函数定义",be,children,scope);
                }
                try{
                    b=exec(func,rst,children);
                }catch(...)
                {
                    //无法捕获到，怎么处理？
                    throw call_exception("调用出错",be,children,scope);
                }
#else
                b=exec(func,rst,children);
#endif
            }else
            if(be->Type()==parse::Type::Medium)
            {
                //中括号
                b=calNode(be->Cache(),scope);
            }else 
            if(be->Type()==parse::Type::Large)
            {
                //大括号
                b=new UserFunction(be,scope);
            }else{
                b=NULL;
            }
            scope->eval_release();
            return b;
        }else
        if(e->Type()==parse::Type::String)
        {
            return new String(e->Value());
        }else
        if(e->Type()==parse::Type::Int)
        {
            return new Int(e->Value());
        }else
        if(e->Type()==parse::Type::Id)
        {
            return kvs::find1st(scope,e->Value());
        }else{
            return NULL;
        }
    }
};