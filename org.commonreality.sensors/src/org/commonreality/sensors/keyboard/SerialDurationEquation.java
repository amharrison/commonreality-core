package org.commonreality.sensors.keyboard;

import org.commonreality.efferent.IEfferentCommand;
import org.commonreality.modalities.motor.MovementCommand;
import org.commonreality.object.IMutableObject;
import org.commonreality.object.delta.DeltaTracker;
import org.commonreality.sensors.handlers.ICommandTimingEquation;

/*
 * default logging
 */

import org.slf4j.LoggerFactory;

public class SerialDurationEquation implements ICommandTimingEquation
{
  /**
   * Logger definition
   */
  static private final transient org.slf4j.Logger LOGGER = LoggerFactory
      .getLogger(SerialDurationEquation.class);

  public double computeTimings(DeltaTracker<IMutableObject> command)
  {
    double duration = computeTimings((IEfferentCommand) command.get(),
        ((IEfferentCommand) command.get()).getRequestedStartTime());

    command.setProperty(IEfferentCommand.ESTIMATED_DURATION, duration);
    return duration;
  }

  public double computeTimings(IEfferentCommand command, double startTime)
  {
    if (command instanceof MovementCommand)
    {
      MovementCommand movement = (MovementCommand) command;
      double duration = 0;

      if (movement.isCompound())
      {
        movement.setProperty(IEfferentCommand.REQUESTED_START_TIME, startTime);
        /*
         * if parallel, they all have the same start time, but different
         * durations
         */
        if (movement.isParallel())
          for (IEfferentCommand com : movement.getComponents())
          {
            double comDuration = computeTimings(com, startTime);

            ((IMutableObject) com)
                .setProperty(IEfferentCommand.REQUESTED_START_TIME, startTime);
            ((IMutableObject) com)
                .setProperty(IEfferentCommand.ESTIMATED_DURATION, comDuration);
            duration = Math.max(duration, comDuration);
          }
        else
        {
          /*
           * if serial, each one starts when the other ends
           */
          double lastStart = startTime;
          for (IEfferentCommand com : movement.getComponents())
          {
            double comDuration = computeTimings(com, lastStart);
            ((IMutableObject) com)
                .setProperty(IEfferentCommand.REQUESTED_START_TIME, lastStart);
            ((IMutableObject) com)
                .setProperty(IEfferentCommand.ESTIMATED_DURATION, comDuration);
            duration += comDuration;
            lastStart += comDuration;
          }
        }
        movement.setProperty(IEfferentCommand.ESTIMATED_DURATION, duration);
      }
      else
      {
        double[] origin = movement.getOrigin();
        double[] target = movement.getTarget();
        double[] rate = movement.getRate();

        for (int i = 0; i < rate.length; i++)
        {
          double tmpDuration = Math.abs((target[i] - origin[i]) / rate[i]);

          if (LOGGER.isDebugEnabled()) LOGGER.debug("o: " + origin[i] + " t: "
              + target[i] + " r: " + rate[i] + " duration: " + tmpDuration);

          if (!Double.isNaN(tmpDuration))
            duration = Math.max(duration, tmpDuration);
        }
      }

      if (LOGGER.isDebugEnabled())
        LOGGER.debug(movement + " starts: " + startTime + " for: " + duration);

      return duration;
    }

    throw new RuntimeException("Can only process movement commands");
  }

}
