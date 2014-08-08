package com.jsoto.planes.generator;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

import com.jsoto.planes.data.ICsvWritable;
import com.jsoto.planes.data.impl.Flight;
import com.jsoto.planes.data.impl.Itinerary;
import com.jsoto.planes.data.impl.Passenger;
import com.jsoto.planes.data.impl.Person;
import com.jsoto.planes.data.impl.Plane;
import com.jsoto.planes.util.PlanesUtil;

public class HubManagerUseCase {

	@Test
	public void useCaseOne(){

		String folder = "src/test/resources/hubManager/useCaseOne/";

		PlanesUtil.deleteFolder(new File(folder));

		// Flight
		List<ICsvWritable> csvWritable = new ArrayList<>();
		csvWritable.add(new Flight("flightId0", "planeId0", "Madrid", "Paris",     "2014-08-14 12:30", "2014-08-14 12:30", "2014-08-14 13:30", "2014-08-14 13:30"));
		csvWritable.add(new Flight("flightId1", "planeId0", "Paris",  "Amsterdam", "2014-08-14 14:30", "2014-08-14 14:30", "2014-08-14 15:30", "2014-08-14 15:30"));
		csvWritable.add(new Flight("flightId2", "planeId0", "Paris",  "London",    "2014-08-14 16:30", "2014-08-14 16:30", "2014-08-14 17:30", "2014-08-14 17:30"));

		// Plane
		csvWritable.add(new Plane("planeId0", 10, 5, 300));

		// Passenger
		int passengerId = 0;
		for (int i = 0; i < 15; i++) {
			csvWritable.add(new Passenger("passengerId" + passengerId++, "flightId0", "itineraryId" + i, 20));
		}
		for (int i = 15; i < 30; i++) {
			csvWritable.add(new Passenger("passengerId" + passengerId++, "flightId1", "itineraryId" + i, 20));
		}
		for (int i = 30; i < 45; i++) {
			csvWritable.add(new Passenger("passengerId" + passengerId++, "flightId2", "itineraryId" + i, 20));
		}

		// Itinerary
		int itineraryId = 0 ;
		for (int i = 0; i < 5; i++) {
			csvWritable.add(new Itinerary("itineraryId" + itineraryId++, "personId" + i, "Madrid", "London", Arrays.asList("Paris")));	
		}
		for (int i = 5; i < 10; i++) {
			csvWritable.add(new Itinerary("itineraryId" + itineraryId++, "personId" + i, "Madrid", "Amsterdam", Arrays.asList("Paris")));	
		}
		for (int i = 10; i < 15; i++) {
			csvWritable.add(new Itinerary("itineraryId" + itineraryId++, "personId" + i, "Madrid", "Paris", Collections.<String>emptyList()));	
		}
		for (int i = 15; i < 30; i++) {
			csvWritable.add(new Itinerary("itineraryId" + itineraryId++, "personId" + i, "Paris", "London", Collections.<String>emptyList()));	
		}
		for (int i = 30; i < 45; i++) {
			csvWritable.add(new Itinerary("itineraryId" + itineraryId++, "personId" + i, "Paris", "Amsterdam", Collections.<String>emptyList()));	
		}

		for (int i = 0; i < 45; i++) {
			csvWritable.add(new Person("personId" + i, "John", "Sullivan"));
		}

		PlanesUtil.write(csvWritable, folder);

	}

}
