package edu.upc.dama.planes.actions;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import com.ibm.icu.impl.duration.impl.DataRecord.EDecimalHandling;
import com.opensymphony.xwork2.Action;
import com.sparsity.sparksee.gdb.Condition;
import com.sparsity.sparksee.gdb.EdgesDirection;
import com.sparsity.sparksee.gdb.Graph;
import com.sparsity.sparksee.gdb.Objects;
import com.sparsity.sparksee.gdb.ObjectsIterator;
import com.sparsity.sparksee.gdb.Value;

import edu.upc.dama.dex.preparers.GraphAware;
import edu.upc.dama.dex.utils.DexUtil;

public class OnTimePassengersThruParis implements Action, GraphAware {

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

		Objects allPassengers = graph.select(passengerItinerary);
		ObjectsIterator allPassIt = allPassengers.iterator();
		while(allPassIt.hasNext()){
			Long passId = allPassIt.next();
			boolean considerThisPassenger = false;
			
			Objects legsOfPassenger = graph.neighbors(passId, passengerItineraryPassengerLegType, EdgesDirection.Outgoing);
			ObjectsIterator legsOfPassengerIt = legsOfPassenger.iterator();
			TreeMap<Long, Long> legMap = new TreeMap<>();
			while(legsOfPassengerIt.hasNext()){
			
				Long legsOfPassengerId = legsOfPassengerIt.next();
				Value legId = graph.getAttribute(legsOfPassengerId, legIdAttr);
				legMap.put(legId.getLong(), legsOfPassengerId);
				Objects singleFlight = graph.neighbors(legsOfPassengerId, passLegsFligtsType, EdgesDirection.Outgoing);
				ObjectsIterator singleFlightIt = singleFlight.iterator();
				if (singleFlightIt.hasNext()) {
					Long singleFlightId = singleFlightIt.next();
					Objects singleFlightPlan = graph.neighbors(singleFlightId, flightPlanFlightsType, EdgesDirection.Outgoing);
					ObjectsIterator singleFlightPlanIt = singleFlightPlan.iterator();
					if (singleFlightPlanIt.hasNext()) {
						Long singleFlightPlanId = singleFlightIt.next();
						Value originCity = graph.getAttribute(singleFlightPlanId, originCityType);
						if (originCity.getString().equals("Paris")) {
							considerThisPassenger = true;
						}
					} else {
						throw new RuntimeException();
					}
					singleFlightPlan.close();
					singleFlightPlanIt.close();
				} else {
					throw new RuntimeException();
				}
				singleFlight.close();
				singleFlightIt.close();
				
				if(considerThisPassenger){
					Entry<Long, Long> lastEntry = legMap.pollLastEntry();
				}
				
			}
			legsOfPassenger.close();
			legsOfPassengerIt.close();
		}
		allPassengers.close();
		allPassIt.close();
		
		Value v = new Value();
		v.setString("AP052_04/08/2014");
		Objects preciseFlight = graph.select(idAttr, Condition.Equal, v);
		ObjectsIterator itPreciseFlight = preciseFlight.iterator();
		while(itPreciseFlight.hasNext()){
			Long preceiseFlightId = itPreciseFlight.next();
			long nbPassengers = graph.degree(preceiseFlightId, passLegsFligtsType, EdgesDirection.Ingoing);
			Objects airplane = graph.neighbors(preceiseFlightId, flightsAirPlanes, EdgesDirection.Outgoing);
			ObjectsIterator airplaneIt = airplane.iterator();
			if (airplaneIt.hasNext()) {
				Long airPlaneId = airplaneIt.next();
				Value capacityBusiness = graph.getAttribute(airPlaneId, capacityBusinessAttr);
				Value capacityEconomy = graph.getAttribute(airPlaneId, capacityEconomyAttr);
				Value wieghtCapacity = graph.getAttribute(airPlaneId, weightCapacityAttr);
				
				System.out.println(
						" nbPassengers " + nbPassengers + 
						" capacityBusiness " + capacityBusiness + 
						" capacityEconomy " + capacityEconomy + 
						" wieghtCapacity " + wieghtCapacity + 
						" loadFactor " + (capacityBusiness.getLong() * 40 + capacityEconomy.getLong() * 30));
			} else {
				throw new RuntimeException("no data " + graph.getAttribute(preceiseFlightId, idAttr));
			}
			
		}

		preciseFlight.close();
		itPreciseFlight.close();

		return Action.SUCCESS;

	}	
}
