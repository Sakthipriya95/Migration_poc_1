/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.views.providers;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.swt.graphics.Image;

import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;


/**
 * ICDM-2593 Label provider for value edit column in predefined attribute value table viewer
 * 
 * @author dja7cob
 */
public class PredefinedValEditColLblProvider extends ColumnLabelProvider {


  /**
   * Defines attribute value edit image
   */
  private Image editImage;

  /**
   * Get the value edit image {@inheritDoc}
   */
  @Override
  public Image getImage(final Object element) {
    if (this.editImage == null) {
      this.editImage = ImageManager.getInstance().getRegisteredImage(ImageKeys.VALUE_EDIT_28X30);
      return this.editImage;
    }
    return this.editImage;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getText(final Object element) {
    return "";
  }

}
