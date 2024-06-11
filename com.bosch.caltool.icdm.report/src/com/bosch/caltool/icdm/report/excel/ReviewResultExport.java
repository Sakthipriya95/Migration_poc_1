/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.report.excel;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.eclipse.core.runtime.IProgressMonitor;

import com.bosch.caltool.excel.ExcelFactory;
import com.bosch.caltool.excel.ExcelFile;
import com.bosch.caltool.icdm.client.bo.cdr.ReviewResultBO;
import com.bosch.caltool.icdm.client.bo.cdr.ReviewResultClientBO;
import com.bosch.caltool.icdm.client.bo.cdr.ReviewResultClientBO.SortColumns;
import com.bosch.caltool.icdm.client.bo.qnaire.QnaireRespEditorDataHandler;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.a2l.A2lWorkPackage;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.cdr.CDRConstants.RULE_SOURCE;
import com.bosch.caltool.icdm.model.cdr.CDRResultFunction;
import com.bosch.caltool.icdm.model.cdr.CDRResultParameter;
import com.bosch.caltool.icdm.model.cdr.CombinedReviewResultExcelExportData;
import com.bosch.caltool.icdm.model.cdr.DATA_REVIEW_SCORE;
import com.bosch.caltool.icdm.model.cdr.QnaireResponseCombinedModel;
import com.bosch.caltool.icdm.model.cdr.RvwAttrValue;
import com.bosch.caltool.icdm.model.cdr.RvwFile;
import com.bosch.caltool.icdm.model.cdr.RvwParametersSecondary;
import com.bosch.caltool.icdm.model.cdr.RvwParticipant;
import com.bosch.caltool.icdm.model.cdr.RvwResultsSecondary;
import com.bosch.caltool.icdm.model.cdr.RvwWpResp;
import com.bosch.caltool.icdm.report.Activator;
import com.bosch.caltool.icdm.report.common.ExcelCommon;


/**
 * @author bru2cob
 */
public class ReviewResultExport {

  /**
   *
   */
  private static final int BIG_COMMENT_LENGTH = 900;
  /**
   *
   */
  private static final int CELL_LIMIT = 20;
  /**
   * Array index for Attr Val used for review.
   */
  private static final int ATTR_VAL_IDX = 18;

  /**
   * Array index for export options used for review.
   */
  private static final int EXPORT_VAL_IDX = 19;

  /**
   * String builder initial Capacity
   */
  private static final int STR_BUILDER_SIZE = 100;

  private static final int RVW_RESULT_EXPORT_WORK_TIME_30 = 30;

  private static final int RVWINF_SHEET_FILE_RVWED_COL_NO = 15;

  private static final int RVWINF_SHEET_FUN_RVWED_COL_NO = 17;

  private static final int RVWINF_SHEET_FILE_ATCHD_COL_NO = 16;

  private static final int RVW_RSLT_FREEZE_PANE_ARG_NO_1 = 2;

  private static final int RVW_RES_EXPORT_COMMENT_HEIGHT = 20;

  private static final int RVW_RES_EXPORT_COMMENT_WIDTH = 4;

  /**
   * The progress monitor instance to which progess information is to be set.
   */
  private final IProgressMonitor monitor;

  private final ReviewResultClientBO cdrResult;

  /**
   * Cellstyle for header row
   */
  private CellStyle headerCellStyle;

  /**
   * Normal cell style
   */
  private CellStyle cellStyle;
  /**
   * Font instance
   */
  private Font font;

  private final RvwResultExportInput rvwResultExportInput;

  private final Set<Long> exportedFuncIdSet = new HashSet<>();
  private CellStyle reviewScoreColRedCellStyle;
  private CellStyle reviewScoreColYellowCellStyle;
  private CellStyle reviewScoreColBlueCellStyle;
  private CellStyle reviewScoreColGreenCellStyle;
  private CellStyle reviewScoreColWhiteCellStyle;

  /**
   * Constructor
   *
   * @param monitor IProgressMonitor
   * @param cdrResult ReviewResultBO
   * @param rvwResultExportInput export options
   */
  public ReviewResultExport(final IProgressMonitor monitor, final ReviewResultClientBO cdrResult,
      final RvwResultExportInput rvwResultExportInput) {
    this.monitor = monitor;
    this.cdrResult = cdrResult;
    this.rvwResultExportInput = rvwResultExportInput;
  }

  /**
   * @param filePath the file Path
   * @param fileExtn the file Extn
   * @param filteredParamIds the filtered out review result param Ids
   * @param exportOnlyVisibleCols flag to export only visible columns in iCDM
   * @param hiddenColNames the hidden column names in review result editor
   * @param onlyFiltered the flag to export only filtered(visible) rows
   * @param rvwResultExportInput2
   */
  public void exportReviewResult(final List<String> hiddenColNames, final Set<Long> filteredParamIds,
      final RvwResultExportInput rvwResultExportInput, final CombinedReviewResultExcelExportData combinedExportData,
      final List<String> qnaireColumns) {

    String filePath = rvwResultExportInput.getFilePath();
    String fileExt = rvwResultExportInput.getFileExt();

    final boolean exportOnlyVisibleCols = rvwResultExportInput.isExportOnlyVisibleCols();

    final ExcelFactory exFactory = ExcelFactory.getFactory(fileExt);
    final ExcelFile xlFile = exFactory.getExcelFile();
    final Workbook workbook = xlFile.createWorkbook();

    createSheet(workbook);

    this.headerCellStyle = ExcelCommon.getInstance().createHeaderCellStyle(workbook);
    this.cellStyle = ExcelCommon.getInstance().createCellStyle(workbook);

    // create Review Score column Cell Styles
    createReviewScoreCellStyle(workbook);


    this.font = ExcelCommon.getInstance().createFont(workbook);


    this.monitor.worked(RVW_RESULT_EXPORT_WORK_TIME_30);
    this.monitor.subTask("Exporting review results . . .");

    CDMLogger.getInstance().debug("ReviewResult work sheet creation in export started");

    createResultWorkSheet(workbook, exportOnlyVisibleCols, hiddenColNames, rvwResultExportInput.isOnlyFiltered(),
        filteredParamIds);


    CDMLogger.getInstance().debug("ReviewResult work sheet creation in export completed");

    this.monitor.worked(RVW_RESULT_EXPORT_WORK_TIME_30);
    this.monitor.subTask("Exporting review information. . .");

    createReviewInfoWorkSheet(workbook);

    CDMLogger.getInstance().debug("ReviewInfo work sheet creation in export completed");

    createQnaireRespWorkSheet(rvwResultExportInput, combinedExportData, qnaireColumns, workbook);

    final String fileFullPath;
    if (filePath.contains(".xlsx") || filePath.contains(".xls")) {
      fileFullPath = filePath;
    }
    else {
      fileFullPath = filePath + "." + fileExt;
    }

    try (OutputStream fileOut = new FileOutputStream(fileFullPath)) {

      workbook.setSelectedTab(0);
      workbook.write(fileOut);
      fileOut.flush();

    }
    catch (IOException ioe) {
      CDMLogger.getInstance().error(ioe.getMessage(), ioe, Activator.PLUGIN_ID);
    }

  }

