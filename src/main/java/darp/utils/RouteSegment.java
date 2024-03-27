package darp.utils;

import java.awt.Color;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.jxmapviewer.viewer.GeoPosition;

/**
 * Class that represents a segment in a Route.
 * A Segment contains a list of positions and a color.
 * @author Daniel Schröder
 */
public class RouteSegment implements Serializable {

	private static final long serialVersionUID = 8230640713742223825L;

	private List<GeoPosition> positions;
	private Color color;

	/**
	 * Creates a new route segment.
	 * @param positions the positions of the segment
	 * @param color the color of the segment
	 * @throws NullPointerException if null is passed
	 */
	public RouteSegment(List<GeoPosition> positions, Color color) throws NullPointerException {
		this.positions = Objects.requireNonNull(positions);
		this.color = Objects.requireNonNull(color);
	}

	/**
	 * @return the positions of the segment
	 */
	public List<GeoPosition> getPositions() {
		return Collections.unmodifiableList(positions);
	}

	/**
	 * @return the color of the segment
	 */
	public Color getColor() {
		return color;
	}
}