///*
// * Copyright (c) Robert Bosch GmbH. All rights reserved.
// */
//package com.bosch.caltool.icdm.report.compare;
//
//import java.util.SortedSet;
//
//import org.apache.poi.ss.usermodel.CellStyle;
//import org.apache.poi.ss.usermodel.Row;
//import org.apache.poi.ss.usermodel.Sheet;
//import org.apache.poi.ss.util.CellRangeAddress;
//import org.eclipse.core.runtime.IProgressMonitor;
//
//import com.bosch.caltool.icdm.model.apic.ApicConstants;
//import com.bosch.caltool.icdm.model.apic.attr.AttributeValueType;
//import com.bosch.caltool.icdm.report.excel.ExcelUtil;
//import com.bosch.caltool.icdm.report.excel.IExcelConstants;
//
//
///**
// * @author jvi6cob
// */
//public class PIDCCompareReport extends AbstractCompareReport {
//
//  /**
//   * monitor working 3 in milli sec
//   */
//  private static final int MONITOR_WORKING3_CONS = 90;
//
//  /**
//   * monitor working 2 in milli sec
//   */
//  private static final int MONITOR_WORKING2_CONS = 50;
//
//  /**
//   * monitor working 1 in milli sec
//   */
//  private static final int MONITOR_WORKING1_CONS = 30;
//
//  /**
//   * monitor working in milli second
//   */
//  private static final int MONITOR_WRK_IN_MILLISEC = 10;
//
//  /**
//   * begin task in milli second
//   */
//  private static final int BEGIN_TASK_IN_MILLISEC = 100;
//
//  private static final int PIDC_COMP_RES_START_ROW_COUNT = 2;
//
//  private static final int PIDC_COMP_REP_ROW_INDEX = 2;
//
//  /**
//   * monitor
//   */
//  private IProgressMonitor monitor;
//
//  private static final int SGRP_COLUMN_WIDTH = 17 * 256;
//  private static final int GRP_COLUMN_WIDTH = 20 * 256;
//  private static final int NAME_COLUMN_WIDTH = 35 * 256;
//  private static final int DESC_COLUMN_WIDTH = 35 * 256;
//  private static final int USED_COLUMN_WIDTH = 10 * 256;
//  private static final int VAL_COLUMN_WIDTH = 19 * 256;
//  private static final int PART_NUM_COLUMN_WIDTH = 19 * 256;
//  private static final int SPEC_LINK_COLUMN_WIDTH = 19 * 256;
//  private static final int DESCRIP_COLUMN_WIDTH = 19 * 256;
//  private static final int DIFF_COLUMN_WIDTH = 8 * 256;
//
//  /**
//   * Columns for PIDC attr compare sheet display
//   */
//  private final String[] pidcAttrCompareSheetHeader = new String[] {
//      IExcelConstants.RH_SUPER_GROUP,
//      IExcelConstants.RH_GROUP,
//      IExcelConstants.RH_NAME,
//      IExcelConstants.RH_DESCRIPTION,
//      IExcelConstants.RH_USED,
//      IExcelConstants.RH_VALUE,
//      IExcelConstants.RH_PART_NUMBER,
//      IExcelConstants.RH_SPEC_LINK,
//      IExcelConstants.RH_ADDITIONAL_INFO_DESC,
//      IExcelConstants.RH_USED,
//      IExcelConstants.RH_VALUE,
//      IExcelConstants.RH_PART_NUMBER,
//      IExcelConstants.RH_SPEC_LINK,
//      IExcelConstants.RH_ADDITIONAL_INFO_DESC,
//      IExcelConstants.H_DIFF };
//
//  private int rowCounter;
//
//
//  /**
//   *
//   */
//  public PIDCCompareReport() {
//    super();
//  }
//
//  /**
//   *
//   */
//  public PIDCCompareReport(final IProgressMonitor monitor) {
//    super();
//    this.monitor = monitor;
//  }
//
//  /**
//   * {@inheritDoc}
//   */
//  @Override
//  protected void createCompareWorkSheet(final Object obj1, final Object obj2) {
//    if ((obj1 instanceof PIDCVersion) && (obj2 instanceof PIDCVersion)) {
//      this.monitor.beginTask("", BEGIN_TASK_IN_MILLISEC);
//      final PIDCVersion pidcVer1 = (PIDCVersion) obj1;
//      final PIDCVersion pidcVer2 = (PIDCVersion) obj2;
//      createPIDCCompareWorkSheet(pidcVer1, pidcVer2);
//    }
//
//  }
//
//  /**
//   * Creates the PID Card compare sheet
//   *
//   * @param pidcVer1 {@link PIDCVersion} to compare
//   * @param pidcVer2 {@link PIDCVersion} to compare
//   */
//  private void createPIDCCompareWorkSheet(final PIDCVersion pidcVer1, final PIDCVersion pidcVer2) {
//
//    this.monitor.worked(MONITOR_WRK_IN_MILLISEC);
//    // initialize sheet
//    final Sheet pidcAttrSheet = initializeWorkSheet(pidcVer1, pidcVer2);
//
//    // create Compare Header Cell Style
//    createCompareHeaderCellStyle(this.workBook);
//    // ICDM-1470
//    // construct First Header
//    createHeaderForCompare(this.workBook, pidcVer1.getPidc().getName(), pidcVer2.getPidc().getName(),
//        pidcVer1.getPidcVersionName(), pidcVer2.getPidcVersionName(), pidcAttrSheet, IExcelConstants.INDEX_1_CONSTANT,
//        IExcelConstants.INDEX_2_CONSTANT, 0);
//
//    // construct Second Header
//    createCommonHeaderForCompare(this.workBook, "COMMON", null, pidcAttrSheet, IExcelConstants.INDEX_CONSTANT, 1);
//
//    // construct Third Header
//    final String[] sheetHeaders = this.pidcAttrCompareSheetHeader;
//    final Row headerSecondRow = ExcelUtil.getInstance().createExcelRow(pidcAttrSheet, PIDC_COMP_REP_ROW_INDEX);
//    for (int headerCol = 0; headerCol < sheetHeaders.length; headerCol++) {
//      ExcelUtil.getInstance().createHeaderCell(headerSecondRow, sheetHeaders[headerCol], headerCol,
//          this.headerCellStyle, this.font);
//    }
//
//    // Create pidc comparator
//    this.monitor.worked(MONITOR_WORKING1_CONS);
//    final PidcComparator pidcComparator = new PidcComparator(pidcVer1, pidcVer2);
//    pidcComparator.compare();
//    this.monitor.worked(MONITOR_WORKING2_CONS);
//    // get compare results
//    final SortedSet<PidcCompareResult> compareResult = pidcComparator.getResult();
//
//    this.rowCounter = PIDC_COMP_RES_START_ROW_COUNT;
//    for (PidcCompareResult pidcCompareResult : compareResult) {
//      this.rowCounter++;
//      constructRow(pidcAttrSheet, sheetHeaders, this.rowCounter, pidcCompareResult);
//    }
//
//    this.monitor.worked(MONITOR_WORKING3_CONS);
//    // set Sheet behavior
//    setAutoFilterForPIDAttrCompareWorkSheet(pidcAttrSheet, this.rowCounter);
//    pidcAttrSheet.createFreezePane(IExcelConstants.COLUMN_NUM_FOUR, IExcelConstants.ROW_NUM_THREE,
//        IExcelConstants.COLUMN_NUM_FOUR, IExcelConstants.ROW_NUM_THREE);
//    pidcAttrSheet.setColumnHidden(sheetHeaders.length, true);
//    setColumnWidths(pidcAttrSheet);
//    // fit to page
//    pidcAttrSheet.setFitToPage(true);
//    groupAdditionalInfoColumns(pidcAttrSheet);
//
//  }
//
//  /**
//   * @param pidcVer1
//   * @param pidcVer2
//   * @return
//   */
//  private Sheet initializeWorkSheet(final PIDCVersion pidcVer1, final PIDCVersion pidcVer2) {
//    final String sheetName = (pidcVer1.getName()).replaceAll("[^a-zA-Z0-9]+", "_") + " Vs " +
//        (pidcVer2.getName()).replaceAll("[^a-zA-Z0-9]+", "_");
//    final Sheet pidcAttrSheet = this.workBook.createSheet(sheetName);
//    this.workBook.setActiveSheet(this.workBook.getSheetIndex(pidcAttrSheet));
//    return pidcAttrSheet;
//  }
//
//  /**
//   * Creates the row for the sheet
//   *
//   * @param workbook
//   * @param pidcAttrSheet
//   * @param sheetHeader
//   * @param rowCount
//   * @param pidcCompareResult
//   */
//  private void constructRow(final Sheet pidcAttrSheet, final String[] sheetHeader, final int rowCount,
//      final PidcCompareResult pidcCompareResult) {
//    final Row row = ExcelUtil.getInstance().createExcelRow(pidcAttrSheet, rowCount);
//    fillColumnData(sheetHeader, pidcCompareResult, row);
//  }
//
//  /**
//   * Fills the column data for the sheet
//   *
//   * @param workbook
//   * @param pidcAttrSheet
//   * @param sheetHeader
//   * @param rowCount
//   * @param pidcCompareResult
//   * @param row
//   */
//  private void fillColumnData(final String[] sheetHeader, final PidcCompareResult pidcCompareResult, final Row row) {
//    final Attribute attr = pidcCompareResult.getAttribute();
//    final CompareColumns firstColumnSet = pidcCompareResult.getFirstColumnSet();
//    final CompareColumns secondColumnSet = pidcCompareResult.getSecondColumnSet();
//
//    for (int col = 0; col <= sheetHeader.length; col++) {
//
//      if ((col < sheetHeader.length) && sheetHeader[col].equalsIgnoreCase(IExcelConstants.RH_VALUE)) {
//        fillValueColumn(pidcCompareResult, row, firstColumnSet, secondColumnSet, col);
//      }
//      else if ((col < sheetHeader.length) && sheetHeader[col].equalsIgnoreCase(IExcelConstants.RH_USED)) {
//        fillUsedColumn(pidcCompareResult, row, firstColumnSet, secondColumnSet, col);
//      }
//      else if ((col < sheetHeader.length) && sheetHeader[col].equalsIgnoreCase(IExcelConstants.RH_PART_NUMBER)) {
//        fillPartNumberColumn(pidcCompareResult, row, firstColumnSet, secondColumnSet, col);
//      }
//      else if ((col < sheetHeader.length) && sheetHeader[col].equalsIgnoreCase(IExcelConstants.RH_SPEC_LINK)) {
//        fillSpecLinkColumn(pidcCompareResult, row, firstColumnSet, secondColumnSet, col);
//      }
//      // ICDM-415
//      else if (((col == IExcelConstants.COLUMN_NUM_EIGHT) || (col == IExcelConstants.COLUMN_NUM_THIRTEEN)) &&
//          sheetHeader[col].equalsIgnoreCase(IExcelConstants.RH_ADDITIONAL_INFO_DESC)) {
//        fillDescColumn(pidcCompareResult, row, firstColumnSet, secondColumnSet, col);
//      }
//      else if (col == sheetHeader.length) {
//        // create invisible cell indicating attribute ID
//        ExcelUtil.getInstance().createCell(row, String.valueOf(attr.getAttributeID()), col, this.cellStyle);
//      }
//      else {
//        String result = IExcelConstants.EMPTY_STRING;
//        if (col == IExcelConstants.COLUMN_NUM_FOURTEEN) {
//          if (pidcCompareResult.isDiff()) {
//            // fetches Data for column Diff
//            result = IExcelConstants.DIFF_MARKER;
//          }
//        }
//        else {
//          // fetches Data for columns SuperGroup,Group,Name,Description
//          result = colTxtFromPrjCompareAttr(col, attr);
//        }
//        ExcelUtil.getInstance().createCell(row, result, col, this.cellStyle);
//      }
//
//    }
//
//  }
//
//  /**
//   * @param pidcAttrSheet
//   * @param length
//   */
//  private void groupAdditionalInfoColumns(final Sheet pidcAttrSheet) {
//
//    pidcAttrSheet.groupColumn(IExcelConstants.COLUMN_NUM_SIX, IExcelConstants.COLUMN_NUM_SEVEN);
//    pidcAttrSheet.setColumnGroupCollapsed(IExcelConstants.COLUMN_NUM_SIX, true);
//    pidcAttrSheet.groupColumn(IExcelConstants.COLUMN_NUM_ELEVEN, IExcelConstants.COLUMN_NUM_TWELEVE);
//    pidcAttrSheet.setColumnGroupCollapsed(IExcelConstants.COLUMN_NUM_ELEVEN, true);
//
//  }
//
//  /**
//   * Column text of Project id card table viewer from project attribute
//   *
//   * @param columnIndex
//   * @param viwvAttrProjectattr
//   * @return String
//   */
//  private String colTxtFromPrjCompareAttr(final int columnIndex, final Attribute attr) {
//    String result = IExcelConstants.EMPTY_STRING;
//
//    switch (columnIndex) {
//
//      case IExcelConstants.COLUMN_NUM_ZERO:
//        result = attr.getAttributeGroup().getSuperGroup().getName();
//        break;
//
//      case IExcelConstants.COLUMN_NUM_ONE:
//        result = attr.getAttributeGroup().getName();
//        break;
//
//      case IExcelConstants.COLUMN_NUM_TWO:
//        result = attr.getAttributeName();
//        break;
//
//      case IExcelConstants.COLUMN_NUM_THREE:
//        result = attr.getAttributeDesc();
//        break;
//    }
//    return result;
//  }
//
//  /**
//   * Fills the used column in PID card compare sheet. Text Color is calculated based on difference between used flags of
//   * the two PID cards. If attribute is not existing due to dependency attribute,used flag is marked as empty string
//   *
//   * @param pidcCompareResult
//   * @param row
//   * @param firstColumnSet
//   * @param secondColumnSet
//   * @param col
//   */
//  private void fillUsedColumn(final PidcCompareResult pidcCompareResult, final Row row,
//      final CompareColumns firstColumnSet, final CompareColumns secondColumnSet, final int col) {
//    String usedValue = IExcelConstants.EMPTY_STRING;
//    if (col == IExcelConstants.COLUMN_NUM_FOUR) {
//      usedValue = firstColumnSet.getUsedFlag();
//    }
//    else if (col == IExcelConstants.COLUMN_NUM_NINE) {
//      usedValue = secondColumnSet.getUsedFlag();
//    }
//    CellStyle localCellStyle;
//    localCellStyle = this.cellStyle;
//    if (pidcCompareResult.isAttrUsedFlagDiff() &&
//        !usedValue.equals(ApicConstants.PROJ_ATTR_USED_FLAG.NOT_DEFINED.getUiType())) {
//      if ((col == IExcelConstants.COLUMN_NUM_FOUR) &&
//          (secondColumnSet.getUsedFlag().equals(ApicConstants.PROJ_ATTR_USED_FLAG.NOT_DEFINED.getUiType()))) {
//        localCellStyle = this.redCellStyle;
//      }
//      if (col == IExcelConstants.COLUMN_NUM_NINE) {
//        localCellStyle = this.redCellStyle;
//      }
//    }
//    ExcelUtil.getInstance().createCell(row, usedValue, col, localCellStyle);
//  }
//
//  /**
//   * Fills the Value column in PID card compare sheet. Text Color is calculated based on difference between values of
//   * the two PID cards. If attribute is not existing due to dependency attribute,value is marked as empty string. If
//   * attribute value is null,it is marked as '-'.
//   *
//   * @param pidcAttrSheet
//   * @param pidcCompareResult
//   * @param row
//   * @param attr
//   * @param firstColumnSet
//   * @param secondColumnSet
//   * @param col
//   */
//  private void fillValueColumn(final PidcCompareResult pidcCompareResult, final Row row,
//      final CompareColumns firstColumnSet, final CompareColumns secondColumnSet, final int col) {
//    String result = IExcelConstants.EMPTY_STRING;
//    if (col == IExcelConstants.COLUMN_NUM_FIVE) {
//      result = firstColumnSet.getAttrValue();
//    }
//    else if (col == IExcelConstants.COLUMN_NUM_TEN) {
//      result = secondColumnSet.getAttrValue();
//    }
//    boolean isRed = false;
//    if (pidcCompareResult.isAttrValueDiff() && (result != null) && !result.trim().isEmpty()) {
//      if ((col == IExcelConstants.COLUMN_NUM_FIVE) && isSecondColValValid(secondColumnSet)) {
//        isRed = true;
//      }
//      if (col == IExcelConstants.COLUMN_NUM_TEN) {
//        isRed = true;
//      }
//    }
//    // ICDM-323
//    if (((firstColumnSet.getValueType() == AttributeValueType.HYPERLINK) ||
//        (secondColumnSet.getValueType() == AttributeValueType.HYPERLINK)) && (result != null) &&
//        (!result.equals(ApicConstants.SUB_VARIANT_ATTR_DISPLAY_NAME)) &&
//        (!result.equals(ApicConstants.VARIANT_ATTR_DISPLAY_NAME))) {
//      createPidcCompareHyperLinkCol(row, col, result, isRed);
//    }
//    else {
//      createPidcCompareCol(row, col, result, isRed);
//    }
//  }
//
//  /**
//   * Fills the PartNumber column in PID card compare sheet. Text Color is calculated based on difference between part
//   * numbers of the two PID cards. If attribute is not existing due to dependency attribute,PartNumber is marked as
//   * empty string. If PartNumber is null,it is marked as '-'.
//   *
//   * @param pidcAttrSheet
//   * @param pidcCompareResult
//   * @param row
//   * @param attr
//   * @param firstColumnSet
//   * @param secondColumnSet
//   * @param col
//   */
//  private void fillPartNumberColumn(final PidcCompareResult pidcCompareResult, final Row row,
//      final CompareColumns firstColumnSet, final CompareColumns secondColumnSet, final int col) {
//    String result = IExcelConstants.EMPTY_STRING;
//    if (col == IExcelConstants.COLUMN_NUM_SIX) {
//      result = firstColumnSet.getPartNumber();
//    }
//    else if (col == IExcelConstants.COLUMN_NUM_ELEVEN) {
//      result = secondColumnSet.getPartNumber();
//    }
//    boolean isRed = false;
//    if (pidcCompareResult.isPartNumberDiff() && (result != null) && !result.trim().isEmpty()) {
//      if ((col == IExcelConstants.COLUMN_NUM_SIX) && isSecondColPartNumValid(secondColumnSet)) {
//        isRed = true;
//      }
//      if (col == IExcelConstants.COLUMN_NUM_ELEVEN) {
//        isRed = true;
//      }
//    }
//    createPidcCompareCol(row, col, result, isRed);
//  }
//
//  /**
//   * Fills the PartNumber column in PID card compare sheet. Text Color is calculated based on difference between part
//   * numbers of the two PID cards. If attribute is not existing due to dependency attribute,PartNumber is marked as
//   * empty string. If PartNumber is null,it is marked as '-'.
//   *
//   * @param pidcAttrSheet
//   * @param pidcCompareResult
//   * @param row
//   * @param attr
//   * @param firstColumnSet
//   * @param secondColumnSet
//   * @param col
//   */
//  private void fillDescColumn(final PidcCompareResult pidcCompareResult, final Row row,
//      final CompareColumns firstColumnSet, final CompareColumns secondColumnSet, final int col) {
//    String result = IExcelConstants.EMPTY_STRING;
//    if (col == IExcelConstants.COLUMN_NUM_EIGHT) {
//      result = firstColumnSet.getDesc();
//    }
//    else if (col == IExcelConstants.COLUMN_NUM_THIRTEEN) {
//      result = secondColumnSet.getDesc();
//    }
//    boolean isRed = false;
//    if (pidcCompareResult.isDescDiff() && (result != null) && !result.trim().isEmpty()) {
//      if ((col == IExcelConstants.COLUMN_NUM_EIGHT) && isSecondColDescValid(secondColumnSet)) {
//        isRed = true;
//      }
//      if (col == IExcelConstants.COLUMN_NUM_THIRTEEN) {
//        isRed = true;
//      }
//    }
//    createPidcCompareCol(row, col, result, isRed);
//  }
//
//  /**
//   * @param pidcCompareResult
//   * @param row
//   * @param firstColumnSet
//   * @param secondColumnSet
//   * @param col
//   */
//  private void fillSpecLinkColumn(final PidcCompareResult pidcCompareResult, final Row row,
//      final CompareColumns firstColumnSet, final CompareColumns secondColumnSet, final int col) {
//    String result = IExcelConstants.EMPTY_STRING;
//    if (col == IExcelConstants.COLUMN_NUM_SEVEN) {
//      result = firstColumnSet.getSpecLink();
//    }
//    else if (col == IExcelConstants.COLUMN_NUM_TWELEVE) {
//      result = secondColumnSet.getSpecLink();
//    }
//    boolean isRed = false;
//    if (pidcCompareResult.isSpecLinkDiff() && (result != null) && !result.trim().isEmpty()) {
//      if ((col == IExcelConstants.COLUMN_NUM_SEVEN) && isSecondColSpecLinkValid(secondColumnSet)) {
//        isRed = true;
//      }
//      if (col == IExcelConstants.COLUMN_NUM_TWELEVE) {
//        isRed = true;
//      }
//    }
//    createPidcCompareHyperLinkCol(row, col, result, isRed);
//
//  }
//
//  /**
//   * @param secondColumnSet
//   * @return
//   */
//  private boolean isSecondColValValid(final CompareColumns secondColumnSet) {
//    return (secondColumnSet.getAttrValue() == null) || secondColumnSet.getAttrValue().isEmpty();
//  }
//
//  /**
//   * @param secondColumnSet
//   * @return
//   */
//  private boolean isSecondColSpecLinkValid(final CompareColumns secondColumnSet) {
//    return (secondColumnSet.getSpecLink() == null) || secondColumnSet.getSpecLink().isEmpty();
//  }
//
//  /**
//   * @param secondColumnSet
//   * @return
//   */
//  private boolean isSecondColPartNumValid(final CompareColumns secondColumnSet) {
//    return (secondColumnSet.getPartNumber() == null) || secondColumnSet.getPartNumber().isEmpty();
//  }
//
//  /**
//   * @param secondColumnSet
//   * @return
//   */
//  private boolean isSecondColDescValid(final CompareColumns secondColumnSet) {
//    return (secondColumnSet.getDesc() == null) || secondColumnSet.getDesc().isEmpty();
//  }
//
//  /**
//   * Sets the columns widths for the sheets, specific for this PIDC Compare report
//   *
//   * @param pidcAttrSheet
//   */
//  private void setColumnWidths(final Sheet pidcAttrSheet) {
//    pidcAttrSheet.setColumnWidth(IExcelConstants.COLUMN_NUM_ZERO, SGRP_COLUMN_WIDTH);
//    pidcAttrSheet.setColumnWidth(IExcelConstants.COLUMN_NUM_ONE, GRP_COLUMN_WIDTH);
//    pidcAttrSheet.setColumnWidth(IExcelConstants.COLUMN_NUM_TWO, NAME_COLUMN_WIDTH);
//    pidcAttrSheet.setColumnWidth(IExcelConstants.COLUMN_NUM_THREE, DESC_COLUMN_WIDTH);
//    pidcAttrSheet.setColumnWidth(IExcelConstants.COLUMN_NUM_FOUR, USED_COLUMN_WIDTH);
//    pidcAttrSheet.setColumnWidth(IExcelConstants.COLUMN_NUM_FIVE, VAL_COLUMN_WIDTH);
//    pidcAttrSheet.setColumnWidth(IExcelConstants.COLUMN_NUM_SIX, PART_NUM_COLUMN_WIDTH);
//    pidcAttrSheet.setColumnWidth(IExcelConstants.COLUMN_NUM_SEVEN, SPEC_LINK_COLUMN_WIDTH);
//    pidcAttrSheet.setColumnWidth(IExcelConstants.COLUMN_NUM_EIGHT, DESCRIP_COLUMN_WIDTH);
//    pidcAttrSheet.setColumnWidth(IExcelConstants.COLUMN_NUM_NINE, USED_COLUMN_WIDTH);
//    pidcAttrSheet.setColumnWidth(IExcelConstants.COLUMN_NUM_TEN, VAL_COLUMN_WIDTH);
//    pidcAttrSheet.setColumnWidth(IExcelConstants.COLUMN_NUM_ELEVEN, PART_NUM_COLUMN_WIDTH);
//    pidcAttrSheet.setColumnWidth(IExcelConstants.COLUMN_NUM_TWELEVE, SPEC_LINK_COLUMN_WIDTH);
//    pidcAttrSheet.setColumnWidth(IExcelConstants.COLUMN_NUM_THIRTEEN, DESCRIP_COLUMN_WIDTH);
//    pidcAttrSheet.setColumnWidth(IExcelConstants.COLUMN_NUM_FOURTEEN, DIFF_COLUMN_WIDTH);
//  }
//
//  /**
//   * Create column in compare project id cards attribute sheet
//   *
//   * @param workbook
//   * @param attr
//   * @param pidcAttrSheet
//   * @param rowCount
//   * @param row
//   * @param col
//   * @param tempAttrSheet
//   */
//  private void createPidcCompareCol(final Row row, final int col, final String result, final boolean isRed) {
//
//    CellStyle localCellStyle = this.cellStyle;
//    if (isRed) {
//      localCellStyle = this.redCellStyle;
//    }
//    // result null indicates that attribute value is null && result is "" indicates attribute
//    // dependcy is missing in one of the compare objects
//    ExcelUtil.getInstance().createCell(row, result, col, localCellStyle);
//  }
//
//  /**
//   * Create column in compare project id cards attribute sheet
//   *
//   * @param workbook
//   * @param attr
//   * @param pidcAttrSheet
//   * @param rowCount
//   * @param row
//   * @param col
//   * @param tempAttrSheet
//   */
//  private void createPidcCompareHyperLinkCol(final Row row, final int col, final String result, final boolean isRed) {
//
//    CellStyle localCellStyle = this.cellStyle;
//    if (isRed) {
//      localCellStyle = this.redCellStyle;
//    }
//    // result null indicates that attribute value is null && result is "" indicates attribute
//    // dependcy is missing in one of the compare objects
//    ExcelUtil.getInstance().createHyperLinkCell(this.workBook, row, result, col, localCellStyle, isRed);
//  }
//
//  /**
//   * Auto filter for project id car attribute work sheet
//   *
//   * @param pidcAttrSheet
//   * @param rowCount
//   */
//  private void setAutoFilterForPIDAttrCompareWorkSheet(final Sheet pidcAttrSheet, final int rowCount) {
//    final String autoFilterRange = "A3:O" + rowCount;
//    pidcAttrSheet.setAutoFilter(CellRangeAddress.valueOf(autoFilterRange));
//  }
//
//}
