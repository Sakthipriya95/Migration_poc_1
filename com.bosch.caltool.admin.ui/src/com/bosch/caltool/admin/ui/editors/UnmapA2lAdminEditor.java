/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.admin.ui.editors;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.PartInitException;

import com.bosch.caltool.admin.ui.Activator;
import com.bosch.caltool.admin.ui.editors.pages.UnmapA2lAdminPage;
import com.bosch.caltool.icdm.common.ui.editors.AbstractFormEditor;
import com.bosch.caltool.icdm.logger.CDMLogger;


/**
 * @author hnu1cob
 */
public class UnmapA2lAdminEditor extends AbstractFormEditor {

  /**
   * Defines Admin Editor id
   */
  public static final String EDITOR_ID = "com.bosch.caltool.admin.ui.editors.UnmapA2lAdminEditor";

  /**
   * {@inheritDoc}
   */
  @Override
  protected void addPages() {
    try {
      // Add page to the editor form
      UnmapA2lAdminPage unmapA2lAdminPage = new UnmapA2lAdminPage(this, "Unmap A2L");
      addPage(unmapA2lAdminPage);
    }
    catch (PartInitException exep) {
      CDMLogger.getInstance().warn(exep.getMessage(), exep, Activator.PLUGIN_ID);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void doSave(final IProgressMonitor arg0) {
    // TODO Auto-generated method stub

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void doSaveAs() {
    // TODO Auto-generated method stub

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isSaveAsAllowed() {
    // TODO Auto-generated method stub
    return false;
  }

}
