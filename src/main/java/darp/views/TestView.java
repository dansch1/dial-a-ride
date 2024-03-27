package darp.views;

import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import darp.swing.JNumberField;
import darp.utils.NumberFieldTuple;
import net.miginfocom.swing.MigLayout;

/**
 * The GUI representing a test.
 * @author Daniel Schröder
 */
public class TestView extends JScrollPane {

	private static final long serialVersionUID = 2164571145153544954L;

	private JPanel contentPane;

	private JTextField txtTestName;
	private JNumberField numNorth, numWest, numEast, numSouth;
	private NumberFieldTuple northWestTuple, southEastTuple;
	private JButton btnSelectBounds, btnToggleBounds, btnRandomize;

	private JPanel requestPanel;
	private VehicleRequestView vehicleRequest;
	private JButton btnAddRequest;

	/**
	 * Creates a new GUI representing a test.
	 */
	public TestView() {
		// create and set content pane
		setupContentPane();

		// add components
		addNameSection();
		addBoundsSection();
		addRequestSection();
	}

	/**
	 * Creates and sets the content pane.
	 */
	private void setupContentPane() {
		contentPane = new JPanel();

		contentPane.setLayout(
				new MigLayout("", "[100px,grow][100px,grow][100px,grow]", "[grow][grow][grow][grow][grow][grow][grow][grow][grow][grow][grow]"));

		setViewportView(contentPane);
	}

	/**
	 * Adds the JTextField for the test name.
	 */
	private void addNameSection() {
		txtTestName = new JTextField("Testname", 10);
		contentPane.add(txtTestName, "cell 0 0 3 1,growx");

		JSeparator separator = new JSeparator();
		contentPane.add(separator, "cell 0 1 3 1,growx");
	}

	/**
	 * Adds the section for the bounding box.
	 */
	private void addBoundsSection() {
		// add labels and number fields to specify the bounding box
		JLabel lblNorth = new JLabel("Norden", SwingConstants.CENTER);
		contentPane.add(lblNorth, "cell 1 2,growx,aligny bottom");

		numNorth = new JNumberField("0", 5, SwingConstants.CENTER);
		contentPane.add(numNorth, "cell 1 3,growx");

		JLabel lblWest = new JLabel("Westen", SwingConstants.CENTER);
		contentPane.add(lblWest, "cell 0 3,growx,aligny bottom");

		numWest = new JNumberField("0", 5, SwingConstants.CENTER);
		contentPane.add(numWest, "cell 0 4,growx");

		// represents the north west point of the bounding box
		northWestTuple = new NumberFieldTuple(numNorth, numWest);

		JLabel lblEast = new JLabel("Osten", SwingConstants.CENTER);
		contentPane.add(lblEast, "cell 2 3,growx,aligny bottom");

		numEast = new JNumberField("0", 5, SwingConstants.CENTER);
		contentPane.add(numEast, "cell 2 4,growx");

		JLabel lblSouth = new JLabel("Süden", SwingConstants.CENTER);
		contentPane.add(lblSouth, "cell 1 6,growx,aligny top");

		numSouth = new JNumberField("0", 5, SwingConstants.CENTER);
		contentPane.add(numSouth, "cell 1 5,growx");

		// represents the south east point of the bounding box
		southEastTuple = new NumberFieldTuple(numSouth, numEast);

		// add buttons to select and toggle the bounding box
		btnSelectBounds = new JButton("Auswählen");
		contentPane.add(btnSelectBounds, "cell 0 7,growx");

		btnToggleBounds = new JButton("An");
		contentPane.add(btnToggleBounds, "cell 1 7,growx");

		// and a button to get random positions from the bounding box
		btnRandomize = new JButton("Füllen");
		contentPane.add(btnRandomize, "cell 2 7,growx");

		JSeparator separator = new JSeparator();
		contentPane.add(separator, "cell 0 8 3 1,growx");
	}

	/**
	 * Adds the section containing all the requests of a test.
	 */
	private void addRequestSection() {
		requestPanel = new JPanel();
		requestPanel.setLayout(new GridLayout(0, 1));
		contentPane.add(requestPanel, "cell 0 9 3 1,grow");

		// add a vehicle request
		vehicleRequest = new VehicleRequestView("A");
		requestPanel.add(vehicleRequest);

		// button to add requests
		btnAddRequest = new JButton("+");
		contentPane.add(btnAddRequest, "cell 1 10,growx");
	}

	/**
	 * @return the JTextField that contains the name of the test
	 */
	public JTextField getTxtTestName() {
		return txtTestName;
	}

	/**
	 * @return the JNumberField that contains the north value of the bounding box
	 */
	public JNumberField getNumNorth() {
		return numNorth;
	}

	/**
	 * @return the JNumberField that contains the west value of the bounding box
	 */
	public JNumberField getNumWest() {
		return numWest;
	}

	/**
	 * @return the JNumberField that contains the east value of the bounding box
	 */
	public JNumberField getNumEast() {
		return numEast;
	}

	/**
	 * @return the JNumberField that contains the south value of the bounding box
	 */
	public JNumberField getNumSouth() {
		return numSouth;
	}

	/**
	 * @return the NumberFieldTuple representing the north west point of the bounding box
	 */
	public NumberFieldTuple getNorthWestTuple() {
		return northWestTuple;
	}

	/**
	 * @return the NumberFieldTuple representing the south east point of the bounding box
	 */
	public NumberFieldTuple getSouthEastTuple() {
		return southEastTuple;
	}

	/**
	 * @return the JButton to select the bounding box
	 */
	public JButton getBtnSelectBounds() {
		return btnSelectBounds;
	}

	/**
	 * @return the JButton to toggle the bounding box
	 */
	public JButton getBtnToggleBounds() {
		return btnToggleBounds;
	}

	/**
	 * @return the JButton to get random positions from the bounding box
	 */
	public JButton getBtnRandomize() {
		return btnRandomize;
	}

	/**
	 * @return the JPanel that contains all the requests
	 */
	public JPanel getRequestPanel() {
		return requestPanel;
	}

	/**
	 * @return the VehicleRequestView that represents the vehicle request
	 */
	public VehicleRequestView getVehicleRequest() {
		return vehicleRequest;
	}

	/**
	 * @return the JButton to add a request
	 */
	public JButton getBtnAddRequest() {
		return btnAddRequest;
	}
}