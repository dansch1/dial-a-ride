package darp.algorithm;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.jxmapviewer.viewer.GeoPosition;

import darp.jxmapviewer2.MyWaypoint;
import darp.models.ParametersModel;
import darp.models.RequestsModel;
import darp.models.TestModel;

/**
 * Tests the Psaraftis class.
 * @author Daniel Schröder
 */
public class PsaraftisTest {

	private static DarpSolver darpSolver;
	private static Map<TestModel, List<MyWaypoint>> expectedResults;

	@BeforeAll
	public static void setup() {
		// just test the algorithm
		darpSolver = new Psaraftis();
		expectedResults = new HashMap<>();

		createTests();
	}

	public static void createTests() {
		// test 0
		TestModel defaultTest = new TestModel();
		RequestsModel defaultRequestsModel = defaultTest.getRequestsModel();

		// there is only one possible solution for one customer, so it must be the optimal solution
		List<MyWaypoint> defaultTestResult = new ArrayList<>(defaultRequestsModel.getStartWaypoints());
		defaultTestResult.addAll(defaultRequestsModel.getDestWaypoints());

		// save the result for the test
		expectedResults.put(defaultTest, defaultTestResult);

		// test 1
		TestModel test1 = new TestModel();
		RequestsModel test1Requests = test1.getRequestsModel();
		ParametersModel test1Paramaters = test1.getParametersModel();

		List<MyWaypoint> test1Starts = Arrays.asList(new MyWaypoint("AS", new GeoPosition(0, 0)), new MyWaypoint("K1S", new GeoPosition(0, 1)),
				new MyWaypoint("K2S", new GeoPosition(0, 2)), new MyWaypoint("K3S", new GeoPosition(0, 3)));
		List<MyWaypoint> test1Dests = Arrays.asList(new MyWaypoint("K1Z", new GeoPosition(0, 6)), new MyWaypoint("K2Z", new GeoPosition(0, 5)),
				new MyWaypoint("K3Z", new GeoPosition(0, 4)));

		// set test waypoints
		test1Requests.setStartWaypoints(test1Starts);
		test1Requests.setDestWaypoints(test1Dests);

		// set test parameters
		test1Paramaters.setCapacity(3);

		// mps = 0 therefore each customer should be picked up and delivered according to their positions in the lists
		List<MyWaypoint> test1Result = new ArrayList<>(test1Starts);
		test1Result.addAll(test1Dests);

		// save the result for the test
		expectedResults.put(test1, test1Result);

		// test 2
		TestModel test2 = new TestModel();
		RequestsModel test2Model = test2.getRequestsModel();
		ParametersModel test2Paramaters = test2.getParametersModel();

		List<MyWaypoint> test2Starts = new ArrayList<>(test1Starts);
		List<MyWaypoint> test2Dests = new ArrayList<>(test1Dests);
		Collections.reverse(test2Dests);

		// set test waypoints
		test2Model.setStartWaypoints(test2Starts);
		test2Model.setDestWaypoints(test2Dests);

		// set test parameters
		test2Paramaters.setCapacity(3);
		test2Paramaters.setMPS(3);

		// no mps or capacity restriction
		// optimal route should be the shortest path
		List<MyWaypoint> test2Result = new ArrayList<>(test2Starts);
		test2Result.addAll(test2Dests);

		// save the result for the test
		expectedResults.put(test2, test2Result);

		// test 3
		TestModel test3 = new TestModel();
		RequestsModel test3Requests = test3.getRequestsModel();

		List<MyWaypoint> test3Starts =
				Arrays.asList(new MyWaypoint("AS", new GeoPosition(89.345, 32.7)), new MyWaypoint("K1S", new GeoPosition(154.9, 2.1)),
						new MyWaypoint("K2S", new GeoPosition(75.356, 55.32)), new MyWaypoint("K3S", new GeoPosition(68.3, 35.65)));
		List<MyWaypoint> test3Dests = Arrays.asList(new MyWaypoint("K1Z", new GeoPosition(13.89, 99.9)),
				new MyWaypoint("K2Z", new GeoPosition(45.23, 6.796)), new MyWaypoint("K3Z", new GeoPosition(25.79, 6.7821)));

		// set test waypoints
		test3Requests.setStartWaypoints(test3Starts);
		test3Requests.setDestWaypoints(test3Dests);

		// mps = 0 and capacity = 1 therefore, all customers should be picked up and delivered one after the other according to their position in the lists
		List<MyWaypoint> test3Result = new ArrayList<>();

		test3Result.add(test3Starts.get(0));

		for (int i = 0; i < test3Dests.size(); i++) {
			test3Result.add(test3Starts.get(i + 1));
			test3Result.add(test3Dests.get(i));
		}

		// save the result for the test
		expectedResults.put(test3, test3Result);
	}

	@Test
	public void getOptRouteTest() {
		// test exceptions
		final TestModel testNull = null;
		assertThrows(NullPointerException.class, () -> darpSolver.getOptRoute(testNull));

		final TestModel testParametersNull = new TestModel();
		testParametersNull.setParametersModel(null);
		assertThrows(NullPointerException.class, () -> darpSolver.getOptRoute(testParametersNull));

		final TestModel testRequestsNull = new TestModel();
		testRequestsNull.setRequestsModel(null);
		assertThrows(NullPointerException.class, () -> darpSolver.getOptRoute(testRequestsNull));

		final TestModel testResultNull = new TestModel();
		testResultNull.setResultModel(null);
		assertThrows(NullPointerException.class, () -> darpSolver.getOptRoute(testResultNull));

		// compare results
		for (Entry<TestModel, List<MyWaypoint>> entry : expectedResults.entrySet()) {
			assertEquals(darpSolver.getOptRoute(entry.getKey()), entry.getValue());
		}
	}
}