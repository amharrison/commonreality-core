package org.commonreality.modalities.vocal;

/*
 * default logging
 */
 
import org.slf4j.LoggerFactory;
import org.commonreality.agents.IAgent;
import org.commonreality.efferent.AbstractEfferentCommandTemplate;
import org.commonreality.efferent.IEfferentCommand;
import org.commonreality.efferent.IEfferentCommandManager;
import org.commonreality.identifier.IIdentifier;
import org.commonreality.object.IEfferentObject;
import org.commonreality.object.manager.IRequestableObjectManager;

public class VocalizationCommandTemplate extends
    AbstractEfferentCommandTemplate<VocalizationCommand>
{
  /**
   * 
   */
  private static final long serialVersionUID = -2425510983692712378L;
  /**
   * Logger definition
   */
  static private final transient org.slf4j.Logger LOGGER = LoggerFactory
                                                .getLogger(VocalizationCommandTemplate.class);

  
  public VocalizationCommandTemplate(String name, String description)
  {
    super(name, description);
  }

  public VocalizationCommand instantiate(IAgent agent, IEfferentObject object) throws Exception
  {
    if(!VocalUtilities.canVocalize(object))
      throw new IllegalStateException(object.getIdentifier()+" cannot vocalize.");
    
    return super.instantiate(agent, object);
  }

  public boolean isConsistent(IEfferentCommand command)
  {
    return command instanceof VocalizationCommand;
  }

  @Override
  protected void configure(VocalizationCommand command, IAgent agent,
      IEfferentObject object)
  {
    // noop
    
  }

  @Override
  protected VocalizationCommand create(IIdentifier commandId,
      IIdentifier muscleId)
  {
    return new VocalizationCommand(commandId, muscleId);
  }

}
