package org.commonreality.notification.impl;

/*
 * default logging
 */
 
import org.slf4j.LoggerFactory;
import org.commonreality.identifier.IIdentifier;

public class SimpleStringNotification extends AbstractNotification
{
  /**
   * 
   */
  private static final long          serialVersionUID = -6096212488946030873L;

  /**
   * Logger definition
   */
  static private final transient org.slf4j.Logger LOGGER = LoggerFactory
                                                .getLogger(SimpleStringNotification.class);

  private final String               _message;

  public SimpleStringNotification(IIdentifier notificationId, String message)
  {
    super(notificationId);
    _message = message;
  }

  public String getMessage()
  {
    return _message;
  }
}
