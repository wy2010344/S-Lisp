


(let 
    forEach-run {
        (let (array) args)
        {
            (forEach array
                {
                    ((first args))
                }
            )
        }
    }
)


{
    (let (Parse) args)
    (log 'parse Parse)
    {
        (let (es watch k mve) args)
        (let (el inits destroys) (Parse es [] [] watch k mve)) 
        (list
            {
                el
            }
            (forEach-run inits)
            (forEach-run destroys)
        )
    }
}