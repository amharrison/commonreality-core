package org.commonreality.sensors.swing.processors;

import javax.swing.JComponent;
import javax.swing.JLabel;

import org.commonreality.sensors.swing.internal.Coordinates;

public class LabelCreatorProcessor extends AbstractCreatorProcessor
{

  public LabelCreatorProcessor(Coordinates coordinates)
  {
    super(coordinates, JLabel.class);
  }

  @Override
  protected String[] calculateTypes(JComponent component)
  {
    return new String[] { "label", "text" };
  }

  @Override
  protected String getText(JComponent component)
  {
    return ((JLabel) component).getText();
  }
}
