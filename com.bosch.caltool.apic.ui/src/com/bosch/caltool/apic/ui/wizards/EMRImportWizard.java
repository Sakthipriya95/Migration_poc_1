/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.wizards;

import java.util.Map;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IEditorInput;

import com.bosch.caltool.apic.ui.editors.PIDCEditorInput;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersion;

/**
 * this class is for the wizard to import CODEX measurement program
 *
 * @author mkl2cob
 */
public class EMRImportWizard extends Wizard {

  /**
   * CodexFileImportWizardPage
   */
  private EMRFileImportWizardPage codexFileImprtWizardPge;

  /**
   * CodexVariantSelectionWizardPage
   */
  private EMRVariantSelectionWizardPage varAssignPage;

  /**
   * CodexResultWizardPage
   */
  private EMRResultWizardPage codexResultPage;

  private final IEditorInput editorInput;


  /**
   * PidcVersion
   */
  private final PidcVersion pidcVersion;

  /**
   * Constructor
   *
   * @param pidcEditorInput
   */
  public EMRImportWizard(final IEditorInput pidcEditorInput) {
    this.pidcVersion = ((PIDCEditorInput) pidcEditorInput).getSelectedPidcVersion();
    this.editorInput = pidcEditorInput;
    setNeedsProgressMonitor(true);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean performFinish() {
    if (isVariantsAvailable() && this.codexFileImprtWizardPge.isVarAssgnPageNeeded()) {
      // just close the wizard
      super.performCancel();
    }
    return true;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void addPages() {
    // Set title
    setWindowTitle("Codex Measurement Program - Import Wizard");
    this.codexFileImprtWizardPge = new EMRFileImportWizardPage("File Selection Page");
    addPage(this.codexFileImprtWizardPge);

    this.codexResultPage = new EMRResultWizardPage("Result Page");
    addPage(this.codexResultPage);

    if (isVariantsAvailable()) {
      this.varAssignPage = new EMRVariantSelectionWizardPage("Variant Selection Page");
      addPage(this.varAssignPage);
    }

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public IWizardPage getNextPage(final IWizardPage page) {
    if (page instanceof EMRFileImportWizardPage) {

      if (!this.codexFileImprtWizardPge.uploadSuccess()) {
        return this.codexFileImprtWizardPge;
      }
    }

    if (page instanceof EMRResultWizardPage) {

      if (!this.codexFileImprtWizardPge.isVarAssgnPageNeeded()) {
        return null;
      }

    }
    return super.getNextPage(page);
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public boolean canFinish() {
    if (isVariantsAvailable() && this.codexFileImprtWizardPge.isVarAssgnPageNeeded()) {
      // when the finish has to be enabled only at the variant page
      return this.codexFileImprtWizardPge.uploadSuccess() && this.varAssignPage.isPageComplete();
    }
    // when the finish can be enabled at the result page itself
    return this.codexFileImprtWizardPge.uploadSuccess() && this.codexResultPage.isPageComplete();
  }

  /**
   * @return boolean
   */
  boolean isVariantsAvailable() {
    Map<Long, PidcVariant> variantsMap =
        ((PIDCEditorInput) this.editorInput).getPidcVersionBO().getPidcDataHandler().getVariantsMap(false);
    return CommonUtils.isNotEmpty(variantsMap);
  }

  /**
   * @return the varAssignPage
   */
  public EMRVariantSelectionWizardPage getVarAssignPage() {
    return this.varAssignPage;
  }

  /**
   * @return
   */
  public PidcVersion getPidcVersion() {
    return this.pidcVersion;
  }

}