  /**
   * Method to create the questionnaire response sheets in combined excel export
   *
   * @param rvwResultExportInput as input
   * @param combinedExportData as input
   * @param qnaireColumns
   * @param workbook
   */
  private void createQnaireRespWorkSheet(final RvwResultExportInput rvwResultExportInput,
      final CombinedReviewResultExcelExportData combinedExportData, final List<String> qnaireColumns,
      final Workbook workbook) {
    if (rvwResultExportInput.isOnlyRvwResAndQnaireWrkSet() || rvwResultExportInput.isOnlyRvwResAndQnaireLstBaseline()) {
      createQnaireResponseSheet(combinedExportData, qnaireColumns, workbook);
    }
  }

  /**
   * @param combinedExportData
   * @param qnaireColumns
   * @param workbook
   * @param pidcVersionWithDetails
   * @return
   */
  private void createQnaireResponseSheet(final CombinedReviewResultExcelExportData combinedExportData,
      final List<String> qnaireColumns, final Workbook workbook) {
    int qnaireCounter = 1;
    for (RvwWpResp rvwWpResp : combinedExportData.getRvwWpRespSet()) {
      Set<QnaireResponseCombinedModel> qnaireRespSet =
          combinedExportData.getQnaireRespCombinedModelMap().get(rvwWpResp.getId());
      if (CommonUtils.isNotEmpty(qnaireRespSet)) {
        SortedSet<QnaireResponseCombinedModel> qnaireResponseCombinedModels = new TreeSet<>(qnaireRespSet);
        for (QnaireResponseCombinedModel qnaireResponseCombinedModel : qnaireResponseCombinedModels) {
          if (isQuestionnaireRelevant(qnaireResponseCombinedModel.getA2lWorkPackage())) {
            QnaireRespEditorDataHandler qnaireRespEditorDataHandler = new QnaireRespEditorDataHandler(
                qnaireResponseCombinedModel, combinedExportData.getPidcVersionWithDetails());
            new QuestResponseExport(this.monitor).exportQuestionnaireResponseReport(
                qnaireRespEditorDataHandler.getAllQuestionAnswers(), qnaireRespEditorDataHandler, qnaireColumns,
                workbook, qnaireCounter, qnaireResponseCombinedModel);
            // increment qnaire counter
            qnaireCounter++;
          }
        }
      }
    }
  }

  /**
   * @param workbook - instance of a workbook
   */
  public void createReviewScoreCellStyle(final Workbook workbook) {
    this.reviewScoreColRedCellStyle = ExcelCommon.getInstance().createRatingNotReviewedCellStyle(workbook);
    this.reviewScoreColYellowCellStyle = ExcelCommon.getInstance().createRatingPrelimCalScoreCellStyle(workbook);
    this.reviewScoreColBlueCellStyle = ExcelCommon.getInstance().createRatingCalibratedCellStyle(workbook);
    this.reviewScoreColGreenCellStyle = ExcelCommon.getInstance().createRatingCmpltdRChkdCellStyle(workbook);
    this.reviewScoreColWhiteCellStyle = ExcelCommon.getInstance().createDefaultReviewScoreCellStyle(workbook);
  }


  /**
   * @param reviewScore reviewScore
   * @return the review Score Column CellStyle
   */
  private CellStyle getReviewScoreColCellStyle(final DATA_REVIEW_SCORE reviewScore) {
    CellStyle reviewScoreColCellStyle;

    switch (reviewScore.getRating()) {
      case DATA_REVIEW_SCORE.RATING_NOT_REVIEWED:
        reviewScoreColCellStyle = this.reviewScoreColRedCellStyle; // red
        break;

      case DATA_REVIEW_SCORE.RATING_PRELIM_CAL:
        reviewScoreColCellStyle = this.reviewScoreColYellowCellStyle;// yellow
        break;

      case DATA_REVIEW_SCORE.RATING_CALIBRATED:
        reviewScoreColCellStyle = this.reviewScoreColBlueCellStyle;// blue
        break;
      case DATA_REVIEW_SCORE.RATING_COMPLETED:
      case DATA_REVIEW_SCORE.RATING_CHECKED:
        reviewScoreColCellStyle = this.reviewScoreColGreenCellStyle;// green
        break;
      default:
        reviewScoreColCellStyle = this.reviewScoreColWhiteCellStyle;// white
    }

    return reviewScoreColCellStyle;
  }

  /**
   * @param workbook
   * @param cdrResult
   */
  private void createReviewInfoWorkSheet(final Workbook workbook) {
    final CellStyle styleToUse = this.cellStyle;
    ReviewResultExcelColumn.getInstance();
    final String[] reviewInfoSheetHeader = ReviewResultExcelColumn.reviewInfoSheetHeader;

    final Sheet reviewInfoSheet = workbook.getSheetAt(1);

    createHeaderSheet(styleToUse, reviewInfoSheetHeader, reviewInfoSheet);

    createReviewInfoSheet(workbook, styleToUse, reviewInfoSheetHeader, reviewInfoSheet);
  }

  private ReviewResultBO getResultBO() {
    return this.cdrResult.getResultBo();
  }

