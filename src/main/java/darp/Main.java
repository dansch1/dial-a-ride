package darp;

import java.awt.EventQueue;

import darp.controllers.SettingsController;
import darp.utils.Log;
import darp.views.SettingsView;

/**
 * Class containing the {@link #main(String[])} method
 * called by the JVM upon execution of the program.
 * @author Daniel Schröder
 */
public class Main {

	/**
	 * Main method called by the JVM.
	 * @param args ignored
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					// create settings view and controller
					SettingsView v = new SettingsView();
					SettingsController c = new SettingsController(v);
					// initialize controller
					c.initController();
				} catch (Exception e) {
					Log.getLogger().info("Could not start the application");
					e.printStackTrace();
				}
			}
		});
	}
}