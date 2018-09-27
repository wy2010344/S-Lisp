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
						(x) args
						inits x.inits
						destroys x.destroys
					)
					(let e (DOM.createElement x.o.type))
					(x.bindMap x.o.attr
						{
							(apply DOM.attr (extend e args))
						}
					)
					(x.bindEvent x.o.event 
						{
							(apply DOM.event (extend e args))
						}
					)
					(let build-children 
						(kvs-find1st other_build_children x.o.type)
					)
					(let 
						(inits destroys)
						(if-run (exist? build-children)
							{
								(build-children x)
							}
							{
								(build-children-control e x)
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