use dbgraph flights into 'flights.dex'
LOAD NODES '${dataset}/flightplan-final.csv'  
COLUMNS flightNr,
		aircraftType,
		originCity,
		destinationCity,
		scheduledFlyingTime,
		adHocFlights
INTO FlightPlan
FIELDS TERMINATED ';'
mode rows