package darp.controllers;

import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.jxmapviewer.viewer.GeoPosition;

import darp.jxmapviewer2.MyWaypoint;
import darp.jxmapviewer2.RoutePainter;
import darp.jxmapviewer2.WaypointPainter;
import darp.models.RequestsModel;
import darp.models.TestModel;
import darp.utils.Utilities;
import darp.utils.WaypointNFTuple;
import darp.views.CustomerRequestView;
import darp.views.TestView;
import darp.views.VehicleRequestView;

/**
 * The controller for a {@link TestView}.
 * @author Daniel Schröder
 */
public class TestController {

	private DarpController darpController;
	private TestView view;
	private TestModel model;

	private RoutePainter routePainter;
	private WaypointPainter<MyWaypoint> waypointPainter;

	private boolean boundsEnabled;
	private List<CustomerRequestView> customerRequests;
	private WaypointNFTuple selected;

	/**
	 * Creates a new controller for a new TestView.
	 * @param darpController the DarpController
	 * @param model the test to be loaded
	 * @throws NullPointerException if null is passed
	 */
	public TestController(DarpController darpController, TestModel model) throws NullPointerException {
		this(darpController, new TestView(), model);
	}

	/**
	 * Creates a new controller for a given TestView.
	 * @param darpController the DarpController
	 * @param view the TestView
	 * @param model the test to be loaded
	 * @throws NullPointerException if null is passed
	 */
	public TestController(DarpController darpController, TestView view, TestModel model) throws NullPointerException {
		this.darpController = Objects.requireNonNull(darpController);
		this.view = Objects.requireNonNull(view);
		this.model = Objects.requireNonNull(model);

		routePainter = darpController.getRoutePainter();
		waypointPainter = darpController.getWaypointPainter();
		customerRequests = new ArrayList<>();
	}

	/**
	 * Initializes the controller.
	 */
	public void initController() {
		setRequests(model.getRequestsModel());

		setupBoundsSection();
		setupRequestSection();
	}

