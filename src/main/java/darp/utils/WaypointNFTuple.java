package darp.utils;

import java.awt.Color;
import java.util.Objects;

import org.jxmapviewer.viewer.GeoPosition;

import darp.jxmapviewer2.MyWaypoint;
import darp.swing.JNumberField;

/**
 * Combines two JNumberFields and a MyWaypoint to represent a GeoPosition.
 * @author Daniel Schröder
 */
public class WaypointNFTuple extends NumberFieldTuple {

	private MyWaypoint waypoint;

	/**
	 * Creates a new Tuple containing two JNumberFields,
	 * that represent a GeoPosition and a MyWaypoint.
	 * The color of the MyWaypoint is chosen at random.
	 * @param lat the JNumberField containing the latitude value of the GeoPosition
	 * @param lon the JNumberField containing the longitude value of the GeoPosition
	 * @param label the label of the MyWaypoint
	 * @throws NullPointerException if null is passed
	 */
	public WaypointNFTuple(JNumberField lat, JNumberField lon, String label) throws NullPointerException {
		this(lat, lon, label, Utilities.getRandomPleasingColor());
	}

	/**
	 * Creates a new Tuple containing two JNumberFields,
	 * that represent a GeoPosition and a MyWaypoint.
	 * @param lat the JNumberField containing the latitude value of the GeoPosition
	 * @param lon the JNumberField containing the longitude value of the GeoPosition
	 * @param label the label of the MyWaypoint
	 * @param color the color of the MyWaypoint
	 * @throws NullPointerException if null is passed
	 */
	public WaypointNFTuple(JNumberField lat, JNumberField lon, String label, Color color) throws NullPointerException {
		super(lat, lon);

		waypoint = new MyWaypoint(Objects.requireNonNull(label), Objects.requireNonNull(color), getGeoPos());
	}

	@Override
	public void setGeoPos(GeoPosition gp) throws NullPointerException {
		super.setGeoPos(gp);
		waypoint.setPosition(gp);
	}

	/**
	 * @return the MyWaypoint
	 */
	public MyWaypoint getWaypoint() {
		return waypoint;
	}

	/**
	 * @param waypoint the new MyWaypoint
	 * @throws NullPointerException if waypoint is null
	 */
	public void setWaypoint(MyWaypoint waypoint) throws NullPointerException {
		this.waypoint = Objects.requireNonNull(waypoint);
		updateNumberFieldsPos();
	}

	/**
	 * Updates the JNumberFields with the GeoPosition of the MyWaypoint
	 */
	public void updateNumberFieldsPos() {
		super.setGeoPos(waypoint.getPosition());
	}

	/**
	 * Updates the MyWaypoint GeoPostion with the values of the JNumberFields
	 */
	public void updateWaypointPos() {
		waypoint.setPosition(getGeoPos());
	}
}