#pragma once
#include "./library/better.h"
namespace s{
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
        bool isValidKey(const string & key)
        {
            if(key[0]=='.' || key[key.size()-1]=='.'){
                //为了模拟js中多层字典，第一与最后不能是.
                return false;
            }else{
                bool ret=true;
                for(unsigned i=0;i<key.size();i++)
                {
                    char c=key[i];
                    if(c=='*')//c=='.' || 为了模拟js中多层字典访问，还是把.这个限制给去掉了。
                    {
                        ret=false;
                    }else
                    if(Tokenize::isBlank(c))
                    {
                        ret=false;
                    }
                }
                return ret;
            }
        }
        Node* bracket_match(Node * scope,Exp * key,Node *vas){
            BracketExp * sq=static_cast<BracketExp *>(key);
            Node *xs=sq->Children();
            while (xs!=NULL) {
                Exp * x=static_cast<Exp *>(xs->First());
                const string &vk=x->Value();
                Base *v=NULL;
                if (xs->Rest()==NULL && iswait(vk))
                {
                    /*
                    最后一个是匹配(可能还是kvs_match匹配，但不是bracket_match)
                    */
                    string subvk=vk.substr(3,vk.size());//不能是引用，只能是复制
                    if(subvk[subvk.size()-1]=='*'){
                        scope=when_kvs_match(scope,subvk,vas);
                    }else{
                        //普通匹配
                        scope=when_normal_match(scope,subvk,vas);
                    }
                }else
                {
                    if(vas!=NULL){
                        v=vas->First();
                        vas=vas->Rest();
                    }
                    scope=match(scope,x,v);
                }
                xs=xs->Rest();
            }
            return scope;
        }
#ifdef WITH_KVS_MATCH
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
        /*感觉kvs-match问题越来越多，不好用！vas可为空*/
        Node *when_kvs_match(Node* scope,string & id,Base *vas){
            id=id.substr(0,id.size()-1);//去掉星号
            if(isValidKey(id)){
                //字典增加前缀
                if(vas!=NULL){
                    vas->retain();
                    scope=kvs_match(scope,id,(Node*)vas);
                    vas->release();
                }else{
                    scope=kvs_match(scope,id,NULL);
                }
            }else{
                throw new DefinedException(id+"不是合法的key");
            }
            return scope;
        }
#else
        Node *when_kvs_match(Node* scope,string &id,Base *vas){
            throw new DefinedException("为了雪藏的kvs-math，暂时不支持以*号结尾");
            return scope;
        }
#endif
        Node * match(Node *scope,Exp *key,Base *vas){
            //值为空，仍然需要增加定义
            if (key->exp_type()==Exp::Exp_Id) {
                string id=key->Value();
                if(id[id.size()-1]=='*') {
                    scope=when_kvs_match(scope,id,vas);
                }else{
                    //单值匹配
                    scope=when_normal_match(scope,id,vas);
                }
            }else{
                if (key->exp_type()==Exp::Exp_Small) {
                    //括号匹配
                    if(vas!=NULL){
                        vas->retain();
                        scope=bracket_match(scope,key,static_cast<Node *>(vas));
                        vas->release();
                    }else{
                        scope=bracket_match(scope,key,NULL);
                    }
                }else{
                    throw new DefinedException(key->Value()+"不是合法的类型");
                }
            }
            return scope;
        }
        /*vas可为空*/
        Node *when_normal_match(Node *scope,string & id,Base *vas){
            if(isValidKey(id)){
                scope=kvs::extend(id,vas,scope);
            }else{
                throw new DefinedException(id+"不是合法的key");
            }
            return scope;
        }
        Node * & scope;
        Base* run(Exp * e){
            if(e->exp_type()==Exp::Exp_Small){
                BracketExp *be=static_cast<BracketExp *>(e);
                if (be->Children()!=NULL) {
                    Exp *t=static_cast<Exp *>(be->Children()->First());
                    if (t->exp_type()==Exp::Exp_Id && t->Value()=="let") {
                        //let表达式
                        Node *rst=be->Children()->Rest();
                        while (rst!=NULL) {
                            Exp *key=static_cast<Exp*>(rst->First());
                            rst=rst->Rest();
                            Exp *value=static_cast<Exp*>(rst->First());
                            rst=rst->Rest();
                            Base * vas=interpret(value, scope);
                            scope=match(scope,key,vas);
                        }
                        return NULL;
                    }else{
                        return interpret(e, scope);
                    }
                }else{
                    return interpret(e, scope);
                }
            }else{
                return interpret(e, scope);
            }
        }
        /*(b a log)*/
        Node * calNode(Node * list,Node * scope)
        {
            Node * r=NULL;
            for(Node * x=list;x!=NULL;x=x->Rest())
            {
                Exp *xe=static_cast<Exp *>(x->First());
                Base *xv=interpret(xe,scope);
                r=new Node(xv,r);
            }
            return r;
            //return list::reverseAndDelete(r);
        }
        LocationException* call_exception(string msg,BracketExp * exp,Node * children,Node * scope)
        {
            msg=msg+"\n"+exp->toString()+"\n"+children->toString()+"\n";
            //cout<<"出现异常:"<<msg<<"在位置:"<<exp->Index()<<endl;
            children->release();
            /*
            scope->retain();
            scope->release();
            */
            return new LocationException(msg,exp->Index());
        }
        Base * exec(Function* func,Node *rst,Node *children){
            /*函数的计算结果默认是+1的*/
            Base *b=func->exec(rst);
            children->release();
            if (b!=NULL) {
                //在计算结果时伪销毁。
                b->eval_release();
            }
            return b;
        }
        Base *interpret(Exp* e,Node * scope);
    public:
        QueueRun(Node * & scope):scope(scope){}
        Base * exec(BracketExp *exp){
            Base * ret=NULL;
            for (Node * tmp=exp->Children(); tmp!=NULL; tmp=tmp->Rest()) {
                if(ret!=NULL)
                {
                    //上一次的计算结果，未加到作用域
                    ret->retain();
                    ret->release();
                }
                Exp *e=static_cast<Exp *>(tmp->First());
                ret=this->run(e);
            }
            return ret;
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
        Fun_Type ftype(){
            return Function::fUser;
        }
        string toString(){
            return exp->toString();
        }
    protected:
        Node * parentScope;
        BracketExp * exp;
        Base * run(Node * args,Node * & scope){
            QueueRun qr(scope);
            return qr.exec(exp);
        }
    };
    Base *QueueRun::interpret(Exp* e,Node * scope){
        if(e->isBracket()){
            /*
             *scope可能作为父作用域，避免被销毁
             */
            scope->retain();
            Base *b;
            BracketExp * be=static_cast<BracketExp*>(e);
            if(be->exp_type()==Exp::Exp_Small)
            {
                //小括号
                Node * children=calNode(be->R_children(),scope);
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
                }catch(Exception *e){
                    throw e;
                }catch(...){
                    //无法捕获到，怎么处理？
                    throw call_exception("调用出错",be,children,scope);
                }
#else
                b=exec(func,rst,children);
#endif
            }else
            if(be->exp_type()==Exp::Exp_Medium)
            {
                //中括号
                b=calNode(be->R_children(),scope);
            }else
            if(be->exp_type()==Exp::Exp_Large)
            {
                //大括号
                b=new UserFunction(be,scope);
            }else{
                b=NULL;
            }
            scope->eval_release();
            return b;
        }else
        if(e->exp_type()==Exp::Exp_String)
        {
            return new String(e->Value());
        }else
        if(e->exp_type()==Exp::Exp_Int)
        {
            return new Int(e->Value());
        }else
        if(e->exp_type()==Exp::Exp_Id)
        {
            return kvs::find1st(scope,e->Value());
        }else{
            return NULL;
        }
    }
};
