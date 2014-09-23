use dbgraph flights into 'flights.dex'
LOAD NODES '${dataset}/passenger_itenerary-final.csv'  
COLUMNS itineraryId,
		originCity,
		destinationCity,
		passengerName,
		class,
		luggageWeight,
		scheduledDepartureDateTime,
		scheduledArrivalDateTim,
		schekingDateTime,
		luggageLost,
		lateArrival
INTO PassengerItinerary
FIELDS TERMINATED ';'
mode rows