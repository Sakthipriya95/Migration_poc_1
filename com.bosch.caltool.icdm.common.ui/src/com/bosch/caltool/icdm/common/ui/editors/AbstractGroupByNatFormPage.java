/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.common.ui.editors;

import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.nebula.widgets.nattable.extension.glazedlists.groupBy.GroupByHeaderLayer;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.editor.FormEditor;


/**
 * Abstraction for FormPage using nat table with Group By feature. This abstraction eliminates the need to write new
 * class CustomGroupByHeaderLayer for each Nat table with Group by feature
 *
 * @author dmo5cob
 */

public abstract class AbstractGroupByNatFormPage extends AbstractNatFormPage {


  /**
   *
   */
  private static final String OUTLINE_VIEW_PART = "com.bosch.caltool.icdm.common.ui.views.OutlineViewPart";

  /**
   * @param editor FormEditor instance
   * @param pageId String
   * @param title title of the page
   */
  public AbstractGroupByNatFormPage(final FormEditor editor, final String pageId, final String title) {
    super(editor, pageId, title);
  }


  /**
   * @param groupByHeaderLayer CustomGroupByHeaderLayer
   * @param outlineSelction boolean
   */
  public void setStatusBarMessage(final GroupByHeaderLayer groupByHeaderLayer, final boolean outlineSelction) {
    if ((groupByHeaderLayer != null) && (!groupByHeaderLayer.getGroupByModel().getGroupByColumnIndexes().isEmpty())) {
      // if there is grouping
      IStatusLineManager statusLineManager;
      if (outlineSelction) {
        final IViewSite viewPartSite = (IViewSite) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
            .findView(OUTLINE_VIEW_PART).getSite();
        statusLineManager = viewPartSite.getActionBars().getStatusLineManager();
      }
      else {
        statusLineManager = getEditorSite().getActionBars().getStatusLineManager();
      }
      statusLineManager.setErrorMessage("No statistics available due to grouping");
      statusLineManager.update(true);
    }
    else {
      // otherwise
      setStatusBarMessage(outlineSelction);
    }
  }

  /**
   * abstract method to have different implementation
   *
   * @param outlineSelction boolean
   */
  public abstract void setStatusBarMessage(boolean outlineSelction);

}