  /**
   * @param workbook
   * @param cdrResult
   * @param styleToUse
   * @param reviewInfoSheetHeader
   * @param reviewInfoSheet
   */
  private void createReviewInfoSheet(final Workbook workbook, final CellStyle styleToUse,
      final String[] reviewInfoSheetHeader, final Sheet reviewInfoSheet) {

    for (int headerRow = 0; headerRow < (reviewInfoSheetHeader.length - 1); headerRow++) {

      switch (reviewInfoSheetHeader[headerRow]) {

        case ExcelClientConstants.PROJECT:
          createCol(workbook, getResultBO().getPidcVersion().getName(), reviewInfoSheet.getRow(headerRow), 1, false);
          break;

        case ExcelClientConstants.REVIEW_VARIANT:
          caseRvwVar(workbook, reviewInfoSheet, headerRow);
          break;

        case ExcelClientConstants.A2LFILE:
          createCol(workbook, getResultBO().getA2lFileName(), reviewInfoSheet.getRow(headerRow), 1, false);
          break;

        case ExcelClientConstants.CALIBRATION_ENGINEER:
          ExcelCommon.getInstance().createCell(reviewInfoSheet.getRow(headerRow),
              getResultBO().getCalibrationEngineer() != null ? getResultBO().getCalibrationEngineer().getName()
                  : ApicConstants.EMPTY_STRING,
              1, styleToUse);
          break;

        case ExcelClientConstants.REVIEW_CREATOR:// ICDM-1746
          String createdUserName = getResultBO().getCreatedUser().getDescription();
          createCol(workbook, createdUserName, reviewInfoSheet.getRow(headerRow), 1, false);
          break;

        case ExcelClientConstants.AUDITOR:
          createCol(workbook,
              getResultBO().getAuditor() != null ? getResultBO().getAuditor().getName() : ApicConstants.EMPTY_STRING,
              reviewInfoSheet.getRow(headerRow), 1, false);
          break;

        case ExcelClientConstants.WORKPACKAGE:
          createCol(workbook, getResultBO().getWorkPackageName(), reviewInfoSheet.getRow(headerRow), 1, false);
          break;

        case ExcelClientConstants.RVW_CREATION_DATE:
          // ICDM-2345
          // Review result export - Creation date
          createCol(workbook, getResultBO().getCDRResult().getCreatedDate(), reviewInfoSheet.getRow(headerRow), 1,
              false);
          break;

        case ExcelClientConstants.OTH_PARTICIPANTS:
          caseOtherParticipants(workbook, styleToUse, reviewInfoSheet, headerRow);
          break;

        case ExcelClientConstants.INTERNAL_FILES:
          caseInternalFiles(workbook, styleToUse, reviewInfoSheet, headerRow);
          break;

        // Icdm-738 Show lab fun files in CDR result
        case ExcelClientConstants.INPUT_FILES:
          caseInpFiles(workbook, styleToUse, reviewInfoSheet, headerRow);
          break;

        // ICDM 617
        case ExcelClientConstants.PARENT_REVIEW:
          createCol(workbook, getResultBO().getBaseReviewInfo(), reviewInfoSheet.getRow(headerRow), 1, false);
          break;

        // ICDM-876
        case ExcelClientConstants.REVIEW_TYPE:
          createCol(workbook, getResultBO().getReviewTypeStr(), reviewInfoSheet.getRow(headerRow), 1, false);
          break;

        // ICDM 668
        case ExcelClientConstants.REVIEW_STATUS:
          createCol(workbook, getResultBO().getStatusUIType(), reviewInfoSheet.getRow(headerRow), 1, false);
          break;

        case ExcelClientConstants.REVIEW_DESCRIPTION:
          createCol(workbook, getResultBO().getCDRResult().getDescription(), reviewInfoSheet.getRow(headerRow), 1,
              false);
          break;

        default:
          break;
      }
    }

    fillReviewInfoSheetData(workbook, styleToUse, reviewInfoSheetHeader, reviewInfoSheet);
  }

  /**
   * @param workbook
   * @param styleToUse
   * @param reviewInfoSheetHeader
   * @param reviewInfoSheet
   */
  private void fillReviewInfoSheetData(final Workbook workbook, final CellStyle styleToUse,
      final String[] reviewInfoSheetHeader, final Sheet reviewInfoSheet) {
    int rowCount = RVWINF_SHEET_FILE_RVWED_COL_NO;
    int startRowNum = rowCount;
    reviewInfoSheet.setRowSumsBelow(false);

    // files reviewed
    rowCount = createFilesRow(workbook, styleToUse, reviewInfoSheet, rowCount, getResultBO().getInputFiles(),
        reviewInfoSheetHeader[RVWINF_SHEET_FILE_RVWED_COL_NO]);
    groupRows(reviewInfoSheet, rowCount, startRowNum);

    // files attached
    startRowNum = rowCount;
    rowCount = createFilesRow(workbook, styleToUse, reviewInfoSheet, rowCount, getResultBO().getAdditionalFiles(),
        reviewInfoSheetHeader[RVWINF_SHEET_FILE_ATCHD_COL_NO]);
    groupRows(reviewInfoSheet, rowCount, startRowNum);

    // functions reviewed
    startRowNum = rowCount;

    rowCount = createFuncReviewedRow(workbook, styleToUse, reviewInfoSheetHeader[RVWINF_SHEET_FUN_RVWED_COL_NO],
        reviewInfoSheet, rowCount);
    groupRows(reviewInfoSheet, rowCount, startRowNum);

    startRowNum = rowCount;
    rowCount = createAttrValRow(workbook, styleToUse, reviewInfoSheetHeader[ATTR_VAL_IDX], reviewInfoSheet, rowCount);
    groupRows(reviewInfoSheet, rowCount, startRowNum);

    createExportOptionsRow(workbook, styleToUse, reviewInfoSheetHeader[EXPORT_VAL_IDX], reviewInfoSheet, rowCount);

    reviewInfoSheet.autoSizeColumn(1);
  }


  /**
   * @param workbook
   * @param cdrResult
   * @param styleToUse
   * @param reviewInfoSheet
   * @param headerRow
   */
  private void caseInpFiles(final Workbook workbook, final CellStyle styleToUse, final Sheet reviewInfoSheet,
      final int headerRow) {
    final SortedSet<RvwFile> inpFiles = getResultBO().getLabFunFiles();
    final StringBuilder inputFiles = new StringBuilder();
    for (RvwFile file : inpFiles) {
      inputFiles.append(file.getName());
      inputFiles.append("\n");
    }
    final Cell excelCell =
        ExcelCommon.getInstance().createCell(reviewInfoSheet.getRow(headerRow), inputFiles.toString(), 1, styleToUse);
    setCellBorder(workbook, excelCell);
  }

  private void createExportOptionsRow(final Workbook workbook, final CellStyle styleToUse,
      final String reviewInfoShtHeader, final Sheet reviewInfoSheet, final int rowCount2) {

    int rowCount = rowCount2;
    final Cell headerCell =
        ExcelCommon.getInstance().createCell(reviewInfoSheet.getRow(rowCount), reviewInfoShtHeader, 0, styleToUse);
    headerCell.getCellStyle().setVerticalAlignment(CellStyle.VERTICAL_TOP);
    final StringBuilder exportFilterOptions = new StringBuilder(STR_BUILDER_SIZE);

    exportFilterOptions.append("Row Filter : ");

    if (CommonUtils.isNotEmpty(this.rvwResultExportInput.getSelectedWpList())) {
      exportFilterOptions.append("By work package");
      exportFilterOptions.append("\n");
    }
    else if (this.rvwResultExportInput.isOnlyFiltered()) {
      exportFilterOptions.append("Filtered rows");
      exportFilterOptions.append("\n");
    }
    else {
      exportFilterOptions.append("All rows");
      exportFilterOptions.append("\n");
    }

    exportFilterOptions.append("Column Filter : ");

    if (this.rvwResultExportInput.isExportOnlyVisibleCols()) {
      exportFilterOptions.append("Visible columns");
      exportFilterOptions.append("\n");
    }
    else {
      exportFilterOptions.append("All columns");
      exportFilterOptions.append("\n");
    }

    Cell cell = ExcelCommon.getInstance().createCell(reviewInfoSheet.getRow(rowCount2), exportFilterOptions.toString(),
        1, styleToUse);

    setCellBorder(workbook, cell);

  }


