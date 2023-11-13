package org.commonreality.sensors.swing.internal;

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;

import org.commonreality.modalities.motor.TranslateCommand;
import org.commonreality.object.IEfferentObject;
import org.commonreality.sensors.handlers.EfferentCommandHandler;
import org.commonreality.sensors.keyboard.DefaultActuator;
import org.commonreality.sensors.keyboard.PressCommand;
import org.commonreality.sensors.keyboard.ReleaseCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SwingActuator extends DefaultActuator {

	static private transient Logger LOGGER = LoggerFactory.getLogger(SwingActuator.class);

	private boolean _useMock = true;
	private Robot _robot;
	private MockInterface _interface;

	private Coordinates _coordinates;

	public SwingActuator(Coordinates coordinates, boolean useMock) {
		_coordinates = coordinates;
		_useMock = useMock;
		if (_useMock)
			_interface = new MockInterface();
		else
			try {
				_robot = new Robot();
				// autoWaitForIdle can cause deadlocks when running full bore. Not sure
				// why
				// _robot.setAutoWaitForIdle(true);
				testRobot();
			} catch (AWTException e) {

				throw new RuntimeException(e);
			}
	}

	protected void testRobot() {
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		_robot.mouseMove(screen.width / 2, screen.height / 2);
		_robot.waitForIdle();
		Point p = MouseInfo.getPointerInfo().getLocation();
		if (p.x != screen.width / 2 || p.y != screen.height / 2) {
			RuntimeException e = new RuntimeException(
					"Could not control mouse pointer. Check your OS's security/accessibility settings.");
			LOGGER.error(e.getMessage());
			throw e;
		}
	}

	public void setCoordinates(Coordinates coords) {
		_coordinates = coords;
	}

	private boolean isMouse(int keyCode) {
		return keyCode >= MouseEvent.BUTTON1 && keyCode <= MouseEvent.BUTTON3;
	}

	@Override
	protected void press(PressCommand command, EfferentCommandHandler handler) {
		int keyCode = getCode(command, handler);
		if (isMouse(keyCode)) {
			int mask = MouseEvent.getMaskForButton(keyCode);
			if (_useMock)
				_interface.mousePress(mask, keyCode);
			else
				_robot.mousePress(mask);

		} else if (_useMock)
			_interface.keyPress(keyCode);
		else
			_robot.keyPress(keyCode);
	}

	@Override
	protected void release(ReleaseCommand command, EfferentCommandHandler handler) {
		int keyCode = getCode(command, handler);
		if (isMouse(keyCode)) {
			int mask = MouseEvent.getMaskForButton(keyCode);
			if (_useMock)
				_interface.mouseRelease(mask, keyCode);
			else
				_robot.mouseRelease(mask);

		} else if (_useMock)
			_interface.keyRelease(keyCode);
		else
			_robot.keyRelease(keyCode);
	}

	@Override
	protected void positionMouse(TranslateCommand command, EfferentCommandHandler handler, IEfferentObject mouse,
			double[] position) {
		/*
		 * position is retinotopic, need to convert back to screen first
		 */
		Point2D retino = new Point2D.Double(position[0], position[1]);
		Point2D inCM = _coordinates.fromRetinotopic(retino);
		Point2D inPx = _coordinates.fromCentimeters(inCM);
		Point onScreen = _coordinates.fromCenterOfScreen(inPx);

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(String.format("Retino %s", retino));
			LOGGER.debug(String.format("CM %s", inCM));
			LOGGER.debug(String.format("Pixels %s", inPx));
			LOGGER.debug(String.format("Screen %s", onScreen));
		}

		if (_useMock)
			_interface.mouseMove(onScreen.x, onScreen.y);
		else
			_robot.mouseMove(onScreen.x, onScreen.y);

	}

}
