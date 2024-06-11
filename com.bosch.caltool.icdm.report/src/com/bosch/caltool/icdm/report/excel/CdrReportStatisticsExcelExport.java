/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.report.excel;

import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import com.bosch.caltool.icdm.client.bo.a2l.A2LParameter;
import com.bosch.caltool.icdm.client.bo.cdr.CdrReportDataHandler;
import com.bosch.caltool.icdm.client.bo.cdr.CdrReportDataStatistics;
import com.bosch.caltool.icdm.common.bo.a2l.ParamWpResponsibility;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.a2l.A2lResponsibility;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.report.common.ExcelCommon;

/**
 * Excel report for Review statistics
 *
 * @author dja7cob
 */
// ICDM-2330
class CdrReportStatisticsExcelExport {

  /**
   * Percentage -Cell type
   */
  private static final String PERCENTAGE = "Percentage";
  /**
   * Number -Cell type
   */
  private static final String NUMBER = "Number";
  /**
   * Subtotal
   */
  private static final String SUBTOTAL = "Subtotal:";
  /**
   * Excel fomrat for percentage
   */
  private static final String PERCENTAGE_FORMAT = "0%";
  /**
   * Excel format for number , with thousnad separators
   */
  private static final String THOUSAND_SEPARATOR_FORMAT = "#,##0";
  /**
   * ColumnName For Percentage of paramters reviewed out of total parameters
   */
  private static final String PERCENT_OF_PARAMETERS_REVIEWED = "Reviewed[%]";
  /**
   * Questionnaire Statistics sheet number
   */
  private static final int QNAIRE_SHEET = 4;
  /**
   * Functional Component Statistics sheet number
   */
  private static final int FC_SHEET = 3;
  /**
   * Base Component Statistics sheet number
   */
  private static final int BC_SHEET = 2;
  /**
   * Work Package Statistics sheet number
   */
  private static final int WP_SHEET = 1;
  /**
   * This a2l file has unassigned parameters info
   */
  private static final String NOTE_UNASSIGNED_PARAM = "Note : This a2l file has unassigned parameters";
  /**
   * Total number of parameters
   */
  private static final String TOTAL_PARAMS = "Total Parameters";
  /**
   * Number of parameters reviewed
   */
  private static final String PARAMS_REVIEWED = "Parameters Reviewed";

  /**
   * Count of columns to be frozen
   */
  private static final int FREEZABLE_COL = 3;


  private static final String TEXT_SEPERATOR = " --> ";

  private final CdrReportDataHandler cdrData;

  private final CdrReportDataStatistics cdrReportStatistics;

  /**
   * Cellstyle for headings
   */
  private CellStyle tableHeadingCellStyle;

  private Font headingFont;

  /**
   * Cell styles for number , percentge formats
   */
  private CellStyle numCellStyle;

  private CellStyle percentCellStyle;

  private CellStyle subtotalCellStyle;
  /**
   * Bold Font style For subtotal Row
   */

  private Font fontForSubtotalRow;

  /**
   * Unassigned Parameters in a2l file
   */
  private final SortedSet<String> unassigndParamSet = new TreeSet<>();

  /**
   * Sorted Map to store WP statistics - with customised comparator
   */
  private final SortedMap<String, int[]> wpStatistics = new TreeMap<>(ApicUtil::compare);

  /**
   * Sorted Map to store FC statistics - with customised comparator
   */
  private final SortedMap<String, int[]> funcStatistics = new TreeMap<>(ApicUtil::compare);

  /**
   * Sorted Map to store BC statistics - with customised comparator
   */
  private final SortedMap<String, int[]> bcStatistics = new TreeMap<>(ApicUtil::compare);

  private Workbook workbook;

  /**
   * Columns for compare info sheet
   */
  protected static final String[] questionnaireStatisticSheet = new String[] {
      ExcelClientConstants.NUM_PARAM_BSH_RESP,
      ExcelClientConstants.NUM_PARAM_BSH_RESP_RVW,
      ExcelClientConstants.PARAM_BSH_RESP_RVW,
      ExcelClientConstants.PARAM_BSH_RESP_QNAIRE,
      ExcelClientConstants.QNAIRE_NEGATIVE_ANSWER };


