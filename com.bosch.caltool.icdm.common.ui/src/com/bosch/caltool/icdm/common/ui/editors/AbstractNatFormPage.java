/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.common.ui.editors;

import org.eclipse.ui.forms.editor.FormEditor;

import com.bosch.caltool.nattable.INatColumnFilterObserver;


/**
 * Abstraction for FormPage using nat table
 * 
 * @author dmo5cob
 */

public abstract class AbstractNatFormPage extends AbstractFormPage implements INatColumnFilterObserver {

  /**
   * @param editor FormEditor instance
   * @param pageId String
   * @param title title of the page
   */
  public AbstractNatFormPage(final FormEditor editor, final String pageId, final String title) {
    super(editor, pageId, title);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void updateStatusBar(final boolean outlineSelection) {
    // This is to enable or disable the reset filter button when any filter is applied
    // enable only if the filter action is applicable
    if (getResetFiltersAction() != null) {
      getResetFiltersAction().enableResetFilterAction();
    }
  }


}
