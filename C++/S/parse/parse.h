#pragma once
#include "./parse_base.h"
namespace s{
    /*抛弃*/
    BracketExp* Parse(Node* tokens){
        Location *root_loc=new Location(0,0,0);
        root_loc->retain();
        BracketExp *exp=new BracketExp(Exp::Exp_Large,"{}",NULL,root_loc);//缓存子列表
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
                string v="";
                if(x->Value()==")"){
                    tp=Exp::Exp_Small;
                    v="()";
                }else
                if(x->Value()=="]"){
                    tp=Exp::Exp_Medium;
                    v="[]";
                }else
                {
                    //"{"
                    tp=Exp::Exp_Large;
                    v="{}";
                }
                //临时括号，记录一些位置数据
                BracketExp *exp=new BracketExp(tp,v,children,x->Loc());
                caches=new Node(exp,caches);
                //这里也必须使用引用计数的方式才能正确执行->突然又得行了。
                caches->retain();
                children=NULL;
            }else
            if(x->token_type()==Token::Token_BracketLeft)
            {
                BracketExp *exp=static_cast<BracketExp*>(caches->First());
                Node * r_children=NULL;
                if(exp->exp_type()!=Exp::Exp_Large){
                    r_children=list::reverse(children);
                }
                Node *  n_result=new Node(
                    new BracketExp(
                        exp->exp_type(),
                        exp->Value(),
                        children,
                        exp->Loc(),
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
                Exp::Exp_Type tp;
                bool deal=true;
                if(x->token_type()==Token::Token_String)
                {
                    tp=Exp::Exp_String;
                }else
                if(x->token_type()==Token::Token_Int)
                {
                    tp=Exp::Exp_Int;
                }else{
                    BracketExp* parent=static_cast<BracketExp*>(caches->First());
                    if(parent->exp_type()==Exp::Exp_Medium){
                        //中括号
                        if (x->token_type()==Token::Token_Prevent) {
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
                    Exp* e=new Exp(tp,x->Value(),x->Loc());
                    e->original_type=x->token_type();
                    children=new Node(e,children);
                }
            }
        }
        caches->release();
        exp=new BracketExp(Exp::Exp_Large,"{}",children,root_loc);
        root_loc->release();
        return exp;
    }
};
