package org.commonreality.sensors.handlers;

/*
 * default logging
 */
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.Executor;

import org.commonreality.efferent.IEfferentCommand;
import org.commonreality.efferent.IEfferentCommandManager;
import org.commonreality.efferent.IEfferentCommandTemplate;
import org.commonreality.efferent.event.IEfferentCommandListener;
import org.commonreality.executor.InlineExecutor;
import org.commonreality.identifier.IIdentifier;
import org.commonreality.net.message.command.object.IObjectCommand;
import org.commonreality.net.message.request.object.ObjectCommandRequest;
import org.commonreality.net.message.request.object.ObjectDataRequest;
import org.commonreality.object.IAgentObject;
import org.commonreality.object.IEfferentObject;
import org.commonreality.object.IMutableObject;
import org.commonreality.object.delta.DeltaTracker;
import org.commonreality.object.delta.IObjectDelta;
import org.commonreality.object.identifier.ISensoryIdentifier;
import org.commonreality.object.manager.event.IObjectEvent;
import org.commonreality.sensors.ISensor;
import org.slf4j.LoggerFactory;

/**
 * general handler to deal with {@link IEfferentCommand}s. After instantiating a
 * sensor, this can be attached to the {@link IEfferentCommandManager} by using
 * the constructor and providing the appropriate {@link Executor}. <br/>
 * By default, all methods ignore all {@link IEfferentCommand}s. To change this
 * behavior override any of the shouldXXXX methods. If you overload the actual
 * state change methods, be sure to call the super method as well as it
 * invariably sets specific state variables in the {@link IEfferentCommand}. At
 * the termination of a command's run be sure to call the
 * {@link #completed(IEfferentCommand, Object)} or
 * {@link #aborted(IEfferentCommand, Object)} methods.<br>
 * <br>
 * The processing of efferent commands should usually be done on the thread that
 * received the event (the io executor). This is because most participants
 * expect that the state change requests will be processed immediately, as
 * opposed to queued up on a clock limited thread. The easiest way to do this is
 * to set the {@link Executor} to be {@link InlineExecutor#get()}.
 * 
 * @author harrison
 */
public class EfferentCommandHandler implements IEfferentCommandListener
{
  /**
   * Logger definition
   */
  static private final transient org.slf4j.Logger LOGGER              = LoggerFactory
                                                                      .getLogger(EfferentCommandHandler.class);

  static private final String                 ASSOCIATED_DELEGATE = "AssociatedDelegate";

  private ISensor                             _sensor;

  private ICommandTimingEquation              _durationEquation;

  private Collection<ICommandHandlerDelegate> _delegates;

  public EfferentCommandHandler(ISensor sensor)
  {
    _sensor = sensor;
    _delegates = new ArrayList<ICommandHandlerDelegate>();
  }

  public Collection<ICommandHandlerDelegate> getDelegates()
  {
    return new ArrayList<ICommandHandlerDelegate>(_delegates);
  }

  public void add(ICommandHandlerDelegate delegate)
  {
    _delegates.add(delegate);
  }

  public void remove(ICommandHandlerDelegate delegate)
  {
    _delegates.remove(delegate);
  }

  public ISensor getSensor()
  {
    return _sensor;
  }

  public void objectsAdded(IObjectEvent<IEfferentCommand, ?> addEvent)
  {
    for (IEfferentCommand command : addEvent.getObjects())
      processNewEfferentCommand(command);
  }

  public void objectsRemoved(IObjectEvent<IEfferentCommand, ?> removeEvent)
  {

  }

  public void objectsUpdated(IObjectEvent<IEfferentCommand, ?> updateEvent)
  {
    IEfferentCommandManager manager = _sensor.getEfferentCommandManager();

    /**
     * since we will receive updates generated by both the agent and the sensor,
     * we only pay attention to requests for a change in state
     */
    for (IObjectDelta delta : updateEvent.getDeltas())
      if (delta.getChangedProperties().contains(
          IEfferentCommand.REQUESTED_STATE))
      {
        IEfferentCommand command = manager.get(delta.getIdentifier());

        if (command == null)
        {
          /*
           * it has already been removed.
           */
          if (LOGGER.isDebugEnabled())
            LOGGER.debug("Command " + delta.getIdentifier()
                + " has already been removed. Update command is stale");
          continue;
        }

        DeltaTracker<IMutableObject> deltaTracker = new DeltaTracker<IMutableObject>(
            command);

        IEfferentCommand.ActualState actual = command.getActualState();
        IEfferentCommand.RequestedState requested = command.getRequestedState();
        boolean started = false;
        boolean aborted = false;
        boolean rejected = false;

        if (actual == IEfferentCommand.ActualState.ACCEPTED
            && requested == IEfferentCommand.RequestedState.START)
        {
          Object result = shouldStart(command);

          if (Boolean.TRUE.equals(result))
          {
            start(deltaTracker);
            started = true;
          }
          else
          {
            reject(deltaTracker, result);
            rejected = true;
          }
        }
        else if (actual == IEfferentCommand.ActualState.RUNNING
            && requested == IEfferentCommand.RequestedState.ABORT)
          if (shouldAbort(command))
          {
            abort(deltaTracker);
            aborted = true;
          }

        if (aborted || started || rejected)
        {
          _sensor.send(new ObjectDataRequest(_sensor.getIdentifier(), command
              .getIdentifier().getAgent(), Collections.singleton(deltaTracker
              .getDelta())));
          _sensor.send(new ObjectCommandRequest(_sensor.getIdentifier(),
              command.getIdentifier().getAgent(), IObjectCommand.Type.UPDATED,
              Collections.singleton((IIdentifier) command.getIdentifier())));

          ICommandHandlerDelegate delegate = (ICommandHandlerDelegate) command
              .getProperty(ASSOCIATED_DELEGATE);

          IAgentObject agent = _sensor.getAgentObjectManager().get(
              command.getIdentifier().getAgent());

          if (aborted)
            delegate.aborted(command, agent, this);
          else if (started)
            delegate.started(command, agent, this);
          else
            delegate.rejected(command, agent, this);
        }

      }
  }