  public CdrReportStatisticsExcelExport(final CdrReportDataHandler cdrData) {
    this.cdrData = cdrData;
    this.cdrReportStatistics = new CdrReportDataStatistics(cdrData);
  }

  /**
   * @param workBook Review Excel Report
   * @param cellStyle Cell Style
   * @param headerCellStyle Header Cell Style
   */
  protected void createRvwStatisticsReport(final Workbook workBook, final CellStyle headerCellStyle,
      final CellStyle cellStyle) {
    this.workbook = workBook;
    // Create table heading style without borders
    this.tableHeadingCellStyle = ExcelCommon.getInstance().createHeaderCellStyleWithoutBorder(this.workbook);
    // Create table heading font with font size 12
    this.headingFont = ExcelCommon.getInstance().createFontBig(this.workbook);

    // create cell style of number format - aligned right and separators for thousands
    this.numCellStyle = ExcelCommon.getInstance().createCellStyle(this.workbook);
    this.numCellStyle.setAlignment(CellStyle.ALIGN_RIGHT);
    this.numCellStyle.setDataFormat(this.workbook.createDataFormat().getFormat(THOUSAND_SEPARATOR_FORMAT));

    // create cell style of percentage type and aligned right
    this.percentCellStyle = ExcelCommon.getInstance().createCellStyle(this.workbook);
    this.percentCellStyle.setAlignment(CellStyle.ALIGN_RIGHT);
    this.percentCellStyle.setDataFormat(this.workbook.createDataFormat().getFormat(PERCENTAGE_FORMAT));

    // create cell style for subtotal
    this.subtotalCellStyle = ExcelCommon.getInstance().createHeaderCellStyleWithoutBorder(this.workbook);
    this.subtotalCellStyle.setAlignment(CellStyle.ALIGN_RIGHT);
    // create cell style Bold Font - for Subtotal
    this.fontForSubtotalRow = ExcelCommon.getInstance().createFont(this.workbook);

    // Get Review statistics of a2l file
    CDMLogger.getInstance().debug("Computing review statistics");
    getA2lRvwStatistics();

    // Create a sheet to display WP review statistics
    CDMLogger.getInstance().debug("Creating Review Statistics - WP sheet");
    createWPRvwStatSheet(headerCellStyle, cellStyle);

    // Create a sheet to display BC review statistics
    CDMLogger.getInstance().debug("Creating Review Statistics - BC sheet");
    createBCRvwStatSheet(headerCellStyle, cellStyle);

    // Create a sheet to display FC review statistics
    CDMLogger.getInstance().debug("Creating Review Statistics - FC sheet");
    createFCRvwStatSheet(headerCellStyle, cellStyle);

    CDMLogger.getInstance().debug("Creating Questionnaire Statistics");
    createQniareStatSheet(cellStyle);
  }

