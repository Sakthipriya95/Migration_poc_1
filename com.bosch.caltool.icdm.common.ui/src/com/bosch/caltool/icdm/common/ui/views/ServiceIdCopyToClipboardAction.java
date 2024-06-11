/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.common.ui.views;

import java.util.Set;

import org.eclipse.jface.action.Action;

import com.bosch.caltool.icdm.common.ui.utils.CommonUiUtils;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;

/**
 * @author dmr1cob
 */
public class ServiceIdCopyToClipboardAction extends Action {

  private final Set<String> sidList;

  /**
   * @param sidList change event list
   */
  public ServiceIdCopyToClipboardAction(final Set<String> sidList) {
    this.sidList = sidList;
    setProperties();
  }

  /**
  *
  */
  private void setProperties() {
    setText("Copy Service ID(s)");
    setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.COPY_EDIT_16X16));
    setEnabled(true);
  }

  @Override
  public void run() {
    // Multiple service id seperated by comma to copy it to log reader directly
    String serviceIds = String.join(", ", this.sidList);
    CommonUiUtils.setTextContentsToClipboard(serviceIds);
  }

}
