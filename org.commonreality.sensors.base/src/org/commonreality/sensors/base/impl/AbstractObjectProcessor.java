package org.commonreality.sensors.base.impl;

/*
 * default logging
 */
import java.util.Map;

 
import org.slf4j.LoggerFactory;
import org.commonreality.sensors.base.IObjectProcessor;
import org.commonreality.sensors.base.PerceptManager;

public abstract class AbstractObjectProcessor implements IObjectProcessor<DefaultObjectKey>
{
  /**
   * Logger definition
   */
  static private final transient org.slf4j.Logger LOGGER = LoggerFactory
                                                .getLogger(AbstractObjectProcessor.class);

  public void configure(Map<String, String> options)
  {
    // noop
    
  }


  public void installed(PerceptManager manager)
  {
    // noop
    
  }


  public void uninstalled(PerceptManager manager)
  {
    // noop
    
  }

}
