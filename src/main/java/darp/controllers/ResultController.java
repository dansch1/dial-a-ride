package darp.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import darp.models.ResultModel;
import darp.utils.RouteSegment;
import darp.views.ResultView;

/**
 * The controller for a {@link ResultView}.
 * @author Daniel Schröder
 */
public class ResultController {

	private DarpController darpController;
	private ResultView view;

	private List<RouteSegment> route;
	private int frame;

	/**
	 * Creates new controller for a given ResultView.
	 * @param darpController the DarpController
	 * @param view the ResultView
	 * @throws NullPointerException if null is passed
	 */
	public ResultController(DarpController darpController, ResultView view) throws NullPointerException {
		this.darpController = Objects.requireNonNull(darpController);
		this.view = Objects.requireNonNull(view);

		route = new ArrayList<>();
	}

	/**
	 * Initializes the controller.
	 */
	public void initController() {
		setupButtons();
	}

	/**
	 * Adds the functionality for the control buttons.
	 */
	private void setupButtons() {
		view.getBtnFirst().addActionListener(e -> firstFrame());
		view.getBtnPrev().addActionListener(e -> prevFrame());
		view.getBtnNext().addActionListener(e -> nextFrame());
		view.getBtnLast().addActionListener(e -> lastFrame());
	}

	/**
	 * Sets the frame to 0.
	 * The route will not be rendered.
	 */
	private void firstFrame() {
		if (frame != 0) {
			setFrame(0);
		}
	}

	/**
	 * Reduces the frame by 1.
	 * One less segment of the route will be rendered.
	 */
	private void prevFrame() {
		if (frame > 0) {
			setFrame(frame - 1);
		}
	}

	/**
	 * Increases the frame by 1.
	 * One more segment of the route will be rendered.
	 */
	private void nextFrame() {
		if (frame < route.size()) {
			setFrame(frame + 1);
		}
	}

	/**
	 * Sets the frame to the last one possible.
	 * The entire route will be rendered.
	 */
	private void lastFrame() {
		if (frame != route.size()) {
			setFrame(route.size());
		}
	}

	/**
	 * Sets the frame to the new value.
	 * The route will render segments up to this frame (not included).
	 * @param frame the new frame
	 */
	private void setFrame(int frame) {
		this.frame = frame;
		view.getTxtFrame().setText(frame + "/" + route.size());
		darpController.getRoutePainter().stopAt(frame);
		darpController.updateView();
	}

	/**
	 * @return the ResultModel representing the view
	 */
	public ResultModel getResultModel() {
		ResultModel result = new ResultModel();

		result.setRoute(route);
		result.setTotalTime(view.getNumTotal().getLongValue());
		result.setRoutingTime(view.getNumRouting().getLongValue());
		result.setAlgoTime(view.getNumAlgo().getLongValue());
		result.setFrame(frame);

		return result;
	}

	/**
	 * @param result the ResultModel to be set by the view
	 * @throws NullPointerException if result is null
	 */
	public void setResultModel(ResultModel result) throws NullPointerException {
		if (result == null) {
			throw new NullPointerException("ResultModel can't be null");
		}

		setTimes(result);

		route = result.getRoute();
		darpController.getRoutePainter().setRoute(route);
		setFrame(result.getFrame());
	}

	/**
	 * @param result the ResultModel that contains the times that should be set by the view
	 * @throws NullPointerException if result is null
	 */
	public void setTimes(ResultModel result) throws NullPointerException {
		if (result == null) {
			throw new NullPointerException("ResultModel can't be null");
		}

		view.getNumAlgo().setLongValue(result.getAlgoTime());
		view.getNumRouting().setLongValue(result.getRoutingTime());
		view.getNumTotal().setLongValue(result.getTotalTime());
	}
}