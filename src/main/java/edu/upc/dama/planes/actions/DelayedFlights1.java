package edu.upc.dama.planes.actions;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
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

public class DelayedFlights1 implements Action, GraphAware {

	private Graph graph;
	
	private Map<String, Set<User>> delayedPassengers = new HashMap<>();
	
	private Map<String, Set<User>> delayedBusinessPassengers = new HashMap<>();
	
	private Objects filteredFlights;

	
	public Objects getFilteredFlights() {
		return filteredFlights;
	}

	public void setFilteredFlights(Objects filteredFlights) {
		this.filteredFlights = filteredFlights;
	}

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
		int PAssengerLegAttr = graph.findAttribute(passengerType, "id");
		
		int flightsType = graph.findType("Flights");
		int scheduledDepartureDateTimeAttr = graph.findAttribute(flightsType, "scheduledDepartureDateTime");
		int flightNrattr = graph.findAttribute(flightsType, "flightNr");
		int scheduledArrivalDateTimeAttr = graph.findAttribute(flightsType, "scheduledArrivalDateTime");
		int updatedScheduledDepartureTimeAttr = graph.findAttribute(flightsType, "updatedScheduledDepartureTime");
		int updatedScheduledArrivalDateTimeAttr = graph.findAttribute(flightsType, "updatedScheduledArrivalTime");
		int actualDepartureDateTimeAttr = graph.findAttribute(flightsType, "actualDepartureDateTime");
		int actualArrivalDateTimeAttr = graph.findAttribute(flightsType, "actualArrivalDateTime");
		
		int passLegsFligtsType = graph.findType("PassengerLegs_Flights");
		int nextLegType = graph.findType("nextLeg");
		Objects f1;
		if(filteredFlights== null){
			f1 = graph.select(updatedScheduledDepartureTimeAttr, Condition.NotEqual, new Value());
			Objects f2 = graph.select(actualDepartureDateTimeAttr, Condition.NotEqual, new Value());
			f1.union(f2);
		}
		else{
			f1 = filteredFlights;
		}
		ObjectsIterator itf1 = f1.iterator();
		int countDelayed = 0;
		while (itf1.hasNext()) {
			Long flightId = itf1.next();
			Long expectedArrivalTime = null;
			Value actualArrivalDateTime = graph.getAttribute(flightId, actualArrivalDateTimeAttr);
			Value scheduledArrivalDateTime = graph.getAttribute(flightId, scheduledArrivalDateTimeAttr);
			
			if (!actualArrivalDateTime.isNull()) {
				if (actualArrivalDateTime.getTimestamp() - scheduledArrivalDateTime.getTimestamp() >= 60 * 60 * 1000) {
					expectedArrivalTime = actualArrivalDateTime.getTimestamp();
				}
			} else {
				Value actualDepartureDateTime = graph.getAttribute(flightId, actualDepartureDateTimeAttr);
				Value scheduledDepartureDateTime = graph.getAttribute(flightId, scheduledDepartureDateTimeAttr);
				if (!actualDepartureDateTime.isNull()) {
					if (actualDepartureDateTime.getTimestamp() - scheduledDepartureDateTime.getTimestamp() >= 60 * 60 * 1000) {
						expectedArrivalTime = actualDepartureDateTime.getTimestamp() + 
								(scheduledArrivalDateTime.getTimestamp() - scheduledDepartureDateTime.getTimestamp());
					}
				} else {
					Value updatedScheduledArrivalTime = graph.getAttribute(flightId, updatedScheduledArrivalDateTimeAttr);
					if (updatedScheduledArrivalTime.getTimestamp() - scheduledArrivalDateTime.getTimestamp() >= 60 * 60 * 1000) {
						expectedArrivalTime = updatedScheduledArrivalTime.getTimestamp();
					}
				}
			}
			
			if (expectedArrivalTime != null) {
				countDelayed++;
				Objects passengeLegs = graph.neighbors(flightId, passLegsFligtsType, EdgesDirection.Ingoing);
				ObjectsIterator it1 = passengeLegs.iterator();
				while(it1.hasNext()){
					Long passLEgId = it1.next();
					Objects nextLeg = graph.neighbors(passLEgId, nextLegType, EdgesDirection.Outgoing);
					ObjectsIterator nextLegIt = nextLeg.iterator();
					if (nextLegIt.hasNext()) {
						Long nextLegId = nextLegIt.next();
						//System.out.println(graph.getAttribute(nextLegId, PAssengerLegAttr));
						//Objects nextFlight = graph.neighbors(nextLegId, passLegsFligtsType, EdgesDirection.Outgoing);
						//ObjectsIterator nextFlightIt = nextFlight.iterator();
						//Long nextFlightId = nextFlightIt.next();
						//Long departureTime = null;
						/**Value nextFlightActualDeparture = graph.getAttribute(nextFlightId, actualDepartureDateTimeAttr);
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
						}*/
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
						//nextFlight.close();
						//nextFlightIt.close();
					}
					nextLeg.close();
					nextLegIt.close();
				}
				passengeLegs.close();
				it1.close();
			}
			
		}
		itf1.close();
		f1.close();
		System.out.println("delayed " + countDelayed++);
		System.out.println("delayed business passengers ");
		for (Entry<String, Set<User>> entry : delayedBusinessPassengers.entrySet()) {
			System.out.println(entry.getValue().size() + " " + entry);
		}
		
		System.out.println("delayed passengers ");
		for (Entry<String, Set<User>> entry : delayedPassengers.entrySet()) {
			System.out.println(entry);
		}
		System.out.println(delayedPassengers);
		return Action.SUCCESS;
	}
	
	private void saveData(String flight, String className, String name) {
		Set<User> passengers = delayedPassengers.get(flight);
		if (passengers == null) {
			passengers = new HashSet<>();
			delayedPassengers.put(flight, passengers);
		}
		passengers.add(new User(name, className));
		
		passengers = delayedBusinessPassengers.get(flight);
		if (passengers == null) {
			passengers = new HashSet<>();
			delayedBusinessPassengers.put(flight, passengers);
		}
		if (className.equals("B")) {
			passengers.add(new User(name, className));
		}
		
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
