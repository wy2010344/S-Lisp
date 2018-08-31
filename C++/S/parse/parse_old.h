#pragma once
#include "./parse_base.h"
namespace s{
   /**
    抛弃
    返回列表
    边界情况，树节点到达根节点,列表解析完成
    接收的是(a b c)->) c b a ( 这样减少reverse的节点
    下一步优化，对{}内非let表达式的值节点，普通()表达式和[]表达式，进行反转，这样每次计算时不用reverse。
    */
    BracketExp* Parse(
        Node* tokens,//供解析的列表
        Node* caches,//树
        Node* children//平行列表
        )
    {
        if(tokens==NULL)
        {
            //解析完成
            return new BracketExp(parse::Type::Large,"{}",children,0);
        }
        else
        {
            Token* x=static_cast<Token*>(tokens->First());
            Node* xs=tokens->Rest();
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
                return Parse(
                    xs,
                    caches,//新树节点更新
                    NULL//子列表置空
                );
            }else
            if(x->Type()==token::Types::BracketLeft)
            {
                BracketExp *exp=static_cast<BracketExp*>(caches->First());
                Node *  n_result=new Node(
                        new BracketExp(exp->Type(),exp->Value(),children,exp->Index()),
                        exp->Cache()
                );
                Node * caches_parent=caches->Rest();
                //cout<<"引用数"<<bracket->_ref_()<<endl;
                //清理临时括号
                //delete bracket;
                caches->release();
                return Parse(
                    xs,
                    caches_parent,//树节点返回上一级
                    n_result//上一级的列表追加
                );
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
                return Parse(
                    xs,
                    caches,//树节点不变
                    children//子列表追加
                );
            }
        }
    }
    /*抛弃*/
    BracketExp* Parse(Node* tokens){
        BracketExp *exp=new BracketExp(parse::Type::Large,"{}",NULL,0);//缓存子列表
        Node *caches=new Node(exp,NULL);
        //还必须在Parse前后retain和release，因为参数了树节点的动作？模拟有一个引用着它。
        caches->retain();
        BracketExp *result=Parse(tokens,caches,NULL);
        caches->release();
        return result;
    }
};