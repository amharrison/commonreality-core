package org.commonreality.modalities.motor;

/*
 * default logging
 */
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.commonreality.efferent.AbstractEfferentCommand;
import org.commonreality.efferent.ICompoundCommand;
import org.commonreality.efferent.IEfferentCommand;
import org.commonreality.identifier.IIdentifier;
import org.slf4j.LoggerFactory;

public class MovementCommand extends AbstractEfferentCommand
    implements ICompoundCommand
{
  static public final String                      MOVEMENT_ORIGIN  = "MovementCommand.Origin";

  static public final String                      MOVEMENT_TARGET  = "MovementCommand.Target";

  static public final String                      MOVEMENT_RATE    = "MovementCommand.Rate";

  static public final String                      IS_PARALLEL      = "MovementCommand.isParallel";

  /**
   * 
   */
  private static final long                       serialVersionUID = -3419302731749071125L;

  /**
   * Logger definition
   */
  static private final transient org.slf4j.Logger LOGGER           = LoggerFactory
      .getLogger(MovementCommand.class);

  public MovementCommand(IIdentifier identifier)
  {
    super(identifier);
  }

  public MovementCommand(IIdentifier identifier, IIdentifier efferentId)
  {
    this(identifier);
    setEfferentIdentifier(efferentId);
  }

  public double[] getRate()
  {
    try
    {
      return MotorUtilities.getDoubles(MOVEMENT_RATE, this);
    }
    catch (Exception e)
    {
      return new double[0];
    }
  }

  public double[] getTarget()
  {
    try
    {
      return MotorUtilities.getDoubles(MOVEMENT_TARGET, this);
    }
    catch (Exception e)
    {
      return new double[0];
    }
  }

  public double[] getOrigin()
  {
    try
    {
      return MotorUtilities.getDoubles(MOVEMENT_ORIGIN, this);
    }
    catch (Exception e)
    {
      return new double[0];
    }
  }

  public Collection<IEfferentCommand> getComponents()
  {
    try
    {
      return (Collection<IEfferentCommand>) getProperty(COMPONENTS);
    }
    catch (Exception e)
    {
      return Collections.EMPTY_LIST;
    }
  }

  public void add(IEfferentCommand command)
  {
    Collection<IEfferentCommand> composite = getComponents();
    if (composite.size() == 0)
    {
      composite = new ArrayList<IEfferentCommand>();
      setProperty(COMPONENTS, composite);
      setProperty(IS_COMPOUND, Boolean.TRUE);
    }

    composite.add(command);
  }

  public boolean isCompound()
  {
    try
    {
      return (Boolean) getProperty(IS_COMPOUND);
    }
    catch (Exception e)
    {
      return false;
    }
  }

  public boolean isParallel()
  {
    try
    {
      return (Boolean) getProperty(IS_PARALLEL);
    }
    catch (Exception e)
    {
      return false;
    }
  }

  public void setParallel(boolean isParallel)
  {
    setProperty(IS_PARALLEL, isParallel);
  }

}
