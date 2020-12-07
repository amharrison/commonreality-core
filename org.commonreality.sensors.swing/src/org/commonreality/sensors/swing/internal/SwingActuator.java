package org.commonreality.sensors.swing.internal;

import java.awt.AWTException;
import java.awt.Point;
import java.awt.Robot;
import java.awt.geom.Point2D;

import org.commonreality.modalities.motor.MotorUtilities;
import org.commonreality.modalities.motor.MovementCommand;
import org.commonreality.modalities.motor.TranslateCommand;
import org.commonreality.object.IEfferentObject;
import org.commonreality.sensors.handlers.EfferentCommandHandler;
import org.commonreality.sensors.keyboard.DefaultActuator;
import org.commonreality.sensors.keyboard.PressCommand;
import org.commonreality.sensors.keyboard.ReleaseCommand;

public class SwingActuator extends DefaultActuator {

	private Robot _robot;
	private Coordinates _coordinates;
	
	public SwingActuator(Coordinates coordinates) {
		_coordinates = coordinates;
		try {
			_robot = new Robot();
		} catch (AWTException e) {

			throw new RuntimeException(e);
		}
	}
	
	public void setCoordinates(Coordinates coords)
	{
		_coordinates = coords;
	}

	private boolean isMouse(MovementCommand command, EfferentCommandHandler handler) {
		IEfferentObject muscle = handler.getSensor().getEfferentObjectManager().get(command.getEfferentIdentifier());
		String name = MotorUtilities.getName(muscle);
		if ("mouse".equals(name))
			return true;
		return false;
	}

	@Override
  protected void press(PressCommand command, EfferentCommandHandler handler) {
		int keyCode = getCode(command, handler);
		if (isMouse(command, handler))
			_robot.mousePress(keyCode);
		else
			_robot.keyPress(keyCode);
	}

	@Override
  protected void release(ReleaseCommand command, EfferentCommandHandler handler) {
		int keyCode = getCode(command, handler);
		if (isMouse(command, handler))
			_robot.mouseRelease(keyCode);
		else
			_robot.keyRelease(keyCode);
	}

	@Override
  protected void positionMouse(TranslateCommand command, EfferentCommandHandler handler, IEfferentObject mouse,
			double[] position) {
		/*
		 * position is retinotopic, need to convert back to screen first
		 * 
		 */
		Point2D inCM = _coordinates.fromRetinotopic(new Point2D.Double(position[0],position[1]));
		Point2D inPx = _coordinates.fromCentimeters(inCM);
		Point onScreen = _coordinates.fromCenterOfScreen(inPx);
		
		_robot.mouseMove(onScreen.x, onScreen.y);
	}

}
