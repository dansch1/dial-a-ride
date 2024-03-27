package darp.swing;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/**
 * Tests the JNumberField class.
 * @author Daniel Schröder
 */
public class JNumberFieldTest {

	private static JNumberField numberField;

	@BeforeAll
	public static void setup() {
		DecimalNumberFilter df = new DecimalNumberFilter();
		df.setMaxLength(15);

		numberField = new JNumberField("0", 10, 0, df);
	}

	@ParameterizedTest
	@CsvSource({ "0, 0", "1, 1", "00, 0", "11, 11", "01, 1", "10, 10", "000, 0", "-0, 0", "-1, -1", "-00, 0", "-11, -11", "-01, -1", "-10, -10",
			"-000, 0", "012300, 12300", "00100200, 100200", "102003000, 102003000", "-012300, -12300", "-00100200, -100200", "-102003000, -102003000",
			"0.0, 0", "0.00, 0", "-0.0, 0", "-0.00, 0", "-00.00, 0", "0.000, 0", "0.1000, 0.1", "0.1230012300, 0.12300123", "-0.1000, -0.1",
			"-0.1230012300, -0.12300123", "00102003040.0010200, 102003040.001", "-00102003040.0010200, -102003040" })
	public void formatTest(String text, String expected) {
		assertEquals(numberField.format(text), expected);
	}
}