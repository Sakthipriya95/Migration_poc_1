/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.actions;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

import com.bosch.caltool.apic.ui.Activator;
import com.bosch.caltool.apic.ui.dialogs.CustomProgressDialog;
import com.bosch.caltool.apic.ui.wizards.EMRResultComposite;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.emr.EMRFileUploadResponse;
import com.bosch.caltool.icdm.ws.rest.client.apic.emr.EmrFileServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * @author MKL2COB
 */
public class EMRReloadBtnAction {

  /**
   * reload button
   */
  private final Button reloadButton;
  /**
   * EMRFileUploadResponse
   */
  private EMRFileUploadResponse reloadEmrFile;

  /**
   * Constructor
   *
   * @param parent Composite
   * @param style int
   * @param emrResultComposite EMRResultComposite
   */
  public EMRReloadBtnAction(final Composite parent, final int style, final EMRResultComposite emrResultComposite) {
    // create button
    this.reloadButton = new Button(parent, style);
    this.reloadButton.setText("Try to reload sheet");
    // set layout data
    GridData btnGridData = new GridData();
    btnGridData.horizontalSpan = 2;
    this.reloadButton.setLayoutData(btnGridData);

    this.reloadButton.addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final org.eclipse.swt.events.SelectionEvent e) {
        whenButtonPressed(emrResultComposite);
      }


    });

  }

  /**
   * @param emrResultComposite
   */
  private void whenButtonPressed(final EMRResultComposite emrResultComposite) {
    EmrFileServiceClient fileServClient = new EmrFileServiceClient();

    // show progress monitor dilaog
    ProgressMonitorDialog dialog = new CustomProgressDialog(Display.getDefault().getActiveShell());
    try {
      dialog.run(true, true, new IRunnableWithProgress() {

        @Override
        public void run(final IProgressMonitor monitor) {
          monitor.beginTask("Reloading sheet", 100);
          monitor.worked(30);
          // call the reload service
          callReloadService(emrResultComposite, fileServClient);
          monitor.done();
        }

      });

      if (null != EMRReloadBtnAction.this.reloadEmrFile) {
        // when there is response without errors
        emrResultComposite.changeStatus(EMRReloadBtnAction.this.reloadEmrFile);
      }
    }
    catch (InvocationTargetException | InterruptedException exp) {
      CDMLogger.getInstance().errorDialog(exp.getMessage(), exp, Activator.PLUGIN_ID);
    }
  }

  /**
   * @param emrResultComposite
   * @param fileServClient
   */
  private void callReloadService(final EMRResultComposite emrResultComposite,
      final EmrFileServiceClient fileServClient) {
    try {
      this.reloadEmrFile = fileServClient.reloadEmrFile(emrResultComposite.getEMRFileId());
    }
    catch (ApicWebServiceException exp) {
      // show the exception in a dilaog
      CDMLogger.getInstance().errorDialog(exp.getMessage(), exp, Activator.PLUGIN_ID);
    }
  }

  /**
   * @return Button
   */
  public Button getButton() {
    return this.reloadButton;
  }
}
