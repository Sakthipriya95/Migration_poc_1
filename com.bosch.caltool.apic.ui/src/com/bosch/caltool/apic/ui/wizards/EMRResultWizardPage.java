/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.wizards;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.bosch.caltool.icdm.model.emr.EMRFileUploadResponse;
import com.bosch.rcputils.griddata.GridDataUtil;

/**
 * @author mkl2cob
 */
public class EMRResultWizardPage extends WizardPage {

  /**
   * FormToolkit
   */
  private FormToolkit formToolkit;
  /**
   * EMRResultComposite
   */
  private EMRResultComposite codexResultComp;

  /**
   * @param pageName String
   */
  protected EMRResultWizardPage(final String pageName) {
    super(pageName);
    setTitle("Import Codex Measurement Program Sheet");
    setDescription("Result of upload operation");
    // to disable the finish button
    setPageComplete(false);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void createControl(final Composite parent) {
    initializeDialogUnits(parent);
    final Composite workArea = new Composite(parent, SWT.NONE);
    this.codexResultComp = new EMRResultComposite(false, false,null);
    this.codexResultComp.createComposite(workArea, getFormToolkit());
    final GridLayout layout = new GridLayout();
    workArea.setLayout(layout);
    workArea.setLayoutData(GridDataUtil.getInstance().createGridData());
    workArea.pack();
    setControl(workArea);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public IWizardPage getPreviousPage() {
    // disabling the back button
    return null;
  }


  /**
   * This method initializes formToolkit
   *
   * @return org.eclipse.ui.forms.widgets.FormToolkit
   */
  protected FormToolkit getFormToolkit() {
    if (this.formToolkit == null) {
      this.formToolkit = new FormToolkit(Display.getCurrent());
    }
    return this.formToolkit;
  }

  /**
   * @param uploadResponse
   */
  public void setInput(final EMRFileUploadResponse uploadResponse) {
    this.codexResultComp.setErrorsInput(uploadResponse);
  }

  /**
   * show success controls in the wizard page
   */
  public void showSuccessControls() {
    this.codexResultComp.showSuccessControls();
  }
}
