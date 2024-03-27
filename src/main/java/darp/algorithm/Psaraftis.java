package darp.algorithm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.jxmapviewer.viewer.GeoPosition;

import darp.jxmapviewer2.MyWaypoint;
import darp.models.ParametersModel;
import darp.models.RequestsModel;
import darp.models.ResultModel;
import darp.models.TestModel;
import darp.utils.Router;

/**
 * An implementation of Harilaos N. Psaraftis dynamic programming solution
 * to the single vehicle many-to-many immediate request dial-a-ride problem (static case only).
 * @author Daniel Schröder
 */
public class Psaraftis implements DarpSolver {

	// used for routing between points
	private Router router;

	// defined by the test
	private List<MyWaypoint> points;
	private int n, c, mps;
	private double a, w1, w2;
	private boolean routing;

	// statistics
	private long totalTime, routingTime, algoTime;

	// used by the algorithm
	private Set<State> feasbl;
	private Map<State, Double> optValue;
	private Map<State, Integer> next;
	private List<MyWaypoint> sequence;

	/**
	 * Creates a new darp solver that cannot use routing.
	 * (Mainly for testing purposes)
	 */
	public Psaraftis() {

	}

	/**
	 * Creates a new darp solver.
	 * @param router a Router to calculate paths between points
	 * @throws NullPointerException if router is null
	 */
	public Psaraftis(Router router) throws NullPointerException {
		this.router = Objects.requireNonNull(router);
	}

	@Override
	public List<MyWaypoint> getOptRoute(TestModel test) throws NullPointerException {
		if (test == null || test.getParametersModel() == null || test.getRequestsModel() == null || test.getResultModel() == null) {
			throw new NullPointerException("Invalid TestModel");
		}

		RequestsModel requests = test.getRequestsModel();
		ParametersModel parameters = test.getParametersModel();

		// the start and destination positions
		// this includes the start position of the vehicle
		// and a start and destination position for each customer
		// therefore the list contains 2N+1 positions
		points = new ArrayList<>();
		points.addAll(requests.getStartWaypoints());
		points.addAll(requests.getDestWaypoints());

		// load the test parameters
		n = requests.getDestWaypoints().size(); // only customers have a destination (and exactly one)
		c = parameters.getCapacity();
		mps = parameters.getMPS();
		a = parameters.getTimePref();
		w2 = parameters.getWeight() / 100d;
		w1 = (1 - w2);
		routing = parameters.isRouting() & (router != null);

		// reset times
		totalTime = 0;
		routingTime = 0;
		algoTime = 0;

		long startTime = System.currentTimeMillis();
		// start the algorithm
		calcOptRoute();
		long stopTime = System.currentTimeMillis();

		// calculate times
		totalTime = stopTime - startTime;
		algoTime = totalTime - routingTime;

		ResultModel result = test.getResultModel();

		// set times
		result.setTotalTime(totalTime);
		result.setRoutingTime(routingTime);
		result.setAlgoTime(algoTime);

		return sequence;
	}

	/**
	 * Calculates the optimal route for the specified values.
	 */
	private void calcOptRoute() {
		// create fields for the solution
		feasbl = new HashSet<>();
		optValue = new HashMap<>();
		next = new HashMap<>();
		sequence = new ArrayList<>();

		// backward recursion is used in the first two steps, so that the following applies to all states:
		// all possible next states of a state are checked before the state itself
		// thus the methods start with the terminal states that have no next state and end with the start states
		// this is important for memory optimization

		// step 1
		screening(new int[n], 0);

		// step 2
		optimization(new int[n], 0);

		// step 3
		identification();
	}

	/**
	 * This method corresponds to the first step of the algorithm.
	 * In this part all possible states are checked for feasibility (using backward recursion).
	 * @param k the array to be used as the status vector
	 * @param index the index from which the array should be filled
	 */
	private void screening(int[] k, int index) {
		if (index >= k.length) {
			checkFeasibilities(k);
			return;
		}

		for (int i = 1; i <= 3; i++) {
			k[index] = i;
			screening(k, index + 1);
		}
	}

	/**
	 * Checks all possible states for a given status vector for feasibility.
	 * Only feasible states get stored in the array feasbl.
	 * @param k the status vector
	 */
	private void checkFeasibilities(int[] k) {
		for (int j = 0; j <= 2 * n; j++) {
			State state = new State(j, k.clone());

			// for memory optimization: only store state if it is feasible
			if (feasibleState(state)) {
				feasbl.add(state);
			}
		}
	}

	/**
	 * This method corresponds to the second step of the algorithm.
	 * In this part the optimal value of all subsequent decisions
	 * from each feasible state till the end of the route is calculated (using backward recursion).
	 * (Measured in terms of the specific objective function (1))
	 * Also for each state the best next stop (which is part of the optimal route)
	 * will be stored in the array next.
	 * @param k the array to be used as the status vector
	 * @param index the index from which the array should be filled
	 */
	private void optimization(int[] k, int index) {
		if (index >= k.length) {
			calculateOptValues(k);
			return;
		}

		for (int i = 1; i <= 3; i++) {
			k[index] = i;
			optimization(k, index + 1);
		}
	}

