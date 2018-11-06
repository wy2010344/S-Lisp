{
	(let 
		x (py-eval "__import__('Tkinter')")
		tk (py-call x "Tk")
		center_window (load './center-window.lisp)
		lb (py-call x "Listbox" (list tk))
		btn (py-call x "Button" (list tk))
	)
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
	(center_window tk 500 500)
	(py-call tk "mainloop")
}