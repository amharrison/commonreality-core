package org.commonreality.modalities.motor;

/*
 * default logging
 */
import java.util.Collection;

 
import org.slf4j.LoggerFactory;
import org.commonreality.identifier.IIdentifier;

public class MotorConstants
{
  /**
   * Logger definition
   */
  static private final transient org.slf4j.Logger LOGGER = LoggerFactory
                                                .getLogger(MotorConstants.class);

  /**
   * general marker for all motor systems (ie. muscles and actuators)
   * {@link Boolean}
   */
  static public final String IS_MOTOR = "motor.isMotor";
  
  /**
   * since many motor systems have parent/child relations, this
   * marks the parent {@link IIdentifier}
   */
  static public final String PARENT_IDENTIFIER = "motor.parent";
  
  /**
   * children of this motor {@link Collection} of {@link IIdentifier}s
   */
  static public final String CHILD_IDENTIFIERS = "motor.children";
  
  /**
   * current position of the motor in the parent's coordinates, a primitive
   * double[] of unspecified length and format
   */
  static public final String         POSITION          = "motor.position";
  
  /**
   * range of positions, again a double[] that is 2xPOSITION. Each pair
   * of doubles represents the minimum and maximum for that position.
   */
  static public final String         POSITION_RANGE    = "motor.positionRange";
  
  /**
   * current movement rate double[] that is POSITION[]/s
   */
  static public final String         RATE              = "motor.rate";
  
  /**
   * range of rates
   */
  static public final String         RATE_RANGE        = "motor.rateRange";
  
  static public final String         NAME              = "motor.name";
  
}
