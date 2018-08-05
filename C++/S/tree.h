#pragma once
namespace s{
    
    namespace parse{
        enum Type{
            Large,
            Medium,
            Small,
            String,
            Int,
            Id//,Comment//不要Comment
        };
    };
    class Exp:public Base{
    private:
        parse::Type type;
        string value;
        int index;
    public:
        Exp(parse::Type type,string value,int index):Base(){
            this->type=type;
            this->value=value;
            this->index=index;
        }
        string & Value(){
            return value;
        }
        parse::Type Type(){
            return type;
        }
        int Index(){
            return index;
        }
        virtual bool isBracket(){
            return false;
        }
        token::Types original_type;
        virtual string toString(){
            if(original_type==token::Types::Prevent){
                return "'"+value;
            }else
            if(type==parse::Type::String)
            {
                return stringToEscape(value);
            }else{
                return value;
            }
        }
    };
    class BracketExp:public Exp{
    private:
        Node *cache;
    public:
        BracketExp(parse::Type type,string value,Node * cache,int index)
            :Exp(type,value,index){
            this->cache=cache;
            if(cache!=NULL)
            {
                cache->retain();
            }
        }
        Node* Cache(){
            return cache;
        }
        virtual bool isBracket(){
            return true;
        }

        virtual ~BracketExp(){
            if(cache!=NULL)
            {
                cache->release();
            }
        }
        
        virtual string toString(){
            char a[2]={Value()[0],'\0'};
            string x=string(a);
            for(Node * t=cache;t!=NULL;t=t->Rest())
            {
                Exp *e=static_cast<Exp*>(t->First());
                x+=e->toString();
                x+=" ";
            }
            x+=Value()[1];
            return x;
        }
    };

    /**
    返回列表
    边界情况，树节点到达根节点,列表解析完成
    */
    BracketExp* Parse(
        Node* tokens,//供解析的列表
        Node* bracket,//树
        Node* rest//平行列表
        )
    {
        if(tokens==NULL)
        {
            //解析完成
            rest=list::reverseAndDelete(rest);
            
            return new BracketExp(parse::Type::Large,"{}",rest,0);
        }
        else
        {
            Token* x=static_cast<Token*>(tokens->First());
            Node* xs=tokens->Rest();
            if(x->Type()==token::Types::BracketLeft)
            {
                parse::Type tp;
                string v="";
                if(x->Value()=="("){
                    tp=parse::Type::Small;
                    v="()";
                }else
                if(x->Value()=="["){
                    tp=parse::Type::Medium;
                    v="[]";
                }else
                {
                    //"{"
                    tp=parse::Type::Large;
                    v="{}";
                }
                //临时括号，记录一些位置数据
                BracketExp *exp=new BracketExp(tp,v,rest,x->Index());
                Node *bracket_new=new Node(exp,bracket);
                //这里也必须使用引用计数的方式才能正确执行->突然又得行了。
                bracket_new->retain();
                return Parse(
                    xs,
                    bracket_new,//新树节点更新
                    NULL//子列表置空
                );
            }else
            if(x->Type()==token::Types::BracketRight)
            {
                BracketExp *exp=static_cast<BracketExp*>(bracket->First());

                //翻转后原来的数据不要了。
                rest=list::reverseAndDelete(rest);
                Node *  n_result=new Node(
                        new BracketExp(exp->Type(),exp->Value(),rest,exp->Index()),
                        exp->Cache()
                );
                Node * bracket_parent=bracket->Rest();
                //cout<<"引用数"<<bracket->_ref_()<<endl;
                //清理临时括号
                //delete bracket;
                bracket->release();
                return Parse(
                    xs,
                    bracket_parent,//树节点返回上一级
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
                    BracketExp* parent=static_cast<BracketExp*>(bracket->First());
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
                Node* n_rest=NULL;
                if(deal)
                {
                    Exp* e=new Exp(tp,x->Value(),x->Index());
                    e->original_type=x->Type();
                    n_rest=new Node(e,rest);
                }else
                {
                    n_rest=rest;
                }
                return Parse(
                    xs,
                    bracket,//树节点不变
                    n_rest//子列表追加
                );
            }
        }
    }
    

    BracketExp* Parse(Node* tokens){
        BracketExp *exp=new BracketExp(parse::Type::Large,"{}",NULL,0);//缓存子列表
        Node *tree=new Node(exp,NULL);
        //还必须在Parse前后retain和release，因为参数了树节点的动作？模拟有一个引用着它。
        tree->retain();
        BracketExp *result=Parse(tokens,tree,NULL);
        tree->release();
        return result;
    }
};