#pragma once
#include "./parse_base.h"
namespace s{

    void resetLetID(IDExp * k){
        if(k->Value()->StdStr().find('.')!=string::npos){
            //有点的情况
            throw k->exception("Let表达式中，不是合法的id类型"+k->toString());
        }else{
            k->exp_type(Exp::Exp_LetId);
        }
    }
    void resetLetSmallKV(BracketExp * small){
        small->exp_type(Exp::Exp_LetSmall);
        Node* vs=static_cast<BracketExp*>(small)->Children();
        while(vs!=NULL){
            Exp * k=static_cast<Exp*>(vs->First());
            vs=vs->Rest();
            if(vs==NULL && k->exp_type()==Exp::Exp_Id){
                IDExp* id_k=static_cast<IDExp*>(k);
                string &v=id_k->Value()->StdStr();
                if(v.size()>3){
                    if(v[0]=='.' && v[1]=='.' && v[2]=='.'){
                        //候选let表达式
                        string rv=v.substr(3);
                        if(rv.find('.')!=string::npos){
                            throw id_k->exception("Let表达式中，不是合法的剩余匹配"+v);
                        }else{
                            v=rv;//其实已经修改内部
                            id_k->exp_type(Exp::Exp_LetRest);
                        }
                    }else{
                        resetLetID(id_k);
                    }
                }else{
                    resetLetID(id_k);
                }
            }else{
                if(k->exp_type()==Exp::Exp_Small){
                    resetLetSmallKV(static_cast<BracketExp*>(k));
                }else
                if(k->exp_type()==Exp::Exp_Id){
                    resetLetID(static_cast<IDExp*>(k));
                }else{
                    throw k->exception("Let表达式中，不是合法的key类型"+k->toString());
                }
            }
        }
    }
    /*
    *对Let表达式内部重新赋值
    */
    void resetLetKV(Node* kvs){
        while(kvs!=NULL){
            Exp * k=static_cast<Exp*>(kvs->First());
            kvs=kvs->Rest();
            if(kvs!=NULL){
                if(k->exp_type()==Exp::Exp_Id){
                    resetLetID(static_cast<IDExp*>(k));
                }else
                if(k->exp_type()==Exp::Exp_Small){
                    BracketExp* b_k=static_cast<BracketExp*>(k);
                    if(b_k->Children()==NULL){
                        k->warn("Let表达式中无意义的空()，请检查"+kvs->First()->toString());
                    }
                    resetLetSmallKV(b_k);
                }else{
                    throw k->exception("Let表达式中，不是合法的key类型"+k->toString());
                }
                kvs=kvs->Rest();
            }else{
                throw k->exception("Let表达式中期待与key:"+k->toString()+"匹配，却结束了let表达式");
            }
        }
    }

    void check_Large(Node * vs){
        while(vs!=NULL){
            Exp * v=static_cast<Exp*>(vs->First());
            vs=vs->Rest();
            if(vs!=NULL){
                //非最后一个表达式
                Exp::Exp_Type t=v->exp_type();
                if(!(t==Exp::Exp_Let || t==Exp::Exp_Small || t==Exp::Exp_Medium))
                {
                    v->warn("函数中定义无意义表达式"+v->toString()+"，请检查");
                }
            }
        }
    }

