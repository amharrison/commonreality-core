package org.commonreality.sensors.swing.listeners;

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.Container;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.function.Consumer;
import java.util.function.Predicate;

import org.commonreality.sensors.swing.ComponentVisitor;

public class GlobalWindowListener extends WindowAdapter implements AWTEventListener {

	private final Predicate<Container> _shouldDescend;
	private final Predicate<Component> _shouldAccept;
	private final Consumer<Component> _addAcceptor;
	private final Consumer<Component> _removeAcceptor;

	public GlobalWindowListener(Predicate<Container> shouldDescend, Predicate<Component> shouldAccept,
			Consumer<Component> addAcceptor, Consumer<Component> removeAcceptor) {
		_shouldDescend = shouldDescend;
		_shouldAccept = shouldAccept;
		_addAcceptor = addAcceptor;
		_removeAcceptor = removeAcceptor;
	}

	public void install() {
		Toolkit.getDefaultToolkit().addAWTEventListener(this, AWTEvent.WINDOW_EVENT_MASK);
	}

	public void uninstall() {
		Toolkit.getDefaultToolkit().removeAWTEventListener(this);
	}

	@Override
  public void windowOpened(WindowEvent e) {
		new ComponentVisitor(_shouldDescend, _shouldAccept, _addAcceptor).visit(e.getComponent());
	}

	@Override
  public void windowClosed(WindowEvent e) {
		new ComponentVisitor(_shouldDescend, _shouldAccept, _removeAcceptor).visit(e.getComponent());
	}

	@Override
  public void windowIconified(WindowEvent e) {
		windowClosed(e);
	}

	@Override
  public void windowDeiconified(WindowEvent e) {
		windowOpened(e);
	}

	@Override
	public void eventDispatched(AWTEvent event) {
		System.err.println(event);
		
		WindowEvent e = (WindowEvent) event;
		switch (e.getID()) {
		case WindowEvent.WINDOW_OPENED:
			windowOpened(e);
			break;
		case WindowEvent.WINDOW_CLOSED:
			windowClosed(e);
			break;
		case WindowEvent.WINDOW_DEICONIFIED:
			windowDeiconified(e);
			break;
		case WindowEvent.WINDOW_ICONIFIED:
			windowIconified(e);
			break;
		}
		//ignore the other events.
	}
}