	/**
	 * Calculates the optimal value for all feasible states for a given status vector.
	 * @param k the status vector
	 */
	private void calculateOptValues(int[] k) {
		for (int j = 0; j <= 2 * n; j++) {
			State state = new State(j, k.clone());

			// calculate optimal value only for feasible states
			if (feasbl.contains(state)) {
				optValue.put(state, getOptimalValue(state));
			}
		}
	}

	/**
	 * This method corresponds to the last step of the algorithm.
	 * In this part the previously filled array next is used
	 * to construct the optimal route. The result is saved in the array sequence.
	 */
	private void identification() {
		// the start position is always the first stop in the sequence
		sequence.add(points.get(0));

		// begin with the start state
		int[] ks = new int[n];
		Arrays.fill(ks, 3);

		State currentState = new State(0, ks);

		// construct optimal route
		while (next.containsKey(currentState) && next.get(currentState) != -1) {
			currentState = getNextState(currentState, next.get(currentState));
			sequence.add(points.get(currentState.l));
		}
	}

	/**
	 * Checks if a given state is feasible or not.
	 * @param state the state to be checked
	 * @return <code> true </code> if the state is feasible
	 */
	private boolean feasibleState(State state) {
		// screen 1
		if (!checkStateConsistency(state)) {
			return false;
		}

		if (state.l == 0) {
			return true;
		}

		// screen 2
		if (!checkVehicleCapacity(state)) {
			return false;
		}

		// screen 3
		if (!checkMPS(state)) {
			return false;
		}

		// screen 4
		List<Integer> nextStops = getNextStops(state);

		if (nextStops.isEmpty()) {
			return true;
		}

		return hasFeasibleNextState(state, nextStops);
	}

	/**
	 * First part of screening.
	 * This method checks the conditions (4)-(6) (the one that applies).
	 * @param state the state to be checked
	 * @return <code> true </code> if the conditions hold
	 */
	private boolean checkStateConsistency(State state) {
		if (state.l == 0) {
			// if L=0 => k[j] = 3 for j = 1,...,N
			for (int j = 1; j <= n; j++) {
				if (state.k[j - 1] != 3) {
					return false;
				}
			}
		} else if (state.l >= 1 && state.l <= n) {
			// if 0<L<N+1 => k[L] = 2
			if (state.k[state.l - 1] != 2) {
				return false;
			}
		} else if (state.l >= n + 1 && state.l <= 2 * n) {
			// if N<L<2N+1 => k[L-N] = 1
			if (state.k[(state.l - n) - 1] != 1) {
				return false;
			}
		}

		return true;
	}

	/**
	 * Second part of screening.
	 * This method checks the conditions (7)-(8) (the one that applies).
	 * @param state the state to be checked
	 * @return <code> true </code> if the conditions hold
	 */
	private boolean checkVehicleCapacity(State state) {
		int x2 = getKsInState(state, 2);

		if (state.l >= 1 && state.l <= n) {
			// if 0<L<N+1 => x2 < C+1
			if (x2 > c) {
				return false;
			}
		}

		if (state.l >= n + 1 && state.l <= 2 * n) {
			// if N<L<2N+1 => x2 < C
			if (x2 > c - 1) {
				return false;
			}
		}

		return true;
	}

	/**
	 * Returns the count of the given number in the given state.
	 * @param state the state
	 * @param status the number
	 * @return the count
	 */
	private int getKsInState(State state, int status) {
		int count = 0;

		for (int i = 0; i < state.k.length; i++) {
			if (state.k[i] == status) {
				count++;
			}
		}

		return count;
	}

	/**
	 * Third part of screening.
	 * This method checks the conditions (9)-(10) (the one that applies).
	 * @param state the state to be checked
	 * @return <code> true </code> if the conditions hold
	 */
	private boolean checkMPS(State state) {
		int x1 = getKsInState(state, 1);

		if (state.l >= 1 && state.l <= n) {
			// if 0<L<N+1 => |L-(x1+x2)| < MPS+1
			int x2 = getKsInState(state, 2);

			if (Math.abs(state.l - (x1 + x2)) > mps) {
				return false;
			}
		}

		if (state.l >= n + 1 && state.l <= 2 * n) {
			// if N<L<2N+1 => |(L-N)-x1| < MPS+1
			if (Math.abs((state.l - n) - x1) > mps) {
				return false;
			}
		}

		return true;
	}

	/**
	 * Returns all possible next stops of a given state.
	 * This corresponds to the set S which is the union
	 * of the sets S3 and S2 defined in (11) and (12).
	 * @param state the state
	 * @return a list of all possible next stops
	 */
	private List<Integer> getNextStops(State state) {
		List<Integer> nextStops = new ArrayList<>();

		for (int i = 1; i <= 2 * n; i++) {
			// S3
			if (i >= 1 && i <= n) {
				if (state.k[i - 1] == 3) {
					nextStops.add(i);
				}
			}

			// S2
			if (i >= n + 1 && i <= 2 * n) {
				if (state.k[(i - n) - 1] == 2) {
					nextStops.add(i);
				}
			}
		}

		return nextStops;
	}

