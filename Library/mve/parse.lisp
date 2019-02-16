
(let 
	bindFactory {
		(let (watch) args)
		{
			(let (value f) args)
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
	} 
	if-bind {
		(let (bind) args)
		{
			(let (value f) args)
			(if-run 
				(exist? value)
				{
					(bind value f)
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
	add-k {
		(let (k id obj) args)
		(if-run (exist? id)
			{
				(let old (kvs-find1st k id))
				(if-run (exist? old)
					{
						(log "已经存在同名id" id k old "不能添加新的" obj)
						k
					}
					{
						(kvs-extend id obj k)
					}
				)
			}
			{k}
		)
	}
)
{
	(let (locsize p) args)
	`对函数`
	(let ParseFun 
		{
			`
			[
				watch 
				inits 
				destroys 
				mve 
			]
			[
				k
				json
				inits
				destroys
			]
			`
			(let (x o) args)
			(let change (cache []))
			(x.watch
				[
					exp { (o.json) }
					after {
						(let (element) args)
						(let newObj 
							((x.mve {[ element 'element]}) o.e)
						)
						(let obj (change))
						(change newObj)
						(if-run (exist? obj)
							{
								`非第一次生成`
								(o.e.replaceChild 
									o.e 
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
			[
				change 'change
				`绑定第一个生成`
				inits (extend (kvs-path (change) [init]) o.inits )
				`销毁最后一个`
				destroys (extend 
					{
						(kvs-path-run (change) [destroy])
					} 
					o.destroys
				) 
			]
		}
	)
	`对列表`
	(let ParseObject {
			`
			[
				watch 
				mve
				bindMap
				bindEvent
				if-bind
			]
			[
				json
				k 
				inits 
				destroys 
			]
			`
			(let 
				(x o) args
				json (default o.json "")
			)
			(if-run (type? json 'list)
				{
					`列表情况，对应js中字典`
					(if-run (type? json.type 'string)
						{
							(let 
								obj (p.buildElement x o)
								e obj.element
							)
							`绑定locsize`
							(build-locsize locsize o 
								{(let (str vf) args)
									(x.bind vf {
											(let (v) args)
											(p.locsize e str v)
										}
									)
								}
							)
							(p.makeUpElement e x o.json)
							[
								element 'e
								k (add-k obj.k json.id e)
								inits 'o.inits
								destroys 'o.destroys
							]
						}
						{
							`自定义组件`
							(let 
								obj ((json.type json.params) o.e)
								e (obj.getElement)
							)
							`绑定locsize`
							(build-locsize locsize json
								{
									(let 
										(str vf) args
									 	ef (default (kvs-find1st obj str) empty-fun)
									)
									(x.bind vf {
											(let (v) args)
											(ef v)
											(p.locsize e str v)
										}
									)
								}
							)
							(p.makeUpElement e x o.json)
							[
								element 'e
								k (add-k o.k json.id obj)
								inits (extend obj.init o.inits)
								destroys (extend obj.destroy o.destroys)
							]
						}
					)
				}
				{
					`值节点`
					(p.createTextNode x o)
				}
			)
		}
	)
	(let Parse {
		(let (x o) args)
		(if-run (type? o.json 'function)
			{
				(let vm (ParseFun x o))
				[
					element (kvs-path-run (vm.change) [getElement])
					k 'o.k
					inits 'vm.inits
					destroys 'vm.destroys
				]
			}
			{
				(ParseObject x o)
			}
		)
	})
	{
		(let 
			(e json watch mve k) args
			bind (bindFactory watch)
			x [
				Parse 'Parse
				watch 'watch 
				mve 'mve
				bind 'bind
				if-bind (if-bind bind)
			]
			o [
				e 'e
				json 'json
				k [] `无副作用的处理`
				inits [] 
				destroys [] 
			]
		)
		(if-run (type? o.json 'function)
			{
				`function`
				(let vm (ParseFun x o))
				(list
					{
						(kvs-path-run (vm.change) [getElement])
					}
					[]
					vm.inits
					vm.destroys
				)
			}
			{
				(let vm (ParseObject x o))
				(list
					{vm.element}
					vm.k
					vm.inits
					vm.destroys
				)
			}
		)
	}
}