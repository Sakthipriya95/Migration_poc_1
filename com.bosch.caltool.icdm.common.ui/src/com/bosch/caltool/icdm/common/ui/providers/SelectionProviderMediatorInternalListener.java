/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.common.ui.providers;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;

/**
 * class for selection provider mediator listener
 */
class SelectionProviderMediatorInternalListener implements ISelectionChangedListener, FocusListener {

  /**
   * SelectionProviderMediator intance
   */
  private final SelectionProviderMediator selectionProviderMediator;

  /**
   * @param selectionProviderMediator
   */
  SelectionProviderMediatorInternalListener(final SelectionProviderMediator selectionProviderMediator) {
    this.selectionProviderMediator = selectionProviderMediator;
  }

  /*
   * @see ISelectionChangedListener#selectionChanged
   */
  @Override
  public void selectionChanged(final SelectionChangedEvent event) {
    this.selectionProviderMediator.doOnSelectionChange(event);
  }

  /*
   * @see FocusListener#focusGained
   */
  @Override
  public void focusGained(final FocusEvent event) {
    this.selectionProviderMediator.doFocusChanged(event.widget);
  }

  /*
   * @see FocusListener#focusLost
   */
  @Override
  public void focusLost(final FocusEvent event) {
    // TO-DO
  }
}