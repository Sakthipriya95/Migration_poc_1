/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.common.ui.listeners;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.nebula.jface.gridviewer.GridTableViewer;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

import com.bosch.caltool.icdm.common.ui.utils.CommonUIConstants;
import com.bosch.caltool.icdm.common.ui.views.data.LinkData;


/**
 * ICDM-1502 common selection listener for links table viewer
 * 
 * @author mkl2cob
 */
public final class LinkTableSelectionListener extends SelectionAdapter {


  /**
   * Links table viewer
   */
  private final GridTableViewer linksTabViewer;

  /**
   * Edit link action
   */
  private final Action editLinkAction;
  /**
   * delete link action
   */
  private final Action deleteLinkAction;

  /**
   * @param linksTabViewer GridTableViewer
   * @param editLinkAction Action
   * @param deleteLinkAction Action
   */
  public LinkTableSelectionListener(final GridTableViewer linksTabViewer, final Action editLinkAction,
      final Action deleteLinkAction) {
    this.linksTabViewer = linksTabViewer;
    this.editLinkAction = editLinkAction;
    this.deleteLinkAction = deleteLinkAction;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void widgetSelected(final SelectionEvent event) {
    final IStructuredSelection selection = (IStructuredSelection) this.linksTabViewer.getSelection();
    if (selection != null) {
      LinkData linkData = (LinkData) selection.getFirstElement();
      if (linkData.getOprType() == CommonUIConstants.CHAR_CONSTANT_FOR_DELETE) {
        // disable delete and edit link actions for deleted links
        this.deleteLinkAction.setEnabled(false);
        this.editLinkAction.setEnabled(false);
      }
      else {
        this.deleteLinkAction.setEnabled(true);
        this.editLinkAction.setEnabled(true);
      }
    }
  }
}
