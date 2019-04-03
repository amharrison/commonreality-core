package org.commonreality.sensors.motor.interpolator;

/*
 * default logging
 */
 
import org.slf4j.LoggerFactory;
import org.commonreality.modalities.motor.MovementCommand;
import org.commonreality.object.IAgentObject;
import org.commonreality.object.IMutableObject;
import org.commonreality.object.delta.DeltaTracker;
import org.commonreality.sensors.handlers.EfferentCommandHandler;

public class BasicActuatorCompletion implements IActuatorCompletion
{
  /**
   * Logger definition
   */
  static private final transient org.slf4j.Logger LOGGER = LoggerFactory
                                                .getLogger(BasicActuatorCompletion.class);
  
  private EfferentCommandHandler _handler;
  
  /**
   * must call {@link #setHandler(EfferentCommandHandler)} before using.
   */
  public BasicActuatorCompletion()
  {

  }

  public BasicActuatorCompletion(EfferentCommandHandler handler)
  {
    setHandler(handler);
  }

  public void setHandler(EfferentCommandHandler handler)
  {
    _handler = handler;
  }
  
  public EfferentCommandHandler getHandler()
  {
    return _handler;
  }
  
  
  public void aborted(IAgentObject agent, MovementCommand command,
      Object extraInfo)
  {
    _handler.aborted(command, extraInfo);
  }

  public void completed(IAgentObject agent, MovementCommand command,
      Object extraInfo)
  {
    _handler.completed(command, extraInfo);
  }

  public void updated(IAgentObject agent, MovementCommand command,
      DeltaTracker<IMutableObject> motorTracker)
  {
    // TODO Auto-generated method stub
    
  }

}
