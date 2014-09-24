package edu.upc.dama.planes.preparers;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import com.sparsity.sparksee.gdb.Condition;
import com.sparsity.sparksee.gdb.Graph;
import com.sparsity.sparksee.gdb.Objects;
import com.sparsity.sparksee.gdb.ObjectsIterator;
import com.sparsity.sparksee.gdb.Value;
import com.sparsity.sparksee.gdb.Values;
import com.sparsity.sparksee.gdb.ValuesIterator;

import edu.upc.dama.dex.preparers.GraphAware;
import edu.upc.dama.dex.preparers.Preparer;

public class CreateLegEdges implements Preparer, GraphAware {

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
		int passengerLegsType = graph.findType("PassengerLegs");
		
		int nextLegEdgeType = graph.findType("nextLeg");
		
		int itineraryIdAttr = graph.findAttribute(passengerLegsType,
				"itineraryId");

		int legIdAttr = graph.findAttribute(passengerLegsType, "legId");

		Values vs = graph.getValues(itineraryIdAttr);
		try {
			ValuesIterator it = vs.iterator();
			while (it.hasNext()) {
				Value v = it.next();
				Objects o = graph.select(itineraryIdAttr, Condition.Equal, v);
				try {
					if (o.size() > 1) {
						Map<Long, Long> legs = new TreeMap<Long, Long>();

						ObjectsIterator it2 = o.iterator();
						while (it2.hasNext()) {
							Long legOid = it2.next();
							Value legId = graph.getAttribute(legOid, legIdAttr);
							legs.put(legId.getLong(), legOid);
						}
						it2.close();
						
						Set<Long> legIds= legs.keySet();
						Iterator<Long> itLegs = legIds.iterator();
						long currentOid = legs.get(itLegs.next());
						while(itLegs.hasNext()){							
							long nextOid = legs.get(itLegs.next());
							graph.newEdge(nextLegEdgeType, currentOid, nextOid);
							currentOid = nextOid;
						}
					}
				} finally {
					o.close();
				}

			}
		} finally {
			vs.close();
		}

	}

}
