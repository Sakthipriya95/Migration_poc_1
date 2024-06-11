/*
 * \ * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.common.ui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;


/** This class can be used to create filter action instances
 * @author dmo5cob
 */
public class FilterAction extends Action {


  /**
   * This flag indicates whether the filter action is checked by default or not
   */
  private final boolean enabledByDefault;

  /**
   * @param text text to be set to the action
   * @param image image to be set to the action
   */
  public FilterAction(final String text, final ImageDescriptor image) {
    super(text, SWT.TOGGLE);
    setImageDescriptor(image);
    this.enabledByDefault = true;
    setChecked(this.enabledByDefault);

  }

  /**
   * @param text text to be set to the action
   * @param image image to be set to the action
   * @param enabledByDefault - true : filter action checked by default / false : filter action unchecked by default
   */
  public FilterAction(final String text, final ImageDescriptor image, final boolean enabledByDefault) {
    super(text, SWT.TOGGLE);
    setImageDescriptor(image);
    setChecked(enabledByDefault);
    this.enabledByDefault = enabledByDefault;
  }

  /**
   * @return the enabledByDefault
   */
  public boolean isEnabledByDefault() {
    return this.enabledByDefault;
  }
}
