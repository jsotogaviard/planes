use dbgraph flights into 'flights.dex'
LOAD NODES '${dataset}/cities.csv'  
COLUMNS 
	airportCode,
	city,
	country,
	timezoneDifference
INTO Cities
FIELDS TERMINATED ';'
from 1
mode rows