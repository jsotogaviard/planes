use dbgraph flights into 'flights.dex'
LOAD NODES '${generated}/passenger_legs_final.csv'  
COLUMNS itineraryId,
		legId,
		date,
		flightNr,
		annulated,
		*,
		flightId,
		id
INTO PassengerLegs
FIELDS TERMINATED ';'
mode rows



