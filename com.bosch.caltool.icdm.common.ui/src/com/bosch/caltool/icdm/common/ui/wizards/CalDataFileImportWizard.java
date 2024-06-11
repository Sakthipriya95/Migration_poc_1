/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.common.ui.wizards;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.PlatformUI;

import com.bosch.caltool.icdm.client.bo.caldataimport.CalDataImporterHandler;
import com.bosch.caltool.icdm.common.ui.actions.IImportRefresher;
import com.bosch.caltool.icdm.common.ui.wizards.pages.AttrValueImpWizardPage;
import com.bosch.caltool.icdm.common.ui.wizards.pages.CalDataFileImpWizardPage;
import com.bosch.caltool.icdm.common.ui.wizards.pages.CompareRuleImpWizardPage;
import com.bosch.caltool.icdm.common.ui.wizards.pages.SummaryImpWizardPage;
import com.bosch.caltool.icdm.model.caldataimport.CalDataImportData;


/**
 * @author bru2cob
 */
public class CalDataFileImportWizard extends Wizard {

  /**
   * AttrValueImpWizardPage instance
   */
  private AttrValueImpWizardPage attrValWizardPage;
  /**
   * CompareRuleImpWizardPage instance
   */
  private CompareRuleImpWizardPage compRuleWizardPage;
  /**
   * SummaryImpWizardPage instance
   */
  private SummaryImpWizardPage summaryWizardPage;

  /**
   * CalDataFileImportWizardData instance
   */
  private final CalDataFileImportWizardData wizardData;

  private final CalDataImporterHandler calDataImportHandler = new CalDataImporterHandler();
  private CalDataImportData calImportData;

  /**
   * @param wizardData CalDataFileImportWizardData
   */
  public CalDataFileImportWizard(final CalDataFileImportWizardData wizardData) {
    this.wizardData = wizardData;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void addPages() {


    // Set tile
    setWindowTitle("Calibration Data Import - " +
        this.wizardData.getParamColDataProvider().getObjectTypeName(this.wizardData.getImportObject()) + " : " +
        this.wizardData.getImportObject().getName());
    // Progress monitor
    setNeedsProgressMonitor(true);

    CalDataFileImpWizardPage fileWizardPage = new CalDataFileImpWizardPage("Import Calibration Data 1");
    addPage(fileWizardPage);
    this.attrValWizardPage = new AttrValueImpWizardPage("Import Calibration Data 2");
    addPage(this.attrValWizardPage);
    this.compRuleWizardPage = new CompareRuleImpWizardPage("Import Calibration Data 3");
    addPage(this.compRuleWizardPage);
    this.summaryWizardPage = new SummaryImpWizardPage("Import Calibration Data 4");
    addPage(this.summaryWizardPage);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean performFinish() {
    refresh();
    return true;
  }

  /**
   * 
   */
  private void refresh() {
    if (PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().isEditorAreaVisible()) {
      IEditorReference[] editorReferences =
          PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getEditorReferences();
      for (IEditorReference iEditorReference : editorReferences) {
        IEditorPart editorPart = iEditorReference.getEditor(false);
        if (!(editorPart instanceof IImportRefresher)) {
          continue;
        }
        IImportRefresher editor = (IImportRefresher) editorPart;
        editor.doImportRefresh();
      }
    }
  }

  /**
   * @return the wizardData
   */
  public CalDataFileImportWizardData getWizardData() {
    return this.wizardData;
  }

  /**
   * @return the attrValWizardPage
   */
  public AttrValueImpWizardPage getAttrValWizardPage() {
    return this.attrValWizardPage;
  }

  /**
   * @return the compRuleWizardPage
   */
  public CompareRuleImpWizardPage getCompRuleWizardPage() {
    return this.compRuleWizardPage;
  }


  /**
   * @return the summaryWizardPage
   */
  public SummaryImpWizardPage getSummaryWizardPage() {
    return this.summaryWizardPage;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public IWizardPage getNextPage(final IWizardPage page) {
    
    IWizardPage wizardPage = super.getNextPage(page);
    
    if (page instanceof CalDataFileImpWizardPage) {
      // only if there are dependencies available display this page
      if (this.wizardData.getAttrValMap().isEmpty()) {
        wizardPage = this.compRuleWizardPage;
      } // Display compare page directly
      else {
        wizardPage = this.attrValWizardPage;
      }
    }
    else if (page instanceof CompareRuleImpWizardPage) {
      CompareRuleImpWizardPage currentPage = (CompareRuleImpWizardPage) page;
      if (!currentPage.isPageComplete()) {
        wizardPage= currentPage;
      }
    }
    return wizardPage;
  }


  /**
   * @return the calDataImportHandler
   */
  public CalDataImporterHandler getCalDataImportHandler() {
    return this.calDataImportHandler;
  }

  /**
   * @param calImportData calImportData
   */
  public void setCalImportData(final CalDataImportData calImportData) {
    this.calImportData = calImportData;

  }


  /**
   * @return the calImportData
   */
  public CalDataImportData getCalImportData() {
    return this.calImportData;
  }
}
