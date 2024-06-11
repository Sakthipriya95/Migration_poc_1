/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.views.providers;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;

import com.bosch.caltool.icdm.model.rm.PidcRmDefinition;

/**
 * @author gge6cob
 */
public class RiskEvalTreeViewLabelProvider implements ILabelProvider {

  /**
   * {@inheritDoc}
   */
  @Override
  public void addListener(final ILabelProviderListener arg0) {
    // TODO Auto-generated method stub

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void dispose() {
    // TODO Auto-generated method stub

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isLabelProperty(final Object arg0, final String arg1) {
    // TODO Auto-generated method stub
    return false;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void removeListener(final ILabelProviderListener arg0) {
    // TODO Auto-generated method stub

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Image getImage(final Object arg0) {
    // TODO Auto-generated method stub
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getText(final Object element) {
    if (element instanceof PidcRmDefinition) { // Level nodes
      return ((PidcRmDefinition) element).getName();
    }
    return "";
  }

}
