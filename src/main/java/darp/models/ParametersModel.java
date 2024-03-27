package darp.models;

import java.io.Serializable;

/**
 * The class that contains all the parameters of a test.
 * @author Daniel Schröder
 */
public class ParametersModel implements Serializable {

	private static final long serialVersionUID = -7613421855696447925L;

	private int capacity, mps, weight;
	private double timePref;
	private boolean routing;

	/**
	 * Creates default parameters.
	 */
	public ParametersModel() {
		capacity = 1;
		mps = 0;
		weight = 50;
		timePref = 1;
		routing = false;
	}

	/**
	 * @return the capacity of the vehicle
	 */
	public int getCapacity() {
		return capacity;
	}

	/**
	 * @param capacity the new capacity of the vehicle
	 */
	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}

	/**
	 * @return the maximum position shift
	 */
	public int getMPS() {
		return mps;
	}

	/**
	 * @param mps the new maximum position shift
	 */
	public void setMPS(int mps) {
		this.mps = mps;
	}

	/**
	 * @return the weighting
	 */
	public int getWeight() {
		return weight;
	}

	/**
	 * @param weight the new weighting
	 */
	public void setWeight(int weight) {
		this.weight = weight;
	}

	/**
	 * @return the time preference
	 */
	public double getTimePref() {
		return timePref;
	}

	/**
	 * @param timePref the new time preference
	 */
	public void setTimePref(double timePref) {
		this.timePref = timePref;
	}

	/**
	 * @return <code> true </code> if routing is enabled
	 */
	public boolean isRouting() {
		return routing;
	}

	/**
	 * @param routing <code> true </code> if routing should be enabled
	 */
	public void setRouting(boolean routing) {
		this.routing = routing;
	}
}