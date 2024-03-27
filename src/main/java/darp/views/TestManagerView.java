package darp.views;

import java.awt.BorderLayout;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import net.miginfocom.swing.MigLayout;

/**
 * The GUI representing a test manager.
 * @author Daniel Schröder
 */
public class TestManagerView extends JPanel {

	private static final long serialVersionUID = 9064267653466600908L;

	private JButton btnFirst, btnPrev, btnNext, btnLast;
	private JPanel testPanel;
	private JLabel lblPage;
	private JButton btnRemove, btnAdd, btnLoad, btnSave;

	/**
	 * Creates a new GUI representing a test manager.
	 */
	public TestManagerView() {
		setLayout(new MigLayout("insets 0", "[100px,grow][100px,grow][100px,grow][100px,grow]", "[grow][max,grow][grow]"));

		// add components
		addControlButtons();
		addTestPanel();
		addCRUDButtons();
	}

	/**
	 * Adds the JButtons to cycle between tests.
	 */
	private void addControlButtons() {
		btnFirst = new JButton("<<");
		add(btnFirst, "cell 0 0,growx");

		btnPrev = new JButton("<");
		add(btnPrev, "cell 1 0,growx");

		btnNext = new JButton(">");
		add(btnNext, "cell 2 0,growx");

		btnLast = new JButton(">>");
		add(btnLast, "cell 3 0,growx");
	}

	/**
	 * Adds the JPanel for the current test.
	 */
	private void addTestPanel() {
		JPanel wrapper = new JPanel();
		wrapper.setLayout(new BorderLayout());
		add(wrapper, "cell 0 1 4 1,grow");

		// the panel that should hold the test
		testPanel = new JPanel();
		testPanel.setLayout(new BorderLayout());
		wrapper.add(testPanel, BorderLayout.CENTER);

		// label to show the user which test is visible
		lblPage = new JLabel("1/1", SwingConstants.CENTER);
		wrapper.add(lblPage, BorderLayout.SOUTH);
	}

	/**
	 * Adds JButtons for removing/adding and loading/saving a test.
	 */
	private void addCRUDButtons() {
		btnRemove = new JButton("-");
		btnRemove.setEnabled(false);
		add(btnRemove, "cell 0 2,growx");

		btnAdd = new JButton("+");
		add(btnAdd, "cell 1 2,growx");

		btnLoad = new JButton("Laden");
		add(btnLoad, "cell 2 2,growx");

		btnSave = new JButton("Speichern");
		add(btnSave, "cell 3 2,growx");
	}

	/**
	 * @return the JButton to show the first test
	 */
	public JButton getBtnFirst() {
		return btnFirst;
	}

	/**
	 * @return the JButton to show the previous test
	 */
	public JButton getBtnPrev() {
		return btnPrev;
	}

	/**
	 * @return the JButton to show the next test
	 */
	public JButton getBtnNext() {
		return btnNext;
	}

	/**
	 * @return the JButton to show the last test
	 */
	public JButton getBtnLast() {
		return btnLast;
	}

	/**
	 * @return the JPanel containing the current test
	 */
	public JPanel getTestPanel() {
		return testPanel;
	}

	/**
	 * @return the JLabel containing the current test index
	 */
	public JLabel getLblPage() {
		return lblPage;
	}

	/**
	 * @return the JButton to remove the current test
	 */
	public JButton getBtnRemove() {
		return btnRemove;
	}

	/**
	 * @return the JButton to add a new test
	 */
	public JButton getBtnAdd() {
		return btnAdd;
	}

	/**
	 * @return the JButton to load tests
	 */
	public JButton getBtnLoad() {
		return btnLoad;
	}

	/**
	 * @return the JButton to save all tests
	 */
	public JButton getBtnSave() {
		return btnSave;
	}
}