/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.views.providers;

import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import com.bosch.caltool.icdm.model.rm.PidcRmDefinition;

/**
 * The Class RiskEvalTreeViewContentProvider.
 *
 * @author gge6cob
 */
public class RiskEvalTreeViewContentProvider implements ITreeContentProvider {

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
  public void inputChanged(final Viewer arg0, final Object arg1, final Object arg2) {
    // TODO Auto-generated method stub

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Object[] getChildren(final Object arg0) {
    // TODO Auto-generated method stub
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Object[] getElements(final Object inputElement) {

    if ((inputElement instanceof List) && !((List<?>) inputElement).isEmpty() &&
        (((List<?>) inputElement).get(0) instanceof PidcRmDefinition)) {
      return ((List<PidcRmDefinition>) inputElement).toArray();
    }

    // Return an empty array if no nodes are available
    return new Object[] {};
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Object getParent(final Object arg0) {
    // TODO Auto-generated method stub
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean hasChildren(final Object arg0) {
    // TODO Auto-generated method stub
    return false;
  }

}
