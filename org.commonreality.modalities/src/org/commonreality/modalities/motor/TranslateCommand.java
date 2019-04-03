package org.commonreality.modalities.motor;

/*
 * default logging
 */
 
import org.slf4j.LoggerFactory;
import org.commonreality.identifier.IIdentifier;

public class TranslateCommand extends MovementCommand
{
  /**
   * 
   */
  private static final long serialVersionUID = 993759307275979260L;
  /**
   * Logger definition
   */
  static private final transient org.slf4j.Logger LOGGER = LoggerFactory
                                                .getLogger(TranslateCommand.class);


  public TranslateCommand(IIdentifier identifier)
  {
    super(identifier);
  }
  
  public TranslateCommand(IIdentifier identifier, IIdentifier efferentId)
  {
    super(identifier, efferentId);
  }
  
  public void translate(double[] origin, double[] target, double[] rate)
  {
    setProperty(MOVEMENT_ORIGIN, origin);
    setProperty(MOVEMENT_RATE, rate);
    setProperty(MOVEMENT_TARGET, target);
  }
}
