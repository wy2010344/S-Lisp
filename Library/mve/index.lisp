{
	(let 
		(DOM) args
		util  (load './util.lisp)
		Parse (load './parse.lisp)
		build-children (load './build-children.lisp)
		nokey (load './nokey.lisp)
	)
	(util.Exp 
		(Parse
			DOM
			(build-children
				util.Value
				util.Watcher
				DOM
				nokey
			)
			util.locsize
		)
		DOM
	)
}