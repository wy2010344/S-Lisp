{
	(let 
		DOM (load './DOM.lisp)
		util  (load '../util.lisp)
		exp (load '../exp.lisp)
		Parse (load '../parse.lisp)
		build-children (
			(load '../build-children.lisp)
			[
				Value 'util.Value
				Watcher 'util.Watcher
				key children
				appendChild 'DOM.appendChild
				removeChild 'DOM.removeChild
			]
		)
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
		replaceChild {
			(let (pel old_el new_el) args)
			(DOM.replaceWith old_el new_el)
		}
		makeUpElement {
			(let (e x json) args)
			`attr属性`
			(bindMap x.bind json.attr 
				{
					(apply DOM.attr (extend e args))
				}
			)
			`style属性`
			(bindMap x.bind json.style
				{
					(apply DOM.style (extend e args))
				}
			)
			`动作`
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
					(apply DOM.value (extend e args))
				}
			)
			`innerHTML`
			(x.if-bind json.html
				{
					(apply DOM.html (extend e args))
				}
			)
		}
	)
	(exp
		DOM
		(Parse
			[
				createTextNode {
					(let (x o) args)
					[
						element (DOM.createTextNode (default o.json ""))
						k 'o.k
						inits 'o.inits
						destroys 'o.destroys
					]
				}
				buildElement {
					(let (x o) args)
					`原生组件`
					(let e  (DOM.createElement o.json.type o.json.NS))
					`children`
					(let obj (build-children 
						[pel 'e replaceChild 'replaceChild] x o)
					)
					(makeUpElement e x o.json)
					[
						element 'e
						k 'obj.k
						inits 'obj.inits
						destroys 'obj.destroys
					]
				}
			]
		)
	)
}