  /**
   * @param workbook
   * @param cdrResult
   * @param styleToUse
   * @param reviewInfoSheet
   * @param headerRow
   */
  private void caseInternalFiles(final Workbook workbook, final CellStyle styleToUse, final Sheet reviewInfoSheet,
      final int headerRow) {
    final SortedSet<RvwFile> internFiles = new TreeSet<>();
    if (CommonUtils.isNotEmpty(getResultBO().getRuleFile())) { // ICDM-844
      for (RvwFile icdmFile : getResultBO().getRuleFile()) {
        internFiles.add(icdmFile);
      }
    }
    internFiles.addAll(getResultBO().getOutputFiles());
    final StringBuilder files = new StringBuilder();
    for (RvwFile file : internFiles) {
      files.append(file.getName());
      files.append("\n");

    }
    Cell cell;
    if (files.length() == 0) {
      cell = ExcelCommon.getInstance().createCell(reviewInfoSheet.getRow(headerRow), "", 1, styleToUse);

    }
    else {
      cell = ExcelCommon.getInstance().createCell(reviewInfoSheet.getRow(headerRow),
          files.substring(0, files.length() - 1), 1, styleToUse);
    }
    setCellBorder(workbook, cell);
  }


  /**
   * @param workbook
   * @param cdrResult
   * @param styleToUse
   * @param reviewInfoSheet
   * @param headerRow
   */
  private void caseOtherParticipants(final Workbook workbook, final CellStyle styleToUse, final Sheet reviewInfoSheet,
      final int headerRow) {
    if (getResultBO().getOtherParticipants().isEmpty()) {
      createCol(workbook, "", reviewInfoSheet.getRow(headerRow), 1, false);
    }
    else {
      final StringBuilder builder = new StringBuilder();

      for (RvwParticipant user : getResultBO().getOtherParticipants()) {
        builder.append(user.getName());
        builder.append("\n");
      }

      final Cell cell = ExcelCommon.getInstance().createCell(reviewInfoSheet.getRow(headerRow),
          builder.substring(0, builder.length() - 1), 1, styleToUse);
      setCellBorder(workbook, cell);

    }
  }


  /**
   * @param workbook
   * @param cdrResult
   * @param reviewInfoSheet
   * @param headerRow
   */
  private void caseRvwVar(final Workbook workbook, final Sheet reviewInfoSheet, final int headerRow) {
    if (getResultBO().getVariant() == null) {
      createCol(workbook, "", reviewInfoSheet.getRow(headerRow), 1, false);
    }
    else {
      createCol(workbook, getResultBO().getVariant().getName(), reviewInfoSheet.getRow(headerRow), 1, false);
    }
  }


  /**
   * @param workbook
   * @param cdrResult
   * @param styleToUse
   * @param string
   * @param reviewInfoSheet
   * @param rowCount
   * @return
   */
  private int createAttrValRow(final Workbook workbook, final CellStyle styleToUse, final String reviewInfoShtHeader,
      final Sheet reviewInfoSheet, final int rowCount2) {
    int rowCount = rowCount2;
    final Cell headerCell =
        ExcelCommon.getInstance().createCell(reviewInfoSheet.getRow(rowCount), reviewInfoShtHeader, 0, styleToUse);
    headerCell.getCellStyle().setVerticalAlignment(CellStyle.VERTICAL_TOP);
    final StringBuilder attrValStr = new StringBuilder(STR_BUILDER_SIZE);
    int attrValCnt = 0;
    for (RvwAttrValue rvwAttrVal : getResultBO().getRvwAttrValSet()) {
      attrValStr.append("Attribute : " + rvwAttrVal.getName() + " ,Value :" + rvwAttrVal.getValueDisplay());
      attrValStr.append("\n");
      attrValCnt++;

      // 20 functions are added per cell.
      if (attrValCnt == CELL_LIMIT) {
        rowCount = createAddRows(workbook, styleToUse, reviewInfoSheet, rowCount, attrValStr);
        attrValStr.setLength(0);
        attrValCnt = 0;
      }

    }
    // remaining functions left are added in one cell
    // if no attribute values present , one empty row is created
    if ((attrValCnt != 0) || CommonUtils.isNullOrEmpty(getResultBO().getRvwAttrValSet())) {
      rowCount = createAddRows(workbook, styleToUse, reviewInfoSheet, rowCount, attrValStr);
    }
    return rowCount;
  }


  /**
   * @param reviewInfoSheet review info sheet
   * @param rowCount final row count
   * @param startRowNum intial row num
   */
  private void groupRows(final Sheet reviewInfoSheet, final int rowCount, final int startRowNum) {
    if (rowCount != (startRowNum + 1)) {
      reviewInfoSheet.groupRow(startRowNum + 1, rowCount - 1);
      reviewInfoSheet.setRowGroupCollapsed(startRowNum + 1, true);
    }
  }


  /**
   * ICDM-922
   *
   * @param workbook current workbook
   * @param cdrResult result
   * @param styleToUse cell style
   * @param reviewInfoSheetHeader sheet header
   * @param reviewInfoSheet current sheet
   * @param rowCount2
   * @param reviewInfoSheetHeader
   */
  private int createFilesRow(final Workbook workbook, final CellStyle styleToUse, final Sheet reviewInfoSheet,
      final int rowCount2, final SortedSet<RvwFile> filesSet, final String reviewInfoSheetHeader) {
    int rowCount = rowCount2;
    final Cell headerCell =
        ExcelCommon.getInstance().createCell(reviewInfoSheet.getRow(rowCount), reviewInfoSheetHeader, 0, styleToUse);
    headerCell.getCellStyle().setVerticalAlignment(CellStyle.VERTICAL_TOP);
    int fileCount = 0;
    final StringBuilder files = new StringBuilder();
    for (RvwFile file : filesSet) {
      files.append(file.getName());
      files.append("\n");
      fileCount++;
      // 20 functions are added per cell.
      if (fileCount == CELL_LIMIT) {
        rowCount = createAddRows(workbook, styleToUse, reviewInfoSheet, rowCount, files);
        files.setLength(0);
        fileCount = 0;
      }
    }
    // remaining functions left are added in one cell
    // if no files reviewed , one empty row is created
    if ((fileCount != 0) || filesSet.isEmpty()) {
      rowCount = createAddRows(workbook, styleToUse, reviewInfoSheet, rowCount, files);
    }
    return rowCount;
  }


