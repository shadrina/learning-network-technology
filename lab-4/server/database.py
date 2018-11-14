import postgresql

db = postgresql.open('pq://postgres:postgres@localhost:5432/chat')
