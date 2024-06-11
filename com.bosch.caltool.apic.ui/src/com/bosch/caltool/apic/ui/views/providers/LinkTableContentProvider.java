/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.views.providers;

import java.util.SortedSet;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

import com.bosch.caltool.apic.ui.dialogs.ValueDialog;


/**
 * @author dja7cob
 */
public class LinkTableContentProvider implements IStructuredContentProvider {

  private final ValueDialog valueDialog;

  /**
   * @param valueDialog
   */
  public LinkTableContentProvider(final ValueDialog valueDialog) {
    this.valueDialog = valueDialog;
  }

  /**
   * Enable/disable save button when input changes.
   * <p>
   * {@inheritDoc}
   */
  @Override
  public void inputChanged(final Viewer viewer, final Object oldInput, final Object newInput) {
    // Selection listener for ok/Cancel
    if (!this.valueDialog.isOkCancelPress()) {
      this.valueDialog.setLinksChanged(true);
      this.valueDialog.checkSaveBtnEnable();
    }
  }

  /**
   * Sorted elements
   * <p>
   * {@inheritDoc}
   */
  @Override
  public Object[] getElements(final Object inputElement) {
    if (inputElement instanceof SortedSet<?>) {
      return ((SortedSet) inputElement).toArray();
    }
    return new Object[0];
  }

  @Override
  public void dispose() {
    // TODO Auto-generated method stub
  }

}
