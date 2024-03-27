package darp.views;

import java.awt.Color;

import javax.swing.JButton;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;

import darp.swing.JNumberField;
import darp.utils.WaypointNFTuple;

/**
 * The GUI representing a customer request of a test.
 * @author Daniel Schröder
 */
public class CustomerRequestView extends VehicleRequestView {

	private static final long serialVersionUID = -2077215028622484613L;

	private JNumberField numDestLat, numDestLon;
	private JButton btnSetDest, btnRemove;

	private WaypointNFTuple destTuple;

	/**
	 * Creates a new GUI representing a customer request of a test.
	 * @param label label of the request
	 * @throws NullPointerException if label is null
	 */
	public CustomerRequestView(String label) throws NullPointerException {
		// add components for the start position
		super(label);

		// add components for the destination position
		addDestPos();
		addRemoveButton();

		// helper class representing the destination position
		destTuple = new WaypointNFTuple(numDestLat, numDestLon, label + "Z");
	}

	@Override
	protected void addHeading() {
		JSeparator separator = new JSeparator();
		separator.setForeground(Color.LIGHT_GRAY);
		add(separator, "cell 0 0 7 1,growx");
	}

	/**
	 * Adds JNumberFields for the destination position
	 * and a button to set it.
	 */
	private void addDestPos() {
		btnSetDest = new JButton("Ausw.");
		add(btnSetDest, "cell 4 1 1 2,growx");

		numDestLat = new JNumberField("0", 10, SwingConstants.CENTER);
		add(numDestLat, "cell 5 1,growx");

		numDestLon = new JNumberField("0", 10, SwingConstants.CENTER);
		add(numDestLon, "cell 5 2,growx");
	}

	/**
	 * Adds the button to remove the request.
	 */
	private void addRemoveButton() {
		btnRemove = new JButton("-");
		add(btnRemove, "cell 6 1 1 2,growx");
	}

	/**
	 * @return the JNumberField containing the latitude of the destination position
	 */
	public JNumberField getNumDestLat() {
		return numDestLat;
	}

	/**
	 * @return the JNumberField containing the longitude of the destination position
	 */
	public JNumberField getNumDestLon() {
		return numDestLon;
	}

	/**
	 * @return the JButton that sets the destination position
	 */
	public JButton getBtnSetDest() {
		return btnSetDest;
	}

	/**
	 * @return the JButton to remove the request
	 */
	public JButton getBtnRemove() {
		return btnRemove;
	}

	/**
	 * @return the WaypointNFTuple representing the destination position
	 */
	public WaypointNFTuple getDestTuple() {
		return destTuple;
	}
}