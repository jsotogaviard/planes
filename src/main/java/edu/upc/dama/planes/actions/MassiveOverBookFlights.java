package edu.upc.dama.planes.actions;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.opensymphony.xwork2.Action;
import com.sparsity.sparksee.gdb.Condition;
import com.sparsity.sparksee.gdb.EdgesDirection;
import com.sparsity.sparksee.gdb.Graph;
import com.sparsity.sparksee.gdb.Objects;
import com.sparsity.sparksee.gdb.ObjectsIterator;
import com.sparsity.sparksee.gdb.Value;

import edu.upc.dama.dex.preparers.GraphAware;
import edu.upc.dama.dex.utils.DexUtil;

public class MassiveOverBookFlights implements Action, GraphAware {

	private Graph graph;

	@Override
	public void setGraph(Graph graph) {
		this.graph =graph;
	}

	@Override
	public Graph getGraph() {
		return graph;
	}

	@Override
	public String execute() throws Exception {
		if (graph == null) {
			graph = DexUtil.getDBGraph();
		}

		int passengerItineraryPassengerLegType = graph.findType("PassengerItinerary_PassengerLegs");

		int passengerItinerary = graph.findType("PassengerItinerary");
		int classAttr = graph.findAttribute(passengerItinerary, "class");
		int nameAttr = graph.findAttribute(passengerItinerary, "passengerName");
		int luggageWeightAttr = graph.findAttribute(passengerItinerary, "luggageWeight");

		int passengerType = graph.findType("PassengerLegs");
		int flightNrAttr = graph.findAttribute(passengerType, "flightNr");
		int PAssengerLegAttr = graph.findAttribute(passengerType, "id");

		int flightsType = graph.findType("Flights");
		int scheduledDepartureDateTimeAttr = graph.findAttribute(flightsType, "scheduledDepartureDateTime");
		int dateFlightAttr = graph.findAttribute(flightsType, "date");
		int idAttr = graph.findAttribute(flightsType, "id");
		int aircraftTypeAttr = graph.findAttribute(flightsType, "aircraftType");
		int flightNrattr = graph.findAttribute(flightsType, "flightNr");
		int scheduledArrivalDateTimeAttr = graph.findAttribute(flightsType, "scheduledArrivalDateTime");
		int updatedScheduledDepartureTimeAttr = graph.findAttribute(flightsType, "updatedScheduledDepartureTime");
		int updatedScheduledArrivalDateTimeAttr = graph.findAttribute(flightsType, "updatedScheduledArrivalTime");
		int actualDepartureDateTimeAttr = graph.findAttribute(flightsType, "actualDepartureDateTime");
		int actualArrivalDateTimeAttr = graph.findAttribute(flightsType, "actualArrivalDateTime");

		int passLegsFligtsType = graph.findType("PassengerLegs_Flights");
		int nextLegType = graph.findType("nextLeg");

		int flightsAirPlanes = graph.findType("Flights_Airplanes");
		int airPlanes = graph.findType("Airplanes");
		int capacityBusinessAttr = graph.findAttribute(airPlanes, "capacityBusiness");
		int weightCapacityAttr = graph.findAttribute(airPlanes, "weightCapacity");
		int capacityEconomyAttr = graph.findAttribute(airPlanes, "capacityEconomy");

		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm");
		Date actualDate = sdf.parse("04/08/2014 12:00");
		Long actualTime = actualDate.getTime();
		Value v = new Value();
		v.setTimestamp(actualTime);
		Objects allFlights = graph.select(dateFlightAttr, Condition.GreaterEqual, v);
		ObjectsIterator itAllFlights = allFlights.iterator();
		while(itAllFlights.hasNext()){
			Long flightId = itAllFlights.next();
			
			// Only take flights 
			// that have not departed yet
			Long passengerCapacity = 0L;
			Long weightCapacity = 0L;
			Objects airplane = graph.neighbors(flightId, flightsAirPlanes, EdgesDirection.Outgoing);
			ObjectsIterator airplaneIt = airplane.iterator();
			if (airplaneIt.hasNext()) {
				Long airPlaneId = airplaneIt.next();
				Value capacityBusiness = graph.getAttribute(airPlaneId, capacityBusinessAttr);
				Value capacityEconomy = graph.getAttribute(airPlaneId, capacityEconomyAttr);
				Value wieghtCapacity = graph.getAttribute(airPlaneId, weightCapacityAttr);
				passengerCapacity = capacityBusiness.getLong() + capacityEconomy.getLong();
				weightCapacity = wieghtCapacity.getLong();
			} else {
				throw new RuntimeException("no data " + graph.getAttribute(flightId, idAttr));
			}
			airplane.close();
			airplaneIt.close();

			Objects passengerLegs = graph.neighbors(flightId, passLegsFligtsType, EdgesDirection.Ingoing);
			ObjectsIterator passengerLegsIt = passengerLegs.iterator();
			Long nbLuggage = 0L;
			Set<String> removedPassengers= new HashSet<>();
			while (passengerLegsIt.hasNext()) {
				Long passengerLegId = passengerLegsIt.next();
				Objects passengers = graph.neighbors(passengerLegId, passengerItineraryPassengerLegType, EdgesDirection.Ingoing);
				ObjectsIterator passengerIt = passengers.iterator();
				if (passengerIt.hasNext()) {
					Long passengerId = passengerIt.next();
					Value classValue = graph.getAttribute(passengerId, classAttr);
					if (classValue.getString().equals("B")) {
						nbLuggage += 40;	
					} else {
						nbLuggage += 30;
					}
					long ngLegs = graph.degree(passengerId, passengerItineraryPassengerLegType, EdgesDirection.Outgoing);
					
					boolean haveLegs = ngLegs > 1;
					if (classValue.getString().equals("B") || haveLegs ) {
						// Cannot be taken out
						
					} else {
						Value name = graph.getAttribute(passengerId, nameAttr);
						removedPassengers.add(name.getString());
					}
					
				} else {
					throw new RuntimeException();
				}
				passengerIt.close();
			}
			if (passengerCapacity < passengerLegs.size()) {
				System.out.println(graph.getAttribute(flightId, idAttr) + " " + graph.getAttribute(flightId, aircraftTypeAttr));
				System.out.println(" passenger " + passengerCapacity + " nbPass " + passengerLegs.size());
				System.out.println(" weightCapacity " + weightCapacity + " nbLuggage " + nbLuggage);
				
				// get passengers names not business not connecting
				for (String string : removedPassengers) {
					System.out.println(string);
				}
			}
			
			if (weightCapacity < nbLuggage) {
				System.out.println(graph.getAttribute(flightId, idAttr) + " " + graph.getAttribute(flightId, aircraftTypeAttr));
				System.out.println(" passenger " + passengerCapacity + " nbPass " + passengerLegs.size());
				System.out.println(" weightCapacity " + weightCapacity + " nbLuggage " + nbLuggage);
			}
			passengerLegsIt.close();
			passengerLegs.close();
		}

		allFlights.close();
		itAllFlights.close();

		return Action.SUCCESS;

	}	
}
