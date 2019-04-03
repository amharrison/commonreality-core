package org.commonreality.time.impl;

import org.commonreality.agents.IAgent;
import org.commonreality.participant.IParticipant;
import org.commonreality.reality.CommonReality;
import org.commonreality.reality.IReality;
import org.commonreality.sensors.ISensor;
import org.commonreality.time.IClock;
import org.commonreality.time.impl.OwnedClock.OwnedAuthoritativeClock;
/*
 * default logging
 */
 
import org.slf4j.LoggerFactory;

public class ClockInterrogator
{
  /**
   * Logger definition
   */
  static private final transient org.slf4j.Logger LOGGER = LoggerFactory
      .getLogger(ClockInterrogator.class);

  static public String getAllClockDetails()
  {
    StringBuilder sb = new StringBuilder();

    IReality reality = CommonReality.getReality();
    if (reality != null) sb.append(getClockDetails(reality)).append("\n");

    for (ISensor sensor : CommonReality.getSensors())
      sb.append(getClockDetails(sensor)).append("\n");
    for (IAgent agent : CommonReality.getAgents())
      sb.append(getClockDetails(agent)).append("\n");

    return sb.toString();
  }

  static protected String getClockDetails(IParticipant participant)
  {
    StringBuilder sb = new StringBuilder(participant.getIdentifier().toString());
    IClock clock = participant.getClock();
    sb.append(" [").append(clock.getClass().getSimpleName()).append("] ");
    if (clock instanceof BasicClock)
    {
      BasicClock bc = (BasicClock) clock;
      sb.append(String.format(" lock[%d] ", bc.getLock().hashCode()));
      sb.append(String.format(
              "lastUpdate: %.4f updateTime=%d lastRequest: %.4f requestTime=%d lastRequestingThread=%s",
          bc.getLastUpdateLocalTime(), bc.getLastUpdateSystemTime(),
              bc.getLastRequestLocalTime(), bc.getLastRequestSystemTime(),
              bc.getLastRequestingThread()));

      if (clock instanceof OwnedClock)
      {
        OwnedClock.OwnedAuthoritativeClock auth = (OwnedAuthoritativeClock) clock
            .getAuthority().get();
        sb.append("Last request key : ").append(auth.getLastRequestKey());

        sb.append(String.format("\n\t Waiting on : %s\n",
            auth.getUnaccountedForOwners()));
        sb.append("\t Last Heard From : ");
        auth.getLastAccessTimes().entrySet().forEach((e) -> {
          sb.append(String.format("\n\t\t%s @ %d", e.getKey(), e.getValue()));
        });
      }
      if (clock instanceof NetworkedClock)
      {
        NetworkedClock nc = (NetworkedClock) clock;
        sb.append(String.format("\n\t message send : %d  message sent:%d",
            nc.getPreSendTime(), nc.getPostSendTime()));
      }
    }

    return sb.toString();
  }
}
