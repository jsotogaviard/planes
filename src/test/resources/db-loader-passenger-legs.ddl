use dbgraph flights into 'flights.dex'
LOAD NODES '${dataset}/passenger_legs_final.csv'  
COLUMNS itineraryId,
		legId,
		date,
		flightNr,
		annulated
INTO PassengerLegs
FIELDS TERMINATED ';'
mode rows



