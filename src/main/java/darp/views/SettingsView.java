package darp.views;

import java.awt.Dimension;
import java.awt.Font;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import darp.swing.JComponentGroup;
import darp.swing.JNumberField;
import darp.utils.NumberFieldTuple;
import net.miginfocom.swing.MigLayout;

/**
 * The GUI that contains the settings.
 * @author Daniel Schröder
 */
public class SettingsView extends JFrame {

	private static final long serialVersionUID = 8255921649051801198L;

	private JPanel contentPane;

	private JCheckBox cbOffline, cbCaching, cbBounds;

	private JNumberField numNorth, numWest, numEast, numSouth;
	private NumberFieldTuple nwTuple, seTuple;
	private JComponentGroup boundsSection;

	private JTextField txtRoutingFilePath, txtMapFilePath;
	private JButton btnRoutingFileChooser, btnMapFileChooser;
	private JComponentGroup osmSection;

	private JButton btnStart;

	/**
	 * Creates a new GUI that contains the settings.
	 */
	public SettingsView() {
		// initialize frame and content pane
		setupFrame();
		setupContentPane();

		// add components
		addHeading();
		addGeneralSection();
		addBoundsSection();
		addGHSection();
		addOSMSection();
		addStartButton();

		// show frame
		setVisible(true);
	}

	/**
	 * Initializes the frame.
	 */
	private void setupFrame() {
		// close operation should be handled by the controller
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setMinimumSize(new Dimension(500, 500));

		// According to the Javadoc:
		// If the component is null, or the GraphicsConfiguration associated with this component is null, the window is placed in the center of the screen.
		setLocationRelativeTo(null);
		setTitle("Dial-a-Ride");
	}

	/**
	 * Creates and sets the content pane.
	 */
	private void setupContentPane() {
		contentPane = new JPanel();

		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new MigLayout("", "[sizegroup side,grow][grow][grow][grow][sizegroup side,grow]",
				"[grow][grow][grow][grow][grow][grow][grow][grow][grow][grow][grow][grow][grow][grow][grow][grow]"));

