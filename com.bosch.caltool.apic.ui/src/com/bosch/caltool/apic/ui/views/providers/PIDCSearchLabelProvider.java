/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.views.providers;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.swt.graphics.Image;

import com.bosch.caltool.icdm.common.ui.utils.CommonUIConstants;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.model.apic.attr.Attribute;


/**
 * ICDM-1135 This class builds the attribute-vaues tree in PIDC scout
 *
 * @author bru2cob
 */
public class PIDCSearchLabelProvider extends StyledCellLabelProvider implements ITableLabelProvider {


  /**
   * checkbox image is used for USED-yes/no/??? columns {@inheritDoc}
   */
  @Override
  public Image getColumnImage(final Object arg0, final int arg1) {
    if (arg0 instanceof Attribute) {
      switch (arg1) {
        // used yes col
        case CommonUIConstants.COLUMN_INDEX_1:
          // used no col
        case CommonUIConstants.COLUMN_INDEX_2:
          // used not defined col
        case CommonUIConstants.COLUMN_INDEX_3:
          // set all three col images to checkbox image initiall
          return ImageManager.getInstance().getRegisteredImage(ImageKeys.CHECKBOX_NO_16X16);
        default:
          return null;
      }
    }
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getColumnText(final Object element, final int columnIndex) {
    return "";
  }

}
