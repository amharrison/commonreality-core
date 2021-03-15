package org.commonreality.sensors.swing;

import java.awt.Polygon;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.function.Consumer;

import javax.swing.SwingUtilities;

import org.commonreality.executor.InlineExecutor;
import org.commonreality.notification.event.INotificationListener;
import org.commonreality.notification.event.NotificationEvent;
import org.commonreality.notification.impl.SimpleMapNotification;
import org.commonreality.sensors.base.BaseSensor;
import org.commonreality.sensors.swing.internal.Coordinates;
import org.commonreality.sensors.swing.internal.SwingCenter;
import org.commonreality.sensors.swing.ui.GlassPaneManager;

public class DefaultSwingSensor extends BaseSensor
{

  protected Coordinates      _coordinates;

  protected SwingCenter      _swingCenter;

  protected GlassPaneManager _glassPaneManager;

  public DefaultSwingSensor()
  {
    super();
    setRealtimeClockEnabled(false);
    setImmediateModeEnabled(false);

    getNotificationManager().addListener(new INotificationListener() {

      @Override
      public void notificationPosted(NotificationEvent event)
      {
        String eventType = event.getNotification().getIdentifier().getName();
        if (eventType.equals("fixation"))
          fixationNotification(
              (SimpleMapNotification<String, Object>) event.getNotification());
        else if (eventType.equals("debugShape")) renderDebuggingShape(
            (SimpleMapNotification<String, Object>) event.getNotification());
      }

    }, InlineExecutor.get());
  }

  private void renderDebuggingShape(
      SimpleMapNotification<String, Object> notification)
  {
    Map<String, Object> data = notification.getData();
    if (_glassPaneManager != null) data.forEach((k, v) -> {
      String id = k.substring(k.indexOf(".") + 1);
      String type = k.substring(0, k.indexOf("."));
      Shape shape = null;
      double[] points = (double[]) v;
      if (points.length == 0)
        _glassPaneManager.setDebugShape(id, null);
      else
      {

        if (type.equals("polygon"))
        {
          Polygon poly = new Polygon();
          for (int i = 0; i < points.length; i += 2)
          {
            java.awt.Point screen = toScreen(points[i], points[i + 1]);
            poly.addPoint(screen.x, screen.y);
          }
          shape = poly;
        }
        else if (type.equals("line"))
        {
          Line2D line = new Line2D.Double();
          java.awt.Point a = toScreen(points[0], points[1]);
          java.awt.Point b = toScreen(points[2], points[3]);
          line.setLine(a, b);
          shape = line;
        }
        else if (type.equals("rectangle"))
        {
          java.awt.Point a = toScreen(points[0], points[1]);
          java.awt.Point b = toScreen(points[0] + points[2],
              points[1] + points[3]);
          shape = new Rectangle2D.Double(Math.min(a.x, b.x), Math.min(a.y, b.y),
              b.x - a.x, a.y - b.y);
        }
        else if (type.equals("circle"))
        {
          // center and radisu
          java.awt.Point a = toScreen(points[0] - points[2],
              points[1] - points[2]);
          java.awt.Point b = toScreen(points[0] + points[2],
              points[1] + points[2]);
          shape = new Ellipse2D.Double(Math.min(a.x, b.x), Math.min(a.y, b.y),
              Math.abs(b.x - a.x), Math.abs(a.y - b.y));
        }
        else if (type.equals("text"))
        {
          String newId = id.substring(0, id.indexOf("."));
          String text = id.substring(id.indexOf(".") + 1);
          System.err.println("Adding text debug " + text);
          _glassPaneManager.setDebugShape(newId, text, points);
        }

        if (shape != null) _glassPaneManager.setDebugShape(id, shape);
      }
    });
  }

  private java.awt.Point toScreen(double x, double y)
  {
    java.awt.geom.Point2D centerPointRetino = new java.awt.geom.Point2D.Double(
        x, y);
    java.awt.geom.Point2D centerCM = _coordinates
        .fromRetinotopic(centerPointRetino);
    java.awt.geom.Point2D centerPixel = _coordinates.fromCentimeters(centerCM);
    java.awt.Point screen = _coordinates.fromCenterOfScreen(centerPixel);
    return screen;
  }

  private void fixationNotification(
      SimpleMapNotification<String, Object> notification)
  {
    if (_glassPaneManager != null)
    {
      double[] fixationPoint = (double[]) notification.getData()
          .getOrDefault("fixation.point", new double[] { 0, 0 });

      java.awt.Point screen = toScreen(fixationPoint[0], fixationPoint[1]);

      _glassPaneManager.update(screen);
    }
  }

  public Coordinates getCoordinateTransform()
  {
    return _coordinates;
  }

  public SwingCenter getSwingCenter()
  {
    return _swingCenter;
  }

  @Override
  public void configure(Map<String, String> options) throws Exception
  {
    super.configure(options);

    double dotsPerCM = Double
        .parseDouble(options.getOrDefault("DotsPerCM", "86.8"));
    double distanceToScreenCM = Double
        .parseDouble(options.getOrDefault("DistanceToScreenCM", "33"));
    boolean useFixation = Boolean
        .parseBoolean(options.getOrDefault("UseFixationTracker", "false"));
    String className = options.getOrDefault("ConfigurationConsumer", "");

    _coordinates = new Coordinates(distanceToScreenCM, dotsPerCM);
    if (useFixation) _glassPaneManager = new GlassPaneManager();

    configureSwing();

    if (className.trim().length() != 0) try
    {
      @SuppressWarnings("unchecked")
      Class<Consumer<DefaultSwingSensor>> clazz = (Class<Consumer<DefaultSwingSensor>>) getClass()
          .getClassLoader().loadClass(className);
      Consumer<DefaultSwingSensor> consumer = clazz.getConstructor()
          .newInstance();
      consumer.accept(this);
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }

  @Override
  public String getName()
  {
    return "SwingSensor";
  }

  protected void configureSwing()
  {
    _swingCenter = new SwingCenter(this, _coordinates);

  }

  @Override
  protected double processMotor()
  {
    /**
     * this will block until the awt event thread is free
     */
    try
    {
      SwingUtilities.invokeAndWait(new Runnable() {

        @Override
        public void run()
        {

        }

      });
    }
    catch (InvocationTargetException e)
    {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    catch (InterruptedException e)
    {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    /*
     * DefaultKeyboardSensor is used for motor
     */
    return Double.NaN;
  }

  @Override
  public void start() throws Exception
  {
    _swingCenter.install();

    super.start();
  }

  @Override
  public void shutdown() throws Exception
  {
    _swingCenter.uninstall();
    super.shutdown();
  }

  @Override
  protected void startOfCycle()
  {
    super.startOfCycle();
    _swingCenter.newCycle();
  }
}
