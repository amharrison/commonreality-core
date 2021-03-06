package org.commonreality.sensors.swing.processors;

import java.awt.Color;
import java.awt.Component;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;

import org.commonreality.modalities.visual.IVisualPropertyHandler;
import org.commonreality.object.IMutableObject;
import org.commonreality.sensors.base.IObjectProcessor;
import org.commonreality.sensors.base.PerceptManager;
import org.commonreality.sensors.base.impl.AbstractObjectCreator;
import org.commonreality.sensors.base.impl.DefaultObjectKey;
import org.commonreality.sensors.swing.internal.Coordinates;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractCreatorProcessor extends AbstractObjectCreator
    implements IObjectProcessor<DefaultObjectKey>
{

  static private transient Logger    LOGGER = LoggerFactory
      .getLogger(AbstractCreatorProcessor.class);

  protected Coordinates              _coordinates;

  private Class<? extends Component> _componentClass;

  private PerceptManager             _perceptManager;

  public AbstractCreatorProcessor(Coordinates coordinates,
      Class<? extends Component> componentClass)
  {
    _coordinates = coordinates;
    _componentClass = componentClass;
  }

  @Override
  public void installed(PerceptManager manager)
  {
    super.installed(manager);
    _perceptManager = manager;
  }

  public PerceptManager getPerceptManager()
  {
    return _perceptManager;
  }

  public Class<? extends Component> getComponentClass()
  {
    return _componentClass;
  }

  abstract protected String getText(Component component);

  abstract protected String[] calculateTypes(Component component);

  /**
   * checks all the ancestors' visibibilty
   * 
   * @param component
   * @return
   */
  protected boolean calculateVisibility(Component component)
  {
    boolean visible = true;
    while (component != null)
    {
      visible &= component.isVisible() && component.isDisplayable();
      component = component.getParent();
    }
    /*
     * reliable disposal events are hard to come by in swing, instead when it is
     * no longer visible we flag it for removal on the next cycle.
     */
    if (!visible) getPerceptManager().flagForRemoval(component);

    return visible;
  }

  /**
   * calculate the visible bounds. Currently this just takes the bounds and
   * converts them to retinotopic. Ideally this should check the hiearchy for
   * viewports and calculate the clipping bounds.
   * 
   * @param component
   * @return
   */
  protected Rectangle2D calculateBounds(Component component)
  {
    Rectangle rect = component.getBounds();
    return _coordinates.toRetinotopic(rect, component.getParent());
  }

  @Override
  public void deleted(DefaultObjectKey arg0)
  {

  }

  @Override
  public boolean handles(DefaultObjectKey arg0)
  {
    return _componentClass.isInstance(arg0.getObject())
        && arg0.getCreator() == this;
  }

  @Override
  public boolean handles(Object arg0)
  {
    return _componentClass.isInstance(arg0)
        && calculateVisibility((Component) arg0);
  }

  @Override
  protected void initialize(DefaultObjectKey objectKey,
      IMutableObject afferentPercept)
  {
    super.initialize(objectKey, afferentPercept);

    Component component = (Component) objectKey.getObject();
    String[] types = calculateTypes(component);

    afferentPercept.setProperty(IVisualPropertyHandler.IS_VISUAL, Boolean.TRUE);

    String name = component.getName();
    if (name == null) name = component.toString();

    // static
    afferentPercept.setProperty(IVisualPropertyHandler.TYPE, types);
    afferentPercept.setProperty(IVisualPropertyHandler.TOKEN, name);
  }

  private boolean wasTrueNowFalse(IMutableObject afferentPercept,
      boolean currentVisibility)
  {
    if (!afferentPercept.hasProperty(IVisualPropertyHandler.VISIBLE))
      return false;
    boolean lastValue = (Boolean) afferentPercept
        .getProperty(IVisualPropertyHandler.VISIBLE);
    return lastValue && !currentVisibility;
  }

  @Override
  public void process(DefaultObjectKey objectKey,
      IMutableObject afferentPercept)
  {
    Component component = (Component) objectKey.getObject();

    if (LOGGER.isDebugEnabled()) LOGGER.debug("Processing " + component);

    /*
     * we calculate the visibility which can flag the component for removal if
     * it is hidden. we ignore the result and just assume visibility in the
     * properties, though. This is intentional. Otherwise we'd mark it as
     * invisible then remove, when we really just want to remove it as it is.
     */
    boolean visibility = calculateVisibility(component);

    if (wasTrueNowFalse(afferentPercept, visibility)) return; // change nothing
                                                              // on remove, just
                                                              // remove

    afferentPercept.setProperty(IVisualPropertyHandler.VISIBLE, true);

    Color color = component.getForeground();
    afferentPercept.setProperty(IVisualPropertyHandler.COLOR,
        new double[] { color.getRed() / 255f, color.getGreen() / 255f,
            color.getBlue() / 255f, color.getAlpha() / 255f });

    Rectangle2D rect = calculateBounds(component);
    afferentPercept.setProperty(IVisualPropertyHandler.RETINAL_LOCATION,
        new double[] { rect.getCenterX(), rect.getCenterY() });
    afferentPercept.setProperty(IVisualPropertyHandler.RETINAL_SIZE,
        new double[] { rect.getWidth(), rect.getHeight() });

    String text = getText(component);
    if (text != null && !"".equals(text))
    {
      afferentPercept.setProperty(IVisualPropertyHandler.TEXT, text);
      afferentPercept.setProperty(IVisualPropertyHandler.TOKEN, text);
    }
  }

}
