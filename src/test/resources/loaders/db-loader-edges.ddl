use dbgraph flights into 'flights.dex'
LOAD EDGES '${generated}/flights-date-formatted-final.csv'
COLUMNS src,*,*,*,*,*,*,*,*,*,*,target
INTO FlightPlan_Flights
ignore src
WHERE TAIL src = FlightPlan.flightNr HEAD target = Flights.id
FIELDS TERMINATED ';'
mode rows 

LOAD EDGES '${generated}/flightplan-final.csv'
COLUMNS src,target,*,*,*,*,*
INTO FlightPlan_Airplanes
ignore src, target
WHERE TAIL src = FlightPlan.flightNr HEAD target = Airplanes.aircraftType
FIELDS TERMINATED ';'
mode rows 

LOAD EDGES '${generated}/flightplan-final.csv'
COLUMNS src,*,target,*,*,*,*
INTO FlightPlan_OriginCity
ignore src, target
WHERE TAIL src = FlightPlan.flightNr HEAD target = Cities.city
FIELDS TERMINATED ';'
mode rows 

LOAD EDGES '${generated}/flightplan-final.csv'
COLUMNS src,*,*,target,*,*,*
INTO FlightPlan_DestinationCity
ignore src, target
WHERE TAIL src = FlightPlan.flightNr HEAD target = Cities.city
FIELDS TERMINATED ';'
mode rows 

LOAD EDGES '${generated}/flights-date-formatted-final.csv'
COLUMNS src,target,*,*,*,*,*,*,*,*,*
INTO Flights_Airplanes
ignore src, target
WHERE TAIL src = Flights.flightNr HEAD target = Airplanes.aircraftType
FIELDS TERMINATED ';'
mode rows 


LOAD EDGES '${generated}/passenger_legs_final.csv'
COLUMNS src,*,*,*,*,*,*,target
INTO PassengerItinerary_PassengerLegs
ignore src
WHERE TAIL src = PassengerItinerary.itineraryId HEAD target = PassengerLegs.id
FIELDS TERMINATED ';'
mode rows 

LOAD EDGES '${generated}/passenger_itenerary-final.csv'
COLUMNS src,target,*,*,*,*,*,*,*,*,*
INTO PassengerItinerary_OriginCity
ignore src, target
WHERE TAIL src = PassengerItinerary.itineraryId HEAD target = Cities.city
FIELDS TERMINATED ';'
mode rows 

LOAD EDGES '${generated}/passenger_itenerary-final.csv'
COLUMNS src,*,target,*,*,*,*,*,*,*,*
INTO PassengerItinerary_DestinationCity
ignore src, target
WHERE TAIL src = PassengerItinerary.itineraryId HEAD target = Cities.city
FIELDS TERMINATED ';'
mode rows 


LOAD EDGES '${generated}/passenger_legs_final.csv'
COLUMNS *,*,*,*,*,*,target,src
INTO PassengerLegs_Flights
ignore src, target
WHERE TAIL src = PassengerLegs.id HEAD target = Flights.id
FIELDS TERMINATED ';'
mode rows 

