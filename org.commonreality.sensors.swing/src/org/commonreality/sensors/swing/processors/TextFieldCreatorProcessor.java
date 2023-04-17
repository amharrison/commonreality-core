package org.commonreality.sensors.swing.processors;

import javax.swing.JComponent;
import javax.swing.text.JTextComponent;

import org.commonreality.sensors.swing.internal.Coordinates;

public class TextFieldCreatorProcessor extends AbstractCreatorProcessor
{

  public TextFieldCreatorProcessor(Coordinates coordinates)
  {
    super(coordinates, JTextComponent.class);
  }

  @Override
  protected String[] calculateTypes(JComponent component)
  {
    String[] types = { "text-area", "gui" };

    return types;
  }

  @Override
  protected String getText(JComponent component)
  {
    return ((JTextComponent) component).getText();
  }
}
