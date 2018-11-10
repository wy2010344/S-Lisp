(let x (py-eval "__import__('Tkinter')"))
[
	`
	生成子控件Tk本身，Button等控件
	父控件
	子控件名字
	子控件初始化参数
	子控件初始化字典
	`
	c {
		(let (name parent dict) args)
		(py-call 
			x name 
			(list parent) 
			dict
		)
	}
	
	`
	执行属性方法
	实体
	方法名
	数组参数
	字典参数
	`
	run {
		(apply py-call (extend x args))
	}
	`
	窗口居中
	tk:窗口
	w:宽度 可选
	h:高度 可选
	`
	center-window {
		(let 
			(tk ...wh) args
			(w h) 
				(if-run (exist? wh)
					{wh}
					{
						(list
							(py-call tk 'winfo_width)
							(py-call tk 'winfo_height)
						)
					}
				)
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
]