/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.product.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;

import com.bosch.caltool.apic.ui.Activator;
import com.bosch.caltool.icdm.common.ui.utils.CommonUiUtils;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.product.dialogs.CaldataAnalyzerDisclaimerDialog;
import com.bosch.caltool.icdm.ui.editors.CaldataAnalyzerEditor;
import com.bosch.caltool.icdm.ui.editors.CaldataAnalyzerEditorInput;

/**
 * @author pdh2cob
 */
public class CaldataAnalyzerAction extends Action implements IWorkbenchAction {

  private static final String CAL_DATA_ANALYZER_ACTION_ID = "com.bosch.icdm.caldataanalyzer";

  private static final String ACTION_TEXT = "Open Caldata Analyzer";

  /**
   * {@inheritDoc}
   */
  @Override
  public void dispose() {
    // TODO Auto-generated method stub

  }

  /**
   *
   */
  public CaldataAnalyzerAction() {
    setId(CAL_DATA_ANALYZER_ACTION_ID);
    super.setText(ACTION_TEXT);
    // Set the Image for pidc stat report
    super.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.CDA_EDITOR_16X16));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void run() {
    // To Hide the Intro Page
    CommonUiUtils.hideIntroViewPart();
    boolean result = createDisclaimerDialog();
    if (result) {
      final CaldataAnalyzerEditorInput input = new CaldataAnalyzerEditorInput();
      try {
        PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().openEditor(input,
            CaldataAnalyzerEditor.EDITOR_ID);
      }
      catch (PartInitException e) {
        CDMLogger.getInstance().error(e.getMessage(), e, Activator.PLUGIN_ID);
      }


    }

  }

  /**
   * Method to create disclaimer dialog and get the acceptance result
   */
  private boolean createDisclaimerDialog() {
    boolean result = false;
    IWorkbench workbench = PlatformUI.getWorkbench();
    Shell parent = workbench.getActiveWorkbenchWindow().getShell();
    CaldataAnalyzerDisclaimerDialog dialog = new CaldataAnalyzerDisclaimerDialog(parent);
    dialog.open();
    if (dialog.getReturnCode() == Window.OK) {
      result = true;
    }
    return result;
  }

}
