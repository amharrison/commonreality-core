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

public class TranslateCommandTemplate extends MovementCommandTemplate<TranslateCommand>
{
  /**
   * 
   */
  private static final long serialVersionUID = 210365422153510766L;
  /**
   * Logger definition
   */
  static private final transient org.slf4j.Logger LOGGER = LoggerFactory
                                                .getLogger(TranslateCommandTemplate.class);
  
  
  public TranslateCommandTemplate()
  {
    super("translate","translate");
  }
  
  
  @Override
  protected void configure(TranslateCommand command, IAgent agent,
      IEfferentObject object)
  {
    
  }
  
  @Override
  protected TranslateCommand create(IIdentifier commandId, IIdentifier muscleId)
  {
    return new TranslateCommand(commandId, muscleId);
  }
  
  public boolean isConsistent(IEfferentCommand command)
  {
    return command instanceof TranslateCommand;
  }

 

}
