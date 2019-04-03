package org.commonreality.time.impl;

/*
 * default logging
 */
import java.util.Optional;

import org.commonreality.time.IAuthoritativeClock;
import org.commonreality.time.IClock;
 
import org.slf4j.LoggerFactory;

/**
 * 
 * @author harrison
 *
 */
public abstract class AbstractAuthoritativeClock extends WrappedClock implements IAuthoritativeClock
{
  /**
   * Logger definition
   */
  static private final transient org.slf4j.Logger LOGGER = LoggerFactory
      .getLogger(AbstractAuthoritativeClock.class);

  public AbstractAuthoritativeClock(IClock master)
  {
    super(master);
  }

  @Override
  public Optional<IAuthoritativeClock> getAuthority()
  {
    return Optional.of(this);
  }

}
