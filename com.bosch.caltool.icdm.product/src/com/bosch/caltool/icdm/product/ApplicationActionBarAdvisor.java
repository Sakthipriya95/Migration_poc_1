/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.product;

import org.eclipse.core.runtime.IExtension;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.eclipse.ui.internal.WorkbenchPlugin;
import org.eclipse.ui.internal.registry.ActionSetRegistry;
import org.eclipse.ui.internal.registry.IActionSetDescriptor;

import com.bosch.caltool.icdm.product.actions.ConnectionStateStatusLineContribution;
import com.bosch.caltool.icdm.product.actions.CurrentUserStatusLineContribution;
import com.bosch.caltool.icdm.product.menuactions.MenuBarActions;
import com.bosch.caltool.icdm.product.util.ICDMConstants;
import com.bosch.rcputils.action.TableInfoStatusLineContribution;

/**
 * This class controls application action bar
 */
@SuppressWarnings("restriction")
public class ApplicationActionBarAdvisor extends ActionBarAdvisor {

  /**
   * Member to handle all menu bar actions
   */
  private final MenuBarActions menuBarAction;


  /**
   * Constructor
   * 
   * @param configurer IActionBarConfigurer
   */
  public ApplicationActionBarAdvisor(final IActionBarConfigurer configurer) {
    super(configurer);
    // initialize the MenuBar
    this.menuBarAction = new MenuBarActions(configurer);

    /* --- Removing unwanted activities-- */

    ActionSetRegistry reg = WorkbenchPlugin.getDefault().getActionSetRegistry();
    IActionSetDescriptor[] actionSets = reg.getActionSets();

    // Removing actionsSet navigations
    String actionSetId = ICDMConstants.ACTIV_ID_NAVIGATION;
    removeActivity(reg, actionSets, actionSetId);
    // Removing annotation navigations
    actionSetId = ICDMConstants.ACTIV_ID_ANNOT_NAVIGATION;
    removeActivity(reg, actionSets, actionSetId);
    // Removing convert line delimiters menu
    actionSetId = ICDMConstants.ACTIV_ID_CONVERT_LINE_DELIM;
    removeActivity(reg, actionSets, actionSetId);
  }

  /**
   * Method to remove unwanted activity which comes with some plug-ins
   * 
   * @param reg actionReg
   * @param actionSets actionDescps
   * @param actionSetId actionids
   */
  private void removeActivity(final ActionSetRegistry reg, final IActionSetDescriptor[] actionSets,
      final String actionSetId) {
    for (int i = 0; i < actionSets.length; i++) {
      if (!actionSets[i].getId().equals(actionSetId)) {
        continue;
      }
      IExtension ext = actionSets[i].getConfigurationElement().getDeclaringExtension();
      reg.removeExtension(ext, new Object[] { actionSets[i] });
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void makeActions(final IWorkbenchWindow window) {
    // set all action
    register(ActionFactory.INTRO.create(window));
    register(ActionFactory.HELP_SEARCH.create(window));
    register(ActionFactory.DYNAMIC_HELP.create(window));
    this.menuBarAction.setActions(window);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void fillMenuBar(final IMenuManager menuBar) {
    // set menu bar
    this.menuBarAction.setMenuBar(menuBar);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void fillStatusLine(final IStatusLineManager statusLine) {
    statusLine.add(new TableInfoStatusLineContribution());
    statusLine.add(new CurrentUserStatusLineContribution());
    statusLine.add(new ConnectionStateStatusLineContribution());
  }
}