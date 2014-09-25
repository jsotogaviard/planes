package edu.upc.dama.planes.actions;

import java.util.GregorianCalendar;

import com.opensymphony.xwork2.Action;
import com.sparsity.sparksee.gdb.Condition;
import com.sparsity.sparksee.gdb.EdgesDirection;
import com.sparsity.sparksee.gdb.Graph;
import com.sparsity.sparksee.gdb.Objects;
import com.sparsity.sparksee.gdb.Value;

import edu.upc.dama.dex.preparers.GraphAware;
import edu.upc.dama.dex.utils.DexUtil;

public class GetPAXStatisticsByDate implements Action, GraphAware {

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
		
		Value v = new Value();
		int flightsType = graph.findType("Flights");
		int date_attr = graph.findAttribute(flightsType,"date");
		
		GregorianCalendar yesterday = new GregorianCalendar();
		yesterday.clear();
		yesterday.set(2014, 07, 03);
		
		Value v1 = new Value();
		v1.setTimestamp(yesterday.getTimeInMillis());
		
		GregorianCalendar tomorrow = new GregorianCalendar();
		tomorrow.clear();
		tomorrow.set(2014, 07, 05);
		
		Value v2 = new Value();
		v2.setTimestamp(tomorrow.getTimeInMillis());
		
		Objects todayFlights = graph.select(date_attr, Condition.Between, v1, v2); 
		
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
		destinationParisFlightPlan.close();
		Objects allParisFlightsPlans = originParisFlightPlan;
		Objects allParisFlights = graph.neighbors(allParisFlightsPlans,
				flightPlanFlightsType, EdgesDirection.Outgoing);
		allParisFlightsPlans.close();
		allParisFlights.intersection(todayFlights);
		todayFlights.close();
		
		Objects delayedFlights = graph.select(isDelayedType, Condition.Equal,
				v.setBoolean(true));
		

		delayedFlights.intersection(allParisFlights);
		
		GetPAXStatistics pax = new GetPAXStatistics();
		pax.setFilteredFlights(delayedFlights);
		pax.setGraph(graph);
		pax.execute();
		allParisFlights.close();
		
		return Action.SUCCESS;
	}
	
	
	
}
