package org.commonreality.sensors.swing.internal;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import javax.swing.SwingUtilities;

import org.slf4j.LoggerFactory;

public class Coordinates
{

  private static final transient org.slf4j.Logger LOGGER = LoggerFactory
      .getLogger(Coordinates.class);

  private final double                            _dotsPerCM;

  private final double                            _distanceToScreenCM;

  public Coordinates(double distanceToScreenCM, double dotsPerCM)
  {
    _dotsPerCM = dotsPerCM;
    _distanceToScreenCM = distanceToScreenCM;
  }

  public Rectangle2D toRetinotopic(Rectangle rectangle, Component component)
  {
    Point upperLeft = rectangle.getLocation();
    Point lowerRight = rectangle.getLocation();
    lowerRight.x += rectangle.width;
    lowerRight.y += rectangle.height;

    Point2D ulCentered = toCenterOfScreen(upperLeft, component);
    Point2D lrCentered = toCenterOfScreen(lowerRight, component);
    Point2D ulCM = toCentimeters(ulCentered);
    Point2D lrCM = toCentimeters(lrCentered);
    Point2D ulRetino = toRetinotopic(ulCM);
    Point2D lrRetino = toRetinotopic(lrCM);

    if (LOGGER.isDebugEnabled())
    {
      LOGGER.debug(String.format("Component %s %s", upperLeft, lowerRight));
      LOGGER.debug(String.format("Screen %s %s", ulCentered, lrCentered));
      LOGGER.debug(String.format("CM %s %s", ulCM, lrCM));
      LOGGER.debug(String.format("Retino %s %s", ulRetino, lrRetino));
    }
    Rectangle2D.Double rect = new Rectangle2D.Double(ulRetino.getX(),
        lrRetino.getY(), lrRetino.getX() - ulRetino.getX(),
        ulRetino.getY() - lrRetino.getY());
    return rect;
  }

  /**
   * @param pointInPixels
   * @param component
   *          the component the point is relative to, null for screen
   * @return
   */
  public Point2D toCenterOfScreen(Point pointInPixels, Component component)
  {

    Point p = new Point(pointInPixels);
    if (component != null) SwingUtilities.convertPointToScreen(p, component);
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    return new Point2D.Double(p.x - screenSize.width / 2.0,
        screenSize.height / 2.0 - p.y);
  }

  public Point fromCenterOfScreen(Point2D centerPointPixels)
  {
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

    Point p = new Point(
        (int) (centerPointPixels.getX() + screenSize.width / 2.0),
        (int) (screenSize.height / 2.0 + centerPointPixels.getY()));
    return p;
  }

  public Point2D toCentimeters(Point2D centerPointPixels)
  {
    return new Point2D.Double(centerPointPixels.getX() / _dotsPerCM,
        centerPointPixels.getY() / _dotsPerCM);
  }

  public Point2D fromCentimeters(Point2D centerPointCM)
  {
    return new Point2D.Double(centerPointCM.getX() * _dotsPerCM,
        centerPointCM.getY() * _dotsPerCM);
  }

  public Point2D toRetinotopic(Point2D centerPointCM)
  {

    double thetaX = Math.atan(centerPointCM.getX() / _distanceToScreenCM);
    double thetaY = Math.atan(centerPointCM.getY() / _distanceToScreenCM);
    thetaX = Math.toDegrees(thetaX);
    thetaY = Math.toDegrees(thetaY);

    return new Point2D.Double(thetaX, thetaY);
  }

  public Point2D fromRetinotopic(Point2D centerPointRetino)
  {

    double thetaX = Math.toRadians(centerPointRetino.getX());
    double thetaY = Math.toRadians(centerPointRetino.getY());
    double x = Math.tan(thetaX) * _distanceToScreenCM;
    double y = Math.tan(-thetaY) * _distanceToScreenCM;

    return new Point2D.Double(x, y);
  }
}
