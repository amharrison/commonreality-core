package org.commonreality.sensors.swing.ui;

import java.awt.Component;
import java.awt.KeyboardFocusManager;
import java.awt.Point;
import java.awt.Window;

import javax.swing.RootPaneContainer;
import javax.swing.SwingUtilities;

public class GlassPaneManager {

	private RootPaneContainer _currentGPContainer;
	private Component _originalGP;

	private EyeTrackingGlassPane _glassPane;

	public GlassPaneManager() {
		_glassPane = new EyeTrackingGlassPane();
	}

	public void update(Point fixation) {
		Window window = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusedWindow();

		if (window instanceof RootPaneContainer) {
			updateInternal((RootPaneContainer) window, fixation);
		}
	}

	protected void updateInternal(RootPaneContainer newContainer, Point fixation) {
		if (newContainer != _currentGPContainer) {
			detach(_currentGPContainer);
			attach(newContainer);
		}
		SwingUtilities.convertPointFromScreen(fixation, _glassPane.getParent());
		_glassPane.setFixation(fixation);
	}

	protected void detach(RootPaneContainer current) {
		if (_currentGPContainer != null)
			_currentGPContainer.setGlassPane(_originalGP);
		_originalGP = null;
		_currentGPContainer = null;
	}

	protected void attach(RootPaneContainer newContainer) {
		_currentGPContainer = newContainer;
		_originalGP = _currentGPContainer.getGlassPane();
		_currentGPContainer.setGlassPane(_glassPane);
		_glassPane.setVisible(true);
		_glassPane.repaint();
	}
}
