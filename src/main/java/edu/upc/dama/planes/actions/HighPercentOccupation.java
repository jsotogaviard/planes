package edu.upc.dama.planes.actions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.opensymphony.xwork2.Action;
import com.sparsity.sparksee.gdb.EdgesDirection;
import com.sparsity.sparksee.gdb.Graph;
import com.sparsity.sparksee.gdb.Objects;
import com.sparsity.sparksee.gdb.ObjectsIterator;
import com.sparsity.sparksee.gdb.Value;

import edu.upc.dama.dex.preparers.GraphAware;
import edu.upc.dama.dex.utils.DexUtil;

public class HighPercentOccupation implements Action, GraphAware {

	private Graph graph;
	private Map<String,Long> flight = new HashMap<>();
	private Map<String,String> flightType = new HashMap<>();
	private Map<String,List<Long>> nbPassengersMap = new HashMap<>();
	private Map<String,List<Double>> nbPassengerPercent = new HashMap<>();

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

		int flightPlanType = graph.findType("FlightPlan");
		int originCityType = graph.findAttribute(flightPlanType, "originCity");
		
		int passengerItineraryPassengerLegType = graph.findType("PassengerItinerary_PassengerLegs");

		int passengerItinerary = graph.findType("PassengerItinerary");
		int classAttr = graph.findAttribute(passengerItinerary, "class");
		int nameAttr = graph.findAttribute(passengerItinerary, "passengerName");
		int luggageWeightAttr = graph.findAttribute(passengerItinerary, "luggageWeight");

		int passengerType = graph.findType("PassengerLegs");
		int flightNrAttr = graph.findAttribute(passengerType, "flightNr");
		int PAssengerLegAttr = graph.findAttribute(passengerType, "id");
		int flightIdAttr = graph.findAttribute(passengerType, "flightId");
		int legIdAttr = graph.findAttribute(passengerType, "legId");

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

		int flightPlanFlightsType = graph.findType("FlightPlan_Flights");
		
		int flightsAirPlanes = graph.findType("Flights_Airplanes");
		int airPlanes = graph.findType("Airplanes");
		int capacityBusinessAttr = graph.findAttribute(airPlanes, "capacityBusiness");
		int weightCapacityAttr = graph.findAttribute(airPlanes, "weightCapacity");
		int capacityEconomyAttr = graph.findAttribute(airPlanes, "capacityEconomy");

		Objects allFlights = graph.select(flightsType);
		ObjectsIterator itAllFlights = allFlights.iterator();
		while(itAllFlights.hasNext()){
			Long flightId = itAllFlights.next();
			
			// Only take flights 
			// that have not departed yet
			Long passengerCapacity = 0L;
//			Long weightCapacity = 0L;
			Objects airplane = graph.neighbors(flightId, flightsAirPlanes, EdgesDirection.Outgoing);
			ObjectsIterator airplaneIt = airplane.iterator();
			if (airplaneIt.hasNext()) {
				Long airPlaneId = airplaneIt.next();
				Value capacityBusiness = graph.getAttribute(airPlaneId, capacityBusinessAttr);
				Value capacityEconomy = graph.getAttribute(airPlaneId, capacityEconomyAttr);
				Value wieghtCapacity = graph.getAttribute(airPlaneId, weightCapacityAttr);
				passengerCapacity = capacityBusiness.getLong() + capacityEconomy.getLong();
//				weightCapacity = wieghtCapacity.getLong();
			} else {
				throw new RuntimeException("no data " + graph.getAttribute(flightId, idAttr));
			}
			airplane.close();
			airplaneIt.close();

			long nbPassengers = graph.degree(flightId, passLegsFligtsType, EdgesDirection.Ingoing);
			double percentFilled = (double)nbPassengers / (double)passengerCapacity ;
			if (percentFilled == 1) {
				Value flightNr = graph.getAttribute(flightId, flightNrattr);
				String flightName = flightNr.getString();
				Long nbFlights = flight .get(flightName);
				if (nbFlights == null) {
					flight.put(flightName, 1L);
					List<Long> nbPassengerList = new ArrayList<>();
					nbPassengerList.add(nbPassengers);
					nbPassengersMap.put(flightName, nbPassengerList);
					List<Double> nbPassengerPercentList = new ArrayList<>();
					nbPassengerPercentList.add(percentFilled);
					nbPassengerPercent.put(flightName, nbPassengerPercentList);
					flightType.put(flightName, graph.getAttribute(flightId, aircraftTypeAttr).getString());
				} else {
					flight.put(flightName, ++nbFlights);
					nbPassengersMap.get(flightName).add(nbPassengers);
					nbPassengerPercent.get(flightName).add(percentFilled);
				}
				
			}
		}

		allFlights.close();
		itAllFlights.close();

		for (Entry<String, Long> entry : flight.entrySet()) {
			System.out.println(entry + " " + flightType.get(entry.getKey()));
//			System.out.println(nbPassengersMap.get(entry.getKey()));
//			System.out.println(nbPassengerPercent.get(entry.getKey()));
		}
		
		return Action.SUCCESS;

	}	
}
