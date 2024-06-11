/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;

import com.bosch.caltool.icdm.common.ui.utils.CommonUiUtils;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;

/**
 * Action class to copy name of PIDC and variant to clipboard
 *
 * @author pdh2cob
 */
public class PIDCNameCopyAction extends Action {


  private final String actionName;

  private final String name;

  /**
   * @param name - name of pidc version/variant
   * @param actionName - Action name
   */
  public PIDCNameCopyAction(final String name, final String actionName) {
    this.name = name;
    this.actionName = actionName;
    // Properties of the context menu will be set here
    setProperties();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void run() {
    // Copy to clipboard action
    CommonUiUtils.setTextContentsToClipboard(this.name);
  }


  /**
   * Method to set the context menu properties
   */
  private void setProperties() {
    setText(this.actionName);
    final ImageDescriptor imageDesc = ImageManager.getImageDescriptor(ImageKeys.COPY_EDIT_16X16);
    setImageDescriptor(imageDesc);
    setEnabled(true);

  }


}
