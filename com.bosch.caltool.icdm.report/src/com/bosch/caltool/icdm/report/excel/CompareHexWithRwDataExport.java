/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.report.excel;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.SortedSet;
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
import com.bosch.caltool.icdm.client.bo.cdr.CdrReportDataHandler;
import com.bosch.caltool.icdm.client.bo.comphex.CompHexWithCDFxDataHandler;
import com.bosch.caltool.icdm.common.util.CommonUtilConstants;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.cdr.CDRConstants;
import com.bosch.caltool.icdm.model.cdr.CDRConstants.QS_STATUS_TYPE;
import com.bosch.caltool.icdm.model.cdr.CDRConstants.WP_RESP_STATUS_TYPE;
import com.bosch.caltool.icdm.model.cdr.DATA_REVIEW_SCORE;
import com.bosch.caltool.icdm.model.comphex.CompHexWithCDFParam;
import com.bosch.caltool.icdm.report.Activator;
import com.bosch.caltool.icdm.report.common.ExcelCommon;
import com.bosch.rcputils.jobs.AbstractChildJob;

/**
 * The Class CompareHexWithRwDataExport.
 *
 * @author bru2cob
 */
public class CompareHexWithRwDataExport {

  /**
   *
   */
  private static final String SPLIT_BRACKET = "\\[";

  /**
   * The progress monitor instance to which progess information is to be set.
   */
  private final IProgressMonitor monitor;

  /** Cellstyle for header row. */
  private CellStyle headerCellStyle;

  /** Normal cell style. */
  private CellStyle cellStyle;

  /** Font instance. */
  private Font font;

  /** The Constant COMP_EXPORT_WORK_TIME_30. */
  private static final int COMP_EXPORT_WORK_TIME_30 = 30;

  /** The Constant COMP_EXPORT_WORK_TIME_70. */
  private static final int COMP_EXPORT_WORK_TIME_70 = 70;

  /**
   * Column width
   */
  private static final int COL_WIDTH = 4000;

  private static final int COMP_HEX_EXPORT_COMMENT_HEIGHT = 5;

  private static final int COMP_HEX_EXPORT_COMMENT_WIDTH = 4;

  /** CdrReportData. */
  private CdrReportDataHandler reportData;

  private CellStyle style;

  private CellStyle redStyle;

  private CellStyle yellowStyle;

  private CellStyle blueStyle;

  private CellStyle greenStyle;

  private CellStyle whiteStyle;

  private final boolean isStandardExcelExport;

  /**
   * Constructor.
   *
   * @param monitor IProgressMonitor
   * @param isStandardExcelExport as boolean value
   */
  public CompareHexWithRwDataExport(final IProgressMonitor monitor, final boolean isStandardExcelExport) {
    this.monitor = monitor;
    this.isStandardExcelExport = isStandardExcelExport;
  }

