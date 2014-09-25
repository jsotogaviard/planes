package edu.upc.dama.planes.actions;

import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
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

public class GetPAXStatistics implements Action, GraphAware {

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
		int dateAttr = graph.findAttribute(passengerType, "date");
		
		int flightsType = graph.findType("Flights");
		int updatedScheduledDepartureTimeAttr = graph.findAttribute(flightsType, "updatedScheduledDepartureTime");
		int actualDepartureDateTimeAttr = graph.findAttribute(flightsType, "actualDepartureDateTime");
		
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

				countDelayed++;
				Objects passengeLegs = graph.neighbors(flightId, passLegsFligtsType, EdgesDirection.Ingoing);
				ObjectsIterator it1 = passengeLegs.iterator();
				while(it1.hasNext()){
					Long passLEgId = it1.next();
					Objects nextLeg = graph.neighbors(passLEgId, nextLegType, EdgesDirection.Outgoing);
					ObjectsIterator nextLegIt = nextLeg.iterator();
					if (nextLegIt.hasNext()) {
						Long nextLegId = nextLegIt.next();
						Value flightNbr = graph.getAttribute(nextLegId, flightNrAttr);
						Objects passengerIt = graph.neighbors(nextLegId, passengerItineraryPassengerLegType, EdgesDirection.Ingoing);
						Value datePassenger = graph.getAttribute(nextLegId, dateAttr);
						ObjectsIterator it2 = passengerIt.iterator();
						if (it2.hasNext()) {
							Long passengerLEgId = it2.next();
							Value className = graph.getAttribute(passengerLEgId, classAttr);
							Value namePassenger = graph.getAttribute(passengerLEgId, nameAttr); 
							saveData(flightNbr.getString()+" Time["+getStringDate(datePassenger)+"]", className.getString(), namePassenger.getString());
						}
						it2.close();
						passengerIt.close();
					}
					nextLeg.close();
					nextLegIt.close();
				}
				passengeLegs.close();
				it1.close();
			
			
		}
		itf1.close();
		f1.close();
		System.out.println("Delayed flights:" + countDelayed);
		System.out.println("Delayed connect business passengers: ");
		printMap(delayedBusinessPassengers);
		
		System.out.println("Delayed connect all passengers: ");
		printMap(delayedPassengers);
		return Action.SUCCESS;
	}
	
	private void printMap(Map<String, Set<User>> map){

		Iterator<String> itMap = map.keySet().iterator();
		while(itMap.hasNext()){
			String key = itMap.next();
			System.out.println(key+" Passengers: "+map.get(key).size());
			Iterator<User> itUsers = map.get(key).iterator();
			while(itUsers.hasNext()){
				User u = itUsers.next();
				System.out.println("\t"+u.getClazz()+" "+u.getName());
			}
		}
		
	}
	
	private String getStringDate(Value value) throws UnsupportedEncodingException{
		
		DateFormat date = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss.SSS");
		String result = new String(date.format(
				value.getTimestampAsDate().getTime()).getBytes(),
				"UTF-8");
		
		return result;
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
	
}
