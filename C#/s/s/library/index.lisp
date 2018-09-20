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
        (let (key C#-run type toString) args)
        [
            "\n
            class "(quote key)":Function{
                private static "(quote key)" _ini_=new "(quote key)"();
                public static "(quote key)" instance(){return _ini_;}
                public override string ToString(){return \"" (toString) "\";}
                public override Function_Type Function_type(){return Function.Function_Type."(quote type)";}
                public override object exec(Node<object> args){
                    "(quote C#-run)"
                }
            }
            "
        ]
    }
    build-m {
        (let (k key) args)
        [
            "
            m=Node<Object>.kvs_extend(\""(quote k)"\","(quote key)".instance(),m);"
        ]
    }
)

(let
    system (load './system.lisp)
    (cls fun)
        (kvs-reduce
            system
            {
                (let (init v k i) args)
                (let (cls fun) init)
                (let C#-run (kvs-path v [C# run]))
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
                                C#-run
                                'Fun_BuildIn
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
    (pathOf './System.cs)
    (str-join
        [
"
using System;
using System.Collections.Generic;
using System.Text;

namespace s.library
{
    public class System
    {
    "
        (str-join cls)
    "
        public static Node<Object> library(){
            Node<Object> m = null;
            m = Node<Object>.kvs_extend(\"true\",true, m);
            m = Node<Object>.kvs_extend(\"false\", false, m);
            "
            (str-join fun)
            "
            return m;
        }
    }
}"        
        
        ]
    )
)


(let
    system (load './better.lisp)
    (cls fun)
        (kvs-reduce
            system
            {
                (let (init v k i) args)
                (let (cls fun) init)
                (let C#-run (kvs-path v [C# run]))
                (if-run (exist? C#-run)
                    {
                        (let key (kvs-path v [alias]))
                        (let key
                            (if-run (exist? key)
                                {key}
                                {(transKey k)}
                           )
                        )
                        (let lispfun (kvs-path v [lisp]))
                        (let (ftype toStr)
                            (if-run (exist? lispfun)
                                {
                                    [Fun_Better {(stringify lispfun)}]
                                }
                                {
                                    [Fun_BuildIn {k}]
                                }
                             )
                        )
                        (list
                            (extend
                                (str-join
                                    (build-cls
                                        key
                                        C#-run
                                        ftype
                                        toStr
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
            []
         )
)
(write
    (pathOf './Better.cs)
    (str-join
        [
"
using System;
using System.Collections.Generic;
using System.Text;

namespace s.library
{
    public class Better
    {
    "
        (str-join cls)
    "
        public static Node<Object> build(Node<Object> m){
            "
            (str-join fun)
            "
            return m;
        }
    }
}"        
        
        ]
    )
)