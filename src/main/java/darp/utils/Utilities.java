package darp.utils;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.jxmapviewer.viewer.GeoPosition;

import com.graphhopper.util.PointList;

/**
 * Utility class containing useful methods that can be reused across the application.
 * @author Daniel Schröder
 */
public final class Utilities {

	private static Random random = new Random();
	private static DecimalFormat df;

	/**
	 * A private constructor to prevent instantiation.
	 */
	private Utilities() {

	}

	/**
	 * Clamps the value between the minimum and maximum value and returns the result.
	 * @param val the value
	 * @param min the minimum value
	 * @param max the maximum value
	 * @return the clamped value
	 */
	public static double clamp(double val, double min, double max) {
		return Math.max(min, Math.min(max, val));
	}

	/**
	 * Returns a random value between the given minimum and maximum.
	 * @param min the minimum value
	 * @param max the maximum value
	 * @return a random value between the minimum and maximum
	 */
	public static double getRandomValueBetween(double min, double max) {
		return Math.random() * (max - min) + min;
	}

	/**
	 * @return a random color
	 */
	public static Color getRandomColor() {
		float r = random.nextFloat();
		float g = random.nextFloat();
		float b = random.nextFloat();

		return new Color(r, g, b);
	}

	/**
	 * @return a random pleasing color
	 */
	public static Color getRandomPleasingColor() {
		float hue = random.nextFloat();

		// Saturation between 0.1 and 0.3
		float saturation = (random.nextInt(2000) + 1000) / 10000f;
		float luminance = 0.9f;

		return Color.getHSBColor(hue, saturation, luminance);
	}

	/**
	 * /**
	 * Creates a list of GeoPositions from a PointList.
	 * @param pointList the pointList
	 * @return the GeoPositions from the PointList as a list
	 * @throws NullPointerException if pointList is null
	 */
	public static List<GeoPosition> pointListToGeoList(PointList pointList) throws NullPointerException {
		if (pointList == null) {
			throw new NullPointerException("PointList can't be null");
		}

		List<GeoPosition> geoList = new ArrayList<>();

		for (int i = 0; i < pointList.size(); i++) {
			geoList.add(new GeoPosition(pointList.getLatitude(i), pointList.getLongitude(i)));
		}

		return geoList;
	}

	/**
	 * Returns the geo center of the given list of GeoPositions.
	 * An empty list will return a GeoPosition at (0,0).
	 * @param positions the list of GeoPositions
	 * @return the GeoPosition representing the geo center
	 * @throws NullPointerException if positions is null
	 */
	public static GeoPosition computeGeoCenter(List<GeoPosition> positions) throws NullPointerException {
		if (positions == null) {
			throw new NullPointerException("GeoPositions can't be null");
		}

		if (positions.isEmpty()) {
			return new GeoPosition(0, 0);
		}

		double sumLat = 0;
		double sumLon = 0;

		for (GeoPosition pos : positions) {
			sumLat += pos.getLatitude();
			sumLon += pos.getLongitude();
		}

		double avgLat = sumLat / positions.size();
		double avgLon = sumLon / positions.size();

		return new GeoPosition(avgLat, avgLon);
	}

	/**
	 * Converts a given number to a string with specified precision.
	 * @param number the number to be formatted
	 * @param precision the digits after the decimal point
	 * @return the String representing the given number
	 */
	public static String format(double number, int precision) {
		// if the decimal formatter has not yet been initialized
		if (df == null) {
			df = new DecimalFormat();

			// use a point as a separator instead of a comma
			DecimalFormatSymbols sym = DecimalFormatSymbols.getInstance();
			sym.setDecimalSeparator('.');

			df.setDecimalFormatSymbols(sym);
		}

		// set the desired precision
		df.setMaximumFractionDigits(precision);

		return df.format(number);
	}

	/**
	 * Brings up a scrollable dialog that displays a message using a default icon determined by the messageType parameter.
	 * @param parentComponent determines the frame in which the dialog is displayed
	 * @param message the message to display
	 * @param heading the heading to display
	 * @param title the title string for the dialog
	 * @param messageType the type of message to be displayed
	 * @throws NullPointerException if null is passed
	 * @see javax.swing.JOptionPane#showMessageDialog(Component, Object, String, int, javax.swing.Icon)
	 */
	public static void showScrollableMessageDialog(Component parentComponent, String message, String heading, String title, int messageType)
			throws NullPointerException {
		if (parentComponent == null || message == null || heading == null || title == null) {
			throw new NullPointerException("Parameters can't be null");
		}

		// content pane containing the heading and the text area
		JPanel contentPane = new JPanel();
		contentPane.setLayout(new BorderLayout());

		// create the heading
		JLabel lblHeading = new JLabel(heading);
		contentPane.add(lblHeading, BorderLayout.NORTH);

		// create the text area to display the message
		JTextArea txtAreaMessage = new JTextArea(message, 5, 50);
		txtAreaMessage.setEditable(false);

		// wrap a scroll pane around it
		JScrollPane scrollPane = new JScrollPane(txtAreaMessage);
		contentPane.add(scrollPane, BorderLayout.CENTER);

		// display them in a message dialog
		JOptionPane.showMessageDialog(parentComponent, contentPane, title, messageType);
	}
}