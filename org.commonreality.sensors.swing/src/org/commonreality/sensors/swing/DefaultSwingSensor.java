package org.commonreality.sensors.swing;

import java.util.Map;

import org.commonreality.sensors.base.BaseSensor;
import org.commonreality.sensors.swing.internal.Coordinates;
import org.commonreality.sensors.swing.internal.SwingCenter;

public class DefaultSwingSensor extends BaseSensor
{

  protected Coordinates _coordinates;

  protected SwingCenter _swingCenter;

  public DefaultSwingSensor()
  {
    super();
    setRealtimeClockEnabled(true);
    setImmediateModeEnabled(false);
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
    _coordinates = new Coordinates(distanceToScreenCM, dotsPerCM);

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