  /**
   * @param headerCellStyle
   * @param cellStyle
   */
  private void createQniareStatSheet(final CellStyle cellStyle) {

    final Sheet qnaireStatSheet = this.workbook.getSheetAt(QNAIRE_SHEET);

    final String[] qnaireStatisticSheetHeader = questionnaireStatisticSheet;

    int rowCount = 0;

    final Row qniareHeadingRow = ExcelCommon.getInstance().createExcelRow(qnaireStatSheet, rowCount);

    // Table Heading
    ExcelCommon.getInstance().createHeaderCell(qniareHeadingRow, "Questionnaire Statistics", 0,
        this.tableHeadingCellStyle, this.headingFont);

    int qnaireRowNum = qniareHeadingRow.getRowNum() + 1;

    CdrReportDataStatistics cdrReportDataStatistics = new CdrReportDataStatistics(this.cdrData);

    for (int statRow = 0; statRow < (qnaireStatisticSheetHeader.length); statRow++) {

      switch (qnaireStatisticSheetHeader[statRow]) {
        case ExcelClientConstants.NUM_PARAM_BSH_RESP:
          createQnaireStatRow(cellStyle, qnaireStatSheet, qnaireRowNum, ExcelClientConstants.NUM_PARAM_BSH_RESP,
              String.valueOf(cdrReportDataStatistics.getParameterInBoschResp()));
          break;

        case ExcelClientConstants.NUM_PARAM_BSH_RESP_RVW:
          createQnaireStatRow(cellStyle, qnaireStatSheet, qnaireRowNum, ExcelClientConstants.NUM_PARAM_BSH_RESP_RVW,
              String.valueOf(cdrReportDataStatistics.getParameterInBoschRespRvwed()));
          break;

        case ExcelClientConstants.PARAM_BSH_RESP_RVW:
          createQnaireStatRow(cellStyle, qnaireStatSheet, qnaireRowNum, ExcelClientConstants.PARAM_BSH_RESP_RVW,
              String.valueOf(cdrReportDataStatistics.getReviewedParameterWithBoschResp() + "%"));
          break;

        case ExcelClientConstants.PARAM_BSH_RESP_QNAIRE:
          createQnaireStatRow(cellStyle, qnaireStatSheet, qnaireRowNum, ExcelClientConstants.PARAM_BSH_RESP_QNAIRE,
              String.valueOf(cdrReportDataStatistics.getReviewedParameterWithBoschRespForComplQnaire() + "%"));
          break;

        case ExcelClientConstants.QNAIRE_NEGATIVE_ANSWER:
          createQnaireStatRow(cellStyle, qnaireStatSheet, qnaireRowNum, ExcelClientConstants.QNAIRE_NEGATIVE_ANSWER,
              String.valueOf(cdrReportDataStatistics.getQnaireWithNegativeAnswersCount()));
          break;
        default:
          break;
      }
      qnaireRowNum++;
    }

    // Auto resize columns in Questionnaire Statisics sheet
    autoResizeColumns(qnaireStatSheet, 2);
  }

  /**
   * @param cellStyle
   * @param qnaireStatSheet
   * @param qnaireRowNum
   * @param cdrReportDataStatistics
   */
  private void createQnaireStatRow(final CellStyle cellStyle, final Sheet qnaireStatSheet, final int qnaireRowNum,
      final String qnaireStatCell1, final String qnaireStatCell2) {
    final Row row = ExcelCommon.getInstance().createExcelRow(qnaireStatSheet, qnaireRowNum);
    ExcelCommon.getInstance().createCell(row, qnaireStatCell1, 0, cellStyle);
    ExcelCommon.getInstance().createCell(row, qnaireStatCell2, ApicConstants.COLUMN_INDEX_1, this.numCellStyle);
  }


