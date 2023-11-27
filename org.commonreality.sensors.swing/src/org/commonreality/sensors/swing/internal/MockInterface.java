package org.commonreality.sensors.swing.internal;

import java.awt.Component;
import java.awt.Container;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.lang.reflect.InvocationTargetException;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.FocusManager;
import javax.swing.JPanel;
import javax.swing.RootPaneContainer;
import javax.swing.SwingUtilities;

public class MockInterface
{

  private Component    _currentComponent;

  private int          _currentMask;

  private int          _currentButtons;

  private Point        _screenLocation    = new Point(0, 0);

  private Point        _windowLocation    = new Point(0, 0);

  private Point        _componentLocation = new Point(0, 0);

  private Set<Integer> _pressed           = new TreeSet<>();

  public void keyPress(int keyCode)
  {
    if (_pressed.contains(keyCode)) return;

    FocusManager fm = FocusManager.getCurrentManager();
    Component c = fm.getFocusOwner();

    _currentMask ^= mask(keyCode);
    char keyChar = getKeyChar(keyCode);
    KeyEvent keyEvent = new KeyEvent(c, KeyEvent.KEY_PRESSED,
        System.currentTimeMillis(), _currentMask, keyCode, keyChar);

    Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(keyEvent);
    _pressed.add(keyCode);
  }

  public void keyRelease(int keyCode)
  {
    if (!_pressed.contains(keyCode)) return;

    FocusManager fm = FocusManager.getCurrentManager();
    Component c = fm.getFocusOwner();

    _currentMask ^= mask(keyCode);
    char keyChar = getKeyChar(keyCode);
    KeyEvent keyEvent = new KeyEvent(c, KeyEvent.KEY_RELEASED,
        System.currentTimeMillis(), _currentMask, keyCode, keyChar);

    Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(keyEvent);

    _pressed.remove(keyCode);
  }

  protected char getKeyChar(int keyCode)
  {
    if (keyCode >= KeyEvent.VK_0 && keyCode <= KeyEvent.VK_9
        || keyCode >= KeyEvent.VK_A && keyCode <= KeyEvent.VK_Z)
    {
      return (char) keyCode;
    }
    return KeyEvent.CHAR_UNDEFINED;
  }

  protected int mask(int keyCode)
  {
    int mask = 0;
    switch (keyCode)
    {
      case KeyEvent.VK_CONTROL:
        mask = InputEvent.CTRL_DOWN_MASK;
        break;
      case KeyEvent.VK_ALT:
        mask = InputEvent.ALT_DOWN_MASK;
        break;
      case KeyEvent.VK_SHIFT:
        mask = InputEvent.SHIFT_DOWN_MASK;
        break;
      case KeyEvent.VK_META:
        mask = InputEvent.META_DOWN_MASK;
        break;
    }
    return mask;
  }

  public void mouseMove(int x, int y)
  {

    _screenLocation.setLocation(x, y);

    // find the component it is over
//		System.err.println("move to " + _screenLocation);

    Component component = findComponent(_screenLocation);
    Window window = SwingUtilities.getWindowAncestor(component);
    _windowLocation.setLocation(x - window.getX(), y - window.getY());
    _componentLocation.setLocation(_windowLocation);
    _componentLocation = SwingUtilities.convertPoint(window, _componentLocation,
        component);

//		System.err.println("Currently over " + component);

    var eventQueue = Toolkit.getDefaultToolkit().getSystemEventQueue();

    if (_currentComponent != component)
    {
      // exit
      if (_currentComponent != null)
      {
        eventQueue.postEvent(new MouseEvent(_currentComponent,
            MouseEvent.MOUSE_EXITED, System.currentTimeMillis(), _currentMask,
            _componentLocation.x, _componentLocation.y, _screenLocation.x,
            _screenLocation.y, 0, false, MouseEvent.NOBUTTON));
        waitForIdle();
      }
      // enter
      if (component != null)
      {
        eventQueue.postEvent(new MouseEvent(component, MouseEvent.MOUSE_ENTERED,
            System.currentTimeMillis(), _currentMask, _componentLocation.x,
            _componentLocation.y, _screenLocation.x, _screenLocation.y, 0,
            false, _currentButtons));
        waitForIdle();
      }
    }

    _currentComponent = component;

    MouseEvent me = null;
    if (component != null)
    {
      if (_currentButtons == 0)
        me = new MouseEvent(component, MouseEvent.MOUSE_MOVED,
            System.currentTimeMillis(), _currentMask, _componentLocation.x,
            _componentLocation.y, _screenLocation.x, _screenLocation.y, 0,
            false, MouseEvent.NOBUTTON);
      else
      {
        me = new MouseEvent(component, MouseEvent.MOUSE_DRAGGED,
            System.currentTimeMillis(), _currentMask, _componentLocation.x,
            _componentLocation.y, _screenLocation.x, _screenLocation.y, 0,
            false, _currentButtons);
      }

      eventQueue.postEvent(me);
      waitForIdle();
    }
  }

