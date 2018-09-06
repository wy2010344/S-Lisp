#pragma once
namespace s{
    class Exp:public Base{
    public:
        enum Exp_Type{
            Exp_Large,
            Exp_Medium,
            Exp_Small,
            Exp_String,
            Exp_Int,
            Exp_Id//,Comment//不要Comment
        };
    private:
        Exp_Type type;
        string value;
        Location* loc;
    public:
        Exp(Exp_Type type,string value,Location* loc):Base(){
            this->type=type;
            this->value=value;
            this->loc=loc;
            loc->retain();
        }
        virtual ~Exp(){
            loc->release();
        }
        string & Value(){
            return value;
        }
        Exp_Type exp_type(){
            return type;
        }
        Location* Loc(){
            return loc;
        }
        virtual bool isBracket(){
            return false;
        }
        Token::Token_Type original_type;
        virtual string toString(){
            if(original_type==Token::Token_Prevent){
                return "'"+value;
            }else
            if(type==Exp::Exp_String)
            {
                return str::stringToEscape(value,'"','"');
            }else{
                return value;
            }
        }
        S_Type stype(){
            return Base::sExp;
        }
    };
    class BracketExp:public Exp{
    private:
        Node *children;
        /*减少计算时的反转*/
        Node *r_children;
    public:
        BracketExp(Exp_Type type,string value,Node * children,Location* loc,Node* r_children=NULL)
            :Exp(type,value,loc){
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