  /**
   * ICDM-922
   *
   * @param workbook current workbook
   * @param cdrResult result
   * @param styleToUse cell style
   * @param reviewInfoShtHeader sheet header
   * @param reviewInfoSheet current sheet
   * @param rowCount2
   */
  private int createFuncReviewedRow(final Workbook workbook, final CellStyle styleToUse,
      final String reviewInfoShtHeader, final Sheet reviewInfoSheet, final int rowCount2) {
    int rowCount = rowCount2;
    final Cell headerCell =
        ExcelCommon.getInstance().createCell(reviewInfoSheet.getRow(rowCount), reviewInfoShtHeader, 0, styleToUse);
    headerCell.getCellStyle().setVerticalAlignment(CellStyle.VERTICAL_TOP);
    final StringBuilder functions = new StringBuilder();
    int funCount = 0;
    for (CDRResultFunction function : this.cdrResult.getFunctions()) {

      if (!this.exportedFuncIdSet.contains(function.getId())) {
        continue;
      }

      // ICDM-1333
      functions.append(this.cdrResult.getFunctionNameWithVersion(function));
      functions.append("\n");
      funCount++;
      // 20 functions are added per cell.
      if (funCount == CELL_LIMIT) {
        rowCount = createAddRows(workbook, styleToUse, reviewInfoSheet, rowCount, functions);
        functions.setLength(0);
        funCount = 0;
      }
    }
    // remaining functions left are added in one cell
    if (funCount != 0) {
      rowCount = createAddRows(workbook, styleToUse, reviewInfoSheet, rowCount, functions);
    }

    return rowCount;
  }


  /**
   * @param workbook current workbook
   * @param styleToUse cell style
   * @param reviewInfoSheet review sheet
   * @param rowCount row count
   * @param columnContentStr StringBuilder containing the column content
   * @return last row number
   */
  private int createAddRows(final Workbook workbook, final CellStyle styleToUse, final Sheet reviewInfoSheet,
      final int rowCount, final StringBuilder columnContentStr) {
    int rowNum = rowCount;
    String cellValue =
        "".equals(columnContentStr.toString()) ? "" : columnContentStr.substring(0, columnContentStr.length() - 1);
    final Cell cell = ExcelCommon.getInstance().createCell(reviewInfoSheet.getRow(rowNum), cellValue, 1, styleToUse);
    rowNum++;
    ExcelCommon.getInstance().createExcelRow(reviewInfoSheet, rowNum);
    setCellBorder(workbook, cell);
    return rowNum;
  }


  /**
   * @param styleToUse
   * @param reviewInfoSheetHeader
   * @param reviewInfoSheet
   */
  private void createHeaderSheet(final CellStyle styleToUse, final String[] reviewInfoSheetHeader,
      final Sheet reviewInfoSheet) {
    for (int headerRow = 0; headerRow < (reviewInfoSheetHeader.length); headerRow++) {
      final Row excelRow = ExcelCommon.getInstance().createExcelRow(reviewInfoSheet, headerRow);
      final Cell cell = ExcelCommon.getInstance().createCell(excelRow, reviewInfoSheetHeader[headerRow], 0, styleToUse);
      cell.getCellStyle().setVerticalAlignment(CellStyle.VERTICAL_TOP);
    }
    reviewInfoSheet.autoSizeColumn(0);
  }

  /**
   * @param workbook
   * @param cell
   */
  private void setCellBorder(final Workbook workbook, final Cell cell) {
    final CellStyle style = workbook.createCellStyle();
    style.setWrapText(true);
    style.setBorderBottom(CellStyle.BORDER_THIN);
    style.setBorderTop(CellStyle.BORDER_THIN);
    style.setBorderRight(CellStyle.BORDER_THIN);
    style.setBorderLeft(CellStyle.BORDER_THIN);

    cell.setCellStyle(style);
  }


  /**
   * @param workbook
   * @param exportOnlyVisibleCols
   * @param hiddenColNames
   * @param cdrResult
   */
  private void createResultWorkSheet(final Workbook workbook, final boolean exportOnlyVisibleCols,
      final List<String> hiddenColNames, final boolean onlyFiltered, final Set<Long> filteredParamIds) {

    ReviewResultExcelColumn.getInstance();
    List<String> resultSheetHeader;
    resultSheetHeader = new ArrayList<>(Arrays.asList(ReviewResultExcelColumn.resultSheetHeader));

    removeHiddenColumns(exportOnlyVisibleCols, hiddenColNames, resultSheetHeader);
    final Sheet reviewResultSheet = workbook.getSheetAt(0);

    int columnSize = resultSheetHeader.size();
    int rowCount = 0;
    final Row headingRow = ExcelCommon.getInstance().createExcelRow(reviewResultSheet, rowCount);
    CellStyle createHeadingCellStyle = ExcelCommon.getInstance().createHeadingCellStyle(workbook);
    // creating the heading for questionnaire name and status
    ExcelCommon.getInstance().createCell(headingRow, "Review Results", 0, createHeadingCellStyle);
    // creating a merged cell for heading
    reviewResultSheet.addMergedRegion(new CellRangeAddress(0, 0, 0, columnSize - 1));
    // incrementing the rownum after creating heading
    rowCount++;

    final Row headerRow = ExcelCommon.getInstance().createExcelRow(reviewResultSheet, rowCount);
    for (int headerCol = 0; headerCol < (resultSheetHeader.size()); headerCol++) {
      ExcelCommon.getInstance().createHeaderCell(headerRow, resultSheetHeader.get(headerCol), headerCol,
          this.headerCellStyle, this.font);
    }

    // column names for secondary result
    Set<String> secResColumnSet = new HashSet<>();
    SortedSet<RvwResultsSecondary> secondaryResultSet = this.cdrResult.getSecondaryResultsSorted();

    for (RvwResultsSecondary cDRSecondaryResult : secondaryResultSet) {
      if (RULE_SOURCE.RULE_SET.getDbVal().equals(cDRSecondaryResult.getSource())) {
        secResColumnSet.add(cDRSecondaryResult.getRuleSetName());
      }
      else {
        secResColumnSet.add(RULE_SOURCE.getSource(cDRSecondaryResult.getSource()).getUiVal());
      }
    }
    int secResColumn = resultSheetHeader.size();
    for (String resultSetName : secResColumnSet) {
      ExcelCommon.getInstance().createHeaderCell(headerRow, resultSetName, secResColumn, this.headerCellStyle,
          this.font);
      reviewResultSheet.autoSizeColumn(secResColumn);
      secResColumn++;
    }

    final SortedSet<CDRResultParameter> parameterSet =
        new TreeSet<>((p1, p2) -> ReviewResultExport.this.cdrResult.compareTo(p1, p2, SortColumns.SORT_FUNC_NAME));
    parameterSet.addAll(getResultBO().getParameters());

    if (onlyFiltered) {
      removeFilteredOutParams(parameterSet, filteredParamIds);
    }

    for (CDRResultParameter parameter : parameterSet) {
      if (isResultParamRelevant(parameter)) {
        rowCount++;
        final Row row = ExcelCommon.getInstance().createExcelRow(reviewResultSheet, rowCount);
        createCols(workbook, resultSheetHeader, parameter, row);
        this.exportedFuncIdSet.add(parameter.getRvwFunId());
      }
    }
    CDMLogger.getInstance().debug("Parameter Work sheet created. Total params = {}", parameterSet.size());

    // ICDM-894
    for (int col = 0; col < resultSheetHeader.size(); col++) {
      reviewResultSheet.autoSizeColumn(col);
    }

    // Disable autoFilter when there are no rows
    if (rowCount > 0) {

      // column filter for secondary result column
      if (!secResColumnSet.isEmpty()) {

        CellRangeAddress cellRangeAddress =
            new CellRangeAddress(0, rowCount, 0, (resultSheetHeader.size() + secondaryResultSet.size()) - 1);
        reviewResultSheet.setAutoFilter(cellRangeAddress);
      }
      else {

        ExcelCommon.getInstance().setAutoFilter(reviewResultSheet,
            ReviewResultExcelColumn.getInstance().REVIEW_RESULT_AUTOFILTERRANGE, rowCount);
      }
    }
    reviewResultSheet.createFreezePane(RVW_RSLT_FREEZE_PANE_ARG_NO_1, 1);
  }

