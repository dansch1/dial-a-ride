package darp.controllers;

import java.awt.event.ItemEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Objects;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;

import darp.models.SettingsModel;
import darp.swing.JComponentGroup;
import darp.utils.Log;
import darp.views.DarpView;
import darp.views.SettingsView;

/**
 * The controller for a {@link SettingsView}.
 * @author Daniel Schröder
 */
public class SettingsController {

	private SettingsView view;

	private final String SETTINGS_PATH = "data/settings.ser";

	private boolean settingsChanged;

	private final double MIN_WIDTH = 0.01, MIN_HEIGHT = 0.01;

	/**
	 * Creates new controller for a given SettingsView.
	 * @param view the SettingsView
	 * @throws NullPointerException if view is null
	 */
	public SettingsController(SettingsView view) throws NullPointerException {
		this.view = Objects.requireNonNull(view);
	}

	/**
	 * Initializes the controller.
	 */
	public void initController() {
		loadSettings();

		setupCheckboxes();
		setupNumberFields();
		setupButtons();
		setupCloseOperation();
	}

	/**
	 * Adds the functionality for the JCheckBoxes.
	 */
	private void setupCheckboxes() {
		view.getCbOffline().addItemListener(e -> toggleJComponentGroup(view.getOSMSection(), e));
		view.getCbCaching().addItemListener(e -> settingsChanged = true);
		view.getCbBounds().addItemListener(e -> toggleJComponentGroup(view.getBoundsSection(), e));
	}

	/**
	 * Depending on whether the item has been selected,
	 * each element of the given group is either enabled or disabled.
	 * @param group the JComponentGroup
	 * @param e the ItemEvent
	 */
	private void toggleJComponentGroup(JComponentGroup group, ItemEvent e) {
		group.setEnabled(e.getStateChange() == ItemEvent.SELECTED);
		settingsChanged = true;
	}

	/**
	 * Adds the functionality for the JNumberFields.
	 */
	private void setupNumberFields() {
		view.getNumNorth().addChangeListener(() -> settingsChanged = true);
		view.getNumWest().addChangeListener(() -> settingsChanged = true);
		view.getNumEast().addChangeListener(() -> settingsChanged = true);
		view.getNumSouth().addChangeListener(() -> settingsChanged = true);
	}

	/**
	 * Adds the functionality for the JButtons.
	 */
	private void setupButtons() {
		view.getBtnRoutingFileChooser().addActionListener(e -> selectFile(view.getTxtRoutingFilePath(), "pbf"));
		view.getBtnMapFileChooser().addActionListener(e -> selectFile(view.getTxtMapFilePath(), "zip"));
		view.getBtnStart().addActionListener(e -> start());
	}

	/**
	 * A JFileChooser gets created that only accepts files that match the given format.
	 * The path of the selected file gets saved in the output.
	 * @param output the JTextField to save the selected file path
	 * @param format the format the file has to match
	 */
	private void selectFile(JTextField output, String format) {
		JFileChooser chooser = new JFileChooser(output.getText());

		// only accept files that match the format
		chooser.setAcceptAllFileFilterUsed(false);
		chooser.setFileFilter(new FileNameExtensionFilter("*." + format, format));

		int result = chooser.showOpenDialog(null);

		// only save path if user approves
		if (result == JFileChooser.APPROVE_OPTION) {
			output.setText(chooser.getSelectedFile().getAbsolutePath());
			output.setCaretPosition(0);
			settingsChanged = true;
		}
	}

	/**
	 * If the settings are valid, they are saved and the application started.
	 */
	private void start() {
		// settings must be valid
		if (!checkBounds() || !checkTextFields()) {
			return;
		}

		// dispose the settings window
		view.dispose();

		// start main application
		DarpView v = new DarpView();
		SettingsModel m = saveSettings();
		DarpController c = new DarpController(v, m);
		c.initController();
	}

	/**
	 * Returns whether the selected bounding box is valid or not.
	 * If the bounding box is not valid the user has still the option to reset it.
	 * @return <code> true </code> if the bounding box is valid after this method
	 */
	private boolean checkBounds() {
		if (!view.getCbBounds().isSelected() || validBounds()) {
			return true;
		}

		// give the user the option to reset it
		int result = JOptionPane.showConfirmDialog(view, "Der angegebenen Bereich ist zu klein. Soll der Bereich zurückgesetzt werden?",
				"Ungültiger Bereich", JOptionPane.YES_NO_OPTION);

		if (result == JOptionPane.YES_OPTION) {
			resetBounds();
			return true;
		}

		// inform the user that the selected bounding box is not valid
		JOptionPane.showMessageDialog(view, "Bitte wählen Sie einen gültigen Bereich aus.", "Bereich auswählen", JOptionPane.INFORMATION_MESSAGE);

		return false;
	}

	/**
	 * @return <code> true </code> if the selected bounding box is valid
	 */
	private boolean validBounds() {
		double vgap = view.getNumNorth().getDoubleValue() - view.getNumSouth().getDoubleValue();
		double hgap = view.getNumEast().getDoubleValue() - view.getNumWest().getDoubleValue();

		return vgap >= MIN_HEIGHT && hgap >= MIN_WIDTH;
	}

	/**
	 * Resets and disables the bounding box.
	 */
	private void resetBounds() {
		view.getCbBounds().setSelected(false);

		view.getNumNorth().setDoubleValue(0);
		view.getNumWest().setDoubleValue(0);
		view.getNumSouth().setDoubleValue(0);
		view.getNumEast().setDoubleValue(0);
	}

