{
	`
		分两部分，build-columns,build-rows
	`
	(let 
		(DOM build-children-Factory util) args
		build-columns
			(build-children-Factory
				[
					key columns
					Value 'util.Value
					appendChild 'DOM.list-view-appendColumn
					removeChild 'DOM.list-view-removeColumn
					Watcher 'util.Watcher
				]
			)
		build-rows
			(build-children-Factory
				[
					key rows
					Value 'util.Value
					appendChild 'DOM.list-view-appendRow
					removeChild 'DOM.list-view-removeRow
					Watcher 'util.Watcher
					before 'DOM.list-view-beginUpdate
					after 'DOM.list-view-endUpdate
				]
			)
	)
	{
		(apply build-columns args)
		(apply build-rows args)
	}
}