  /**
   * @param headerCellStyle
   * @param cellStyle
   */
  private void createFCRvwStatSheet(final CellStyle headerCellStyle, final CellStyle cellStyle) {

    // Create sheet for FC Statistics
    final Sheet fcRvwStatSheet = this.workbook.getSheetAt(FC_SHEET);

    int rowCount = 0;

    // Heading for BC parameter statistics
    final Row fcHeadingRow = ExcelCommon.getInstance().createExcelRow(fcRvwStatSheet, rowCount);

    // Table Heading
    ExcelCommon.getInstance().createHeaderCell(fcHeadingRow, "Review Statistics in Functional Components", 0,
        this.tableHeadingCellStyle, this.headingFont);


    // Subtotal row
    final Row subtotalRow = ExcelCommon.getInstance().createExcelRow(fcRvwStatSheet, fcHeadingRow.getRowNum() + 1);

    // Display Subtotal for each statistics column
    ExcelCommon.getInstance().createHeaderCell(subtotalRow, SUBTOTAL, ApicConstants.COLUMN_INDEX_0,
        this.subtotalCellStyle, this.fontForSubtotalRow);


    // Table heading for a2l file parameter statistics
    final Row fcTableHeaderRow = ExcelCommon.getInstance().createExcelRow(fcRvwStatSheet, subtotalRow.getRowNum() + 1);
    String[] fcTableHeaderName =
        { "Functional Component Name", PARAMS_REVIEWED, TOTAL_PARAMS, PERCENT_OF_PARAMETERS_REVIEWED };
    for (int col = 0; col < ApicConstants.COLUMN_INDEX_4; col++) {
      ExcelCommon.getInstance().createCell(fcTableHeaderRow, fcTableHeaderName[col], col, headerCellStyle);
    }

    int fcRowNum = fcTableHeaderRow.getRowNum() + 1;
    // Iterate over the FCs in a2l file
    for (Entry<String, int[]> entry : this.funcStatistics.entrySet()) {
      String funName = entry.getKey();
      int[] stats = entry.getValue();
      final Row row = ExcelCommon.getInstance().createExcelRow(fcRvwStatSheet, fcRowNum);
      ExcelCommon.getInstance().createCell(row, funName, 0, cellStyle);
      createCellForNum(row, stats[0], ApicConstants.COLUMN_INDEX_1, this.numCellStyle);
      createCellForNum(row, stats[1], ApicConstants.COLUMN_INDEX_2, this.numCellStyle);
      createCellForNum(row, stats[2] * 0.01, ApicConstants.COLUMN_INDEX_3, this.percentCellStyle);
      fcRowNum++;
    }

    rowCount = fcRowNum;

    // Subtotal excel Function ,109-Excel function number for Sum -SUBTOTAL(FuncNumber,Range)
    // Used OFFSET func to calculate range dynamically
    // -OFFSET(Reference,Row Offset from reference,Column reference from offset,Height,Width)
    // ($B4 - starting cell for range)
    createHeaderCellOfFormulaType(subtotalRow, "SUBTOTAL(109,OFFSET($B4,0,0," + (rowCount - 3) + ",1))",
        ApicConstants.COLUMN_INDEX_1, NUMBER);

    // Subtotal excel function ($C4 - starting cell for range)
    createHeaderCellOfFormulaType(subtotalRow, "SUBTOTAL(109,OFFSET($C4,0,0," + (rowCount - 3) + ",1))",
        ApicConstants.COLUMN_INDEX_2, NUMBER);

    // Excel formula to divide Values from Parameters reviewed by total parameters (Divide Column B by Column C)
    createHeaderCellOfFormulaType(subtotalRow, "$B2/$C2", ApicConstants.COLUMN_INDEX_3, PERCENTAGE);

    // Auto resize columns in FC Statisics sheet
    autoResizeColumns(fcRvwStatSheet, ApicConstants.COLUMN_INDEX_3);

    // Disable autoFilter when there are no rows
    if (rowCount > 0) {
      ExcelCommon.getInstance().setAutoFilter(fcRvwStatSheet,
          "A3:" + ExcelCommon.getInstance().getExcelColName(ApicConstants.COLUMN_INDEX_3), rowCount);
    }
    // Freeze the parameter related columns
    fcRvwStatSheet.createFreezePane(FREEZABLE_COL, ApicConstants.COLUMN_INDEX_3);
  }

