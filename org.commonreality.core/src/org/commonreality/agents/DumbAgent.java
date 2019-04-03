package org.commonreality.agents;

 
import org.slf4j.LoggerFactory;

public class DumbAgent extends AbstractAgent
{
  /**
   * Logger definition
   */
  static private final transient org.slf4j.Logger LOGGER              = LoggerFactory
                                                             .getLogger(DumbAgent.class);

  private boolean                    _participatesInTime = true;

  public DumbAgent()
  {
  }

  @Override
  public String getName()
  {
    return "dumb";
  }

  @Override
  public void start() throws Exception
  {
    super.start();
    if (_participatesInTime) getPeriodicExecutor().execute(this::timeLoop);
  }

  protected void timeLoop()
  {
    if (stateMatches(State.STARTED) && _participatesInTime)
      getClock().getAuthority().get().requestAndWaitForChange(null)
          .thenAccept((t) -> {
            timeLoop();
          });
  }

}
