/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.report.excel;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.eclipse.core.runtime.IProgressMonitor;

import com.bosch.caltool.excel.ExcelFactory;
import com.bosch.caltool.excel.ExcelFile;
import com.bosch.caltool.icdm.client.bo.a2l.A2LFileInfoBO;
import com.bosch.caltool.icdm.client.bo.a2l.A2LParameter;
import com.bosch.caltool.icdm.client.bo.a2l.A2LParameter.SortColumns;
import com.bosch.caltool.icdm.client.bo.cdr.CdrReportDataHandler;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.cdr.CDRConstants;
import com.bosch.caltool.icdm.model.cdr.CDRConstants.WP_RESP_STATUS_TYPE;
import com.bosch.caltool.icdm.model.cdr.CdrReport;
import com.bosch.caltool.icdm.model.cdr.DATA_REVIEW_SCORE;
import com.bosch.caltool.icdm.model.cdr.ReviewDetails;
import com.bosch.caltool.icdm.report.Activator;
import com.bosch.caltool.icdm.report.common.ExcelCommon;

// ICDM-1703
/**
 * @author bru2cob
 */
public class CdrReportDataExport {

  /**
   * Latest function version column index
   */
  private static final int LATEST_FUNC_VERS_COL = 11;
  /**
   * Count of columns to be frozen
   */
  private static final int FREEZABLE_COL = 11;
  /**
   * Second review
   */
  private static final int SECOND_REVIEW = 2;
  /**
   * second level progress
   */
  private static final int PROGRESS_2 = 40;
  /**
   * first level progress
   */
  private static final int PROGRESS_1 = 30;
  /**
   * Column width
   */
  private static final int COL_WIDTH = 4000;


  private static final int CDR_RPT_EXPORT_COMMENT_HEIGHT = 5;

  private static final int CDR_REPORT_EXPORT_COMMENT_WIDTH = 4;
  /**
   * The progress monitor instance to which progess information is to be set.
   */
  private final IProgressMonitor monitor;
  /**
   * Cellstyle for header row
   */
  private CellStyle headerCellStyle;

  /**
   * Normal cell style
   */
  private CellStyle normalCellStyle;

  /**
   * Font instance
   */
  private Font font;

  private boolean isStandardExcelExport;

  /**
   * @param monitor progress monitor
   */
  public CdrReportDataExport(final IProgressMonitor monitor) {
    this.monitor = monitor;
  }

  /**
   * Method creates the excel sheet and its content
   *
   * @param cdrData cdrData
   * @param filePath filePath
   * @param fileExtn fileExtn
   * @param isStandardExcelExport as boolean value
   */
  public boolean exportCdrReportData(final CdrReportDataHandler cdrData, final String filePath, final String fileExtn,
      final boolean isStandardExcelExport) {

    this.isStandardExcelExport = isStandardExcelExport;

    final ExcelFactory exFactory = ExcelFactory.getFactory(fileExtn);
    final ExcelFile xlFile = exFactory.getExcelFile();
    final Workbook workbook = xlFile.createWorkbook();

    // create the first sheet
    workbook.createSheet("Data Review Report");

    // ICDM-2330
    // create the review statistics sheets for WP, BC and FC
    workbook.createSheet("Review Statistics - WP");
    workbook.createSheet("Review Statistics - BC");
    workbook.createSheet("Review Statistics - FC");
    workbook.createSheet("Questionnaire Statistics");

    this.headerCellStyle = ExcelCommon.getInstance().createHeaderCellStyle(workbook);
    this.normalCellStyle = ExcelCommon.getInstance().createCellStyle(workbook);

    // create Review Score column Cell Styles
    ExcelCommon.getInstance().createReviewScoreCellStyle(workbook);

    this.font = ExcelCommon.getInstance().createFont(workbook);

    String filePathLwr = filePath.toLowerCase(java.util.Locale.getDefault());
    String fileFullPath =
        filePathLwr.endsWith(".xlsx") || filePathLwr.endsWith(".xls") ? filePath : filePath + "." + fileExtn;

    try (OutputStream fileOut = new FileOutputStream(fileFullPath)) {
      // set monitor progress-1
      this.monitor.worked(PROGRESS_1);
      this.monitor.subTask("Exporting parameters' review results . . .");

      final SortedSet<A2LParameter> a2lParamSortedSet =
          new TreeSet<>((p1, p2) -> p1.compareTo(p2, SortColumns.SORT_CHAR_NAME));

      // 'Generate Data Review Report' for A2l parameters or A2l Wp/Resp parameters
      A2LFileInfoBO a2lFileInfoBO = cdrData.getA2lEditorDataProvider().getA2lFileInfoBO();
      a2lParamSortedSet.addAll(cdrData.getCdrReport().isToGenDataRvwRprtForWPResp()
          ? a2lFileInfoBO.getA2lParamMapForWPResp(cdrData.getCdrReport()).values()
          : a2lFileInfoBO.getA2lParamMap(null).values());

      if (!this.isStandardExcelExport && !isDataAvailableForExport(a2lParamSortedSet, cdrData)) {
        CDMLogger.getInstance().errorDialog("Excel export is not possible because no parameter has score 8 or 9",
            Activator.PLUGIN_ID);
        return false;
      }

      // set monitor progress-2
      this.monitor.worked(PROGRESS_2);
      CDMLogger.getInstance().debug("CDR Report work sheet creation in export started");


      createCDRDatatWorkSheet(workbook, cdrData, a2lParamSortedSet);

      // ICDM-2330
      // Create Review Statistics Report
      CdrReportStatisticsExcelExport reviewStatisticsReport = new CdrReportStatisticsExcelExport(cdrData);
      reviewStatisticsReport.createRvwStatisticsReport(workbook, this.headerCellStyle, this.normalCellStyle);

      CDMLogger.getInstance().debug("CDR Report work sheet creation in export completed");


      workbook.write(fileOut);
      fileOut.flush();
    }
    catch (IOException ioe) {
      CDMLogger.getInstance().error(ioe.getMessage(), ioe, Activator.PLUGIN_ID);
      return false;
    }
    return true;
  }