  /**
   * process a new efferent command, checking
   * {@link #shouldAccept(IEfferentCommand)} and either
   * {@link #accept(DeltaTracker)} or {@link #reject(DeltaTracker)}
   * 
   * @param command
   */
  public void processNewEfferentCommand(IEfferentCommand command)
  {
    DeltaTracker<IMutableObject> delta = new DeltaTracker<IMutableObject>(
        command);

    Object result = shouldAccept(command);
    if (Boolean.TRUE.equals(result))
      accept(delta);
    else
      reject(delta, result);

    _sensor.send(new ObjectDataRequest(_sensor.getIdentifier(), command
        .getIdentifier().getAgent(), Collections.singleton(delta.getDelta())));
    _sensor.send(new ObjectCommandRequest(_sensor.getIdentifier(), command
        .getIdentifier().getAgent(), IObjectCommand.Type.UPDATED, Collections
        .singleton((IIdentifier) command.getIdentifier())));
  }

  /**
   * return true if this command should be accepted (state changed from
   * submitted to accepted).<br>
   * <br>
   * Default impl preforms some security and consistency checks. First it makes
   * sure that the agent associated with the command and the efferent object
   * match. Then it makes sure that the {@link IEfferentCommandTemplate}
   * associated with the {@link IEfferentObject} is consistent with the
   * {@link IEfferentCommand}
   * 
   * @param command
   * @return Boolean.TRUE if it should be accepted. Any other value will be used
   *         as the explanation for the rejection
   */
  protected Object shouldAccept(IEfferentCommand command)
  {
    IAgentObject agent = _sensor.getAgentObjectManager().get(
        command.getIdentifier().getAgent());
    Object lastRtn = "No registered handlers for command";

    for (ICommandHandlerDelegate delegate : _delegates)
      if (delegate.isInterestedIn(command))
      {
        lastRtn = delegate.shouldAccept(command, agent, this);
        if (Boolean.TRUE.equals(lastRtn))
        {
          if (LOGGER.isDebugEnabled())
            LOGGER.debug(delegate + " wants to accept " + command);
          // we store the delegate so that we dont have to iterate later
          ((IMutableObject) command).setProperty(ASSOCIATED_DELEGATE, delegate);
          break;
        }
      }

    if (LOGGER.isDebugEnabled()) LOGGER.debug("Command accepted : " + lastRtn);

    return lastRtn;
  }

  /**
   * when a command is accepted, this is called. the default impl should always
   * be called (i.e. super.accept()), as it sets the appropriate states for the
   * command. after this returns, the contents of the delta are written out.
   * 
   * @param efferentCommand
   *          delta tracker that wraps the efferent command
   */
  protected void accept(DeltaTracker<IMutableObject> efferentCommand)
  {
    IAgentObject agent = _sensor.getAgentObjectManager().get(
        ((ISensoryIdentifier) efferentCommand.getIdentifier()).getAgent());

    efferentCommand.setProperty(IEfferentCommand.ACTUAL_STATE,
        IEfferentCommand.ActualState.ACCEPTED);

    ICommandHandlerDelegate delegate = (ICommandHandlerDelegate) efferentCommand
        .getProperty(ASSOCIATED_DELEGATE);

    delegate.getTimingEquation((IEfferentCommand) efferentCommand.get(), agent,
        this).computeTimings(efferentCommand);
  }

  /**
   * when a command is rejected, this is called. the default impl should always
   * be called (i.e. super.reject()), as it sets the appropriate states for the
   * command. after this returns, the contents of the delta are written out.
   * 
   * @param efferentCommand
   *          delta tracker that wraps the efferent command
   */
  protected void reject(DeltaTracker<IMutableObject> efferentCommand,
      Object details)
  {
    efferentCommand.setProperty(IEfferentCommand.ACTUAL_STATE,
        IEfferentCommand.ActualState.REJECTED);
    efferentCommand.setProperty(IEfferentCommand.RESULT, details);

    if (LOGGER.isWarnEnabled())
      LOGGER.warn("Rejecting command " + efferentCommand.getIdentifier()
          + " because " + details);
  }

