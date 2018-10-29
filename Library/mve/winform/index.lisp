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

				}
				createTextNode {

				}
				buildElement {
					(let 
						(x o) args
						inits x.inits
						destroys x.destroys
					)
					(let e (DOM.createElement o.type))
					(x.bindMap o.attr
						{
							(apply DOM.attr (extend e args))
						}
					)
					(x.bindEvent o.event 
						{
							(apply DOM.event (extend e args))
						}
					)
					`内部字符`
					(x.if-bind o.text 
						{
							(let (v) args)
							(DOM.text e v)
						}
					)
					`内部值`
					(x.if-bind o.value
						{
							(let (v) args)
							(DOM.value e v)
						}
					)
					(let build-children 
						(kvs-find1st other_build_children o.type)
					)
					(let 
						(inits destroys)
						(if-run (exist? build-children)
							{
								(build-children e x o)
							}
							{
								(build-children-control e x o)
							}
						)
					)
					(list 
						e
						inits
						destroys
					)
				}
			]
		)
	)
}