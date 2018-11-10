{
	(let 
		x (load './tk.lisp)
		tk (x.run 'Tk)
		lbfram (x.c 'LabelFrame tk [text 登录])
		lb (x.c 'Listbox lbfram)
		btn (x.c 'Button lbfram)
	)
	`
	(forEach [a b c d e]
		{
			(py-call lb "insert" (list 0 (first args)))
		}
	)
	(py-call lb "pack")
	(py-dict btn "text" "feafawefaewf")
	(py-dict btn "command" 
		(py-function 
			{
				(log 98 99)
			}
		)
	)
	(py-call btn "pack")
	`
	`列扩展比率1:2`
	(py-call lbfram "columnconfigure"
		[0]
		[weight 1]
	)
	(py-call lbfram "columnconfigure"
		[1]
		[weight 2]
	)
	
	(py-call
		(x.c "Label" lbfram [text First])
		"grid"
		[]
		[row 0  sticky W]
	)
	(py-call
		(x.c "Label" lbfram
			[text Second]
		)
		"grid"
		[]
		[row 1]
	)
	(py-call 
		(x.c "Entry" lbfram)
		"grid"
		[]
		[row 0 column 1]
	)
	`向左右扩展`
	(py-call
		(x.c "Entry" lbfram)
		"grid"
		[]
		[row 1 column 1 sticky EW]
	)	
	(let var (x.run 'IntVar))
	(py-call 
		(x.c "Checkbutton" lbfram
			[text "Precerve aspect" variable 'var]
		)
		"grid"
		[]
		[columnspan 2 sticky W]
	)
	(py-call lbfram 'pack)
	(x.center-window tk 500 500)
	(py-call tk "mainloop")
}