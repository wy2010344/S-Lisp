
(let update {
	(let (update-index views index) args)
	(reduce views {
		(let (init view) args)
		(update-index view init)
		(+ init 1)
	} index)
})
{
	(let 
		(p) args
		views (cache [])
		view [
			insert {
				(let 
					(index row) args
					view (p.build row index)
					left (slice-to (views) index)
					right (slice-from (views) index)
				)
				(if-run (exist? right)
					{
						(update p.update-index right (+ (len left) 1))
						(p.insertChildBefore view (first right))
					}
					{
						(p.appendChild view)
					}
				)
				(views (combine-two left (extend view right)))
				(p.init view)
			}
			remove {
				(let 
					(index row) args
					view (p.build row index)
					left (slice-to (views) index)
					(view ...right) (slice-from (views) index)
				)
				(update p.update-index right (len left))
				(views (combine-two left right))
				(p.destroy view)
				(p.removeChild view)
			}
		]
	)
	(p.model.addView view)
	(let (i initViews)
		(reduce (p.model.list)
			{
				(let 
					((i xv) row) args
					view (p.build row i)
				)
				(p.appendChild view)
				(list 
					(+ i 1)
					(extend view xv)
				)
			} [0 xv]
		)
	)
	(views (reverse initViews))
	[
		init {
			(forEach initViews p.init)
		}
		destroy {
			(forEach (views) p.destroy)
			(p.model.removeView view)
		}
	]
}