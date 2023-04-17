package org.commonreality.sensors.swing.processors;

import javax.swing.JComponent;
import javax.swing.JToggleButton;

import org.commonreality.sensors.swing.internal.Coordinates;

public class ToggleButtonCreatorProcessor extends AbstractCreatorProcessor {

	public ToggleButtonCreatorProcessor(Coordinates coordinates) {
		super(coordinates, JToggleButton.class);
	}

	@Override
    protected String[] calculateTypes(JComponent component)
    {
		String[] types = { "radio-button" };
		String text = getText(component);
		if (text != null && !"".equals(text))
			types = new String[] { "radio-button", "text" };
		return types;
	}

	@Override
    protected String getText(JComponent component)
    {
		return ((JToggleButton) component).getText();
	}
}
