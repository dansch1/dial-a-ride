package darp.swing;

/**
 * A custom NumberFilter that only allows decimal numbers.
 * @author Daniel Schröder
 */
public class DecimalNumberFilter extends NumberFilter {

	/**
	 * Creates a custom NumberFilter that only allows decimal numbers with a maximum length of 10.
	 */
	public DecimalNumberFilter() {
		// the string must match the following pattern:
		// an optional (-?) sign followed by any number of digits ([0-9]*)
		// and optional a decimal point followed again by any number of digits (([0-9]\\.[0-9]*)?)
		// (there must be a digit before the decimal point)
		// note all of the above parts are optional so strings like "0.", "-" or even an empty string are considered to be valid
		// this is intended for editing convenience and must be fixed by the textField
		super("-?[0-9]*([0-9]\\.[0-9]*)?"); // valid = -?([1-9][0-9]*|0)(\\.[0-9]*[1-9])?
	}
}