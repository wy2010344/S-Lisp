(let 
    `将lisp的key转成C++可以使用的`
    transKey {
        (let str-eq =)
        (let (k) args)
        (let b 
            (if
                (str-eq 
                    (char-at k 
                        (- (str-length k) 1)
                    )
                    '?
                )
                'Is
                ""
            )
        )
        (let vs (str-split k ""))
        (let 
            is?end (str-eq b "")
            b  (if is?end (str-upper (first vs)) b)
            vs  (if is?end (rest vs) vs)
        )
        (let vs 
            (reduce-right vs {
                (let (init v i) args)
                (let v 
                    (if 
                        (str-eq v '-) 
                        '_ 
                        (if 
                            (str-eq v '?) "" v
                        )
                    )
                )
                (extend v init)
            }[Func])
        )
        (str-join (extend b vs))
    }
)

(let 
    better (load './better.lisp)
    (cls fun) 
        (kvs-reduce
            better
            {
                (let (init v k i) args)
                (let (cls fun) init)
                (let cpp (kvs-path v [cpp]))
                (if-run (exist? cpp)
                    {
                        (let cpp-run (kvs-path cpp [run]))
                        (if-run (exist? cpp-run)
                            {
                                (let key (transKey k))
                                (list
                                    (extend 
                                        (str-join 
                                            [
        "\n
        class " (quote key) ": public LibFunction {
        public:    
            string toString(){
                return \"" (stringify (kvs-path v [lisp])) "\";
            }
            Function_type ftype(){
                return Function_type::fBetter;
            }
        protected:
            Base * run(Node * args){
                "(quote cpp-run)"
            }
        };"                      
                                            ]
                                        )
                                        cls
                                    )
                                    (extend
                                        (str-join 
                                            [
                            "
                            m=kvs::extend(\"" (quote k) "\",new " (quote key) "(),m);"
                                            ]
                                        )
                                        fun
                                    )
                                )
                            }
                            {init}
                        )
                    }
                    `如果没有C++的定义，不追加进定义`
                    {init}
                )
            }
            []
        )
)
`写better.h文件`
(write
    (calculate-path './better.h)
    (str-join
        [
"
#pragma once
namespace s{
    namespace library{
        "
        (str-join cls)
        "
        Node * better(Node *m){
            "
            (str-join fun)
            "
            return m;
        }
    }
};"
        ]
    )
)
`写system.h文件`
(let     
    system (load './system.lisp)
    (cls fun) 
        (kvs-reduce 
            system
            {
                (let (init v k i) args)
                (let (cls fun) init)
                (let cpp-run (kvs-path v [cpp run]))
                (let key (transKey k))
                (list 
                    (extend 
                        (str-join 
                            [
        "\n
        class " (quote key) ": public LibFunction {
        public:    
            string toString(){
                return \"" (quote k) "\";
            }
            Function_type ftype(){
                return Function_type::fBuildIn;
            }
        protected:
            Base * run(Node * args){
                "(quote cpp-run)"
            }
        };"                      
                            ]
                        )
                        cls
                    )
                    (extend
                        (str-join 
                            [
            "
            m=kvs::extend(\"" (quote k) "\",new " (quote key) "(),m);"
                            ]
                        )
                        fun
                    )
                )
            } 
            []
        )
)
(write 
    (calculate-path './system.h) 
    (str-join 
        [
"
#pragma once
#include \"./buildIn.h\"
#include \"./better.h\"
#include<fstream>
namespace s{
    namespace library{
            "
            `生成几个类体`
            (str-join cls)
            "
        Node * library(){
            Node * m=buildIn();
            "
            (str-join fun)
            "
            return better(m);
        }
    };
};"
        ]
    )
)