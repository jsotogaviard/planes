package com.jsoto.planes.graph;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Properties;
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
	
	public static final RelationshipType USES = DynamicRelationshipType.withName("Uses");
	public static final RelationshipType PASSENGES = DynamicRelationshipType.withName("Passenges");
	public static final RelationshipType HAS_ITINERARY = DynamicRelationshipType.withName("hasItinerary");
	
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
		
		UniqueFactory.UniqueNodeFactory peopleFactory = uniqueFactory(Person.NAME, Person.ID, graph);
		UniqueFactory.UniqueNodeFactory fligthFactory = uniqueFactory(Flight.NAME, Flight.ID, graph);
		UniqueFactory.UniqueNodeFactory itinerariesFactory = uniqueFactory(Itinerary.NAME, Itinerary.ID, graph);
		UniqueFactory.UniqueNodeFactory passengerFactory = 	uniqueFactory( Passenger.NAME, Passenger.ID, graph);
		UniqueFactory.UniqueNodeFactory planesFactory = uniqueFactory(Plane.NAME,  Plane.ID, graph);
		
		// Create graph
		try (Transaction tx = graph.beginTx()){
			
			loadNodeData(people,  	  Person.ID, 	Person.PROPERTIES, 	peopleFactory);
			loadNodeData(fligths, 	  Flight.ID, 	Flight.PROPERTIES, 	fligthFactory);
			loadNodeData(itineraries, Itinerary.ID, Itinerary.PROPERTIES, 	itinerariesFactory);
			loadNodeData(passengers,  Passenger.ID, Passenger.PROPERTIES, 	passengerFactory);
			loadNodeData(planes, 	  Plane.ID, 	Plane.PROPERTIES, 		planesFactory);
			
			for (Map<String, String> fligth : fligths) {
				String flightId = fligth.get(Flight.ID);
				Node fligthNode = fligthFactory.getOrCreate(Flight.ID, flightId);
				
				String planeId = fligth.get(Plane.ID);
				Node planeNode = planesFactory.getOrCreate(Plane.ID, planeId);
				fligthNode.createRelationshipTo(planeNode, USES);
			}
			
			for (Map<String, String> passenger : passengers) {
				String flightId = passenger.get(Flight.ID);
				Node fligthNode = fligthFactory.getOrCreate(Flight.ID, flightId);
				
				String passengerId = passenger.get(Passenger.ID);
				Node passengerNode = passengerFactory.getOrCreate(Passenger.ID, passengerId);
				fligthNode.createRelationshipTo(passengerNode, PASSENGES);
				
				String itineraryId = passenger.get(Itinerary.ID);
				Node itineraryNode = itinerariesFactory.getOrCreate(Itinerary.ID, itineraryId);
				passengerNode.createRelationshipTo(itineraryNode, HAS_ITINERARY);
			}
			
			for (Map<String, String> itinerary : itineraries) {
				String itineraryId = itinerary.get(Itinerary.ID);
				Node itineraryNode = itinerariesFactory.getOrCreate(Itinerary.ID, itineraryId);
				
				String personId = itinerary.get(Person.ID);
				Node personNode = peopleFactory.getOrCreate(Person.ID, personId);
				
				itineraryNode.createRelationshipTo(personNode, PASSENGES);
			}

			
			tx.success();
		}
		logger.info("Create graph " + (System.currentTimeMillis() - init));

		// Return the created graph
		return graph;


	}

	protected void loadNodeData(List<Map<String, String>> data, String id, String[] props, UniqueFactory<Node> factory) {
		for (Map<String, String> fligth : data) {
			final String fligthId = fligth.get(id);
			final Node n = factory.getOrCreate(id, fligthId);
			for (String prop : props) {
				n.setProperty(prop, fligth.get(prop));
			}
		}
		
	}

	protected UniqueNodeFactory uniqueFactory(final String node, final String nodeId, GraphDatabaseService graph) {
		final UniqueFactory.UniqueNodeFactory placeFactory;
		final Label label = DynamicLabel.label(node);
		try (Transaction tx = graph.beginTx()){
			placeFactory = new UniqueFactory.UniqueNodeFactory(graph, node){
		        @Override
		        protected void initialize(Node created, Map<String, Object> properties){
		            created.addLabel(label);
		            created.setProperty(nodeId, properties.get(nodeId));
		        }
		    };
		    tx.success();
		}
		return placeFactory;
	}
	
	public static void main(String[] args) throws IOException {
		Properties prop = new Properties();
		FileInputStream in = new FileInputStream(new File("src/main/resources/planes.properties"));
		prop.load(in);
		in.close();
		PlanesUtil.deleteFolder(new File(prop.getProperty("graph")));
		PlaneGraph graph = new PlaneGraph();
		GraphDatabaseService db = graph.createGraph(prop.getProperty("graph"), prop.getProperty("data"));
		db.shutdown();
	}

}

