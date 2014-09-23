package com.jsoto.planes.generator;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import com.qfs.planes.data.ICsvWritable;
import com.qfs.planes.data.impl.ACsvWritable;
import com.qfs.planes.data.impl.Flight;
import com.qfs.planes.data.impl.Itinerary;
import com.qfs.planes.data.impl.Passenger;
import com.qfs.planes.data.impl.Person;
import com.qfs.planes.data.impl.Plane;
import com.qfs.planes.util.PlanesUtil;

public class HubManagerUseCaseThree {

	@Test
	public void useCaseTwo() throws IOException, ParseException{

		String folder = "src/test/resources/hubManager/useCaseThree/";

		PlanesUtil.deleteFolder(new File(folder));

		// Flight
		List<ICsvWritable> csvWritable = new ArrayList<>();
		csvWritable.add(new Flight("flightId0", "planeId0", "Madrid", "Paris",     ACsvWritable.SDF.parse("2014-08-14 12:30"), ACsvWritable.SDF.parse("2014-08-14 12:30"), ACsvWritable.SDF.parse("2014-08-14 13:30"), ACsvWritable.SDF.parse("2014-08-14 13:00")));
		csvWritable.add(new Flight("flightId1", "planeId0", "Paris",  "Amsterdam", ACsvWritable.SDF.parse("2014-08-14 14:30"), ACsvWritable.SDF.parse("2014-08-14 14:30"), ACsvWritable.SDF.parse("2014-08-14 15:30"), ACsvWritable.SDF.parse("2014-08-14 15:35")));
		csvWritable.add(new Flight("flightId2", "planeId0", "Paris",  "London",    ACsvWritable.SDF.parse("2014-08-14 16:30"), ACsvWritable.SDF.parse("2014-08-14 16:30"), ACsvWritable.SDF.parse("2014-08-14 17:30"), ACsvWritable.SDF.parse("2014-08-14 17:30")));
		csvWritable.add(new Flight("flightId3", "planeId0", "Lisboa", "Paris",     ACsvWritable.SDF.parse("2014-08-14 12:30"), ACsvWritable.SDF.parse("2014-08-14 12:30"), ACsvWritable.SDF.parse("2014-08-14 13:30"), ACsvWritable.SDF.parse("2014-08-14 13:30")));

		// Plane
		csvWritable.add(new Plane("planeId0", 10, 5, 300,1500));

		int itineraryId = 0 ;
		int passengerId = 0;
		// Madrid - London
		for (int i = 0; i < 5; i++) {
			csvWritable.add(new Itinerary("itineraryId" + itineraryId++, "personId" + i, "Madrid", "London", Arrays.asList("flightId0", "flightId2")));
			csvWritable.add(new Passenger("passengerId" + passengerId++, "flightId0", "itineraryId" + i, 20, false));
			csvWritable.add(new Passenger("passengerId" + passengerId++, "flightId2", "itineraryId" + i, 20, false));
		}
		// Madrid - Amsterdam
		for (int i = 5; i < 10; i++) {
			csvWritable.add(new Itinerary("itineraryId" + itineraryId++, "personId" + i, "Madrid", "Amsterdam", Arrays.asList("flightId0", "flightId1")));
			csvWritable.add(new Passenger("passengerId" + passengerId++, "flightId0", "itineraryId" + i, 20, false));
			csvWritable.add(new Passenger("passengerId" + passengerId++, "flightId1", "itineraryId" + i, 20, false));
		}
		// Madrid Paris
		for (int i = 10; i < 15; i++) {
			csvWritable.add(new Itinerary("itineraryId" + itineraryId++, "personId" + i, "Madrid", "Paris", Arrays.asList("flightId0")));	
			csvWritable.add(new Passenger("passengerId" + passengerId++, "flightId0", "itineraryId" + i, 20, false));
		}
		
		// Paris Amsterdam
		for (int i = 15; i < 20; i++) {
			csvWritable.add(new Itinerary("itineraryId" + itineraryId++, "personId" + i, "Paris", "Amsterdam", Arrays.asList("flightId1")));	
			csvWritable.add(new Passenger("passengerId" + passengerId++, "flightId1", "itineraryId" + i, 20, false));
		}
		
		// Paris London
		for (int i = 20; i < 30; i++) {
			csvWritable.add(new Itinerary("itineraryId" + itineraryId++, "personId" + i, "Paris", "London", Arrays.asList("flightId2")));	
			csvWritable.add(new Passenger("passengerId" + passengerId++, "flightId2", "itineraryId" + i, 20, false));
		}
		
		// Lisboa Paris
		for (int i = 30; i < 40; i++) {
			csvWritable.add(new Itinerary("itineraryId" + itineraryId++, "personId" + i, "Lisboa", "Paris", Arrays.asList("flightId3")));	
			csvWritable.add(new Passenger("passengerId" + passengerId++, "flightId3", "itineraryId" + i, 20, false));
		}
		
		// Lisboa Amstedam
		for (int i = 40; i < 45; i++) {
			csvWritable.add(new Itinerary("itineraryId" + itineraryId++, "personId" + i, "Lisboa", "Amsterdam", Arrays.asList("flightId3", "flightId1") ));
			csvWritable.add(new Passenger("passengerId" + passengerId++, "flightId3", "itineraryId" + i, 20, false));
			csvWritable.add(new Passenger("passengerId" + passengerId++, "flightId1", "itineraryId" + i, 20, false));
		}

		for (int i = 0; i < 45; i++) {
			csvWritable.add(new Person("personId" + i, "John" + i, "Sullivan" + i));
		}

		PlanesUtil.write(csvWritable, folder);

	}

}