	/**
	 * Checks if a given state has at least one feasible next state.
	 * This is part of the fourth step of screening.
	 * @param state the state to be checked
	 * @param nextStops all the possible next stops of the given state
	 * @return <code> true </code> if the state has at least one feasible next state
	 */
	private boolean hasFeasibleNextState(State state, List<Integer> nextStops) {
		for (int l : nextStops) {
			if (feasbl.contains(getNextState(state, l))) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Returns the next state of a given state at the given stop.
	 * @param state the state
	 * @param l the stop
	 * @return the next state at the given stop
	 */
	private State getNextState(State state, int l) {
		return new State(l, getNextStatusVector(state, l));
	}

	/**
	 * Returns the status vector of the next state
	 * for a given state and a given stop.
	 * This corresponds to definition (13).
	 * @param state the state
	 * @param l the stop
	 * @return the next status vector for the given stop
	 */
	private int[] getNextStatusVector(State state, int l) {
		int[] k = new int[n];

		for (int j = 1; j <= n; j++) {

			if (j == l || j == l - n) {
				// if j = L' or j = L'-N => k'[j] = k[j] - 1
				k[j - 1] = state.k[j - 1] - 1;
			} else {
				// otherwise k'[j] = k[j]
				k[j - 1] = state.k[j - 1];
			}
		}

		return k;
	}

	/**
	 * Returns the optimal value of all subsequent decisions
	 * from a given state till the end of the route.
	 * (Measured in terms of the specific objective function (1))
	 * Also the best next stop (which is part of the optimal route),
	 * will be stored in the array next.
	 * @param state the state
	 * @return the optimal value
	 */
	private double getOptimalValue(State state) {
		// get all possible next stops
		List<Integer> nextStops = getNextStops(state);

		double minVal = 0;
		int bestNextStop = -1;

		for (int l : nextStops) {
			// get the next state at a possible next stop
			State nextState = getNextState(state, l);

			// only consider feasible states
			if (!feasbl.contains(nextState)) {
				continue;
			}

			// the travel time between points
			double t = getDuration(state.l, l);
			// the proportionality factor
			double m = getPropFactor(state);

			// the optimal value for the next state
			double v;

			// only calculate the optimal value for the next state
			// if it has not yet been calculated
			if (optValue.containsKey(nextState)) {
				v = optValue.get(nextState);
			} else {
				// if a backward recursion is used, this does not happen
				v = getOptimalValue(nextState);
				optValue.put(nextState, v);
			}

			// calculate value (14)
			double val = t * m + v;

			// if the value is the current minimum save it and the next stop
			// (or if it is the first value/stop)
			if (bestNextStop == -1 || val < minVal) {
				minVal = val;
				bestNextStop = l;
			}
		}

		// save the best next stop for the state
		next.put(state, bestNextStop);

		return minVal;
	}

	/**
	 * Returns the time it takes the vehicle to get from one point to another.
	 * If Routing is disabled the distance between the points gets returned.
	 * @param l1 the start point
	 * @param l2 the destination point
	 * @return the time or distance
	 */
	private double getDuration(int l1, int l2) {
		return routing ? getTime(l1, l2) : getDistance(l1, l2);
	}

	/**
	 * Returns the time it takes the vehicle to get from one point to another.
	 * @param l1 the start point
	 * @param l2 the destination point
	 * @return the time
	 */
	private double getTime(int l1, int l2) {
		long startTime = System.currentTimeMillis();
		double t = router.getTime(points.get(l1), points.get(l2));
		long stopTime = System.currentTimeMillis();

		routingTime += stopTime - startTime;

		return t;
	}

	/**
	 * Returns the distance between the given points.
	 * @param l1 the first point
	 * @param l2 the second point
	 * @return the distance
	 */
	private double getDistance(int l1, int l2) {
		GeoPosition start = points.get(l1).getPosition();
		GeoPosition dest = points.get(l2).getPosition();

		double dx = start.getLongitude() - dest.getLongitude();
		double dy = start.getLatitude() - dest.getLatitude();

		// the square distance could be used for better performance
		return Math.sqrt(dx * dx + dy * dy);
	}

	/**
	 * Returns the proportionality factor for a given state (15).
	 * @param state the state
	 * @return the proportionality factor
	 */
	private double getPropFactor(State state) {
		int x2 = getKsInState(state, 2);
		int x3 = getKsInState(state, 3);

		return w1 + w2 * (a * x3 + (2 - a) * x2);
	}
}

/**
 * Class representing a state.
 * @author Daniel Schröder
 */
class State {

	public int l;
	public int k[];

	/**
	 * Creates a new state with the given stop and status vector.
	 * @param l the stop
	 * @param k the status vector
	 */
	public State(int l, int k[]) {
		this.l = l;
		this.k = k;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}

		if (obj == null || obj.getClass() != this.getClass()) {
			return false;
		}

		State other = (State) obj;
		return l == other.l && Arrays.equals(k, other.k);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + l;
		result = prime * result + Arrays.hashCode(k);
		return result;
	}
}