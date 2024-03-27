package darp.views;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import darp.swing.JNumberField;
import net.miginfocom.swing.MigLayout;

/**
 * The GUI containing statistics and controls for a result.
 * @author Daniel Schröder
 */
public class ResultView extends JPanel {

	private static final long serialVersionUID = -5584042920239135643L;

	private JNumberField numAlgo, numRouting, numTotal;
	private JButton btnFirst, btnPrev, btnNext, btnLast;
	private JTextField txtFrame;

	/**
	 * Creates a new GUI containing statistics and controls for a result.
	 */
	public ResultView() {
		setLayout(new MigLayout("insets 0", "[100px,grow][100px,grow][100px,grow][100px,grow][100px,grow]", "[grow][grow][grow]"));

		// add components
		addStatistics();
		addControls();
	}

	/**
	 * Adds fields for the algorithm time, the routing time and total time.
	 */
	private void addStatistics() {
		JLabel lblAlgo = new JLabel("Algorithmus (ms)", SwingConstants.CENTER);
		add(lblAlgo, "cell 1 0,growx");

		numAlgo = new JNumberField("0", 10, SwingConstants.CENTER);
		numAlgo.setEditable(false);
		add(numAlgo, "cell 1 1,growx");

		JLabel lblRouting = new JLabel("Routing (ms)", SwingConstants.CENTER);
		add(lblRouting, "cell 2 0,growx");

		numRouting = new JNumberField("0", 10, SwingConstants.CENTER);
		numRouting.setEditable(false);
		add(numRouting, "cell 2 1,growx");

		JLabel lblTotal = new JLabel("Insgesamt (ms)", SwingConstants.CENTER);
		add(lblTotal, "cell 3 0,growx");

		numTotal = new JNumberField("0", 10, SwingConstants.CENTER);
		numTotal.setEditable(false);
		add(numTotal, "cell 3 1,growx");
	}

	/**
	 * Adds controls to display the route.
	 */
	private void addControls() {
		btnFirst = new JButton("<<");
		add(btnFirst, "cell 0 2,growx");

		btnPrev = new JButton("<");
		add(btnPrev, "cell 1 2,growx");

		btnNext = new JButton(">");
		add(btnNext, "cell 2 2,growx");

		btnLast = new JButton(">>");
		add(btnLast, "cell 3 2,growx");

		// shows the user how much of the route is rendered
		txtFrame = new JTextField("1/1", 10);
		txtFrame.setHorizontalAlignment(SwingConstants.CENTER);
		txtFrame.setEditable(false);
		add(txtFrame, "cell 4 2,growx");
	}

	/**
	 * @return the JNumberField that contains the algorithm time
	 */
	public JNumberField getNumAlgo() {
		return numAlgo;
	}

	/**
	 * @return the JNumberField that contains the routing time
	 */
	public JNumberField getNumRouting() {
		return numRouting;
	}

	/**
	 * @return the JNumberField that contains the total time
	 */
	public JNumberField getNumTotal() {
		return numTotal;
	}

	/**
	 * @return the JButton to hide the route
	 */
	public JButton getBtnFirst() {
		return btnFirst;
	}

	/**
	 * @return the JButton to remove one step of the route
	 */
	public JButton getBtnPrev() {
		return btnPrev;
	}

	/**
	 * @return the JButton to add one step to the route
	 */
	public JButton getBtnNext() {
		return btnNext;
	}

	/**
	 * @return the JButton to display the entire route
	 */
	public JButton getBtnLast() {
		return btnLast;
	}

	/**
	 * @return the JTextField that shows how much of the route is rendered
	 */
	public JTextField getTxtFrame() {
		return txtFrame;
	}
}