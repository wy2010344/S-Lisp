(let

	parse {
		(let (tokens caches children) args parse this)
		(if-run (exist? tokens)
			{

			}
			{
				(let (token tokens) tokens)
				(let (t-type t-value) token)
				(if-run (str-eq t-type "brack-right")
					{

						(parse
							tokens
							(extend
								(list 
									t-value
									children
								)
								caches
							)
							[]
						)
					}
					{
						(if-run (str-eq t-type "brack-left")
							{
								(let (cache ...caches) caches)
								(let (c-value c-children) cache)
								(parse

									(extend
										(
										)
										c-children
									)
								)
							}
							{

							}
						)
					}
				)
			}
		)
	}
)