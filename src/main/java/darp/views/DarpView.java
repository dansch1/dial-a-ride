package darp.views;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLayeredPane;

import darp.jxmapviewer2.JXMapViewerBounded;
import net.miginfocom.swing.MigLayout;

/**
 * The GUI for the main application.
 * @author Daniel Schröder
 */
public class DarpView extends JFrame {

	private static final long serialVersionUID = -4018698061596668173L;

	private JLayeredPane contentPane;
	private JXMapViewerBounded mapViewer;

	private TestManagerView testManagerView;
	private ParametersView parametersView;
	private ResultView resultView;

	private JButton btnStart;

	/**
	 * Creates a new GUI for the main application.
	 */
	public DarpView() {
		// initialize frame and content pane
		setupFrame();
		setupContentPane();

		// first layer
		addMapViewer();

		// second layer
		addTestSection();
		addParameterSection();
		addResultSection();

		addStartButton();

		// show frame
		setVisible(true);
	}

	/**
	 * Initializes the frame.
	 */
	private void setupFrame() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setMinimumSize(new Dimension(1280, 720));

		// According to the Javadoc:
		// If the component is null, or the GraphicsConfiguration associated with this component is null, the window is placed in the center of the screen.
		setLocationRelativeTo(null);
		setTitle("Dial-a-Ride");
	}

	/**
	 * Creates and sets the content pane.
	 */
	private void setupContentPane() {
		contentPane = new JLayeredPane();
		contentPane.setLayout(new BorderLayout());

		setContentPane(contentPane);
	}

	/**
	 * Adds the map viewer.
	 */
	private void addMapViewer() {
		mapViewer = new JXMapViewerBounded();
		mapViewer.setLayout(
				new MigLayout("", "[sizegroup side,grow][grow 1000][100px,grow][grow 1000][sizegroup side,grow]", "[grow][max,grow][grow][grow]"));

		contentPane.add(mapViewer);
	}

	/**
	 * Adds the test section.
	 */
	private void addTestSection() {
		testManagerView = new TestManagerView();
		testManagerView.setOpaque(false);
		mapViewer.add(testManagerView, "cell 0 0 1 3,grow");
	}

	/**
	 * Adds the parameter section.
	 */
	private void addParameterSection() {
		parametersView = new ParametersView();
		parametersView.setOpaque(false);
		mapViewer.add(parametersView, "cell 4 0,grow");
	}

	/**
	 * Adds the result section.
	 */
	private void addResultSection() {
		resultView = new ResultView();
		resultView.setOpaque(false);
		mapViewer.add(resultView, "cell 4 2,grow");
	}

	/**
	 * Adds the start button.
	 */
	private void addStartButton() {
		btnStart = new JButton("Start");
		mapViewer.add(btnStart, "cell 2 2,growx,aligny bottom");
	}

	@Override
	public JLayeredPane getContentPane() {
		return contentPane;
	}

	/**
	 * @return the JXMapViewerBounded that displays the map
	 */
	public JXMapViewerBounded getMapViewer() {
		return mapViewer;
	}

	/**
	 * @return the TestManagerView that displays the tests
	 */
	public TestManagerView getTestPanel() {
		return testManagerView;
	}

	/**
	 * @return the ParametersView that displays the parameters of a test
	 */
	public ParametersView getParamPanel() {
		return parametersView;
	}

	/**
	 * @return the ResultView that displays and controls the result of a test
	 */
	public ResultView getControlPanel() {
		return resultView;
	}

	/**
	 * @return the JButton to start the algorithm
	 */
	public JButton getBtnStart() {
		return btnStart;
	}
}