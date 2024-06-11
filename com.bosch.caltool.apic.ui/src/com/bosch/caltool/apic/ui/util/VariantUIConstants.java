/**
 *
 */
package com.bosch.caltool.apic.ui.util;

/**
 * @author ukt1cob
 */
public final class VariantUIConstants {

  /**
   * selection to copy variant along with sub-variants
   */
  public static final int COPY_SEL_YES = 0;
  /**
   * selection to copy variant without sub-variants
   */
  public static final int COPY_SEL_NO = 1;
  /**
   * selection to cancel the copy process
   */
  public static final int COPY_SEL_CANCEL = 2;
  /**
   * selection to override the attribute values of variant while copying variant
   */
  public static final int COPY_SEL_OVERRIDE = 0;
  /**
   * selection to copy variant without overriding the attribute values of variant
   */
  public static final int COPY_SEL_NO_OVERRIDE = 1;
  /**
   * selection to undo the deleted sub-variant in destination variant
   */
  public static final int UNDO_DELETED_SUB_VAR_YES = 0;
  /**
   * selection to not undo the deleted sub-variant in destination variant
   */
  public static final int UNDO_DELETED_SUB_VAR_NO = 1;
  /**
   * message dialog title
   */
  public static final String COPY_SUB_VARIANT = "Copy Sub-Variant(s)";
  /**
   * message dialog option cancel
   */
  public static final String MSG_DIALOG_OPTION_CANCEL = "Cancel";
  /**
   * message dialog option cancel
   */
  public static final int MSG_DIALOG_CLOSE_BUTTON = -1;

  /**
   * Private constructor
   */
  private VariantUIConstants() {
    // private constructor
  }

}