  /**
   * @param exportOnlyVisibleCols
   * @param hiddenColNames
   * @param resultSheetHeader
   */
  private void removeHiddenColumns(final boolean exportOnlyVisibleCols, final List<String> hiddenColNames,
      final List<String> resultSheetHeader) {
    // When export needs to be done for only visible columns, hidden columns needs to be removed from header
    if (exportOnlyVisibleCols) {
      removeHiddenCols(resultSheetHeader, hiddenColNames);
    }
  }

  private boolean isResultParamRelevant(final CDRResultParameter parameter) {
    if (CommonUtils.isNotNull(this.rvwResultExportInput.getWorkPackage())) {
      return this.rvwResultExportInput.getWorkPackage().getName().equals(this.cdrResult.getWpName(parameter));
    }
    else if (CommonUtils.isNotEmpty(this.rvwResultExportInput.getSelectedWpList())) {
      for (A2lWorkPackage wp : this.rvwResultExportInput.getSelectedWpList()) {
        if (wp.getName().equals(this.cdrResult.getWpName(parameter))) {
          return true;
        }
      }
    }
    else {
      return true;
    }
    return false;
  }

  /**
   * Method to check if there are questionnaire specific to the selected workpackage from review result export dialog
   *
   * @param qnaireA2lWorkPackage as input
   */
  private boolean isQuestionnaireRelevant(final A2lWorkPackage qnaireA2lWorkPackage) {
    // check done for single workpackage
    // workpackage will be available when create seperate worksheet option is used
    if (CommonUtils.isNotNull(this.rvwResultExportInput.getWorkPackage())) {
      return this.rvwResultExportInput.getWorkPackage().getName().equals(qnaireA2lWorkPackage.getName());
    }
    // if there is a single/multiple workpackage selection in review export dialog
    else if (CommonUtils.isNotEmpty(this.rvwResultExportInput.getSelectedWpList())) {
      for (A2lWorkPackage wp : this.rvwResultExportInput.getSelectedWpList()) {
        if (wp.getName().equals(qnaireA2lWorkPackage.getName())) {
          return true;
        }
      }
    }
    else {
      return true;
    }
    return false;
  }

  /**
   * @param resultSheetHeader
   * @param hiddenColNames
   */
  private void removeHiddenCols(final List<String> resultSheetHeader, final List<String> hiddenColNames) {
    if (hiddenColNames != null) {
      resultSheetHeader.removeAll(hiddenColNames);
    }
  }

  /**
   * @param parameterSet
   * @param filteredParamIds
   */
  private void removeFilteredOutParams(final SortedSet<CDRResultParameter> parameterSet,
      final Set<Long> filteredParamIds) {

    Set<CDRResultParameter> filteredOutParams = new HashSet<>();

    for (CDRResultParameter rvwParam : parameterSet) {
      if (!filteredParamIds.contains(rvwParam.getId())) {
        filteredOutParams.add(rvwParam);
      }
    }

    // remove filtered out review result params
    parameterSet.removeAll(filteredOutParams);
  }

