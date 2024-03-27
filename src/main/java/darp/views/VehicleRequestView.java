package darp.views;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import darp.swing.JNumberField;
import darp.utils.WaypointNFTuple;
import net.miginfocom.swing.MigLayout;

/**
 * The GUI representing the vehicle request of a test.
 * @author Daniel Schröder
 */
public class VehicleRequestView extends JPanel {

	private static final long serialVersionUID = 1707827217507311031L;

	protected JLabel lblLabel;
	protected JNumberField numStartLat, numStartLon;
	protected JButton btnSetStart;

	protected WaypointNFTuple startTuple;

	/**
	 * Creates a new GUI representing the vehicle request of a test.
	 * @param label the label for the request
	 * @throws NullPointerException if label is null
	 */
	public VehicleRequestView(String label) throws NullPointerException {
		if (label == null) {
			throw new NullPointerException("Label can't be null");
		}

		setLayout(new MigLayout("insets 0", "[40px,grow][80px,grow][70px,grow][20px,grow][70px,grow][80px,grow][40px,grow]", "[grow][grow][grow]"));

		// add components
		addHeading();
		addLabel(label);
		addStartPos();

		// helper class representing the start position
		startTuple = new WaypointNFTuple(numStartLat, numStartLon, label + "S");
	}

	/**
	 * Adds the heading.
	 */
	protected void addHeading() {
		JLabel lblStart = new JLabel("Startposition", SwingConstants.CENTER);
		add(lblStart, "cell 1 0,growx");

		JLabel lblDest = new JLabel("Zielposition", SwingConstants.CENTER);
		add(lblDest, "cell 5 0,growx");
	}

	/**
	 * Adds the JLabel for the request.
	 * @param label the text for the JLabel
	 */
	protected void addLabel(String label) {
		lblLabel = new JLabel(label + ":", SwingConstants.CENTER);
		add(lblLabel, "cell 0 1 1 2,growx");
	}

	/**
	 * Adds JNumberFields for the start position
	 * and a button to set it.
	 */
	protected void addStartPos() {
		numStartLat = new JNumberField("0", 10, SwingConstants.CENTER);
		add(numStartLat, "cell 1 1,growx");

		numStartLon = new JNumberField("0", 10, SwingConstants.CENTER);
		add(numStartLon, "cell 1 2,growx");

		btnSetStart = new JButton("Ausw.");
		add(btnSetStart, "cell 2 1 1 2,growx");
	}

	/**
	 * @return the JLabel representing the request
	 */
	public JLabel getLblLabel() {
		return lblLabel;
	}

	/**
	 * @return the JNumberField containing the latitude of the start position
	 */
	public JNumberField getNumStartLat() {
		return numStartLat;
	}

	/**
	 * @return the JNumberField containing the longitude of the start position
	 */
	public JNumberField getNumStartLon() {
		return numStartLon;
	}

	/**
	 * @return the JButton that sets the start position
	 */
	public JButton getBtnSetStart() {
		return btnSetStart;
	}

	/**
	 * @return the WaypointNFTuple representing the start position
	 */
	public WaypointNFTuple getStartTuple() {
		return startTuple;
	}
}