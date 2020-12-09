package org.commonreality.sensors.swing.listeners;

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.Container;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.ComponentEvent;
import java.util.function.Consumer;
import java.util.function.Predicate;

import org.commonreality.sensors.swing.internal.ComponentVisitor;

public class GlobalComponentListener implements AWTEventListener {

	private final Predicate<Container> _shouldDescend;
	private final Predicate<Component> _shouldAccept;
	private final Consumer<Component> _addAcceptor;
	private final Consumer<Component> _removeAcceptor;

	public GlobalComponentListener(Predicate<Container> shouldDescend, Predicate<Component> shouldAccept,
			Consumer<Component> addAcceptor, Consumer<Component> removeAcceptor) {
		_shouldDescend = shouldDescend;
		_shouldAccept = shouldAccept;
		_addAcceptor = addAcceptor;
		_removeAcceptor = removeAcceptor;
	}

	public void install() {
		Toolkit.getDefaultToolkit().addAWTEventListener(this, AWTEvent.COMPONENT_EVENT_MASK);
	}

	public void uninstall() {
		Toolkit.getDefaultToolkit().removeAWTEventListener(this);
	}

	public void add(ComponentEvent e) {
		new ComponentVisitor(_shouldDescend, _shouldAccept, _addAcceptor).visit(e.getComponent());
	}

	public void remove(ComponentEvent e) {
		new ComponentVisitor(_shouldDescend, _shouldAccept, _removeAcceptor).visit(e.getComponent());
	}

	@Override
	public void eventDispatched(AWTEvent event) {


		ComponentEvent e = (ComponentEvent) event;
		switch (e.getID()) {
		case ComponentEvent.COMPONENT_MOVED:
		case ComponentEvent.COMPONENT_RESIZED:
		case ComponentEvent.COMPONENT_SHOWN:
			add(e);
			break;
		case ComponentEvent.COMPONENT_HIDDEN:
			remove(e);
			break;
		}
		// ignore the other events.
	}
}
