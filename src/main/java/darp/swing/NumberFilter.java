package darp.swing;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.DocumentFilter;

/**
 * A custom DocumentFilter designed to only allow numbers.
 * @author Daniel Schröder
 */
public class NumberFilter extends DocumentFilter {

	private String pattern;
	private int maxLength;

	private List<ChangeListener> listeners;

	/**
	 * Creates a new DocumentFilter that only allows strings that
	 * match the given pattern and are at most 10 characters long.
	 * @param pattern the pattern the string has to match
	 * @throws NullPointerException if pattern is null
	 */
	public NumberFilter(String pattern) throws NullPointerException {
		this(pattern, 10);
	}

	/**
	 * Creates a new DocumentFilter that only allows strings that
	 * match the given pattern and are no longer than the specified maximum length.
	 * @param pattern the pattern the string has to match
	 * @param maxLength the maximum allowed length
	 * @throws NullPointerException if pattern is null
	 */
	public NumberFilter(String pattern, int maxLength) throws NullPointerException {
		this.pattern = Objects.requireNonNull(pattern);
		this.maxLength = maxLength;

		listeners = new ArrayList<>();
	}

	@Override
	public void remove(FilterBypass fb, int offset, int length) throws BadLocationException {
		// build the new string
		Document doc = fb.getDocument();
		StringBuilder sb = new StringBuilder();
		sb.append(doc.getText(0, doc.getLength()));
		sb.delete(offset, offset + length);

		// only apply the changes if the string matches the pattern
		if (validNumber(sb.toString())) {
			super.remove(fb, offset, length);
			notifyListeners();
		}
	}

	@Override
	public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
		replace(fb, offset, 0, string, attr);
	}

	@Override
	public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
		// build the new string
		Document doc = fb.getDocument();
		StringBuilder sb = new StringBuilder();
		sb.append(doc.getText(0, doc.getLength()));
		sb.replace(offset, offset + length, text);

		// only apply the changes if the string matches the pattern
		// and is no longer than the specified maximum length
		if (sb.length() <= maxLength && validNumber(sb.toString())) {
			super.replace(fb, offset, length, text, attrs);
			notifyListeners();
		}
	}

	/**
	 * Checks if the text matches a specific pattern.
	 * @param text the text to be checked
	 * @return <code> true </code> if the text matches the pattern
	 * @throws NullPointerException if text is null
	 */
	public boolean validNumber(String text) throws NullPointerException {
		return Objects.requireNonNull(text).matches(pattern);
	}

	/**
	 * Adds a ChangeListener to this NumberFilter, which is notified when the value changes.
	 * @param listener the ChangeListener to be added
	 * @throws NullPointerException if listener is null
	 */
	public void addChangeListener(ChangeListener listener) throws NullPointerException {
		listeners.add(Objects.requireNonNull(listener));
	}

	/**
	 * Notifies all ChangeListeners that the value has changed.
	 */
	private void notifyListeners() {
		for (ChangeListener l : listeners) {
			l.valueChanged();
		}
	}

	/**
	 * @return the maximum allowed length
	 */
	public int getMaxLength() {
		return maxLength;
	}

	/**
	 * @param maxLength the new maximum allowed length
	 */
	public void setMaxLength(int maxLength) {
		this.maxLength = maxLength;
	}
}