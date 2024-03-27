package darp.controllers;

import java.awt.Component;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.OSMTileFactoryInfo;
import org.jxmapviewer.cache.FileBasedLocalCache;
import org.jxmapviewer.painter.CompoundPainter;
import org.jxmapviewer.painter.Painter;
import org.jxmapviewer.viewer.DefaultTileFactory;
import org.jxmapviewer.viewer.GeoPosition;
import org.jxmapviewer.viewer.TileFactoryInfo;

import com.graphhopper.GHResponse;

import darp.algorithm.DarpSolver;
import darp.algorithm.Psaraftis;
import darp.jxmapviewer2.FancyWaypointRenderer;
import darp.jxmapviewer2.JXMapViewerBounded;
import darp.jxmapviewer2.MouseInputListener;
import darp.jxmapviewer2.MyWaypoint;
import darp.jxmapviewer2.RoutePainter;
import darp.jxmapviewer2.SelectionAdapter;
import darp.jxmapviewer2.SelectionPainter;
import darp.jxmapviewer2.WaypointPainter;
import darp.models.ResultModel;
import darp.models.SettingsModel;
import darp.models.TestModel;
import darp.utils.Log;
import darp.utils.RouteSegment;
import darp.utils.Router;
import darp.utils.Utilities;
import darp.views.DarpView;

/**
 * The controller for a {@link DarpView}.
 * @author Daniel Schröder
 */
public class DarpController {

	private DarpView view;
	private SettingsModel model;

	private Router router;
	private DarpSolver darpSolver;
	private Thread darpThread;

	private ParametersController parametersController;
	private ResultController resultController;
	private TestManagerController testManagerController;

	private RoutePainter routePainter;
	private WaypointPainter<MyWaypoint> waypointPainter;

	private MouseInputListener mouseListener;
	private SelectionAdapter selectionAdapter;

	private boolean loadLock;

	private Set<TestModel> invalidResults;

	private final String CACHE_PATH = "src/main/resources/.jxmapviewer2";

	private enum Visibility {
		FULL, RESULT_ONLY, NONE;

		private static Visibility[] vals = values();

		public Visibility next() {
			return vals[(this.ordinal() + 1) % vals.length];
		}
	}

	private Visibility visibility = Visibility.FULL;

	/**
	 * Creates new controller for a given DarpView.
	 * @param view the DarpView
	 * @param model the settings to be used
	 * @throws NullPointerException if null is passed
	 */
	public DarpController(DarpView view, SettingsModel model) throws NullPointerException {
		this.view = Objects.requireNonNull(view);
		this.model = Objects.requireNonNull(model);

		invalidResults = new HashSet<>();
	}

	/**
	 * Initializes the controller.
	 */
	public void initController() {
		createKeyBinds();
		createDarpSolver();

		setupMapViewer();
		setupController();
		setupStartButton();
	}

