{
	(let
		(Factory) args
		util (load '../util.lisp)
		exp (load '../exp.lisp)
		Parse (load '../parse.lisp)
		build-children-Factory {
			(let p args)
			((load '../build-children.lisp)	
				[
					key 'p.key
					Value 'util.Value
					Watcher 'util.Watcher
					before 'p.before
					after 'p.after
					appendChild 'p.appendChild
					removeChild 'p.removeChild
					insertChildBefore 'p.insertChildBefore
				]
			)
		}
		DOM (Factory build-children-Factory)
		locsize [
			width height left top right bottom
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
	(exp
		locsize
		DOM
		(Parse
			locsize
			[
				locsize {

				}
				createTextNode {
					(log "未实现，请尽量避免createTextNode")
				}
				buildElement {
					(let (x o) args)
					(DOM.build o.json.type x o)
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
							(apply DOM.text (extend e args))
						}
					)
					`内部值`
					(x.if-bind json.value
						{
							(let (v) args)
							(apply DOM.value (extend e args))
						}
					)
				}
			]
		)
	)
}