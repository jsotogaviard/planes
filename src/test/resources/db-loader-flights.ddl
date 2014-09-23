use dbgraph flights into 'flights.dex'
LOAD NODES '${dataset}/flights-date-formatted-final.csv'  
COLUMNS flightNr,
		aircraftType,
		date,	
		scheduledDepartureTime,
		scheduledArrivalDate,
		updatedScheduledDepartureTime,
		updatedScheduledArrivalDate,
		actualScheduledDepartureTime,
		actualScheduledArrivalDate
INTO Flights
FIELDS TERMINATED ';'
mode rows

