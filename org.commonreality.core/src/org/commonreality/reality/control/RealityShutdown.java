package org.commonreality.reality.control;

/*
 * default logging
 */
 
import org.slf4j.LoggerFactory;
import org.commonreality.participant.IParticipant.State;
import org.commonreality.reality.IReality;

public class RealityShutdown implements Runnable
{
  /**
   * Logger definition
   */
  static private final transient org.slf4j.Logger LOGGER = LoggerFactory
                                                .getLogger(RealityShutdown.class);

  private IReality                   _reality;

  private boolean                    _waitForStop;

  public RealityShutdown(IReality reality, boolean waitForStop)
  {
    _reality = reality;
    _waitForStop = waitForStop;
  }

  public void run()
  {
    if (_reality == null) return;

    try
    {
      if (_reality.stateMatches(State.STARTED, State.SUSPENDED))
      {
        _reality.stop();
        if (_waitForStop) _reality.waitForState(State.STOPPED);
      }
    }
    catch (Exception e)
    {
      LOGGER.error(
          "Could not stop common reality, attempting to proceed to shutdown ",
          e);
    }

    try
    {
      if (!_reality.stateMatches(State.UNKNOWN)) _reality.shutdown();
    }
    catch (Exception e)
    {
      LOGGER.error("Could not shutdown prior common reality instance ", e);
    }
    finally
    {
      _reality = null;
    }

  }

}
