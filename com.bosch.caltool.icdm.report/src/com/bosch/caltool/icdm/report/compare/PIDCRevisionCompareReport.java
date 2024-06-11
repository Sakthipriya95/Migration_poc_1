///*
// * Copyright (c) Robert Bosch GmbH. All rights reserved.
// */
//package com.bosch.caltool.icdm.report.compare;
//
//import java.util.List;
//import java.util.Map;
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
//public class PIDCRevisionCompareReport extends AbstractCompareReport {
//
//  private IProgressMonitor monitor;
//
//  private static final int VAR_COLUMN_WIDTH = 17 * 256;
//  private static final int SVAR_COLUMN_WIDTH = 17 * 256;
//  private static final int SGRP_COLUMN_WIDTH = 16 * 256;
//  private static final int GRP_COLUMN_WIDTH = 16 * 256;
//  private static final int NAME_COLUMN_WIDTH = 22 * 256;
//  private static final int DESC_COLUMN_WIDTH = 14 * 256;
//  private static final int USED_COLUMN_WIDTH = 10 * 256;
//  private static final int VAL_COLUMN_WIDTH = 19 * 256;
//  private static final int DIFF_COLUMN_WIDTH = 8 * 256;
//  private static final int PART_NUM_COLUMN_WIDTH = 19 * 256;
//  private static final int SPEC_LINK_COLUMN_WIDTH = 19 * 256;
//  private static final int DESCRIP_COLUMN_WIDTH = 19 * 256;
//  /**
//   * the beginning time of pidc revision
//   */
//  private static final int PIDC_REV_REP_BEGIN_TIME = 100;
//  /**
//   * the working time for the pidc compare worksheet
//   */
//  private static final int PIDC_REV_REP_WORK_TIME_1 = 10;
//  /**
//   * the index for the pidc header compare
//   */
//  private static final int PIDC_REV_REP_HEADER_INDEX_1 = 6;
//  /**
//   * the index for the pidc header compare
//   */
//  private static final int PIDC_REV_REP_HEADER_INDEX_2 = 11;
//  /**
//   * the index for the pidc revision header compare
//   */
//  private static final int PIDC_REV_HDR_SECOND_ROW_INDEX = 2;
//  /**
//   * the working time for the pidc revision report-2
//   */
//  private static final int PIDC_REV_REP_WORK_TIME_2 = 30;
//  /**
//   * the working time for the pidc revision report-3
//   */
//  private static final int PIDC_REV_REP_WORK_TIME_3 = 50;
//  /**
//   * the working time for the pidc revision report-4
//   */
//  private static final int PIDC_REV_REP_WORK_TIME_4 = 90;
//  /**
//   * the starting count of the row in the excel
//   */
//  private static final int ROW_STARTING_COUNT = 2;
//  /**
//   * the variant index
//   */
//  private static final int VAR_INDEX = 0;
//  /**
//   * the sub-variant index
//   */
//  private static final int SUB_VAR_INDEX = 1;
//  /**
//   * the index for super group attribute
//   */
//  private static final int ATTR_SUPER_GROUP_INDEX = 2;
//  /**
//   * the index for group attribute
//   */
//  private static final int ATTR_GROUP_NAME_INDEX = 3;
//  /**
//   * the index for attribute name
//   */
//  private static final int ATTR_NAME_INDEX = 4;
//  /**
//   * the index for attribute description
//   */
//  private static final int ATTR_DESC_INDEX = 5;
//
//  /**
//   * Columns for PIDC variant attr compare sheet display
//   */
//  private static final String[] pidcRevSheetHdr = new String[] {
//      IExcelConstants.COMP_VARIANT,
//      IExcelConstants.COMP_SUBVARIANT,
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
//  /**
//   * Constructor with empty args
//   */
//  public PIDCRevisionCompareReport() {
//    super();
//  }
//
//  /**
//   * Constructor initialised with ProgressMonitor
//   *
//   * @param monitor IProgressMonitor
//   */
//  public PIDCRevisionCompareReport(final IProgressMonitor monitor) {
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
//      this.monitor.beginTask("", PIDC_REV_REP_BEGIN_TIME);
//      final PIDCVersion pidcVer1 = (PIDCVersion) obj1;
//      final PIDCVersion pidcVer2 = (PIDCVersion) obj2;
//      createPIDCCompareWorkSheet(pidcVer1, pidcVer2);
//    }
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
//    this.monitor.worked(PIDC_REV_REP_WORK_TIME_1);
//
//    // initialize sheet
//    final Sheet pidcAttrSheet = initializeWorksheet(pidcVer1, pidcVer2);
//
//    createCompareHeaderCellStyle(this.workBook);
//
//    // construct First Header One card name is parsed since both variants belong to same card
//    createHeaderForCompare(this.workBook, pidcVer1.getName(), pidcVer2.getName(),
//        Long.toString(pidcVer1.getPidcRevision()), Long.toString(pidcVer2.getPidcRevision()), pidcAttrSheet,
//        PIDC_REV_REP_HEADER_INDEX_1, PIDC_REV_REP_HEADER_INDEX_2, 0);
//
//    // construct Second Header
//    createCommonHeaderForCompare(this.workBook, "COMMON", null, pidcAttrSheet, PIDC_REV_REP_HEADER_INDEX_1, 1);
//
//    // construct Third Header
//    final String[] sheetHeaders = PIDCRevisionCompareReport.pidcRevSheetHdr;
//    final Row headerSecondRow = ExcelUtil.getInstance().createExcelRow(pidcAttrSheet, PIDC_REV_HDR_SECOND_ROW_INDEX);
//    for (int headerCol = 0; headerCol < sheetHeaders.length; headerCol++) {
//      ExcelUtil.getInstance().createHeaderCell(headerSecondRow, sheetHeaders[headerCol], headerCol,
//          this.headerCellStyle, this.font);
//    }
//    // construct other rows with compare data
//    this.monitor.worked(PIDC_REV_REP_WORK_TIME_2);
//    final PidcRevisionComparator pidcRevComparator = new PidcRevisionComparator(pidcVer1, pidcVer2);
//    pidcRevComparator.compare();
//    this.monitor.worked(PIDC_REV_REP_WORK_TIME_3);
//
//    pidcAttrSheet.setRowSumsBelow(false);
//    // populate data in sheet
//    final int rowCount = populateData(pidcAttrSheet, sheetHeaders, pidcRevComparator);
//    this.monitor.worked(PIDC_REV_REP_WORK_TIME_4);
//    // set Sheet behavior
//    setAutoFilterForPIDAttrCompareWorkSheet(pidcAttrSheet, rowCount);
//    pidcAttrSheet.createFreezePane(IExcelConstants.COLUMN_NUM_SIX, IExcelConstants.ROW_NUM_THREE,
//        IExcelConstants.COLUMN_NUM_SIX, IExcelConstants.ROW_NUM_THREE);
//    pidcAttrSheet.setColumnHidden(sheetHeaders.length, true);
//    setColumnWidths(pidcAttrSheet);
//    pidcAttrSheet.setFitToPage(true);
//    groupAdditionalInfoColumns(pidcAttrSheet);
//
//  }
//
//  /**
//   * @param pidcAttrSheet
//   * @param length
//   */
//  private void groupAdditionalInfoColumns(final Sheet pidcAttrSheet) {
//
//    pidcAttrSheet.groupColumn(IExcelConstants.COLUMN_NUM_EIGHT, IExcelConstants.COLUMN_NUM_NINE);
//    pidcAttrSheet.setColumnGroupCollapsed(IExcelConstants.COLUMN_NUM_EIGHT, true);
//    pidcAttrSheet.groupColumn(IExcelConstants.COLUMN_NUM_THIRTEEN, IExcelConstants.COLUMN_NUM_FOURTEEN);
//    pidcAttrSheet.setColumnGroupCollapsed(IExcelConstants.COLUMN_NUM_THIRTEEN, true);
//
//  }
//
//  /**
//   * @param pidcAttrSheet
//   * @param sheetHeaders
//   * @param pidcRevComparator
//   * @return
//   */
//  private int populateData(final Sheet pidcAttrSheet, final String[] sheetHeaders,
//
//      final PidcRevisionComparator pidcRevComparator) {
//    final SortedSet<PidcRevisionCompareResult> revCompResults = pidcRevComparator.getResult();
//    int rowCount = ROW_STARTING_COUNT;
//    for (PidcRevisionCompareResult revCompResult : revCompResults) {
//      rowCount++;
//      constructRow(pidcAttrSheet, sheetHeaders, rowCount, revCompResult);
//      rowCount = populateChildRowsAndGroup(pidcAttrSheet, sheetHeaders, rowCount, revCompResult);
//    }
//    return rowCount;
//  }
//
//  /**
//   * @param pidcVariant1
//   * @param pidcAttrSheet
//   * @param sheetHeaders
//   * @param rowCount
//   * @param revCompResult
//   * @return
//   */
//  private int populateChildRowsAndGroup(final Sheet pidcAttrSheet, final String[] sheetHeaders, final int rCount,
//      final PidcRevisionCompareResult revCompResult) {
//    int rowCount = rCount;
//
//    List<PidcRevisionVariantCompareResult> pidcRevVariantCompareResults =
//        revCompResult.getPidcRevisionVariantCompareResults();
//
//    if ((pidcRevVariantCompareResults == null) || pidcRevVariantCompareResults.isEmpty()) {
//      return rowCount;
//    }
//
//    int compareRow = rowCount;
//    for (PidcRevisionVariantCompareResult varCompResult : pidcRevVariantCompareResults) {
//      rowCount = populateChildRowsAndGroup(pidcAttrSheet, sheetHeaders, rowCount, varCompResult);
//    }
//    // Group all variants for an attribute if subvariants are available for variants
//    if (compareRow < rowCount) {
//      compareRow += 1;
//      pidcAttrSheet.groupRow(compareRow, rowCount);
//      pidcAttrSheet.setRowGroupCollapsed(compareRow, true);
//    }
//    return rowCount;
//  }
//
//
//  /**
//   * @param pidcVariant1
//   * @param pidcAttrSheet
//   * @param sheetHeaders
//   * @param rowCount
//   * @param pidcRevVarCompResult
//   * @return
//   */
//  private int populateChildRowsAndGroup(final Sheet pidcAttrSheet, final String[] sheetHeaders, final int rCount,
//      final PidcRevisionVariantCompareResult pidcRevVarCompResult) {
//    int rowCount = rCount;
//    final Map<PIDCSubVariant, PidcCompareResult> subVarMap = pidcRevVarCompResult.getSubVariantMap();
//    if (pidcRevVarCompResult.getPidcVariant() != null) {
//      rowCount++;
//      constructRow(pidcAttrSheet, sheetHeaders, rowCount, pidcRevVarCompResult);
//      if ((subVarMap != null) && !subVarMap.isEmpty()) {
//        rowCount = createVarSubVarRows(pidcAttrSheet, sheetHeaders, rowCount, subVarMap);
//      }
//    }
//    return rowCount;
//  }
//
//  /**
//   * @param pidcAttrSheet
//   * @param sheetHeaders
//   * @param rowCount
//   * @param pidcRevVarCompResult
//   */
//  private void constructRow(final Sheet pidcAttrSheet, final String[] sheetHeaders, final int rowCount,
//      final PidcRevisionVariantCompareResult pidcRevVarCompResult) {
//    final Row row = ExcelUtil.getInstance().createExcelRow(pidcAttrSheet, rowCount);
//    fillColumnData(sheetHeaders, pidcRevVarCompResult, row);
//  }
//
//  /**
//   * @param sheetHeaders
//   * @param pidcRevVarCompResult
//   * @param row
//   */
//  private void fillColumnData(final String[] sheetHeaders, final PidcRevisionVariantCompareResult pidcRevVarCompResult,
//      final Row row) {
//    final Attribute attr = pidcRevVarCompResult.getAttribute();
//    final CompareColumns firstColumnSet = pidcRevVarCompResult.getFirstColumnSet();
//    final CompareColumns secondColumnSet = pidcRevVarCompResult.getSecondColumnSet();
//
//    for (int col = 0; col <= sheetHeaders.length; col++) {
//
//      if ((col < sheetHeaders.length) && sheetHeaders[col].equalsIgnoreCase(IExcelConstants.RH_VALUE)) {
//        fillValueColumn(pidcRevVarCompResult, row, firstColumnSet, secondColumnSet, col);
//      }
//      else if ((col < sheetHeaders.length) && sheetHeaders[col].equalsIgnoreCase(IExcelConstants.RH_USED)) {
//        fillUsedColumn(pidcRevVarCompResult, row, firstColumnSet, secondColumnSet, col);
//      }
//      else if ((col < sheetHeaders.length) && sheetHeaders[col].equalsIgnoreCase(IExcelConstants.RH_PART_NUMBER)) {
//        fillPartNumberColumn(pidcRevVarCompResult, row, firstColumnSet, secondColumnSet, col);
//      }
//      else if ((col < sheetHeaders.length) && sheetHeaders[col].equalsIgnoreCase(IExcelConstants.RH_SPEC_LINK)) {
//        fillSpecLinkColumn(pidcRevVarCompResult, row, firstColumnSet, secondColumnSet, col);
//      }
//      // ICDM-415
//      else if (((col == IExcelConstants.COLUMN_NUM_TEN) || (col == IExcelConstants.COLUMN_NUM_FIFTEEN)) &&
//          (sheetHeaders[col].equalsIgnoreCase(IExcelConstants.RH_ADDITIONAL_INFO_DESC))) {
//        fillDescColumn(pidcRevVarCompResult, row, firstColumnSet, secondColumnSet, col);
//      }
//      else if (col == sheetHeaders.length) {
//        // create invisible cell indicating attribute ID
//        ExcelUtil.getInstance().createCell(row, String.valueOf(attr.getAttributeID()), col, this.cellStyle);
//      }
//      else {
//        String result;
//        if (isDiffEnabled(pidcRevVarCompResult, col)) {
//          // fetches Data for column Diff
//          result = IExcelConstants.DIFF_MARKER;
//        }
//        else {
//          // fetches Data for columns SuperGroup,Group,Name,Description
//          result = colTxtFromAttr(col, attr, pidcRevVarCompResult.getPidcVariant().getVariantName(), "");
//        }
//        ExcelUtil.getInstance().createCell(row, result, col, this.cellStyle);
//      }
//
//    }
//  }
//
//  /**
//   * @param pidcRevVarCompResult
//   * @param firstColumnSet
//   * @param secondColumnSet
//   * @param col
//   * @return
//   */
//  private boolean isDiffEnabled(final PidcRevisionVariantCompareResult pidcRevVarCompResult, final int col) {
//    return (col == IExcelConstants.COLUMN_NUM_SIXTEEN) && (pidcRevVarCompResult.isDiff());
//  }
//
//
//  /**
//   * @param pidcRevVarCompResult
//   * @param row
//   * @param firstColumnSet
//   * @param secondColumnSet
//   * @param col
//   */
//  private void fillUsedColumn(final PidcRevisionVariantCompareResult pidcRevVarCompResult, final Row row,
//      final CompareColumns firstColumnSet, final CompareColumns secondColumnSet, final int col) {
//    String usedValue = IExcelConstants.EMPTY_STRING;
//    if (col == IExcelConstants.COLUMN_NUM_SIX) {
//      usedValue = firstColumnSet.getUsedFlag();
//    }
//    else if (col == IExcelConstants.COLUMN_NUM_ELEVEN) {
//      usedValue = secondColumnSet.getUsedFlag();
//    }
//    CellStyle localCellStyle = this.cellStyle;
//    if (pidcRevVarCompResult.isAttrUsedFlagDiff()) {
//      if (!usedValue.equals(ApicConstants.PROJ_ATTR_USED_FLAG.NOT_DEFINED.getUiType())) {
//        if ((col == IExcelConstants.COLUMN_NUM_SIX) &&
//            ((secondColumnSet.getUsedFlag().equals(ApicConstants.PROJ_ATTR_USED_FLAG.NOT_DEFINED.getUiType())) ||
//                "".equals(secondColumnSet.getUsedFlag()))) {
//          localCellStyle = this.redCellStyle;
//        }
//        if (col == IExcelConstants.COLUMN_NUM_ELEVEN) {
//          localCellStyle = this.redCellStyle;
//        }
//      }
//      else {
//        if ((col == IExcelConstants.COLUMN_NUM_SIX) && "".equals(secondColumnSet.getUsedFlag())) {// getUsedFlag.equals("")
//                                                                                                  // in column
//          localCellStyle = this.redCellStyle;
//        }
//        if ((col == IExcelConstants.COLUMN_NUM_ELEVEN) && "".equals(firstColumnSet.getUsedFlag())) {
//          localCellStyle = this.redCellStyle;
//        }
//      }
//    }
//
//    // Added to avoid neighbour cell text overlapping used cell
//    if (usedValue == null) {
//      usedValue = IExcelConstants.EMPTY_STRING;
//    }
//    //
//    ExcelUtil.getInstance().createCell(row, usedValue, col, localCellStyle);
//  }
//
//  /**
//   * @param pidcRevVarCompResult
//   * @param row
//   * @param firstColumnSet
//   * @param secondColumnSet
//   * @param col
//   */
//  private void fillValueColumn(final PidcRevisionVariantCompareResult pidcRevVarCompResult, final Row row,
//      final CompareColumns firstColumnSet, final CompareColumns secondColumnSet, final int col) {
//    String result = IExcelConstants.EMPTY_STRING;
//    if (col == IExcelConstants.COLUMN_NUM_SEVEN) {
//      result = firstColumnSet.getAttrValue();
//    }
//    else if (col == IExcelConstants.COLUMN_NUM_TWELEVE) {
//      result = secondColumnSet.getAttrValue();
//    }
//    final boolean isRed = isRedFontEnabled(pidcRevVarCompResult, firstColumnSet, secondColumnSet, col, result,
//        IExcelConstants.COLUMN_NUM_SEVEN, IExcelConstants.COLUMN_NUM_TWELEVE);
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
//   * @param pidcRevVarCompResult
//   * @param firstColumnSet
//   * @param secondColumnSet
//   * @param col
//   * @param result
//   * @param index1
//   * @param index2
//   * @return
//   */
//  private boolean isRedFontEnabled(final PidcRevisionVariantCompareResult pidcRevVarCompResult,
//      final CompareColumns firstColumnSet, final CompareColumns secondColumnSet, final int col, final String result,
//      final int index1, final int index2) {
//    boolean isRed = false;
//    if ((index1 == IExcelConstants.COLUMN_NUM_SEVEN) && (index2 == IExcelConstants.COLUMN_NUM_TWELEVE) &&
//        pidcRevVarCompResult.isAttrValueDiff() && (result != null) && !result.trim().isEmpty()) {
//      if (((col == index1) && isSecondColValValid(secondColumnSet)) || (col == index2)) {
//        isRed = true;
//      }
//    }
//    if ((index1 == IExcelConstants.COLUMN_NUM_EIGHT) && (index2 == IExcelConstants.COLUMN_NUM_THIRTEEN) &&
//        pidcRevVarCompResult.isPartNumberDiff() && (result != null) && !result.trim().isEmpty()) {
//      if (((col == index1) && isSecondColPartNumValid(secondColumnSet)) || (col == index2)) {
//        isRed = true;
//      }
//
//    }
//    if ((index1 == IExcelConstants.COLUMN_NUM_NINE) && (index2 == IExcelConstants.COLUMN_NUM_FOURTEEN) &&
//        pidcRevVarCompResult.isSpecLinkDiff() && (result != null) && !result.trim().isEmpty()) {
//      if (((col == index1) && isSecondColSpecLinkValid(secondColumnSet)) || (col == index2)) {
//        isRed = true;
//      }
//    }
//    if ((index1 == IExcelConstants.COLUMN_NUM_TEN) && (index2 == IExcelConstants.COLUMN_NUM_FIFTEEN) &&
//        pidcRevVarCompResult.isDescDiff() && (result != null) && !result.trim().isEmpty()) {
//      if (((col == index1) && isSecondColDescValid(secondColumnSet)) || (col == index2)) {
//        isRed = true;
//      }
//    }
//    // When both are subvariants with difference in attr value both cells are shown in red color
//    if (areBothSubVariants(firstColumnSet, secondColumnSet) && pidcRevVarCompResult.isAttrValueDiff()) {
//      isRed = true;
//    }
//    return isRed;
//  }
//
//  /**
//   * @param pidcRevVarCompResult
//   * @param row
//   * @param firstColumnSet
//   * @param secondColumnSet
//   * @param col
//   */
//  private void fillPartNumberColumn(final PidcRevisionVariantCompareResult pidcRevVarCompResult, final Row row,
//      final CompareColumns firstColumnSet, final CompareColumns secondColumnSet, final int col) {
//    String result = IExcelConstants.EMPTY_STRING;
//    if (col == IExcelConstants.COLUMN_NUM_EIGHT) {
//      result = firstColumnSet.getPartNumber();
//    }
//    else if (col == IExcelConstants.COLUMN_NUM_THIRTEEN) {
//      result = secondColumnSet.getPartNumber();
//    }
//    final boolean isRed = isRedFontEnabled(pidcRevVarCompResult, firstColumnSet, secondColumnSet, col, result,
//        IExcelConstants.COLUMN_NUM_EIGHT, IExcelConstants.COLUMN_NUM_THIRTEEN);
//
//    createPidcCompareCol(row, col, result, isRed);
//  }
//
//  /**
//   * @param pidcRevVarCompResult
//   * @param row
//   * @param firstColumnSet
//   * @param secondColumnSet
//   * @param col
//   */
//  private void fillSpecLinkColumn(final PidcRevisionVariantCompareResult pidcRevVarCompResult, final Row row,
//      final CompareColumns firstColumnSet, final CompareColumns secondColumnSet, final int col) {
//    String result = IExcelConstants.EMPTY_STRING;
//    if (col == IExcelConstants.COLUMN_NUM_NINE) {
//      result = firstColumnSet.getSpecLink();
//    }
//    else if (col == IExcelConstants.COLUMN_NUM_FOURTEEN) {
//      result = secondColumnSet.getSpecLink();
//    }
//    final boolean isRed = isRedFontEnabled(pidcRevVarCompResult, firstColumnSet, secondColumnSet, col, result,
//        IExcelConstants.COLUMN_NUM_NINE, IExcelConstants.COLUMN_NUM_FOURTEEN);
//
//    createPidcCompareHyperLinkCol(row, col, result, isRed);
//  }
//
//  /**
//   * @param pidcRevVarCompResult
//   * @param row
//   * @param firstColumnSet
//   * @param secondColumnSet
//   * @param col
//   */
//  private void fillDescColumn(final PidcRevisionVariantCompareResult pidcRevVarCompResult, final Row row,
//      final CompareColumns firstColumnSet, final CompareColumns secondColumnSet, final int col) {
//    String result = IExcelConstants.EMPTY_STRING;
//    if (col == IExcelConstants.COLUMN_NUM_TEN) {
//      result = firstColumnSet.getDesc();
//    }
//    else if (col == IExcelConstants.COLUMN_NUM_FIFTEEN) {
//      result = secondColumnSet.getDesc();
//    }
//    final boolean isRed = isRedFontEnabled(pidcRevVarCompResult, firstColumnSet, secondColumnSet, col, result,
//        IExcelConstants.COLUMN_NUM_TEN, IExcelConstants.COLUMN_NUM_FIFTEEN);
//
//    createPidcCompareCol(row, col, result, isRed);
//  }
//
//  /**
//   * @param pidcAttrSheet
//   * @param sheetHeaders
//   * @param rCount
//   * @param pidcVariant
//   * @param subVarCompResult
//   * @return
//   */
//  private int createVarSubVarRows(final Sheet pidcAttrSheet, final String[] sheetHeaders, final int rCount,
//      final Map<PIDCSubVariant, PidcCompareResult> subVarCompResult) {
//    int fromRow;
//    int rowCount = rCount;
//    if ((subVarCompResult != null) && !subVarCompResult.isEmpty()) {
//      fromRow = rowCount;
//      int mapSizeCount = 1;
//      for (PIDCSubVariant subVariant : subVarCompResult.keySet()) {
//        final PidcCompareResult pidcCompResult2 = subVarCompResult.get(subVariant);
//        rowCount++;
//        constructSubVarRow(pidcAttrSheet, sheetHeaders, rowCount, pidcCompResult2, subVariant.getSubVariantName());
//        // Group all subvariants belonging to a variant for an attribute
//        if ((fromRow != 0) && (mapSizeCount == subVarCompResult.size()) && (pidcCompResult2 != null)) {
//          pidcAttrSheet.groupRow(fromRow + 1, rowCount);
//          pidcAttrSheet.setRowGroupCollapsed(fromRow + 1, true);
//        }
//        mapSizeCount++;
//      }
//    }
//    return rowCount;
//  }
//
//  /**
//   * @param pidcVer1
//   * @param pidcVer2
//   * @return
//   */
//  private Sheet initializeWorksheet(final PIDCVersion pidcVer1, final PIDCVersion pidcVer2) {
//    final String sheetName = pidcVer1.getName().replaceAll("[^a-zA-Z0-9]+", "_") + " Vs " +
//        pidcVer2.getName().replaceAll("[^a-zA-Z0-9]+", "_");
//    final Sheet pidcAttrSheet = this.workBook.createSheet(sheetName);
//    this.workBook.setActiveSheet(this.workBook.getSheetIndex(pidcAttrSheet));
//    return pidcAttrSheet;
//  }
//
//  private void constructRow(final Sheet pidcAttrSheet, final String[] sheetHeaders, final int rowCount,
//      final PidcRevisionCompareResult revCompResult) {
//    final Row row = ExcelUtil.getInstance().createExcelRow(pidcAttrSheet, rowCount);
//    fillColumnData(sheetHeaders, revCompResult, row);
//  }
//
//  /**
//   * Creates the sub variant row for the sheet
//   *
//   * @param workbook
//   * @param pidcAttrSheet
//   * @param sheetHeaders
//   * @param rowCount
//   * @param pidcCompareResult
//   * @param variant1Name
//   */
//  private void constructSubVarRow(final Sheet pidcAttrSheet, final String[] sheetHeaders, final int rowCount,
//      final PidcCompareResult pidcCompareResult, final String subVariantName) {
//    final Row row = ExcelUtil.getInstance().createExcelRow(pidcAttrSheet, rowCount);
//    fillColumnData(sheetHeaders, pidcCompareResult, row, subVariantName);
//  }
//
//  private void fillColumnData(final String[] sheetHeader, final PidcCompareResult pidcCompareResult, final Row row,
//      final String subVariantName) {
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
//      else if ((col == IExcelConstants.COLUMN_NUM_TEN) || ((col == IExcelConstants.COLUMN_NUM_FIFTEEN) &&
//          (sheetHeader[col].equalsIgnoreCase(IExcelConstants.RH_ADDITIONAL_INFO_DESC)))) {
//        fillDescColumn(pidcCompareResult, row, firstColumnSet, secondColumnSet, col);
//      }
//      else if (col == sheetHeader.length) {
//        // create invisible cell indicating attribute ID
//        ExcelUtil.getInstance().createCell(row, String.valueOf(attr.getAttributeID()), col, this.cellStyle);
//      }
//      else {
//        String result = IExcelConstants.EMPTY_STRING;
//        if (col == IExcelConstants.COLUMN_NUM_SIXTEEN) {
//          if (pidcCompareResult.isDiff()) {
//            // fetches Data for column Diff
//            result = IExcelConstants.DIFF_MARKER;
//          }
//        }
//        else {
//          // fetches Data for columns SuperGroup,Group,Name,Description
//          result = colTxtFromAttr(col, attr, "", subVariantName);
//        }
//        ExcelUtil.getInstance().createCell(row, result, col, this.cellStyle);
//      }
//
//    }
//  }
//
//  /**
//   * @param pidcCompareResult
//   * @param row
//   * @param firstColumnSet
//   * @param secondColumnSet
//   * @param col
//   */
//  private void fillPartNumberColumn(final PidcCompareResult pidcCompareResult, final Row row,
//      final CompareColumns firstColumnSet, final CompareColumns secondColumnSet, final int col) {
//    String result = IExcelConstants.EMPTY_STRING;
//    if (col == IExcelConstants.COLUMN_NUM_EIGHT) {
//      result = firstColumnSet.getPartNumber();
//    }
//    else if (col == IExcelConstants.COLUMN_NUM_THIRTEEN) {
//      result = secondColumnSet.getPartNumber();
//    }
//    boolean isRed = false;
//    if (pidcCompareResult.isPartNumberDiff() && (result != null) && !result.trim().isEmpty()) {
//      if ((col == IExcelConstants.COLUMN_NUM_EIGHT) && isSecondColPartNumValid(secondColumnSet)) {
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
//   * Fills the Additional info desc column in PID card compare sheet. Text Color is calculated based on difference
//   * between desc of the two PID cards. If attribute is not existing due to dependency attribute,PartNumber is marked as
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
//    if (col == IExcelConstants.COLUMN_NUM_TEN) {
//      result = firstColumnSet.getDesc();
//    }
//    else if (col == IExcelConstants.COLUMN_NUM_FIFTEEN) {
//      result = secondColumnSet.getDesc();
//    }
//    boolean isRed = false;
//    if (pidcCompareResult.isDescDiff() && (result != null) && !result.trim().isEmpty()) {
//      if ((col == IExcelConstants.COLUMN_NUM_TEN) && isSecondColDescValid(secondColumnSet)) {
//        isRed = true;
//      }
//      if (col == IExcelConstants.COLUMN_NUM_FIFTEEN) {
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
//    if (col == IExcelConstants.COLUMN_NUM_NINE) {
//      result = firstColumnSet.getSpecLink();
//    }
//    else if (col == IExcelConstants.COLUMN_NUM_FOURTEEN) {
//      result = secondColumnSet.getSpecLink();
//    }
//    boolean isRed = false;
//    if (pidcCompareResult.isSpecLinkDiff() && (result != null) && !result.trim().isEmpty()) {
//      if ((col == IExcelConstants.COLUMN_NUM_NINE) && isSecondColSpecLinkValid(secondColumnSet)) {
//        isRed = true;
//      }
//      if (col == IExcelConstants.COLUMN_NUM_FOURTEEN) {
//        isRed = true;
//      }
//    }
//    createPidcCompareHyperLinkCol(row, col, result, isRed);
//
//  }
//
//  /**
//   * @param pidcCompareResult
//   * @param row
//   * @param firstColumnSet
//   * @param secondColumnSet
//   * @param col
//   */
//  private void fillValueColumn(final PidcCompareResult pidcCompareResult, final Row row,
//      final CompareColumns firstColumnSet, final CompareColumns secondColumnSet, final int col) {
//    String result = IExcelConstants.EMPTY_STRING;
//    if (col == IExcelConstants.COLUMN_NUM_SEVEN) {
//      result = firstColumnSet.getAttrValue();
//    }
//    else if (col == IExcelConstants.COLUMN_NUM_TWELEVE) {
//      result = secondColumnSet.getAttrValue();
//    }
//    boolean isRed = false;
//    if (pidcCompareResult.isAttrValueDiff() && (result != null) && !result.trim().isEmpty()) {
//      if ((col == IExcelConstants.COLUMN_NUM_SEVEN) && isSecondColValValid(secondColumnSet)) {
//        isRed = true;
//      }
//      if (col == IExcelConstants.COLUMN_NUM_TWELEVE) {
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
//   * @param pidcCompareResult
//   * @param row
//   * @param firstColumnSet
//   * @param secondColumnSet
//   * @param col
//   */
//  private void fillUsedColumn(final PidcCompareResult pidcCompareResult, final Row row,
//      final CompareColumns firstColumnSet, final CompareColumns secondColumnSet, final int col) {
//    String usedValue = IExcelConstants.EMPTY_STRING;
//    if (col == IExcelConstants.COLUMN_NUM_SIX) {
//      usedValue = firstColumnSet.getUsedFlag();
//    }
//    else if (col == IExcelConstants.COLUMN_NUM_ELEVEN) {
//      usedValue = secondColumnSet.getUsedFlag();
//    }
//    CellStyle localCellStyle = this.cellStyle;
//    if (pidcCompareResult.isAttrUsedFlagDiff()) {
//      if (!usedValue.equals(ApicConstants.PROJ_ATTR_USED_FLAG.NOT_DEFINED.getUiType())) {
//        if ((col == IExcelConstants.COLUMN_NUM_SIX) &&
//            (secondColumnSet.getUsedFlag().equals(ApicConstants.PROJ_ATTR_USED_FLAG.NOT_DEFINED.getUiType()) ||
//                "".equals(secondColumnSet.getUsedFlag()))) {
//          localCellStyle = this.redCellStyle;
//        }
//        if (col == IExcelConstants.COLUMN_NUM_ELEVEN) {
//          localCellStyle = this.redCellStyle;
//        }
//      }
//      else {
//        if ((col == IExcelConstants.COLUMN_NUM_SIX) && "".equals(secondColumnSet.getUsedFlag())) {// getUsedFlag.equals("")
//          // in column
//          localCellStyle = this.redCellStyle;
//        }
//        if ((col == IExcelConstants.COLUMN_NUM_ELEVEN) && "".equals(firstColumnSet.getUsedFlag())) {
//          localCellStyle = this.redCellStyle;
//        }
//      }
//    }
//    ExcelUtil.getInstance().createCell(row, usedValue, col, localCellStyle);
//  }
//
//  /**
//   * @param sheetHeaders
//   * @param revCompResult
//   * @param row
//   */
//  private void fillColumnData(final String[] sheetHeaders, final PidcRevisionCompareResult revCompResult,
//      final Row row) {
//    final Attribute attr = revCompResult.getAttribute();
//    final CompareColumns firstColumnSet = revCompResult.getFirstColumnSet();
//    final CompareColumns secondColumnSet = revCompResult.getSecondColumnSet();
//
//    for (int col = 0; col <= sheetHeaders.length; col++) {
//
//      if ((col < sheetHeaders.length) && sheetHeaders[col].equalsIgnoreCase(IExcelConstants.RH_VALUE)) {
//        fillValueColumn(revCompResult, row, firstColumnSet, secondColumnSet, col);
//      }
//      else if ((col < sheetHeaders.length) && sheetHeaders[col].equalsIgnoreCase(IExcelConstants.RH_USED)) {
//        fillUsedColumn(revCompResult, row, firstColumnSet, secondColumnSet, col);
//      }
//      else if ((col < sheetHeaders.length) && sheetHeaders[col].equalsIgnoreCase(IExcelConstants.RH_PART_NUMBER)) {
//        fillPartNumberColumn(revCompResult, row, firstColumnSet, secondColumnSet, col);
//      }
//      else if ((col < sheetHeaders.length) && sheetHeaders[col].equalsIgnoreCase(IExcelConstants.RH_SPEC_LINK)) {
//        fillSpecLinkColumn(revCompResult, row, firstColumnSet, secondColumnSet, col);
//      }
//      // ICDM-415
//      else if ((col == IExcelConstants.COLUMN_NUM_TEN) || ((col == IExcelConstants.COLUMN_NUM_FIFTEEN) &&
//          (sheetHeaders[col].equalsIgnoreCase(IExcelConstants.RH_ADDITIONAL_INFO_DESC)))) {
//        fillDescColumn(revCompResult, row, firstColumnSet, secondColumnSet, col);
//      }
//      else if (col == sheetHeaders.length) {
//        // create invisible cell indicating attribute ID
//        ExcelUtil.getInstance().createCell(row, String.valueOf(attr.getAttributeID()), col, this.cellStyle);
//      }
//      else {
//        String result;
//        if (isDiffEnabled(revCompResult, col)) {
//          // fetches Data for column Diff
//          result = IExcelConstants.DIFF_MARKER;
//        }
//        else {
//          // fetches Data for columns SuperGroup,Group,Name,Description
//          result = colTxtFromAttr(col, attr, "", "");
//        }
//        ExcelUtil.getInstance().createCell(row, result, col, this.cellStyle);
//      }
//
//    }
//  }
//
//  /**
//   * @param revCompResult
//   * @param firstColumnSet
//   * @param secondColumnSet
//   * @param col
//   * @return
//   */
//  private boolean isDiffEnabled(final PidcRevisionCompareResult revCompResult, final int col) {
//    return (col == IExcelConstants.COLUMN_NUM_SIXTEEN) && (revCompResult.isDiff());
//  }
//
//
//  /**
//   * Column text of Project id card table viewer from project attribute
//   *
//   * @param columnIndex
//   * @param variant1Name
//   * @param viwvAttrProjectattr
//   * @return String
//   */
//  private String colTxtFromAttr(final int columnIndex, final Attribute attr, final String variantName,
//      final String subVariantName) {
//    String result;
//
//    switch (columnIndex) {
//      case VAR_INDEX:
//        result = variantName;
//        break;
//
//      case SUB_VAR_INDEX:
//        result = subVariantName;
//        break;
//
//      case ATTR_SUPER_GROUP_INDEX:
//        result = attr.getAttributeGroup().getSuperGroup().getName();
//        break;
//
//      case ATTR_GROUP_NAME_INDEX:
//        result = attr.getAttributeGroup().getName();
//        break;
//
//      case ATTR_NAME_INDEX:
//        result = attr.getAttributeName();
//        break;
//
//      case ATTR_DESC_INDEX:
//        result = attr.getAttributeDesc();
//        break;
//
//      default:
//        result = IExcelConstants.EMPTY_STRING;
//
//    }
//    return result;
//  }
//
//
//  /**
//   * @param revCompResult
//   * @param row
//   * @param firstColumnSet
//   * @param secondColumnSet
//   * @param col
//   */
//  private void fillUsedColumn(final PidcRevisionCompareResult revCompResult, final Row row,
//      final CompareColumns firstColumnSet, final CompareColumns secondColumnSet, final int col) {
//    String usedValue = IExcelConstants.EMPTY_STRING;
//    if (col == IExcelConstants.COLUMN_NUM_SIX) {
//      usedValue = firstColumnSet.getUsedFlag();
//    }
//    else if (col == IExcelConstants.COLUMN_NUM_ELEVEN) {
//      usedValue = secondColumnSet.getUsedFlag();
//    }
//    CellStyle localCellStyle = this.cellStyle;
//    if (revCompResult.isAttrUsedFlagDiff() &&
//        !usedValue.equals(ApicConstants.PROJ_ATTR_USED_FLAG.NOT_DEFINED.getUiType())) {
//      if ((col == IExcelConstants.COLUMN_NUM_SIX) &&
//          (secondColumnSet.getUsedFlag().equals(ApicConstants.PROJ_ATTR_USED_FLAG.NOT_DEFINED.getUiType()))) {
//        localCellStyle = this.redCellStyle;
//      }
//      if (col == IExcelConstants.COLUMN_NUM_ELEVEN) {
//        localCellStyle = this.redCellStyle;
//      }
//    }
//
//    // Added to avoid neighbour cell text overlapping used cell
//    if (usedValue == null) {
//      usedValue = IExcelConstants.EMPTY_STRING;
//    }
//    //
//    ExcelUtil.getInstance().createCell(row, usedValue, col, localCellStyle);
//  }
//
//  /**
//   * Fills the Value column in PID card compare sheet. Text Color is calculated based on difference between values of
//   * the two PID cards. If attribute is not existing due to dependency attribute,value is marked as empty string. If
//   * attribute value is null,it is marked as '-'.
//   *
//   * @param pidcAttrSheet
//   * @param pidcRevCompResult
//   * @param row
//   * @param attr
//   * @param firstColumnSet
//   * @param secondColumnSet
//   * @param col
//   */
//  private void fillValueColumn(final PidcRevisionCompareResult revCompResult, final Row row,
//      final CompareColumns firstColumnSet, final CompareColumns secondColumnSet, final int col) {
//    String result = IExcelConstants.EMPTY_STRING;
//    if (col == IExcelConstants.COLUMN_NUM_SEVEN) {
//      result = firstColumnSet.getAttrValue();
//    }
//    else if (col == IExcelConstants.COLUMN_NUM_TWELEVE) {
//      result = secondColumnSet.getAttrValue();
//    }
//    final boolean isRed = isRedFontEnabled(revCompResult, firstColumnSet, secondColumnSet, col, result,
//        IExcelConstants.COLUMN_NUM_SEVEN, IExcelConstants.COLUMN_NUM_TWELEVE);
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
//   * @param revCompResult
//   * @param row
//   * @param firstColumnSet
//   * @param secondColumnSet
//   * @param col
//   */
//  private void fillPartNumberColumn(final PidcRevisionCompareResult revCompResult, final Row row,
//      final CompareColumns firstColumnSet, final CompareColumns secondColumnSet, final int col) {
//    String result = IExcelConstants.EMPTY_STRING;
//    if (col == IExcelConstants.COLUMN_NUM_EIGHT) {
//      result = firstColumnSet.getPartNumber();
//    }
//    else if (col == IExcelConstants.COLUMN_NUM_THIRTEEN) {
//      result = secondColumnSet.getPartNumber();
//    }
//    final boolean isRed = isRedFontEnabled(revCompResult, firstColumnSet, secondColumnSet, col, result,
//        IExcelConstants.COLUMN_NUM_EIGHT, IExcelConstants.COLUMN_NUM_THIRTEEN);
//
//    createPidcCompareCol(row, col, result, isRed);
//  }
//
//  /**
//   * @param revCompResult
//   * @param row
//   * @param firstColumnSet
//   * @param secondColumnSet
//   * @param col
//   */
//  private void fillSpecLinkColumn(final PidcRevisionCompareResult revCompResult, final Row row,
//      final CompareColumns firstColumnSet, final CompareColumns secondColumnSet, final int col) {
//    String result = IExcelConstants.EMPTY_STRING;
//    if (col == IExcelConstants.COLUMN_NUM_NINE) {
//      result = firstColumnSet.getSpecLink();
//    }
//    else if (col == IExcelConstants.COLUMN_NUM_FOURTEEN) {
//      result = secondColumnSet.getSpecLink();
//    }
//    final boolean isRed = isRedFontEnabled(revCompResult, firstColumnSet, secondColumnSet, col, result,
//        IExcelConstants.COLUMN_NUM_NINE, IExcelConstants.COLUMN_NUM_FOURTEEN);
//
//    createPidcCompareHyperLinkCol(row, col, result, isRed);
//  }
//
//  /**
//   * @param revCompResult
//   * @param row
//   * @param firstColumnSet
//   * @param secondColumnSet
//   * @param col
//   */
//  private void fillDescColumn(final PidcRevisionCompareResult revCompResult, final Row row,
//      final CompareColumns firstColumnSet, final CompareColumns secondColumnSet, final int col) {
//    String result = IExcelConstants.EMPTY_STRING;
//    if (col == IExcelConstants.COLUMN_NUM_TEN) {
//      result = firstColumnSet.getDesc();
//    }
//    else if (col == IExcelConstants.COLUMN_NUM_FIFTEEN) {
//      result = secondColumnSet.getDesc();
//    }
//    final boolean isRed = isRedFontEnabled(revCompResult, firstColumnSet, secondColumnSet, col, result,
//        IExcelConstants.COLUMN_NUM_TEN, IExcelConstants.COLUMN_NUM_FIFTEEN);
//
//    createPidcCompareCol(row, col, result, isRed);
//  }
//
//  /**
//   * @param revCompResult
//   * @param firstColumnSet
//   * @param secondColumnSet
//   * @param col
//   * @param result
//   * @param index1
//   * @param index2
//   * @return
//   */
//  private boolean isRedFontEnabled(final PidcRevisionCompareResult revCompResult, final CompareColumns firstColumnSet,
//      final CompareColumns secondColumnSet, final int col, final String result, final int index1, final int index2) {
//    boolean isRed = false;
//    if ((index1 == IExcelConstants.COLUMN_NUM_SEVEN) && (index2 == IExcelConstants.COLUMN_NUM_TWELEVE) &&
//        revCompResult.isAttrValueDiff() && (result != null) && !result.trim().isEmpty()) {
//      if (((col == index1) && isSecondColValValid(secondColumnSet)) || (col == index2)) {
//        isRed = true;
//      }
//    }
//    if ((index1 == IExcelConstants.COLUMN_NUM_EIGHT) && (index2 == IExcelConstants.COLUMN_NUM_THIRTEEN) &&
//        revCompResult.isPartNumberDiff() && (result != null) && !result.trim().isEmpty()) {
//      if (((col == index1) && isSecondColPartNumValid(secondColumnSet)) || (col == index2)) {
//        isRed = true;
//      }
//    }
//    if ((index1 == IExcelConstants.COLUMN_NUM_NINE) && (index2 == IExcelConstants.COLUMN_NUM_FOURTEEN) &&
//        revCompResult.isSpecLinkDiff() && (result != null) && !result.trim().isEmpty()) {
//      if (((col == index1) && isSecondColSpecLinkValid(secondColumnSet)) || (col == index2)) {
//        isRed = true;
//      }
//    }
//    if ((index1 == IExcelConstants.COLUMN_NUM_TEN) && (index2 == IExcelConstants.COLUMN_NUM_FIFTEEN) &&
//        revCompResult.isDescDiff() && (result != null) && !result.trim().isEmpty()) {
//      if (((col == index1) && isSecondColDescValid(secondColumnSet)) || (col == index2)) {
//        isRed = true;
//      }
//    }
//    // When both are variants both cells are shown in red color
//    if (areBothVariants(firstColumnSet, secondColumnSet) && revCompResult.isAttrValueDiff()) {
//      isRed = true;
//    }
//    return isRed;
//  }
//
//  /**
//   * @param firstColumnSet
//   * @param secondColumnSet
//   * @return
//   */
//  private boolean areBothSubVariants(final CompareColumns firstColumnSet, final CompareColumns secondColumnSet) {
//    return (firstColumnSet.getAttrValue() != null) && (secondColumnSet.getAttrValue() != null) &&
//        firstColumnSet.getAttrValue().equals(ApicConstants.SUB_VARIANT_ATTR_DISPLAY_NAME) &&
//        secondColumnSet.getAttrValue().equals(ApicConstants.SUB_VARIANT_ATTR_DISPLAY_NAME);
//  }
//
//  private boolean areBothVariants(final CompareColumns firstColumnSet, final CompareColumns secondColumnSet) {
//    return (firstColumnSet.getAttrValue() != null) && (secondColumnSet.getAttrValue() != null) &&
//        firstColumnSet.getAttrValue().equals(ApicConstants.VARIANT_ATTR_DISPLAY_NAME) &&
//        secondColumnSet.getAttrValue().equals(ApicConstants.VARIANT_ATTR_DISPLAY_NAME);
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
//   * Sets the columns widths for the sheets
//   *
//   * @param pidcAttrSheet
//   */
//  private void setColumnWidths(final Sheet pidcAttrSheet) {
//    pidcAttrSheet.setColumnWidth(IExcelConstants.COLUMN_NUM_ZERO, VAR_COLUMN_WIDTH);
//    pidcAttrSheet.setColumnWidth(IExcelConstants.COLUMN_NUM_ONE, SVAR_COLUMN_WIDTH);
//    pidcAttrSheet.setColumnWidth(IExcelConstants.COLUMN_NUM_TWO, SGRP_COLUMN_WIDTH);
//    pidcAttrSheet.setColumnWidth(IExcelConstants.COLUMN_NUM_THREE, GRP_COLUMN_WIDTH);
//    pidcAttrSheet.setColumnWidth(IExcelConstants.COLUMN_NUM_FOUR, NAME_COLUMN_WIDTH);
//    pidcAttrSheet.setColumnWidth(IExcelConstants.COLUMN_NUM_FIVE, DESC_COLUMN_WIDTH);
//    pidcAttrSheet.setColumnWidth(IExcelConstants.COLUMN_NUM_SIX, USED_COLUMN_WIDTH);
//    pidcAttrSheet.setColumnWidth(IExcelConstants.COLUMN_NUM_SEVEN, VAL_COLUMN_WIDTH);
//    pidcAttrSheet.setColumnWidth(IExcelConstants.COLUMN_NUM_EIGHT, PART_NUM_COLUMN_WIDTH);
//    pidcAttrSheet.setColumnWidth(IExcelConstants.COLUMN_NUM_NINE, SPEC_LINK_COLUMN_WIDTH);
//    pidcAttrSheet.setColumnWidth(IExcelConstants.COLUMN_NUM_TEN, DESCRIP_COLUMN_WIDTH);
//    pidcAttrSheet.setColumnWidth(IExcelConstants.COLUMN_NUM_ELEVEN, USED_COLUMN_WIDTH);
//    pidcAttrSheet.setColumnWidth(IExcelConstants.COLUMN_NUM_TWELEVE, VAL_COLUMN_WIDTH);
//    pidcAttrSheet.setColumnWidth(IExcelConstants.COLUMN_NUM_THIRTEEN, PART_NUM_COLUMN_WIDTH);
//    pidcAttrSheet.setColumnWidth(IExcelConstants.COLUMN_NUM_FOURTEEN, SPEC_LINK_COLUMN_WIDTH);
//    pidcAttrSheet.setColumnWidth(IExcelConstants.COLUMN_NUM_FIFTEEN, DESCRIP_COLUMN_WIDTH);
//    pidcAttrSheet.setColumnWidth(IExcelConstants.COLUMN_NUM_SIXTEEN, DIFF_COLUMN_WIDTH);
//  }
//
//  /**
//   * Create value column in compare project id cards attribute sheet
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
//
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
//    final String autoFilterRange = "A3:Q" + rowCount;
//    pidcAttrSheet.setAutoFilter(CellRangeAddress.valueOf(autoFilterRange));
//  }
//
//}
