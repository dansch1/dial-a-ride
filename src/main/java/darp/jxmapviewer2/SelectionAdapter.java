
package darp.jxmapviewer2;

import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.Objects;

import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.viewer.GeoPosition;

import darp.controllers.DarpController;

/**
 * Creates a selection rectangle based on mouse input.
 * Also triggers repaint events in the viewer.
 * @author Martin Steiger (slightly modified)
 */
public class SelectionAdapter extends MouseAdapter {

	private DarpController darpController;
	private JXMapViewer mapViewer;

	private boolean dragging;
	private boolean selecting;

	private Point2D startPos = new Point2D.Double();
	private Point2D endPos = new Point2D.Double();

	/**
	 * Creates a new selection adapter.
	 * @param darpController the DarpController
	 * @param mapViewer JXMapViewerBounded
	 * @throws NullPointerException if null is passed
	 */
	public SelectionAdapter(DarpController darpController, JXMapViewer mapViewer) throws NullPointerException {
		this.darpController = Objects.requireNonNull(darpController);
		this.mapViewer = Objects.requireNonNull(mapViewer);
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if (!selecting || e.getButton() != MouseEvent.BUTTON3) {
			return;
		}

		startPos.setLocation(e.getX(), e.getY());
		endPos.setLocation(e.getX(), e.getY());

		dragging = true;
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		if (!dragging) {
			return;
		}

		endPos.setLocation(e.getX(), e.getY());

		mapViewer.repaint();
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (!dragging) {
			return;
		}

		if (e.getButton() != MouseEvent.BUTTON3) {
			return;
		}

		// set the selected bounding box
		setBounds();

		mapViewer.repaint();

		dragging = false;
	}

	/**
	 * <New>
	 * Sets the selected area as the new bounding box.
	 */
	private void setBounds() {
		Rectangle selection = getRectangle();

		// get upper left and bottom right point
		Point2D min = new Point2D.Double(selection.getMinX(), selection.getMinY());
		Point2D max = new Point2D.Double(selection.getMaxX(), selection.getMaxY());

		// convert to GeoPositions
		GeoPosition nw = mapViewer.convertPointToGeoPosition(min);
		GeoPosition se = mapViewer.convertPointToGeoPosition(max);

		darpController.setBounds(nw, se);
	}

	/**
	 * @return the selection rectangle
	 */
	public Rectangle getRectangle() {
		if (dragging) {
			int x1 = (int) Math.min(startPos.getX(), endPos.getX());
			int y1 = (int) Math.min(startPos.getY(), endPos.getY());
			int x2 = (int) Math.max(startPos.getX(), endPos.getX());
			int y2 = (int) Math.max(startPos.getY(), endPos.getY());

			return new Rectangle(x1, y1, x2 - x1, y2 - y1);
		}

		return null;
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