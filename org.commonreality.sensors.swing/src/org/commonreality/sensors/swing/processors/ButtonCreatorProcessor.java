package org.commonreality.sensors.swing.processors;

import java.awt.Component;

import javax.swing.JButton;

import org.commonreality.sensors.swing.internal.Coordinates;

public class ButtonCreatorProcessor extends AbstractCreatorProcessor {

	public ButtonCreatorProcessor(Coordinates coordinates) {
		super(coordinates, JButton.class);
	}

	@Override
	protected String[] calculateTypes(Component component) {
      return new String[] { "button", "text" };
	}

	@Override
	protected String getText(Component component) {
		return ((JButton) component).getText();
	}
}