	/**
	 * Adds all the functionality for the bounds section.
	 */
	private void setupBoundsSection() {
		view.getNumNorth().addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				updateBounds();
			}
		});
		view.getNumWest().addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				updateBounds();
			}
		});
		view.getNumEast().addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				updateBounds();
			}
		});
		view.getNumSouth().addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				updateBounds();
			}
		});

		view.getBtnSelectBounds().addActionListener(e -> darpController.selectBounds());
		view.getBtnToggleBounds().addActionListener(e -> setBoundsEnabled(!boundsEnabled));
		view.getBtnRandomize().addActionListener(e -> fillWithRandomLocations());
	}

	/**
	 * Sets whether the bounding box should be visible.
	 * @param enabled <code> true </code> if the bounding box should be visible
	 */
	private void setBoundsEnabled(boolean enabled) {
		this.boundsEnabled = enabled;
		view.getBtnToggleBounds().setText(boundsEnabled ? "Aus" : "An");
		updateBounds();
	}

	/**
	 * Sets or removes the bounding box based on the current settings.
	 */
	private void updateBounds() {
		if (!boundsEnabled || !validBounds()) {
			routePainter.removeBounds();
		} else {
			GeoPosition nw = view.getNorthWestTuple().getGeoPos();
			GeoPosition se = view.getSouthEastTuple().getGeoPos();

			routePainter.setBounds(nw, se);
		}

		darpController.updateView();
	}

	/**
	 * @return <code> true </code> if the selected bounding box is valid
	 */
	private boolean validBounds() {
		return view.getNumNorth().getDoubleValue() > view.getNumSouth().getDoubleValue() &&
				view.getNumWest().getDoubleValue() < view.getNumEast().getDoubleValue();
	}

	/**
	 * Sets every start and destination position to a random position within the bounding box.
	 * Of course only if a valid bounding box has been selected.
	 */
	private void fillWithRandomLocations() {
		// if no valid bounding box is selected inform the user
		if (!validBounds()) {
			JOptionPane.showMessageDialog(SwingUtilities.getRoot(view), "Der angegebenen Bereich ist ungültig.", "Ungültiger Bereich",
					JOptionPane.INFORMATION_MESSAGE);
			return;
		}

		// random vehicle start position
		GeoPosition vStart = randomPositionInBounds();
		view.getVehicleRequest().getStartTuple().setGeoPos(vStart);

		// random customer positions
		for (CustomerRequestView cr : customerRequests) {
			GeoPosition cStart = randomPositionInBounds();
			GeoPosition cDest = randomPositionInBounds();

			cr.getStartTuple().setGeoPos(cStart);
			cr.getDestTuple().setGeoPos(cDest);
		}

		darpController.clearResult();
	}

	/**
	 * @return a random GeoPosition within the selected bounding box
	 */
	private GeoPosition randomPositionInBounds() {
		GeoPosition nw = view.getNorthWestTuple().getGeoPos();
		GeoPosition se = view.getSouthEastTuple().getGeoPos();

		double lat = Utilities.getRandomValueBetween(se.getLatitude(), nw.getLatitude());
		double lon = Utilities.getRandomValueBetween(nw.getLongitude(), se.getLongitude());

		return new GeoPosition(lat, lon);
	}

	/**
	 * Adds the vehicle request to the request section
	 * and adds the functionality for the add request button.
	 */
	private void setupRequestSection() {
		setupStartPos(view.getVehicleRequest());
		view.getBtnAddRequest().addActionListener(e -> addRequest(true));
	}

	/**
	 * Adds all the functionality for the given VehicleRequestView.
	 * @param view the VehicleRequestView
	 */
	private void setupStartPos(VehicleRequestView view) {
		view.getNumStartLat().addChangeListener(() -> darpController.clearResult());
		view.getNumStartLon().addChangeListener(() -> darpController.clearResult());

		view.getNumStartLat().addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				updateWaypointPos(view.getStartTuple());
			}
		});
		view.getNumStartLon().addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				updateWaypointPos(view.getStartTuple());
			}
		});

		view.getBtnSetStart().addActionListener(e -> selectGeoPosition(view.getStartTuple()));
	}

	/**
	 * Adds all the functionality for the given CustomerRequestView.
	 * @param view the CustomerRequestView
	 */
	private void setupDestPos(CustomerRequestView view) {
		view.getNumDestLat().addChangeListener(() -> darpController.clearResult());
		view.getNumDestLon().addChangeListener(() -> darpController.clearResult());

		view.getNumDestLat().addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				updateWaypointPos(view.getDestTuple());
			}
		});
		view.getNumDestLon().addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				updateWaypointPos(view.getDestTuple());
			}
		});

		view.getBtnSetDest().addActionListener(e -> selectGeoPosition(view.getDestTuple()));
		view.getBtnRemove().addActionListener(e -> removeRequest(view, true));
	}

	/**
	 * Updates the GeoPosition of the given tuple.
	 * @param tuple the WaypointNFTuple
	 */
	private void updateWaypointPos(WaypointNFTuple tuple) {
		tuple.updateWaypointPos();
		darpController.updateView();
	}

	/**
	 * Adds a new customer request to the view.
	 * @param addWaypoints <code> true </code> if waypoints should be added
	 * @return the created CustomerRequestView
	 */
	private CustomerRequestView addRequest(boolean addWaypoints) {
		String label = "K" + (customerRequests.size() + 1);
		CustomerRequestView cr = new CustomerRequestView(label);

		setupStartPos(cr);
		setupDestPos(cr);

		if (addWaypoints) {
			waypointPainter.addWaypoint(cr.getStartTuple().getWaypoint());
			waypointPainter.addWaypoint(cr.getDestTuple().getWaypoint());
		}

		customerRequests.add(cr);
		// allow the user to delete the first request if there are at least 2 requests
		customerRequests.get(0).getBtnRemove().setEnabled(customerRequests.size() > 1);
		view.getRequestPanel().add(cr);

		darpController.clearResult();

		// scroll to bottom
		view.getVerticalScrollBar().setValue(view.getVerticalScrollBar().getMaximum());

		return cr;
	}

	/**
	 * Removes the given customer request from the view.
	 * @param cr the customer request
	 * @param removeWaypoints <code> true </code> if waypoints should be removed
	 */
	private void removeRequest(CustomerRequestView cr, boolean removeWaypoints) {
		int index = customerRequests.indexOf(cr);

		// element not in list
		if (index == -1) {
			return;
		}

		if (removeWaypoints) {
			waypointPainter.removeWaypoint(cr.getStartTuple().getWaypoint());
			waypointPainter.removeWaypoint(cr.getDestTuple().getWaypoint());
		}

		customerRequests.remove(cr);

		if (!customerRequests.isEmpty()) {
			// do not allow the user to delete the last request
			customerRequests.get(0).getBtnRemove().setEnabled(customerRequests.size() > 1);
		}

		view.getRequestPanel().remove(cr);
		relabelRequests(index);

		darpController.clearResult();
	}

	/**
	 * Relabels all the requests with their correct indices.
	 * It starts with the last request and stops at the given index.
	 * @param stopAt the last request to relabel
	 */
	private void relabelRequests(int stopAt) {
		for (int i = customerRequests.size() - 1; i >= stopAt; i--) {
			CustomerRequestView cr = customerRequests.get(i);
			String newLabel = "K" + (i + 1);

			cr.getLblLabel().setText(newLabel + ":");
			cr.getStartTuple().getWaypoint().setLabel(newLabel + "S");
			cr.getDestTuple().getWaypoint().setLabel(newLabel + "Z");
		}
	}

	/**
	 * Lets the user select a GeoPosition on the map for the given WaypointNFTuple.
	 * @param selected the tuple
	 */
	private void selectGeoPosition(WaypointNFTuple selected) {
		this.selected = selected;
		darpController.selectGeoPosition();
	}

	/**
	 * Sets the GeoPosition for the selected WaypointNFTuple.
	 * @param gp the GeoPosition
	 * @throws NullPointerException if gp is null
	 */
	public void setGeoPosition(GeoPosition gp) throws NullPointerException {
		selected.setGeoPos(Objects.requireNonNull(gp));
		darpController.clearResult();
	}

	/**
	 * Sets the bounding box.
	 * @param nw the north west GeoPosition of the bounding box
	 * @param se the south east GeoPosition of the bounding box
	 * @throws NullPointerException if null is passed
	 */
	public void setBounds(GeoPosition nw, GeoPosition se) throws NullPointerException {
		view.getNorthWestTuple().setGeoPos(Objects.requireNonNull(nw));
		view.getSouthEastTuple().setGeoPos(Objects.requireNonNull(se));
		updateBounds();
	}

	/**
	 * Loads the bounding box and all waypoints to the map.
	 */
	public void loadVisuals() {
		updateBounds();

		// vehicle waypoint
		waypointPainter.addWaypoint(view.getVehicleRequest().getStartTuple().getWaypoint());

		// customer waypoints
		for (CustomerRequestView cr : customerRequests) {
			waypointPainter.addWaypoint(cr.getStartTuple().getWaypoint());
			waypointPainter.addWaypoint(cr.getDestTuple().getWaypoint());
		}
	}

	/**
	 * @return the RequestsModel representing the view
	 */
	public RequestsModel getRequestsModel() {
		RequestsModel requests = new RequestsModel();

		requests.setTestName(view.getTxtTestName().getText());

		requests.setNorthWestPos(view.getNorthWestTuple().getGeoPos());
		requests.setSouthEastPos(view.getSouthEastTuple().getGeoPos());

		requests.setBoundsEnabled(boundsEnabled);

		// remove all waypoints before adding new ones
		requests.getStartWaypoints().clear();
		requests.getDestWaypoints().clear();

		// vehicle waypoint
		requests.getStartWaypoints().add(view.getVehicleRequest().getStartTuple().getWaypoint());

		// customer waypoints
		for (CustomerRequestView cr : customerRequests) {
			requests.getStartWaypoints().add(cr.getStartTuple().getWaypoint());
			requests.getDestWaypoints().add(cr.getDestTuple().getWaypoint());
		}

		return requests;
	}

	/**
	 * @param requests the RequestsModel to be set by the view
	 * @throws NullPointerException if requests is null
	 */
	public void setRequests(RequestsModel requests) throws NullPointerException {
		if (requests == null) {
			throw new NullPointerException("RequestsModel can't be null");
		}

		darpController.setLoadLock(true);

		view.getTxtTestName().setText(requests.getTestName());

		view.getNorthWestTuple().setGeoPos(requests.getNorthWestPos());
		view.getSouthEastTuple().setGeoPos(requests.getSouthEastPos());

		setBoundsEnabled(requests.isBoundsEnabled());

		// remove all requests before adding new ones
		removeAllRequests();

		// the vehicle request
		WaypointNFTuple start = view.getVehicleRequest().getStartTuple();
		start.setWaypoint(requests.getStartWaypoints().get(0));

		// the customer requests
		for (int i = 0; i < requests.getDestWaypoints().size(); i++) {
			CustomerRequestView cr = addRequest(false);

			cr.getStartTuple().setWaypoint(requests.getStartWaypoints().get(i + 1));
			cr.getDestTuple().setWaypoint(requests.getDestWaypoints().get(i));
		}

		darpController.setLoadLock(false);
	}

	/**
	 * Removes all customer requests from the view.
	 */
	private void removeAllRequests() {
		for (int i = customerRequests.size() - 1; i >= 0; i--) {
			removeRequest(customerRequests.get(i), false);
		}
	}

	/**
	 * @return the TestView
	 */
	public TestView getView() {
		return view;
	}

	/**
	 * @return the TestModel
	 */
	public TestModel getModel() {
		return model;
	}
}