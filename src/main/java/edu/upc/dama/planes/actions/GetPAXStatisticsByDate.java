package edu.upc.dama.planes.actions;

import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.ibm.icu.util.Calendar;
import com.opensymphony.xwork2.Action;
import com.sparsity.sparksee.gdb.Condition;
import com.sparsity.sparksee.gdb.EdgesDirection;
import com.sparsity.sparksee.gdb.Graph;
import com.sparsity.sparksee.gdb.Objects;
import com.sparsity.sparksee.gdb.ObjectsIterator;
import com.sparsity.sparksee.gdb.Value;

import edu.upc.dama.dex.preparers.GraphAware;
import edu.upc.dama.dex.utils.DexUtil;

public class GetPAXStatisticsByDate implements Action, GraphAware {

	private Graph graph;
	
	private Objects filteredFlights;


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
		
		int flightsType = graph.findType("Flights");
		int dateAttrFlights = graph.findAttribute(flightsType, "date");
		
		java.util.Calendar cal = new GregorianCalendar();
		cal.clear();
		cal.set(2014,7,3);
		//cal.set(Calendar.HOUR, 0);
		
		Value valDate = new Value();
		valDate.setTimestamp(cal.getTimeInMillis());
		
		Objects flights = graph.select(dateAttrFlights,Condition.GreaterEqual,valDate);
		GetPAXStatistics pax = new GetPAXStatistics();
		pax.setFilteredFlights(flights);
		pax.setGraph(graph);
		pax.execute();
		
		return Action.SUCCESS;
	}
	
	
	
}
