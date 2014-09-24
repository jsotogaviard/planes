package edu.upc.dama.planes.preparers;

import java.text.SimpleDateFormat;

import com.sparsity.sparksee.gdb.Graph;
import com.sparsity.sparksee.gdb.Objects;
import com.sparsity.sparksee.gdb.ObjectsIterator;
import com.sparsity.sparksee.gdb.Value;

import edu.upc.dama.dex.preparers.GraphAware;
import edu.upc.dama.dex.preparers.Preparer;

public class GenerateKeys implements Preparer, GraphAware {

	private Graph graph;

	@Override
	public void setGraph(Graph graph) {
		this.graph = graph;
	}

	@Override
	public Graph getGraph() {
		return graph;
	}

	@Override
	public void execute() throws Exception {
		int flightsType = graph.findType("Flights");		
		
		int flightNr_attr = graph.findAttribute(flightsType, "flightNr");

		int dateAttr = graph.findAttribute(flightsType, "date");
		
		int idAttr = graph.findAttribute(flightsType, "id");

		Objects flights = graph.select(flightsType);
		
		ObjectsIterator it = flights.iterator();
		
		Value idV = new Value();
		
		SimpleDateFormat simpleDf = new SimpleDateFormat(
				"dd/MM/yyyy");
		
		
		while(it.hasNext()){			
			Long current = it.next();
			Value flightNr = graph.getAttribute(current, flightNr_attr);
			Value date = graph.getAttribute(current, dateAttr);
			String id = flightNr.getString();			
			id +="_"+simpleDf.format(date.getTimestampAsDate());
			idV.setString(id);
			graph.setAttribute(current, idAttr, idV);			
		}
		
		flights.close();
	}

}
