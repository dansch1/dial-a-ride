package darp.jxmapviewer2;

import java.awt.geom.Point2D;
import java.util.Objects;
import java.util.Set;

import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.viewer.GeoPosition;
import org.jxmapviewer.viewer.TileFactoryInfo;

import darp.utils.Utilities;

/**
 * A bounded JXMapViewer.
 * @author Daniel Schröder
 */
public class JXMapViewerBounded extends JXMapViewer {

	private static final long serialVersionUID = 238473036277954222L;

	private GeoPosition nw, se;
	private boolean boundsEnabled;

	/**
	 * Sets the bounding box of the map.
	 * @param nw the north west GeoPosition of the bounding box
	 * @param se the south east GeoPosition of the bounding box
	 * @throws NullPointerException if null is passed
	 */
	public void setBounds(GeoPosition nw, GeoPosition se) throws NullPointerException {
		this.nw = Objects.requireNonNull(nw);
		this.se = Objects.requireNonNull(se);
	}

	/**
	 * @param boundsEnabled <code> true </code> if bounds should be enabled
	 */
	public void setBoundsEnabled(boolean boundsEnabled) {
		this.boundsEnabled = boundsEnabled;
	}

	/**
	 * Sets the new center of the map in pixel coordinates.
	 * The center is moved inside the boundaries if necessary.
	 * @param center the new center of the map in pixel coordinates
	 * @throws NullPointerException if center is null
	 */
	public void setCenterInsideBounds(Point2D center) throws NullPointerException {
		setCenter(Objects.requireNonNull(center));
		fitCenterInBounds();
	}

	/**
	 * Set the current zoom level if there is enough space.
	 * Note: after zooming the center is moved inside the boundaries if necessary.
	 * @param zoom the new zoom level
	 */
	public void setZoomInsideBounds(int zoom) {
		if (!boundsEnabled || viewportInsideBounds(zoom)) {
			setZoom(zoom);
			fitCenterInBounds();
		}
	}

	/**
	 * Moves the center of the map inside the boundaries.
	 */
	public void fitCenterInBounds() {
		if (!boundsEnabled) {
			return;
		}

		Point2D min = convertGeoPositionToPoint(nw);
		Point2D max = convertGeoPositionToPoint(se);

		// calculates shift values
		int dx = (int) Utilities.clamp(0, min.getX(), max.getX() - getViewportBounds().width);
		int dy = (int) Utilities.clamp(0, min.getY(), max.getY() - getViewportBounds().height);

		setCenter(new Point2D.Double(getCenter().getX() + dx, getCenter().getY() + dy));
	}

	/**
	 * Zoom and center the map to a best fit around the input GeoPositions.
	 * Note: the zoom and center position may be adjusted if the viewport is not completely inside the boundaries.
	 * @param positions a set of GeoPositions to calculate the new zoom from
	 * @param maxFraction the maximum fraction of the viewport that should be covered
	 * @throws NullPointerException if positions is null
	 */
	public void zoomToBestFitInsideBounds(Set<GeoPosition> positions, double maxFraction) throws NullPointerException {
		zoomToBestFit(Objects.requireNonNull(positions), maxFraction);
		fitZoomInBounds();
	}

	/**
	 * Decreases the zoom level and adjusts the center position
	 * until the viewport is completely inside the boundaries
	 * or the minimum zoom level is reached.
	 */
	public void fitZoomInBounds() {
		if (!boundsEnabled) {
			return;
		}

		int zoomLevel = getZoom();
		while (!viewportInsideBounds(zoomLevel--));

		if (zoomLevel != getZoom()) {
			setZoom(zoomLevel);
		}

		fitCenterInBounds();
	}

	/**
	 * Checks whether the viewport is completely inside the boundaries.
	 * @param zoom the current zoom level
	 * @return <code> true </code> if the viewport is completely inside the boundaries
	 */
	private boolean viewportInsideBounds(int zoom) {
		TileFactoryInfo info = getTileFactory().getInfo();

		if (info == null || zoom < info.getMinimumZoomLevel() || zoom > info.getMaximumZoomLevel()) {
			return false;
		}

		Point2D min = getTileFactory().geoToPixel(nw, zoom);
		Point2D max = getTileFactory().geoToPixel(se, zoom);

		return max.getX() - min.getX() >= getViewportBounds().width && max.getY() - min.getY() >= getViewportBounds().height;
	}
}