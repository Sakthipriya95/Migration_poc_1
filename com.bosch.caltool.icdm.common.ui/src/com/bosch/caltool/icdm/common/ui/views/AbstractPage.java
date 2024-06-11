/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.common.ui.views;

import org.eclipse.ui.part.Page;

import com.bosch.caltool.icdm.client.bo.framework.IClientDataHandler;
import com.bosch.caltool.icdm.common.ui.actions.CommonActionSet;
import com.bosch.caltool.icdm.common.ui.listeners.IDceRefresher;
import com.bosch.caltool.icdm.ws.rest.client.cns.DisplayChangeEvent;


/**
 * @author adn1cob
 */
public abstract class AbstractPage extends Page implements IDceRefresher {

  /**
   * help link prefix for view pages
   */
  private static final String HELP_LINK_PREFIX = "VIEWPAGE_";

  // ICDM-931
  /**
   * Refreshes the tree viewers in the outline page
   *
   * @param deselectAll - true/false : whether or not to clear the selection on the tree while refreshing
   */
  public abstract void refreshTreeViewer(boolean deselectAll);

  /**
   * adds help action to the toolbar
   */
  public void addHelpAction() {
    CommonActionSet commonActionSet = new CommonActionSet();
    String suffixForHelpKey = HELP_LINK_PREFIX + getClass().getSimpleName();
    commonActionSet.addHelpAction(getSite().getActionBars().getToolBarManager(), suffixForHelpKey);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void refreshUI(final DisplayChangeEvent dce) {
    // No implementation
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public IClientDataHandler getDataHandler() {
    return null;
  }

}
