namespace s{
    class Node;
    class Function:public Base{
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
    };
};
