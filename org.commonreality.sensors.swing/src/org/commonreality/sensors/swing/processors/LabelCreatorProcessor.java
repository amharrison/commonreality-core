package org.commonreality.sensors.swing.processors;

import java.awt.Component;

import javax.swing.JLabel;

import org.commonreality.sensors.swing.Coordinates;

public class LabelCreatorProcessor extends AbstractCreatorProcessor {

	public LabelCreatorProcessor(Coordinates coordinates) {
		super(coordinates, JLabel.class);
	}

	@Override
	protected String[] calculateTypes(Component component) {
		String[] types = {"label"};
		if(getText(component)!=null)
          types = new String[] {"label","text"};
		
		return types;
	}

	@Override
	protected String getText(Component component) {
		return ((JLabel)component).getText();
	}
}
