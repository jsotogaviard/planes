package edu.upc.dama.planes.actions;

import java.io.File;
import java.io.FileReader;
import java.text.SimpleDateFormat;

import org.apache.log4j.Logger;

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

	private int flight_type;

	private int aircraftType_attr;

	private int scheduledDepartureTime_attr;

	private int scheduledArrivalDate_attr;

	private int updatedScheduledDepartureTime_attr;

	private int updatedScheduledArrivalDate_attr;

	private int actualScheduledDepartureTime_attr;

	private int actualScheduledArrivalDate_attr;

	private int id;

	private SimpleDateFormat complexDf;

	private Calendar cal;
	
	protected static final Logger LOG = Logger
			.getLogger(UpdateFlights.class);

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

	public void updateFlight(Value v, long oid, String[] line) throws Exception {
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

	public void updateDb() throws Exception {
		File file = new File(uploads);
		CSVReader reader = new CSVReader(new FileReader(file), ';', '\0', '\0',
				1, false);
		int updates = 0;
		int inserts = 0;
		try {
			String[] line = reader.readNext();

			complexDf = new SimpleDateFormat(
					"dd/MM/yyyy hh:mm");

			Value v = new Value();
			cal = Calendar.getInstance();
			
			while (line != null) {
				String flightNr = line[0];
				String date = line[2];

				String id_flight = flightNr + "_" + date;
				v.setString(id_flight);
				long oid = graph.findObject(id, v);
				if (oid != Objects.InvalidOID) {
					updateFlight(v, oid, line);
					updates++;
				}
				else{
					oid = graph.newNode(flight_type);
					v.setString(id_flight);
					graph.setAttribute(oid, id, v);
					updateFlight(v, oid, line);
					inserts++;
				}

				line = reader.readNext();
			}
		} finally {
			reader.close();
		}
		LOG.info("There were "+ updates+ " updates and "+inserts+ " inserts");
	}

	@Override
	public String execute() throws Exception {
		if (graph == null) {
			graph = DexUtil.getDBGraph();
		}

		flight_type = graph.findType("Flights");
		aircraftType_attr = graph.findAttribute(flight_type, "aircraftType");

		scheduledDepartureTime_attr = graph.findAttribute(flight_type,
				"scheduledDepartureDateTime");
		scheduledArrivalDate_attr = graph.findAttribute(flight_type,
				"scheduledArrivalDateTime");
		updatedScheduledDepartureTime_attr = graph.findAttribute(flight_type,
				"updatedScheduledDepartureTime");
		updatedScheduledArrivalDate_attr = graph.findAttribute(flight_type,
				"updatedScheduledArrivalTime");
		actualScheduledDepartureTime_attr = graph.findAttribute(flight_type,
				"actualDepartureDateTime");
		actualScheduledArrivalDate_attr = graph.findAttribute(flight_type,
				"actualArrivalDateTime");
		id = graph.findAttribute(flight_type, "id");

		updateDb();
		return Action.SUCCESS;
	}

}