	/**
	 * Creates key bindings.
	 */
	private void createKeyBinds() {
		InputMap iMap = view.getContentPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
		ActionMap aMap = view.getContentPane().getActionMap();

		// add the key bindings to the input map
		iMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0), "f1");
		iMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0), "f2");

		// assign actions to the key bindings in the action map
		aMap.put("f1", new AbstractAction() {
			private static final long serialVersionUID = 102466445051728637L;

			@Override
			public void actionPerformed(ActionEvent e) {
				toggleVisibility();
			}
		});

		aMap.put("f2", new AbstractAction() {
			private static final long serialVersionUID = -2254216831479141961L;

			@Override
			public void actionPerformed(ActionEvent e) {
				showAllWayoints();
			}
		});
	}

	/**
	 * Toggles the visibility of the view.
	 */
	private void toggleVisibility() {
		if (!selectionAdapter.isSelecting() && !mouseListener.isSelecting()) {
			setViewVisibility(visibility.next());
		}
	}

	/**
	 * Sets the visibility of the view. There are 3 modes:
	 * FULL: shows entire view
	 * RESULT_ONLY: shows only the statistics and controls for the route
	 * NONE: hides the view
	 * @param visibility the visibility mode
	 */
	private void setViewVisibility(Visibility visibility) {
		this.visibility = visibility;

		switch (visibility) {
			case FULL:
				setVisibilityOfComponents(view.getMapViewer(), true);
				break;
			case RESULT_ONLY:
				setVisibilityOfComponents(view.getMapViewer(), false);
				view.getControlPanel().setVisible(true);
				break;
			case NONE:
				setVisibilityOfComponents(view.getMapViewer(), false);
				break;
			default:
				throw new IllegalArgumentException("Invalid value for enum " + visibility);
		}
	}

	/**
	 * Sets the visibility of each component in the given container.
	 * @param container the container
	 * @param visibility <code> true </code> if the components should be visible
	 */
	private void setVisibilityOfComponents(Container container, boolean visibility) {
		for (Component c : container.getComponents()) {
			c.setVisible(visibility);
		}
	}

	/**
	 * Zoom and center the map to a best fit around the current waypoints.
	 */
	private void showAllWayoints() {
		Set<GeoPosition> positions = new HashSet<>();

		for (MyWaypoint w : waypointPainter.getWaypoints()) {
			positions.add(w.getPosition());
		}

		view.getMapViewer().zoomToBestFitInsideBounds(positions, 0.7);
	}

	/**
	 * Creates all the necessary components for the darp.
	 */
	private void createDarpSolver() {
		router = new Router(model.getGHPath());
		darpSolver = new Psaraftis(router);
		darpThread = new Thread();
	}

	/**
	 * Initializes the map viewer.
	 */
	private void setupMapViewer() {
		// create a TileFactoryInfo for OSM
		TileFactoryInfo info;

		if (model.isOffline()) {
			info = new OSMTileFactoryInfo("ZIP archive", "jar:file:/" + model.getOSMPath().replace("\\", "/") + "!");
		} else {
			info = new OSMTileFactoryInfo();
		}

		DefaultTileFactory tileFactory = new DefaultTileFactory(info);

		// setup local file cache
		if (model.isCaching()) {
			File cacheDir = new File(CACHE_PATH);
			tileFactory.setLocalCache(new FileBasedLocalCache(cacheDir, false));
		}

		// setup the map viewer
		JXMapViewerBounded mapViewer = view.getMapViewer();
		mapViewer.setTileFactory(tileFactory);

		// default center position
		GeoPosition center = new GeoPosition(51.9606649, 7.6261347);

		// bounding box
		if (model.isBoundsEnabled()) {
			mapViewer.setBounds(model.getNorthWestPos(), model.getSouthEastPos());
			mapViewer.setBoundsEnabled(true);
			center = Utilities.computeGeoCenter(Arrays.asList(model.getNorthWestPos(), model.getSouthEastPos()));
		}

		mapViewer.setCenterPosition(center);

		// add interactions
		mouseListener = new MouseInputListener(this, mapViewer);
		mapViewer.addMouseListener(mouseListener);
		mapViewer.addMouseMotionListener(mouseListener);
		mapViewer.addMouseWheelListener(mouseListener);

		selectionAdapter = new SelectionAdapter(this, mapViewer);
		mapViewer.addMouseListener(selectionAdapter);
		mapViewer.addMouseMotionListener(selectionAdapter);

		// create painters
		routePainter = new RoutePainter();
		waypointPainter = new WaypointPainter<>();
		waypointPainter.setRenderer(new FancyWaypointRenderer());
		SelectionPainter sp = new SelectionPainter(selectionAdapter);

		// create a compound painter that combines all painters
		List<Painter<JXMapViewer>> painters = Arrays.asList(routePainter, waypointPainter, sp);
		CompoundPainter<JXMapViewer> painter = new CompoundPainter<>(painters);
		mapViewer.setOverlayPainter(painter);

		// only important if bounds are enabled
		view.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent componentEvent) {
				mapViewer.fitZoomInBounds();
			}
		});
	}

	/**
	 * Creates and initializes the controllers.
	 */
	private void setupController() {
		parametersController = new ParametersController(this, view.getParamPanel());
		parametersController.initController();

		resultController = new ResultController(this, view.getControlPanel());
		resultController.initController();

		testManagerController = new TestManagerController(this, view.getTestPanel());
		testManagerController.initController();
	}

	/**
	 * Add the functionality for the start button.
	 */
	private void setupStartButton() {
		view.getBtnStart().addActionListener(e -> startDarpThread());
	}

	/**
	 * Starts a new thread to solve the darp for the current tests.
	 * If a thread has already started, it will be interrupted instead.
	 */
	private void startDarpThread() {
		if (darpThread.isAlive()) {
			darpThread.interrupt();
			view.getBtnStart().setText("Stoppe...");
			return;
		}

		darpThread = new Thread(() -> startDarpSolver());
		darpThread.start();
	}

	/**
	 * Solves the darp for each created test.
	 * The result of the current test is loaded.
	 * This method should be started in a separate thread.
	 */
	private void startDarpSolver() {
		TestModel test;
		List<TestModel> tests = testManagerController.getTests();
		List<MyWaypoint> sequence;

		// map for the optimal routes of all tests
		Map<TestModel, List<RouteSegment>> results = new HashMap<>();

		// contains all errors of all tests
		String report = "";
		String errors;

		// calculate the optimal route for each test and save it in result
		for (int i = 0; i < tests.size(); i++) {
			// inform the user which test is being worked on
			view.getBtnStart().setText("Stopp (" + i + "/" + tests.size() + ")");

			test = tests.get(i);

			// calculate the optimal route and save it in result
			sequence = darpSolver.getOptRoute(test);
			errors = constructRoute(test, sequence, results);

			// add the test errors if there were any
			if (!errors.isEmpty()) {
				report += test.getRequestsModel().getTestName() + ":\n" + errors + "\n";
			}

			// set the times of the current test
			if (i == testManagerController.getIndexOfCurrent()) {
				resultController.setTimes(test.getResultModel());
			}

			// stop the calculations if the user clicked stop
			if (darpThread.isInterrupted()) {
				break;
			}
		}

		// set results
		for (Entry<TestModel, List<RouteSegment>> entry : results.entrySet()) {
			test = entry.getKey();

			// the test was edited -> the result is invalid
			if (invalidResults.contains(test)) {
				test.setResultModel(new ResultModel());
				continue;
			}

			ResultModel result = test.getResultModel();

			result.setRoute(entry.getValue());
			result.setFrame(0);
		}

		// load the result for the current test
		TestController current = testManagerController.getCurrentController();

		if (current != null) {
			resultController.setResultModel(current.getModel().getResultModel());
		}

		// all results are valid again
		invalidResults.clear();

		// update view
		view.getBtnStart().setText("Start");
		updateView();

		// show errors if there were any
		if (!report.isEmpty()) {
			Utilities.showScrollableMessageDialog(view, report.trim(), "Beim Routing sind folgende Fehler aufgetreten:", "Routing Fehler",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	/**
	 * Constructs the route for the given test based on the given sequence of waypoints.
	 * If routing is enabled, the actual path between the points is calculated.
	 * Otherwise the points are connected by straight lines.
	 * @param test the test
	 * @param sequence the sequence of waypoints
	 * @param results the map to save the result
	 * @return the errors that occurred during routing
	 */
	private String constructRoute(TestModel test, List<MyWaypoint> sequence, Map<TestModel, List<RouteSegment>> results) {
		// final route
		List<RouteSegment> route = new ArrayList<>();
		// positions for each segment of the route
		List<GeoPosition> positions;

		boolean routing = test.getParametersModel().isRouting();
		// routing errors
		String errors = "";

		for (int i = 1; i < sequence.size(); i++) {
			positions = new ArrayList<>();

			if (routing) {
				// calculate path
				GHResponse response = router.getRoute(sequence.get(i - 1), sequence.get(i));

				// add routing errors if there were any
				if (response.hasErrors()) {
					errors += response.getErrors() + "\n";
					continue;
				}

				// add path to list
				positions = Utilities.pointListToGeoList(response.getBest().getPoints());
			} else {
				// add points to list
				positions.add(sequence.get(i - 1).getPosition());
				positions.add(sequence.get(i).getPosition());
			}

			// add the new segment to the route
			route.add(new RouteSegment(positions, sequence.get(i).getColor()));
		}

		// only save the route if there are no errors
		if (errors.isEmpty()) {
			results.put(test, route);
		}

		return errors;
	}

	/**
	 * Activates the position-selection mode.
	 * The user has now the possibility to select a point on the map.
	 * In the meantime the view is disabled.
	 */
	public void selectGeoPosition() {
		setViewVisibility(Visibility.NONE);
		mouseListener.setSelecting(true);
	}

	/**
	 * Sets the selected GeoPosition in the selected JNumberField of the current test.
	 * Then the view is enabled again.
	 * @param selected the selected GeoPosition
	 * @throws NullPointerException if selected is null
	 */
	public void setGeoPosition(GeoPosition selected) throws NullPointerException {
		TestController current = testManagerController.getCurrentController();

		if (current == null) {
			return;
		}

		current.setGeoPosition(Objects.requireNonNull(selected));
		mouseListener.setSelecting(false);
		setViewVisibility(Visibility.FULL);
	}

	/**
	 * Activates the bounds-selection mode.
	 * The user has now the possibility to select a bounding box on the map.
	 * In the meantime the view is disabled.
	 */
	public void selectBounds() {
		setViewVisibility(Visibility.NONE);
		selectionAdapter.setSelecting(true);
	}

	/**
	 * Sets the selected bounding box of the current test.
	 * Then the view is enabled again.
	 * @param nw the north west GeoPosition of the bounding box
	 * @param se the south east GeoPosition of the bounding box
	 * @throws NullPointerException if null is passed
	 */
	public void setBounds(GeoPosition nw, GeoPosition se) throws NullPointerException {
		TestController current = testManagerController.getCurrentController();

		if (current == null) {
			return;
		}

		current.setBounds(nw, se);
		selectionAdapter.setSelecting(false);
		setViewVisibility(Visibility.FULL);
	}

	/**
	 * Saves the values of the view in the test of the given controller.
	 * @param controller the controller
	 * @throws NullPointerException if controller is null
	 */
	public void saveTest(TestController controller) throws NullPointerException {
		if (controller == null) {
			throw new NullPointerException("TestController can't be null");
		}

		TestModel test = controller.getModel();

		test.setRequestsModel(controller.getRequestsModel());
		test.setParametersModel(parametersController.getParametersModel());
		test.setResultModel(resultController.getResultModel());
	}

	/**
	 * Loads the values of the test of the given controller into the view.
	 * @param controller the controller
	 * @throws NullPointerException if controller is null
	 */
	public void loadTest(TestController controller) throws NullPointerException {
		if (controller == null) {
			throw new NullPointerException("TestController can't be null");
		}

		loadLock = true;

		TestModel test = controller.getModel();

		controller.loadVisuals();
		parametersController.setParametersModel(test.getParametersModel());
		resultController.setResultModel(test.getResultModel());

		loadLock = false;
	}

	/**
	 * Clears the route, the bounding box and all waypoints on the map.
	 */
	public void clearVisuals() {
		routePainter.clearRoute();
		routePainter.removeBounds();
		waypointPainter.clearWaypoints();
	}

	/**
	 * Clears the result of the current test if nothing is currently loaded.
	 */
	public void clearResult() {
		if (loadLock) {
			return;
		}

		if (darpThread.isAlive()) {
			TestController current = testManagerController.getCurrentController();

			if (current != null) {
				// make sure that the new result being calculated is not saved
				invalidResults.add(current.getModel());
			}
		}

		// clear result
		resultController.setResultModel(new ResultModel());

		updateView();
	}

	/**
	 * Updates the entire view.
	 */
	public void updateView() {
		view.revalidate();
		view.repaint();
	}

	/**
	 * Lets the user create a test file and saves all the tests in it.
	 */
	public void saveTests() {
		String path = selectFile(true);

		// no file selected
		if (path.isEmpty()) {
			return;
		}

		// add correct file ending if necessary
		if (!path.endsWith(".ser")) {
			path += ".ser";
		}

		List<TestModel> tests = testManagerController.getTests();

		try {
			FileOutputStream fileOut = new FileOutputStream(path);
			ObjectOutputStream out = new ObjectOutputStream(fileOut);

			out.writeObject(tests);

			out.close();
			fileOut.close();
		} catch (IOException i) {
			Log.getLogger().info("Could not save tests in " + path);
			i.printStackTrace();
		}
	}

	/**
	 * Lets the user select a test file and loads all the tests in it.
	 */
	@SuppressWarnings("unchecked")
	public void loadTests() {
		String path = selectFile(false);

		// no file selected
		if (path.isEmpty()) {
			return;
		}

		List<TestModel> tests;

		try {
			FileInputStream fileIn = new FileInputStream(path);
			ObjectInputStream in = new ObjectInputStream(fileIn);

			tests = (List<TestModel>) in.readObject();

			in.close();
			fileIn.close();
		} catch (IOException | ClassNotFoundException e) {
			Log.getLogger().info("Could not load tests from " + path);
			e.printStackTrace();
			return;
		}

		// set and load tests
		testManagerController.setTests(tests);
	}

	/**
	 * Lets the user select or create a test file and return the absolute file path.
	 * @param save <code> true </code> if the user should create a file, <code> false </code> if the user should select a file
	 * @return the absolute file path
	 */
	private String selectFile(boolean save) {
		String path = "";

		JFileChooser chooser = new JFileChooser();

		// only accept files that match the format
		chooser.setAcceptAllFileFilterUsed(false);
		chooser.setFileFilter(new FileNameExtensionFilter("*.ser", "ser"));

		// create or select?
		int result = save ? chooser.showSaveDialog(view) : chooser.showOpenDialog(view);

		// only save the path if the user approves
		if (result == JFileChooser.APPROVE_OPTION) {
			path = chooser.getSelectedFile().getAbsolutePath();
		}

		return path;
	}

	/**
	 * @return the RoutePainter
	 */
	public RoutePainter getRoutePainter() {
		return routePainter;
	}

	/**
	 * @return the WaypointPainter
	 */
	public WaypointPainter<MyWaypoint> getWaypointPainter() {
		return waypointPainter;
	}

	/**
	 * @param loadLock <code> true </code> if loadLock should be enabled
	 */
	public void setLoadLock(boolean loadLock) {
		this.loadLock = loadLock;
	}
}