package org.commonreality.modalities.motor;

/*
 * default logging
 */
 
import org.slf4j.LoggerFactory;
import org.commonreality.identifier.IIdentifier;

public class RotateCommand extends MovementCommand
{
  /**
   * 
   */
  private static final long serialVersionUID = -8873033692950206435L;
  /**
   * Logger definition
   */
  static private final transient org.slf4j.Logger LOGGER = LoggerFactory
                                                .getLogger(RotateCommand.class);

  public RotateCommand(IIdentifier identifier)
  {
    super(identifier);
  }
  
  public RotateCommand(IIdentifier identifier, IIdentifier efferentId)
  {
    super(identifier, efferentId);
  }

  public void rotate(double[] target, double[] rate)
  {
    setProperty(MOVEMENT_RATE, rate);
    setProperty(MOVEMENT_TARGET, target);
  }
}
