package org.commonreality.sensors.swing.processors;

import javax.swing.JButton;
import javax.swing.JComponent;

import org.commonreality.sensors.swing.internal.Coordinates;

public class ButtonCreatorProcessor extends AbstractCreatorProcessor {

	public ButtonCreatorProcessor(Coordinates coordinates) {
		super(coordinates, JButton.class);
	}

	@Override
    protected String[] calculateTypes(JComponent component)
    {
      return new String[] { "button", "text" };
	}

	@Override
    protected String getText(JComponent component)
    {
		return ((JButton) component).getText();
	}
}
