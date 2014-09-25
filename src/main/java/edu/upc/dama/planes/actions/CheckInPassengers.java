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

public class CheckInPassengers implements Action, GraphAware {

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
