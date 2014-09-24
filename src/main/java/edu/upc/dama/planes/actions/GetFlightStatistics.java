package edu.upc.dama.planes.actions;

import com.opensymphony.xwork2.Action;
import com.sparsity.sparksee.gdb.Condition;
import com.sparsity.sparksee.gdb.EdgesDirection;
import com.sparsity.sparksee.gdb.Graph;
import com.sparsity.sparksee.gdb.Objects;
import com.sparsity.sparksee.gdb.Value;

import edu.upc.dama.dex.preparers.GraphAware;

public class GetFlightStatistics implements Action, GraphAware {

	private Graph graph;
	
	private double perfectFlightsManagedByVictor;
	
	private double paxMissingTheirConnection;
	
	private Objects delayedFlights;
	
	private Objects allParisFlights;

	private double getPercentPerfectFlightsManagedByVictor() {

		Value v = new Value();
		int flightsType = graph.findType("Flights");
		int isDelayedType = graph.findAttribute(flightsType, "isDelayed");

		int flightPlanType = graph.findType("FlightPlan");
		int originCityType = graph.findAttribute(flightPlanType, "originCity");
		int destinationCityType = graph.findAttribute(flightPlanType,
				"destinationCity");

		int flightPlanFlightsType = graph.findType("FlightPlan_Flights");

		Objects originParisFlightPlan = graph.select(originCityType,
				Condition.Equal, v.setString("Paris"));
		Objects destinationParisFlightPlan = graph.select(destinationCityType,
				Condition.Equal, v.setString("Paris"));
		originParisFlightPlan.union(destinationParisFlightPlan);
		Objects allParisFlightsPlans = originParisFlightPlan;
		allParisFlights = graph.neighbors(allParisFlightsPlans,
				flightPlanFlightsType, EdgesDirection.Ingoing);

		delayedFlights = graph.select(isDelayedType, Condition.Equal,
				v.setBoolean(true));

		delayedFlights.intersection(allParisFlights);
		return 1- (delayedFlights.size() / allParisFlights.size());
	}

	@Override
	public String execute() throws Exception {
		perfectFlightsManagedByVictor = getPercentPerfectFlightsManagedByVictor();
		
		int passengetLegs_flights_type = graph.findType("PassengerLegs_Flights");
		
		int passengerItinerary_passengerLegs_type = graph.findType("PassengerItinerary_PassengerLegs");
		
		Objects passengerLegsAffected = graph.neighbors(delayedFlights, passengetLegs_flights_type, EdgesDirection.Ingoing);
		
		Objects passengerItineraries = graph.neighbors(passengerLegsAffected, passengerItinerary_passengerLegs_type, EdgesDirection.Ingoing);
		
		return Action.SUCCESS;
	}

	@Override
	public void setGraph(Graph graph) {
		this.graph = graph;
	}

	@Override
	public Graph getGraph() {
		return graph;
	}

}
