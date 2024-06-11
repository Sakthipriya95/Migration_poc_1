/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import com.bosch.caltool.apic.ui.Activator;
import com.bosch.caltool.apic.ui.editors.PIDCEditor;
import com.bosch.caltool.apic.ui.editors.PIDCEditorInput;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersion;

/**
 * Action class to copy name of PIDC and variant to clipboard
 *
 * @author pdh2cob
 */
public class OpenNewPidcAction extends Action {

  /**
   * Action name
   */

  private final String actionName;

  /**
   * PIDC Version
   */
  private final PidcVersion selPidcVer;

  /**
   * @param selPidcVer - selected pidc version
   * @param actionName - Action name
   */
  public OpenNewPidcAction(final PidcVersion selPidcVer, final String actionName) {
    this.selPidcVer = selPidcVer;
    this.actionName = actionName;
    // Properties of the context menu will be set here
    setProperties();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void run() {
    BusyIndicator.showWhile(Display.getDefault().getActiveShell().getDisplay(), new Runnable() {

      @Override
      public void run() {
        try {
          PIDCEditorInput input = new PIDCEditorInput(OpenNewPidcAction.this.selPidcVer, false);

          // open pidc editor
          PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().openEditor(input, PIDCEditor.EDITOR_ID);
        }
        catch (PartInitException e) {
          CDMLogger.getInstance().error(e.getMessage(), e, Activator.PLUGIN_ID);
        }
      }
    });
  }

  /**
   * Method to set the context menu properties
   */
  private void setProperties() {
    setText(this.actionName);
    setEnabled(true);

  }


}
