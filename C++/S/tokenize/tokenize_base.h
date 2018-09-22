#pragma once
namespace s{
    class Token:public Base{
    public:
        enum Token_Type{
            Token_BracketLeft,
            Token_BracketRight,
            Token_Comment,
            Token_Prevent,
            Token_String,
            Token_Id,
            Token_Int
        };
        Token(string value,Token_Type type,Location* loc):Base(){
            //cout<<"TOKEN:"<<value<<"  "<<type<<endl;
            this->value=value;
            this->type=type;
            this->loc=loc;
            loc->retain();
        }
        string & Value(){
            return this->value;
        }
        virtual Token_Type token_type(){
            return this->type;
        }
        Location* Loc(){
            return this->loc;
        }
        S_Type stype(){
            return Base::sToken;
        }
        virtual string toString(){
            return "$Token";
        }
        virtual ~Token(){
            loc->release();
        }
    private:
        Location* loc;
        string value;
        Token_Type type;
    };
    class Code
    {
        unsigned i;
        const string & code;
        unsigned maxLength;
        unsigned row;
        unsigned col;
        const char split;
        char c;
    public:
        Code(const string & txt,char linesplit):
            code(txt),
            maxLength(txt.size()),
            split(linesplit){
            i=-1;
            row=0;
            col=0;
            shift();
        }
        void shift(){
            i++;
            if(i<maxLength){
                c=code[i];
                if(c==split){
                    col=0;
                    row++;
                }else{
                    col++;
                }
            }else{
                c=' ';
            }
        }
        bool noEnd(){
            return i<maxLength;
        }
        char current(){
            return c;
        }
        unsigned index(){
            return i;
        }
        string substr(unsigned start,unsigned end){
            return code.substr(start,end-start);
        }
        Location* currentLoc(){
            return new Location(row,col,i);
        }
    };
    class TokenizeBase{
    public:
        static bool isBlank(char c)
        {
            return (c==' ' || c=='\t' || c=='\r' || c=='\n');
        }
        static bool notNumber(char c)
        {
            return ('0'>c || c>'9');
        }
        static bool isQuoteLeft(char c){
            return (c=='(' || c=='[' || c=='{');
        }
        static bool isQuoteRight(char c){
            return (c=='}' || c==']' || c==')');
        }

        static bool isInt(const string & id)
        {
            bool ret=true;
            for(unsigned i=0;i<id.size();i++)
            {
                char c=id[0];
                if(notNumber(c)){
                    ret=false;
                }
            }
            return ret;
        }
        /*
        各种自定义类型，未来如果支持负数、小数，从这里扩展，乃至如red语言中支持邮箱、路径等类型。
        但数值计算始终是属于函数对字符串的处理。宿主语言库提供优化的数值计算函数。
        因为s-lisp无强类型，只有运行时动态检查出类型，跟动态用字符串转化为特定类型一样的报错体验。
        */
        Token* deal_id(
            Code *code,
            Location* loc){
            unsigned start=loc->index();
            string Id=code->substr(start,code->index());
            Token *token;
            if (Id[0]=='\'') {
                //阻止求值
                if(Id.size()==1){
                    throw new LocationException("单个'不允许",loc);
                }else{
                    token=new Token(Id.substr(1,Id.size()-1),Token::Token_Prevent,loc);
                }
            }else
            if (isInt(Id)){
                //转成Int，方便数值计算
                token=new Token(Id,Token::Token_Int,loc);
            }else
            {
                //ID类型
                token=new Token(Id,Token::Token_Id,loc);
            }
            return token;
        }

        virtual Node* run(const string& txt,const char linesplit)=0;
    };
}
