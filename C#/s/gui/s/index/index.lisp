
(log 98)
{
    (log 98)
    (mve {
        (let (Value k Cache Watch) args)
        (let 
            a (Value 9)
            array (Value 
                [
                    a b c d e f
                ]
            )
        )
        [
            element [ 
                type div
                children [
                    [
                        type flow
                        attr [
                            dock Fill
                            back-color #FFC10710
                        ]
                        children-type kvs
                        children [
                            array 'array
                            repeat {
                                (let (data index) args)
                                [
                                    type button
                                    attr [
                                        back-color #ffeb3b
                                    ]
                                    action [
                                        click {
                                            (array (splice (array) (index) 1))
                                        }
                                    ]
                                    text {
                                        (str-join 
                                            [
                                                (stringify (index))
                                                (data)
                                            ]
                                        )
                                    }
                                ]
                            }
                        ]
                    ]
                    [
                        type flow
                        attr [
                            dock Bottom
                            back-color #66339910
                            height 30
                        ]
                        children [
                            [
                                type button
                                text { 
                                    (str-join 
                                        [
                                            写字1 
                                            (stringify (a)) 
                                            xo 
                                            (stringify (len (array)))
                                        ]
                                    ) 
                                }
                                action [
                                    click {
                                        (log '好 (a))
                                        (a (+ (a) 1))
                                    }
                                ]
                            ]
                            [
                                type input 
                                id input
                                value 好
                            ]
                            [
                                type button 
                                text { 
                                    (str-join 
                                        [
                                            共
                                            (stringify (len (array)))
                                            条记录
                                        ]
                                    )
                                }
                                action [
                                    click {
                                        (let
                                            input (k 'input)
                                            v (DOM 'value input)
                                        )
                                        (if-run (str-eq v "")
                                            {
                                                (DOM 'alert "不允许为空")
                                            }
                                            {
                                                (array  (extend v (array)))
                                                (DOM 'value input "")
                                            }
                                        )
                                    }
                                ]
                            ]

                        ]
                    ]
                ]
            ]
        ]
    })
}