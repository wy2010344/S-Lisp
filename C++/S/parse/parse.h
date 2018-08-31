#pragma once
#include "./parse_base.h"
namespace s{
    /*抛弃*/
    BracketExp* Parse(Node* tokens){
        BracketExp *exp=new BracketExp(parse::Type::Large,"{}",NULL,0);//缓存子列表
        Node *caches=new Node(exp,NULL);
        //还必须在Parse前后retain和release，因为参数了树节点的动作？模拟有一个引用着它。
        caches->retain();
        Node* xs=tokens;
        Node* children=NULL;
        while(xs!=NULL){
            Token* x=static_cast<Token*>(xs->First());
            xs=xs->Rest();
            if(x->Type()==token::Types::BracketRight)
            {
                parse::Type tp;
                string v="";
                if(x->Value()==")"){
                    tp=parse::Type::Small;
                    v="()";
                }else
                if(x->Value()=="]"){
                    tp=parse::Type::Medium;
                    v="[]";
                }else
                {
                    //"{"
                    tp=parse::Type::Large;
                    v="{}";
                }
                //临时括号，记录一些位置数据
                BracketExp *exp=new BracketExp(tp,v,children,x->Index());
                caches=new Node(exp,caches);
                //这里也必须使用引用计数的方式才能正确执行->突然又得行了。
                caches->retain();
                children=NULL;
            }else
            if(x->Type()==token::Types::BracketLeft)
            {
                BracketExp *exp=static_cast<BracketExp*>(caches->First());
                Node * r_children=NULL;
                if(exp->Type()!=parse::Type::Large){
                    r_children=list::reverse(children);
                }
                Node *  n_result=new Node(
                        new BracketExp(
                            exp->Type(),
                            exp->Value(),
                            children,
                            exp->Index(),
                            r_children
                        ),
                        exp->Children()
                );
                Node * caches_parent=caches->Rest();
                //cout<<"引用数"<<bracket->_ref_()<<endl;
                //清理临时括号
                //delete bracket;
                caches->release();
                caches=caches_parent;
                children=n_result;
            }else
            {
                parse::Type tp;
                bool deal=true;
                if(x->Type()==token::Types::Str)
                {
                    tp=parse::String;
                }else
                if(x->Type()==token::Types::Num)
                {
                    tp=parse::Int;
                }else{
                    BracketExp* parent=static_cast<BracketExp*>(caches->First());
                    if(parent->Type()==parse::Type::Medium){
                        //中括号
                        if (x->Type()==token::Types::Prevent) {
                            tp=parse::Id;
                        }else
                        if (x->Type()==token::Types::Id) {
                            tp=parse::String;
                        }else{
                            deal=false;
                        }
                    }else{
                        //其它括号
                        if (x->Type()==token::Types::Prevent){
                            tp=parse::String;
                        }else
                        if (x->Type()==token::Types::Id) {
                            tp=parse::Id;
                        }else{
                            deal=false;
                        }
                    }
                }
                //普通的值节点，返回解析后继节点
                if(deal)
                {
                    Exp* e=new Exp(tp,x->Value(),x->Index());
                    e->original_type=x->Type();
                    children=new Node(e,children);
                }
            }
        }
        caches->release();
        return new BracketExp(parse::Type::Large,"{}",children,0);
    }
};