  /**
   * @param headerCellStyle
   * @param cellStyle
   */
  private void createBCRvwStatSheet(final CellStyle headerCellStyle, final CellStyle cellStyle) {

    // Create sheet for BC statistics
    final Sheet bcRvwStatSheet = this.workbook.getSheetAt(BC_SHEET);

    int rowCount = 0;

    // Heading for BC parameter statistics
    final Row bcHeadingRow = ExcelCommon.getInstance().createExcelRow(bcRvwStatSheet, rowCount);

    ExcelCommon.getInstance().createHeaderCell(bcHeadingRow, "Review Statistics in Base Components", 0,
        this.tableHeadingCellStyle, this.headingFont);

    // Subtotal row
    final Row subtotalRow = ExcelCommon.getInstance().createExcelRow(bcRvwStatSheet, bcHeadingRow.getRowNum() + 1);

    // Display Subtotal for each statistics column
    ExcelCommon.getInstance().createHeaderCell(subtotalRow, SUBTOTAL, ApicConstants.COLUMN_INDEX_0,
        this.subtotalCellStyle, this.fontForSubtotalRow);

    // Table heading for a2l file parameter statistics
    final Row bcTableHeaderRow = ExcelCommon.getInstance().createExcelRow(bcRvwStatSheet, subtotalRow.getRowNum() + 1);
    String[] bcTableHeaderName =
        { "Base Component Name", PARAMS_REVIEWED, TOTAL_PARAMS, PERCENT_OF_PARAMETERS_REVIEWED };
    for (int col = 0; col < ApicConstants.COLUMN_INDEX_4; col++) {
      ExcelCommon.getInstance().createCell(bcTableHeaderRow, bcTableHeaderName[col], col, headerCellStyle);
    }

    int bcRowNum = bcTableHeaderRow.getRowNum() + 1;
    // Iterate over the BCs in a2l file
    for (Entry<String, int[]> entry : this.bcStatistics.entrySet()) {
      String bcName = entry.getKey();
      int[] stats = entry.getValue();
      final Row row = ExcelCommon.getInstance().createExcelRow(bcRvwStatSheet, bcRowNum);
      ExcelCommon.getInstance().createCell(row, bcName, 0, cellStyle);
      createCellForNum(row, stats[0], ApicConstants.COLUMN_INDEX_1, this.numCellStyle);
      createCellForNum(row, stats[1], ApicConstants.COLUMN_INDEX_2, this.numCellStyle);
      createCellForNum(row, stats[2] * 0.01, ApicConstants.COLUMN_INDEX_3, this.percentCellStyle);
      bcRowNum++;
    }

    rowCount = bcRowNum;

    // Subtotal excel function ($B4 - starting cell for range)
    createHeaderCellOfFormulaType(subtotalRow, "SUBTOTAL(109,OFFSET($B4,0,0," + (rowCount - 3) + ",1))",
        ApicConstants.COLUMN_INDEX_1, NUMBER);
    // Subtotal excel function ($C4 - starting cell for range)
    createHeaderCellOfFormulaType(subtotalRow, "SUBTOTAL(109,OFFSET($C4,0,0," + (rowCount - 3) + ",1))",
        ApicConstants.COLUMN_INDEX_2, NUMBER);

    // Excel formula to divide Values from Parameters reviewed by total parameters (Divide Column B by Column C)
    createHeaderCellOfFormulaType(subtotalRow, "$B2/$C2", ApicConstants.COLUMN_INDEX_3, PERCENTAGE);
    // Auto resize columns in BC Statisics sheet
    autoResizeColumns(bcRvwStatSheet, ApicConstants.COLUMN_INDEX_3);

    // Disable autoFilter when there are no rows
    if (rowCount > 0) {
      ExcelCommon.getInstance().setAutoFilter(bcRvwStatSheet,
          "A3:" + ExcelCommon.getInstance().getExcelColName(ApicConstants.COLUMN_INDEX_3), rowCount);
    }
    // Freeze the parameter related columns
    bcRvwStatSheet.createFreezePane(FREEZABLE_COL, ApicConstants.COLUMN_INDEX_3);
  }

