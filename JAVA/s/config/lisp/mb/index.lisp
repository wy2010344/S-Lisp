


`致力于变成多平台通用的文件`
(let 
	!= {
		(not (apply = args))
	}
	empty-fun {}
	default {
		(let (a dv) args)
		(if (exist? a) a dv)
	}
	if-run {
		(let (a b c) args)
		(let c  (default c empty-fun))
		(let run (if a b c))
		(run)
	}
	`偏移量，从0开始`
	offset {
		(let (list i) args offset this)
		(if-run (= i 0) 
			{ list }
			{
				(offset (rest list) (- i 1)) 
			} 
		)
	}
	`第一位是offset，取代forEach-i等`
	with-offset {
		(let (run) args)
		{
			(apply run (extend 0 args))
		}
	}

	`其实str-join有点reduce的意思，但分割符末尾没有`
	reduce (with-offset {
		(let (i xs run init) args reduce-i this)
		(if-run (exist? xs)
			{
				(let (x ...xs) xs)
				(let init (run init x i))
				(reduce-i (+ i 1) xs run init)
			}
			{init}
		)
	})
	
	reduce-right (with-offset {
		(let (i xs run init) args reduce-right-i this)
		(if-run (exist? xs)
			{
				(let (x ...xs) xs)
				(run 
					(reduce-right-i (+ i 1) xs run  init)
					x
					i
				)
			}
			{ init }
		)
	})
	
	kvs-reduce (with-offset {
		(let (i kvs run init) args kvs-reduce-i this)
		(if-run (exist? kvs)
			{
				(let (k v ...kvs) kvs)
				(let init (run init v k i))
				(kvs-reduce-i (+ i 1) kvs run init)
			}
			{init}
		)
	})
	
	kvs-reduce-right (with-offset {
		(let (i kvs run init) args kvs-reduce-right-i this)
		(if-run (exist? kvs)
			{
				(let (k v ...kvs) kvs)
				(run
					(kvs-reduce-right-i (+ i 1) kvs run init)
					v
					k
					i
				)
			}
			{ init }
		)
	})
	`切片到某处`
	slice-to {
		(let (xs to) args slice-to this)
		(if-run (= to 0)
			{[]}
			{
				(let (x ...xs) xs)
				(extend x (slice-to xs (- to 1)))
			}
		)
	}

	`叠加两个，可以用reduce来做N次重复`
	combine-two {
		(let (adds olds) args combine-two this)
		(if-run (exist? adds)
			{
				(let (x ...xs) adds)
				(if-run (exist? xs)
					{
						(extend x (combine-two xs olds))
					}
					{
						(extend x olds)
					}
				)
			}
			{
				olds
			}
		)
	}
	kvs-path {
		(let (e paths) args kvs-path this)
		(if-run (exist? paths)
			{
				(let (path ...paths) paths)
				(kvs-path 
					(kvs-find1st e path)
					paths
				)
			}
			{e}
		)
	}
)

[
	!= '!=
	empty-fun 'empty-fun
	`不想使用*的kvs-match，可以用这个kvs-match`
	kvs-match {
		(let (kvs) args)
		{
			(let (k) args)
			(kvs-find1st kvs k)
		}
	}
	`兼容空列表的长度判断`
	len {
		(let (cs) args)
		(if-run (exist? cs)
			{
				(length cs)
			}
			{0}
		)
	}
	`访问字典路径`
	kvs-path 'kvs-path
	kvs-path-run {
		(let (e paths ...ps) args)
		(apply (kvs-path e paths) ps)
	}
	`如果没有，设置默认值`
	default 'default
	`从某处开始切片`
	slice-from {
		(let (xs from) args slice-from this)
		(if-run (= from 0)
			{xs}
			{
				(slice-from (rest xs) (- from 1))
			}
		)
	}
	slice-to 'slice-to
	`暂不添加slice，因为不知道是(slice from to) 还是 (slice from length)`

	reduce 'reduce
	`reduce-left就是reduce`
	reduce-left 'reduce
	reduce-right 'reduce-right
	`与列表的reduce对应`
	kvs-reduce 'kvs-reduce
	kvs-reduce-left 'kvs-reduce
	kvs-reduce-right 'kvs-reduce-right
	`类似js中的some，已经包含`
	some {
		(let (xs run) args)
		(reduce xs {
			(let (init x) args)
			(or init (run x))
		} false)
	}
	`减少循环`
	forEach (with-offset {
		(let (i xs run) args forEach-i this)
		(if-run (exist? xs)
			{
				(let (x ...xs) xs)
				(run x i)
				(forEach-i (+ i 1) xs run)
			}
		)
	})
	`map，可以用reduce实现`
	map {
		(let (xs run) args)
		(reduce-right xs {
			(let (init x i) args)
			(extend (run x i) init)
		} [])
	}

	`筛选`
	filter (with-offset {
		(let (i xs run) args filter-i this)
		(if-run (exist? xs)
			{
				(let (x ...xs) xs)
				(let olds 
					(filter-i (+ i 1) xs run)
				)
				(if-run (run x i)
					{
						(extend x olds)
					}
					{ olds }
				)
			}
		)
	})
	combine-two 'combine-two

	`类似js-Array的splice:list,offset,count,...adds，先不考虑异常`
	splice  {
		(let (xs i count ...adds) args)
		(let olds 
			(offset xs (+ i count))
		)
		(let olds (combine-two adds olds))
		(combine-two (slice-to xs i) olds)
	}
	
	`其实是与splice-last对应`
	splice-first {
		(let (xs count ...adds) args)
		(let olds (offset xs count))
		(combine-two adds olds)
	}
	`最后一个list,count,adds`
	splice-last {
		(let (xs count ...adds) args)
		(let offset (- (length xs) count))
		(combine-two (slice-to xs offset) adds)
	}
	
	kvs-forEach (with-offset {
		(let (i kvs run) args kvs-forEach-i this)
		(if-run (exist? kvs)
			{
				(let (k v ...kvs) kvs)
				(run v k i)
				(kvs-forEach-i (+ i 1) kvs run)
			}
		)
	})

	kvs-map {
		(let (kvs run) args)
		(kvs-reduce-right kvs 
			{
				(let (init v k i) args)
				(kvs-extend k (run v k i) init)
			} 
			[]
		)
	}
	`接受一组函数，如果一为假，不执行后续返回假；如果全为真，最后返回真`
	and_q {
		(let xs args and_q this)
		(if-run (exist? xs)
			{
				(let (x ...xs) xs)
				(if-run (x)
					{
						(apply and_q xs)
					}
					{false}
				)
			}
			{true}
		)
	}
	`接受一组函数，有一个为真，不执行后续返回真；如果全为假，返回假`
	or_q {
		(let xs args or_q this)
		(if-run (exist? xs)
			{
				(let (x ...xs) xs)
				(if-run (x)
					{true}
					{
						(apply or_q xs)
					}
				)
			}
			{false}
		)
	}
	`条件执行`
	if-run 'if-run
	switch {
		(let (str kvs default-fun) args)
		(let o (kvs-find1st kvs str))
		(if (exist? o) o (default default-fun empty-fun))
	}
	`多条件if,switch`
	switch-run {
		(let run (apply switch args))
		(run)
	}
]