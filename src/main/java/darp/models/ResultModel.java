package darp.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import darp.utils.RouteSegment;

/**
 * The class that contains the result of a test.
 * @author Daniel Schröder
 */
public class ResultModel implements Serializable {

	private static final long serialVersionUID = 5995101335529966957L;

	private List<RouteSegment> route;
	private long totalTime, routingTime, algoTime;
	private int frame;

	/**
	 * Creates a default result.
	 */
	public ResultModel() {
		route = new ArrayList<>();
		totalTime = 0;
		routingTime = 0;
		algoTime = 0;
		frame = 0;
	}

	/**
	 * @return the route
	 */
	public List<RouteSegment> getRoute() {
		return route;
	}

	/**
	 * @param route the new route
	 */
	public void setRoute(List<RouteSegment> route) {
		this.route = route;
	}

	/**
	 * @return the total time
	 */
	public long getTotalTime() {
		return totalTime;
	}

	/**
	 * @param totalTime the new total time
	 */
	public void setTotalTime(long totalTime) {
		this.totalTime = totalTime;
	}

	/**
	 * @return the routing time
	 */
	public long getRoutingTime() {
		return routingTime;
	}

	/**
	 * @param routingTime the new routing time
	 */
	public void setRoutingTime(long routingTime) {
		this.routingTime = routingTime;
	}

	/**
	 * @return the algorithm time
	 */
	public long getAlgoTime() {
		return algoTime;
	}

	/**
	 * @param algoTime the new algorithm time
	 */
	public void setAlgoTime(long algoTime) {
		this.algoTime = algoTime;
	}

	/**
	 * @return the current frame
	 */
	public int getFrame() {
		return frame;
	}

	/**
	 * @param frame the new current frame
	 */
	public void setFrame(int frame) {
		this.frame = frame;
	}
}