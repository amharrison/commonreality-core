package org.commonreality.sensors.swing.internal;

import java.awt.AWTException;
import java.awt.Point;
import java.awt.Robot;
import java.awt.event.InputEvent;
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

public class SwingActuator extends DefaultActuator
{

  static private transient Logger LOGGER = LoggerFactory
      .getLogger(SwingActuator.class);

  private Robot                   _robot;

  private Coordinates             _coordinates;

  public SwingActuator(Coordinates coordinates)
  {
    _coordinates = coordinates;
    try
    {
      _robot = new Robot();
    }
    catch (AWTException e)
    {

      throw new RuntimeException(e);
    }
  }

  public void setCoordinates(Coordinates coords)
  {
    _coordinates = coords;
  }

  private boolean isMouse(int keyCode)
  {
    return keyCode >= MouseEvent.BUTTON1 && keyCode <= MouseEvent.BUTTON3;
  }

  @Override
  protected void press(PressCommand command, EfferentCommandHandler handler)
  {
    int keyCode = getCode(command, handler);
    if (isMouse(keyCode))
      switch (keyCode)
      {
        case MouseEvent.BUTTON1:
          _robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
          break;
        case MouseEvent.BUTTON2:
          _robot.mousePress(InputEvent.BUTTON2_DOWN_MASK);
          break;
        case MouseEvent.BUTTON3:
          _robot.mousePress(InputEvent.BUTTON3_DOWN_MASK);
          break;
      }
    else
      _robot.keyPress(keyCode);
  }

  @Override
  protected void release(ReleaseCommand command, EfferentCommandHandler handler)
  {
    int keyCode = getCode(command, handler);
    if (isMouse(keyCode))
      switch (keyCode)
      {
        case MouseEvent.BUTTON1:
          _robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
          break;
        case MouseEvent.BUTTON2:
          _robot.mouseRelease(InputEvent.BUTTON2_DOWN_MASK);
          break;
        case MouseEvent.BUTTON3:
          _robot.mouseRelease(InputEvent.BUTTON3_DOWN_MASK);
          break;
      }
    else
      _robot.keyRelease(keyCode);
  }

  @Override
  protected void positionMouse(TranslateCommand command,
      EfferentCommandHandler handler, IEfferentObject mouse, double[] position)
  {
    /*
     * position is retinotopic, need to convert back to screen first
     */
    Point2D retino = new Point2D.Double(position[0], position[1]);
    Point2D inCM = _coordinates.fromRetinotopic(retino);
    Point2D inPx = _coordinates.fromCentimeters(inCM);
    Point onScreen = _coordinates.fromCenterOfScreen(inPx);

    if (LOGGER.isDebugEnabled())
    {
      LOGGER.debug(String.format("Retino %s", retino));
      LOGGER.debug(String.format("CM %s", inCM));
      LOGGER.debug(String.format("Pixels %s", inPx));
      LOGGER.debug(String.format("Screen %s", onScreen));
    }

    _robot.mouseMove(onScreen.x, onScreen.y);
  }

}
