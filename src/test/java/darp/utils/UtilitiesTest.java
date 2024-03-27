package darp.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.jxmapviewer.viewer.GeoPosition;

import com.graphhopper.util.PointList;

/**
 * Tests the Utilities class.
 * @author Daniel Schröder
 */
public class UtilitiesTest {

	@ParameterizedTest
	@CsvSource({ "0, 0, 0, 0", "1, 0, 0, 0, 0", "4, 5, 6, 5", "43, 12, 39, 39", "-1243, 9, 246, 9", "21354, -45, -14, -14", "-5, -4, -2, -4",
			"-2, -167, -85, -85" })
	public void clampTest(double val, double min, double max, double expected) {
		assertEquals(Utilities.clamp(val, min, max), expected, 0.000001);
	}

	@ParameterizedTest
	@CsvSource({ "0, 0", "0, 100", "-346.457, 453.45", "12.041, 12.042", "-75.789, -75.788", "-0.3467, 0.3576" })
	public void getRandomValueBetweenTest(double min, double max) {
		double val = Utilities.getRandomValueBetween(min, max);
		assertTrue(val >= min);
		assertTrue(val <= max);
	}

	@Test
	public void pointListToGeoListTest() {
		// test exception
		Exception exception = assertThrows(NullPointerException.class, () -> Utilities.pointListToGeoList(null));

		String expectedMessage = "PointList can't be null";
		String actualMessage = exception.getMessage();

		// test exception message
		assertTrue(actualMessage.equals(expectedMessage));

		PointList testList = PointList.EMPTY;

		// test empty list
		assertEquals(Utilities.pointListToGeoList(testList), Collections.emptyList());

		// test list with values
		testList = new PointList();
		testList.add(45.787, 32.4647);
		testList.add(34.672, 12.56);
		testList.add(14.09, 79.4245);
		testList.add(1.589, 0);

		List<GeoPosition> expectedList = Arrays.asList(new GeoPosition(45.787, 32.4647), new GeoPosition(34.672, 12.56),
				new GeoPosition(14.09, 79.4245), new GeoPosition(1.589, 0));

		assertEquals(Utilities.pointListToGeoList(testList), expectedList);
	}

	@Test
	public void computeGeoCenterTest() {
		// test exception
		Exception exception = assertThrows(NullPointerException.class, () -> Utilities.computeGeoCenter(null));

		String expectedMessage = "GeoPositions can't be null";
		String actualMessage = exception.getMessage();

		// test exception message
		assertTrue(actualMessage.equals(expectedMessage));

		// test lists
		List<GeoPosition> testList1 = Collections.emptyList();
		List<GeoPosition> testList2 = Arrays.asList(new GeoPosition(12.1, 4.064), new GeoPosition(3.32, 67));
		List<GeoPosition> testList3 = Arrays.asList(new GeoPosition(62.4, 21.447), new GeoPosition(73.2, 5.86), new GeoPosition(74.4, 84.125));
		List<GeoPosition> testList4 = Arrays.asList(new GeoPosition(45.787, 32.465), new GeoPosition(34.672, 12.55), new GeoPosition(14.09, 79.445),
				new GeoPosition(1.589, 0));

		// results for each test list
		Map<List<GeoPosition>, GeoPosition> expectedResults = new HashMap<>();
		expectedResults.put(testList1, new GeoPosition(0, 0));
		expectedResults.put(testList2, new GeoPosition(7.71, 35.532));
		expectedResults.put(testList3, new GeoPosition(70, 37.144));
		expectedResults.put(testList4, new GeoPosition(24.0345, 31.115));

		// compare results
		for (Entry<List<GeoPosition>, GeoPosition> entry : expectedResults.entrySet()) {
			GeoPosition result = Utilities.computeGeoCenter(entry.getKey());

			assertEquals(result.getLatitude(), entry.getValue().getLatitude(), 0.000001);
			assertEquals(result.getLongitude(), entry.getValue().getLongitude(), 0.000001);
		}
	}

	@ParameterizedTest
	@CsvSource({ "0, 0, 0", "235.3573, 0, 235", "53.67, 3, 53.67", "-25.23, 2, -25.23", "-456.48, 1, -456.5", ".5, 2, 0.5", "45.01, 1, 45" })
	public void formatTest(double number, int precision, String result) {
		assertEquals(Utilities.format(number, precision), result);
	}
}