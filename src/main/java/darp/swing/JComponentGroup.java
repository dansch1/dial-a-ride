package darp.swing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import javax.swing.JComponent;

/**
 * A class to logically group JComponents.
 * @author Daniel Schröder
 */
public class JComponentGroup {

	private List<JComponent> components;

	/**
	 * Creates a new component group.
	 */
	public JComponentGroup() {
		components = new ArrayList<>();
	}

	/**
	 * Creates a new component group with the given components.
	 * @param components the JComponents to be added to the group
	 * @throws NullPointerException if components is null
	 */
	public JComponentGroup(JComponent... components) throws NullPointerException {
		this.components = Arrays.asList(Objects.requireNonNull(components));
	}

	/**
	 * Adds a JComponent to the group.
	 * @param component the JComponent to be added
	 * @throws NullPointerException if component is null
	 */
	public void add(JComponent component) throws NullPointerException {
		components.add(Objects.requireNonNull(component));
	}

	/**
	 * Removes a JComponent from the group.
	 * @param component the JComponent to be removed
	 * @return <code> true </code> if the JComponent was removed successfully
	 */
	public boolean remove(JComponent component) {
		return components.remove(component);
	}

	/**
	 * Sets whether or not each JComponent of the group is enabled.
	 * @param enabled <code> true </code> if the JComponents should be enabled
	 */
	public void setEnabled(boolean enabled) {
		for (JComponent c : components) {
			c.setEnabled(enabled);
		}
	}
}