  /**
   * @param cdrData
   * @param cellStyle
   * @param headerCellStyle
   */
  private void createWPRvwStatSheet(final CellStyle headerCellStyle, final CellStyle cellStyle) {


    // Create sheet for WP statistics
    final Sheet wpRvwStatSheet = this.workbook.getSheetAt(WP_SHEET);

    int rowCount = 0;

    // Heading for a2l file parameter statistics
    final Row a2lHeadingRow = ExcelCommon.getInstance().createExcelRow(wpRvwStatSheet, rowCount);

    // Heading row
    ExcelCommon.getInstance().createHeaderCell(a2lHeadingRow, "Review Statistics in A2l file", 0,
        this.tableHeadingCellStyle, this.headingFont);

    // Table heading for a2l file parameter statistics
    tableHeadingForParamStat(headerCellStyle, wpRvwStatSheet, a2lHeadingRow);

    // // Table data for a2l file parameter statistics
    final Row a2lDataRow = ExcelCommon.getInstance().createExcelRow(wpRvwStatSheet, 2);
    tableDataForParamStat(cellStyle, a2lDataRow);

    // Heading for WP parameter statistics
    final Row wpHeadingRow = ExcelCommon.getInstance().createExcelRow(wpRvwStatSheet, a2lDataRow.getRowNum() + 2);

    // Heading for WP
    ExcelCommon.getInstance().createHeaderCell(wpHeadingRow, "Review Statistics in Work Packages", 0,
        this.tableHeadingCellStyle, this.headingFont);

    // Display Subtotal for each statistics column
    ExcelCommon.getInstance().createHeaderCell(wpHeadingRow, SUBTOTAL, ApicConstants.COLUMN_INDEX_2,
        this.subtotalCellStyle, this.fontForSubtotalRow);

    // Table heading for WP parameter statistics
    final Row wpTableHeaderRow = ExcelCommon.getInstance().createExcelRow(wpRvwStatSheet, wpHeadingRow.getRowNum() + 1);
    String[] wpTableHeaderName = {
        "Workpackage Name",
        ExcelClientConstants.RESP_TYPE,
        ExcelClientConstants.RESPONSIBILITY_STR,
        PARAMS_REVIEWED,
        TOTAL_PARAMS,
        PERCENT_OF_PARAMETERS_REVIEWED };
    for (int col = 0; col < wpTableHeaderName.length; col++) {
      ExcelCommon.getInstance().createCell(wpTableHeaderRow, wpTableHeaderName[col], col, headerCellStyle);
      wpRvwStatSheet.autoSizeColumn(col);
    }
    rowCount = createWorkPackageStatPage(cellStyle, wpRvwStatSheet, wpTableHeaderRow);
    // Subtotal excel function ($D7 - starting cell for range)
    createHeaderCellOfFormulaType(wpHeadingRow, "SUBTOTAL(109,OFFSET($D7,0,0," + (rowCount - 6) + ",1))",
        ApicConstants.COLUMN_INDEX_3, NUMBER);

    // Subtotal excel function ($E7 - starting cell for range)
    createHeaderCellOfFormulaType(wpHeadingRow, "SUBTOTAL(109,OFFSET($E7,0,0," + (rowCount - 6) + ",1))",
        ApicConstants.COLUMN_INDEX_4, NUMBER);

    // Excel formula to divide Values from Parameters reviewed by total parameters (Divide Column D by Column E)
    createHeaderCellOfFormulaType(wpHeadingRow, "$D5/$E5", ApicConstants.COLUMN_INDEX_5, PERCENTAGE);

    // Cheeck for unassigned parameters in a2l file
    if (!this.unassigndParamSet.isEmpty()) {

      // If present, display it as note
      ExcelCommon.getInstance().createCell(wpTableHeaderRow, NOTE_UNASSIGNED_PARAM, ApicConstants.COLUMN_INDEX_8,
          headerCellStyle);

      // Display the unassigned parameters
      int unassignRowNum = wpTableHeaderRow.getRowNum() + 1;
      for (String unassignParam : this.unassigndParamSet) {
        Row unassignRow;
        if (null != wpRvwStatSheet.getRow(unassignRowNum)) {
          unassignRow = wpRvwStatSheet.getRow(unassignRowNum);
        }
        else {
          unassignRow = ExcelCommon.getInstance().createExcelRow(wpRvwStatSheet, unassignRowNum);
        }
        ExcelCommon.getInstance().createCell(unassignRow, unassignParam, ApicConstants.COLUMN_INDEX_8, cellStyle);
        unassignRowNum++;
      }
      wpRvwStatSheet.autoSizeColumn(ApicConstants.COLUMN_INDEX_8);
    }

    // Auto resize columns in WP Statisics sheet
    autoResizeColumns(wpRvwStatSheet, ApicConstants.COLUMN_INDEX_5);

    // Disable autoFilter when there are no rows
    if (rowCount > 0) {
      ExcelCommon.getInstance().setAutoFilter(wpRvwStatSheet,
          "A6:" + ExcelCommon.getInstance().getExcelColName(ApicConstants.COLUMN_INDEX_5), rowCount);
    }
    // Freeze the parameter related columns
    wpRvwStatSheet.createFreezePane(FREEZABLE_COL, ApicConstants.COLUMN_INDEX_6);
  }

  /**
   * @param cellStyle
   * @param a2lDataRow
   */
  private void tableDataForParamStat(final CellStyle cellStyle, final Row a2lDataRow) {
    int[] a2lRvwStat = this.cdrReportStatistics.getA2lReviewDetails();
    ExcelCommon.getInstance().createCell(a2lDataRow, this.cdrData.getPidcA2l().getName(), 0, cellStyle);
    createCellForNum(a2lDataRow, a2lRvwStat[0], ApicConstants.COLUMN_INDEX_1, this.numCellStyle);
    createCellForNum(a2lDataRow, a2lRvwStat[1], ApicConstants.COLUMN_INDEX_2, this.numCellStyle);
    createCellForNum(a2lDataRow, a2lRvwStat[1] - a2lRvwStat[0], ApicConstants.COLUMN_INDEX_3, this.numCellStyle);
  }

