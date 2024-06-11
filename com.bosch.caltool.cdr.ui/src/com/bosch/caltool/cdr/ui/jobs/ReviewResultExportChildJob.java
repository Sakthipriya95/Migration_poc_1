/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.jobs;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import com.bosch.caltool.cdr.ui.Activator;
import com.bosch.caltool.cdr.ui.editors.ReviewResultEditor;
import com.bosch.caltool.cdr.ui.editors.pages.ReviewResultParamListPage;
import com.bosch.caltool.icdm.client.bo.cdr.CDRHandler;
import com.bosch.caltool.icdm.client.bo.cdr.ReviewResultClientBO;
import com.bosch.caltool.icdm.common.ui.utils.CommonUiUtils;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.cdr.CDRConstants;
import com.bosch.caltool.icdm.model.cdr.CombinedReviewResultExcelExportData;
import com.bosch.caltool.icdm.model.cdr.CombinedRvwExportInputModel;
import com.bosch.caltool.icdm.report.excel.ReviewResultExport;
import com.bosch.caltool.icdm.report.excel.RvwResultExportInput;
import com.bosch.rcputils.jobs.AbstractChildJob;


/**
 * @author pdh2cob
 *
 */
public class ReviewResultExportChildJob extends AbstractChildJob {

  
  private final RvwResultExportInput rvwResultExportInput;
  
  private ReviewResultClientBO resultClientBO;
  
 
  /**
   * @param rvwResultExportInput export input
   */
  public ReviewResultExportChildJob( 
      final RvwResultExportInput rvwResultExportInput) {
    super("Exporting review result for - "+rvwResultExportInput.getReviewResult().getName());
    this.rvwResultExportInput = rvwResultExportInput;
    this.resultClientBO = rvwResultExportInput.getResultClientBO();
  }

  /** 
   * {@inheritDoc}
   */
  @Override
  protected IStatus doRun(IProgressMonitor monitor) { 
    CombinedRvwExportInputModel combinedRvwExportInputModel = getCombinedRvwExportInputModel();
    
    CombinedReviewResultExcelExportData combinedExportData = null;
    //loop through all the selected review results for export
    //workpackage selection option is disabled for multiple review results selection
      if (rvwResultExportInput.getResultClientBO() == null) {
        combinedRvwExportInputModel.setLoadEditorData(true);
        combinedExportData = new CDRHandler().getCombinedExportData(combinedRvwExportInputModel);
        resultClientBO = new ReviewResultClientBO(combinedExportData.getReviewResultEditorData(), null);
        if (CommonUtils.isNull(resultClientBO)) {
          return Status.CANCEL_STATUS;
        }
      }else {
        combinedRvwExportInputModel.setLoadEditorData(false);
        combinedExportData = new CDRHandler().getCombinedExportData(combinedRvwExportInputModel);
      }
      
      //get filtered items and hidden items from editor nattable
    Set<Long> filteredParamIds = new HashSet<>();

    List<String> hiddenColNames = new ArrayList<>();
    
    fillDetailsFromEditor(rvwResultExportInput.getReviewResult().getId(),filteredParamIds,hiddenColNames);
    
    // Call for exporting each ReviewResult
    final ReviewResultExport reviewResultExport =
        new ReviewResultExport(monitor, resultClientBO, rvwResultExportInput);
    String filePath = rvwResultExportInput.getFilePath();
    String fileExt = rvwResultExportInput.getFileExt();
    List<String> qnaireColumns = new ArrayList<>();
    //to get questionnaire columns only if the questionnaire need to be exported
    if(combinedRvwExportInputModel.isOnlyRvwResAndQnaireLstBaseLine() || combinedRvwExportInputModel.isOnlyRvwResAndQnaireWrkSet()) {
      qnaireColumns = getQnaireColumns();
    }
    reviewResultExport.exportReviewResult(hiddenColNames, filteredParamIds,rvwResultExportInput,combinedExportData,qnaireColumns);
    
    String rvwrResultName = rvwResultExportInput.getReviewResult().getName();
    if (monitor.isCanceled()) {
      final File file = new File(filePath + "." + fileExt);
      // delete the created file if the user cancels the export operation in between
      try {
        Files.delete(file.toPath());
      }
      catch (IOException e) {
        CDMLogger.getInstance().error(e.getLocalizedMessage(),e,Activator.PLUGIN_ID);
      }
      CDMLogger.getInstance().info("Export Review Result : " + rvwrResultName + " is cancelled", Activator.PLUGIN_ID);
      return Status.CANCEL_STATUS;
    }
    monitor.done();
    if (rvwResultExportInput.isOpenAutomatically()) {
      String fileFullPath;
      if (filePath.contains(".xlsx") || filePath.contains(".xls")) {
        fileFullPath = filePath;
      }
      else {
        fileFullPath = filePath + "." + fileExt;
      }
      CommonUiUtils.openFile(fileFullPath);
    }

    CDMLogger.getInstance().info("Review Result " + rvwrResultName + " exported successfully to file " + filePath,
        Activator.PLUGIN_ID);
    return Status.OK_STATUS;
    }

