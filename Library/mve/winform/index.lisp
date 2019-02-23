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
	)
	(exp
		DOM
		(Parse
			[
				createTextNode {
					(log "未实现，请尽量避免createTextNode")
				}
				buildElement {
					(let (x o) args)
					(DOM.build o.json.type x o)
				}
			]
		)
	)
}