  /**
   * check the command and be sure that it can be started. All commands that get
   * here have an actual state of ACCEPTED. default returns false
   * 
   * @param command
   * @return Boolean.TRUE if should start, any other return value will be used
   *         as the details for a rejection
   */
  protected Object shouldStart(IEfferentCommand command)
  {
    IAgentObject agent = _sensor.getAgentObjectManager().get(
        command.getIdentifier().getAgent());
    ICommandHandlerDelegate delegate = (ICommandHandlerDelegate) command
        .getProperty(ASSOCIATED_DELEGATE);

    if (delegate == null) return "Command was never accepted";

    return delegate.shouldStart(command, agent, this);
  }

  /**
   * when a command is started, this is called. the default impl should always
   * be called (i.e. super.reject()), as it sets the appropriate states for the
   * command. after this returns, the contents of the delta are written out.
   * 
   * @param efferentCommand
   *          delta tracker that wraps the efferent command
   */
  protected void start(DeltaTracker<IMutableObject> efferentCommand)
  {
    efferentCommand.setProperty(IEfferentCommand.ACTUAL_STATE,
        IEfferentCommand.ActualState.RUNNING);

    ICommandHandlerDelegate delegate = (ICommandHandlerDelegate) efferentCommand
        .get().getProperty(ASSOCIATED_DELEGATE);

    IAgentObject agent = _sensor.getAgentObjectManager().get(
        ((ISensoryIdentifier) efferentCommand.getIdentifier()).getAgent());

    delegate.start((IEfferentCommand) efferentCommand.get(), agent, this);
  }

  /**
   * check the command and be sure that it can be aborted. All commands that get
   * here have an actual state of RUNNING. default returns false
   * 
   * @param command
   * @return
   */
  protected boolean shouldAbort(IEfferentCommand command)
  {
    IAgentObject agent = _sensor.getAgentObjectManager().get(
        command.getIdentifier().getAgent());
    ICommandHandlerDelegate delegate = (ICommandHandlerDelegate) command
        .getProperty(ASSOCIATED_DELEGATE);

    if (delegate == null) return false;

    return delegate.shouldAbort(command, agent, this);
  }

  /**
   * when a command is abort, this is called. the default impl should always be
   * called (i.e. super.reject()), as it sets the appropriate states for the
   * command. after this returns, the contents of the delta are written out.
   * 
   * @param efferentCommand
   *          delta tracker that wraps the efferent command
   */
  protected void abort(DeltaTracker<IMutableObject> efferentCommand)
  {
    // the handler should call aborted() when complete
    // efferentCommand.setProperty(IEfferentCommand.ACTUAL_STATE,
    // IEfferentCommand.ActualState.ABORTED);

    ((ICommandHandlerDelegate) efferentCommand.get().getProperty(
        ASSOCIATED_DELEGATE)).abort((IEfferentCommand) efferentCommand.get(),
        _sensor.getAgentObjectManager().get(
            ((ISensoryIdentifier) efferentCommand.getIdentifier()).getAgent()),
        this);
  }

  /**
   * called when a command is aborted on the sensor side (not at the request of
   * the agent).
   * 
   * @param command
   * @param extraInfo
   *          will be stored under {@link IEfferentCommand#RESULT}
   */
  public void aborted(IEfferentCommand command, Object extraInfo)
  {
    terminated(command, extraInfo, IEfferentCommand.ActualState.ABORTED);
  }

  /**
   * called upon the completion of a command
   * 
   * @param command
   * @param extraInfo
   */
  public void completed(IEfferentCommand command, Object extraInfo)
  {
    terminated(command, extraInfo, IEfferentCommand.ActualState.COMPLETED);
  }

  private void terminated(IEfferentCommand command, Object extraInfo,
      IEfferentCommand.ActualState state)
  {
    /**
     * we only send out updates for commands that are registered
     */
    if (_sensor.getEfferentCommandManager().get(command.getIdentifier()) != null)
    {
      if (LOGGER.isDebugEnabled())
        LOGGER.debug("Setting " + command.getIdentifier() + " state to "
            + state);

      DeltaTracker<IMutableObject> deltaTracker = new DeltaTracker<IMutableObject>(
          command);

      deltaTracker.setProperty(IEfferentCommand.ACTUAL_STATE, state);
      deltaTracker.setProperty(IEfferentCommand.RESULT, extraInfo);

      _sensor.send(new ObjectDataRequest(_sensor.getIdentifier(), command
          .getIdentifier().getAgent(), Collections.singleton(deltaTracker
          .getDelta())));
      _sensor.send(new ObjectCommandRequest(_sensor.getIdentifier(), command
          .getIdentifier().getAgent(), IObjectCommand.Type.UPDATED, Collections
          .singleton((IIdentifier) command.getIdentifier())));
    }
    else
    {
      ((IMutableObject) command).setProperty(IEfferentCommand.ACTUAL_STATE,
          state);
      ((IMutableObject) command)
          .setProperty(IEfferentCommand.RESULT, extraInfo);
    }
  }
}
