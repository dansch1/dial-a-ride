package darp.jxmapviewer2;

import java.awt.Color;
import java.io.Serializable;
import java.util.Objects;

import org.jxmapviewer.beans.AbstractBean;
import org.jxmapviewer.viewer.GeoPosition;
import org.jxmapviewer.viewer.Waypoint;

import darp.utils.Utilities;

/**
 * A waypoint that also has a color and a label.
 * @author Martin Steiger (modified)
 */
public class MyWaypoint extends AbstractBean implements Waypoint, Serializable {

	private static final long serialVersionUID = 8090258233974698676L;

	private String label;
	private Color color;
	private GeoPosition position;

	/**
	 * Creates a new waypoint at the specified GeoPosition, with the given label and a random color.
	 * @param label the text
	 * @param position the positions
	 * @throws NullPointerException if null is passed
	 */
	public MyWaypoint(String label, GeoPosition position) throws NullPointerException {
		this(label, Utilities.getRandomPleasingColor(), position);
	}

	/**
	 * Creates a new waypoint at the specified GeoPosition, with the given label and color.
	 * @param label the text
	 * @param color the color
	 * @param position the positions
	 * @throws NullPointerException if null is passed
	 */
	public MyWaypoint(String label, Color color, GeoPosition position) throws NullPointerException {
		this.label = Objects.requireNonNull(label);
		this.color = Objects.requireNonNull(color);
		this.position = Objects.requireNonNull(position);
	}

	/**
	 * @return the label text
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * @param newLabel the new label
	 */
	public void setLabel(String newLabel) {
		label = newLabel;
	}

	/**
	 * @return the color
	 */
	public Color getColor() {
		return color;
	}

	/**
	 * @param newColor the new color
	 * @throws NullPointerException if newColor is null
	 */
	public void setColor(Color newColor) throws NullPointerException {
		color = Objects.requireNonNull(newColor);
	}

	@Override
	public GeoPosition getPosition() {
		return position;
	}

	/**
	 * Set a new GeoPosition for this waypoint.
	 * @param position a new position
	 * @throws NullPointerException if position is null
	 */
	public void setPosition(GeoPosition position) throws NullPointerException {
		if (position == null) {
			throw new NullPointerException("GeoPosition can't be null");
		}

		GeoPosition old = getPosition();
		this.position = position;
		firePropertyChange("position", old, getPosition());
	}
}