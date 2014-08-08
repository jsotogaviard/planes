package com.jsoto.planes.graph;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.DynamicRelationshipType;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.index.UniqueFactory;
import org.neo4j.graphdb.index.UniqueFactory.UniqueNodeFactory;

import com.jsoto.planes.data.impl.Flight;
import com.jsoto.planes.data.impl.Itinerary;
import com.jsoto.planes.data.impl.Passenger;
import com.jsoto.planes.data.impl.Person;
import com.jsoto.planes.data.impl.Plane;
import com.jsoto.planes.util.PlanesUtil;

public class PlaneGraph {
	
	/** the logger **/
	private static Logger logger = Logger.getLogger(PlaneGraph.class.getName());
	
	public static final String PEOPLE = Person.class.getSimpleName();
	public static final String FLIGHT = Flight.class.getSimpleName();
	public static final String ITINERARIES = Itinerary.class.getSimpleName();
	public static final String PASSENGER = Passenger.class.getSimpleName();
	public static final String PLANES = Plane.class.getSimpleName();
	
	public static final Label PEOPLE_LABEL = DynamicLabel.label(PEOPLE);
	public static final Label FLIGTH_LABEL = DynamicLabel.label(FLIGHT);
	public static final Label ITINERARIES_LABEL = DynamicLabel.label(ITINERARIES);
	public static final Label PASSENGER_LABEL = DynamicLabel.label(PASSENGER);
	public static final Label PLANES_LABEL = DynamicLabel.label(PLANES);

	private static final RelationshipType USES = DynamicRelationshipType.withName("Uses");
	private static final RelationshipType PASSENGES = DynamicRelationshipType.withName("Passenges");
	private static final RelationshipType HAS_ITINERARY = DynamicRelationshipType.withName("hasItinerary");
	
	public GraphDatabaseService createGraph(String graphPath, String data) {
		
		// Create graph
		long init = System.currentTimeMillis();
		GraphDatabaseService graph = new GraphDatabaseFactory()
		.newEmbeddedDatabaseBuilder(graphPath)
		.newGraphDatabase();
		
		logger.info("Database creation " + (System.currentTimeMillis() - init));
		
		// Load operations
		init = System.currentTimeMillis();
		
		List<Map<String, String>> people = 	PlanesUtil.loadFile(data + Person.class.getSimpleName());
		List<Map<String, String>> fligths = PlanesUtil.loadFile(data + Flight.class.getSimpleName());
		List<Map<String, String>> itineraries = PlanesUtil.loadFile(data + Itinerary.class.getSimpleName());
		List<Map<String, String>> passengers = 	PlanesUtil.loadFile(data + Passenger.class.getSimpleName());
		List<Map<String, String>> planes = 	PlanesUtil.loadFile(data + Plane.class.getSimpleName());
		
		logger.info(" Load operations " + (System.currentTimeMillis() - init));
		
		UniqueFactory.UniqueNodeFactory peopleFactory = uniqueFactory(PEOPLE, graph);
		UniqueFactory.UniqueNodeFactory fligthFactory = uniqueFactory(FLIGHT, graph);
		UniqueFactory.UniqueNodeFactory itinerariesFactory = uniqueFactory(ITINERARIES, graph);
		UniqueFactory.UniqueNodeFactory passengerFactory = 	uniqueFactory(PASSENGER, graph);
		UniqueFactory.UniqueNodeFactory planesFactory = uniqueFactory(PLANES, graph);
		
		// Create graph
		try (Transaction tx = graph.beginTx()){
			
			loadNodeData(people,  	  Person.ID, 	PEOPLE, 	 Person.PROPERTIES, 	peopleFactory);
			loadNodeData(fligths, 	  Flight.ID, 	FLIGHT, 	 Flight.PROPERTIES, 	fligthFactory);
			loadNodeData(itineraries, Itinerary.ID, ITINERARIES, Itinerary.PROPERTIES, 	itinerariesFactory);
			loadNodeData(passengers,  Passenger.ID, PASSENGER, 	 Passenger.PROPERTIES, 	passengerFactory);
			loadNodeData(planes, 	  Plane.ID, 	PLANES, 	 Plane.PROPERTIES, 		planesFactory);
			
			for (Map<String, String> fligth : fligths) {
				String flightId = fligth.get(Flight.ID);
				Node fligthNode = fligthFactory.getOrCreate(Flight.ID, flightId);
				
				String planeId = fligth.get(Plane.ID);
				Node planeNode = fligthFactory.getOrCreate(Plane.ID, planeId);
				fligthNode.createRelationshipTo(planeNode, USES);
			}
			
			for (Map<String, String> passenger : passengers) {
				String flightId = passenger.get(Flight.ID);
				Node fligthNode = fligthFactory.getOrCreate(Flight.ID, flightId);
				
				String passengerId = passenger.get(Passenger.ID);
				Node passengerNode = passengerFactory.getOrCreate(Passenger.ID, passengerId);
				fligthNode.createRelationshipTo(passengerNode, PASSENGES);
				
				String itineraryId = passenger.get(Itinerary.ID);
				Node itineraryNode = fligthFactory.getOrCreate(Itinerary.ID, itineraryId);
				passengerNode.createRelationshipTo(itineraryNode, HAS_ITINERARY);
			}
			
			for (Map<String, String> itinerary : itineraries) {
				String itineraryId = itinerary.get(Itinerary.ID);
				Node itineraryNode = itinerariesFactory.getOrCreate(Itinerary.ID, itineraryId);
				
				String personId = itinerary.get(Person.ID);
				Node personNode = passengerFactory.getOrCreate(Person.ID, personId);
				
				itineraryNode.createRelationshipTo(personNode, PASSENGES);
			}

			
			tx.success();
		}
		logger.info("Create graph " + (System.currentTimeMillis() - init));

		// Return the created graph
		return graph;


	}

	protected void loadNodeData(List<Map<String, String>> data, String id, String node, String[] props, UniqueFactory<Node> factory) {
		for (Map<String, String> fligth : data) {
			final String fligthId = fligth.get(id);
			final Node n = factory.getOrCreate(node, fligthId);
			for (String prop : props) {
				n.setProperty(prop, fligth.get(prop));
			}
		}
		
	}

	protected UniqueNodeFactory uniqueFactory(final String node, GraphDatabaseService graph) {
		final UniqueFactory.UniqueNodeFactory placeFactory;
		final Label label = DynamicLabel.label(node);
		try (Transaction tx = graph.beginTx()){
			placeFactory = new UniqueFactory.UniqueNodeFactory(graph, node){
		        @Override
		        protected void initialize(Node created, Map<String, Object> properties){
		            created.addLabel(label);
		            created.setProperty(node, properties.get(node));
		        }
		    };
		    tx.success();
		}
		return placeFactory;
	}
	
	public static void main(String[] args) {
		
	}

}

