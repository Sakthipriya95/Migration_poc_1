/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.wizard.pages.resolver;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.TableItem;

import com.bosch.caltool.cdr.ui.Activator;
import com.bosch.caltool.cdr.ui.wizards.CalDataReviewWizard;
import com.bosch.caltool.cdr.ui.wizards.pages.ReviewCalDataWizardPage;
import com.bosch.caltool.cdr.ui.wizards.pages.ReviewFilesSelectionWizardPage;
import com.bosch.caltool.excel.ExcelConstants;
import com.bosch.caltool.icdm.client.bo.cdr.CDRHandler;
import com.bosch.caltool.icdm.client.bo.general.CommonDataBO;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.attr.Attribute;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValue;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValueModel;
import com.bosch.caltool.icdm.model.cdr.CDRConstants.DELTA_REVIEW_TYPE;
import com.bosch.caltool.icdm.model.cdr.CDRWizardUIModel;
import com.bosch.caltool.icdm.model.cdr.review.ReviewInput;
import com.bosch.caltool.icdm.model.cdr.review.ReviewOutput;
import com.bosch.caltool.icdm.ws.rest.client.cdr.ReviewServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.rcputils.message.dialogs.MessageDialogUtils;

/**
 * @author say8cob
 */
public class ReviewFilesSelectionPageResolver implements IReviewUIDataResolver {


  /**
   *
   */
  private static final String ICDM_ERR = "ICDM_ERR";

  private final CalDataReviewWizard calDataReviewWizard;

  CDRHandler cdrHandler = new CDRHandler();

  final StringBuilder progressMessage = new StringBuilder("Performing Data Review ...");


