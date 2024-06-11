/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.admin.ui.editors;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.PartInitException;

import com.bosch.caltool.admin.ui.Activator;
import com.bosch.caltool.admin.ui.editors.pages.MultiNodeAccessPage;
import com.bosch.caltool.icdm.common.ui.editors.AbstractFormEditor;
import com.bosch.caltool.icdm.logger.CDMLogger;


/**
 * @author say8cob
 */
public class NodeAccessMgmtEditor extends AbstractFormEditor {


  /**
   * Defines Admin Editor id
   */
  public static final String EDITOR_ID = "com.bosch.caltool.admin.ui.editors.NodeAccessMgmtEditor";


  /**
   * {@inheritDoc}
   */
  @Override
  protected void addPages() {
    try {
      // Add pages to the editor form
      MultiNodeAccessPage multiNodeAccessPage = new MultiNodeAccessPage(this, "Multiple Nodes");
      addPage(multiNodeAccessPage);
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
    // N.A.
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void doSaveAs() {
    // N.A.
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isSaveAsAllowed() {
    // N.A.
    return false;
  }


}
