package darp.jxmapviewer2;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.painter.Painter;
import org.jxmapviewer.viewer.GeoPosition;
import org.jxmapviewer.viewer.TileFactory;

import darp.utils.RouteSegment;

/**
 * Paints a route.
 * @author Martin Steiger (heavily modified)
 */
public class RoutePainter implements Painter<JXMapViewer> {

	private Color color = Color.RED;
	private boolean antiAlias = true;

	private List<RouteSegment> route = new ArrayList<>();
	private int stopAt;

	private Bounds bounds;

	/**
	 * @param route the new route
	 * @throws NullPointerException if route is null
	 */
	public void setRoute(List<RouteSegment> route) throws NullPointerException {
		// copy the list so that changes in the
		// original list do not have an effect here
		this.route = new ArrayList<>(Objects.requireNonNull(route));
	}

	/**
	 * <New>
	 * Clears the current route.
	 */
	public void clearRoute() {
		route.clear();
		stopAt = 0;
	}

	/**
	 * <New>
	 * Sets the index of the first segment that should no longer be rendered.
	 * @param stopAt the index of the segment
	 */
	public void stopAt(int stopAt) {
		if (stopAt >= 0 && stopAt <= route.size()) {
			this.stopAt = stopAt;
		}
	}

	/**
	 * <New>
	 * Sets the bounding box that should be rendered.
	 * @param nw the north west GeoPosition of the bounding box
	 * @param se the south east GeoPosition of the bounding box
	 * @throws NullPointerException if null is passed
	 */
	public void setBounds(GeoPosition nw, GeoPosition se) throws NullPointerException {
		bounds = new Bounds(nw, se);
	}

	/**
	 * <New>
	 * Remove the current bounding box.
	 */
	public void removeBounds() {
		bounds = null;
	}

	@Override
	public void paint(Graphics2D g, JXMapViewer map, int w, int h) {
		g = (Graphics2D) g.create();

		// convert from viewport to world bitmap
		Rectangle rect = map.getViewportBounds();
		g.translate(-rect.x, -rect.y);

		if (antiAlias) {
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		}

		// do the drawing
		drawBoundingBox(g, map);

		for (int i = 0; i < stopAt; i++) {
			drawRouteSegment(g, map, route.get(i));
		}

		g.dispose();
	}

	/**
	 * <New>
	 * Draws the current bounding box.
	 * @param g the graphics object
	 * @param map the map
	 */
	private void drawBoundingBox(Graphics2D g, JXMapViewer map) {
		if (bounds == null) {
			return;
		}

		Rectangle boundingBox = bounds.getBoundingBox(map);

		// draw the outline
		g.setColor(Color.BLACK);
		g.setStroke(new BasicStroke(10));
		g.draw(boundingBox);

		// draw the bounding box
		g.setColor(color);
		g.setStroke(new BasicStroke(6));
		g.draw(boundingBox);
	}

	/**
	 * <New>
	 * Draws the given route segment.
	 * @param g the graphics object
	 * @param map the map
	 * @param rs the route segment
	 */
	private void drawRouteSegment(Graphics2D g, JXMapViewer map, RouteSegment rs) {
		int lastX = 0;
		int lastY = 0;

		boolean first = true;

		for (GeoPosition gp : rs.getPositions()) {
			// convert geo-coordinate to world bitmap pixel
			Point2D pt = map.getTileFactory().geoToPixel(gp, map.getZoom());

			int x = (int) pt.getX();
			int y = (int) pt.getY();

			if (first) {
				first = false;
			} else {
				// draw the outline
				g.setColor(Color.BLACK);
				g.setStroke(new BasicStroke(8));
				g.drawLine(lastX, lastY, x, y);

				// draw the line
				g.setColor(rs.getColor());
				g.setStroke(new BasicStroke(4));
				g.drawLine(lastX, lastY, x, y);
			}

			lastX = x;
			lastY = y;
		}
	}
}

/**
 * <New>
 * Class representing a bounding box.
 * @author Daniel Schröder
 */
class Bounds {

	private GeoPosition nw, se;

	/**
	 * Creates a new bounding box.
	 * @param nw the north west GeoPosition of the bounding box
	 * @param se the south east GeoPosition of the bounding box
	 * @throws NullPointerException if null is passed
	 */
	public Bounds(GeoPosition nw, GeoPosition se) throws NullPointerException {
		this.nw = Objects.requireNonNull(nw);
		this.se = Objects.requireNonNull(se);
	}

	/**
	 * Constructs the bounding box at the correct zoom level.
	 * @param map the map
	 * @return the bounding box
	 * @throws NullPointerException if map is null
	 */
	public Rectangle getBoundingBox(JXMapViewer map) throws NullPointerException {
		if (map == null) {
			throw new NullPointerException("JXMapViewer can't be null");
		}

		TileFactory factory = map.getTileFactory();
		int zoom = map.getZoom();

		// get upper left and bottom right point
		Point2D min = factory.geoToPixel(nw, zoom);
		Point2D max = factory.geoToPixel(se, zoom);

		// construct bounding box
		Rectangle boundingBox = new Rectangle();
		boundingBox.setFrameFromDiagonal(min, max);

		return boundingBox;
	}
}