  /**
   * Export compare report result.
   *
   * @param compData the comp data
   * @param filePath the file path
   * @param fileExtn the file extn
   * @return
   */
  public boolean exportCompareReportResult(final CompHexWithCDFxDataHandler compData, final String filePath,
      final String fileExtn) {
    this.reportData = compData.getCdrReportData();
    final String fileType = fileExtn;
    final ExcelFactory exFactory = ExcelFactory.getFactory(fileType);
    final ExcelFile xlFile = exFactory.getExcelFile();
    final Workbook workbook = xlFile.createWorkbook();

    workbook.createSheet("Hex Compare Results");
    workbook.createSheet("Hex Compare Results Info");

    this.headerCellStyle = ExcelCommon.getInstance().createHeaderCellStyle(workbook);
    this.cellStyle = ExcelCommon.getInstance().createCellStyle(workbook);


    this.font = ExcelCommon.getInstance().createFont(workbook);

    String fileFullPath;
    if (filePath.contains(".xlsx") || filePath.contains(".xls")) {
      fileFullPath = filePath;
    }
    else {
      fileFullPath = filePath + "." + fileExtn;
    }
    try (FileOutputStream fileOut = new FileOutputStream(fileFullPath)) {
      this.monitor.worked(COMP_EXPORT_WORK_TIME_30);
      this.monitor.subTask("Exporting report data  . . .");

      SortedSet<CompHexWithCDFParam> inputSet = compData.getCompHexResultParamSet();

      // Check if it's possible to export data based on certain conditions
      if (!this.isStandardExcelExport && !isDataAvailableForExport(inputSet, compData)) {
        // Show an error message if export conditions are not met
        CDMLogger.getInstance().errorDialog("Excel export is not possible because no parameter has score 8 or 9",
            Activator.PLUGIN_ID);
        return false;
      }

      if (CDMLogger.getInstance().isDebugEnabled()) {
        CDMLogger.getInstance().debug("Compare report work sheet creation in export started");
      }
      this.style = workbook.createCellStyle();
      createCompareWorkSheet(workbook, compData);

      if (CDMLogger.getInstance().isDebugEnabled()) {
        CDMLogger.getInstance().debug("Compare report work sheet creation in export completed");
      }

      this.monitor.worked(COMP_EXPORT_WORK_TIME_70);
      this.monitor.subTask("Exporting report statistics. . .");

      this.style = workbook.createCellStyle();
      createCompareReportInfoWorkSheet(workbook, compData);

      if (CDMLogger.getInstance().isDebugEnabled()) {
        CDMLogger.getInstance().debug("Compare report info work sheet creation in export completed");
      }
      workbook.setSelectedTab(0);
      workbook.write(fileOut);
      fileOut.flush();

      this.monitor.worked(AbstractChildJob.JOB_ADD_END);
      this.monitor.subTask("Report is complete. . .");
    }
    catch (FileNotFoundException fnfe) {
      CDMLogger.getInstance().error(fnfe.getLocalizedMessage(), fnfe, Activator.PLUGIN_ID);
      return false;
    }
    catch (IOException ioe) {
      CDMLogger.getInstance().error(ioe.getLocalizedMessage(), ioe, Activator.PLUGIN_ID);
      return false;
    }
    return true;
  }

