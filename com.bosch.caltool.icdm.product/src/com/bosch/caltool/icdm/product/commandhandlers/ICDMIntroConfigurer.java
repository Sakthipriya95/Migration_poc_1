/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.product.commandhandlers;

import java.util.Map;

import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.ui.intro.IIntroSite;
import org.eclipse.ui.intro.config.IntroConfigurer;
import org.eclipse.ui.intro.config.IntroElement;


/**
 * @author jvi6cob
 */
public class ICDMIntroConfigurer extends IntroConfigurer {


  /**
   * {@inheritDoc}
   */
  @Override
  public void init(final IIntroSite site, final Map themeProperties) {
    // TODO Auto-generated method stub
    super.init(site, themeProperties);
    IToolBarManager toolBarManager = site.getActionBars().getToolBarManager();
    // Get the toolbar manager items
    IContributionItem[] items = toolBarManager.getItems();
    for (IContributionItem iContributionItem : items) {
      if (iContributionItem instanceof ActionContributionItem) {
        ActionContributionItem actionContributionItem = (ActionContributionItem) iContributionItem;
        IAction action = actionContributionItem.getAction();
        action.setEnabled(false);
        iContributionItem.setVisible(false);
      }
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getVariable(final String variableName) {
    // TODO Auto-generated method stub
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public IntroElement[] getGroupChildren(final String pageId, final String groupId) {
    // TODO Auto-generated method stub
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String resolvePath(final String extensionId, final String path) {
    // TODO Auto-generated method stub
    return null;
  }

}
