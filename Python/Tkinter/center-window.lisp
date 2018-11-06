
`
窗口居中
tk
w
h
`
{
	(let 
		(tk w h) args
		ws (py-call tk "winfo_screenwidth")
		hs (py-call tk "winfo_screenheight")
	)
	(py-call tk "geometry" 
		(list 
			(str-join 
				(list (toString w) "x" (toString h) 
						"+" (toString (- (/ ws 2) (/ w 2) ))
						"+" (toString (- (/ hs 2) (/ h 2) ))
				)
			)
		)
	)
}