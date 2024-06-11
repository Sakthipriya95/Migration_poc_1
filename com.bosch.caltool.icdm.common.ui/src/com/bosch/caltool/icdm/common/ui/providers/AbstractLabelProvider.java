/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.common.ui.providers;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.swt.graphics.Image;

import com.bosch.caltool.datamodel.core.IBasicObject;


/**
 * Abstraction for Label providers. This class provides some implementations for if the element is IBasicObject type
 *
 * @author bne4cob
 */
public abstract class AbstractLabelProvider extends StyledCellLabelProvider implements ILabelProvider {

  /**
   * {@inheritDoc}
   */
  @Override
  public Image getImage(final Object element) {
    // No default action
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getText(final Object element) {
    String name = null;
    // Use getter for name if element is basic object type
    if (element instanceof IBasicObject) {
      name = ((IBasicObject) element).getName();
    }
    return name;
  }

  /**
   * {@inheritDoc}
   */
  // iCDM-350
  @Override
  public String getToolTipText(final Object element) {
    String tooltip = null;
    // Use getter for tooltip if element is basic object type
    if (element instanceof IBasicObject) {
      tooltip = ((IBasicObject) element).getName();
    }
    return tooltip;
  }
}
