

{
	`从util里调入`
	(let 
		`
		Value 
		Watcher 
		key:children路径
		appendChild 
		removeChild
		before 可选
		after 可选
		`
		(p) args
		nokey (load './nokey.lisp)

	    build {
			`下面调入`
			(let (repeat mve) args)
			{
				`最终调入`
				(let (row i) args)
				(let 
					o [ 
						data (p.Value row) 
						index (p.Value i) 
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
		type-key (str-join ['p.key - type])
		p-before (default p.before empty-fun)
		p-after (default p.after empty-fun)
	)
	{
		(let 
			(pel x o) args
			children (kvs-find1st o.json p.key)
		)
		(if-run (str-eq 'kvs (kvs-find1st o.json type-key))
			{
				(let
					`是否初始化`
					isInit (cache false)
					`未初始化时缓存值`
					c-inits (cache [])
					(bc-after bc-destroy) 
						(nokey 
							[
								build (build children.repeat x.mve)
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
									(p.appendChild pel (value.obj.getElement))
								}
								removeChild {
									(let (value) args)
									(p.removeChild pel (value.obj.getElement))
								}
							]
						)
					`Array的计算观察`
					watch 
						(p.Watcher
							[
								before {
									(p-before pel)
								}
								exp {
									(children.array)
								}
								after {
									(bc-after (first args))
									(p-after pel)
								}
							]
						)
				)
				[
					k 'o.k 
					inits (extend 
						{
							(forEach (c-inits)
								{
									((first args))
								}
							)
							(c-inits [])
							(isInit true)
						} 
						o.inits
					) 
					destroys (extend 
						{
							(watch.disable)
							(bc-destroy)
						} 
						o.destroys
					)
				]
			}
			{
				(reduce children
					{
						(let 
							(init child) args
							obj (x.Parse x (kvs-extend 'json child init))
						)
						(p.appendChild pel obj.element)
						[
							k 'obj.k
							inits 'obj.inits
							destroys 'obj.destroys
						]
					}
					[
						k 'o.k 
						inits 'o.inits 
						destroys o.destroys
					]
				)
			}
		)
	}
}