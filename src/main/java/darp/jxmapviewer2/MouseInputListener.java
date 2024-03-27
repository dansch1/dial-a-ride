package darp.jxmapviewer2;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.Point2D;
import java.util.Objects;

import javax.swing.SwingUtilities;
import javax.swing.event.MouseInputAdapter;

import darp.controllers.DarpController;

/**
 * Used to pan using press and drag mouse gestures.
 * @author joshy (modified)
 */
public class MouseInputListener extends MouseInputAdapter {

	private DarpController darpController;
	private JXMapViewerBounded mapViewer;

	private Point prev;
	private Cursor priorCursor;

	private boolean selecting;

	/**
	 * Creates a new mouse input listener.
	 * @param darpController the DarpController
	 * @param mapViewer the JXMapViewerBounded
	 * @throws NullPointerException if null is passed
	 */
	public MouseInputListener(DarpController darpController, JXMapViewerBounded mapViewer) throws NullPointerException {
		this.darpController = Objects.requireNonNull(darpController);
		this.mapViewer = Objects.requireNonNull(mapViewer);
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		mapViewer.setZoomInsideBounds(mapViewer.getZoom() + e.getWheelRotation());
	}

	@Override
	public void mousePressed(MouseEvent e) {
		requestFocus(e);

		// panning
		if (SwingUtilities.isLeftMouseButton(e) && mapViewer.isPanningEnabled()) {
			prev = e.getPoint();
			priorCursor = mapViewer.getCursor();
			mapViewer.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
		}

		// selecting
		if (SwingUtilities.isRightMouseButton(e) && selecting) {
			darpController.setGeoPosition(mapViewer.convertPointToGeoPosition(e.getPoint()));
		}
	}

	/**
	 * <New>
	 * Requests the input focus for the object on which the event initially occurred.
	 * @param e the MouseEvent
	 */
	private void requestFocus(MouseEvent e) {
		Component comp = (Component) e.getSource();

		if (comp != null) {
			comp.requestFocusInWindow();
		}
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		if (!SwingUtilities.isLeftMouseButton(e) || !mapViewer.isPanningEnabled()) {
			return;
		}

		Point current = e.getPoint();
		double x = mapViewer.getCenter().getX();
		double y = mapViewer.getCenter().getY();

		if (prev != null) {
			x += prev.x - current.x;
			y += prev.y - current.y;
		}

		int maxHeight = (int) (mapViewer.getTileFactory().getMapSize(mapViewer.getZoom()).getHeight() *
				mapViewer.getTileFactory().getTileSize(mapViewer.getZoom()));
		if (y > maxHeight) {
			y = maxHeight;
		}

		prev = current;
		mapViewer.setCenterInsideBounds(new Point2D.Double(x, y));
		mapViewer.repaint();
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (SwingUtilities.isLeftMouseButton(e)) {
			prev = null;
			mapViewer.setCursor(priorCursor);
		}
	}

	/**
	 * @return <code> true </code> if selecting is enabled
	 */
	public boolean isSelecting() {
		return selecting;
	}

	/**
	 * @param selecting <code> true </code> if selecting should be enabled
	 */
	public void setSelecting(boolean selecting) {
		this.selecting = selecting;
	}
}