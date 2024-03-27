package darp.utils;

import java.util.Objects;

import org.jxmapviewer.viewer.GeoPosition;

import com.graphhopper.GHRequest;
import com.graphhopper.GHResponse;
import com.graphhopper.GraphHopper;
import com.graphhopper.routing.util.EncodingManager;

import darp.jxmapviewer2.MyWaypoint;

/**
 * Easy to use access point for (offline) car routing.
 * @author Daniel Schröder
 */
public class Router {

	private GraphHopper graphHopper;
	private final String LOCATION_PATH = "src/main/resources/gh-car";

	/**
	 * Creates and initializes a GraphHopper instance for (offline) car routing.
	 * @param path the GraphHopper folder
	 * @throws NullPointerException if path is null
	 */
	public Router(String path) throws NullPointerException {
		if (path == null) {
			throw new NullPointerException("Path can't be null");
		}

		// create one GraphHopper instance
		graphHopper = new GraphHopper();

		// where to store GraphHopper files?
		graphHopper.setGraphHopperLocation(LOCATION_PATH);
		graphHopper.setEncodingManager(new EncodingManager("car"));
		graphHopper.setOSMFile(path);

		graphHopper.forServer();
		// this can take minutes if it imports or a few seconds for loading
		graphHopper.importOrLoad();
	}

	/**
	 * Returns the time (in millis) it takes from the start to
	 * the destination position with the car. If no route is found, the time
	 * will be {@link Double#MAX_VALUE}.
	 * @param start the start MyWaypoint
	 * @param dest the destination MyWaypoint
	 * @return the time (in millis) it takes from start to destination position
	 * @throws NullPointerException if null gets passed
	 */
	public double getTime(MyWaypoint start, MyWaypoint dest) throws NullPointerException {
		GHResponse route = getRoute(start, dest, false);
		return route.hasErrors() ? Double.MAX_VALUE : route.getBest().getTime();
	}

	/**
	 * Returns the path from the start to the destination position with the car,
	 * as well as the error output of GraphHopper.
	 * @param start the start MyWaypoint
	 * @param dest the destination MyWaypoint
	 * @return the response containing path and error output of GraphHopper
	 * @throws NullPointerException if null gets passed
	 */
	public GHResponse getRoute(MyWaypoint start, MyWaypoint dest) throws NullPointerException {
		return getRoute(start, dest, true);
	}

	/**
	 * Returns the path from the start to the destination position with the car,
	 * as well as the error output of GraphHopper.
	 * Note: points will only be calculated if specified.
	 * @param start the start MyWaypoint
	 * @param dest the destination MyWaypoint
	 * @param calcPoints boolean true if points should be calculated
	 * @return the response containing path and error output of GraphHopper
	 * @throws NullPointerException if null gets passed
	 */
	private GHResponse getRoute(MyWaypoint start, MyWaypoint dest, boolean calcPoints) throws NullPointerException {
		GeoPosition startPos = Objects.requireNonNull(start).getPosition();
		GeoPosition destPos = Objects.requireNonNull(dest).getPosition();

		GHRequest request = new GHRequest(startPos.getLatitude(), startPos.getLongitude(), destPos.getLatitude(), destPos.getLongitude());

		// configuration of the request object
		request.getHints().put("calcPoints", calcPoints);
		request.getHints().put("instructions", false);
		request.setWeighting("fastest");
		request.setVehicle("car");

		return graphHopper.route(request);
	}
}