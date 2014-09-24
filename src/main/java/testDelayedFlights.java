import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.junit.Test;

import edu.upc.dama.struts2.dispatcher.ShellActionDispatcher;


public class testDelayedFlights {

	@Test
	public void testDelayedFlights1ZeroUpdates() throws Exception {
		InputStream in = new ByteArrayInputStream("flights-manager\n3\n0\n".getBytes("UTF-8"));
		System.setIn(in );
		ShellActionDispatcher dispatcher = new ShellActionDispatcher();
		dispatcher.main(new String[]{});
	}
	
	@Test
	public void testDelayedFlightsZeroUpdates() throws Exception {
		InputStream in = new ByteArrayInputStream("flights-manager\n2\n0\n".getBytes("UTF-8"));
		System.setIn(in );
		ShellActionDispatcher dispatcher = new ShellActionDispatcher();
		dispatcher.main(new String[]{});
	}
	
	@Test
	public void testDelayedFlightsFirstUpdate() throws Exception {
		String s = "flights-manager\n"
				+ "1\n"
				+ "./src/test/resources/4_Update files/flights/flights 1630.csv\n"
				+ "y\n"
				+ "2\n"
				+ "0\n";
		
		InputStream in = new ByteArrayInputStream(s.getBytes("UTF-8"));
		System.setIn(in );
		ShellActionDispatcher dispatcher = new ShellActionDispatcher();
		dispatcher.main(new String[]{});
	}
}
