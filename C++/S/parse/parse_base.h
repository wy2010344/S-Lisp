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
    class IDExp:public Exp{
        Node* paths;
    public:
        IDExp(string value,Location* loc):Exp(Exp::Exp_Id,value,loc){
            if(value[0]=='.' || value[value.size()-1]=='.'){
                paths=NULL;
            }else{
                unsigned i=0;
                unsigned last_i=0;
                Node * r=NULL;
                bool has_error=false;
                while(i<value.size()){
                    char c=value[i];
                    if(c=='.'){
                        string node=value.substr(last_i,i-last_i);
                        last_i=i+1;
                        if(node==""){
                            has_error=true;
                        }else{
                            r=new Node(new String(node),r);
                        }
                    }
                    i++;
                }
                r=new Node(new String(value.substr(last_i)),r);
                if(has_error){
                    throw new LocationException(value+"不是合法的ID，不允许.连续",loc);
                }else{
                    paths=list::reverseAndDelete(r);
                }
            }
        }
        Node * Paths(){
            return paths;
        }

        virtual ~IDExp(){
            if(paths!=NULL){
                paths->retain();
                paths->release();
            }
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