  /**
   * Create data work sheet
   *
   * @param workbook
   * @param cdrData
   * @param a2lParamSortedSet
   */
  private void createCDRDatatWorkSheet(final Workbook workbook, final CdrReportDataHandler cdrData,
      final SortedSet<A2LParameter> a2lParamSortedSet) {
    final String[] cdrReportSheetHeader = CDRReportExcelColumn.cdrReportSheetHeader;
    final Sheet cdrReportSheet = workbook.getSheetAt(0);
    CellStyle localHeaderCellStyle = this.headerCellStyle;

    CellStyle blueCellStyle = ExcelCommon.getInstance().createBlueCellStyle(workbook);

    final Row preVersionsRow = ExcelCommon.getInstance().createExcelRow(cdrReportSheet, 0);

    String considerPrePidcVersions = "";
    if (cdrData.getCdrReport().getConsiderReviewsOfPrevPidcVers()) {
      considerPrePidcVersions = "CONSIDERED REVIEWS OF PREVIOUS PIDC VERSIONS";
    }
    else {
      considerPrePidcVersions = "NOT CONSIDERED REVIEWS OF PREVIOUS PIDC VERSIONS";
    }


    ExcelCommon.getInstance().createCell(preVersionsRow, considerPrePidcVersions, 0, blueCellStyle);


    final Row headerRow = ExcelCommon.getInstance().createExcelRow(cdrReportSheet, 2);
    // ICDM-2584 (Parent Task ICDM-2412)
    int headerLength = cdrReportSheetHeader.length;

    // Reason for header length minus 1 in the for loop is, to skip the Report data column name in the sheet header
    for (int headerCol = 0; headerCol < (headerLength - 1); headerCol++) {
      ExcelCommon.getInstance().createHeaderCell(headerRow, cdrReportSheetHeader[headerCol], headerCol,
          localHeaderCellStyle, this.font);
    }
    CdrReport cdrReport = cdrData.getCdrReport();
    int totalSize = (headerLength - 1) + cdrReport.getMaxParamReviewCount();
    String headerName = "Latest Review";
    int count = SECOND_REVIEW;
    // create the header row cells
    for (int headerCol = headerLength - 1; headerCol < totalSize; headerCol++ ,headerName = "Review " + count++) {
      ExcelCommon.getInstance().createHeaderCell(headerRow, headerName, headerCol, localHeaderCellStyle, this.font);
    }

    int rowCount = setExcelInput(workbook, cdrData, cdrReportSheetHeader, cdrReportSheet, a2lParamSortedSet);
    for (int col = 0; col < totalSize; col++) {
      cdrReportSheet.setColumnWidth(col, COL_WIDTH);
    }
    // Disable autoFilter when there are no rows
    if (rowCount > 0) {
      ExcelCommon.getInstance().setAutoFilter(cdrReportSheet,
          "A3:" + ExcelCommon.getInstance().getExcelColName(totalSize - 1), rowCount);
    }
    // freeze the parameter related cols
    cdrReportSheet.createFreezePane(FREEZABLE_COL, 1);
    // Latest func version column is hidden
    cdrReportSheet.setColumnHidden(LATEST_FUNC_VERS_COL, true);

  }

