package darp.models;

import java.io.Serializable;

import org.jxmapviewer.viewer.GeoPosition;

/**
 * The class that represents the settings the user can adjust.
 * @author Daniel Schröder
 */
public class SettingsModel implements Serializable {

	private static final long serialVersionUID = 5683501663381840076L;

	private boolean offline, caching, boundsEnabled;
	private GeoPosition nw, se;
	private String osmPath, ghPath;

	/**
	 * Creates default settings.
	 */
	public SettingsModel() {
		offline = false;
		caching = false;
		boundsEnabled = false;

		nw = new GeoPosition(0, 0);
		se = new GeoPosition(0, 0);

		osmPath = "";
		ghPath = "";
	}

	/**
	 * @return <code> true </code> if offline mode is enabled
	 */
	public boolean isOffline() {
		return offline;
	}

	/**
	 * @param offline <code> true </code> if offline mode should be enabled
	 */
	public void setOffline(boolean offline) {
		this.offline = offline;
	}

	/**
	 * @return <code> true </code> if caching is enabled
	 */
	public boolean isCaching() {
		return caching;
	}

	/**
	 * @param caching <code> true </code> if caching should be enabled
	 */
	public void setCaching(boolean caching) {
		this.caching = caching;
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
	 * @return the path for the map files
	 */
	public String getOSMPath() {
		return osmPath;
	}

	/**
	 * @param osmPath the new path for the map files
	 */
	public void setOSMPath(String osmPath) {
		this.osmPath = osmPath;
	}

	/**
	 * @return the path for the pbf file
	 */
	public String getGHPath() {
		return ghPath;
	}

	/**
	 * @param gpPath the new path for the pbf file
	 */
	public void setGHPath(String gpPath) {
		this.ghPath = gpPath;
	}
}