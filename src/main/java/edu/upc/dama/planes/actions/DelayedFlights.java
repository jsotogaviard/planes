package edu.upc.dama.planes.actions;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
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

public class DelayedFlights implements Action, GraphAware {

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
		
		int passengerType = graph.findType("PassengerLegs");
		int flightNrAttr = graph.findAttribute(passengerType, "flightNr");
		
		int flightsType = graph.findType("Flights");
		int scheduledDepartureDateTimeAttr = graph.findAttribute(flightsType, "scheduledDepartureDateTime");
		int scheduledArrivalDateTimeAttr = graph.findAttribute(flightsType, "scheduledArrivalDateTime");
		int updatedScheduledDepartureTimeAttr = graph.findAttribute(flightsType, "updatedScheduledDepartureTime");
		int updatedScheduledArrivalDateTimeAttr = graph.findAttribute(flightsType, "updatedScheduledArrivalTime");
		int actualDepartureDateTimeAttr = graph.findAttribute(flightsType, "actualDepartureDateTime");
		int actualArrivalDateTimeAttr = graph.findAttribute(flightsType, "actualArrivalDateTime");
		
		int passLegsFligtsType = graph.findType("PassengerLegs_Flights");
		int nextLegType = graph.findType("nextLeg");
		
		Objects allFlights = graph.select(updatedScheduledDepartureTimeAttr, Condition.NotEqual, new Value());
		System.out.println(allFlights.size());
		ObjectsIterator it = allFlights.iterator();
		while (it.hasNext()) {
			Long oId = it.next();
			
			Long expectedArrivalTime = null;
			Value actualArrivalDateTime = graph.getAttribute(oId, actualArrivalDateTimeAttr);
			Value scheduledArrivalDateTime = graph.getAttribute(oId, scheduledArrivalDateTimeAttr);
			
			if (!actualArrivalDateTime.isNull()) {
				if (actualArrivalDateTime.getTimestamp() > scheduledArrivalDateTime.getTimestamp()) {
					expectedArrivalTime = actualArrivalDateTime.getTimestamp();
				}
			} else {
				Value actualDepartureDateTime = graph.getAttribute(oId, actualDepartureDateTimeAttr);
				Value scheduledDepartureDateTime = graph.getAttribute(oId, scheduledDepartureDateTimeAttr);
				if (!actualDepartureDateTime.isNull()) {
					if (actualDepartureDateTime.getTimestamp() > scheduledDepartureDateTime.getTimestamp()) {
						expectedArrivalTime = actualDepartureDateTime.getTimestamp() + 
								(scheduledArrivalDateTime.getTimestamp() - scheduledDepartureDateTime.getLong());
					}
				} else {
					Value updatedScheduledArrivalTime = graph.getAttribute(oId, updatedScheduledArrivalDateTimeAttr);
					if (updatedScheduledArrivalTime.getTimestamp() > scheduledArrivalDateTime.getTimestamp() ) {
						expectedArrivalTime = updatedScheduledArrivalTime.getTimestamp();
					}
				}
			}
			
			if (expectedArrivalTime != null) {
				Objects passengeLegs = graph.neighbors(oId, passLegsFligtsType, EdgesDirection.Ingoing);
				ObjectsIterator it1 = passengeLegs.iterator();
				while(it.hasNext()){
					Long passLEgId = it.next();
					Objects nextLeg = graph.neighbors(passLEgId, nextLegType, EdgesDirection.Outgoing);
					ObjectsIterator nextLEgIt = nextLeg.iterator();
					if (nextLEgIt.hasNext()) {
						Long nextLegId = nextLEgIt.next();
						Objects nextFlight = graph.neighbors(nextLegId, passLegsFligtsType, EdgesDirection.Outgoing);
						ObjectsIterator nextFlightIt = nextFlight.iterator();
						Long nextFlightId = nextFlightIt.next();
						
						Long departureTime = null;
						Value nextFlightActualDeparture = graph.getAttribute(nextFlightId, actualDepartureDateTimeAttr);
						if (!nextFlightActualDeparture.isNull()) {
							departureTime = nextFlightActualDeparture.getTimestamp();
						} else {
							Value nextFlightUpdateScheduleDeparture = graph.getAttribute(nextFlightId, updatedScheduledDepartureTimeAttr);
							if (!nextFlightUpdateScheduleDeparture.isNull()) {
								departureTime = nextFlightUpdateScheduleDeparture.getTimestamp();
							} else {
								Value scheduledDepartureDateTime = graph.getAttribute(nextFlightId, scheduledDepartureDateTimeAttr);
								departureTime = scheduledDepartureDateTime.getTimestamp();
							}
						}
						if ((departureTime - expectedArrivalTime) < 45 * 60 * 1000) {
							Value flightNbr = graph.getAttribute(nextLegId, flightNrAttr);
							Objects passengerIt = graph.neighbors(nextLegId, passengerItineraryPassengerLegType, EdgesDirection.Ingoing);
							ObjectsIterator it2 = passengerIt.iterator();
							if (it2.hasNext()) {
								Long passengerLEgId = it2.next();
								Value className = graph.getAttribute(passengerLEgId, classAttr);
								Value namePassenger = graph.getAttribute(passengerLEgId, nameAttr);
								saveData(flightNbr.getString(), className.getString(), namePassenger.getString());
							}
							it2.close();
							passengerIt.close();
						}
						
						nextFlight.close();
						nextFlightIt.close();
					}
					nextLeg.close();
					nextLEgIt.close();
				}
				passengeLegs.close();
				it1.close();
			}
			
		}
		it.close();
		allFlights.close();
		return Action.SUCCESS;
	}
	
	private void saveData(String flight, String className, String name) {
		System.out.println(flight +  " " + className + " " + name);
		
	}

	protected void delayedFlights() {

		int flightsType = graph.findType("Flights");
		int isDelayedType = graph.findAttribute(flightsType, "isDelayed");
		int flightNbrType = graph.findAttribute(flightsType, "flightNr");
		
		Value v = new Value();
		int passengerFlightsType = graph.findType("PassengerLegs_Flights");
		
		int passengerItinerayPassengerLegsType = graph.findType("PassengerItinerary_PassengerLegs");
		
		int passengerType = graph.findType("PassengerItinerary");
		int nameType = graph.findAttribute(passengerType, "passengerName");
		int classType = graph.findAttribute(passengerType, "class");
		Objects businessPassengers = graph.select(classType, Condition.Equal, v.setString("B"));
		
		Objects delayedFlights = graph.select(isDelayedType, Condition.Equal, v.setBoolean(true));
		int delayedEdgeType = graph.findType("delayedPassenger_Flights");
		
		Map<String,Set<String>> flightsToDelay = new HashMap<>();
		
		ObjectsIterator it = delayedFlights.iterator();
		while (it.hasNext()) {
			Long oId = it.next();
			long degree = graph.degree(oId, delayedEdgeType, EdgesDirection.Outgoing);
			if (degree > 10) {
				Objects delayedPassengers = graph.neighbors(oId, delayedEdgeType, EdgesDirection.Outgoing);
				delayedPassengers.intersection(businessPassengers);
				if(delayedPassengers.size() > 10){
					Value flightNbr = graph.getAttribute(oId, flightNbrType);
					Objects passengerLesgs = graph.neighbors(oId, passengerFlightsType, EdgesDirection.Ingoing);
					Objects passengerDelayed = graph.neighbors(passengerLesgs, passengerItinerayPassengerLegsType, EdgesDirection.Ingoing);
					ObjectsIterator passIt = passengerDelayed.iterator();
					Set<String> passNames = new HashSet<>();
					while (passIt.hasNext()) {
						Long passOid = passIt.next();
						Value passName = graph.getAttribute(passOid, nameType);
						passNames.add(passName.getString());
					}
					passIt.close();
					passengerDelayed.close();
					passengerLesgs.close();
					flightsToDelay.put(flightNbr.getString(), passNames);
				}
				delayedPassengers.close();
			}
		}
		
		it.close();
		delayedFlights.close();
		businessPassengers.close();
		
		System.out.println(flightsToDelay);
	}
	
}
