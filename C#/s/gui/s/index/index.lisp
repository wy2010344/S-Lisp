
(log 98)
{
    (log 98)
    (mve {
        (let me args)
        (let 
            a (me.Value 9)
            array (me.Value 
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
                                (let o args)
                                [
                                    type button
                                    attr [
                                        back-color #ffeb3b
                                    ]
                                    action [
                                        click {
                                            (array (splice (array) (o.index) 1))
                                        }
                                    ]
                                    text {
                                        (str-join 
                                            [
                                                (stringify (o.index))
                                                (o.data)
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
                                            input (me.k 'input)
                                            v (me.DOM.value input)
                                        )
                                        (if-run (str-eq v "")
                                            {
                                                (me.DOM.alert "不允许为空")
                                            }
                                            {
                                                (array  (extend v (array)))
                                                (me.DOM.value input "")
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