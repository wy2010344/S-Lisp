
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
            width (me.Value 0)
            height (me.Value 0)
        )
        [
            width 'width
            height 'height
            element12 [
                type div
                children [
                    [
                        type list-view
                        attr [
                            Dock Fill
                            View Details
                            FullRowSelect true
                        ]
                        children [
                            [
                                type columns
                                children [
                                    [
                                        type col
										text abfdaefa
                                    ]
                                    [
                                        type col
										text abfdaefa
                                    ]
                                ]
                            ]
                            [
                                type rows
                                children [
                                    [
                                        type row
										text dd
                                        children [
                                            [
                                                type cell
                                                text f9d8
                                            ]
                                            [
                                                type cell
                                                text frea
                                            ]
                                            [
                                                type cell
                                                text s9eaw8
                                            ]
                                        ]
                                    ]
                                    [
                                        type row
										text abf
                                        children [
                                            [
                                                type cell 
                                                text abc
                                            ]
                                            [
                                                type cell 
                                                text Pfeawfae
                                            ]
                                            [
                                                type cell 
                                                text efg
                                            ]
                                        ]
                                    ]
                                ]
                            ]
                        ]
                    ]
                    [
                        type flow
                        attr [
                            Dock Bottom
                            height 30
                        ]
                        children [
                            [
                                type input
                                value 98
                            ]
                            [
                                type button
                                text 增加
                            ]
                        ]
                    ]
                ]
            ]
            element [ 
                type div
                children [
                    [
                        type flow
                        attr [
                            Dock Fill
                            Height {
                                (- (height) 30)
                            }
                            WrapContents false
                            FlowDirection TopDown
                            AutoScroll true
                            BackColor #FFC10710
                        ]
                        children-type kvs
                        children [
                            array 'array
                            repeat {
                                (let o args)
                                [
                                    type button
                                    attr [
                                        Width {
                                            (- (width) 30)
                                        }
                                        BackColor #ffeb3b
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
                            Dock Bottom
                            BackColor #66339910
                            Height 30
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