	/**
	 * Returns whether the user has selected all of the required files.
	 * @return <code> true </code> if all required files have been selected
	 */
	private boolean checkTextFields() {
		// pbf file is mandatory
		if (view.getTxtRoutingFilePath().getText().isEmpty()) {
			JOptionPane.showMessageDialog(view, "Bitte wählen Sie im Bereich GraphHopper eine Datei aus.", "Datei auswählen",
					JOptionPane.INFORMATION_MESSAGE);
			return false;
		}

		// map files only if offline mode is selected
		if (!view.getCbOffline().isSelected() || !view.getTxtMapFilePath().getText().isEmpty()) {
			return true;
		}

		// give the user the option to disable the offline mode if no map files are selected
		int result = JOptionPane.showConfirmDialog(view,
				"Der Offline-Modus ist aktiviert. Sie müssen eine Datei im OpenStreetMap Bereich auswählen. Oder soll der Offline-Modus ausgeschaltet werden?",
				"Ungültige Datei", JOptionPane.YES_NO_OPTION);

		if (result == JOptionPane.YES_OPTION) {
			view.getCbOffline().setSelected(false);
			return true;
		}

		// inform the user that map files are required for offline mode
		JOptionPane.showMessageDialog(view, "Bitte wählen Sie im Bereich OpenStreetMap eine Datei aus.", "Datei auswählen",
				JOptionPane.INFORMATION_MESSAGE);

		return false;
	}

	/**
	 * Add the close operation for the view.
	 */
	private void setupCloseOperation() {
		view.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent windowEvent) {
				if (!settingsChanged || canCloseWindow()) {
					System.exit(0);
				}
			}
		});
	}

	/**
	 * Checks whether the window can be closed. If there are unsaved changes, the user is asked if they should be saved.
	 * Changes are only saved if they are valid. This means {@link #checkBounds()} and {@link #checkTextFields()} must return true.
	 * The window can only be closed if there have been no changes or they have been saved successfully.
	 * @return <code> true </code> if the window can be closed
	 */
	private boolean canCloseWindow() {
		int result = JOptionPane.showConfirmDialog(view, "Möchten Sie die Änderungen Speichern?", "Änderungen Speichern", JOptionPane.YES_NO_OPTION);

		// do nothing if the user closes the dialog
		if (result == JOptionPane.CLOSED_OPTION) {
			return false;
		}

		if (result == JOptionPane.YES_OPTION) {
			// settings must be valid
			if (!checkBounds() || !checkTextFields()) {
				return false;
			}

			saveSettings();
		}

		return true;
	}

	/**
	 * @return the SettingsModel representing the view
	 */
	private SettingsModel getSettings() {
		SettingsModel settings = new SettingsModel();

		settings.setOffline(view.getCbOffline().isSelected());
		settings.setCaching(view.getCbCaching().isSelected());
		settings.setBoundsEnabled(view.getCbBounds().isSelected());

		settings.setNorthWestPos(view.getNorthWestTuple().getGeoPos());
		settings.setSouthEastPos(view.getSouthEastTuple().getGeoPos());

		settings.setOSMPath(view.getTxtMapFilePath().getText());
		settings.setGHPath(view.getTxtRoutingFilePath().getText());

		return settings;
	}

	/**
	 * @param settings the settings to be set by the view
	 * @throws NullPointerException if settings is null
	 */
	private void setSettings(SettingsModel settings) throws NullPointerException {
		if (settings == null) {
			throw new NullPointerException("SettingsModel can't be null");
		}

		view.getCbOffline().setSelected(settings.isOffline());
		view.getCbCaching().setSelected(settings.isCaching());
		view.getCbBounds().setSelected(settings.isBoundsEnabled());

		view.getNorthWestTuple().setGeoPos(settings.getNorthWestPos());
		view.getSouthEastTuple().setGeoPos(settings.getSouthEastPos());
		// only enable if bounding box is selected
		view.getBoundsSection().setEnabled(settings.isBoundsEnabled());

		view.getTxtMapFilePath().setText(settings.getOSMPath());
		// makes sure that the beginning of the path is shown
		view.getTxtMapFilePath().setCaretPosition(0);

		// only enable if offline mode is selected
		view.getTxtMapFilePath().setEnabled(settings.isOffline());
		view.getBtnMapFileChooser().setEnabled(settings.isOffline());

		view.getTxtRoutingFilePath().setText(settings.getGHPath());
		// makes sure that the beginning of the path is shown
		view.getTxtRoutingFilePath().setCaretPosition(0);
	}

	/**
	 * Saves the settings represented by the view persistently in {@link #SETTINGS_PATH}.
	 * @return the SettingsModel that was saved
	 */
	private SettingsModel saveSettings() {
		// create default settings
		SettingsModel settings = getSettings();

		try {
			FileOutputStream fileOut = new FileOutputStream(SETTINGS_PATH);
			ObjectOutputStream out = new ObjectOutputStream(fileOut);

			out.writeObject(settings);

			out.close();
			fileOut.close();
		} catch (IOException i) {
			Log.getLogger().info("Could not save the new settings in " + SETTINGS_PATH);
			i.printStackTrace();
		}

		return settings;
	}

	/**
	 * Loads and sets the settings saved in {@link #SETTINGS_PATH}.
	 */
	private void loadSettings() {
		SettingsModel settings = new SettingsModel();

		try {
			FileInputStream fileIn = new FileInputStream(SETTINGS_PATH);
			ObjectInputStream in = new ObjectInputStream(fileIn);

			settings = (SettingsModel) in.readObject();

			in.close();
			fileIn.close();
		} catch (IOException | ClassNotFoundException e) {
			Log.getLogger().info("Could not load the settings from " + SETTINGS_PATH + ". Loaded default settings instead");
			e.printStackTrace();
		}

		// set and display settings
		setSettings(settings);
	}
}