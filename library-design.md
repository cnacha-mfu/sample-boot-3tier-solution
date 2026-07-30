nouns:
- books
- members
- categories
- transactions


list all books GET /books
list all book under categories GET /categories/{id}/books
get members GET /members/{id}
borrow/return  POST /members/{id}/transactions
    {book_id: ....
    type: borrow/return}



    POST /transactions
    {book_id:..., member_id:...., type...}


note: this is the shape the service really answers with now that there are DTOs.
In the JPA sample the entity itself was the request body, so the client had to
send a nested {"book": {"id": 3}} instead. Adding TransactionDTO put the wire
format back under our control - which is the whole argument for the pattern.
