


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
)

[
	!= '!=
	empty-fun 'empty-fun
	`如果没有，设置默认值`
	default 'default
	`减少循环`
	forEach {
		(let (xs run) args forEach this)
		(if-run (exist? xs)
			{
				(let (x ...xs) xs)
				(run x)
				(forEach xs run)
			}
		)
	}
	kvs-forEach {
		(let (kvs run) args kvs-forEach this)
		(if-run (exist? kvs)
			{
				(let (k v ...kvs) kvs)
				(run v k)
				(kvs-forEach kvs run)
			}
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