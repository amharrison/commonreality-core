package org.commonreality.net.impl;

/*
 * default logging
 */
 
import org.slf4j.LoggerFactory;
import org.commonreality.net.session.ISessionInfo;
import org.commonreality.net.session.ISessionListener;

public class DefaultSessionListener implements ISessionListener
{
  /**
   * Logger definition
   */
  static private final transient org.slf4j.Logger LOGGER = LoggerFactory
                                                .getLogger(DefaultSessionListener.class);

  public DefaultSessionListener()
  {
  }

  @Override
  public void opened(ISessionInfo<?> session)
  {
    if (LOGGER.isDebugEnabled())
      LOGGER.debug(String.format("Opened %s", session));

  }

  @Override
  public void closed(ISessionInfo<?> session)
  {
    if (LOGGER.isDebugEnabled())
      LOGGER.debug(String.format("Closed %s", session));

  }

  @Override
  public void created(ISessionInfo<?> session)
  {
    if (LOGGER.isDebugEnabled())
      LOGGER.debug(String.format("Created %s", session));

  }

  @Override
  public void destroyed(ISessionInfo<?> session)
  {
    if (LOGGER.isDebugEnabled())
      LOGGER.debug(String.format("Destroyed %s", session));

  }

}
