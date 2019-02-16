


(let Dep-target (cache []))

(let Dep ({
		(let uid (cache 0))
		{
			(let subs  (cache []))
			`subs中已经包含`
			(let contain {
				(let (target) args)
				(some (subs) 
					{
						(let (sub) args)
						(eq sub target)
					}
				)
			})
			(uid (+ (uid) 1))
    		[
    			depend {
    				(if-run (exist? (Dep-target))
						{
						 	(if-run (contain (Dep-target))
						 		{
						 			`一个watch包含两个观察节点时`
						 			`(log '已经包含了)`
						 		}
						 		{
						 			(subs 
						 				(extend 
						 					(Dep-target) 
						 					(subs)
						 				)
						 			)
						 		}
						 	)
						}
					)
    			}
    			notify {
    				(let old_subs (subs))
    				(subs [])
					`
						调用每个Watcher的update
						每个Watcher将Dep-Target设置成自己
						Watcher计算期间，所有依赖的Value进行depend，每个Value将这个Watcher加入自己的通知链
						本值节点通知期间，重新收集通知链。而其它值节点，可能不需要收集（已经存在）
					`
    				(forEach old_subs call)
    			}
    			id (uid)
    		]
		}
	})
	`值节点`
	Value {
		(let dep (Dep))
		(let v (apply cache args))
		{
			(let xs args)
			(if-run (exist? xs)
				{
					(if-run (exist? (Dep-target))
						{
							(log '计算期间不允许修改)
						}
						{
							(v (first xs))
							(dep.notify)
						}
					)
				}
				{
					(dep.depend)
					(v)
				}
			)
		}
	}

	ArrayModel {
		(let 
			array (cache args)
			views (cache [])
		)
		[
			list {
				(array)
			}
			addView {
				(views (extend (first args) (views)))
			}
			removeView {
				(let index (indexOf (views) (first args)))
				(if-run (< -1 index)
					{
						(views (splice (views) index 1))
					}
				)
			}

			insert {
				(let (index row) args)
				(array (splice (array) index 0 row))
				(forEach (views) {
					(let (view) args)
					(view.insert index row)
				})
			}

			remove {
				(let (index) args)
				(array (splice (array) index 1))
				(forEach (views) {
					(let (view) args)
					(view.remove index)
				})
			}
		]
	}
	Watcher ({
		(let uid (cache 0))
		{
			(let (p) args)
			(let  
				before  (default p.before empty-fun) 
				after (default p.after empty-fun)
			)

			(let enable (cache true))
			(uid (+ (uid) 1))
			(let id (uid))
			(let update 
				{
					(let update this)
					(if-run (enable)
						{
							(let bo (before))
							(Dep-target update)
							(let ao (p.exp bo))
							(Dep-target [])
							(after ao)
						}
					)
				}
			)
			(update)
			[
				id 'id
				update 'update
				disable {
					(enable false)
				}
			]
		}
	})

	Cache {
		(let dep (Dep))
		(let (watch func) args)
		(let cache (cache []))
		(watch [
			exp {
				(cache (func ))
				(dep.notify)
			}
		])
		{
			(dep.depend)
			(cache)
		}
	}
)

[
	Value 'Value

	Watcher 'Watcher

	ArrayModel 'ArrayModel

	Cache 'Cache
]