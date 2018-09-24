`
    将名字转成符合语法的
`
{
    (let (k) args)
    (let b 
        (if
            (str-eq 
                (str-charAt k 
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
        }[Fun])
    )
    (str-join (extend b vs))
}