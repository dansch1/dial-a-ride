package darp.views;

import java.awt.Font;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingConstants;

import darp.swing.JNumberField;
import darp.swing.NumberFilter;
import net.miginfocom.swing.MigLayout;

/**
 * The GUI representing the parameters of a test.
 * @author Daniel Schröder
 */
public class ParametersView extends JPanel {

	private static final long serialVersionUID = -4282400004385850291L;

	private JNumberField numCapacity, numMPS, numTimePref;
	private JSlider sliderWeights;
	private JLabel lblWeight1, lblWeight2;
	private JCheckBox cbRouting;

	/**
	 * Creates a new GUI representing the parameters of a test.
	 */
	public ParametersView() {
		setLayout(new MigLayout("insets 0", "[grow][grow][75px,grow][grow]", "[grow][grow][grow][grow][grow][grow]"));

		// add components
		addNumberFields();
		addSlider();
		addCheckBox();
	}

	/**
	 * Adds the fields for capacity, mps, and time preference.
	 */
	private void addNumberFields() {
		JLabel lblCapacity = new JLabel("Fahrzeugkapazität (C):", SwingConstants.RIGHT);
		add(lblCapacity, "cell 0 0,growx");

		numCapacity = new JNumberField("1", 3, SwingConstants.CENTER, new NumberFilter("[1-9][0-9]*|^$"));
		add(numCapacity, "cell 2 0,growx");

		JLabel lblMPS = new JLabel("Maximum Position Shift (MPS):", SwingConstants.RIGHT);
		add(lblMPS, "cell 0 1,growx");

		numMPS = new JNumberField("0", 3, SwingConstants.CENTER, new NumberFilter("[0-9]*"));
		add(numMPS, "cell 2 1,growx");

		JLabel lblTimePref = new JLabel("Zeitpräferenz (a):", SwingConstants.RIGHT);
		add(lblTimePref, "cell 0 2,growx");

		numTimePref = new JNumberField("1", 3, SwingConstants.CENTER, new NumberFilter("[0-1](\\.[0-9]*)?|2|^$"));
		add(numTimePref, "cell 2 2,growx");
	}

	/**
	 * Adds the JSlider for the weighting.
	 */
	private void addSlider() {
		JLabel lblWeights = new JLabel("Gewichtung (w1, w2):", SwingConstants.RIGHT);
		add(lblWeights, "cell 0 3,growx");

		sliderWeights = new JSlider();
		// show ticks
		sliderWeights.setMinorTickSpacing(5);
		sliderWeights.setMajorTickSpacing(25);
		sliderWeights.setPaintTicks(true);
		// transparent background
		sliderWeights.setOpaque(false);
		add(sliderWeights, "cell 1 3 3 1,growx");

		// displays the value of w1
		lblWeight1 = new JLabel("w1: 0.5", SwingConstants.CENTER);
		lblWeight1.setFont(new Font("Tahoma", Font.BOLD, 10));
		add(lblWeight1, "cell 1 4,growx,aligny top");

		// displays the value of w2
		lblWeight2 = new JLabel("w1: 0.5", SwingConstants.CENTER);
		lblWeight2.setFont(new Font("Tahoma", Font.BOLD, 10));
		add(lblWeight2, "cell 3 4,growx,aligny top");
	}

	/**
	 * Adds the JCheckBox for routing.
	 */
	private void addCheckBox() {
		cbRouting = new JCheckBox("Routing");
		cbRouting.setHorizontalAlignment(SwingConstants.CENTER);
		// transparent background
		cbRouting.setOpaque(false);
		add(cbRouting, "cell 2 5,growx");
	}

	/**
	 * @return the JNumberField containing the capacity value
	 */
	public JNumberField getNumCapacity() {
		return numCapacity;
	}

	/**
	 * @return the JNumberField containing the mps value
	 */
	public JNumberField getNumMPS() {
		return numMPS;
	}

	/**
	 * @return the JNumberField containing the time preference value
	 */
	public JNumberField getNumTimePref() {
		return numTimePref;
	}

	/**
	 * @return the JSlider that controls the weighting
	 */
	public JSlider getSliderWeights() {
		return sliderWeights;
	}

	/**
	 * @return the JLabel displaying the value of w1
	 */
	public JLabel getLblWeight1() {
		return lblWeight1;
	}

	/**
	 * @return the JLabel displaying the value of w2
	 */
	public JLabel getLblWeight2() {
		return lblWeight2;
	}

	/**
	 * @return the JCheckBox representing the routing option
	 */
	public JCheckBox getCbRouting() {
		return cbRouting;
	}
}