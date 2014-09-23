create dbgraph flights into 'flights.dex'
  
create node FlightPlan (
		flightNr string unique,
		aircraftType string indexed,
		originCity string indexed,
		destinationCity string indexed,
		scheduledFlyingTime timestamp indexed,
		adHocFlights boolean indexed
  )
  
  
create node Flights (
		flightNr string indexed,
		date timestamp indexed,
		aircraftType string indexed,		
		scheduledDepartureTime timestamp indexed,
		scheduledArrivalDate timestamp indexed,
		updatedScheduledDepartureTime timestamp indexed,
		updatedScheduledArrivalDate timestamp indexed,
		actualScheduledDepartureTime timestamp indexed,
		actualScheduledArrivalDate timestamp indexed
	)
	
create node Airplanes (
		aircraftType string unique,
		capacityBusiness long indexed,
		capacityEconomy long indexed,
		weightCapacity long indexed
	)
		
create node PassengerItinerary (
		itineraryId string unique,
		originCity string indexed,
		destinationCity string indexed,
		passengerName string indexed,
		class string indexed,
		luggageWeight long indexed,
		scheduledDepartureDateTime timestamp indexed,
		scheduledArrivalDateTime timestamp indexed,
		schekingDateTime timestamp indexed,
		luggageLost boolean indexed,
		lateArrival boolean indexed
	)

create node PassengerLegs (
	itineraryId long indexed,
	legId long indexed,
	date timestamp indexed,
	flightNr string indexed,
	annulated boolean indexed
)

create node Cities (
	airportCode string indexed,
	city string unique,
	country string indexed,
	timezoneDifference int indexed
)

	
create edge FlightPlan_Flights 
  		from FlightPlan 
  		to Flights materialize neighbors
  
create edge FlightPlan_Airplanes 
  		from  FlightPlan
  		to Airplanes materialize neighbors
  		
create edge FlightPlan_OriginCity from FlightPlan to Cities materialize neighbors

create edge FlightPlan_DestinationCity from FlightPlan to Cities materialize neighbors

create edge Flights_Airplanes from Flights to Airplanes

create edge PassengerItinerary_PassengerLegs 
  		from PassengerItinerary 
  		to PassengerLegs materialize neighbors
  
create edge PassengerItinerary_OriginCity from PassengerItinerary to Cities materialize neighbors

create edge PassengerItinerary_DestinationCity from PassengerItinerary to Cities materialize neighbors

create edge PassengerLegs_Flights 
  		from PassengerLegs 
  		to Flights materialize neighbors
