use dbgraph flights into 'flights.dex'
LOAD NODES '${generated}/flights-date-formatted-final.csv'  
COLUMNS flightNr,
		aircraftType,
		date,	
		scheduledDepartureDateTime,
		scheduledArrivalDateTime,
		updatedScheduledDepartureTime,
		updatedScheduledArrivalTime,
		actualDepartureDateTime,
		actualArrivalDateTime
INTO Flights
FIELDS TERMINATED ';'
mode rows
