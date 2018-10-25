namespace s{
    class Node;
    class Function:public Base{
    private:
        static String* s_args;
        static String* s_this;
    public:
        Function():Base(){}
        virtual Base * exec(Node * args)=0;
        S_Type stype(){
            return Base::sFunction;
        }
        enum Fun_Type{
            fBuildIn,//内置函数
            //fBetter,//迁移到C++优化函数
            fUser,//用户函数，只有一个
            fCache//cache函数，只有一个
        };
        virtual Fun_Type ftype()=0;
        static String* S_args(){
            return s_args;
        }
        static String* S_this(){
            return s_this;
        }
    };
    String* Function::s_this=new String("this");
    String* Function::s_args=new String("args");
};
