package edu.upc.dama.planes.actions;

import java.io.File;
import java.io.FileReader;
import java.text.SimpleDateFormat;

import au.com.bytecode.opencsv.CSVReader;

import com.ibm.icu.util.Calendar;
import com.opensymphony.xwork2.Action;
import com.sparsity.sparksee.gdb.Graph;
import com.sparsity.sparksee.gdb.Objects;
import com.sparsity.sparksee.gdb.Value;

import edu.upc.dama.dex.preparers.GraphAware;
import edu.upc.dama.dex.utils.DexUtil;
import edu.upc.dama.struts2.dispatcher.ActionParameter;

public class UpdateFlights implements Action, GraphAware {

	private Graph graph;

	private String uploads;

	public String getUploads() {
		return uploads;
	}

	@ActionParameter
	public void setUploads(String uploads) {
		this.uploads = uploads;
	}

	@Override
	public void setGraph(Graph graph) {
		this.graph = graph;
	}

	@Override
	public Graph getGraph() {
		return graph;
	}

	public void updateDb() throws Exception {
		File file = new File(uploads);
		CSVReader reader = new CSVReader(new FileReader(file), ';', '\0', '\0',
				1, false);
		try {
			String[] line = reader.readNext();
			int flight_type = graph.findType("Flights");
			int aircraftType_attr = graph.findAttribute(flight_type,
					"aircraftType");
			int scheduledDepartureTime_attr = graph.findAttribute(flight_type,
					"scheduledDepartureTime");
			int scheduledArrivalDate_attr = graph.findAttribute(flight_type,
					"scheduledArrivalDate");
			int updatedScheduledDepartureTime_attr = graph.findAttribute(
					flight_type, "updatedScheduledDepartureTime");
			int updatedScheduledArrivalDate_attr = graph.findAttribute(
					flight_type, "updatedScheduledArrivalDate");
			int actualScheduledDepartureTime_attr = graph.findAttribute(
					flight_type, "actualScheduledDepartureTime");
			int actualScheduledArrivalDate_attr = graph.findAttribute(
					flight_type, "actualScheduledArrivalDate");
			int id = graph.findAttribute(flight_type, "id");

			SimpleDateFormat complexDf = new SimpleDateFormat(
					"dd/MM/yyyy hh:mm");

			Value v = new Value();
			Calendar cal = Calendar.getInstance();

			while (line != null) {
				String flightNr = line[0];
				String date = line[2];

				String id_flight = flightNr + "_" + date;
				v.setString(id_flight);
				long oid = graph.findObject(id, v);
				if (oid != Objects.InvalidOID) {
					String aircraftType = line[1];
					if ("".equals(aircraftType.trim())) {
						v.setNull();
					} else {
						v.setString(aircraftType);
					}
					int i = 3;
					graph.setAttribute(oid, aircraftType_attr, v);

					String scheduledDepartureDateTime = line[i++];
					if ("".equals(scheduledDepartureDateTime.trim())) {
						v.setNull();
					} else {
						cal.setTime(complexDf.parse(scheduledDepartureDateTime));
						v.setTimestamp(cal.getTimeInMillis());
					}

					graph.setAttribute(oid, scheduledDepartureTime_attr, v);

					String scheduledArrivalDateTime = line[i++];

					if ("".equals(scheduledArrivalDateTime.trim())) {
						v.setNull();
					} else {
						cal.setTime(complexDf.parse(scheduledArrivalDateTime));
						v.setTimestamp(cal.getTimeInMillis());
					}
					graph.setAttribute(oid, scheduledArrivalDate_attr, v);

					String updatedScheduledDepartureDateTime = line[i++];
					if ("".equals(updatedScheduledDepartureDateTime.trim())) {
						v.setNull();
					} else {
						cal.setTime(complexDf
								.parse(updatedScheduledDepartureDateTime));
						v.setTimestamp(cal.getTimeInMillis());
					}
					graph.setAttribute(oid, updatedScheduledDepartureTime_attr,
							v);

					String updatedScheduledArrivalDateTime = line[i++];
					if ("".equals(updatedScheduledArrivalDateTime.trim())) {
						v.setNull();
					} else {
						cal.setTime(complexDf
								.parse(updatedScheduledArrivalDateTime));
						v.setTimestamp(cal.getTimeInMillis());
					}
					graph.setAttribute(oid, updatedScheduledArrivalDate_attr, v);

					String actualDepartureDateTime = line[i++];

					if ("".equals(actualDepartureDateTime.trim())) {
						v.setNull();
					} else {
						cal.setTime(complexDf.parse(actualDepartureDateTime));
						v.setTimestamp(cal.getTimeInMillis());
					}

					graph.setAttribute(oid, actualScheduledDepartureTime_attr,
							v);

					String actualArrivalDateTime = line[i];

					if ("".equals(actualArrivalDateTime.trim())) {
						v.setNull();
					} else {
						cal.setTime(complexDf.parse(actualArrivalDateTime));
						v.setTimestamp(cal.getTimeInMillis());
					}

					graph.setAttribute(oid, actualScheduledArrivalDate_attr, v);
				}

				line = reader.readNext();
			}
		} finally {
			reader.close();
		}

	}

	@Override
	public String execute() throws Exception {
		if (graph == null) {
			graph = DexUtil.getDBGraph();
		}
		updateDb();
		return Action.SUCCESS;
	}

}