  /**
   * Set the input for the created data sheet
   *
   * @param workbook
   * @param cdrData
   * @param cdrReportSheetHeader
   * @param cdrReportSheet
   * @param cdrReportData
   * @return
   */
  private int setExcelInput(final Workbook workbook, final CdrReportDataHandler cdrData,
      final String[] cdrReportSheetHeader, final Sheet cdrReportSheet, final SortedSet<A2LParameter> cdrReportData) {
    int rowCount = 2;
    for (A2LParameter parameter : cdrReportData) {
      DATA_REVIEW_SCORE reviewScore = cdrData.getReviewScore(parameter.getName(), 0);
      // Check if it's not a standard Excel export and either the review score is not null (indicating it's reviewed) or
      // it's a standard Excel export, and also if the review score is checked.
      if ((!this.isStandardExcelExport && ((reviewScore != null) && reviewScore.isChecked())) ||
          this.isStandardExcelExport) {
        rowCount++;
        final Row row = ExcelCommon.getInstance().createExcelRow(cdrReportSheet, rowCount);
        createSheetData(workbook, cdrData, cdrReportSheetHeader, parameter, row);
      }
    }
    return rowCount;
  }


  private boolean isDataAvailableForExport(final SortedSet<A2LParameter> cdrReportData,
      final CdrReportDataHandler cdrData) {
    for (A2LParameter parameter : cdrReportData) {
      DATA_REVIEW_SCORE reviewScore = cdrData.getReviewScore(parameter.getName(), 0);
      // Check if it's not a standard Excel export and either the review score is not null (indicating it's reviewed) or
      // it's a standard Excel export, and also if the review score is reviewed but not checked.
      if ((!this.isStandardExcelExport && ((reviewScore != null) && reviewScore.isChecked())) ||
          this.isStandardExcelExport) {
        // Return true if any parameter has a review score 8 or 9
        return true;
      }
    }
    // Return false if all parameters have review scores is not 8 or 9
    return false;
  }


  /**
   * @param workbook
   * @param cdrData
   * @param cdrReportSheetHeader
   * @param parameter
   * @param row
   */
  private void createSheetData(final Workbook workbook, final CdrReportDataHandler cdrData,
      final String[] cdrReportSheetHeader, final A2LParameter parameter, final Row row) {
    for (int col = 0; col < cdrReportSheetHeader.length; col++) {

      switch (cdrReportSheetHeader[col]) {
        // ICDM-2444
        case ExcelClientConstants.COMPLIANCE_NAME:
          caseCompliName(workbook, parameter, row, col);
          break;

        case ExcelClientConstants.PARAMETER:
          createCol(workbook, parameter.getName(), row, col, false);
          break;

        case ExcelClientConstants.FUNCTION:
          // ICDM-1901
          createCol(workbook, parameter.getDefFunction() == null ? "" : parameter.getDefFunction().getName(), row, col,
              false);
          break;

        case ExcelClientConstants.FC_VERSION:
          // ICDM-1901
          createCol(workbook, parameter.getDefFunction() == null ? "" : parameter.getDefFunction().getFunctionVersion(),
              row, col, false);
          break;

        // 230083 RESP renamed to WP
        case ExcelClientConstants.WP:
          createCol(workbook, cdrData.getWpName(parameter.getParamId()), row, col, false);
          break;
        case ExcelClientConstants.RESP_TYPE:
          createCol(workbook, cdrData.getRespType(parameter.getParamId()), row, col, false);
          break;
        // 230083, new column added
        case ExcelClientConstants.RESPONSIBILITY_STR:
          createCol(workbook, cdrData.getRespName(parameter.getParamId()), row, col, false);
          break;
        case ExcelClientConstants.PTYPE:
          createCol(workbook, parameter.getType(), row, col, false);
          break;

        case ExcelClientConstants.IS_CODE_WORD:
          createCol(workbook, parameter.getCodeWordString(), row, col, false);
          break;

        case ExcelClientConstants.LATEST_A2L_VER:
          createCol(workbook, cdrData.getLatestA2LVersion(parameter.getName()), row, col, false);
          break;

        case ExcelClientConstants.LATEST_FUNC_VER:
          createCol(workbook, cdrData.getLatestFunctionVersion(parameter.getName()), row, col, false);
          break;
        case ExcelClientConstants.QUESTIONNAIRE_STATUS:
          String qnaireStatus = getQnaireStatus(cdrData, parameter);
          createCol(workbook, qnaireStatus, row, col, false);
          break;


        case ExcelClientConstants.WP_FINISHED:
          String status = cdrData.getWpFinishedRespStatus(parameter.getParamId());
          String wpFinishedStatus = ApicConstants.EMPTY_STRING;

          WP_RESP_STATUS_TYPE wpRespStatusType = CDRConstants.WP_RESP_STATUS_TYPE.getTypeByDbCode(status);
          if (CommonUtils.isNotNull(wpRespStatusType)) {
            wpFinishedStatus = wpRespStatusType.getUiType();
          }

          createCol(workbook, wpFinishedStatus, row, col, false);
          break;

        case ExcelClientConstants.IS_REVIEWED:
          createCol(workbook, cdrData.isReviewedStr(parameter.getName()), row, col, false);
          break;

        case ExcelClientConstants.REVIEW_COMMENTS:
          createCol(workbook, cdrData.getLatestReviewComment(parameter.getName()), row, col, false);
          break;
        // ICDM-2584 (Parent Task ICDM-2412)
        case ExcelClientConstants.REVIEW_TYPE:
          caseRvwType(workbook, cdrData, parameter, row, col);
          break;

        case ExcelClientConstants.REVIEW_DATA:
          setReviewData(cdrData, parameter, row, col);
          break;

        case ExcelClientConstants.REVIEW_RESULT_DESCRIPTION:
          createCol(workbook, cdrData.getReviewResultName(parameter.getName()), row, col, false);
          break;

        default:
          createCol(workbook, "", row, col, false);
      }
    }
  }


