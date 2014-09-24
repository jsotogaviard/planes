use dbgraph flights into 'flights.dex'
LOAD NODES '${generated}/flightplan-final.csv'  
COLUMNS flightNr,
		aircraftType,
		originCity,
		destinationCity,
		scheduledFlyingTime,
		adHocFlights
INTO FlightPlan
FIELDS TERMINATED ';'
mode rows