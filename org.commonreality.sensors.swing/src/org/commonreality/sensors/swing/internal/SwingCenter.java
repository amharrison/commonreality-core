package org.commonreality.sensors.swing.internal;

import java.awt.Component;
import java.awt.Container;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;

import org.commonreality.reality.CommonReality;
import org.commonreality.sensors.base.BaseSensor;
import org.commonreality.sensors.base.IObjectCreator;
import org.commonreality.sensors.base.IObjectProcessor;
import org.commonreality.sensors.keyboard.DefaultKeyboardSensor;
import org.commonreality.sensors.swing.listeners.GlobalComponentListener;
import org.commonreality.sensors.swing.listeners.GlobalWindowListener;
import org.commonreality.sensors.swing.listeners.HierarchyListener;
import org.commonreality.sensors.swing.processors.AbstractCreatorProcessor;
import org.commonreality.sensors.swing.processors.ButtonCreatorProcessor;
import org.commonreality.sensors.swing.processors.LabelCreatorProcessor;
import org.commonreality.sensors.swing.processors.MenuCreatorProcessor;
import org.commonreality.sensors.swing.processors.MouseCreatorProcessor;
import org.commonreality.sensors.swing.processors.TextFieldCreatorProcessor;
import org.commonreality.sensors.swing.processors.ToggleButtonCreatorProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SwingCenter
{

  static private transient Logger                                   LOGGER         = LoggerFactory
      .getLogger(SwingCenter.class);

  private GlobalWindowListener                                      _windowListener;

  private HierarchyListener                                         _hierarchyListener;

  private GlobalComponentListener                                   _componentListener;

  private Map<Class<? extends Component>, AbstractCreatorProcessor> _processors    = new HashMap<>();

  private Predicate<Container>                                      _shouldDescend = (
      c) -> {
                                                                                     return true;
                                                                                   };

  private Predicate<Component>                                      _shouldAccept  = (
      c) -> {
                                                                                     boolean rtn = _processors
                                                                                         .keySet()
                                                                                         .stream()
                                                                                         .anyMatch(
                                                                                             clazz -> {
                                                                                                                                                                                return clazz
                                                                                                                                                                                    .isInstance(
                                                                                                                                                                                        c);
                                                                                                                                                                              });
                                                                                     return rtn;
                                                                                   };

  private BaseSensor                                                _sensor;

  private Coordinates                                               _coordinates;

  private MouseState                                                _mouseState    = new MouseState();

  public SwingCenter(BaseSensor sensor, Coordinates coordinates)
  {
    _sensor = sensor;
    _coordinates = coordinates;
  }

  public void newCycle()
  {
    _sensor.getPerceptManager().markAsDirty(_mouseState);
  }

  public void install()
  {
    Consumer<Component> add = (c) -> {
      _sensor.getPerceptManager().markAsDirty(c);
      if (LOGGER.isDebugEnabled()) LOGGER.debug("Adding " + c);
    };
    Consumer<Component> remove = (c) -> {
      _sensor.getPerceptManager().flagForRemoval(c);
      if (LOGGER.isDebugEnabled()) LOGGER.debug("Removing " + c);
    };

    _windowListener = new GlobalWindowListener(_shouldDescend, _shouldAccept,
        add, remove);
    _windowListener.install();
    _componentListener = new GlobalComponentListener(_shouldDescend,
        _shouldAccept, add, remove);
    _componentListener.install();
    _hierarchyListener = new HierarchyListener(_sensor, _shouldDescend,
        _shouldAccept);
    _hierarchyListener.install();

    configureKeyboardSensor();

    MouseCreatorProcessor mcp = new MouseCreatorProcessor(_coordinates);
    _sensor.getPerceptManager().install((IObjectCreator) mcp);
    _sensor.getPerceptManager().install((IObjectProcessor) mcp);

    add(new ButtonCreatorProcessor(_coordinates));
    add(new ToggleButtonCreatorProcessor(_coordinates));
    add(new TextFieldCreatorProcessor(_coordinates));
    add(new MenuCreatorProcessor(_coordinates));
    add(new LabelCreatorProcessor(_coordinates));
  }

  public void uninstall()
  {
    _windowListener.uninstall();
    _hierarchyListener.uninstall();
    _componentListener.uninstall();
  }

  public void add(AbstractCreatorProcessor processor)
  {
    _processors.put(processor.getComponentClass(), processor);
    _sensor.getPerceptManager().install((IObjectCreator) processor);
    _sensor.getPerceptManager().install((IObjectProcessor) processor);
  }

  public void remove(AbstractCreatorProcessor processor)
  {
    _processors.remove(processor.getComponentClass());
    // TODO add uninstall
  }

  protected void configureKeyboardSensor()
  {
    DefaultKeyboardSensor sensor = (DefaultKeyboardSensor) CommonReality
        .getSensors().stream().filter(DefaultKeyboardSensor.class::isInstance)
        .findAny().get();
    sensor.setActuator(new SwingActuator(_coordinates));

  }
}
