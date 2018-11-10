[
    type [
        cpp [
            other 
            ({
                (let (init def out release)
                    (reduce 
                        [
                            list
                            string
                            function
                            int
                            bool
                            user
                            token
                            exp
                            location
                        ]
                        {
                            (let ((init def out release) v) args)
                            (list
                                (extend (str-join ["s_" 'v "=new String(\"" 'v "\");" "s_" 'v "->retain();"]) init)
                                (extend (str-join ["String* s_" 'v ";"]) def)
                                (extend (str-join ["String* S_" 'v "(){ return s_" 'v ";}"]) out)
                                (extend (str-join ["s_" 'v "->release();"]) release)
                            )
                        }
                        [[] [] [] []]
                    )
                )
                (str-join
                    [
                        "TypeFun(){"
                            (str-join init "\n")
                        "}"
                        "~TypeFun(){"
                            (str-join release "\n")
                        "}"
                        (str-join out "\n")
                        "
                        String* base_run(Base* b){
                            String* s;
                            if(b==NULL){
                                s=s_list;
                            }else{
                                Base::S_Type t=b->stype();
                                if(t==Base::sList){
                                    s=s_list;
                                }else
                                if(t==Base::sFunction){
                                    s=s_function;
                                }else
                                if(t==Base::sInt){
                                    s=s_int;
                                }else
                                if(t==Base::sString){
                                    s=s_string;
                                }else
                                if(t==Base::sBool){
                                    s=s_bool;
                                }else
                                if(t==Base::sUser){
                                    s=s_user;
                                }else
                                {
                                    if(t==Base::sToken){
                                        s=s_token;
                                    }else
                                    if(t==Base::sExp){
                                        s=s_exp;
                                    }else
                                    if(t==Base::sLocation){
                                        s=s_location;
                                    }
                                }
                            }
                            return s;
                        }
                        private:
                        "
                        (str-join def "\n")
                    ]
                    "\n"
                )
            })
            run "
                Base *b=args->First();
                return base_run(b);
            "
        ]
        C# [
            other "
                public static String base_run(Object b){
                    if(b==null){
                        return \"list\";
                    }else{
                        if(b is Node<Object>)
                        {
                            return \"list\";
                        }else if(b is Function)
                        {
                            return \"function\";
                        }else if(b is int)
                        {
                            return \"int\";
                        }else if(b is String)
                        {
                            return \"string\";
                        }else if(b is bool)
                        {
                            return \"bool\";
                        }else{
                            if(b is Token)
                            {
                                return \"token\";
                            }else if(b is Exp)
                            {
                                return \"exp\";
                            }else if(b is Location)
                            {
                                return \"location\";
                            }else{
                                return \"user\";
                            }
                        }
                    }
                }
            "
            run "
                Object b=args.First();
                return base_run(b);
            "
        ]
        js [
            other "
                TypeFun.base_run=function(n){
                    if(n==null){
                        return \"list\";
                    }else{
                        if(p.isList(n)){
                            return \"list\";
                        }else
                        if(p.isFun(n)){
                            return \"function\";
                        }else{
                            var t=typeof(n);
                            if(t==\"string\"){
                                return \"string\";
                            }else
                            if(t==\"boolean\"){
                                return \"bool\";
                            }else
                            if(t==\"number\"){
                                if(n%1===0){
                                    return \"int\";
                                }else{
                                    return \"float\";
                                }
                            }else{
                                return t;
                            }
                        }
                    }
                }
            "
            run "
                var n=args.First();
                return TypeFun.base_run(n);
            "
        ]
        OC [
            other "
            +(NSString*)base_run:(NSObject*)n{
                if(n==nil){
                    return @\"list\";
                }else{
                    if([n isKindOfClass:[SNode class]]){
                        return @\"list\";
                    }else
                    if([n isKindOfClass:[SFunction class]]){
                        return @\"function\";
                    }else
                    if([n isKindOfClass:[SBool class]]){
                        return @\"bool\";
                    }else
                    if([n isKindOfClass:[NSString class]]){
                        return @\"string\";
                    }else
                    if([n isKindOfClass:[NSNumber class]]){
                        return @\"int\";
                    }else{
                        return @\"\";
                    }
                }
            }
            "
            run "
                return [S_TypeFun base_run:[args First]];
            "
        ]
        python [
            other "
    @staticmethod
    def base_run(o):
        if (o==None or isinstance(o,Node)):
            return \"list\"
        elif isinstance(o,Function):
            return \"function\"
        elif isinstance(o,bool):
            return \"bool\"
        elif isinstance(o,basestring):
            return \"string\"
        elif isinstance(o,int):
            return \"int\"
        else:
            return \"\"
            "
            run "
            return TypeFun.base_run(args.First())
            "
        ]
    ]
]