  /**
   * @param calDataReviewWizard
   */
  public ReviewFilesSelectionPageResolver(final CalDataReviewWizard calDataReviewWizard) {
    this.calDataReviewWizard = calDataReviewWizard;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void processNextPressed() {
    try {
      this.calDataReviewWizard.getContainer().run(true, false, new IRunnableWithProgress() {

        @Override
        public void run(final IProgressMonitor monitor) {

          // Setting Review Input Model
          ReviewInput reviewInputModel = ReviewFilesSelectionPageResolver.this.calDataReviewWizard
              .createReviewInputModel(ReviewFilesSelectionPageResolver.this.calDataReviewWizard.getCdrWizardUIModel());
          monitor.beginTask(ReviewFilesSelectionPageResolver.this.progressMessage.toString(), 100);
          monitor.worked(50);
          ReviewOutput performReview = null;
          Set<String> pathSet = new TreeSet<>();
          String repeatedParamsExcelPath = null;
          Set<AttributeValueModel> attrWithoutMapping = new TreeSet<>();
          try {
            performReview = ReviewFilesSelectionPageResolver.this.cdrHandler.performReview(reviewInputModel);
            ReviewFilesSelectionPageResolver.this.calDataReviewWizard.setErrorOccuredinReview(false);
            if (reviewInputModel.isDeltaReview() &&
                reviewInputModel.getDeltaReviewType().equals(DELTA_REVIEW_TYPE.PROJECT_DELTA_REVIEW.getDbType()) &&
                !performReview.isDeltaReviewValid()) {
              CommonDataBO dataBo = new CommonDataBO();
              String msgToDisplay = dataBo.getMessage("REVIEW_MESSAGE", "DELTA_TO_NORMAL_RVW");
              CDMLogger.getInstance().infoDialog(msgToDisplay, Activator.PLUGIN_ID);
            }
          }
          catch (ApicWebServiceException e) {
            String errorMsg = e.getMessage();
            if ("CDR.REPEAT_PARAM_EXCEL_REPORT".equalsIgnoreCase(e.getErrorCode()) && (errorMsg != null) &&
                errorMsg.contains("Param Repeat Excel report")) {
              // Error case for Repeated Params in File Selection Page
              String[] filepath = errorMsg.split("\\|");
              repeatedParamsExcelPath = filepath[1].trim();
            }
            else if (ICDM_ERR.equalsIgnoreCase(e.getErrorCode()) && (errorMsg != null) && errorMsg.contains(".ssd")) {
              String[] filePaths = errorMsg.split("\\|");
              pathSet.add(filePaths[1].trim());
              pathSet.add(filePaths[2].trim());
            }
            else if (ICDM_ERR.equalsIgnoreCase(e.getErrorCode()) && (errorMsg != null) &&
                errorMsg.contains("Attribute Value Model")) {

              AttributeValueModel attributeValueModel = new AttributeValueModel();
              Attribute attribute = new Attribute();
              AttributeValue value = new AttributeValue();
              String[] attributeValues = errorMsg.split(":");
              attribute.setId(Long.parseLong(attributeValues[1]));
              attribute.setName(attributeValues[2]);
              value.setId(Long.parseLong(attributeValues[3]));
              value.setName(attributeValues[4]);
              attributeValueModel.setAttr(attribute);
              attributeValueModel.setValue(value);
              attrWithoutMapping.add(attributeValueModel);
              // CONDITION called if the attribute is yes but value not set
              if (attributeValues[3].equals(CalDataReviewWizard.CODE_FOR_ATTRVALUENOTSET)) {
                errorMsg = CommonUtils.concatenate("Value of attribute '", attributeValues[2],
                    "' is <USED> but not set in PIDC");
              }
              // CONDITION called if the attribute is Not set
              else if (attributeValues[3].equals(CalDataReviewWizard.CODE_FOR_ATTRNOTSET)) {
                errorMsg = CommonUtils.concatenate("Value of attribute '", attributeValues[2],
                    "' is <NOT-USED>. Please define a value in the PIDC for the Attribute.");
              }
              else {
                errorMsg = CommonUtils.concatenate("Value ", attributeValues[4], " (", attributeValues[3], ")",
                    " of attribute '", attributeValues[2],
                    "' is not associated to a feature value or <USED> value! Please contact iCDM hotline.");
              }
            }

            // error msg
            //
            if (!attrWithoutMapping.isEmpty() && (errorMsg != null)) {
              ReviewFilesSelectionPageResolver.this.calDataReviewWizard.getCdrWizardUIModel()
                  .setAttrWithoutMapping(attrWithoutMapping);
              CDMLogger.getInstance().errorDialog(errorMsg, e, Activator.PLUGIN_ID);
            }
            else if (!pathSet.isEmpty()) {
              try {
                String dirPath = new ReviewServiceClient().downloadFilesFromServer(pathSet,
                    CommonUtils.getICDMTmpFileDirectoryPath());
                CDMLogger.getInstance().errorDialog(
                    "Syntax errror in SSD file. Please check " + dirPath + " to get more information", e,
                    Activator.PLUGIN_ID);
              }
              catch (ApicWebServiceException exp) {
                CDMLogger.getInstance().errorDialog(exp.getLocalizedMessage(), exp, Activator.PLUGIN_ID);
              }
            }
            else if (repeatedParamsExcelPath != null) {
              // Added to download the repeated params excel file form server
              boolean excelReportNeeded = MessageDialogUtils.getConfirmMessageDialog("Parameters Repeated",
                  "Parameters are repeated in multiple files - resolve this to proceed with the review. \nDo you want an excel report on the parameters ?");
              if (excelReportNeeded) {
                File fileName = new File(repeatedParamsExcelPath);
                final Set<String> filesToBeDownloaded = new HashSet<>();
                filesToBeDownloaded.add(repeatedParamsExcelPath);
                Display.getDefault().asyncExec(new Runnable() {

                  public void run() {
                    final FileDialog fileDialog = new FileDialog(Display.getDefault().getActiveShell(), SWT.SAVE);
                    fileDialog.setText("Save Excel Report");
                    fileDialog.setFilterExtensions(ExcelConstants.FILTER_EXTNS);
                    fileDialog.setFilterNames(ExcelConstants.FILTER_NAMES);
                    fileDialog.setFilterIndex(0);
                    fileDialog.setFileName(fileName.getName());
                    fileDialog.setOverwrite(true);
                    final String fileSelected = fileDialog.open();

                    if (fileSelected != null) {
                      try {
                        new ReviewServiceClient().downloadFilesFromServer(filesToBeDownloaded,
                            new File(fileSelected).getParent());
                      }
                      catch (ApicWebServiceException exp) {
                        CDMLogger.getInstance().errorDialog(exp.getLocalizedMessage(), exp, Activator.PLUGIN_ID);
                      }
                    }
                  }
                });
              }

            }
            else {
              CDMLogger.getInstance().errorDialog(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
            }
            ReviewFilesSelectionPageResolver.this.calDataReviewWizard.setErrorOccuredinReview(true);
          }
          monitor.worked(70);
          ReviewFilesSelectionPageResolver.this.calDataReviewWizard.getCdrWizardUIModel()
              .setReviewOutput(performReview);
          IWizardPage wizardPage = ReviewFilesSelectionPageResolver.this.calDataReviewWizard
              .getNextPage(ReviewFilesSelectionPageResolver.this.calDataReviewWizard.getFilesSelWizPage());

          if (wizardPage instanceof ReviewCalDataWizardPage) {
            ReviewCalDataWizardPage reviewCalDataWizardPage = (ReviewCalDataWizardPage) wizardPage;
            reviewCalDataWizardPage.setPageComplete(true);
            reviewCalDataWizardPage.fillData();
          }
          monitor.done();

        }
      });

    }
    catch (InvocationTargetException | InterruptedException e) {
      CDMLogger.getInstance().errorDialog(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
    }


  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setInput(final CalDataReviewWizard calDataReviewWizard) {
    ReviewFilesSelectionWizardPage filesSelWizPage = this.calDataReviewWizard.getFilesSelWizPage();
    if (!filesSelWizPage.getSelectedFilesPath().isEmpty()) {
      this.calDataReviewWizard.getCdrWizardUIModel().setSelFilesPath(filesSelWizPage.getSelectedFilesPath());
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public CalDataReviewWizard getInput() {
    // TODO Auto-generated method stub
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void fillUIData(final CalDataReviewWizard calDataReviewWizard) {

    ReviewFilesSelectionWizardPage filesSelWizPage = calDataReviewWizard.getFilesSelWizPage();
    CDRWizardUIModel cdrWizardUIModel = calDataReviewWizard.getCdrWizardUIModel();
    if (cdrWizardUIModel.getSelFilesPath().size() == 1) {
      cdrWizardUIModel.getSelFilesPath().forEach(val -> {
        if (val.toLowerCase().endsWith(ReviewFilesSelectionWizardPage.HEX)) {
          filesSelWizPage.getAddFileButton().setEnabled(false);
        }
      });
    }
    if (filesSelWizPage.getFilesList().getItemCount() == 0) {
      if ((null != cdrWizardUIModel.getSelFilesPath()) || !cdrWizardUIModel.getSelFilesPath().isEmpty()) {
        for (String fileName : cdrWizardUIModel.getSelFilesPath()) {
          TableItem tableItem = new TableItem(filesSelWizPage.getFilesList(), SWT.NONE);
          tableItem.setText(fileName);
          filesSelWizPage.getSelectedFilesPath().add(fileName);
        }
      }

      filesSelWizPage.setPageComplete(true);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void processBackPressed() {
    // TODO Auto-generated method stub
  }


}