  private String getQnaireStatus(final CdrReportDataHandler cdrData, final A2LParameter parameter) {
    String qnaireRespVersStatus = cdrData.getQnaireRespVersStatus(parameter.getName(), false);
    String qnaireStatus = ApicConstants.EMPTY_STRING;
    if (CommonUtils.isEqual(CDRConstants.RVW_QNAIRE_STATUS_N_A, qnaireRespVersStatus)) {
      qnaireStatus = CDRConstants.RVW_QNAIRE_STATUS_N_A_TOOLTIP;
    }
    else if (CommonUtils.isEqual(ApicConstants.EMPTY_STRING, qnaireRespVersStatus) ||
        CommonUtils.isEqual(CDRConstants.NOT_BASELINED_QNAIRE_RESP, qnaireRespVersStatus)) {
      qnaireStatus = "No questionnaire baseline available";
    }
    else if (CommonUtils.isEqual(CDRConstants.NO_QNAIRE_STATUS, qnaireRespVersStatus)) {
      qnaireStatus = qnaireRespVersStatus;
    }
    else if (CommonUtils.isNotNull(CDRConstants.QS_STATUS_TYPE.getTypeByDbCode(qnaireRespVersStatus))) {
      qnaireStatus = CDRConstants.QS_STATUS_TYPE.getTypeByDbCode(qnaireRespVersStatus).getUiType();
    }
    return qnaireStatus;
  }

  /**
   * @param workbook
   * @param cdrData
   * @param parameter
   * @param row
   * @param col
   */
  private void caseRvwType(final Workbook workbook, final CdrReportDataHandler cdrData, final A2LParameter parameter,
      final Row row, final int col) {
    String rvwType = "";
    ReviewDetails reviewDetails = cdrData.getReviewDetailsLatest(parameter.getName());
    if (null != reviewDetails) {
      rvwType = CDRConstants.REVIEW_TYPE.getType(reviewDetails.getReviewType()).getUiType();
    }
    createCol(workbook, rvwType, row, col, false);
  }

  /**
   * @param workbook
   * @param parameter
   * @param row
   * @param col
   */
  private void caseCompliName(final Workbook workbook, final A2LParameter parameter, final Row row, final int col) {
    String ssdClassString = "";
    ssdClassString = getSSDClassString(parameter);
    Cell cell = createCol(workbook, ssdClassString, row, col, false);
    setToolTip(parameter, cell);
  }

