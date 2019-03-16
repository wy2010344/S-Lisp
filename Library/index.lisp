


`致力于变成多平台通用的文件`
(let 
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
	
	reverse-join {
		(reduce args
			{
				(let (init xs) args)
				(reduce xs
					{
						(let (init x) args)
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
	type? {
		(let (x n) args)
		(str-eq (type x) n)
	}
	`如果没有，设置默认值`
	default 'default
	`从某处开始切片`
	`暂不添加slice，因为不知道是(slice from to) 还是 (slice from length)`

	reduce 'reduce
	`reduce-left就是reduce`
	reduce-left 'reduce
	reduce-right 'reduce-right

	
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

	`与列表的reduce对应`
	kvs-reduce 'kvs-reduce
	kvs-reduce-left 'kvs-reduce
	kvs-reduce-right 'kvs-reduce-right

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

	`
	类似js中的some，已经包含，
	这会对所有检查，即使已经存在了
	收集所有符合条件的，是filter
	统计符合条件的个数？
	检查存在的some
	`
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

	`类似js-Array的splice:list,slice-from,count,...adds，先不考虑异常`
	splice  {
		(let (xs i count ...adds) args)
		(let olds  (slice-from xs (+ i count)))
		(let olds (combine-two adds olds))
		(combine-two (slice-to xs i) olds)
	}
	
	`其实是与splice-last对应`
	splice-first {
		(let (xs count ...adds) args)
		(let olds (slice-from xs count))
		(combine-two adds olds)
	}
	`最后一个list,count,adds`
	splice-last {
		(let (xs count ...adds) args)
		(let slice-from (- (length xs) count))
		(combine-two (slice-to xs slice-from) adds)
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
										(let ((smallers eqs largers) v) args)
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

	`将多个函数串联成一个`
	comnination {
		(let fs args)
		{
			(apply pip (extend args fs))
		}
	}

	`柯西化(n v)`
	cauchy {
		(let (n fun) args cauchy this)
		{
		}
		(if-run (= 0 n)
			{

			}
			{

			}
		)
	}
	`转换成一个函数，函数会infix后续的，但后续的参数会被计算出来`
	to-infix {
		(let fs args)
		{
			(let (v) args)
			(apply infix (extend v fs))
		}
	}
	`转换成一个函数，函数会extension后续的，但后续的参数会被计算出来`
	to-extension {
		(let fs args)
		{
			(let (v) args)
			(apply extension (extend v fs))
		}
	}


	`本质上是获得kvs-find1st`
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