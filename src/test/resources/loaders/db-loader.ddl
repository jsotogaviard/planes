create dbgraph flights into 'flights.dex'
LOAD NODES './src/test/resources/data/a.txt'  
COLUMNS id
INTO A
FIELDS TERMINATED '|'
mode rows

LOAD NODES './src/test/resources/data/b.txt'  
COLUMNS id
INTO B
FIELDS TERMINATED '|'
mode rows 

LOAD NODES './src/test/resources/data/Document.txt'  
COLUMNS id, body
INTO Document
FIELDS TERMINATED '|'
mode rows

LOAD NODES './src/test/resources/data/DocumentAux.txt'  
locale ".utf8"
COLUMNS id, body, abstract
INTO DocumentAux
FIELDS TERMINATED '|'
mode rows

LOAD NODES './src/test/resources/data/c.txt'  
COLUMNS id
INTO C
FIELDS TERMINATED '|'
mode rows 

LOAD EDGES './src/test/resources/data/b_a.txt'
COLUMNS src, target
INTO B_A
ignore src, target
WHERE TAIL src = B.id HEAD target = A.id
FIELDS TERMINATED '|'
mode rows 

LOAD EDGES './src/test/resources/data/c_a.txt'
COLUMNS src, target
INTO C_A
ignore src, target
WHERE TAIL src = C.id HEAD target = A.id
FIELDS TERMINATED '|'
mode rows 

