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

(let build-cls 
    {
        (let (key cpp-run type toString) args)
        [
            "\n
            class " (quote key) ": public LibFunction {
            private:
                static "(quote key)" * _in_;
            public:    
                static "(quote key)"*instance(){
                    return _in_;
                }
                string toString(){
                    return \"" (toString)"\";
                }
                Function_type ftype(){
                    return Function_type::"(quote type)";
                }
            protected:
                Base * run(Node * args){
                    "(quote cpp-run)"
                }
            };
            "(quote key)"* "(quote key)"::_in_=new "(quote key)"();
            "                      
        ]
    }
    build-m {
        (let (k key) args)
        [
            "
            m=kvs::extend(\"" (quote k) "\"," (quote key) "::instance(),m);"
        ]
    }
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
                (let key (kvs-path v [alias]))
                (let key 
                    (if-run (exist? key) 
                        {key} 
                        {(transKey k)}
                    ) 
                )
                (list 
                    (extend 
                        (str-join 
                            (build-cls 
                                key 
                                cpp-run 
                                'fBuildIn
                                {k}
                            )
                        )
                        cls
                    )
                    (extend
                        (str-join 
                            (build-m k key)
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
            return m;
        }
    };
};"
        ]
    )
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
                                            (build-cls 
                                                key 
                                                cpp-run 
                                                'fUser
                                                {(stringify (kvs-path v [lisp]))}
                                            )
                                        )
                                        cls
                                    )
                                    (extend
                                        (str-join 
                                            (build-m k key)
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
#include \"./system.h\"
namespace s{
    namespace library{
        "
        (str-join cls)
        "
        Node * better(){
            Node * m=library();
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