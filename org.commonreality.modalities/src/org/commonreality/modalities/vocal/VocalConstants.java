package org.commonreality.modalities.vocal;

import org.commonreality.object.IEfferentObject;
import org.commonreality.object.IRealObject;

public interface VocalConstants
{

  /**
   * property tag for {@link IEfferentObject} that contains a string rep
   * of the vocalization
   */
  static public final String VOCALIZATON = "vocal.vocalization";
  
  /**
   * marker property for any {@link IEfferentObject} that
   * can be used to vocalize. should be boolean
   */
  static public final String CAN_VOCALIZE = "vocal.canVocalize";
  
  
  /**
   * property for the {@link IRealObject} representing the aural event
   * that contains the speaker's identifer
   */
  static public final String SPEAKER = "vocal.speaker";

  public static final String IS_SUBVOCALIZATION = "vocal.isSubvocal";
}
