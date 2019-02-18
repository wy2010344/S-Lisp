

{
	`从util里调入`
	(let 
		`
		Value 
		Watcher 
		key:children路径
		appendChild 
		insertChildBefore(pel,new_el,old_el)
		removeChild
		before 可选
		after 可选
		`
		(p) args
		childOperate (load './childOperate.lisp)
		buildArray (load './buildArray.lisp)
		buildModel (load './buildModel.lisp)
		type-key (str-join ['p.key - type])
		p-before (default p.before empty-fun)
		p-after (default p.after empty-fun)
		getOArray {
			(let (row i) args)
			[
				data (p.Value row)
				index 'i
			]
		}
		updateArrayData {
			(let (view data) args)
			(view.row.data data)
		}
		getOModel {
			(let (row i) args)
			[
				data 'row
				index (p.Value i)
			]
		}
		updateModelIndex {
			(let (view index) args)
			(view.row.index index)
		}
	)
	{
		(let 
			(e x o) args
			children (kvs-find1st o.json p.key)
		)
		(if-run (str-eq 'kvs (kvs-find1st o.json type-key))
			{
				(if-run (exist? children.array)
					{
						(let
							`是否初始化`
							isInit (cache false)
							`未初始化时缓存值`
							c-inits (cache [])
							(bc-after bc-destroy) 
								(buildArray 
									[
										build (childOperate.build e children.repeat x.mve getOArray)
										no_cache 'p.no_cache
										after {
											(let (view) args)
											(let init (childOperate.getInit view))
											(if-run (isInit)
												{(init)}
												{
													(c-inits (extend init (c-inits)))
												}
											)
										}
										update-data 'updateArrayData
										destroy 'childOperate.destroy
										appendChild {
											(let (view) args)
											(p.appendChild e.pel (view.obj.getElement))
										}
										removeChild {
											(let (view) args)
											(p.removeChild e.pel (view.obj.getElement))
										}
									]
								)
							`Array的计算观察`
							watch 
								(p.Watcher
									[
										before {
											(p-before e.pel)
										}
										exp {
											(children.array)
										}
										after {
											(bc-after (first args))
											(p-after e.pel)
										}
									]
								)
						)
						[
							k 'o.k 
							inits (extend 
								{
									(forEach (c-inits) call)
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
						(if-run (exist? children.model)
							{
								(let bm 
									(buildModel [
										build (childOperate.build e children.repeat x.mve getOModel)
										model 'children.model
										update-index 'updateModelIndex
										init 'childOperate.init
										destroy 'childOperate.destroy
										insertChildBefore {
											(let (new_view old_view) args)
											(p.insertChildBefore e.pel (new_view.obj.getElement) (old_view.obj.getElement))
										}
										removeChild {
											(let (view) args)
											(p.removeChild e.pel (view.obj.getElement))
										}
										appendChild {
											(let (view) args)
											(p.appendChild e.pel (view.obj.getElement))
										}
									])
								)
								[
									k 'o.k
									inits (extend bm.init o.inits)
									destroys (extend bm.destroy o.destroys)
								]
							}
							{
								(log "error,需要array或model")
								o
							}
						)
					}
				)
			}
			{
				(reduce children
					{
						(let 
							(init child) args
							obj (x.Parse x [
								json 'child
								e 'e
								k 'init.k
								inits 'init.inits
								destroys 'init.destroys
							])
						)
						(p.appendChild e.pel obj.element)
						obj
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