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
		actualArrivalDateTime, *,*,
		id
INTO Flights
FIELDS TERMINATED ';'
mode rows