  private boolean isDataAvailableForExport(final SortedSet<CompHexWithCDFParam> inputSet,
      final CompHexWithCDFxDataHandler compData) {
    for (CompHexWithCDFParam compObj : inputSet) {
      DATA_REVIEW_SCORE reviewScore = compData.getCdrReportData().getReviewScore(compObj.getParamName());
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
   * Creates the compare report info work sheet.
   *
   * @param workbook the workbook
   * @param compData the comp data
   */
  private void createCompareReportInfoWorkSheet(final Workbook workbook, final CompHexWithCDFxDataHandler compData) {

    CompHexWithRvDataExcelColumn.getInstance();
    final String[] compareInfoSheetHeader = CompHexWithRvDataExcelColumn.compareInfoSheetHeader;
    final Sheet compareInfoSheet = workbook.getSheetAt(1);

    createHeaderSheet(this.cellStyle, compareInfoSheetHeader, compareInfoSheet);
    createCompareInfoSheet(compData, compareInfoSheetHeader, compareInfoSheet);
  }


  /**
   * Creates the compare info sheet.
   *
   * @param compData the comp data
   * @param compareInfoSheetHeader the compare info sheet header
   * @param compareInfoSheet the compare info sheet
   */
  private void createCompareInfoSheet(final CompHexWithCDFxDataHandler compData, final String[] compareInfoSheetHeader,
      final Sheet compareInfoSheet) {

    for (int headerRow = 0; headerRow < (compareInfoSheetHeader.length); headerRow++) {

      switch (compareInfoSheetHeader[headerRow]) {
        case ExcelClientConstants.HEX_FILE:
          createCol(compData.getHexFileName(), compareInfoSheet.getRow(headerRow), 1, false);
          break;

        case ExcelClientConstants.A2LFILE:
          createCol(compData.getA2lFile().getFilename(), compareInfoSheet.getRow(headerRow), 1, false);
          break;

        case ExcelClientConstants.COMP_VARIANT:
          createCol(compData.getSelctedVar() == null ? "" : compData.getSelctedVar().getName(),
              compareInfoSheet.getRow(headerRow), 1, false);
          break;

        case ExcelClientConstants.TOTAL_PARAMS:
          createCol(String.valueOf(compData.getCompHexResultParamSet().size()), compareInfoSheet.getRow(headerRow), 1,
              false);
          break;


        case ExcelClientConstants.REVIEWED_PARAMS:
          createCol(String.valueOf(compData.getCompHexStatitics().getStatParamReviewed()),
              compareInfoSheet.getRow(headerRow), 1, false);
          break;

        case ExcelClientConstants.EQUAL_HEX_CDFX:
          createCol(String.valueOf(compData.getCompHexStatitics().getStatEqualParCount()),
              compareInfoSheet.getRow(headerRow), 1, false);
          break;

        case ExcelClientConstants.EQUAL_REVIEWED:
          createCol(String.valueOf(compData.getCompHexStatitics().getStatParamRvwdEqual()),
              compareInfoSheet.getRow(headerRow), 1, false);
          break;

        case ExcelClientConstants.COMPLI_PARAMS:
          createCol(String.valueOf(compData.getCompHexStatitics().getStatCompliParamInA2L()),
              compareInfoSheet.getRow(headerRow), 1, false);
          break;

        case ExcelClientConstants.COMPLI_PARAMS_PASSED:
          createCol(String.valueOf(compData.getCompHexStatitics().getStatCompliParamPassed()),
              compareInfoSheet.getRow(headerRow), 1, false);
          break;

        case ExcelClientConstants.PARAMS_FILTERED:
          createCol(String.valueOf(compData.getCompHexStatitics().getStatFilteredParam()),
              compareInfoSheet.getRow(headerRow), 1, false);
          break;

        case ExcelClientConstants.PARAMS_FILTERED_REV:
          createCol(String.valueOf(compData.getCompHexStatitics().getStatFilteredParamRvwd()),
              compareInfoSheet.getRow(headerRow), 1, false);
          break;

        case ExcelClientConstants.NUM_PARAM_BSH_RESP:
          createCol(String.valueOf(compData.getParameterInBoschResp()), compareInfoSheet.getRow(headerRow), 1, false);
          break;

        case ExcelClientConstants.NUM_PARAM_BSH_RESP_RVW:
          createCol(String.valueOf(compData.getParameterInBoschRespRvwed()), compareInfoSheet.getRow(headerRow), 1,
              false);
          break;

        case ExcelClientConstants.PARAM_BSH_RESP_RVW:
          createCol(String.valueOf(compData.getReviewedParameterWithBoschResp() + "%"),
              compareInfoSheet.getRow(headerRow), 1, false);
          break;

        case ExcelClientConstants.PARAM_BSH_RESP_QNAIRE:
          createCol(String.valueOf(compData.getRvwParamWithBoschRespForCompletedQnaire() + "%"),
              compareInfoSheet.getRow(headerRow), 1, false);
          break;

        case ExcelClientConstants.QNAIRE_NEGATIVE_ANSWER:
          createCol(String.valueOf(compData.getQnaireWithNegativeAnswersCount()), compareInfoSheet.getRow(headerRow), 1,
              false);
          break;
        case ExcelClientConstants.CONSIDERED_PREVIOUS_REVIEWS:
          createCol(
              compData.getCdrReportData().getCdrReport().getConsiderReviewsOfPrevPidcVers()
                  ? CommonUtilConstants.DISPLAY_YES : CommonUtilConstants.DISPLAY_NO,
              compareInfoSheet.getRow(headerRow), 1, false);
          break;

        default:
          createCol("", compareInfoSheet.getRow(headerRow), 1, false);
      }
    }
    compareInfoSheet.autoSizeColumn(1);
  }


  /**
   * Creates the header sheet.
   *
   * @param styleToUse the style to use
   * @param compareInfoSheetHeader the compare info sheet header
   * @param compareInfoSheet the compare info sheet
   */
  private void createHeaderSheet(final CellStyle styleToUse, final String[] compareInfoSheetHeader,
      final Sheet compareInfoSheet) {
    for (int headerRow = 0; headerRow < (compareInfoSheetHeader.length); headerRow++) {
      final Row excelRow = ExcelCommon.getInstance().createExcelRow(compareInfoSheet, headerRow);
      final Cell cell =
          ExcelCommon.getInstance().createCell(excelRow, compareInfoSheetHeader[headerRow], 0, styleToUse);
      cell.getCellStyle().setVerticalAlignment(CellStyle.VERTICAL_TOP);
    }
    compareInfoSheet.autoSizeColumn(0);
  }

  /**
   * Creates the compare work sheet.
   *
   * @param workbook the workbook
   * @param compData the comp data
   */
  private void createCompareWorkSheet(final Workbook workbook, final CompHexWithCDFxDataHandler compData) {
    CompHexWithRvDataExcelColumn.getInstance();
    final String[] compareSheetHeader = CompHexWithRvDataExcelColumn.compareSheetHeader;

    final Sheet reviewInfoSheet = workbook.getSheetAt(0);

    final Row headerRow = ExcelCommon.getInstance().createExcelRow(reviewInfoSheet, 0);
    for (int headerCol = 0; headerCol < (compareSheetHeader.length); headerCol++) {
      ExcelCommon.getInstance().createHeaderCell(headerRow, compareSheetHeader[headerCol], headerCol,
          this.headerCellStyle, this.font);
    }

    int rowCount = 0;
    SortedSet<CompHexWithCDFParam> inputSet = compData.getCompHexResultParamSet();
    createColouredStyles(workbook);

    // Iterate through the data set and create rows and columns
    for (CompHexWithCDFParam compObj : inputSet) {
      // Retrieve the review score for the current parameter from the CompHexWithCDFxDataHandler
      DATA_REVIEW_SCORE reviewScore = compData.getCdrReportData().getReviewScore(compObj.getParamName());
      if ((!this.isStandardExcelExport && ((reviewScore != null) && reviewScore.isChecked())) ||
          this.isStandardExcelExport) {
        rowCount++;
        final Row row = ExcelCommon.getInstance().createExcelRow(reviewInfoSheet, rowCount);
        createCols(compareSheetHeader, compObj, row);
      }
    }
    for (int col = 0; col < compareSheetHeader.length; col++) {
      reviewInfoSheet.setColumnWidth(col, COL_WIDTH);
    }
    // Disable autoFilter when there are no rows
    if (rowCount > 0) {
      ExcelCommon.getInstance().setAutoFilter(reviewInfoSheet,
          CompHexWithRvDataExcelColumn.getInstance().HEX_COMPARE_REPORT_AUTOFILTERRANGE, rowCount);
    }

  }

  /**
   * Creates the cols.
   *
   * @param resultSheetHeader the result sheet header
   * @param compObj the comp obj
   * @param row the row
   */
  private void createCols(final String[] resultSheetHeader, final CompHexWithCDFParam compObj, final Row row) {
    for (int col = 0; col < resultSheetHeader.length; col++) {
      generateColumnData(resultSheetHeader[col], compObj, row, col);
    }
  }

  /**
   * Generate column data.
   *
   * @param columnHeader the column header
   * @param compObj the comp obj
   * @param row the row
   * @param col the col
   */
  private void generateColumnData(final String columnHeader, final CompHexWithCDFParam compObj, final Row row,
      final int col) {
    switch (columnHeader) {
      case ExcelClientConstants.COMPLIANCE_NAME:
        String ssdClassString = "";
        ssdClassString = getSSDClassString(compObj);
        Cell cell = createCol(ssdClassString, row, col, false);
        setToolTip(compObj, cell);
        break;

      case ExcelClientConstants.TYPE:
        createCol(compObj.getParamType().getText(), row, col, false);
        break;

      case ExcelClientConstants.PARAMETER:
        createCol(compObj.getParamName(), row, col, false);
        break;

      case ExcelClientConstants.FUNCTION:
        createCol(compObj.getFuncName(), row, col, false);
        break;

      case ExcelClientConstants.FC_VERSION:
        createCol(compObj.getFuncVers(), row, col, false);
        break;

      case ExcelClientConstants.WP:
        createCol(this.reportData.getWpName(compObj.getParamName()), row, col, false);
        break;
      case ExcelClientConstants.RESP_TYPE:
        createCol(this.reportData.getRespType(compObj.getParamName()), row, col, false);
        break;
      case ExcelClientConstants.RESP:
        createCol(this.reportData.getRespName(compObj.getParamName()), row, col, false);
        break;
      case ExcelClientConstants.WP_FINISHED:
        String status = this.reportData.getWpFinishedRespStatuswithName(compObj.getParamName());
        String wpFinishedStatus = ApicConstants.EMPTY_STRING;

        WP_RESP_STATUS_TYPE typeByDbCode = CDRConstants.WP_RESP_STATUS_TYPE.getTypeByDbCode(status);
        if (CommonUtils.isNotNull(typeByDbCode)) {
          wpFinishedStatus = typeByDbCode.getUiType();
        }

        createCol(wpFinishedStatus, row, col, false);
        break;

      case ExcelClientConstants.LATEST_A2L_VER:
        createCol(compObj.getLatestA2lVersion(), row, col, false);
        break;

      case ExcelClientConstants.LATEST_FUNC_VER:
        createCol(compObj.getLatestFunctionVersion(), row, col, getBgColor(compObj));
        break;

      case ExcelClientConstants.QUESTIONNAIRE_STATUS:
        String qnaireStatus = getQnaireStatus(compObj);
        createCol(qnaireStatus, row, col, false);
        break;

      case ExcelClientConstants.IS_REVIEWED:
        createCol(compObj.isReviewed() ? CommonUtilConstants.DISPLAY_YES : CommonUtilConstants.DISPLAY_NO, row, col,
            false);
        break;

      case ExcelClientConstants.EQUAL:
        createCol(compObj.isEqual() ? CommonUtilConstants.DISPLAY_YES : CommonUtilConstants.DISPLAY_NO, row, col,
            false);
        break;

      case ExcelClientConstants.HEX_VALUE:
        String hexData = null;
        hexData = CommonUtils.isNotNull(compObj.getHexCalDataPhySimpleDispValue())
            ? compObj.getHexCalDataPhySimpleDispValue() : "";
        createCol(hexData.trim(), row, col, false);
        break;

      case ExcelClientConstants.REVIEWED_VALUE:
        String rvwData = ApicConstants.EMPTY_STRING;
        if (!compObj.isNeverReviewed() && (CommonUtils.isNotNull(compObj.getCdfxCalDataPhySimpleDispValue()))) {
          rvwData = compObj.getCdfxCalDataPhySimpleDispValue();
        }
        createCol(rvwData.trim(), row, col, false);
        break;

      case ExcelClientConstants.COMPLI_RESULT:
        createCol(compObj.getCompliResult().getUiValue(), row, col, false);
        break;

      case ExcelClientConstants.REVIEW_SCORE:
        String rvwScore = getReviewScore(compObj);
        createColForScore(rvwScore, row, col);
        break;

      case ExcelClientConstants.REVIEW_COMMENTS:
        createCol(compObj.getLatestReviewComments(), row, col, false);
        break;

      case ExcelClientConstants.REVIEW_RESULT_DESCRIPTION:
        createCol(this.reportData.getReviewResultName(compObj.getParamName()), row, col, false);
        break;

      default:
        createCol(ApicConstants.EMPTY_STRING, row, col, false);
    }
  }


  private String getReviewScore(final CompHexWithCDFParam compObj) {
    String rvwScore = "";
    if (this.isStandardExcelExport) {
      rvwScore = compObj.getReviewScore();
    }
    else {
      if (compObj.isReviewed()) {
        rvwScore = compObj.getHundredPecReviewScore();
      }
      else {
        rvwScore = compObj.getReviewScore();
      }
    }
    return rvwScore;
  }

  private String getQnaireStatus(final CompHexWithCDFParam compObj) {
    String qnaireStatus = ApicConstants.EMPTY_STRING;
    String qnaireRespVersStatus = this.reportData.getQnaireRespVersStatus(compObj.getParamName(), false);
    if (CommonUtils.isEqual(ApicConstants.EMPTY_STRING, qnaireRespVersStatus) ||
        CommonUtils.isEqual(CDRConstants.NOT_BASELINED_QNAIRE_RESP, qnaireRespVersStatus)) {
      qnaireStatus = "No questionnaire baseline available";
    }
    else if (CommonUtils.isEqual(CDRConstants.RVW_QNAIRE_STATUS_N_A, qnaireRespVersStatus)) {
      qnaireStatus = CDRConstants.RVW_QNAIRE_STATUS_N_A_TOOLTIP;
    }
    else if (CommonUtils.isEqual(CDRConstants.NO_QNAIRE_STATUS, qnaireRespVersStatus)) {
      qnaireStatus = qnaireRespVersStatus;
    }
    else {
      QS_STATUS_TYPE typeByDbCode = CDRConstants.QS_STATUS_TYPE.getTypeByDbCode(qnaireRespVersStatus);
      if (CommonUtils.isNotNull(typeByDbCode)) {
        qnaireStatus = typeByDbCode.getUiType();
      }
    }
    return qnaireStatus;
  }

  /**
   * @param compObj
   * @param cell
   */
  private void setToolTip(final CompHexWithCDFParam compObj, final Cell cell) {
    if (compObj.isDependantCharacteristic() && (null != compObj.getDepCharsName()) &&
        CommonUtils.isNotNull(compObj.getDepCharsName()) && (compObj.getDepCharsName().length > 0)) {

      ExcelCommon.getInstance().createCellComment(
          Arrays.stream(compObj.getDepCharsName()).collect(Collectors.joining("\n")), cell,
          COMP_HEX_EXPORT_COMMENT_HEIGHT, COMP_HEX_EXPORT_COMMENT_WIDTH);
    }
  }

  /**
   * @param compObj
   * @return
   */
  private String getSSDClassString(final CompHexWithCDFParam compObj) {
    StringBuilder ssdLblStrBuilder = new StringBuilder();
    if (compObj.isCompli()) {
      ssdLblStrBuilder.append("Compliance,");
    }
    if (compObj.isReadOnly()) {
      ssdLblStrBuilder.append("Read Only,");
    }
    if (compObj.isDependantCharacteristic()) {
      ssdLblStrBuilder.append("Dependant Characteristic,");
    }
    if (compObj.isBlackList()) {
      ssdLblStrBuilder.append("Black List,");
    }
    if (compObj.isQssdParameter()) {
      ssdLblStrBuilder.append("Q-SSD,");
    }
    // delete "," if it is the last character
    return ssdLblStrBuilder.toString().isEmpty() ? ""
        : ssdLblStrBuilder.toString().substring(0, ssdLblStrBuilder.length() - 1);
  }

  /**
   * Gets the bg color.
   *
   * @param compObj the comp obj
   * @return the bg color
   */
  private boolean getBgColor(final CompHexWithCDFParam compObj) {
    boolean hasColor = false;
    if (CommonUtils.isNotEqual(compObj.getFuncVers(), compObj.getLatestFunctionVersion())) {
      hasColor = true;
    }
    return hasColor;
  }


  /**
   * Creates the col.
   *
   * @param workbook the workbook
   * @param valueName the value name
   * @param row the row
   * @param col the col
   * @param bgColor yellow background for changed values needed or not
   * @param style
   */
  private void createColForScore(final String valueName, final Row row, final int col) {
    Cell cell = createCell(valueName, row, col);
    if (valueName != null) {
      String[] scoreSplit = valueName.split(SPLIT_BRACKET);
      DATA_REVIEW_SCORE scoreVal = DATA_REVIEW_SCORE.getType(scoreSplit[0].trim());
      if (scoreVal != null) {
        cell.setCellStyle(getReviewScoreStyle(scoreVal));
      }
    }

  }

  /**
   * Creates the col.
   *
   * @param valueName the value name
   * @param row the row
   * @param col the col
   * @param bgColor yellow background for changed values needed or not
   */
  private Cell createCol(final String valueName, final Row row, final int col, final boolean bgColor) {
    Cell cell = createCell(valueName, row, col);
    if (bgColor) {
      short colorIndex = IndexedColors.YELLOW.getIndex();
      cell.setCellStyle(createStyleCell(this.style, colorIndex));
    }
    return cell;
  }

  /**
   * @param reviewScore reviewScore
   * @return the review color for the score
   */
  private CellStyle getReviewScoreStyle(final DATA_REVIEW_SCORE reviewScore) {
    CellStyle outputStyle;

    switch (reviewScore.getRating()) {
      case DATA_REVIEW_SCORE.RATING_NOT_REVIEWED:
        outputStyle = getRedStyle(); // red
        break;
      case DATA_REVIEW_SCORE.RATING_PRELIM_CAL:
        outputStyle = getYellowStyle();// yellow
        break;

      case DATA_REVIEW_SCORE.RATING_CALIBRATED:
        outputStyle = getBlueStyle();// blue
        break;
      case DATA_REVIEW_SCORE.RATING_COMPLETED:
      case DATA_REVIEW_SCORE.RATING_CHECKED:
        outputStyle = getGreenStyle();// green
        break;
      default:
        outputStyle = getWhiteStyle();// white

    }

    return outputStyle;

  }

  /**
   * @return the default white style
   */
  private CellStyle getWhiteStyle() {
    return this.whiteStyle;
  }

  /**
   * @return the green style
   */
  private CellStyle getGreenStyle() {
    return this.greenStyle;
  }

  /**
   * @return the blue style
   */
  private CellStyle getBlueStyle() {
    return this.blueStyle;
  }

  /**
   * @return the yellow style
   */
  private CellStyle getYellowStyle() {
    return this.yellowStyle;
  }

  /**
   * @return the red style
   */
  private CellStyle getRedStyle() {
    return this.redStyle;
  }

  /**
   * @param workbook workbook create the different colured styles at the start
   */
  private void createColouredStyles(final Workbook workbook) {
    this.redStyle = workbook.createCellStyle();
    createStyleCell(this.redStyle, IndexedColors.RED.getIndex());

    this.yellowStyle = workbook.createCellStyle();
    createStyleCell(this.yellowStyle, IndexedColors.YELLOW.getIndex());

    this.blueStyle = workbook.createCellStyle();
    createStyleCell(this.blueStyle, IndexedColors.BLUE.getIndex());

    this.greenStyle = workbook.createCellStyle();
    createStyleCell(this.greenStyle, IndexedColors.GREEN.getIndex());

    this.whiteStyle = workbook.createCellStyle();
    createStyleCell(this.whiteStyle, IndexedColors.WHITE.getIndex());

  }

  /**
   * Creates the style cell.
   *
   * @param style the style
   * @param cell the cell
   * @param colorIndex the color index
   * @return
   */
  private CellStyle createStyleCell(final CellStyle style, final short colorIndex) {

    style.setFillForegroundColor(colorIndex);
    style.setFillPattern(CellStyle.SOLID_FOREGROUND);
    style.setBorderBottom(CellStyle.BORDER_THIN);
    style.setBorderLeft(CellStyle.BORDER_THIN);
    style.setBorderRight(CellStyle.BORDER_THIN);
    style.setBorderTop(CellStyle.BORDER_THIN);
    style.setAlignment(CellStyle.ALIGN_CENTER);
    return style;
  }

  /**
   * Creates the cell.
   *
   * @param valueName the value name
   * @param row the row
   * @param col the col
   * @return the cell
   */
  private Cell createCell(final String valueName, final Row row, final int col) {
    final CellStyle styleToUse = this.cellStyle;
    Cell cell;

    if (valueName == null) {
      cell = ExcelCommon.getInstance().createCell(row, "", col, styleToUse);
    }
    else {
      cell = ExcelCommon.getInstance().createCell(row, valueName, col, styleToUse);
    }
    return cell;
  }

}
