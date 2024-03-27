package darp.algorithm;

import java.util.List;

import darp.jxmapviewer2.MyWaypoint;
import darp.models.TestModel;

/**
 * The interface for a darp solver.
 * @author Daniel Schröder
 */
public interface DarpSolver {

	/**
	 * Returns the sequence of waypoints of the optimal route for a given test.
	 * @param test the test
	 * @return the sequence
	 */
	public List<MyWaypoint> getOptRoute(TestModel test);

}