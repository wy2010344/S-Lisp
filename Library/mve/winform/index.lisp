{
	(let
		(DOM) args
		util (load '../util.lisp)
		Parse (load '../parse.lisp)
		build-children-Factory (load '../build-children.lisp)	
		locsize [
			width height left top right bottom
		]
		build-children-control (build-children-Factory
			[
				key children
				Value 'util.Value
				appendChild 'DOM.appendChild
				removeChild 'DOM.removeChild
				Watcher 'util.Watcher
			]
		)
		`
			listView需要begin和end的watch
		`
		other_build_children [
			list-view ((load './list-view.lisp) DOM build-children-Factory util)
		]
		bindKV {
			(let (bind key value f) args)
			(bind value {
					(f key 
						(first args)
					)
				}
			)
		} 
		bindMap {
			(let (bind map f) args)
			(if-run (exist? map)
				{
					(kvs-forEach map 
						{
							(let (v k) args)
							(bindKV bind k v f)
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
	)
	(util.Exp
		locsize
		DOM
		(Parse
			locsize
			[
				locsize {

				}
				replaceWith {
					(log "未实现，请尽量避免replaceWith")
				}
				createTextNode {
					(log "未实现，请尽量避免createTextNode")
				}
				buildElement {
					(let (x o) args)
					(let e (DOM.createElement o.json.type))
					(let build-children 
						(kvs-find1st other_build_children o.json.type)
					)
					(let obj 
						(if-run (exist? build-children)
							{
								(build-children e x o)
							}
							{
								(build-children-control e x o)
							}
						)
					)
					[
						element 'e
						k 'obj.k
						inits 'obj.inits
						destroys 'obj.destroys
					]
				}
				makeUpElement {
					(let (e x json) args)
					(bindMap x.bind json.attr
						{
							(apply DOM.attr (extend e args))
						}
					)
					(bindEvent json.event 
						{
							(apply DOM.event (extend e args))
						}
					)
					`内部字符`
					(x.if-bind json.text 
						{
							(let (v) args)
							(DOM.text e v)
						}
					)
					`内部值`
					(x.if-bind json.value
						{
							(let (v) args)
							(DOM.value e v)
						}
					)
				}
			]
		)
	)
}