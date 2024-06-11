/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ruleseditor.listeners;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.IStructuredSelection;

import com.bosch.caltool.icdm.ruleseditor.actions.RuleParamContextMenuActions;
import com.bosch.caltool.icdm.ruleseditor.pages.DetailsPage;


/**
 * @author rgo7cob
 */
public class DetailsPageMenuListener implements IMenuListener {

  private final DetailsPage detailsPage;
  private final MenuManager menuMgr;

  /**
   * @param detailsPage detailsPage
   * @param menuMgr menuMgr
   */
  public DetailsPageMenuListener(final DetailsPage detailsPage, final MenuManager menuMgr) {
    this.detailsPage = detailsPage;
    this.menuMgr = menuMgr;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void menuAboutToShow(final IMenuManager mgr) {

    final IStructuredSelection selection = (IStructuredSelection) this.detailsPage.getFcTableViewer().getSelection();
    final Object firstElement = selection.getFirstElement();
    if ((firstElement != null) && (!selection.isEmpty())) {

      List<Object> selectedObj = new ArrayList<>();
      selectedObj.add(firstElement);
      RuleParamContextMenuActions commonContextMenuAction = new RuleParamContextMenuActions(this.menuMgr);
      commonContextMenuAction.paramRightClick(this.detailsPage, mgr, firstElement, selectedObj);
    }

  }


}
