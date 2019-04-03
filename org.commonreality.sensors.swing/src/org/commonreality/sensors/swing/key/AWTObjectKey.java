package org.commonreality.sensors.swing.key;

/*
 * default logging
 */
import java.awt.Component;

 
import org.slf4j.LoggerFactory;
import org.commonreality.sensors.base.IObjectCreator;
import org.commonreality.sensors.base.impl.DefaultObjectKey;

public class AWTObjectKey extends DefaultObjectKey
{
  /**
   * Logger definition
   */
  static private final transient org.slf4j.Logger LOGGER = LoggerFactory
                                                .getLogger(AWTObjectKey.class);

  public AWTObjectKey(Component component, IObjectCreator<AWTObjectKey> creator)
  {
    super(component, creator);
  }


  public Component getComponent()
  {
    return (Component) getObject();
  }
}