  public void mousePress(int modifiers, int button)
  {

    if (_currentComponent == null) return;

    MouseEvent me = new MouseEvent(_currentComponent, MouseEvent.MOUSE_PRESSED,
        System.currentTimeMillis(), modifiers, _componentLocation.x,
        _componentLocation.y, _screenLocation.x, _screenLocation.y, 1, true,
        button);

//		System.err.println("press at " + _screenLocation + " over " + _currentComponent);

    _currentMask ^= modifiers;
    _currentButtons ^= button;

    Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(me);
    waitForIdle();
  }

  public void mouseRelease(int modifiers, int button)
  {

    if (_currentComponent == null) return;
    Component component = _currentComponent;

    MouseEvent me = new MouseEvent(component, MouseEvent.MOUSE_RELEASED,
        System.currentTimeMillis(), modifiers, _componentLocation.x,
        _componentLocation.y, _screenLocation.x, _screenLocation.y, 1, false,
        button);

    MouseEvent clicked = new MouseEvent(component,
        MouseEvent.MOUSE_CLICKED, System.currentTimeMillis(), modifiers,
        _componentLocation.x, _componentLocation.y, _screenLocation.x,
        _screenLocation.y, 1, false, button);

    _currentMask ^= modifiers;
    _currentButtons ^= button;

    var queue = Toolkit.getDefaultToolkit().getSystemEventQueue();
    queue.postEvent(me);
    waitForIdle();
    queue.postEvent(clicked);
    waitForIdle();
  }

  protected void waitForIdle()
  {
    try
    {
      SwingUtilities.invokeAndWait(() -> {
      });
    }
    catch (InvocationTargetException e)
    {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    catch (InterruptedException e)
    {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  protected Component findComponent(Point location)
  {
    Container rootWindow = findSmallestWindowAt(location.x, location.y);
    Container root = rootWindow;
    // root might be a rootpanecontainer
    if (rootWindow instanceof RootPaneContainer frame)
    {
//			System.err.println("rootPane");
      root = frame.getRootPane().getContentPane();
    }
    Component component = SwingUtilities.getDeepestComponentAt(root,
        location.x - rootWindow.getX(), location.y - rootWindow.getY());
    if (component == null)
    {
      // could be in a layered pane
      if (rootWindow instanceof RootPaneContainer frame)
      {
//				System.err.println("layeredPane");
        root = frame.getRootPane().getLayeredPane();
        Point offset = new Point(location.x, location.y);
        SwingUtilities.convertPointFromScreen(offset, root);
        component = SwingUtilities.getDeepestComponentAt(root, offset.x,
            offset.y);
//				/*
//				 * sometimes we get a stale popup window with an empty
//				 * jpanel in the layered pane. this window should not be visible, falling back
//				 */
        if (component instanceof JPanel jpanel)
        {
          if (jpanel.getComponentCount() == 0)
          {
            System.err.println("Oops");
            return null;
          }
        }

        if (component.getClass().getName()
            .equals("reschu.game.view.PanelMsgBoard"))
        {
          System.err.println("Oops");
        }

      }
    }
    return component;
  }

  protected Window findSmallestWindowAt(int x, int y)
  {
    Window smallest = null;
    int size = Integer.MAX_VALUE;

    for (Window w : Window.getWindows())
    {
      Rectangle dim = w.getBounds();
      if (!dim.contains(x, y) || !w.isVisible()) continue;
      int wSize = dim.height * dim.width;
      if (wSize < size)
      {
        wSize = size;
        smallest = w;
      }
    }

    return smallest;
  }
}
