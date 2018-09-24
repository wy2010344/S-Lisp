

{
	`从util里调入`
	(let 
		(Value Watcher DOM nokey) args
	    build {
			`下面调入`
			(let (repeat mve) args)
			{
				`最终调入`
				(let (row i) args)
				(let 
					o [ 
						data (Value row) 
						index (Value i) 
					] 
				)
				[ 
					row 'o
					obj (mve 
							{
								[
									element {
										(apply repeat o)
									}
								]
							}
						)
				]
			}
		}
	)
	{
		(let 
			(pel children inits destroys mve) args
			`是否初始化`
			isInit (cache false)
			`未初始化时缓存值`
			c-inits (cache [])
			(bc-after bc-destroy) 
				(nokey 
					[
						build (build children.repeat mve)
						after {
							(let (value) args)
							(let init value.obj.init)
							(if-run (isInit)
								{(init)}
								{
									(c-inits (extend init (c-inits)))
								}
							)
						}
						update-data {
							(let (value v) args)
							(value.row.data v)
						}
						destroy {
							(let (value) args)
							(value.obj.destroy)
						}
						appendChild {
							(let (value) args)
							(DOM.appendChild pel (value.obj.getElement))
						}
						removeChild {
							(let (value) args)
							(DOM.removeChild pel (value.obj.getElement))
						}
					]
				)
			`Array的计算观察`
			watch 
				(Watcher 
					[
						exp {
							(children.array)
						}
						after {
							(bc-after (first args))
						}
					]
				)
		)
		(list 
			`inits`
			(extend 
				{
					(forEach (c-inits)
						{
							((first args))
						}
					)
					(c-inits [])
					(isInit true)
				} 
				inits
			) 
			`destroys`
			(extend 
				{
					(watch.disable)
					(bc-destroy)
				} 
				destroys
			)
		)
	}
}