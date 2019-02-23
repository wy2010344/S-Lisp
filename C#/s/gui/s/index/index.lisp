{

	(mve {
		(let me args)
		(let array (me.Value [
				a b c d e f
			])
			width (me.Value 0)
			height (me.Value 0)
            list-array (me.Value
                [
                    [left x1 right x2]
                    [left 11 right 12]
                    [left 34 right 5i]
                ]
            )
            model-array (me.ArrayModel 
                [left x1 right x2]
                [left 11 right 12]
                [left 34 right 5i]
            )
		)
		[
			width 'width
			height 'height

            element1 [
                type div
                children [
                    [
                        type tab
                        attr [
                            Dock Fill
                        ]
                        pages-type kvs
                        pages [
                            model 'model-array
                            repeat {
                                (let o args)
                                [
                                    type tab-page
                                    text {
                                       (toString (o.index)) 
                                    }
                                    children [
                                        [
                                            type button
                                            text {
                                                (str-join [(toString (o.index)) - (toString o.data.left) - (toString o.data.right)])
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
                                type button
                                text 增加
                                event [
                                    click {
                                        (model-array.insert 1 [left abc right aewfawef])
                                    }
                                ]
                            ]
                            [
                                type button
                                text 减少
                                event [
                                    click {
                                        (model-array.remove 1)
                                    }
                                ]
                            ]
                        ]
                    ]
                ]
            ]
			element [
				type div
				children [
					[
						type list
						id list-view
						attr [
							Dock Fill
							View Details
							FullRowSelect true
						]
						columns [
							[
								type list-column
								text left
							]
							[
								type list-column
								text right
							]
						]

						rows-type kvs
						rows [
							array 'list-array
							repeat{
								(let o args)
								[
									type list-row
									text {
										(let x (o.data))
										(str-join [ (toString o.index) - (toString x.left)])
									}
									cells [
										[
											type list-cell
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
                                            selectedIndexs (me.DOM.attr list-view 'SelectedIndices)
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
                                                (me.DOM.attr list-view 'SelectedIndices [])
                                                (me.DOM.attr list-view 'CheckedIndices [])`check置空`
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
			element1 [
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
									text {
										(str-join
											[
												(toString o.index)
												(o.data)
											]
										)
									}
									event [
										click {
											(array (splice (array) o.index 1))
										}
									]
								]
							}
						]
					]
				]
			]
		]
	})
}