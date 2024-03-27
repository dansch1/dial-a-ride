package darp.controllers;

import java.util.Objects;

import darp.models.ParametersModel;
import darp.utils.Utilities;
import darp.views.ParametersView;

/**
 * The controller for a {@link ParametersView}.
 * @author Daniel Schröder
 */
public class ParametersController {

	private DarpController darpController;
	private ParametersView view;

	/**
	 * Creates new controller for a given ParametersView.
	 * @param darpController the DarpController
	 * @param view the ParametersView
	 * @throws NullPointerException if null is passed
	 */
	public ParametersController(DarpController darpController, ParametersView view) throws NullPointerException {
		this.darpController = Objects.requireNonNull(darpController);
		this.view = Objects.requireNonNull(view);
	}

	/**
	 * Initializes the controller.
	 */
	public void initController() {
		setupNumberFields();
		setupSlider();
		setupCheckBox();
	}

	/**
	 * Adds the functionality for the JNumberFields.
	 */
	private void setupNumberFields() {
		view.getNumCapacity().addChangeListener(() -> darpController.clearResult());
		view.getNumMPS().addChangeListener(() -> darpController.clearResult());
		view.getNumTimePref().addChangeListener(() -> darpController.clearResult());
	}

	/**
	 * Adds the functionality for the JSlider.
	 */
	private void setupSlider() {
		view.getSliderWeights().addChangeListener(e -> updateSlider());
	}

	/**
	 * Updates the labels of the weight slider.
	 */
	private void updateSlider() {
		double percent = view.getSliderWeights().getValue() / 100d;

		view.getLblWeight1().setText("w1: " + Utilities.format(1 - percent, 2));
		view.getLblWeight2().setText("w2: " + Utilities.format(percent, 2));

		darpController.clearResult();
	}

	/**
	 * Adds the functionality for the routing check box.
	 */
	private void setupCheckBox() {
		view.getCbRouting().addItemListener(e -> darpController.clearResult());
	}

	/**
	 * @return the ParametersModel representing the view
	 */
	public ParametersModel getParametersModel() {
		ParametersModel parameters = new ParametersModel();

		parameters.setCapacity(view.getNumCapacity().getIntValue());
		parameters.setMPS(view.getNumMPS().getIntValue());
		parameters.setTimePref(view.getNumTimePref().getDoubleValue());
		parameters.setWeight(view.getSliderWeights().getValue());
		parameters.setRouting(view.getCbRouting().isSelected());

		return parameters;
	}

	/**
	 * @param parameters the ParametersModel to be set by the view
	 * @throws NullPointerException if parameters is null
	 */
	public void setParametersModel(ParametersModel parameters) throws NullPointerException {
		if (parameters == null) {
			throw new NullPointerException("ParametersModel can't be null");
		}

		view.getNumCapacity().setIntValue(parameters.getCapacity());
		view.getNumMPS().setIntValue(parameters.getMPS());
		view.getNumTimePref().setDoubleValue(parameters.getTimePref());
		view.getSliderWeights().setValue(parameters.getWeight());
		view.getCbRouting().setSelected(parameters.isRouting());
	}
}