  /**
   * @param headerCellStyle
   * @param wpRvwStatSheet
   * @param a2lHeadingRow
   */
  private void tableHeadingForParamStat(final CellStyle headerCellStyle, final Sheet wpRvwStatSheet,
      final Row a2lHeadingRow) {
    final Row a2lTableHeaderRow =
        ExcelCommon.getInstance().createExcelRow(wpRvwStatSheet, a2lHeadingRow.getRowNum() + 1);

    ExcelCommon.getInstance().createCell(a2lTableHeaderRow, "A2l file Name", ApicConstants.COLUMN_INDEX_0,
        headerCellStyle);
    ExcelCommon.getInstance().createCell(a2lTableHeaderRow, PARAMS_REVIEWED, ApicConstants.COLUMN_INDEX_1,
        headerCellStyle);
    ExcelCommon.getInstance().createCell(a2lTableHeaderRow, TOTAL_PARAMS, ApicConstants.COLUMN_INDEX_2,
        headerCellStyle);
    ExcelCommon.getInstance().createCell(a2lTableHeaderRow, "Parameters not reviewed", ApicConstants.COLUMN_INDEX_3,
        headerCellStyle);
  }

  private int createWorkPackageStatPage(final CellStyle cellStyle, final Sheet wpRvwStatSheet,
      final Row wpTableHeaderRow) {
    int wpRowNum = 0;
    if (CommonUtils.isNotEmpty(this.wpStatistics)) {
      wpRowNum = wpTableHeaderRow.getRowNum() + 1;
      // Iterate over the WP statistics in a2l file
      for (Entry<String, int[]> entry : this.wpStatistics.entrySet()) {
        String[] wpWithResp = entry.getKey().split(TEXT_SEPERATOR);
        int[] stats = entry.getValue();
        final Row row = ExcelCommon.getInstance().createExcelRow(wpRvwStatSheet, wpRowNum);
        ExcelCommon.getInstance().createCell(row, wpWithResp[0], ApicConstants.COLUMN_INDEX_0, cellStyle);
        ExcelCommon.getInstance().createCell(row, wpWithResp[1], ApicConstants.COLUMN_INDEX_1, cellStyle);
        ExcelCommon.getInstance().createCell(row, wpWithResp[2], ApicConstants.COLUMN_INDEX_2, cellStyle);
        createCellForNum(row, stats[0], ApicConstants.COLUMN_INDEX_3, this.numCellStyle);
        createCellForNum(row, stats[1], ApicConstants.COLUMN_INDEX_4, this.numCellStyle);
        createCellForNum(row, stats[2] * 0.01, ApicConstants.COLUMN_INDEX_5, this.percentCellStyle);
        wpRowNum++;
      }
    }
    return wpRowNum;
  }

  /**
   * @param columns
   * @param cdrRvwStatSheet
   */
  private void autoResizeColumns(final Sheet sheet, final int columns) {

    // Auto Resize the columns with data
    for (int columnIndex = 0; columnIndex <= columns; columnIndex++) {
      sheet.autoSizeColumn(columnIndex);
    }
  }

  /**
   * @param cdrData
   */
  private void getA2lRvwStatistics() {

    // Retrieve review statistics of all componenets
    getunassignedParam();
    // get the workpackage related Statistics
    getWorkPackageStatistics();
    CDMLogger.getInstance().debug("WP statistics computed. Record count = {}", this.wpStatistics.size());

    getBCStatistics();
    CDMLogger.getInstance().debug("BC statistics computed. Record count = {}", this.bcStatistics.size());

    getFCStatistics();
    CDMLogger.getInstance().debug("FC statistics computed. Record count = {}", this.funcStatistics.size());
  }

