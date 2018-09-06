


`致力于变成多平台通用的文件`
(let 
	!= {
		(not (apply = args))
	}
	`
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
	`
	`偏移量，从0开始，最大为list的length`
	offset {
		(let (list i) args offset this)
		(if-run (= i 0) 
			{ list }
			{
				(offset (rest list) (- i 1)) 
			} 
		)
	}

	`其实str-join有点reduce的意思，但分割符末尾没有，至于下标序号，在init参数中`
	`
	reduce {
		(let (xs run init) args reduce this)
		(if-run (exist? xs)
			{
				(let (x ...xs) xs)
				(let init (run init x))
				(reduce xs run init)
			}
			{init}
		)
	}
	`
	
	`异步的reduce`
	async-reduce {
		(let (notice xs run init) args async-reduce this)
		(if-run (exist? xs)
			{
				(let (x ...xs) xs)
				(run 
					{
						`单个的notice`
						(let (init) args)
						(async-reduce notice xs run init)
					}
					init x
				)
			}
			{
				(notice init)
			}
		)
	}

	reduce-right {
		(let (xs run init) args reduce-right this)
		(if-run (exist? xs)
			{
				(let (x ...xs) xs)
				(run 
					(reduce-right xs run  init)
					x
				)
			}
			{ init }
		)
	}
	
	kvs-reduce {
		(let (kvs run init) args kvs-reduce this)
		(if-run (exist? kvs)
			{
				(let (k v ...kvs) kvs)
				(let init (run init v k))
				(kvs-reduce kvs run init)
			}
			{init}
		)
	}

	async-kvs-reduce {
		(let (notice kvs run init) args async-kvs-reduce this)
		(if-run (exist? kvs)
			{
				(let (k v ...kvs) kvs)
				(run 
					{
						`单个的notice`
						(let (init) args)
						(async-kvs-reduce notice kvs run init)
					}
					init v k
				)
			}
			{
				(notice init)
			}
		)
	}
	
	kvs-reduce-right {
		(let (kvs run init) args kvs-reduce-right this)
		(if-run (exist? kvs)
			{
				(let (k v ...kvs) kvs)
				(run
					(kvs-reduce-right kvs run init)
					v
					k
				)
			}
			{ init }
		)
	}
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
	`从某处开始切片`
	slice-from offset
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
	
	reverse-join {
		(reduce args
			{
				(let (init xs i) args)
				(reduce xs
					{
						(let (init x i) args)
						(extend x init)
					}
					init
				)
			}
			[]
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
	slice-from 'slice-from
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

	forEach {
		(let (xs run) args)
		(reduce-right xs {
			(let (init x) args)
			(run x)
		} [])
	}

	map {
		(let (xs run) args)
		(reduce-right xs {
			(let (init x) args)
			(extend (run x ) init)
		} [])
	}

	filter {
		(let (xs run) args)
		(reduce-right xs {
			(let (init x) args)
			(let is (run x))
			(if-run is
				{(extend x init)}
				{init}
			)
		} [])
	}

	combine-two 'combine-two

	`类似js-Array的splice:list,offset,count,...adds，先不考虑异常`
	splice  {
		(let 
			(xs i count ...adds) args
			index (+ i count)
		)
		(if-run (< index (length xs))
			{
				(let olds  (offset xs (+ i count)))
				(let olds (combine-two adds olds))
				(combine-two (slice-to xs i) olds)
			}
			{
				(slice-to xs (- i 1))
			}
		)
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

	sort {
		`run flag v =0 <0 >0`
		(let (xs run) args sort this)
		(if-run (exist? xs)
			{
				(let (x ...xs) xs)
				(if-run (empty? xs)
					{
						`只有一个元素`
						(list x)
					}
					{
						`有别的元素`
						(let (smallers eqs largers ) 
								(reduce xs
									{
										(let ((smallers eqs largers) v i) args)
										(let c (run x v))
										(if-run (= 0 c)
											{
												(list
													smallers
													(extend v eqs)
													largers
												)
											}
											{
												(if-run (> 0 c)
													{
														(list
															smallers
															eqs
															(extend v largers)
														)
													}
													{
														`<`
														(list
															(extend v smallers)
															eqs
															largers
														)
													}
												)
											}
										)
									}
									(list [] [] [])
								)
						)
						(reverse 
							(reverse-join 
								(sort smallers run)
								(extend x eqs) 
								(sort largers run)
							)
						)
					}
				)
			}
		)
	}

	kvs-forEach {
		(let (kvs run) args)
		(kvs-reduce-right kvs {
			(let (init v k) args)
			(run v k)
		} [])
	}

	kvs-map {
		(let (kvs run) args)
		(kvs-reduce-right kvs 
			{
				(let (init v k) args)
				(kvs-extend k (run v k) init)
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