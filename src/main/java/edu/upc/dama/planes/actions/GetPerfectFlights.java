package edu.upc.dama.planes.actions;

import com.opensymphony.xwork2.Action;
import com.sparsity.sparksee.gdb.Condition;
import com.sparsity.sparksee.gdb.EdgesDirection;
import com.sparsity.sparksee.gdb.Graph;
import com.sparsity.sparksee.gdb.Objects;
import com.sparsity.sparksee.gdb.Value;

import edu.upc.dama.dex.preparers.GraphAware;

public class GetPerfectFlights implements Action, GraphAware {

	private Graph graph;

	private void percentDelayedFlightsManagedByVictor() {

		Value v = new Value();
		int flightsType = graph.findType("Flights");
		int isDelayedType = graph.findAttribute(flightsType, "isDelayed");
		int flightNbrType = graph.findAttribute(flightsType, "flightNr");

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
		Objects allParisFlights = graph.neighbors(allParisFlightsPlans,
				flightPlanFlightsType, EdgesDirection.Ingoing);

		Objects delayedFlights = graph.select(isDelayedType, Condition.Equal,
				v.setBoolean(true));

		delayedFlights.intersection(allParisFlights);
		System.out.println(delayedFlights.size() / allParisFlights.size());
	}

	@Override
	public String execute() throws Exception {

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
