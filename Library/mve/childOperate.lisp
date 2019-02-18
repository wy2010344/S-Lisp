[
    build {
		`下面调入`
		(let (e repeat mve getO) args)
		{
			`最终调入`
			(let 
				o (apply getO args)
				fn (mve { 
							[element {
									(apply repeat o)
								}
							]
						}
					)
			)
			[ 
				row 'o
				obj (fn e)
			]
		}
	}
	getInit {
		(let (view) args)
		view.obj.init
	}
	init {
		(let (view) args)
		(view.obj.init)
	}
	destroy {
		(let (view) args)
		(view.obj.destroy)
	}
]