    BracketExp* Parse(Node* tokens){
        Location* loc=new Location(0,0,0);
        Token* root_left=new Token(new String("{"),"{",Token::Token_BracketLeft,loc);
        Token* root_right=new Token(new String("}"),"}",Token::Token_BracketRight,loc);
        root_left->retain();
        root_right->retain();
        BracketExp *exp=new BracketExp(Exp::Exp_Large,NULL,root_right,NULL,NULL);//缓存子列表
        Node *caches=new Node(exp,NULL);
        //还必须在Parse前后retain和release，因为参数了树节点的动作？模拟有一个引用着它。
        caches->retain();
        Node* xs=tokens;
        Node* children=NULL;
        while(xs!=NULL){
            Token* x=static_cast<Token*>(xs->First());
            xs=xs->Rest();
            if(x->token_type()==Token::Token_BracketRight)
            {
                Exp::Exp_Type tp;
                string& b=x->Value()->StdStr();
                if(b==")"){
                    tp=Exp::Exp_Small;
                }else
                if(b=="]"){
                    tp=Exp::Exp_Medium;
                }else
                {
                    //"{"
                    tp=Exp::Exp_Large;
                }
                //临时括号，记录一些位置数据
                BracketExp *exp=new BracketExp(tp,NULL,x,children,NULL);
                caches=new Node(exp,caches);
                //这里也必须使用引用计数的方式才能正确执行->突然又得行了。
                caches->retain();
                children=NULL;
            }else
            if(x->token_type()==Token::Token_BracketLeft)
            {
                BracketExp *exp=static_cast<BracketExp*>(caches->First());
                Node * caches_parent=caches->Rest();
                Exp::Exp_Type tp=exp->exp_type();

                Node * r_children=NULL;
                if(tp==Exp::Exp_Large){
                    //检查函数内的无用表达式，最外层的不会经过这里，要单独调用
                    check_Large(children);
                }else{
                    r_children=list::reverse(children);
                }

                if(caches_parent!=NULL){
                    BracketExp *p_exp=static_cast<BracketExp*>(caches_parent->First());
                    if(p_exp->exp_type()==Exp::Exp_Large){
                        //父表达式是函数
                        if(tp==Exp::Exp_Small){
                            if(children==NULL){
                                //()
                                throw new LocationException("不允许空的()",x->Loc());
                            }else{
                                Exp * first=static_cast<Exp*>(children->First());
                                if(first->exp_type()==Exp::Exp_Id && static_cast<IDExp*>(first)->Value()->StdStr()=="let"){
                                    //(let )
                                    tp=Exp::Exp_Let;
                                    if(children->Length()==1){
                                        throw first->exception("不允许空的let表达式");
                                    }else{
                                        resetLetKV(children->Rest());
                                    }
                                }else{
                                    //非let表达式，检查第一个是id/{}/()
                                    if(!(first->exp_type()==Exp::Exp_Id || first->exp_type()==Exp::Exp_Large || first->exp_type()==Exp::Exp_Small))
                                    {
                                        throw first->exception("函数调用第一个应该是id或{}或()，而不是"+first->toString());
                                    }
                                }
                            }
                        }
                    }
                }
                Node *  n_result=new Node(
                    new BracketExp(
                        tp,
                        x,
                        exp->Right(),
                        children,
                        r_children
                    ),
                    exp->Children()
                );
                //cout<<"引用数"<<bracket->_ref_()<<endl;
                //清理临时括号
                //delete bracket;
                caches->release();
                caches=caches_parent;
                children=n_result;
            }else
            {
                Exp::Exp_Type tp;
                bool deal=true;
                if(x->token_type()==Token::Token_String)
                {
                    tp=Exp::Exp_String;
                }else
                if(x->token_type()==Token::Token_Int)
                {
                    tp=Exp::Exp_Int;
                }else
                if(x->token_type()==Token::Token_Bool){
                    tp=Exp::Exp_Bool;
                }else{
                    BracketExp* parent=static_cast<BracketExp*>(caches->First());
                    if(parent->exp_type()==Exp::Exp_Medium){
                        //中括号
                        if (x->token_type()==Token::Token_Prevent) {
                            string& sr=x->Value()->StdStr();
                            if(sr=="true" || sr=="false" || Tokenize::isInt(sr)){
                                throw new LocationException("中括号内转义寻找作用域上的" +sr+ "定义",x->Loc());
                            }
                            tp=Exp::Exp_Id;
                        }else
                        if (x->token_type()==Token::Token_Id) {
                            tp=Exp::Exp_String;
                        }else{
                            deal=false;
                        }
                    }else{
                        //其它括号
                        if (x->token_type()==Token::Token_Prevent){
                            tp=Exp::Exp_String;
                        }else
                        if (x->token_type()==Token::Token_Id) {
                            tp=Exp::Exp_Id;
                        }else{
                            deal=false;
                        }
                    }
                }
                //普通的值节点，返回解析后继节点
                if(deal)
                {
                    Exp* e=NULL;
                    if(tp==Exp::Exp_Id){
                        e=new IDExp(x);
                    }else
                    if(tp==Exp::Exp_Int){
                        e=new IntExp(x);
                    }else
                    if(tp==Exp::Exp_Bool){
                        e=new BoolExp(x);
                    }else{
                        e=new AtomExp(tp,x);
                    }
                    children=new Node(e,children);
                }
            }
        }
        caches->release();
        //检查函数内的无用表达式
        check_Large(children);
        exp=new BracketExp(
            Exp::Exp_Large,
            root_left,
            root_right,
            children,
            NULL
        );
        root_left->release();
        root_right->release();
        return exp;
    }
};
