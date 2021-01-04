package org.commonreality.sensors.swing;

import java.util.Map;

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
        if (event.getNotification().getIdentifier().getName()
            .equals("fixation"))
          fixationNotification(
              (SimpleMapNotification<String, Object>) event.getNotification());
      }

    }, InlineExecutor.get());
  }

  private void fixationNotification(
      SimpleMapNotification<String, Object> notification)
  {
    if (_glassPaneManager != null)
    {
      double[] fixationPoint = (double[]) notification.getData()
          .getOrDefault("fixation.point", new double[] { 0, 0 });
      // IIdentifier fixationObject = (IIdentifier)
      // notification.getData().get("fixation.identifier");

      java.awt.geom.Point2D centerPointRetino = new java.awt.geom.Point2D.Double(
          fixationPoint[0], fixationPoint[1]);
      java.awt.geom.Point2D centerCM = _coordinates
          .fromRetinotopic(centerPointRetino);
      java.awt.geom.Point2D centerPixel = _coordinates
          .fromCentimeters(centerCM);
      java.awt.Point screen = _coordinates.fromCenterOfScreen(centerPixel);

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
    _coordinates = new Coordinates(distanceToScreenCM, dotsPerCM);
    if (useFixation) _glassPaneManager = new GlassPaneManager();

    configureSwing();
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
