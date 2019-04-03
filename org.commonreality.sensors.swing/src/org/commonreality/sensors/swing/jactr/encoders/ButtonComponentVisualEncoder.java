package org.commonreality.sensors.swing.jactr.encoders;

/*
 * default logging
 */
 
import org.slf4j.LoggerFactory;
import org.commonreality.modalities.visual.IVisualPropertyHandler;
import org.commonreality.object.IAfferentObject;
import org.jactr.core.chunk.IChunk;
import org.jactr.core.slot.IMutableSlot;
import org.jactr.modules.pm.visual.memory.IVisualMemory;

public class ButtonComponentVisualEncoder extends
    AbstractComponentVisualEncoder
{

  /**
   * Logger definition
   */
  static private final transient org.slf4j.Logger LOGGER = LoggerFactory
                                                .getLogger(ButtonComponentVisualEncoder.class);

  public ButtonComponentVisualEncoder()
  {
    super("button");
  }

  @Override
  protected void updateComponentSlots(IAfferentObject guiPercept,
      IChunk perceptualEncoding, IVisualMemory visualMemory)
  {
    /*
     * we set the text slot..
     */
    try
    {
      ((IMutableSlot) perceptualEncoding.getSymbolicChunk().getSlot("text"))
          .setValue(getHandler().getText(guiPercept));
    }
    catch (Exception e)
    {
      // no text..
    }
  }

  @Override
  protected boolean canEncodeVisualObjectType(IAfferentObject afferentObject)
  {
    try
    {
      for (String type : getHandler().getTypes(afferentObject))
        if (type.equals("button")) return true;
      return false;
    }
    catch (Exception e)
    {
      return false;
    }
  }

}
