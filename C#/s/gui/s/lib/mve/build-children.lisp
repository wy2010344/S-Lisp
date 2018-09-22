

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
				(let o [ (Value row) (Value i) ] )
				(list 
					o
					(mve 
						{
							[
								element {
									(apply repeat o)
								}
							]
						}
					)
				)
			}
		}
		getEl {
			(let ((`data index`) (getElement)) (first args))
			(getElement)
		}
		getInit {
			(let ((`data index`) (getElement init destroy)) (first args))
			init
		}
		getDestroy {
			(let ((`data index`) (getElement init destroy)) (first args))
			destroy
		}
	)
	{
		(let 
			(pel children inits destroys mve) args
			`c.array c.repeat`
			cd (kvs-match children)
			`是否初始化`
			isInit (cache false)
			`未初始化时缓存值`
			c-inits (cache [])
			(bc-after bc-destroy) 
				(nokey 
					`build` 
					(build (cd 'repeat) mve)
					`after` 
					{
						(let init (apply getInit args))
						(if-run (isInit)
							{(init)}
							{
								(c-inits (extend init (c-inits)))
							}
						)
					}
					`getDestroy`
					getDestroy
					`appendChild` 
					{
						(DOM 'appendChild pel (apply getEl args))
					}
					`removeChild` 
					{
						(DOM 'removeChild pel (apply getEl args))
					}
				)
			`Array的计算观察`
			watch 
				(Watcher 
					`before`
					[]
					`exp`
					{
						((cd 'array))
					}
					`after` 
					{
						(bc-after (first args))
					}
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
					(let (id update disable) watch)
					(disable)
					(bc-destroy)
				} 
				destroys
			)
		)
	}
}