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
                return str::stringToEscape(value);
            }else{
                return value;
            }
        }
        Base_type xtype(){
            return Base_type::xExp;
        }
    };
    class BracketExp:public Exp{
    private:
        Node *children;
        /*减少计算时的反转*/
        Node *r_children;
    public:
        BracketExp(parse::Type type,string value,Node * children,int index,Node* r_children=NULL)
            :Exp(type,value,index){
            this->children=children;
            this->r_children=r_children;
            if(children!=NULL)
            {
                children->retain();
            }
            if(r_children!=NULL)
            {
                r_children->retain();
            }
        }
        Node* Children(){
            return children;
        }
        virtual bool isBracket(){
            return true;
        }

        Node* R_children(){
            return r_children;
        }

        virtual ~BracketExp(){
            if(children!=NULL)
            {
                children->release();
            }
            if(r_children!=NULL)
            {
                r_children->release();
            }
        }

        virtual string toString(){
            char a[2]={Value()[0],'\0'};
            string x=string(a);
            for(Node * t=children;t!=NULL;t=t->Rest())
            {
                Exp *e=static_cast<Exp*>(t->First());
                x+=e->toString();
                x+=" ";
            }
            x+=Value()[1];
            return x;
        }
    };
}