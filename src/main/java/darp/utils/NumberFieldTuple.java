package darp.utils;

import java.util.Objects;

import org.jxmapviewer.viewer.GeoPosition;

import darp.swing.JNumberField;

/**
 * Combines two JNumberFields to represent a GeoPosition.
 * @author Daniel Schröder
 */
public class NumberFieldTuple {

	private JNumberField lat, lon;

	/**
	 * Creates a new tuple of JNumberFields that represent a GeoPosition.
	 * @param lat the JNumberField containing the latitude value of the GeoPosition
	 * @param lon the JNumberField containing the longitude value of the GeoPosition
	 * @throws NullPointerException if null is passed
	 */
	public NumberFieldTuple(JNumberField lat, JNumberField lon) throws NullPointerException {
		this.lat = Objects.requireNonNull(lat);
		this.lon = Objects.requireNonNull(lon);
	}

	/**
	 * @return the GeoPosition defined by the JNumberFields
	 */
	public GeoPosition getGeoPos() {
		return new GeoPosition(lat.getDoubleValue(), lon.getDoubleValue());
	}

	/**
	 * @param gp the GeoPosition that should be set by the JNumberFields
	 * @throws NullPointerException if gp is null
	 */
	public void setGeoPos(GeoPosition gp) throws NullPointerException {
		if (gp == null) {
			throw new NullPointerException("GeoPosition can't be null");
		}

		lat.setDoubleValue(gp.getLatitude());
		lon.setDoubleValue(gp.getLongitude());
	}
}