  /**
   * @return
   */
  private CombinedRvwExportInputModel getCombinedRvwExportInputModel() {
    CombinedRvwExportInputModel combinedRvwExportInputModel = new CombinedRvwExportInputModel();
    combinedRvwExportInputModel.setRvwResultId( rvwResultExportInput.getReviewResult().getId());
    combinedRvwExportInputModel.setOnlyReviewResult(rvwResultExportInput.isOnlyRvwResult());
    combinedRvwExportInputModel.setOnlyRvwResAndQnaireWrkSet(rvwResultExportInput.isOnlyRvwResAndQnaireWrkSet());
    combinedRvwExportInputModel.setOnlyRvwResAndQnaireLstBaseLine(rvwResultExportInput.isOnlyRvwResAndQnaireLstBaseline());
    return combinedRvwExportInputModel;
  }
  
  private List<String> getQnaireColumns(){
    List<String>  qnaireColList = new ArrayList<>();
    qnaireColList.add(ApicConstants.SERIAL_NO);
    qnaireColList.add(ApicConstants.QUESTION_COL_NAME);
    qnaireColList.add(CommonUiUtils.getMessage(ApicConstants.REVIEW_QUESTIONNAIRES_GRP, ApicConstants.KEY_HINT));
    qnaireColList.add(CommonUiUtils.getMessage(ApicConstants.REVIEW_QUESTIONNAIRES_GRP, ApicConstants.KEY_SERIES_MAT_Y_N));
    qnaireColList.add(CommonUiUtils.getMessage(ApicConstants.REVIEW_QUESTIONNAIRES_GRP, ApicConstants.KEY_MEASURABLE_Y_N));
    qnaireColList.add(CDRConstants.LINK_COL_NAME);
    qnaireColList.add(CommonUiUtils.getMessage(ApicConstants.REVIEW_QUESTIONNAIRES_GRP, ApicConstants.KEY_OPL_OPEN_POINTS));
    qnaireColList.add(CommonUiUtils.getMessage(ApicConstants.REVIEW_QUESTIONNAIRES_GRP, ApicConstants.KEY_REMARK));
    qnaireColList.add(CDRConstants.ANSWER_COL_NAME);
    qnaireColList.add(ApicConstants.RESULT_COL_NAME);
    return qnaireColList;
  }
  
  /**
  *
  */
 private void fillDetailsFromEditor(Long reviewResultId, Set<Long> filteredParamIds,List<String> hiddenColNames) {
   Display.getDefault().syncExec(() -> {
     ReviewResultEditor rvwResultEditor = getReviewResultEditor(reviewResultId);
     if (rvwResultEditor != null) {
       ReviewResultParamListPage rvwResParamListPage = rvwResultEditor.getReviewResultParamListPage();
       filteredParamIds.addAll(rvwResParamListPage.getFilteredCdrParamIds());
       hiddenColNames.addAll(rvwResParamListPage.getHiddenColumns());
     }
   });

 }

 /**
  * @return
  */
 private ReviewResultEditor getReviewResultEditor(Long reviewResultId) {
   for (IWorkbenchWindow window : PlatformUI.getWorkbench().getWorkbenchWindows()) {
     for (IWorkbenchPage page : window.getPages()) {
       for (IEditorReference editor : page.getEditorReferences()) {
         if (editor.getEditor(false) instanceof ReviewResultEditor) {
           ReviewResultEditor reviewResultEditor = (ReviewResultEditor) editor.getEditor(false);
           if (reviewResultEditor.getReviewResultParamListPage().getCdrResult().getId()
               .equals(reviewResultId)) {
             return reviewResultEditor;
           }
         }
       }
     }
   }
   return null;
 }

}
