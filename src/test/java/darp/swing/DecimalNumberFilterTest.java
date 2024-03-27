package darp.swing;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/**
 * Tests the DecimalNumberFilter class.
 * @author Daniel Schröder
 */
public class DecimalNumberFilterTest {

	private static DecimalNumberFilter df;

	@BeforeAll
	public static void setup() {
		df = new DecimalNumberFilter();
	}

	@ParameterizedTest
	@CsvSource({ "'', true", "0, true", "1, true", "00, true", "11, true", "01, true", "10, true", "000, true", "-, true", "-0, true", "-1, true",
			"-00, true", "-11, true", "-01, true", "-10, true", "-000, true", "012300, true", "00100200, true", "102003000, true", "-012300, true",
			"-00100200, true", "-102003000, true", "0., true", "0.0, true", "0.00, true", "-0., true", "-0.0, true", "-0.00, true", "-00.00, true",
			"0.000, true", "0.1000, true", "0.1230012300, true", "-0.1000, true", "-0.1230012300, true", "00102003040.0010200, true",
			"-00102003040.0010200, true", "a, false", "1b, false", "c1, false", "-d, false", "-1e, false", "-f1, false", "-1.g, false", "-h.1, false",
			"325i235, false", "., false", "-., false", "--, false", "³, false", "§$%&, false" })
	public void validNumberTest(String text, boolean expected) {
		assertEquals(df.validNumber(text), expected);
	}
}