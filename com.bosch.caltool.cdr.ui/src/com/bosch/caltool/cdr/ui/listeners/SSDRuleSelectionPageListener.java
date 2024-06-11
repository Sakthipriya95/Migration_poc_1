/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.listeners;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Display;

import com.bosch.caltool.cdr.ui.wizards.CalDataReviewWizard;
import com.bosch.caltool.cdr.ui.wizards.pages.SSDRuleSelectionPage;
import com.bosch.caltool.icdm.common.ui.dialogs.FileDialogHandler;
import com.bosch.caltool.icdm.model.ssd.SSDReleaseIcdmModel;

/**
 * @author say8cob
 */
public class SSDRuleSelectionPageListener {


  private final CalDataReviewWizard calDataReviewWizard;
  private final SSDRuleSelectionPage ruleSelectionPageNew;

  /**
   * @param calDataReviewWizardNew
   * @param dataSelectionWizardPageValidator
   */
  public SSDRuleSelectionPageListener(final CalDataReviewWizard calDataReviewWizardNew) {
    this.calDataReviewWizard = calDataReviewWizardNew;
    this.ruleSelectionPageNew = calDataReviewWizardNew.getSsdRuleSelPage();
  }


  /**
  *
  */
  public void createActionListeners() {

    this.ruleSelectionPageNew.getSsdFileTxt().addModifyListener(new ModifyListener() {

      @Override
      public void modifyText(final ModifyEvent arg0) {

        String text = SSDRuleSelectionPageListener.this.ruleSelectionPageNew.getSsdFileTxt().getText();
        if ((text == null) || (text.length() == 0)) {
          SSDRuleSelectionPageListener.this.ruleSelectionPageNew.getSsdTabViewer().getGrid().setEnabled(true);
          SSDRuleSelectionPageListener.this.ruleSelectionPageNew.getFeaValTabViewer().getGrid().setEnabled(true);
          SSDRuleSelectionPageListener.this.ruleSelectionPageNew.setSelSSDPath(null);
          SSDRuleSelectionPageListener.this.calDataReviewWizard.getCdrWizardUIModel().setSsdRuleFilePath(null);
        }
      }
    });

    this.ruleSelectionPageNew.getSsdFileBrowse().addSelectionListener(new SelectionAdapter() {

      /**
       * {@inheritDoc}
       */
      @Override
      public void widgetSelected(final SelectionEvent e) {
        FileDialogHandler fileDialog =
            new FileDialogHandler(Display.getCurrent().getActiveShell(), SWT.OPEN | SWT.SINGLE);
        fileDialog.setText("Select ssd file for review");
        fileDialog.setFilterNames(new String[] { "SSD Files (*.ssd)" });
        // set the extensions order based on user previous selection
        fileDialog.setFilterExtensions(new String[] { "*.SSD" });

        String result = fileDialog.open();
        String selectedFileName = fileDialog.getFileName();
        setSSDfields(result, selectedFileName);
      }


    });


    this.ruleSelectionPageNew.getSsdTabViewer().getGrid().addSelectionListener(new SelectionAdapter() {

      /**
       * {@inheritDoc}
       */
      @Override
      public void widgetSelected(final SelectionEvent e) {
        final IStructuredSelection selection =
            (IStructuredSelection) SSDRuleSelectionPageListener.this.ruleSelectionPageNew.getSsdTabViewer()
                .getSelection();
        SSDReleaseIcdmModel ssdReleaseIcdmModel = (SSDReleaseIcdmModel) selection.getFirstElement();
        SSDRuleSelectionPageListener.this.calDataReviewWizard.getCdrWizardUIModel().setSsdRuleFilePath(null);
        SSDRuleSelectionPageListener.this.calDataReviewWizard.getCdrWizardUIModel()
            .setSsdReleaseId(ssdReleaseIcdmModel.getReleaseId());
        SSDRuleSelectionPageListener.this.ruleSelectionPageNew.setSelSSDPath(null);
        SSDRuleSelectionPageListener.this.ruleSelectionPageNew.setFeaValInput();
        SSDRuleSelectionPageListener.this.ruleSelectionPageNew.setPageComplete(true);

      }
    });


    this.ruleSelectionPageNew.getFeaValTabViewer().getGrid().addSelectionListener(new SelectionAdapter() {

      /**
       * {@inheritDoc}
       */
      @Override
      public void widgetSelected(final SelectionEvent e) {

        // Do nothing as of now

      }
    });


    this.ruleSelectionPageNew.getFeaValFilterTxt().addModifyListener(new ModifyListener() {

      /**
       * {@inheritDoc}
       */
      @Override
      public void modifyText(final ModifyEvent event) {

        final String text =
            SSDRuleSelectionPageListener.this.ruleSelectionPageNew.getFeaValFilterTxt().getText().trim();
        SSDRuleSelectionPageListener.this.ruleSelectionPageNew.getFeaValTabFilters().setFilterText(text);
        SSDRuleSelectionPageListener.this.ruleSelectionPageNew.getFeaValTabViewer().refresh();
      }
    });


    this.ruleSelectionPageNew.getSsdRelFilterTxt().addModifyListener(new ModifyListener() {

      /**
       * {@inheritDoc}
       */
      @Override
      public void modifyText(final ModifyEvent event) {

        final String text =
            SSDRuleSelectionPageListener.this.ruleSelectionPageNew.getSsdRelFilterTxt().getText().trim();
        SSDRuleSelectionPageListener.this.ruleSelectionPageNew.getSsdReleaseTabFilter().setFilterText(text);
        SSDRuleSelectionPageListener.this.ruleSelectionPageNew.getSsdTabViewer().refresh();
      }
    });

  }

  /**
   * @param selectedSsdFilePath
   * @param selectedFileName
   */
  public void setSSDfields(final String selectedSsdFilePath, final String selectedFileName) {
    if ((selectedFileName != null) && (selectedFileName.length() > 0)) {
      SSDRuleSelectionPageListener.this.ruleSelectionPageNew.setCurrentSsdFilePath(selectedSsdFilePath);
      SSDRuleSelectionPageListener.this.ruleSelectionPageNew.getSsdFileTxt().setText(selectedFileName);
      SSDRuleSelectionPageListener.this.ruleSelectionPageNew.getSsdTabViewer().getGrid().setEnabled(false);
      SSDRuleSelectionPageListener.this.ruleSelectionPageNew.getFeaValTabViewer().getGrid().setEnabled(false);
      SSDRuleSelectionPageListener.this.ruleSelectionPageNew.setSelSSDPath(selectedSsdFilePath);
      SSDRuleSelectionPageListener.this.ruleSelectionPageNew.setSelSSDRel(null);
      SSDRuleSelectionPageListener.this.calDataReviewWizard.getCdrWizardUIModel().setSsdReleaseId(null);
      SSDRuleSelectionPageListener.this.calDataReviewWizard.getCdrWizardUIModel()
          .setSsdRuleFilePath(selectedSsdFilePath);
    }
    else {
      SSDRuleSelectionPageListener.this.ruleSelectionPageNew.getSsdTabViewer().getGrid().setEnabled(true);
      SSDRuleSelectionPageListener.this.ruleSelectionPageNew.getFeaValTabViewer().getGrid().setEnabled(true);
    }
    SSDRuleSelectionPageListener.this.ruleSelectionPageNew.updatePageContainer();
  }
}
