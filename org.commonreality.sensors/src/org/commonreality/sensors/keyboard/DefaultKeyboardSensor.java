package org.commonreality.sensors.keyboard;

/*
 * default logging
 */
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.commonreality.executor.InlineExecutor;
import org.commonreality.object.IAgentObject;
import org.commonreality.sensors.AbstractSensor;
import org.commonreality.sensors.ISensor;
import org.commonreality.sensors.handlers.EfferentCommandHandler;
import org.commonreality.sensors.handlers.ICommandTimingEquation;
import org.commonreality.sensors.keyboard.map.ACTRDeviceMap;
import org.commonreality.sensors.keyboard.map.IDeviceMap;
import org.commonreality.sensors.motor.IActuator;
import org.commonreality.sensors.motor.interpolator.BasicInterpolator;
import org.commonreality.sensors.motor.interpolator.IActuatorCompletion;
import org.commonreality.sensors.motor.interpolator.IInterpolator;
import org.commonreality.sensors.motor.interpolator.InterpolatorActuator;
import org.commonreality.time.IAuthoritativeClock;
import org.slf4j.LoggerFactory;

/**
 * generic keyboard & mouse handler.
 * 
 * @author harrison
 */
public class DefaultKeyboardSensor extends AbstractSensor
{
  /**
   * Logger definition
   */
  static private final transient org.slf4j.Logger LOGGER            = LoggerFactory
                                                           .getLogger(DefaultKeyboardSensor.class);

  static public final String         DEVICE_MAP        = "DeviceMap";

  static public final String         ACTUATOR_PARAM    = "Actuator";

  static public final String         DURATION_EQUATION = "DurationEquation";

  private IDeviceMap                 _deviceMap;

  private ICommandTimingEquation     _durationEquation;

  private IInterpolator              _interpolator;

  private IActuator                  _actuator;

  private IActuatorCompletion        _completion;

  private ExecutorService            _executor;

  private Runnable                   _cycle;

  private EfferentCommandHandler                  _handler;

  private volatile boolean           _shouldStop       = false;

  private volatile boolean           _shouldSuspend    = false;

  @Override
  public String getName()
  {
    return "keyboard";
  }

  @Override
  public void configure(Map<String, String> options) throws Exception
  {
    /*
     * we can only be configured once
     */
    if (_actuator == null)
    {
      _executor = Executors.newSingleThreadExecutor(getCentralThreadFactory());

      /*
       * create the duration equation.
       */
      ICommandTimingEquation equation = null;
      try
      {
        equation = (ICommandTimingEquation) getClass().getClassLoader()
            .loadClass(options.get(DURATION_EQUATION)).getConstructor()
            .newInstance();
      }
      catch (Exception e)
      {
        if (LOGGER.isDebugEnabled())
          LOGGER.debug("Could not create ICommandDurationEquation ", e);
        equation = new SerialDurationEquation();
      }

      _durationEquation = equation;

      /*
       * now we need the device map
       */
      try
      {
        _deviceMap = (IDeviceMap) getClass().getClassLoader().loadClass(
            options.get(DEVICE_MAP)).getConstructor().newInstance();
      }
      catch (Exception e)
      {
        if (LOGGER.isDebugEnabled())
          LOGGER.debug("Could not create IDeviceMap ", e);
        _deviceMap = new ACTRDeviceMap();
      }

      /*
       * we need a motor command handler, this will install itself. Since the
       * actual processing of commands is relatively quick and easy with no
       * blocking calls, we can run this inline on the IO thread
       */
      KeyboardMotorHandler delegate = new KeyboardMotorHandler();

      delegate.setTimingEquation(_durationEquation);

      _handler = new EfferentCommandHandler(this);
      _handler.add(delegate);

      getEfferentCommandManager().addListener(_handler, InlineExecutor.get());

      /*
       * create the actual actuator to use to execute the motor commands
       */

      /*
       * now we need the device map
       */
      IKeyboardActuator actuator = null;
      try
      {
        actuator = (IKeyboardActuator) getClass().getClassLoader()
            .loadClass(
                options.get(ACTUATOR_PARAM))
            .getConstructor().newInstance();
        actuator.setDevice(_deviceMap);
        actuator.setHandler(_handler);
      }
      catch (Exception e)
      {
        if (LOGGER.isDebugEnabled())
          LOGGER.debug("Could not create actuator ", e);
        actuator = new DefaultActuator(_handler, _deviceMap);
      }


      _actuator = actuator;
      _completion = actuator;

      _interpolator = new BasicInterpolator(_handler, _actuator, _completion);

      delegate.setActuator(new InterpolatorActuator(_interpolator));
    }

    super.configure(options);
  }