  /**
   * @param parameter
   * @param cell
   */
  private void setToolTip(final A2LParameter parameter, final Cell cell) {
    if (parameter.getCharacteristic().isDependentCharacteristic() &&
        (null != parameter.getCharacteristic().getDependentCharacteristic()) &&
        CommonUtils.isNotNull(parameter.getCharacteristic().getDependentCharacteristic().getCharacteristicName()) &&
        (parameter.getCharacteristic().getDependentCharacteristic().getCharacteristicName().length > 0)) {

      ExcelCommon.getInstance().createCellComment(
          Arrays.stream(parameter.getCharacteristic().getDependentCharacteristic().getCharacteristicName())
              .collect(Collectors.joining("\n")),
          cell, CDR_RPT_EXPORT_COMMENT_HEIGHT, CDR_REPORT_EXPORT_COMMENT_WIDTH);
    }
  }

  /**
   * @param a2lParam A2LParameter
   * @return String
   */
  private String getSSDClassString(final A2LParameter a2lParam) {
    StringBuilder ssdLblStrBuilder = new StringBuilder();
    if (a2lParam.isComplianceParam()) {
      ssdLblStrBuilder.append("Compliance,");
    }
    if (a2lParam.getCharacteristic().isReadOnly()) {
      ssdLblStrBuilder.append("Read Only,");
    }
    if (a2lParam.getCharacteristic().isDependentCharacteristic()) {
      ssdLblStrBuilder.append("Dependant Characteristic,");
    }
    if (a2lParam.isBlackList()) {
      ssdLblStrBuilder.append("Black List,");
    }
    if (a2lParam.isQssdParameter()) {
      ssdLblStrBuilder.append("Q-SSD,");
    }
    // delete "," if it is the last character
    return ssdLblStrBuilder.toString().isEmpty() ? ""
        : ssdLblStrBuilder.toString().substring(0, ssdLblStrBuilder.length() - 1);
  }

  /**
   * Set the review data
   *
   * @param cdrData
   * @param parameter
   * @param row
   * @param col
   */
  private void setReviewData(final CdrReportDataHandler cdrData, final A2LParameter parameter, final Row row,
      final int col) {
    int colIndex = col;
    for (int reviewCol = 0; reviewCol < cdrData.getCdrReport().getMaxParamReviewCount(); reviewCol++) {
      DATA_REVIEW_SCORE reviewScore = cdrData.getReviewScore(parameter.getName(), reviewCol);
      String displayText = "";
      if (CommonUtils.isNotNull(reviewScore)) {
        displayText = setReviewDataDispTxt(cdrData, parameter, reviewCol, reviewScore);
      }
      ExcelCommon.getInstance().createCol(displayText, row, colIndex, reviewScore, this.normalCellStyle);
      colIndex++;
    }
  }

  /**
   * @param cdrData
   * @param parameter
   * @param reviewCol
   * @param reviewScore
   * @return
   */
  private String setReviewDataDispTxt(final CdrReportDataHandler cdrData, final A2LParameter parameter,
      final int reviewCol, final DATA_REVIEW_SCORE reviewScore) {
    String displayText;
    if ((reviewCol == 0)) {
      if (this.isStandardExcelExport) {
        displayText = reviewScore.getScoreDisplay();
      }
      else {
        if (ApicConstants.REVIEWED.equals(cdrData.isReviewedStr(parameter.getName()))) {
          displayText = reviewScore.getHundredPercScoreDisplay();
        }
        else {
          displayText = reviewScore.getScoreDisplay();
        }
      }
    }
    else {
      displayText = reviewScore.getScoreDisplay();
    }
    return displayText;
  }

  /**
   * Creates column with the specified value
   *
   * @param workbook
   * @param valueName
   * @param reviewResultSheet
   * @param row
   * @param col
   * @param bgColor yellow background for changed values needed or not
   */
  private Cell createCol(final Workbook workbook, final String valueName, final Row row, final int col,
      final boolean bgColor) {
    final CellStyle styleToUse = this.normalCellStyle;
    Cell cell;
    // value to be set is null , an empty cell is created
    if (valueName == null) {
      cell = ExcelCommon.getInstance().createCell(row, "", col, styleToUse);
    }
    else {
      cell = ExcelCommon.getInstance().createCell(row, valueName, col, styleToUse);
    }
    if (bgColor) {
      final CellStyle style = workbook.createCellStyle();
      style.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
      style.setFillPattern(CellStyle.SOLID_FOREGROUND);
      cell.setCellStyle(style);
    }
    return cell;
  }
}

