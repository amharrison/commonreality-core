package org.commonreality.sensors.swing.processors;

import java.awt.Component;

import javax.swing.text.JTextComponent;

import org.commonreality.sensors.swing.internal.Coordinates;

public class TextFieldCreatorProcessor extends AbstractCreatorProcessor {

	public TextFieldCreatorProcessor(Coordinates coordinates) {
		super(coordinates, JTextComponent.class);
	}

	@Override
	protected String[] calculateTypes(Component component) {
		String[] types = {"text-area"};
		if(getText(component)!=null)
          types = new String[] {"text-area","text"};
		
		return types;
	}

	@Override
	protected String getText(Component component) {
		return ((JTextComponent)component).getText();
	}
}
