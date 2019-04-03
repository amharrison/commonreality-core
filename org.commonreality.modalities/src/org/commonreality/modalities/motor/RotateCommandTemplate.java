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

public class RotateCommandTemplate extends MovementCommandTemplate<RotateCommand>
{
  /**
   * 
   */
  private static final long serialVersionUID = 210365422153510766L;
  /**
   * Logger definition
   */
  static private final transient org.slf4j.Logger LOGGER = LoggerFactory
                                                .getLogger(RotateCommandTemplate.class);
  
  
  public RotateCommandTemplate()
  {
    super("rotate","rotate");
  }
  
  
  @Override
  protected void configure(RotateCommand command, IAgent agent,
      IEfferentObject object)
  {
    
  }
  
  @Override
  protected RotateCommand create(IIdentifier commandId, IIdentifier muscleId)
  {
    return new RotateCommand(commandId, muscleId);
  }
  
  public boolean isConsistent(IEfferentCommand command)
  {
    return command instanceof RotateCommand;
  }

 

}
