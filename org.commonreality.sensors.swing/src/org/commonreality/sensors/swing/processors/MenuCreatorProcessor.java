package org.commonreality.sensors.swing.processors;

import java.awt.Component;

import javax.swing.JMenuItem;

import org.commonreality.sensors.swing.internal.Coordinates;

public class MenuCreatorProcessor extends AbstractCreatorProcessor {

	public MenuCreatorProcessor(Coordinates coordinates) {
		super(coordinates, JMenuItem.class);
	}

	@Override
	protected String[] calculateTypes(Component component) {
		String[] types = { "menu" };
		String text = getText(component);
		if (text != null && !"".equals(text))
			types = new String[] { "menu", "text" };
		return types;
	}

	@Override
	protected String getText(Component component) {
		return ((JMenuItem) component).getText();
	}
}