  /**
   *
   */
  private void getWorkPackageStatistics() {
    for (A2LParameter a2lParam : this.cdrData.getA2lEditorDataProvider().getA2lFileInfoBO().getA2lParamMap(null)
        .values()) {
      Long paramId = a2lParam.getParamId();
      ParamWpResponsibility respObj = this.cdrData.getParamWpResp(paramId);
      if (respObj != null) {
        StringBuilder builder = new StringBuilder();
        builder.append(getWpName(respObj));
        builder.append(TEXT_SEPERATOR);
        builder.append(getRespType(respObj));
        builder.append(TEXT_SEPERATOR);
        builder.append(getRespName(respObj));
        if (!this.wpStatistics.containsKey(builder.toString())) {
          int[] statistics = this.cdrReportStatistics.getStatistics(respObj);
          this.wpStatistics.put(builder.toString(), statistics);
        }
      }
    }
  }

  private String getWpName(final ParamWpResponsibility respObj) {
    String wpName = this.cdrData.getA2lEditorDataProvider().getA2lWpInfoBO().getA2lWpDefnModel().getWpRespMap()
        .get(respObj.getWpRespId()).getName();
    if (null != wpName) {
      return wpName;
    }
    return "";
  }

  private String getRespType(final ParamWpResponsibility respObj) {
    A2lResponsibility a2lResp = this.cdrData.getA2lEditorDataProvider().getA2lWpInfoBO().getA2lResponsibilityModel()
        .getA2lResponsibilityMap().get(respObj.getRespId());
    return this.cdrData.getA2lEditorDataProvider().getA2lWpInfoBO().getRespTypeName(a2lResp);
  }

  public String getRespName(final ParamWpResponsibility respObj) {
    return this.cdrData.getA2lEditorDataProvider().getA2lWpInfoBO().getA2lResponsibilityModel()
        .getA2lResponsibilityMap().get(respObj.getRespId()).getName();
  }

  /**
   * Method to retrieve info about unassigned parameters in the a2l file
   *
   * @param cdrData
   */
  private void getunassignedParam() {

    // Add unassigned parameters to the set
    for (A2LParameter a2lParam : this.cdrData.getA2lEditorDataProvider().getA2lFileInfoBO().getUnassignedParams()) {
      this.unassigndParamSet.add(a2lParam.getName());
    }
  }


  /**
   * Retrieve review statistics of Functional Components
   *
   * @param cdrReportData
   * @param cdrData
   */
  private void getFCStatistics() {
    this.cdrData.getA2lEditorDataProvider().getA2lFileInfoBO().getAllFunctionMap().values().forEach(func -> {
      int[] stat = this.cdrReportStatistics.getStatistics(func);
      this.funcStatistics.put(func.getName(), stat);
    });
  }

  /**
   * Retrieve review statistics of Base Components
   *
   * @param cdrReportData
   * @param cdrData
   */
  private void getBCStatistics() {
    this.cdrData.getA2lEditorDataProvider().getA2lFileInfoBO().getA2lBcMap().values().forEach(bc -> {
      int[] stat = this.cdrReportStatistics.getStatistics(bc);
      this.bcStatistics.put(bc.getBcName(), stat);
    });
  }


  private void createHeaderCellOfFormulaType(final Row wpHeadingRow, final String cellValue, final int cellColumn,
      final String cellType) {
    CellStyle headerCellStyle = ExcelCommon.getInstance().createHeaderCellStyleWithoutBorder(this.workbook);
    headerCellStyle.setAlignment(CellStyle.ALIGN_RIGHT);
    if (cellType.equals(NUMBER)) {
      headerCellStyle.setDataFormat(this.workbook.createDataFormat().getFormat(THOUSAND_SEPARATOR_FORMAT));
    }
    else if (cellType.equals(PERCENTAGE)) {
      headerCellStyle.setDataFormat(this.workbook.createDataFormat().getFormat(PERCENTAGE_FORMAT));
    }
    headerCellStyle.setFont(this.fontForSubtotalRow);
    Cell cell = wpHeadingRow.createCell(cellColumn);
    cell.setCellType(2);
    cell.setCellFormula(cellValue);
    cell.setCellStyle(headerCellStyle);
  }

  private Cell createCellForNum(final Row row, final double cellValue, final int cellColumn,
      final CellStyle cellStyle) {
    final Cell cell = row.createCell(cellColumn);
    cell.setCellType(0);
    cell.setCellValue(cellValue);
    cell.setCellStyle(cellStyle);
    return cell;
  }
}
