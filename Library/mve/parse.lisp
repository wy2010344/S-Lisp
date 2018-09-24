
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
		(let (watch map f) args)
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
		(let (watch value f) args)
		(if-run 
			(exist? value)
			{
				(bind watch value f)
			}
		)
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
	(let (DOM build-children locsize) args)
	`对函数`
	(let Parse-fun 
		{
			(let (fun watch inits destroys mve) args)
			(let change (cache []))
			(watch
				[
					exp 'fun 
					after {
						(let (element) args)
						(let newObj 
							(mve 
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
								(DOM.replaceWith 
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
				(extend (kvs-path (change) [init]) inits ) 
				`销毁最后一个`
				(extend 
					{
						(kvs-path-run (change) [destroy])
					} 
					destroys
				)
			)
		}
	)
	`对列表`
	(let Parse {
			(let (json watch k inits destroys mve) args Parse this)
			(let json (default json ""))
			(if-run (type? json 'list)
				{
					`列表情况，对应js中字典`
					(let j json)
					(if-run (type? j.type 'function)
							{
								`自定义组件`
								(let obj (j.type j.params))
								`绑定id`
								(if-run (exist? j.id)
									{
										(k (kvs-extend j.id obj (k)))
									}
								)
								(let e (obj.getElement))
								`绑定locsize`
								(build-locsize 
									locsize 
									json 
									{
										(let 
											(str vf) args
										 	ef (default (kvs-find1st obj str) empty-fun)
										)
										(bind 
											watch 
											vf 
											{
												(let (v) args)
												(ef v)
												(DOM.locsize e str v)
											}
										)
									}
								)
								(list 
									e
									(extend obj.init inits)
									(extend obj.destroy destroys)
								)
							}
							{
								`原生组件`
								(let e 
									(DOM.createElement j.type)
								)
								`绑定id`
								(if-run (exist? j.id)
									{
										(k (kvs-extend j.id e (k)))
									}
								)
								`attr属性`
								(bindMap watch j.attr 
									{
										(let (k v) args)
										(DOM.attr e k v)
									}
								)
								`style属性`
								(bindMap watch j.style
									{
										(let (k v) args)
										(DOM.style e k v)
									}
								)
								`动作`
								(bindEvent j.action
									{
										(let (k v) args)
										(DOM.action e k v)
									}
								)
								`内部字符`
								(if-bind watch j.text 
									{
										(let (v) args)
										(DOM.text e v)
									}
								)
								`内部值`
								(if-bind watch j.value
									{
										(let (v) args)
										(DOM.value e v)
									}
								)
								`innerHTML`
								(if-bind watch j.html
									{
										(let (v) args)
										(DOM.html e v)
									}
								)
								`children`
								(let (inits destroys) 
									(if-run (str-eq 'kvs j.children-type)
										{
											`children-type是kvs，即repeat`
											(build-children e j.children inits destroys mve)
										}
										{
											`children是列表`
											(reduce j.children
												{
													(let (ini child) args)
													(let (inits destroys) ini)
													(let (ce inits destroys) (Parse child watch k inits destroys mve))
													(DOM.appendChild e ce)
													(list inits destroys)
												}
												(list inits destroys)
											)
										}
									)
								)
								`绑定locsize`
								(build-locsize 
									locsize 
									json 
									{
										(let (str vf) args)
										(bind 
											watch 
											vf 
											{
												(let (v) args)
												(DOM.locsize e str v)
											}
										)
									}
								)
								(list e inits destroys)
							}
					)
				}
				{
					(if-run (type? json 'function)
						{
							`函数节点`
							(let 
								(change inits destroys) (Parse-fun json watch inits destroys mve)
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
							(list 
								(DOM.createTextNode json)
								inits
								destroys
							)
						}
					)
				}
			)
		}
	)
	{
		(let 
			(json watch k mve) args
			inits (cache []) 
			destroys (cache [])
		)
		(if-run (type? json 'function)
			{
				`function`
				(let (change inits destroys) (Parse-fun json watch [] [] mve))
				(list
					{
						(kvs-path-run (change) [getElement])
					}
					(forEach-run inits)
					(forEach-run destroys)
				)
			}
			{
				(let (el inits destroys) 
					(Parse json watch k [] [] mve)
				)
				(list
					{el}
					(forEach-run inits)
					(forEach-run destroys)
				)
			}
		)
	}
}