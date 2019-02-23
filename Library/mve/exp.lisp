{
	(let 
		util (load './util.lisp)
		(DOM Parse) args
		`供后面inits和destroys使用`
		forEach-run {
			(forEach (first args) call)
		}
		ret {
		(let (user-func) args mve this)
		{
			`pel replaceChild(pel,old_el,new_el)`
			(let e args)
			(let watchPool (cache []))
			(let Watch 
				{
					(let w (apply util.Watcher args))
					(watchPool 
						(extend w 
							(watchPool)
						)
					)
					w
				}
			)
			(let Cache 
				{
					(util.Cache Watch (first args))
				}
			)
			(let k (cache []))
			`用户函数返回`
			(let user-result 
				(apply 
					user-func 
					[
						Value 'util.Value
						ArrayModel 'util.ArrayModel
						k {
							(let (str) args)
							(kvs-find1st (k) str)
						}
						Cache 'Cache
						Watch 'Watch
						DOM 'DOM
					]
				)
			)
			(let me
				(kvs-reduce user-result.out
					{
						(let (init v k) args)
						(kvs-extend k v init)
					}
					me
				)
			)
			(let 
				(getElement c_k element-inits element-destroys) 
				(Parse 
					e
					user-result.element
					`Watch 给内部使用的`
					{
						(let w  (apply util.Watcher args))
						(watchPool 
							(extend w 
								(watchPool)
							)
						)
						w
					}
					mve
					k
				)
			)
			(k c_k)
			(let 
				user-init (default user-result.init empty-fun)
				user-destroy (default user-result.destroy empty-fun)
			)
			(kvs-reduce
				[ 
					getElement 'getElement
					init {
						(forEach-run element-inits)
						(user-init)
					}
					destroy {
						(user-destroy)
						(forEach-run element-destroys)
						(forEach (watchPool) 
							{
								(let (w) args)
								(w.disable)
							}
						)
					}
				]
				{
					(let (init value k) args)
					(kvs-extend k value init)
				}
				me
			)
		}
	})
	ret
}