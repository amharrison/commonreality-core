package org.commonreality.modalities.motor;

/*
 * default logging
 */
 
import org.slf4j.LoggerFactory;
import org.commonreality.agents.IAgent;
import org.commonreality.efferent.AbstractEfferentCommandTemplate;
import org.commonreality.efferent.IEfferentCommand;
import org.commonreality.identifier.IIdentifier;
import org.commonreality.object.IEfferentObject;

public class MovementCommandTemplate<M extends MovementCommand>
    extends AbstractEfferentCommandTemplate<M>
{
  /**
   * 
   */
  private static final long          serialVersionUID = -3686426978933208922L;

  /**
   * Logger definition
   */
  static private final transient org.slf4j.Logger LOGGER           = LoggerFactory
                                                          .getLogger(MovementCommandTemplate.class);

  public MovementCommandTemplate(String name, String desc)
  {
    super(name, desc);
  }

  public M instantiate(IAgent agent, IEfferentObject object)
      throws Exception
  {
    if (!MotorUtilities.isMotor(object))
      throw new IllegalStateException(object.getIdentifier()
          + " is not a motor.");

    return super.instantiate(agent, object);
  }

  @Override
  protected void configure(M command, IAgent agent, IEfferentObject object)
  {
    // noop
    
  }

  @SuppressWarnings("unchecked")
  @Override
  protected M create(IIdentifier commandId, IIdentifier muscleId)
  {
    return (M) new MovementCommand(commandId, muscleId);
  }

  public boolean isConsistent(IEfferentCommand command)
  {
    return command instanceof MovementCommand;
  }

}
