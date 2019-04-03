package org.commonreality.sensors.motor.interpolator;

/*
 * default logging
 */
 
import org.slf4j.LoggerFactory;
import org.commonreality.modalities.motor.MovementCommand;
import org.commonreality.object.IAgentObject;
import org.commonreality.sensors.handlers.EfferentCommandHandler;
import org.commonreality.sensors.motor.IActuator;
import org.commonreality.sensors.motor.MotorCommandHandler;

/**
 * routes commands from the {@link MotorCommandHandler} to the proper actuator,
 * which in this case just queues up the appropriate events
 * @author harrison
 *
 */
public class InterpolatorActuator implements IActuator
{
  /**
   * Logger definition
   */
  static private final transient org.slf4j.Logger LOGGER = LoggerFactory
                                                .getLogger(InterpolatorActuator.class);

  private IInterpolator _interpolator;
  
  public InterpolatorActuator(IInterpolator interpolator)
  {
    _interpolator = interpolator;
  }
  
  public void abort(IAgentObject agent, MovementCommand movement, EfferentCommandHandler handler)
  {
    _interpolator.abort(agent, movement);
  }

  public void start(IAgentObject agent, MovementCommand movement, EfferentCommandHandler handler)
  {
    /*
     * agent is starting movement..
     */
    _interpolator.start(agent, movement);
  }
}
