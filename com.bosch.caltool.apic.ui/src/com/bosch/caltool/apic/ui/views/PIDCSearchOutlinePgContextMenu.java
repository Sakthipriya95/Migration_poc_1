/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.views;

import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Menu;

import com.bosch.caltool.icdm.client.bo.apic.AttrGroupClientBO;
import com.bosch.caltool.icdm.client.bo.apic.AttrSuperGroupClientBO;
import com.bosch.caltool.icdm.client.bo.uc.FavUseCaseItemNode;
import com.bosch.caltool.icdm.client.bo.uc.IUseCaseItemClientBO;
import com.bosch.caltool.icdm.client.bo.uc.OutLineViewDataHandler;
import com.bosch.caltool.icdm.client.bo.uc.UseCaseSectionClientBO;
import com.bosch.caltool.icdm.client.bo.uc.UsecaseClientBO;
import com.bosch.caltool.icdm.common.ui.actions.CommonActionSet;
import com.bosch.caltool.icdm.model.apic.attr.AttrGroup;
import com.bosch.caltool.icdm.model.apic.attr.AttrSuperGroup;


/**
 * This class adds context menu to PIDC search outline page
 *
 * @author bru2cob
 */
public class PIDCSearchOutlinePgContextMenu {

  private final TreeViewer viewer;
  private final OutLineViewDataHandler dataHandler;

  /**
   * @param viewer outline treeviewer
   * @param dataHandler OutLineViewDataHandler
   */
  public PIDCSearchOutlinePgContextMenu(final TreeViewer viewer, final OutLineViewDataHandler dataHandler) {
    this.viewer = viewer;
    this.dataHandler = dataHandler;
  }

  /**
   * Build the context menu for tree
   */
  public void hookContextMenu() {
    // Create the menu manager and fill context menu
    final MenuManager menuMgr = new MenuManager("popupmenu");
    menuMgr.setRemoveAllWhenShown(true);
    menuMgr.addMenuListener(new IMenuListener() {

      @Override
      public void menuAboutToShow(final IMenuManager manager) {
        fillContextMenu(manager);

      }
    });
    final Menu menu = menuMgr.createContextMenu(this.viewer.getControl());
    this.viewer.getControl().setMenu(menu);
  }

  /**
   * Fills the context menu
   *
   * @param manager
   */
  private void fillContextMenu(final IMenuManager manager) {
    final CommonActionSet actionSet = new CommonActionSet();

    // Get the current selection and add actions to it
    final IStructuredSelection selection = (IStructuredSelection) this.viewer.getSelection();
    if ((selection != null) && (selection.getFirstElement() != null)) {
      // displaying the additional links in context menu
      if (selection.getFirstElement() instanceof IUseCaseItemClientBO) {
        useCaseMenu(manager, actionSet, selection);
      }
      else if (selection.getFirstElement() instanceof AttrGroup) {
        actionSet.addLinkAction(manager, (new AttrGroupClientBO((AttrGroup) selection.getFirstElement())).getLinks());
      }
      else if (selection.getFirstElement() instanceof AttrSuperGroup) {
        actionSet.addLinkAction(manager,
            (new AttrSuperGroupClientBO((AttrSuperGroup) selection.getFirstElement())).getLinks());
      }
      else if (selection.getFirstElement() instanceof FavUseCaseItemNode) {
        favUCMenu(manager, actionSet, selection);
      }
    }
  }


  /**
   * Right click menu for fav use case items
   *
   * @param manager menu manager
   * @param actionSet action set
   * @param selection selected element
   */
  private void favUCMenu(final IMenuManager manager, final CommonActionSet actionSet,
      final IStructuredSelection selection) {
    FavUseCaseItemNode favUcNode = (FavUseCaseItemNode) selection.getFirstElement();
    // add link context menu for usecase, usecase section
    if (favUcNode.getUseCaseItem() instanceof UsecaseClientBO) {
      // add 'Open' use case action for private use cases
      actionSet.openUseCaseAction(manager, (UsecaseClientBO) favUcNode.getUseCaseItem());
      manager.add(new Separator());
      actionSet.addLinkAction(manager, ((UsecaseClientBO) (favUcNode.getUseCaseItem())).getLinks());
    }
    else if (favUcNode.getUseCaseItem() instanceof UseCaseSectionClientBO) {
      actionSet.addLinkAction(manager, ((UseCaseSectionClientBO) (favUcNode.getUseCaseItem())).getLinks());
    }
    // add delete context menu if this is a fav node
    if (favUcNode.getFavUcItem() != null) {
      // context menu for private usecases
      actionSet.addDelFavUcAction(manager, favUcNode, this.viewer, null, null);

    }
  }


  /**
   * Right click menu for use case items
   *
   * @param manager menu manager
   * @param actionSet action set
   * @param selection selected element
   */
  private void useCaseMenu(final IMenuManager manager, final CommonActionSet actionSet,
      final IStructuredSelection selection) {

    // add link context menu for usecase ,usecase section
    if (selection.getFirstElement() instanceof UseCaseSectionClientBO) {
      actionSet.addLinkAction(manager, ((UseCaseSectionClientBO) selection.getFirstElement()).getLinks());
    }
    else if (selection.getFirstElement() instanceof UsecaseClientBO) {
      // add 'Open' use case action for common use cases
      actionSet.openUseCaseAction(manager, (UsecaseClientBO) selection.getFirstElement());
      manager.add(new Separator());
      actionSet.addLinkAction(manager, ((UsecaseClientBO) selection.getFirstElement()).getLinks());
    }

    actionSet.addMovToUserFav(manager, (IUseCaseItemClientBO) selection.getFirstElement(), this.viewer,
        this.dataHandler.getUcDataHandler(), true);

  }

}
