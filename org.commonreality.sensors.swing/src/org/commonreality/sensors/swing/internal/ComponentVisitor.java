package org.commonreality.sensors.swing.internal;

import java.awt.Component;
import java.awt.Container;
import java.util.function.Consumer;
import java.util.function.Predicate;

import javax.swing.RootPaneContainer;

public class ComponentVisitor
{

  private final Predicate<Container> _shouldDescend;

  private final Predicate<Component> _shouldAccept;

  private final Consumer<Component>  _acceptor;

  public ComponentVisitor(Predicate<Container> shouldDescend,
      Predicate<Component> shouldAccept, Consumer<Component> acceptor)
  {

    _shouldDescend = shouldDescend;
    _shouldAccept = shouldAccept;
    _acceptor = acceptor;
  }

  public void visit(Component component)
  {
    if (component == null) return;

    if (_shouldAccept.test(component)) _acceptor.accept(component);
    if (component instanceof RootPaneContainer)
    {
      try
      {
        visit(((RootPaneContainer) component).getContentPane());
      }
      catch (Exception e)
      {

      }
      try
      {
        visit(((RootPaneContainer) component).getLayeredPane());
      }
      catch (Exception e)
      {

      }

    }
    if (component instanceof Container)
      if (_shouldDescend.test((Container) component))
        for (Component child : ((Container) component).getComponents())
        visit(child);
  }
}
