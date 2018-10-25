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
            Exp_Id,//,Comment//不要Comment
            Exp_Let,
            Exp_LetId,//一般的ID
            Exp_LetSmall,//()
            Exp_LetRest//...
        };
    private:
        String* value;
        Location* loc;
    protected:
        Exp_Type type;
    public:
        Exp(Exp_Type type,String* value,Location* loc):Base(){
            this->type=type;
            this->value=value;
            value->retain();
            this->loc=loc;
            loc->retain();
        }
        virtual ~Exp(){
            value->release();
            loc->release();
        }
        String* Value(){
            return value;
        }
        /*
        *目前，主要是()->letSmall,letID,letRestID
        */
        void exp_type(Exp_Type type){
            this->type=type;
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
            string & v=value->StdStr();
            if(original_type==Token::Token_Prevent){
                return "'"+v;
            }else
            if(type==Exp::Exp_String)
            {
                return str::stringToEscape(v,'"','"');
            }else
            if(type==Exp::Exp_LetRest){
                return "..."+v;
            }else{
                return v;
            }
        }
        S_Type stype(){
            return Base::sExp;
        }
    };
    class IntExp:public Exp{
    private:
        Int* int_value;
    public:
        IntExp(String* value,Location* loc):Exp(Exp::Exp_Int,value,loc){
            int_value=new Int(value->StdStr());
            int_value->retain();
        }
        Int* Int_Value(){
            return int_value;
        }
        ~IntExp(){
            int_value->release();
        }
    };
    class IDExp:public Exp{
        Node* paths;
    public:
        IDExp(String* v,Location* loc):Exp(Exp::Exp_Id,v,loc){
            string & value=v->StdStr();
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
        BracketExp(Exp_Type type,String* value,Node * children,Location* loc,Node* r_children=NULL)
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
            char a[2]={Value()->StdStr()[0],'\0'};
            string x=string(a);
            for(Node * t=children;t!=NULL;t=t->Rest())
            {
                Exp *e=static_cast<Exp*>(t->First());
                x+=e->toString();
                x+=" ";
            }
            x+=Value()->StdStr()[1];
            return x;
        }
    };
}
