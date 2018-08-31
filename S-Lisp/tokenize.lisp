

(let 
	is-in {
		(let list args)
		{
			(let (v) args)
			(reduce 
				list
				{
					(let (init vi i) args)
					(if-run init 
						{true}
						{
							(or init (str-eq v vi))
						}
					)
				}
				false
			)
		}
	}
	isBlank (is-in " " "\r" "\n" "\t")
	isQuoteLeft (is-in "(" "[" "{")
	isQuoteRight (is-in ")" "]" "}")
	tokenize {
		(let (x ...xs) args tokenize this)
		(if-run (isBlank x)
			{
				(apply tokenize xs)
			}
			{
				(if-run (isQuoteLeft x)
					{

					}
					{
						(if-run (isQuoteRight x)
							{

							}
							{
								(if-run (str-eq x "\"")
									{

									}
									{
										(if-run (str-eq x "`")
											{

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
			}
		)
	}

	tokenize-run {
		(let list 
			(split (first args) "")
		)
		(apply tokenize list)
	}
)