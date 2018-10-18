
(let 
	bind {
		(let (watch value f) args)
		(if-run (type? value 'function)
				{
					(watch 
						[
							exp {
								(value)
							}
							after {
								(f (first args))
							}
						]
					)
				}
				{
					(f value)
				}
		)
	} 
	bindKV {
		(let (watch key value f) args)
		(bind watch value {
				(f key 
					(first args)
				)
			}
		)
	} 
	bindMap {
		(let (watch) args)
		{
			(let (map f) args)
			(if-run (exist? map)
				{
					(kvs-forEach map 
						{
							(let (v k) args)
							(bindKV watch k v f)
						}
					)
				}
			)
		}
	} 
	bindEvent {
		(let (map f) args)
		(if-run (exist? map)
				{
					(kvs-forEach map 
						{
							(let (v k) args)
							(f k v)
						}
					)
				}
		)
	} 
	if-bind {
		(let (watch) args)
		{
			(let (value f) args)
			(if-run 
				(exist? value)
				{
					(bind watch value f)
				}
			)
		}
	}
	build-locsize {
		(let (locsize json fun) args)
		(forEach 
			locsize 
			{
				(let 
					(str) args
					vf (kvs-find1st json str)
				)
				(if-run (exist? vf)
					(fun str vf)
				)
			}
		)
	}
	`供后面inits和destroys使用`
	forEach-run {
		(let (array) args)
		{
			(forEach array
				{
					((first args))
				}
			)
		}
	}
)
{
	(let (locsize p) args)
	`对函数`
	(let Parse-fun 
		{
			`
			[
				watch 
				inits 
				destroys 
				mve 
			]
			o 
			`
			(let (x o) args)
			(let change (cache []))
			(x.watch
				[
					exp { (o) }
					after {
						(let (element) args)
						(let newObj 
							(x.mve 
								{ 
									[ element 'element] 
								}
							)
						)
						(let obj (change))
						(change newObj)
						(if-run (exist? obj)
							{
								`非第一次生成`
								(p.replaceWith 
									(obj.getElement)
									(newObj.getElement)
								)
								`mve生成，都是有init函数与destroy函数的`
								(obj.destroy)
								(newObj.init)
							}
						)
					}
				]
			)
			(list 
				change 
				`绑定第一个生成`
				(extend (kvs-path (change) [init]) p.inits ) 
				`销毁最后一个`
				(extend 
					{
						(kvs-path-run (change) [destroy])
					} 
					p.destroys
				)
			)
		}
	)
	`对列表`
	(let Parse {
			`[
				watch 
				k 
				inits 
				destroys 
				mve
				bindMap
				bindEvent
				if-bind
			]
			o 
			`
			(let 
				(x o) args
				o (default o "")
			)
			(if-run (type? o 'list)
				{
					`列表情况，对应js中字典`
					(if-run (type? o.type 'function)
							{
								`自定义组件`
								(let obj (o.type o.params))
								`绑定id`
								(if-run (exist? o.id)
									{
										(x.k (kvs-extend o.id obj (x.k)))
									}
								)
								(let e (obj.getElement))
								`绑定locsize`
								(build-locsize  locsize o 
									{
										(let 
											(str vf) args
										 	ef (default (kvs-find1st obj str) empty-fun)
										)
										(bind 
											x.watch 
											vf 
											{
												(let (v) args)
												(ef v)
												(p.locsize e str v)
											}
										)
									}
								)
								(list 
									e
									(extend obj.init x.inits)
									(extend obj.destroy x.destroys)
								)
							}
							{

								(let (e inits destroys) (p.buildElement x o))
								`绑定id`
								(if-run (exist? o.id)
									{
										(x.k (kvs-extend o.id e (x.k)))
									}
								)
								`绑定locsize`
								(build-locsize 
									locsize 
									o 
									{
										(let (str vf) args)
										(bind 
											x.watch 
											vf 
											{
												(let (v) args)
												(p.locsize e str v)
											}
										)
									}
								)
								(list e inits destroys)
							}
					)
				}
				{
					(if-run (type? o 'function)
						{
							`函数节点`
							(let 
								(change inits destroys) (Parse-fun x o)
								obj (change)
							)
							(list 
								(obj.getElement)
								inits
								destroys
							)
						}
						{
							`值节点`
							(p.createTextNode x o)
						}
					)
				}
			)
		}
	)
	{
		(let 
			(o watch k mve) args
			x [
				Parse 'Parse
				watch 'watch 
				k 'k 
				inits [] 
				destroys [] 
				mve 'mve
				bindEvent 'bindEvent
				bindMap (bindMap watch)
				if-bind (if-bind watch)
			]
		)
		(if-run (type? o 'function)
			{
				`function`
				(let (change inits destroys) (Parse-fun x o))
				(list
					{
						(kvs-path-run (change) [getElement])
					}
					(forEach-run inits)
					(forEach-run destroys)
				)
			}
			{
				(let (el inits destroys) (Parse x o))
				(list
					{el}
					(forEach-run inits)
					(forEach-run destroys)
				)
			}
		)
	}
}