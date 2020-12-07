package org.commonreality.sensors.swing.listeners;

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.Container;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.util.function.Predicate;

import org.commonreality.sensors.base.BaseSensor;
import org.commonreality.sensors.swing.ComponentVisitor;

public class HierarchyListener implements AWTEventListener {

	private final Predicate<Container> _shouldDescend;
	private final Predicate<Component> _shouldAccept;
	private final BaseSensor _sensor;

	public HierarchyListener(BaseSensor sensor, Predicate<Container> shouldDescend, Predicate<Component> shouldAccept) {
		_shouldDescend = shouldDescend;
		_shouldAccept = shouldAccept;
		_sensor = sensor;
	}

	public void install() {
		Toolkit.getDefaultToolkit().addAWTEventListener(this,
				AWTEvent.HIERARCHY_EVENT_MASK | AWTEvent.HIERARCHY_BOUNDS_EVENT_MASK);
	}

	public void uninstall() {
		Toolkit.getDefaultToolkit().removeAWTEventListener(this);
	}

	@Override
	public void eventDispatched(AWTEvent event) {

		Component source = (Component) event.getSource();

		new ComponentVisitor(_shouldDescend, _shouldAccept, (c) -> {
			_sensor.getPerceptManager().markAsDirty(c);
		}).visit(source);
	}

}
