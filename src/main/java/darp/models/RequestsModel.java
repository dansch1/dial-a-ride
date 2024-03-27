package darp.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.jxmapviewer.viewer.GeoPosition;

import darp.jxmapviewer2.MyWaypoint;

/**
 * The class that contains all the requests, the selected bounding box and the name of the test.
 * @author Daniel Schröder
 */
public class RequestsModel implements Serializable {

	private static final long serialVersionUID = 5620016566259448406L;

	private String testName;
	private GeoPosition nw, se;
	private boolean boundsEnabled;
	private List<MyWaypoint> startWaypoints, destWaypoints;

	/**
	 * Creates default requests.
	 */
	public RequestsModel() {
		testName = "Testname";

		nw = new GeoPosition(0, 0);
		se = new GeoPosition(0, 0);

		boundsEnabled = false;

		// A test always consists of a starting position for the vehicle and at least one customer request
		MyWaypoint vStart = new MyWaypoint("AS", new GeoPosition(0, 0));
		MyWaypoint cStart = new MyWaypoint("K1S", new GeoPosition(0, 0));

		startWaypoints = new ArrayList<>();
		startWaypoints.add(vStart);
		startWaypoints.add(cStart);

		MyWaypoint cDest = new MyWaypoint("K1Z", new GeoPosition(0, 0));

		destWaypoints = new ArrayList<>();
		destWaypoints.add(cDest);
	}

	/**
	 * @return the name of the test
	 */
	public String getTestName() {
		return testName;
	}

	/**
	 * @param testName the new name of the test
	 */
	public void setTestName(String testName) {
		this.testName = testName;
	}

	/**
	 * @return the north west GeoPosition
	 */
	public GeoPosition getNorthWestPos() {
		return nw;
	}

	/**
	 * @param nw the new north west GeoPosition
	 */
	public void setNorthWestPos(GeoPosition nw) {
		this.nw = nw;
	}

	/**
	 * @return the south east GeoPosition
	 */
	public GeoPosition getSouthEastPos() {
		return se;
	}

	/**
	 * @param se the new south east GeoPosition
	 */
	public void setSouthEastPos(GeoPosition se) {
		this.se = se;
	}

	/**
	 * @return <code> true </code> if bounds are enabled
	 */
	public boolean isBoundsEnabled() {
		return boundsEnabled;
	}

	/**
	 * @param enabled <code> true </code> if bounds should be enabled
	 */
	public void setBoundsEnabled(boolean enabled) {
		this.boundsEnabled = enabled;
	}

	/**
	 * @return a list containing all the start MyWaypoints
	 */
	public List<MyWaypoint> getStartWaypoints() {
		return startWaypoints;
	}

	/**
	 * @param startWaypoints the new start MyWaypoints
	 */
	public void setStartWaypoints(List<MyWaypoint> startWaypoints) {
		this.startWaypoints = startWaypoints;
	}

	/**
	 * @return a list containing all the destination MyWaypoints
	 */
	public List<MyWaypoint> getDestWaypoints() {
		return destWaypoints;
	}

	/**
	 * @param destWaypoints the new destination MyWaypoints
	 */
	public void setDestWaypoints(List<MyWaypoint> destWaypoints) {
		this.destWaypoints = destWaypoints;
	}
}