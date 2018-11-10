
(let 
	math (py-eval "__import__('math')")
)
[

pow {
	(py-call math 'pow args)
}
`
max:最大网格数
space 网格间距
`
create-board-points 
	{
		(let (max space run) args)
		(offset
			(loop 
				{
					(let (x rows) args)
					(list
						(< x max)
						(+ x 1)
						(extend 
							(offset
								(loop 
									{
										(let (y cols) args
											 point 
											 	[
													x 'x  
													y 'y
													pixel-x (* space x)
													pixel-y (* space y)
												]
										)
										(run point)
										(list
											(< y max)
											(+ y 1)
											(extend 
												point 
												cols
											)
										)
									}
									1 []
								)
								1
							)
							rows
						)
					)
				} 
			    1 []
			)
			1
		)
	}
]