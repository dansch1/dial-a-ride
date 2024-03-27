/*
 * WaypointMapOverlay.java
 * Created on April 1, 2006, 4:59 PM
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package darp.jxmapviewer2;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.painter.AbstractPainter;
import org.jxmapviewer.viewer.DefaultWaypointRenderer;
import org.jxmapviewer.viewer.Waypoint;
import org.jxmapviewer.viewer.WaypointRenderer;

/**
 * Paints waypoints on the JXMapViewer. This is an
 * instance of Painter that only can draw on to JXMapViewers.
 * @param <W> the waypoint type
 * @author rbair (slightly modified)
 */
public class WaypointPainter<W extends Waypoint> extends AbstractPainter<JXMapViewer> {

	private WaypointRenderer<? super W> renderer = new DefaultWaypointRenderer();
	private Set<W> waypoints = new HashSet<>();

	/**
	 * Creates a new instance of WaypointPainter.
	 */
	public WaypointPainter() {
		setAntialiasing(true);
		setCacheable(false);
	}

	/**
	 * Sets the waypoint renderer to use when painting waypoints.
	 * @param r the new WaypointRenderer to use
	 * @throws NullPointerException if r is null
	 */
	public void setRenderer(WaypointRenderer<W> r) throws NullPointerException {
		this.renderer = Objects.requireNonNull(r);
	}

	/**
	 * Gets the current set of waypoints to paint.
	 * @return a typed set of waypoints
	 */
	public Set<W> getWaypoints() {
		return Collections.unmodifiableSet(waypoints);
	}

	/**
	 * Sets the current set of waypoints to paint.
	 * @param waypoints the new set of waypoints to use
	 * @throws NullPointerException if waypoints is null
	 */
	public void setWaypoints(Set<? extends W> waypoints) throws NullPointerException {
		this.waypoints.clear();
		this.waypoints.addAll(Objects.requireNonNull(waypoints));
	}

	/**
	 * <New>
	 * Clears the current set of waypoints.
	 */
	public void clearWaypoints() {
		waypoints.clear();
	}

	/**
	 * <New>
	 * Adds a new waypoint to the current set.
	 * @param waypoint the waypoint to be added
	 * @throws NullPointerException if waypoint is null
	 */
	public void addWaypoint(W waypoint) throws NullPointerException {
		waypoints.add(Objects.requireNonNull(waypoint));
	}

	/**
	 * <New>
	 * Removes a waypoint from the current set.
	 * @param waypoint the waypoint to be removed
	 * @return <code> true </code> if waypoint was removed
	 */
	public boolean removeWaypoint(W waypoint) {
		return waypoints.remove(waypoint);
	}

	@Override
	protected void doPaint(Graphics2D g, JXMapViewer map, int width, int height) {
		if (renderer == null) {
			return;
		}

		Rectangle viewportBounds = map.getViewportBounds();

		g.translate(-viewportBounds.getX(), -viewportBounds.getY());

		for (W w : getWaypoints()) {
			renderer.paintWaypoint(g, map, w);
		}

		g.translate(viewportBounds.getX(), viewportBounds.getY());

	}
}