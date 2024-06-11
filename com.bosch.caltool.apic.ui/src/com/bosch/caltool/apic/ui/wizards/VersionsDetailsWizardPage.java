/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.wizards;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.bosch.caltool.apic.ui.dialogs.PIDCVersionDetailsSection;
import com.bosch.caltool.icdm.client.bo.apic.pidc.PIDCCreationWizardData;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.rcputils.griddata.GridDataUtil;


/**
 * ICDM-2362
 *
 * @author mkl2cob
 */
public class VersionsDetailsWizardPage extends WizardPage {

  /**
   *
   */
  private FormToolkit formToolkit;
  /**
   *
   */
  private PIDCVersionDetailsSection versionSection;

  /**
   * @param pageName page Name
   */
  protected VersionsDetailsWizardPage(final String pageName) {
    super(pageName);
    setTitle("Version Details");
    setDescription("Enter the Version details");
    setPageComplete(false);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void createControl(final Composite parent) {
    initializeDialogUnits(parent);
    final Composite workArea = new Composite(parent, SWT.NONE);
    createComposite(workArea);
    final GridLayout layout = new GridLayout();
    workArea.setLayout(layout);
    workArea.setLayoutData(GridDataUtil.getInstance().createGridData());
    workArea.pack();
    setControl(workArea);
  }

  /**
   * @param workArea
   */
  private void createComposite(final Composite workArea) {
    GridData gridData = GridDataUtil.getInstance().getGridData();
    Composite composite = getFormToolkit().createComposite(workArea);
    composite.setLayout(new GridLayout());
    composite.setLayoutData(gridData);

    this.versionSection = new PIDCVersionDetailsSection(composite, getFormToolkit(), false, null);
    this.versionSection.createSectionPidcVersion();
    this.versionSection.getVersionNameTxt().addModifyListener(e -> getContainer().updateButtons());
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
   * {@inheritDoc}
   */
  @Override
  public boolean canFlipToNextPage() {
    boolean canProceed;
    if (CommonUtils.isEmptyString(this.versionSection.getVersionNameTxt().getText())) {
      canProceed = false;
    }
    else {
      canProceed = true;
    }
    return canProceed;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isPageComplete() {
    return canFlipToNextPage();
  }

  /**
   *
   */
  public void nextPressed() {
    PIDCCreationWizardData wizardData = ((PIDCCreationWizard) getWizard()).getWizardData();
    wizardData.setVersionName(this.versionSection.getVersionNameTxt().getText());
    wizardData.setVersionDescEng(this.versionSection.getVersionDescEngTxt().getText());
    wizardData.setVersionDescGer(this.versionSection.getVersionDescGerTxt().getText());
    ((PIDCCreationWizard) getWizard()).setFinishFlag(true);
  }
}
