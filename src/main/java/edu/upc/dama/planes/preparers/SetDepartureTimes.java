package edu.upc.dama.planes.preparers;

import com.sparsity.sparksee.gdb.EdgesDirection;
import com.sparsity.sparksee.gdb.Graph;
import com.sparsity.sparksee.gdb.Objects;
import com.sparsity.sparksee.gdb.ObjectsIterator;
import com.sparsity.sparksee.gdb.Value;

import edu.upc.dama.dex.preparers.GraphAware;
import edu.upc.dama.dex.preparers.Preparer;

public class SetDepartureTimes implements Preparer, GraphAware {

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

		int flights_type = graph.findType("Flights");

		int flightplan_flights_type = graph.findType("FlightPlan_Flights");

		int isDelayed_attr = graph.findAttribute(flights_type, "isDelayed");

		int flightplan_type = graph.findType("FlightPlan");
		int scheduledFlyingTime_attr = graph.findAttribute(flightplan_type,
				"scheduledFlyingTime");

		int updatedScheduledArrivalDate_attr = graph.findAttribute(
				flights_type, "updatedScheduledArrivalTime");
		int updatedScheduledDepartureTime_attr = graph.findAttribute(
				flights_type, "updatedScheduledDepartureTime");

		int scheduledArrivalDate_attr = graph.findAttribute(flights_type,
				"scheduledArrivalDateTime");
		int scheduledDepartureTime_attr = graph.findAttribute(flights_type,
				"scheduledDepartureDateTime");

		int actualArrivalDateTime_attr = graph.findAttribute(flights_type,
				"actualArrivalDateTime");

		int actualDepartureDateTime_attr = graph.findAttribute(flights_type,
				"actualDepartureDateTime");

		Objects flights = graph.select(flights_type);
		ObjectsIterator it = flights.iterator();

		Value scheduledArrivalTimeValue = new Value();

		Value updatedArrivalTimeValue = new Value();

		Value isDelayedValue = new Value();

		boolean isDelayed = false;
		
		long delay = (60 * 60 * 1000 )-1;

		while (it.hasNext()) {

			Long oid = it.next();

			Value scheduledDepartureTimeValue = graph.getAttribute(oid,
					scheduledDepartureTime_attr);

			Value updatedScheduledDepartureDateValue = graph.getAttribute(oid,
					updatedScheduledDepartureTime_attr);

			Value actualArrivalDateTimeValue = graph.getAttribute(oid,
					actualArrivalDateTime_attr);

			Value actualDepartureDateTimeValue = graph.getAttribute(oid,
					actualDepartureDateTime_attr);

			isDelayed = false;
			Objects flightPlan = graph.neighbors(oid, flightplan_flights_type,
					EdgesDirection.Ingoing);
			if (!flightPlan.isEmpty()) {

				long planOid = flightPlan.any();

				Value scheduledFlyingTimeValue = graph.getAttribute(planOid,
						scheduledFlyingTime_attr);

				// number of spent hours in millis
				long scheduledFlyingTimeInMillis = scheduledFlyingTimeValue
						.getLong();

				scheduledArrivalTimeValue.setNull();

				// updating scheduledArrivalTime
				long scheduledDepartureTimeInMillis = scheduledDepartureTimeValue
						.getTimestamp();

				scheduledArrivalTimeValue
						.setTimestamp(scheduledFlyingTimeInMillis
								+ scheduledDepartureTimeInMillis);
				graph.setAttribute(oid, scheduledArrivalDate_attr,
						scheduledArrivalTimeValue);

				if (!actualArrivalDateTimeValue.isNull()) {
					isDelayed = (actualArrivalDateTimeValue.getTimestamp()  > scheduledArrivalTimeValue
							.getTimestamp() + delay);

				} else {
					if ((!actualDepartureDateTimeValue.isNull())) {
						isDelayed = actualDepartureDateTimeValue.getTimestamp() > scheduledDepartureTimeValue
								.getTimestamp() + delay;
					}
				}

				// updating actualScheduledArrivalDate
				if (!updatedScheduledDepartureDateValue.isNull()) {
					long updatedDepartureTimeInMillis = updatedScheduledDepartureDateValue
							.getTimestamp();
					updatedArrivalTimeValue
							.setTimestamp(scheduledFlyingTimeInMillis
									+ updatedDepartureTimeInMillis);
					graph.setAttribute(oid, updatedScheduledArrivalDate_attr,
							updatedArrivalTimeValue);

					if (!isDelayed && !scheduledArrivalTimeValue.isNull()) {
						isDelayed = updatedArrivalTimeValue.getTimestamp() > scheduledArrivalTimeValue
								.getTimestamp() + delay;
					}
				}
				isDelayedValue.setBoolean(isDelayed);
				graph.setAttribute(oid, isDelayed_attr, isDelayedValue);

			}
			flightPlan.close();

		}
		flights.close();
	}

}
