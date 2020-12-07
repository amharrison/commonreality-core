package org.commonreality.sensors.swing.processors;

import java.awt.Component;

import javax.swing.AbstractButton;

import org.commonreality.sensors.swing.internal.Coordinates;

public class ButtonCreatorProcessor extends AbstractCreatorProcessor {

	public ButtonCreatorProcessor(Coordinates coordinates) {
		super(coordinates, AbstractButton.class);
	}

	@Override
	protected String[] calculateTypes(Component component) {
		String[] types = {"button"};
		if(getText(component)!=null)
          types = new String[] {"button","text"};
		
		return types;
	}

	@Override
	protected String getText(Component component) {
		return ((AbstractButton)component).getText();
	}
}
