package darp.swing;

import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.Objects;

import javax.swing.JTextField;
import javax.swing.text.AbstractDocument;

/**
 * A custom JTextField that only allows numbers.
 * @author Daniel Schröder
 */
public class JNumberField extends JTextField {

	private static final long serialVersionUID = -267579512379404269L;

	private NumberFilter filter;
	private String defaultText;

	/**
	 * Creates a new JTextField that only allows decimal numbers.
	 * @param defaultText the text to be displayed
	 * @param columns the number of columns to use to calculate the preferred width
	 * @param horizontalAlignment the horizontal alignment of the text
	 * @throws NullPointerException if defaultText is null
	 */
	public JNumberField(String defaultText, int columns, int horizontalAlignment) throws NullPointerException {
		this(defaultText, columns, horizontalAlignment, new DecimalNumberFilter());
	}

	/**
	 * Creates a new JTextField with a given filter.
	 * @param defaultText the text to be displayed
	 * @param columns the number of columns to use to calculate the preferred width
	 * @param horizontalAlignment the horizontal alignment of the text
	 * @param filter the NumberFilter that defines which strings are valid
	 * @throws NullPointerException if null is passed
	 */
	public JNumberField(String defaultText, int columns, int horizontalAlignment, NumberFilter filter) throws NullPointerException {
		this.filter = filter;
		this.defaultText = Objects.requireNonNull(defaultText);

		// sets the filter
		((AbstractDocument) getDocument()).setDocumentFilter(Objects.requireNonNull(filter));

		// setup text field
		setColumns(columns);
		setHorizontalAlignment(horizontalAlignment);
		setText(defaultText);

		// format the text after the user has finished editing
		addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				makeItPretty();
			}
		});
	}

	/**
	 * Formats the text contained in this JNumberField.
	 */
	private void makeItPretty() {
		String text = getText();
		String old = text;

		text = format(text);

		// Only apply changes, so that listeners are not notified if nothing has changed
		if (!text.equals(old)) {
			super.setText(text);
		}
	}

	/**
	 * Formats the given text nicely.
	 * @param text the text to format
	 * @return the nicely formatted text
	 * @throws NullPointerException if text is null
	 */
	public String format(String text) throws NullPointerException {
		if (text == null) {
			throw new NullPointerException("Text can't be null");
		}

		// remove characters if the text is to long
		text = text.substring(0, Math.min(text.length(), filter.getMaxLength()));

		// remove trailing zeros:
		// remove decimal point and all following digits if all are zero (\\.0*$) or
		// remove all zeros at the end of the string ((?<=\\.[0-9]*)0+$)
		// (?<=\\.[0-9]*) means only zeros after the decimal point can be removed (600 != 6, 6.00 == 6)
		// and only if there are at the end of the string 0+$
		text = text.replaceFirst("\\.0*$|(?<=\\.[0-9]*)0+$", "");

		// remove leading zeros:
		// remove only zeros that are at the beginning of the string or directly after the sign (?<=^-?)
		// do not remove the last zero before the decimal point/end of the string (?!\\.|$)
		text = text.replaceFirst("(?<=^-?)0+(?!\\.|$)", "");

		// edge cases
		if (text.isEmpty() || text.equals("-")) {
			text = defaultText;
		} else if (text.equals("-0")) {
			text = "0";
		}

		return text;
	}

	/**
	 * @return the integer value represented by the text contained in this JNumberField
	 * @throws NumberFormatException if the string does not contain a parsable integer
	 */
	public int getIntValue() throws NumberFormatException {
		return Integer.parseInt(getText());
	}

	/**
	 * @param value the integer value to be set by this JNumberField
	 */
	public void setIntValue(int value) {
		setText(Integer.toString(value));
	}

	/**
	 * @return the long value represented by the text contained in this JNumberField
	 * @throws NumberFormatException if the string does not contain a parsable long
	 */
	public long getLongValue() throws NumberFormatException {
		return Long.parseLong(getText());
	}

	/**
	 * @param value the long value to be set by this JNumberField
	 */
	public void setLongValue(long value) {
		setText(Long.toString(value));
	}

	/**
	 * @return the double value represented by the text contained in this JNumberField
	 * @throws NumberFormatException if the string does not contain a parsable double
	 */
	public double getDoubleValue() throws NumberFormatException {
		return Double.parseDouble(getText());
	}

	/**
	 * @param value the double value to be set by this JNumberField
	 */
	public void setDoubleValue(double value) {
		setText(Double.toString(value));
	}

	@Override
	public void setText(String t) throws NullPointerException {
		super.setText(format(Objects.requireNonNull(t)));
	}

	/**
	 * Adds a ChangeListener to the underlying NumberFilter, which is notified when the value changes.
	 * @param listener the ChangeListener to be added
	 * @throws NullPointerException if listener is null
	 */
	public void addChangeListener(ChangeListener listener) throws NullPointerException {
		filter.addChangeListener(listener);
	}
}