  @Override
  public void initialize() throws Exception
  {
    checkState(State.CONNECTED);

    _cycle = new Runnable() {


      public void run()
      {
        double currentTime = getClock().getTime();
        double nextTime = Double.NaN;

        try
        {
          nextTime = _interpolator.update(currentTime);
        }
        catch (Exception e)
        {
          /**
           * Error : Un
           */
          LOGGER.error("unknown exception during interpolation ", e);
        }

        if (LOGGER.isDebugEnabled()) LOGGER.debug(
            "Next update time : " + nextTime + " current:" + currentTime);
        /*
         * if we should keep running, queue up again
         */
        CompletableFuture<Double> result = null;
        if (!(_shouldStop || _shouldSuspend))
        {
          IAuthoritativeClock auth = getClock().getAuthority().get();
          if (LOGGER.isDebugEnabled()) LOGGER.debug("Waiting");
          if (Double.isNaN(nextTime) || nextTime <= currentTime)
            result = auth.requestAndWaitForChange(null);
          else
            result = auth.requestAndWaitForTime(nextTime, null);
          if (LOGGER.isDebugEnabled()) LOGGER.debug("Resuming");

          result.thenAccept((d) -> {
            if (!(_shouldStop || _shouldSuspend)) _executor.execute(this);
          });
        }

      }
    };

    super.initialize();
  }

  @Override
  public void start() throws Exception
  {
    checkState(State.INITIALIZED);

    _shouldStop = false;
    _shouldSuspend = false;
    _executor.execute(_cycle);
    super.start();
  }

  @Override
  public void stop() throws Exception
  {
    checkState(State.STARTED, State.SUSPENDED);
    _shouldStop = true;
    super.stop();
  }

  @Override
  public void suspend() throws Exception
  {
    checkState(State.STARTED);
    _shouldSuspend = true;
    super.suspend();
  }

  @Override
  public void resume() throws Exception
  {
    checkState(State.SUSPENDED);
    _shouldSuspend = false;
    _executor.execute(_cycle);
    super.resume();
  }

  /**
   * when we detect a new agent, queue up a request to add the muscles and
   * devices for the hand, keyboard and mouse
   * 
   * @see org.commonreality.sensors.AbstractSensor#agentAdded(org.commonreality.object.IAgentObject)
   */
  @Override
  protected void agentAdded(final IAgentObject agent)
  {
    super.agentAdded(agent);
    final ISensor sensor = this;

    /**
     * the creation of the muscles must be done off of the IO thread since it
     * may block
     */
    Runnable adder = new Runnable() {
      public void run()
      {
        if (LOGGER.isDebugEnabled())
          LOGGER.debug("Creating hands for " + agent.getIdentifier());
        MuscleUtilities util = new MuscleUtilities(sensor, agent);
        util.create(true, true, _deviceMap);
      }
    };

    _executor.execute(adder);
  }

  public IActuator getActuator()
  {
    return _actuator;
  }

  public void setActuator(IKeyboardActuator actuator)
  {
    _actuator = actuator;
    _completion = actuator;
    actuator.setDevice(_deviceMap);
    actuator.setHandler(_handler);
    _interpolator.setActuator(_actuator);
    _interpolator.setActuatorCompletion(_completion);
  }

  public IDeviceMap getDeviceMap()
  {
    return _deviceMap;
  }
}
