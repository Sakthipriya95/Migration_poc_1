/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.wizards;

import java.util.HashSet;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.List;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.bosch.caltool.icdm.model.emr.EMRFileUploadResponse;
import com.bosch.caltool.icdm.model.emr.EmrFile;

/**
 * @author mkl2cob
 */
public class EMRVariantSelectionWizardPage extends WizardPage {

  /**
   * FormToolkit
   */
  private FormToolkit formToolkit;
  /**
   * EMRAssgnComposite
   */
  private EMRAssignVariantComposite codexVarAssgnComp;
  /**
   * set of emr file id's
   */
  private final HashSet<Long> emrFileIds = new HashSet<>();


  /**
   * @param pageName Page Name
   */
  protected EMRVariantSelectionWizardPage(final String pageName) {
    super(pageName);
    setTitle("Import Codex Meaasurement Program Sheet");
    setDescription("Please select the files to import , add optional description and mention scope of validity");
    // disable Next button
    setPageComplete(false);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void createControl(final Composite parent) {
    // set dialog data
    initializeDialogUnits(parent);
    ScrolledComposite scrolledComp = new ScrolledComposite(parent, SWT.V_SCROLL);
    this.codexVarAssgnComp = new EMRAssignVariantComposite(this.emrFileIds, false);
    // initialise wizard
    EMRImportWizard wizard = (EMRImportWizard) getWizard();
    // set the composite details
    Composite workAreaComp =
        this.codexVarAssgnComp.createComposite(scrolledComp, getFormToolkit(), wizard.getPidcVersion());
    workAreaComp.layout(true);
    scrolledComp.setContent(workAreaComp);
    scrolledComp.setExpandHorizontal(true);
    scrolledComp.setExpandVertical(true);
    GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
    scrolledComp.setLayoutData(gridData);
    scrolledComp.setMinSize(workAreaComp.computeSize(SWT.DEFAULT, SWT.DEFAULT));
    workAreaComp.pack();
    setControl(scrolledComp);
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
   * return the table viewer
   *
   * @return
   */
  public List getSheetNameList() {
    return this.codexVarAssgnComp.getSheetNamesList();

  }

  /**
   * @param uploadResponse result
   */
  public void setInput(final EMRFileUploadResponse uploadResponse) {
    if (uploadResponse != null) {
      for (EmrFile obj : uploadResponse.getEmrFileMap().values()) {
        if (obj.getIsVariant()) {
          this.emrFileIds.add(obj.getId());
          // Setting the emr file Ids of the uploaded EMR files
          this.codexVarAssgnComp.getEmrFileIds().add(obj.getId());
        }
      }
    }
    this.codexVarAssgnComp.refreshData();
  }
}
