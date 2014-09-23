
package com.qfs.sparksee;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;

import com.qfs.planes.data.impl.Flight;
import com.qfs.planes.data.impl.Itinerary;
import com.qfs.planes.data.impl.Passenger;
import com.qfs.planes.data.impl.Person;
import com.qfs.planes.data.impl.Plane;
import com.qfs.planes.util.PlanesUtil;
import com.sparsity.sparksee.gdb.AttributeKind;
import com.sparsity.sparksee.gdb.DataType;
import com.sparsity.sparksee.gdb.Database;
import com.sparsity.sparksee.gdb.EdgesDirection;
import com.sparsity.sparksee.gdb.Graph;
import com.sparsity.sparksee.gdb.Objects;
import com.sparsity.sparksee.gdb.ObjectsIterator;
import com.sparsity.sparksee.gdb.Session;
import com.sparsity.sparksee.gdb.Sparksee;
import com.sparsity.sparksee.gdb.SparkseeConfig;
import com.sparsity.sparksee.gdb.Value;

public class SpaskseeServer {
	
	/** the logger **/
	private static Logger logger = Logger.getLogger(SpaskseeServer.class.getName());
	
	public void load(String data) throws FileNotFoundException {
		SparkseeConfig cfg = new SparkseeConfig();
		Sparksee sparksee = new Sparksee(cfg);
		Database db = sparksee.create("target/HelloSparksee.gdb", "HelloSparksee");
		Session sess = db.newSession();
		Graph g = sess.getGraph();
		
		long init = System.currentTimeMillis();
		logger.info("Database creation " + (System.currentTimeMillis() - init));
		
		// Load operations
		init = System.currentTimeMillis();
		
		List<Map<String, Object>> flightPlans = 	PlanesUtil.loadFile(data + Person.class.getSimpleName());
		List<Map<String, Object>> scheduledFlights = PlanesUtil.loadFile(data + Flight.class.getSimpleName());
		List<Map<String, Object>> airplanes = PlanesUtil.loadFile(data + Itinerary.class.getSimpleName());
		List<Map<String, Object>> passengerItineraries = 	PlanesUtil.loadFile(data + Passenger.class.getSimpleName());
		List<Map<String, Object>> passengerLegs = 	PlanesUtil.loadFile(data + Plane.class.getSimpleName());
		List<Map<String, Object>> customerSupport = 	PlanesUtil.loadFile(data + Plane.class.getSimpleName());
		List<Map<String, Object>> cities = 	PlanesUtil.loadFile(data + Plane.class.getSimpleName());
		
		logger.info(" Load operations " + (System.currentTimeMillis() - init));
		
		// Add a node type for the movies, with a unique identifier and two indexed attributes
		int movieType = g.newNodeType("MOVIE");
		int movieIdType = g.newAttribute(movieType, "ID", DataType.Long, AttributeKind.Unique);
		int movieTitleType = g.newAttribute(movieType, "TITLE", DataType.String, AttributeKind.Indexed);
		int movieYearType = g.newAttribute(movieType, "YEAR", DataType.Integer, AttributeKind.Indexed);

		// Add a node type for the people, with a unique identifier and an indexed attribute
		int peopleType = g.newNodeType("PEOPLE");
		int peopleIdType = g.newAttribute(peopleType, "ID", DataType.Long, AttributeKind.Unique);
		int peopleNameType = g.newAttribute(peopleType, "NAME", DataType.String, AttributeKind.Indexed);	// TODO Auto-generated method stub

		// Add an undirected edge type with an attribute for the cast of a movie
		int castType = g.newEdgeType("CAST", false, false);
		int castCharacterType = g.newAttribute(castType, "CHARACTER", DataType.String, AttributeKind.Basic);

		// Add a directed edge type restricted to go from people to movie for the director of a movie
		int directsType = g.newRestrictedEdgeType("DIRECTS", peopleType, movieType, false);


		// 
		// DATA
		//

		// Add some MOVIE nodes
		Value value = new Value();

		long mLostInTranslation = g.newNode(movieType);
		g.setAttribute(mLostInTranslation, movieIdType, value.setLong(1));
		g.setAttribute(mLostInTranslation, movieTitleType, value.setString("Lost in Translation"));
		g.setAttribute(mLostInTranslation, movieYearType, value.setInteger(2003));

		long mVickyCB = g.newNode(movieType);
		g.setAttribute(mVickyCB, movieIdType, value.setLong(2));
		g.setAttribute(mVickyCB, movieTitleType, value.setString("Vicky Cristina Barcelona"));
		g.setAttribute(mVickyCB, movieYearType, value.setInteger(2008));

		long mManhattan = g.newNode(movieType);
		g.setAttribute(mManhattan, movieIdType, value.setLong(3));
		g.setAttribute(mManhattan, movieTitleType, value.setString("Manhattan"));
		g.setAttribute(mManhattan, movieYearType, value.setInteger(1979));


		// Add some PEOPLE nodes
		long pScarlett = g.newNode(peopleType);
		g.setAttribute(pScarlett, peopleIdType, value.setLong(1));
		g.setAttribute(pScarlett, peopleNameType, value.setString("Scarlett Johansson"));

		long pBill = g.newNode(peopleType);
		g.setAttribute(pBill, peopleIdType, value.setLong(2));
		g.setAttribute(pBill, peopleNameType, value.setString("Bill Murray"));

		long pSofia = g.newNode(peopleType);
		g.setAttribute(pSofia, peopleIdType, value.setLong(3));
		g.setAttribute(pSofia, peopleNameType, value.setString("Sofia Coppola"));

		long pWoody = g.newNode(peopleType);
		g.setAttribute(pWoody, peopleIdType, value.setLong(4));
		g.setAttribute(pWoody, peopleNameType, value.setString("Woody Allen"));

		long pPenelope = g.newNode(peopleType);
		g.setAttribute(pPenelope, peopleIdType, value.setLong(5));
		g.setAttribute(pPenelope, peopleNameType, value.setString("PenÃ©lope Cruz"));

		long pDiane = g.newNode(peopleType);
		g.setAttribute(pDiane, peopleIdType, value.setLong(6));
		g.setAttribute(pDiane, peopleNameType, value.setString("Diane Keaton"));



		// Add some CAST edges
		long anEdge;
		anEdge = g.newEdge(castType, mLostInTranslation, pScarlett);
//		g.setAttribute(anEdge, castCharacterType, value.setString("Charlotte"));

		anEdge = g.newEdge(castType, mLostInTranslation, pBill);
		g.setAttribute(anEdge, castCharacterType, value.setString("Bob Harris"));

		anEdge = g.newEdge(castType, mVickyCB, pScarlett);
		g.setAttribute(anEdge, castCharacterType, value.setString("Cristina"));

		anEdge = g.newEdge(castType, mVickyCB, pPenelope);
		g.setAttribute(anEdge, castCharacterType, value.setString("Maria Elena"));

		anEdge = g.newEdge(castType, mManhattan, pDiane);
		g.setAttribute(anEdge, castCharacterType, value.setString("Mary"));

		anEdge = g.newEdge(castType, mManhattan, pWoody);
		g.setAttribute(anEdge, castCharacterType, value.setString("Isaac"));



		// Add some DIRECTS edges
		anEdge = g.newEdge(directsType, pSofia, mLostInTranslation);

		anEdge = g.newEdge(directsType, pWoody, mVickyCB);

		anEdge = g.newEdge(directsType, pWoody, mManhattan);



		// 
		// QUERIES
		//

		// Get the movies directed by Woody Allen
		Objects directedByWoody = g.neighbors(pWoody, directsType, EdgesDirection.Outgoing);

		// Get the cast of the movies directed by Woody Allen
		Objects castDirectedByWoody = g.neighbors(directedByWoody, castType, EdgesDirection.Any);

		// We don't need the directedByWoody collection anymore, so we should close it
		directedByWoody.close();


		// Get the movies directed by Sofia Coppola
		Objects directedBySofia = g.neighbors(pSofia, directsType, EdgesDirection.Outgoing);

		// Get the cast of the movies directed by Sofia Coppola
		Objects castDirectedBySofia = g.neighbors(directedBySofia, castType, EdgesDirection.Any);

		// We don't need the directedBySofia collection anymore, so we should close it
		directedBySofia.close();


		// We want to know the people that acted in movies directed by Woody AND in movies directed by Sofia.
		Objects castFromBoth = Objects.combineIntersection(castDirectedByWoody, castDirectedBySofia);

		// We don't need the other collections anymore
		castDirectedByWoody.close();
		castDirectedBySofia.close();

		// Say hello to the people found
		ObjectsIterator it = castFromBoth.iterator();
		while (it.hasNext())
		{
			long peopleOid = it.next();
			g.getAttribute(peopleOid, peopleNameType, value);
			System.out.println("Hello " + value.getString());
		}
		// The ObjectsIterator must be closed
		it.close();

		// The Objects must be closed
		castFromBoth.close();


		//
		// Close the database
		//
		sess.close();
		db.close();
		sparksee.close();


	}

	private void loadNodeData(Graph g, List<Map<String, Object>> flightPlans, String nodeTypeName) {
		for (Map<String, Object> flightPlan : flightPlans) {
			int nodeType = g.newNodeType(nodeTypeName);
			int idx = 0;
			for (Entry<String, Object> entry : flightPlan.entrySet()) {
				String field = entry.getKey();
				Object value = entry.getValue();
				if (idx == 0 ) {
					 g.newAttribute(nodeType, "ID", DataType.Long, AttributeKind.Unique);
				}
			}
			int movieIdType = g.newAttribute(nodeType, "ID", DataType.Long, AttributeKind.Unique);
			int movieTitleType = g.newAttribute(nodeType, "TITLE", DataType.String, AttributeKind.Indexed);
			int movieYearType = g.newAttribute(nodeType, "YEAR", DataType.Integer, AttributeKind.Indexed);
			
		}
		
	}


}

