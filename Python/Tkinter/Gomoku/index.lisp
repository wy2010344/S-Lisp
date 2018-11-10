
{
	(let 
		x (load '../tk.lisp)
		w (x.run 'Tk)
		chees_board (x.c 'LabelFrame w 
			[text "Chess Board" padx 5 pady 5]
		)
		chess_canvas (x.c 'Canvas chess_board
			[width 480 height 600]
		)
		view (load './view.lisp)
		create-circle {
			`
			绘制点
			`
			(let (point ext) args r 5)
			(py-call chess_canvas 'create_oval
				(list
					(- point.pixel-x r)
					(- point.pixel-y r)
					(+ point.pixel-x r)
					(+ point.pixel-y r)
				)
				ext
			)
		}
		points (view.create-board-points 15 30 create-circle)
	)
	
	`
	创建棋盘:其实没有意义
	`
	(loop
		{
			(let (i) args)
			(log i)
			(py-call chess_canvas 'create_line 
				[
					(* i 30)
					30
					(* i 30)
					(* 15 30)
				]
				[fill black]
			)
			(py-call chess_canvas 'create_line 
				[
					30
					(* i 30)
					(* 15 30)
					(* i 30)
				]
				[fill black]
			)
			(list
				(< i 15)
				(+ i 1)
			)
		}
		1
	)
	(py-call chess_canvas 'bind 
		[
			"<Button-1>"
			(py-function 
				{
					(let 
						(e) args
						x (py-attr e 'x)
						y (py-attr e 'y)
						point
							(first
								(loop `这个算法其实不太好，遍历太费时！`
									{
										(let 
											((r ...rs)) args
											find
												(first 
													(loop
														{
															(let ((c ...cs)) args)
															(let all 
																(+ 
																	(view.pow 
																		(- x c.pixel-x)
																	    2
																	 )
																	 (view.pow
																	    (- y c.pixel-y)
																		2
																	)
																)
															)
															`找到，返回该节点`
															(if-run (< all 25)
																{
																	(list 
																		false
																		c
																	)
																}
																{
																	`存在，继续遍历；不存在，返回空`
																	(if-run (exist? cs)
																		{
																			(list
																				true
																				cs
																			)
																		}
																		{
																			(list
																				false
																				[]
																			)
																		}
																	)
																}
															)
														}
														r
													)
												)
										)
										(if-run (exist? find)
											{(list false find)}
											{
												(if-run (exist? rs)
													{(list true rs)}
													{(list false [])}
												)
											}
										)
									}
									points
								)
							)
					)
					(log point)
					(if-run (exist? point)
						{
							(create-circle point [fill black])
						}
					)
				}
			)
		]
	)
	(py-call chess_canvas 'pack [] [anchor center])
	(py-call chees_board 'pack [] [anchor center])
	(x.center-window w 800 600)
	(py-call w 'mainloop)
}