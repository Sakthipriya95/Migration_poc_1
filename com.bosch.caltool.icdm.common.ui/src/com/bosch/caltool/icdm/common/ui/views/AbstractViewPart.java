/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.common.ui.views;

import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.views.properties.IPropertySheetPage;

import com.bosch.caltool.icdm.client.bo.framework.IClientDataHandler;
import com.bosch.caltool.icdm.common.ui.actions.CommonActionSet;
import com.bosch.caltool.icdm.common.ui.listeners.IDceRefresher;
import com.bosch.caltool.icdm.common.ui.sorters.CdmPropertySheetSorter;
import com.bosch.caltool.icdm.ws.rest.client.cns.DisplayChangeEvent;

/**
 * Abstract class for all View parts
 *
 * @author bne4cob
 */
public abstract class AbstractViewPart extends ViewPart implements IDceRefresher {


  /**
   * help link key prefix for views
   */
  private static final String HELP_LINK_PREFIX = "VIEW_";


  /**
   * adds help action to the toolbar
   */
  protected void addHelpAction() {
    CommonActionSet commonActionSet = new CommonActionSet();
    String suffixForHelpKey = HELP_LINK_PREFIX + getClass().getSimpleName();
    commonActionSet.addHelpAction(getViewSite().getActionBars().getToolBarManager(), suffixForHelpKey);
  }


  /**
   * Uses a custom property sheet page for displaying properties when key is <code>IPropertySheetPage</code>, to disable
   * sorting of properties
   * <p>
   * {@inheritDoc}
   */
  @Override
  public Object getAdapter(@SuppressWarnings("rawtypes") final Class key) {
    if (key == IPropertySheetPage.class) {
      CdmPropertySheetPage page = new CdmPropertySheetPage();
      page.setSheetSorter(new CdmPropertySheetSorter());
      return page;
    }
    return super.getAdapter(key);
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public void refreshUI(final DisplayChangeEvent dce) {
    // Default implementation, no actions
  }


  @Override
  public IClientDataHandler getDataHandler() {
    return null;
  }

}
