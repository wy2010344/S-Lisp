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
            Exp_Bool,
            Exp_Id,//,Comment//不要Comment
            Exp_Let,
            Exp_LetId,//一般的ID
            Exp_LetSmall,//()
            Exp_LetRest//...
        };
    protected:
        Exp_Type type;
    public:
        Exp(Exp_Type type):Base(){
            this->type=type;
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
        virtual bool isBracket()=0;
        S_Type stype(){
            return Base::sExp;
        }
        virtual LocationException* exception(string msg)=0;
        virtual void warn(string msg)=0;
    };
    class AtomExp:public Exp{
    private:
        Token* token;
    public:
        AtomExp(Exp_Type type,Token* token):Exp(type){
            this->token=token;
            token->retain();
        }
        ~AtomExp(){
            token->release();
        }
        String* Value(){
            return token->Value();
        }
        Location* Loc(){
            return token->Loc();
        }

        LocationException* exception(string msg){
            return new LocationException(msg+":"+toString(),token->Loc());
        }

        void warn(string msg){
            cout<<"warn:"<<msg<<":"<<Loc()->toString()<<":"<<toString()<<endl;
        }
        virtual string toString(){
            return token->toString();
        }
        virtual bool isBracket(){
            return false;
        }
    };
    class IntExp:public AtomExp{
    private:
        Int* int_value;
    public:
        IntExp(Token *token):AtomExp(Exp::Exp_Int,token){
            int_value=new Int(token->Value()->StdStr());
            int_value->retain();
        }
        Int* Int_Value(){
            return int_value;
        }
        ~IntExp(){
            int_value->release();
        }
    };
    class BoolExp:public AtomExp{
    private:
        Bool* bool_value;
    public:
        BoolExp(Token *token):AtomExp(Exp::Exp_Bool,token){
            bool_value=Bool::trans(token->Value()->StdStr()=="true");
            bool_value->retain();
        }
        Bool* Bool_Value(){
            return bool_value;
        }
        ~BoolExp(){
            bool_value->release();
        }
    };
    class IDExp:public AtomExp{
        Node* paths;
    public:
        IDExp(Token *token):AtomExp(Exp::Exp_Id,token){
            string & value=token->Value()->StdStr();
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
                    throw new LocationException(value+"不是合法的ID，不允许.连续",token->Loc());
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
        Token * left;
        Token * right;
    public:
        BracketExp(
            Exp_Type type,
            Token * left,
            Token * right,
            Node * children,
            Node* r_children=NULL
        ):Exp(type){
            this->left=left;
            if(left!=NULL){
                left->retain();
            }
            this->right=right;
            if(right!=NULL){
                right->retain();
            }
            this->children=children;
            if(children!=NULL)
            {
                children->retain();
            }
            this->r_children=r_children;
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

        Token* Left(){
            return left;
        }
        Token* Right(){
            return right;
        }
        virtual ~BracketExp(){
            if(left!=NULL){
                left->release();
            }
            if(right!=NULL){
                right->release();
            }
            if(children!=NULL)
            {
                children->release();
            }
            if(r_children!=NULL)
            {
                r_children->release();
            }
        }
        LocationException* exception(string msg){
            return new LocationException(msg+":"+toString(),left->Loc(),right->Loc());
        }
        void warn(string msg){
            cout<<"warn:"<<msg<<":"<<left->Loc()->toString()<<right->Loc()->toString()<<":"<<toString()<<endl;
        }
        virtual string toString(){
            string x=left->Value()->StdStr();
            for(Node * t=children;t!=NULL;t=t->Rest())
            {
                Exp *e=static_cast<Exp*>(t->First());
                x+=e->toString();
                x+=" ";
            }
            x+=right->Value()->StdStr();
            return x;
        }
    };
}
