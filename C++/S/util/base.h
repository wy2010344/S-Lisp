#pragma once
namespace s{
    class Base{
#ifdef DEBUG
    private:
        class LNode{
        public:
            Base *value;
            LNode *next;
        };
        static LNode* lnode;

        static int addsize;//增加的
        static int subsize;//减少的
        static int last_addsize;//上一次的减少
        static int last_subsize;//上一次的增加
    public:
        /*回收*/
        static void clear(){
            while(lnode!=NULL)
            {
                Base * b=lnode->value;
                b->retain();
                while(b->ref_count()!=1){
                    b->release();
                }
                b->release();
            }
        }
        static void print_node(){
            int add=Base::addsize-Base::last_addsize;
            int sub=Base::subsize-Base::last_subsize;
            cout<<"本次新增:"<<add<<" ";
            cout<<"本次减少:"<<sub<<" ";
            cout<<"本次净增:"<<(add-sub)<<" ";
            cout<<"生存数量:"<<(Base::addsize-Base::subsize)<<" ";
            cout<<endl;
            Base::last_addsize=Base::addsize;
            Base::last_subsize=Base::subsize;
        }
        int id;
        static void add_to_link(Base * b){
            addsize++;
            b->id=addsize;
            LNode *n=new LNode();
            n->value=b;
            n->next=lnode;
            lnode=n;
        }
        static void remove_on_link(Base *b){
            subsize++;

            if(lnode!=NULL){
                //有可能在链上找不到
                LNode *t=lnode;
                if(t->value==b)
                {
                    lnode=t->next;
                    delete t;
                }else{
                    LNode *pre=t;
                    t=t->next;
                    bool will=true;
                    while(t!=NULL && will)
                    {
                        if(t->value==b)
                        {
                            pre->next=t->next;
                            delete t;
                            will=false;
                        }else{
                            pre=t;
                            t=t->next;
                        }
                    }
                }
            }
        }
#else
        static void clear(){
        }
        static void eval_clear(){
        }
        static void print_node(){
        }
        static void add_to_link(Base * b){
        }
        static void remove_on_link(Base *b){
        }
#endif
        int count;
    public:
        Base(){
            count=0;
            //cout<<"正在生成:"<<id<<endl;
            add_to_link(this);
        }
        /**
        retain与release是替代new与delete
        构造时默认retain，就像JAVA中Object默认占用内存，必须手动release一次
        在哪儿构造的在哪去销毁。

        但是Node等，每次添加时，增加了一次引用，则原New的地方没有销毁到
        New与Release必须成对存在。
        它的作用域并不是自动retain和release的，尤其是传统的语句占用原变量名。
        所以应对的时候还是C++的手动编程。
        */
        void retain(){
            count++;
        }
        //在函数中特殊的存在，恢复成无计数状态。
        void eval_release(){
            count--;
        }
        void release()
        {
            count--;
            if(count==0)
            {
                delete this;//删除自身
                //this->~Base();
            }
            /*
            else
            if(count<0){
                cout<<id<<"小于0:"<<count<<endl;
            }
            */
        }
        virtual ~Base(){
            remove_on_link(this);
            //cout<<"销毁:"<<id<<endl;
        } /*
        对base进行区分
        */
        enum S_Type{
            sList,
            sInt,
            sString,
            sBool,
            sFunction,

            sUser,
            sToken,
            sExp,
            sLocation
        };
        int ref_count(){
            return count;
        }
        virtual S_Type stype()=0;
        virtual string toString()=0;
    };
#ifdef DEBUG
    int Base::addsize=0;
    int Base::subsize=0;
    int Base::last_addsize=0;
    int Base::last_subsize=0;
    Base::LNode * Base::lnode=NULL;
#endif
};
