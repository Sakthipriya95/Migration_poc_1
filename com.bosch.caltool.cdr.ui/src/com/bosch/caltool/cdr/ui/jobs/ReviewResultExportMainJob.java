/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.jobs;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import com.bosch.caltool.icdm.client.bo.cdr.CDRHandler;
import com.bosch.caltool.icdm.client.bo.cdr.ReviewResultClientBO;
import com.bosch.caltool.icdm.common.ui.jobs.rules.MutexRule;
import com.bosch.caltool.icdm.common.ui.utils.IMessageConstants;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.common.util.FileNameUtil;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.a2l.A2lWorkPackage;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.cdr.CDRReviewResult;
import com.bosch.caltool.icdm.report.excel.RvwResultExportInput;
import com.bosch.rcputils.jobs.AbstractChildJob;
import com.bosch.rcputils.jobs.ChildJobFamily;
import com.bosch.rcputils.message.dialogs.MessageDialogUtils;

/**
 * @author pdh2cob
 */
public class ReviewResultExportMainJob extends Job {

  private final RvwResultExportInput mainExportInput;

  /**
  *
  */
  private static final String PATH_EXTEND = "\\";


  /**
   * @param rule mutex rule
   * @param exportInput - input options chosen from export dialog
   */
  public ReviewResultExportMainJob(final MutexRule rule, final RvwResultExportInput exportInput) {
    super("Exporting Review Result(s)");
    setRule(rule);
    this.mainExportInput = exportInput;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected IStatus run(final IProgressMonitor monitor) {
    Job.getJobManager().resume();

    CDMLogger.getInstance().debug("Export Review Result started");

    monitor.beginTask("Export Review Result", IProgressMonitor.UNKNOWN);

    if (this.mainExportInput.isSeparateExportForWp() && this.mainExportInput.getSelectedWpList().isEmpty()) {
      fillWorkpackageInfoForExport();
    }


    List<RvwResultExportInput> rvwResultExportInputList =
        this.mainExportInput.isFromEditor() ? createInputForExport() : createInputForExportFromTree();

    if (CommonUtils.isNotEmpty(rvwResultExportInputList)) {
      ChildJobFamily subJobFamily = new ChildJobFamily(this);

      // loop through all the selected review results for export
      // workpackage selection option is disabled for multiple review results selection
      for (RvwResultExportInput rvwResultExportInput : rvwResultExportInputList) {
        monitor.beginTask("Export Review Result", AbstractChildJob.JOB_TOTAL);
        ReviewResultExportChildJob exportChildJob = new ReviewResultExportChildJob(rvwResultExportInput);
        subJobFamily.add(exportChildJob);
      }

      try {
        subJobFamily.execute(monitor);
      }
      catch (OperationCanceledException | InterruptedException exp) {
        CDMLogger.getInstance().error(exp.getMessage(), exp);
        Thread.currentThread().interrupt();
        return Status.CANCEL_STATUS;
      }
      if (!subJobFamily.isResultOK()) {
        return Status.CANCEL_STATUS;
      }
    }
    monitor.done();
    return Status.OK_STATUS;
  }


  private void fillWorkpackageInfoForExport() {

    java.util.List<A2lWorkPackage> wpListForReview = new ArrayList<>();

    ReviewResultClientBO resultClientBO = null;

    if (CommonUtils.isNull(this.mainExportInput.getResultClientBO())) {
      resultClientBO = new CDRHandler()
          .getRevResultClientBo(this.mainExportInput.getSelectedReviewResultSet().iterator().next().getId());
      this.mainExportInput.setResultClientBO(resultClientBO);
    }
    else {
      resultClientBO = this.mainExportInput.getResultClientBO();
    }
    resultClientBO.getResponse().getA2lWpSet().stream().forEach(wpResponse -> {
      if (!wpListForReview.contains(wpResponse.getA2lWorkPackage())) {
        wpListForReview.add(wpResponse.getA2lWorkPackage());
      }
    });

    this.mainExportInput.getSelectedWpList().addAll(wpListForReview);

  }


  private List<RvwResultExportInput> createInputForExportFromTree() {
    List<RvwResultExportInput> exportInputList = new ArrayList<>();

    for (CDRReviewResult revResult : this.mainExportInput.getSelectedReviewResultSet()) {

      if (this.mainExportInput.isSeparateExportForWp() &&
          CommonUtils.isNotEmpty(this.mainExportInput.getSelectedWpList())) {

        for (A2lWorkPackage a2lWp : this.mainExportInput.getSelectedWpList()) {

          RvwResultExportInput rvwResultExportInput = createRevResExportInput(a2lWp, revResult);
          final File file = new File(
              CommonUtils.getCompleteFilePath(rvwResultExportInput.getFilePath(), rvwResultExportInput.getFileExt()));

          if (CommonUtils.checkIfFileOpen(file)) {
            MessageDialogUtils.getInfoMessageDialog(IMessageConstants.EXCEL_ALREADY_OPEN,
                IMessageConstants.FILE_ALREADY_OPEN_MSG);
            return new ArrayList<>();
          }

          setReviewDetailsForInput(revResult, rvwResultExportInput);
          exportInputList.add(rvwResultExportInput);

        }

      }

      else {
        RvwResultExportInput rvwResultExportInput = createRevResExportInput(null, revResult);
        rvwResultExportInput.setReviewResult(revResult);
        final File file = new File(
            CommonUtils.getCompleteFilePath(rvwResultExportInput.getFilePath(), rvwResultExportInput.getFileExt()));
        if (checkIfFileOpen(file)) {
          return new ArrayList<>();
        }
        exportInputList.add(rvwResultExportInput);
      }


    }

    return exportInputList;
  }


  /**
   * @param revResult
   * @param rvwResultExportInput
   */
  private void setReviewDetailsForInput(final CDRReviewResult revResult,
      final RvwResultExportInput rvwResultExportInput) {
    rvwResultExportInput.setReviewResult(revResult);

    // if wp is selected, resultCLient bo will be created
    if (this.mainExportInput.getResultClientBO() != null) {
      rvwResultExportInput.setResultClientBO(this.mainExportInput.getResultClientBO());
    }
  }

  private List<RvwResultExportInput> createInputForExport() {
    List<RvwResultExportInput> exportInputList = new ArrayList<>();

    if (this.mainExportInput.isSeparateExportForWp() &&
        CommonUtils.isNotEmpty(this.mainExportInput.getSelectedWpList())) {
      for (A2lWorkPackage a2lWp : this.mainExportInput.getSelectedWpList()) {

        RvwResultExportInput rvwResultExportInput = createRevResExportInput(a2lWp, null);
        rvwResultExportInput.setReviewResult(this.mainExportInput.getResultClientBO().getResultBo().getCDRResult());
        final File file = new File(
            CommonUtils.getCompleteFilePath(rvwResultExportInput.getFilePath(), rvwResultExportInput.getFileExt()));
        if (checkIfFileOpen(file)) {
          return new ArrayList<>();
        }
        exportInputList.add(rvwResultExportInput);
      }
    }
    else {
      RvwResultExportInput rvwResultExportInput = createRevResExportInput(null, null);
      rvwResultExportInput.setReviewResult(this.mainExportInput.getResultClientBO().getResultBo().getCDRResult());
      final File file = new File(
          CommonUtils.getCompleteFilePath(rvwResultExportInput.getFilePath(), rvwResultExportInput.getFileExt()));
      if (checkIfFileOpen(file)) {
        return new ArrayList<>();
      }
      exportInputList.add(rvwResultExportInput);
    }
    return exportInputList;
  }


  private boolean checkIfFileOpen(final File file) {
    if (CommonUtils.checkIfFileOpen(file)) {
      MessageDialogUtils.getInfoMessageDialog(IMessageConstants.EXCEL_ALREADY_OPEN,
          IMessageConstants.FILE_ALREADY_OPEN_MSG);
      return true;
    }
    return false;
  }


  /**
   * @return
   */
  private RvwResultExportInput createRevResExportInput(final A2lWorkPackage a2lWp, final CDRReviewResult reviewResult) {

    RvwResultExportInput exportInput = new RvwResultExportInput();

    exportInput.setExportOnlyVisibleCols(this.mainExportInput.isExportOnlyVisibleCols());
    exportInput.setOnlyFiltered(this.mainExportInput.isOnlyFiltered());
    exportInput.setOpenAutomatically(this.mainExportInput.isOpenAutomatically());
    exportInput.setResultClientBO(this.mainExportInput.getResultClientBO());
    exportInput.setSelectedWpList(this.mainExportInput.getSelectedWpList());

    exportInput.setWorkPackage(a2lWp);
    String fileSelected = null;
    CDRReviewResult rvwResultForInput = this.mainExportInput.getResultClientBO() == null ? reviewResult
        : this.mainExportInput.getResultClientBO().getResultBo().getCDRResult();
    if (exportInput.getWorkPackage() != null) {

      getExportFilePath((rvwResultForInput.getName().trim()) + "_" + exportInput.getWorkPackage().getName());
      fileSelected = FileNameUtil.formatFileName(
          ((rvwResultForInput.getName().trim()) + "_" + exportInput.getWorkPackage().getName()),
          ApicConstants.INVALID_CHAR_PTRN);
    }
    else {
      fileSelected = FileNameUtil.formatFileName(rvwResultForInput.getName().trim(), ApicConstants.INVALID_CHAR_PTRN);
    }

    if ((this.mainExportInput.getSelectedReviewResultSet() != null) &&
        (this.mainExportInput.getSelectedReviewResultSet().size() > 1)) {
      fileSelected = fileSelected + "_" + System.currentTimeMillis();
    }
    String fileFullPath = this.mainExportInput.getFilePath() + PATH_EXTEND + fileSelected;

    exportInput.setFilePath(fileFullPath);
    exportInput.setFileExt(this.mainExportInput.getFileExt());
    exportInput.setOnlyRvwResult(mainExportInput.isOnlyRvwResult());
    exportInput.setOnlyRvwResAndQnaireWrkSet(mainExportInput.isOnlyRvwResAndQnaireWrkSet());
    exportInput.setOnlyRvwResAndQnaireLstBaseline(mainExportInput.isOnlyRvwResAndQnaireLstBaseline());
    
    return exportInput;
  }

  private String getExportFilePath(final String fileName) {
    String fileSelect = FileNameUtil.formatFileName(fileName.trim(), ApicConstants.INVALID_CHAR_PTRN);
    return this.mainExportInput.getFilePath() + PATH_EXTEND + fileSelect;
  }


}