  /**
   * @param workbook
   * @param resultSheetHeader
   * @param parameter
   * @param row
   */
  private void createCols(final Workbook workbook, final List<String> resultSheetHeader,
      final CDRResultParameter parameter, final Row row) {

    int col;
    for (col = 0; col < resultSheetHeader.size(); col++) {


      switch (resultSheetHeader.get(col)) {
        // ICDM-2444
        case ExcelClientConstants.COMPLIANCE_NAME:
          caseCompliName(workbook, parameter, row, col);
          break;

        case ExcelClientConstants.FC_NAME:
          createCol(workbook, parameter.getFuncName(), row, col, false);
          break;

        case ExcelClientConstants.PARAMETER:
          createCol(workbook, parameter.getName(), row, col, false);
          break;

        case ExcelClientConstants.LONG_NAME:
          createCol(workbook, this.cdrResult.getFunctionParameter(parameter).getLongName(), row, col, false);
          break;

        // 496338 - Add WP, Resp columns in NAT table in Review Result Editor
        case ExcelClientConstants.WP_NAME:
          createCol(workbook, this.cdrResult.getWpName(parameter), row, col, false);
          break;

        case ExcelClientConstants.RESPONSIBILITY_STR:
          createCol(workbook, this.cdrResult.getRespName(parameter), row, col, false);
          break;

        case ExcelClientConstants.RESPONSIBILITY_TYPE:
          createCol(workbook, CommonUtils.checkNull(this.cdrResult.getRespType(parameter)), row, col, false);
          break;

        case ExcelClientConstants.TYPE:
          createCol(workbook, CommonUtils.checkNull(this.cdrResult.getFunctionParameter(parameter).getType()), row, col,
              false);
          break;


        case ExcelClientConstants.CLASS:
          createCol(workbook, this.cdrResult.getParameterClassStr(parameter), row, col, false);
          break;

        case ExcelClientConstants.IS_CODE_WORD:
          createCol(workbook, this.cdrResult.getParamIsCodeWordDisplay(parameter), row, col, false);
          break;

        // ICDM-1928
        case ExcelClientConstants.IS_BITWISE:
          caseIsBitwise(workbook, parameter, row, col);
          break;

        case ExcelClientConstants.LOWER_LIMIT:
          caseLowerLimit(workbook, parameter, row, col);
          break;

        case ExcelClientConstants.UPPER_LIMIT:
          caseUpperLimit(workbook, parameter, row, col);
          break;

        // ICDM-1928
        case ExcelClientConstants.BITWISE_LIMIT:
          caseBitwiseLimit(workbook, parameter, row, col);
          break;
        case ExcelClientConstants.READY_FOR_SERIES:
          caseReadyForSeries(workbook, parameter, row, col);
          break;
        case ExcelClientConstants.REFERENCE_VALUE:
          setDataForReferenceCol(workbook, parameter, row, col);
          break;
        case ExcelClientConstants.REF_VAL_MATURITY_LEVEL:
          createCol(workbook, this.cdrResult.getMaturityLevel(parameter), row, col, false);
          break;
        case ExcelClientConstants.EXACT_MATCH:
          caseExactMatch(workbook, parameter, row, col);
          break;
        case ExcelClientConstants.CHECKED_VALUE:
          createCol(workbook, this.cdrResult.getCheckedValueString(parameter), row, col,
              this.cdrResult.isCheckedValueChanged(parameter));
          break;
        case ExcelClientConstants.PARENT_CHECKED_VALUE:
          createCol(workbook, this.cdrResult.getParentCheckedValue(parameter), row, col, false);
          break;

        case ExcelClientConstants.PARENT_REFERENCE_VALUE:
          createCol(workbook, this.cdrResult.getParentRefValue(parameter), row, col, false);
          break;

        case ExcelClientConstants.REFERENCE_VALUE_UNIT:
          createCol(workbook, CommonUtils.checkNull(parameter.getRefUnit()), row, col, false);
          break;

        case ExcelClientConstants.CHECKED_VALUE_UNIT:
          createCol(workbook, CommonUtils.checkNull(parameter.getCheckUnit()), row, col, false);
          break;

        case ExcelClientConstants.RESULT:
          createCol(workbook, this.cdrResult.getResult(parameter), row, col, this.cdrResult.isResultChanged(parameter));
          break;

        // Task 241544
        case ExcelClientConstants.SECONDARY_RESULT:
          createCol(workbook, this.cdrResult.getCustomSecondaryResult(parameter), row, col,
              this.cdrResult.isResultChanged(parameter));
          break;

        case ExcelClientConstants.SCORE:
          DATA_REVIEW_SCORE reviewScore = this.cdrResult.getScore(parameter);
          ExcelCommon.getInstance().createCol(this.cdrResult.getScoreUIType(parameter), row, col, reviewScore,
              this.cellStyle, getReviewScoreColCellStyle(reviewScore));
          break;

        case ExcelClientConstants.SCORE_DESCRIPTION:
          createCol(workbook, this.cdrResult.getScoreDescription(parameter), row, col, false);
          break;

        case ExcelClientConstants.COMMENT:
          createCol(workbook, CommonUtils.checkNull(parameter.getRvwComment()), row, col, false);
          break;

        case ExcelClientConstants.PARAMETER_HINT:
          createCol(workbook, CommonUtils.checkNull(this.cdrResult.getParameterHint(parameter)), row, col, false);
          break;

        case ExcelClientConstants.CDFX_STATUS:
          createCol(workbook, this.cdrResult.getHistoryState(parameter), row, col, false);
          break;
        case ExcelClientConstants.CDFX_USER:
          createCol(workbook, this.cdrResult.getHistoryUser(parameter), row, col, false);
          break;
        case ExcelClientConstants.CDFX_DATE:
          createCol(workbook, this.cdrResult.getHistoryDate(parameter), row, col, false);
          break;
        case ExcelClientConstants.CDFX_WORK_PACKAGE:
          createCol(workbook, this.cdrResult.getHistoryContext(parameter), row, col, false);
          break;
        case ExcelClientConstants.CDFX_PROJECT:
          createCol(workbook, this.cdrResult.getHistoryProject(parameter), row, col, false);
          break;
        case ExcelClientConstants.CDFX_TARGET_VARIANT:
          createCol(workbook, this.cdrResult.getHistoryTargetVariant(parameter), row, col, false);
          break;
        case ExcelClientConstants.CDFX_TEST_OBJECT:
          createCol(workbook, this.cdrResult.getHistoryTestObject(parameter), row, col, false);
          break;
        case ExcelClientConstants.CDFX_PROGRAM_IDENTIFIER:
          createCol(workbook, this.cdrResult.getHistoryProgramIdentifier(parameter), row, col, false);
          break;
        case ExcelClientConstants.CDFX_DATA_IDENTIFIER:
          createCol(workbook, this.cdrResult.getHistoryDataIdentifier(parameter), row, col, false);
          break;
        case ExcelClientConstants.CDFX_REMARK:
          createCol(workbook, this.cdrResult.getHistoryRemark(parameter), row, col, false);
          break;
        case ExcelClientConstants.IMPORTED_VALUE:
          createCol(workbook, this.cdrResult.getParamCalDataPhy(parameter).toString(), row, col, false);
          break;
        default:
          break;
      }
    }
    // columns for secondary results
    createColForSecondaryResult(workbook, parameter, row, col);
  }


  /**
   * @param workbook
   * @param parameter
   * @param row
   * @param col
   */
  private void caseExactMatch(final Workbook workbook, final CDRResultParameter parameter, final Row row,
      final int col) {
    if (parameter.getMatchRefFlag() == null) {
      createCol(workbook, "", row, col, false);
    }
    else {
      createCol(workbook, this.cdrResult.getExactMatchUiStr(parameter), row, col,
          this.cdrResult.isExactMatchFlagChanged(parameter));
    }
  }

  /**
   * @param workbook
   * @param parameter
   * @param row
   * @param col
   */
  private void setDataForReferenceCol(final Workbook workbook, final CDRResultParameter parameter, final Row row,
      final int col) {
    StringBuilder hint = new StringBuilder();
    String paramHint = parameter.getParamHint();
    String ruleHint = parameter.getHint();
    // Empty string
    hint.append(" ");
    if (!CommonUtils.isEmptyString(paramHint)) {
      hint.append(paramHint).append("\n\n");
    }
    if (!CommonUtils.isEmptyString(ruleHint)) {
      hint.append(ruleHint);
    }

    String hintStr = "";
    // big hint size >900
    if (hint.length() > BIG_COMMENT_LENGTH) {
      hintStr = hint.substring(1, BIG_COMMENT_LENGTH);
    }
    else {
      hintStr = hint.toString();
    }

    // Use the hint str as comments
    createReferenceCol(workbook, this.cdrResult.getRefValueString(parameter), hintStr, row, col,
        this.cdrResult.isRefValChanged(parameter));
  }


