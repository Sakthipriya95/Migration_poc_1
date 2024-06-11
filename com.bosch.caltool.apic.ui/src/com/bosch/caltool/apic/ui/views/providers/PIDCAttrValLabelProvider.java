/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.views.providers;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

import com.bosch.caltool.apic.ui.util.ApicUiConstants;
import com.bosch.caltool.icdm.client.bo.apic.AttributeValueClientBO.CLEARING_STATUS;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValue;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValueDummy;


/**
 * @author mga1cob pidc attr value label provider.
 */
public class PIDCAttrValLabelProvider implements ITableLabelProvider, IColorProvider {

  private Map<Long, Boolean> validValMap = new HashMap<>();

  /**
   * @param validValMap
   */
  public PIDCAttrValLabelProvider(final Map<Long, Boolean> validValMap) {
    this.validValMap = validValMap;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void addListener(final ILabelProviderListener listener) {
    // No Implementation
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void dispose() {
    // No Implementation
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isLabelProperty(final Object element, final String property) {
    return false;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void removeListener(final ILabelProviderListener listener) {
    // No Implementation
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Image getColumnImage(final Object element, final int columnIndex) {
    return null;
  }

  /**
   * get the column text for different columns. {@inheritDoc}
   */
  @Override
  public String getColumnText(final Object element, final int columnIndex) {
    String value = "";
    if (element instanceof AttributeValue) {
      final AttributeValue attributeValue = (AttributeValue) element;
      switch (columnIndex) {
        // Attr Value Column.
        case 0:
          value = caseAttrVal(element, attributeValue);
          break;
        // Attr Value desc Column.
        case 1:
          value = attributeValue.getDescription();
          break;
        // Attr val Clearing status
        case 2:
          value = attributeValue.getClearingStatus();
          break;
        // Value Class Column.
        case ApicUiConstants.COLUMN_INDEX_3:
          value = attributeValue.getCharStr();
          break;
        default:
          value = "";
          break;
      }
    }
    return value;
  }

  /**
   * @param element
   * @param attributeValue
   * @return
   */
  private String caseAttrVal(final Object element, final AttributeValue attributeValue) {
    String value;
    // check for attr value Dummy
    if (element instanceof com.bosch.caltool.icdm.model.apic.attr.AttributeValueDummy) {
      if (attributeValue.getName().isEmpty()) {
        value = ((com.bosch.caltool.icdm.model.apic.attr.AttributeValueDummy) element).getValue();
      }
      else {
        value = attributeValue.getName();
      }
    }
    else {
      value = attributeValue.getName();
    }
    return value;
  }

  /**
   * {@inheritDoc} return the Foreground color.
   */
  @Override
  public Color getForeground(final Object element) {
    final Display display = Display.getCurrent();
    // Constant for red Color.
    final Color red = display.getSystemColor(SWT.COLOR_RED);


    if (element instanceof AttributeValue) {
      final AttributeValue attrValue = (AttributeValue) element;
      // if Value is deleted return red color.
      if (attrValue.isDeleted()) {
        return red;
      }
      // Invalid Value. Color = Grey
      else if (((this.validValMap != null) && !(attrValue instanceof AttributeValueDummy)) &&
          (this.validValMap.get(attrValue.getId()) == null)) {
        return Display.getCurrent().getSystemColor(SWT.COLOR_DARK_GRAY);
      }
      // if Value is not cleared or in clearing return Orange color.
      else if ((null != attrValue.getClearingStatus()) &&
          !attrValue.getClearingStatus().equals(CLEARING_STATUS.CLEARED.getDBText())) {
        return ApicUiConstants.NOT_CLEARED_VAL_COLOR;
      }
    }
    // Constant for black Color.
    return display.getSystemColor(SWT.COLOR_BLACK);
  }

  /**
   * return the Background color.
   */
  @Override
  public Color getBackground(final Object element) {
    return null;
  }

}
