[
	createElement {
		(let (type NS) args)
		(if-run (exist? NS)
			{
				(js-call 'document 'createElement (list NS type))
			}
			{
				(js-call 'document 'createElement (list type))
			}
		)
	}
	createTextNode {
		(js-call 'document 'createTextNode args)
	}
	appendChild {
		(let (el child) args)
		(js-call el 'appendChild (list child))
	}
	replaceWith {
		(let (old-e new-e) args)
		(let pn (js-attr old-e 'parentNode))
		(js-call pn 'replaceChild (list new-e old-e))
	}
	removeChild {
		(let (el child) args)
		(js-call el 'removeChild (list child))
	}
	attr {
		(let (el key value) args)
		(if-run (= (length args) 3)
			{
				`设值`
				(if-run (exist? value)
					{
						(js-call el 'removeAttribute (list key))
					}
					{
						(js-call el 'setAttribute (list key value))
					}
				)
			}
			{
				`取值`
				(js-call el 'getAttribute)
			}
		)
	}
	style {
		(let 
			(el key value) args
			style (js-attr el 'style)
		)
		(if-run (= (length args) 3)
			{
				`设值`
				(js-attr style key value)
			}
			{
				`取值`
				(js-attr style key)
			}
		)
	}
	prop {
		(let (el key value) args)
		(if-run (= (length args) 3)
			{
				`设值`
				(js-attr el key value)
			}
			{
				`取值`
				(js-attr el key)
			}
		)
	}
	event {
		(let (el key value) args)
		(js-call 'mb.DOM 'addEvent (list el key value))
	}
	text {
		(let (el value) args)
		(if-run (= (length args) 2)
			{
				`设值`
				(js-attr el 'innerText value)
			}
			{
				`取值`
				(js-attr el 'innerText)
			}
		)
	}
	value {
		(let (el value) args)
		(if-run (= (length args) 2)
			{
				`设值`
				(js-attr el 'value value)
			}
			{
				`取值`
				(js-attr el 'value)
			}
		)
	}
	html {
		(let (el value) args)
		(if-run (= (length args) 2)
			{
				`设值`
				(js-attr el 'innerHTML value)
			}
			{
				`取值`
				(js-attr el 'innerHTML)
			}
		)
	}

	alert {
		(js-call 'window 'alert args)
	}

	confirm {
		(js-call 'window 'confirm args)
	}
]