  /**
   * @param workbook
   * @param parameter
   * @param row
   * @param col
   */
  private void createColForSecondaryResult(final Workbook workbook, final CDRResultParameter parameter, final Row row,
      int col) {
    Map<Long, RvwParametersSecondary> secondaryResParams = this.cdrResult.getSecondaryResParams(parameter);
    if (secondaryResParams != null) {
      Collection<RvwParametersSecondary> values = secondaryResParams.values();
      SortedSet<RvwParametersSecondary> secondaryParamSet = new TreeSet<>();

      secondaryParamSet.addAll(values);

      for (RvwParametersSecondary cDRResParamSecondary : secondaryParamSet) {
        createCol(workbook, this.cdrResult.getSecondaryCommonResult(cDRResParamSecondary), row, col, false);
        col++;
      }
    }
  }

  /**
   * @param workbook
   * @param parameter
   * @param row
   * @param col
   */
  private void caseBitwiseLimit(final Workbook workbook, final CDRResultParameter parameter, final Row row,
      final int col) {
    if (parameter.getBitwiseLimit() == null) {
      createCol(workbook, "", row, col, false);
    }
    else {
      createCol(workbook, parameter.getBitwiseLimit(), row, col, this.cdrResult.isBitwiseLimitChanged(parameter));
    }
  }

  /**
   * @param workbook
   * @param parameter
   * @param row
   * @param col
   */
  private void caseReadyForSeries(final Workbook workbook, final CDRResultParameter parameter, final Row row,
      final int col) {
    if (parameter.getRvwMethod() == null) {
      createCol(workbook, "", row, col, false);
    }
    else {
      createCol(workbook, this.cdrResult.getReadyForSeriesStr(parameter), row, col,
          this.cdrResult.isReadyForSeriesFlagChanged(parameter));
    }
  }

  /**
   * @param workbook
   * @param parameter
   * @param row
   * @param col
   */
  private void caseUpperLimit(final Workbook workbook, final CDRResultParameter parameter, final Row row,
      final int col) {
    if (parameter.getUpperLimit() == null) {
      createCol(workbook, "", row, col, false);
    }
    else {
      createCol(workbook, parameter.getUpperLimit().toString(), row, col,
          this.cdrResult.isUpperLimitChanged(parameter));
    }
  }


  /**
   * @param workbook
   * @param parameter
   * @param row
   * @param col
   */
  private void caseLowerLimit(final Workbook workbook, final CDRResultParameter parameter, final Row row,
      final int col) {
    if (parameter.getLowerLimit() == null) {
      createCol(workbook, "", row, col, false);
    }
    else {
      createCol(workbook, parameter.getLowerLimit().toString(), row, col,
          this.cdrResult.isLowerLimitChanged(parameter));
    }
  }


  /**
   * @param workbook
   * @param parameter
   * @param row
   * @param col
   */
  private void caseIsBitwise(final Workbook workbook, final CDRResultParameter parameter, final Row row,
      final int col) {
    if (null == parameter.getIsbitwise()) {
      createCol(workbook, "", row, col, false);
    }
    else {
      String isBitwise = this.cdrResult.getParamIsBitWiseDisplay(parameter);
      createCol(workbook, isBitwise, row, col, this.cdrResult.isBitwiseFlagChanged(parameter));
    }
  }

  /**
   * @param workbook
   * @param parameter
   * @param row
   * @param col
   */
  private void caseCompliName(final Workbook workbook, final CDRResultParameter parameter, final Row row,
      final int col) {
    String complianceStr = getSSDLabelString(parameter);
    createCol(workbook, complianceStr, row, col, false);
  }


  /**
   * @param cdrResParam CDRResultParameter
   * @return String
   */
  public String getSSDLabelString(final CDRResultParameter cdrResParam) {
    StringBuilder ssdLblStrBuilder = new StringBuilder();
    if (this.cdrResult.isComplianceParameter(cdrResParam)) {
      ssdLblStrBuilder.append("Compliance,");
    }
    if (this.cdrResult.isReadOnly(cdrResParam)) {
      ssdLblStrBuilder.append("Read Only,");
    }
    if (this.cdrResult.isBlackList(cdrResParam)) {
      ssdLblStrBuilder.append("Black List,");
    }
    if (this.cdrResult.isQssdParameter(cdrResParam)) {
      ssdLblStrBuilder.append("Q-SSD,");
    }
    if (this.cdrResult.isDependentParam(cdrResParam)) {
      ssdLblStrBuilder.append("Dependent,");
    }
    // delete "," if it is the last character
    return ssdLblStrBuilder.toString().isEmpty() ? ""
        : ssdLblStrBuilder.toString().substring(0, ssdLblStrBuilder.length() - 1);
  }

  /**
   * @param workbook
   * @param refValue
   * @param row
   * @param col
   * @param refValChanged
   */
  private void createReferenceCol(final Workbook workbook, final String refValue, final String cellComment,
      final Row row, final int col, final boolean refValChanged) {

    final CellStyle styleToUse = this.cellStyle;
    Cell cell = ExcelCommon.getInstance().createCell(row, CommonUtils.checkNull(refValue), col, styleToUse);

    if (cellComment != null) {
      ExcelCommon.getInstance().createCellComment(cellComment, cell, RVW_RES_EXPORT_COMMENT_HEIGHT,
          RVW_RES_EXPORT_COMMENT_WIDTH);
    }
    // Icdm-945 Color Changes for the Ref Value Marker
    if (refValChanged) {
      final CellStyle style = workbook.createCellStyle();
      style.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
      style.setFillPattern(CellStyle.SOLID_FOREGROUND);
      cell.setCellStyle(style);
    }

  }


  /**
   * @param workbook
   * @param valueName
   * @param reviewResultSheet
   * @param row
   * @param col
   * @param bgColor yellow background for changed values needed or not
   */
  private void createCol(final Workbook workbook, final String valueName, final Row row, final int col,
      final boolean bgColor) {

    Cell cell = ExcelCommon.getInstance().createCell(row, CommonUtils.checkNull(valueName), col, this.cellStyle);

    // ICDM-617
    if (bgColor) {
      final CellStyle style = workbook.createCellStyle();
      style.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
      style.setFillPattern(CellStyle.SOLID_FOREGROUND);
      cell.setCellStyle(style);
    }
  }


  /**
   * @param workbook mainworkbook
   * @param cdrResult cdrresult
   */
  private void createSheet(final Workbook workbook) {
    // create review result Sheet
    workbook.createSheet("Review Results");
    // create review info sheet
    workbook.createSheet("Review Information");

  }
}
