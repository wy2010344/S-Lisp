( log (first [ 89 9 ]))
(let n 9)
(log n)
(let if-run {
		(let (a b c) args)
		(let run (if a b c))
		(run)
	}
)
(if-run (empty [9 8])
	{}
	{
		(log "测试if-run")
	}
)
(log "测试this?")
(let xp {
	(let (x ...xs ) (first args) k this)
	(log x)
	(if-run
		(empty xs)
		{(log "结束")}
		{
			(k xs)
		}
	)
})
(xp [1 2 39 4 56 21])

(log (if (empty [9 8]) 9 8) "ool")

`测试字典匹配`
(log "测试字典匹配")
(let zk [x 9 y 8 z 'xp])
(let zk (kvs-extend 'k1 99 zk))
(log (kvs-find1st zk 'k1) '测试kvs )
(let (mk nk) zk)
(let l* zk)
(log 981)
(log mk nk l.x l.y l.z)
(let (m n) [[x 9 y 8 z 'xp]9])
(let k* [x 9 y 8 z 'xp])
(log k.x)
(log k.y)
(log (k 'y) 'ky (k 'x) (k "x") )
(log '结束')
`测试闭包，需要定义父作用域时retain，销毁时release`
(log 'test)
(let a {
	(let m [a 1 b 2 c 3 d 44])
	{
		(first m)
	}
})
(log '这里)
(let b (a ))
(log (b ))
(log '经常假死)
`测试str-join`
(let x (str-join [a c d e f g]))
(log x 
	(str-length x) 
	(charAt x 1) 
	(str-eq 
		(toString (charAt x 1)) (toString (charAt x 2))
	)
)
(let x (load './t1.lisp))
(let y (load './t2.lisp))
(let z (load '../a.lisp)) `一个不存在的文件`
(log x y)
(quote [a b c])
(quote [a b c])
`测试cache，只要无循环引用应该能保证销毁`
(let z (cache 9))
(log z (z) 'a)
(z 8)
(log z (z) 'm)
(log 9999)
`测试函数转化成字符串`
(log 
	{
		(log 98)
		(log 'op "dd\"ddsfef\\\"dd")
	}
	[
		1 daa "dfeawfa\"faef\\\"aefafe" { (quote 'op) }
	]
)
`测试list?,function?`
(log (list? [1]) (function? {}))
(log (list? {}) (function? []))
`测试parse`
(log '测试parse)
(log 
	(parse 
		(toString 
			{ 
				(log 98) 
			}
		)
	)
)