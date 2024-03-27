package darp.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.swing.JLabel;
import javax.swing.JPanel;

import darp.models.TestModel;
import darp.views.TestManagerView;

/**
 * The controller for a {@link TestManagerView}.
 * @author Daniel Schröder
 */
public class TestManagerController {

	private DarpController darpController;
	private TestManagerView view;

	private JPanel testPanel;
	private JLabel lblPage;

	private List<TestController> controllers;
	private int current;

	/**
	 * Creates new controller for a given TestManagerView.
	 * @param darpController the DarpController
	 * @param view the TestManagerView
	 * @throws NullPointerException if null is passed
	 */
	public TestManagerController(DarpController darpController, TestManagerView view) throws NullPointerException {
		this.darpController = Objects.requireNonNull(darpController);
		this.view = Objects.requireNonNull(view);

		testPanel = view.getTestPanel();
		lblPage = view.getLblPage();

		controllers = new ArrayList<>();
		current = -1;
	}

	/**
	 * Initializes the controller.
	 */
	public void initController() {
		setupControlButtons();
		setupCRUDButtons();

		addNewController();
	}

	/**
	 * Adds the functionality for the control buttons.
	 */
	private void setupControlButtons() {
		view.getBtnFirst().addActionListener(e -> first());
		view.getBtnPrev().addActionListener(e -> previous());
		view.getBtnNext().addActionListener(e -> next());
		view.getBtnLast().addActionListener(e -> last());
	}

	/**
	 * Sets the first controller in the list
	 * as the new current controller.
	 */
	private void first() {
		if (current > 0) {
			switchController(0);
		}
	}

	/**
	 * Sets the previous controller in the list
	 * as the new current controller.
	 */
	private void previous() {
		if (current > 0) {
			switchController(current - 1);
		}
	}

	/**
	 * Sets the next controller in the list
	 * as the new current controller.
	 */
	private void next() {
		if (current < controllers.size() - 1) {
			switchController(current + 1);
		}
	}

	/**
	 * Sets the last controller in the list
	 * as the new current controller.
	 */
	private void last() {
		if (current < controllers.size() - 1) {
			switchController(controllers.size() - 1);
		}
	}

	/**
	 * Switches the current controller with the controller at the given index.
	 * @param index the index of the new current controller
	 */
	private void switchController(int index) {
		TestController controller = controllers.get(current);
		darpController.saveTest(controller); // save changes

		// remove from panel and clear visuals
		testPanel.remove(controller.getView());
		darpController.clearVisuals();

		current = index;
		controller = controllers.get(current);

		// add to panel and load visuals
		testPanel.add(controller.getView());
		darpController.loadTest(controller);

		updateView();
	}

	/**
	 * Adds the functionality for the remove/add and load/save buttons.
	 */
	private void setupCRUDButtons() {
		view.getBtnRemove().addActionListener(e -> removeCurrentComponent());
		view.getBtnAdd().addActionListener(e -> addNewController());
		view.getBtnLoad().addActionListener(e -> darpController.loadTests());
		view.getBtnSave().addActionListener(e -> darpController.saveTests());
	}

	/**
	 * Creates a new controller
	 * and sets it as the new current controller.
	 */
	private void addNewController() {
		addNewController(new TestModel());
	}

	/**
	 * Creates a new controller with a given test
	 * and sets it as the new current controller.
	 * @param test the model for the controller
	 */
	private void addNewController(TestModel test) {
		TestController controller;

		// if there is a controller active
		if (current >= 0) {
			controller = controllers.get(current);
			// save changes
			darpController.saveTest(controller);
			// and remove from panel
			testPanel.remove(controller.getView());
		}

		// for safety reasons clear visuals either way
		darpController.clearVisuals();

		// create and initialize new controller
		controller = new TestController(darpController, test);
		controller.initController();

		// and add it to the list
		controllers.add(controller);
		current = controllers.size() - 1;
		view.getBtnRemove().setEnabled(controllers.size() > 1);

		// add to panel and load visuals
		testPanel.add(controller.getView());
		darpController.loadTest(controller);

		updateView();
	}

	/**
	 * Removes the current controller and sets the next
	 * controller in the list as the new current controller.
	 * If there is no next controller the previous controller is loaded.
	 * If there is also no previous controller nothing will be loaded.
	 */
	private void removeCurrentComponent() {
		if (current < 0) {
			return;
		}

		TestController controller = controllers.get(current);
		// remove from panel and clear visuals
		testPanel.remove(controller.getView());
		darpController.clearVisuals();

		// and remove it from the list
		controllers.remove(current);
		current = Math.min(current, controllers.size() - 1);
		view.getBtnRemove().setEnabled(controllers.size() > 1);

		// if there is a controller left
		if (current >= 0) {
			controller = controllers.get(current);
			// add to panel and load visuals
			testPanel.add(controller.getView());
			darpController.loadTest(controller);
		}

		updateView();
	}

	/**
	 * Updates the page and the main view.
	 */
	private void updateView() {
		lblPage.setText((current + 1) + "/" + controllers.size());
		darpController.updateView();
	}

	/**
	 * Returns all tests of the controllers.
	 * The current test is saved.
	 * @return the list of tests
	 */
	public List<TestModel> getTests() {
		List<TestModel> tests = new ArrayList<>();

		// save current test
		if (current >= 0) {
			darpController.saveTest(controllers.get(current));
		}

		for (TestController c : controllers) {
			tests.add(c.getModel());
		}

		return tests;
	}

	/**
	 * Creates and initializes a new controller for each given test in the list.
	 * All previous controllers are removed.
	 * The last controller created will be the new current controller.
	 * @param tests the tests
	 * @throws NullPointerException if tests is null
	 */
	public void setTests(List<TestModel> tests) throws NullPointerException {
		if (tests == null) {
			throw new NullPointerException("Tests can't be null");
		}

		// clear controller
		clear();

		// for each test create and initializes a new controller
		for (TestModel t : tests) {
			addNewController(t);
		}

		// load and set the current controller
		if (current >= 0) {
			darpController.loadTest(controllers.get(current));
		}

		updateView();
	}

	/**
	 * Clears all previous controllers.
	 */
	private void clear() {
		testPanel.removeAll();
		darpController.clearVisuals();
		controllers.clear();
		current = -1;
	}

	/**
	 * @return the index of the current TestController
	 */
	public int getIndexOfCurrent() {
		return current;
	}

	/**
	 * @return the current TestController
	 */
	public TestController getCurrentController() {
		return current < 0 ? null : controllers.get(current);
	}
}