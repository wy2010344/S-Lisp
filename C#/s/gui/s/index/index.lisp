
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

            list-array (me.Value
                [
                    [left x1 right x2]
                    [left 11 right 12]
                    [left 34 right 5i]
                ]
            )
        )
        [
            width 'width
            height 'height
            element [
                type div
                children [
                    [
                        type list-view
                        id list-view
                        attr [
                            Dock Fill
                            View Details
                            FullRowSelect true
                            `CheckBoxes true`
                        ]
                        columns [
                            [
                                type col
                                text left
                            ]
                            [
                                type col
                                text right
                            ]
                        ]
                        rows-type kvs
                        rows [
                            array 'list-array
                            repeat {
                                (let o args)
                                [
                                    type row
                                    text {
                                        (let x (o.data))
                                        (str-join [(toString (o.index)) - (toString x.left)])
                                    }
                                    children [
                                        [
                                            type cell
                                            text {
                                                (let x (o.data))
                                                (toString x.right)
                                            }
                                        ]
                                    ]
                                ]
                            }
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
                                id left
                                value 98
                            ]
                            [
                                type input
                                id right
                                value 99
                            ]
                            [
                                type button
                                text 增加
                                event [
                                    click {
                                        (let 
                                            left (me.DOM.value (me.k 'left))
                                            right (me.DOM.value (me.k 'right))
                                        )
                                        (if-run (or (str-eq left "") (str-eq right ""))
                                            {
                                                (me.DOM.alert "不允许为空")
                                            }
                                            {
                                                (list-array 
                                                    (extend 
                                                        [left 'left right 'right] 
                                                        (list-array)
                                                    )
                                                )
                                                (me.DOM.value (me.k 'left) "")
                                                (me.DOM.value (me.k 'right) "")
                                            }
                                        )
                                        (log left right)
                                    }
                                ]
                            ]
                            [
                                type button
                                text 删除
                                event [
                                    click {
                                        (let 
                                            list-view (me.k 'list-view)
                                            selectedIndexs (me.DOM.list-view-selectedIndexs list-view)
                                        )
                                        (log selectedIndexs)
                                        (if-run (exist? selectedIndexs)
                                            {
                                                (list-array 
                                                    (reverse 
                                                        (first
                                                            (reduce (list-array)
                                                                {
                                                                    (let ((init i) row) args)
                                                                    (list 
                                                                        (if-run (empty? (indexOf selectedIndexs i =))
                                                                            {(extend row init)}`不在选择中，保留`
                                                                            {init}`在选择中，删除`
                                                                        )
                                                                        (+ i 1)
                                                                    )
                                                                }
                                                                [[] 0]
                                                            )
                                                        )
                                                    )
                                                )
                                                `选择置空`
                                                (me.DOM.list-view-selectedIndexs list-view [])
                                                (me.DOM.list-view-checkedIndexs list-view [])`check置空`
                                            }
                                            {
                                                (me.DOM.alert "无删除数据")
                                            }
                                        )
                                    }
                                ]
                            ]
                        ]
                    ]
                ]
            ]
            element12 [ 
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
                                        Text {
                                            (str-join 
                                                [
                                                    (toString (o.index))
                                                    (o.data)
                                                ]
                                            )
                                        }
                                    ]
                                    event [
                                        click {
                                            (array (splice (array) (o.index) 1))
                                        }
                                    ]
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
                                attr [
                                    Text { 
                                        (str-join 
                                            [
                                                写字1 
                                                (toString (a)) 
                                                xo 
                                                (toString (len (array)))
                                            ]
                                        ) 
                                    }
                                ]
                                event [
                                    click {
                                        (log '好 (a))
                                        (a (+ (a) 1))
                                    }
                                ]
                            ]
                            [
                                type input 
                                id input
                                attr [
                                    Text 好
                                ]
                            ]
                            [
                                type button 
                                attr [
                                    Text { 
                                        (str-join 
                                            [
                                                共
                                                (toString (len (array)))
                                                条记录
                                            ]
                                        )
                                    }
                                ]
                                event [
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