		setContentPane(contentPane);
	}

	/**
	 * Adds the heading.
	 */
	private void addHeading() {
		JLabel lblHeading = new JLabel("Einstellungen", SwingConstants.CENTER);
		lblHeading.setFont(new Font("Tahoma", Font.PLAIN, 20));
		contentPane.add(lblHeading, "cell 0 0 5 1,growx");
	}

	/**
	 * Adds the general section containing options for offline mode, caching and a bounding box.
	 */
	private void addGeneralSection() {
		// add section heading
		JLabel lblGeneral = new JLabel("Allgemein");
		lblGeneral.setFont(new Font("Tahoma", Font.PLAIN, 15));
		contentPane.add(lblGeneral, "cell 0 1 5 1,growx");

		// add options
		cbOffline = new JCheckBox("Offline");
		contentPane.add(cbOffline, "cell 0 2,growx");

		cbCaching = new JCheckBox("Caching");
		contentPane.add(cbCaching, "cell 0 3,growx");

		cbBounds = new JCheckBox("Karte begrenzen (Breitengrad/Längengrad)");
		contentPane.add(cbBounds, "cell 0 4 3 1,growx");
	}

	/**
	 * Adds the section for the bounding box.
	 */
	private void addBoundsSection() {
		// add labels and number fields to specify the bounding box
		JLabel lblNorth = new JLabel("Norden", SwingConstants.CENTER);
		contentPane.add(lblNorth, "cell 2 5,growx,aligny bottom");

		numNorth = new JNumberField("0", 10, SwingConstants.CENTER);
		contentPane.add(numNorth, "cell 2 6,growx");

		JLabel lblWest = new JLabel("Westen", SwingConstants.CENTER);
		contentPane.add(lblWest, "cell 1 7,growx,aligny bottom");

		numWest = new JNumberField("0", 10, SwingConstants.CENTER);
		contentPane.add(numWest, "cell 1 8,growx");

		// represents the north west point of the bounding box
		nwTuple = new NumberFieldTuple(numNorth, numWest);

		JLabel lblEast = new JLabel("Osten", SwingConstants.CENTER);
		contentPane.add(lblEast, "cell 3 7,growx,aligny bottom");

		numEast = new JNumberField("0", 10, SwingConstants.CENTER);
		contentPane.add(numEast, "cell 3 8,growx");

		JLabel lblSouth = new JLabel("Süden", SwingConstants.CENTER);
		contentPane.add(lblSouth, "cell 2 9,growx,aligny bottom");

		numSouth = new JNumberField("0", 10, SwingConstants.CENTER);
		contentPane.add(numSouth, "cell 2 10,growx");

		// represents the south east point of the bounding box
		seTuple = new NumberFieldTuple(numSouth, numEast);

		// contains all components of the this section
		boundsSection = new JComponentGroup(lblNorth, numNorth, lblWest, numWest, lblEast, numEast, lblSouth, numSouth);
	}

	/**
	 * Adds the section to select a pbf file.
	 */
	private void addGHSection() {
		// add section heading
		JLabel lblGH = new JLabel("GraphHopper");
		lblGH.setFont(new Font("Tahoma", Font.PLAIN, 15));
		contentPane.add(lblGH, "cell 0 11 5 1,growx");

		// displays the path of the selected file
		txtRoutingFilePath = new JTextField(10);
		txtRoutingFilePath.setEditable(false);
		contentPane.add(txtRoutingFilePath, "cell 0 12 4 1,growx");

		// button to select a file
		btnRoutingFileChooser = new JButton("...");
		contentPane.add(btnRoutingFileChooser, "cell 4 12,growx");
	}

	/**
	 * Adds the section to select map files.
	 */
	private void addOSMSection() {
		// add section heading
		JLabel lblOSM = new JLabel("OpenStreetMap");
		lblOSM.setFont(new Font("Tahoma", Font.PLAIN, 15));
		contentPane.add(lblOSM, "cell 0 13 5 1,growx");

		// displays the path of the selected file
		txtMapFilePath = new JTextField(10);
		txtMapFilePath.setEditable(false);
		contentPane.add(txtMapFilePath, "cell 0 14 4 1,growx");

		// button to select a file
		btnMapFileChooser = new JButton("...");
		contentPane.add(btnMapFileChooser, "cell 4 14,growx");

		// contains all components of the this section
		osmSection = new JComponentGroup(txtMapFilePath, btnMapFileChooser);
	}

	/**
	 * Adds the start button.
	 */
	private void addStartButton() {
		btnStart = new JButton("Start");
		btnStart.setFont(new Font("Tahoma", Font.PLAIN, 20));
		contentPane.add(btnStart, "cell 0 15 5 1,growx");
	}

	/**
	 * @return the JCheckBox representing the offline option
	 */
	public JCheckBox getCbOffline() {
		return cbOffline;
	}

	/**
	 * @return the JCheckBox representing the caching option
	 */
	public JCheckBox getCbCaching() {
		return cbCaching;
	}

	/**
	 * @return the JCheckBox representing the bounding box option
	 */
	public JCheckBox getCbBounds() {
		return cbBounds;
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
		return nwTuple;
	}

	/**
	 * @return the NumberFieldTuple representing the south east point of the bounding box
	 */
	public NumberFieldTuple getSouthEastTuple() {
		return seTuple;
	}

	/**
	 * @return the JComponentGroup containing all components of the bounds section
	 */
	public JComponentGroup getBoundsSection() {
		return boundsSection;
	}

	/**
	 * @return the JTextField that contains the pbf file path
	 */
	public JTextField getTxtRoutingFilePath() {
		return txtRoutingFilePath;
	}

	/**
	 * @return the JTextField that contains the map files path
	 */
	public JTextField getTxtMapFilePath() {
		return txtMapFilePath;
	}

	/**
	 * @return the JButton to select a pbf file
	 */
	public JButton getBtnRoutingFileChooser() {
		return btnRoutingFileChooser;
	}

	/**
	 * @return the JButton to select map files
	 */
	public JButton getBtnMapFileChooser() {
		return btnMapFileChooser;
	}

	/**
	 * @return the JComponentGroup containing all components of the osm section
	 */
	public JComponentGroup getOSMSection() {
		return osmSection;
	}

	/**
	 * @return the JButton to start the application
	 */
	public JButton getBtnStart() {
		return btnStart;
	}
}