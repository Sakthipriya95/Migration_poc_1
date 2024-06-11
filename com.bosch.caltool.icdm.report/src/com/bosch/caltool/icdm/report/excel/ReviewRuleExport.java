/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.report.excel;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.SortedSet;

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
import com.bosch.caltool.icdm.client.bo.cdr.ParameterDataProvider;
import com.bosch.caltool.icdm.client.bo.general.CurrentUserBO;
import com.bosch.caltool.icdm.common.bo.a2l.RuleMaturityLevel;
import com.bosch.caltool.icdm.common.bo.cdr.ReviewRuleUtil;
import com.bosch.caltool.icdm.common.util.Activator;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.cdr.IParameter;
import com.bosch.caltool.icdm.model.cdr.IParameterAttribute;
import com.bosch.caltool.icdm.model.cdr.ParamCollection;
import com.bosch.caltool.icdm.model.cdr.ReviewRule;
import com.bosch.caltool.icdm.model.cdr.RuleSetParameter;
import com.bosch.caltool.icdm.report.common.ExcelCommon;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * This class used to create new Excel file and export the Review Rule/ Rule Set /Component Package
 *
 * @author svj7cob
 */
// ICDM-1539, Excel Export for Review Rule
public class ReviewRuleExport<D extends IParameterAttribute, P extends IParameter> {

  /**
   * comment width
   */
  private static final int COMMENT_WIDTH = 4;
  /**
   * comment height
   */
  private static final int COMMENT_HEIGHT = 5;
  /**
   * ready for series column width
   */
  private static final int READY_FOR_SERIES_COL_WIDTH = 3000;
  /**
   * exact match column width
   */
  private static final int EXACT_MATCH_COL_WIDTH = 1800;
  /**
   * upper limit column width
   */
  private static final int UPPER_LIMIT_COL_WIDTH = 2800;
  /**
   * lower limit column width
   */
  private static final int LOWER_LIMIT_COL_WIDTH = 2800;
  /**
   * long name width
   */
  private static final int LONG_NAME_COL_WIDTH = 11000;
  // ICDM-2537
  /**
   * long name width
   */
  private static final int REMARKS_COL_WIDTH = 9000;
  /**
   * The progress monitor instance to which progess information is to be set.
   */
  private final IProgressMonitor monitor;
  private final ParameterDataProvider<D, P> paramDataProvider;
  // To identity Function or Ruleset
  private final String objectTypeName;

  /**
   * Constructor
   *
   * @param monitor IProgressMonitor
   * @param paramDataProvider
   * @param objectTypeName
   */
  public ReviewRuleExport(final IProgressMonitor monitor, final ParameterDataProvider paramDataProvider,
      final String objectTypeName) {
    this.monitor = monitor;
    this.paramDataProvider = paramDataProvider;
    this.objectTypeName = objectTypeName;
  }

  /**
   * Normal cell style
   */
  private CellStyle cellStyle;
  /**
   * Font instance
   */
  private Font font;

  /**
   * checks not having the dependency flag
   */
  private boolean noDependencyFlag;
  private CellStyle cellStyleForNumber;

  private static final int MONITOR_WORK = 30;
  private static final int SHEET_TAB_ZERO = 0;


  /**
   * This method creates new Excel file and export the Review Rule/ Rule Set /Component Package
   *
   * @param selectedObject selectedObject
   * @param sortedParameters sortedParameters
   * @param filePath file path
   * @param fileExtn file extension
   * @param filteredFlag filtered flag
   */
  /**
   * @param selectedObject selectedObject
   * @param sortedParameters sortedParameters
   * @param filePath filePath
   * @param fileExtn fileExtn
   */
  public void exportReviewRule(final ParamCollection selectedObject, final SortedSet<?> sortedParameters,
      final String filePath, final String fileExtn) {
    final String fileType = fileExtn;
    final ExcelFactory exFactory = ExcelFactory.getFactory(fileType);
    final ExcelFile xlFile = exFactory.getExcelFile();
    final Workbook workbook = xlFile.createWorkbook();
    // creates a new workbook
    createSheet(workbook);
    this.cellStyle = ExcelCommon.getInstance().createCellStyle(workbook);
    this.font = ExcelCommon.getInstance().createFont(workbook);

    // check the full file path with extension
    String fileFullPath = CommonUtils.getCompleteFilePath(filePath, fileExtn);
    try (FileOutputStream fileOut = new FileOutputStream(fileFullPath)) {// to handle fileOut easily in java-7
      this.monitor.worked(MONITOR_WORK);
      this.monitor.subTask("Exporting rule results . . .");
      createResultWorkSheet(workbook, sortedParameters);
      this.monitor.worked(MONITOR_WORK);
      workbook.setSelectedTab(SHEET_TAB_ZERO);
      workbook.write(fileOut);
      fileOut.flush();
      fileOut.close();
    }
    catch (FileNotFoundException fnfe) {
      CDMLogger.getInstance().error(fnfe.getLocalizedMessage(), fnfe, Activator.PLUGIN_ID);
    }
    catch (IOException ioe) {
      CDMLogger.getInstance().error(ioe.getLocalizedMessage(), ioe, Activator.PLUGIN_ID);
    }
  }


  /**
   * This method creates the Review Rule Excel Report
   *
   * @param workbook
   * @param selectedObject
   * @param filteredAttrsMap
   * @param cdrFuncVersion
   */
  private void createResultWorkSheet(final Workbook workbook, final SortedSet<?> sortedParameters) {
    // TODO selected version need to be implemented
    String[] resultSheetHeader;
    if (CommonUtils.isEqual(this.objectTypeName, "Function")) {
      resultSheetHeader = FunctionExcelColumn.getInstance().functionSheetHeader;
    }
    else {
      resultSheetHeader = ReviewRuleExcelColumn.getInstance().reviewRuleSheetHeader;
    }

    this.cellStyleForNumber = ExcelCommon.getInstance().createCellStyle(workbook);
    this.cellStyleForNumber.setAlignment(CellStyle.ALIGN_RIGHT);

    final Sheet reviewRuleSheet = workbook.getSheetAt(SHEET_TAB_ZERO);
    reviewRuleSheet.setRowSumsBelow(false);
    final Row headerRow = ExcelCommon.getInstance().createExcelRow(reviewRuleSheet, ExcelClientConstants.ROW_NUM_ZERO);
    CellStyle wrappingCellStyle = workbook.createCellStyle();
    wrappingCellStyle.setWrapText(true);
    wrappingCellStyle.setFont(this.font);
    CellStyle headerCellStyle = ExcelCommon.getInstance().createHeaderCellStyle(workbook);

    for (int headerCol = 0; headerCol < (resultSheetHeader.length); headerCol++) {
      Cell createdHeaderCell = ExcelCommon.getInstance().createHeaderCell(headerRow, resultSheetHeader[headerCol],
          headerCol, headerCellStyle, this.font);
      createdHeaderCell.setCellStyle(wrappingCellStyle);
    }
    int rowCount = ExcelClientConstants.ROW_NUM_ZERO;
    for (Object item : sortedParameters) {
      IParameter parameter = (IParameter) item;
      rowCount++;
      ReviewRule cdrRule = this.paramDataProvider.getReviewRule(parameter);
      final Row row = ExcelCommon.getInstance().createExcelRow(reviewRuleSheet, rowCount);
      for (int col = ExcelClientConstants.COLUMN_NUM_ZERO; col < resultSheetHeader.length; col++) {
        String result = setParamDetails(parameter, col);
        // call switch case
        createReviewRuleColumn(resultSheetHeader, workbook, parameter, cdrRule, row, col, result);
      }
      // adding default rule and dependencies
      if (this.paramDataProvider.hasDependency(parameter)) {
        int fromTempRowCount = rowCount; // for row grouping, from Row
        if (this.paramDataProvider.getRuleList(parameter) != null) {
          for (ReviewRule dependencyRule : this.paramDataProvider.getRuleList(parameter)) {
            rowCount++;
            final Row dependencyRow = ExcelCommon.getInstance().createExcelRow(reviewRuleSheet, rowCount);

            // default rule manipulation
            boolean isDefaultRule = checkIfDefaultRule(parameter, dependencyRule);

            // depnd result process and create the review ruld column
            depndResProcessAndRvwColCreate(workbook, resultSheetHeader, dependencyRule, dependencyRow, isDefaultRule);
          }
        }
        if (fromTempRowCount < rowCount) {
          fromTempRowCount += 1;
          reviewRuleSheet.groupRow(fromTempRowCount, rowCount);
          reviewRuleSheet.setRowGroupCollapsed(fromTempRowCount, false);
        }
      }
    }
    // setting width
    reviewRuleSheet.autoSizeColumn(ExcelClientConstants.COLUMN_NUM_ZERO);// parameter
    reviewRuleSheet.setColumnWidth(ExcelClientConstants.COLUMN_NUM_ONE, LONG_NAME_COL_WIDTH); // long name
    reviewRuleSheet.autoSizeColumn(ExcelClientConstants.COLUMN_NUM_TWO);// class
    reviewRuleSheet.autoSizeColumn(ExcelClientConstants.COLUMN_NUM_THREE);// code word
    reviewRuleSheet.setColumnWidth(ExcelClientConstants.COLUMN_NUM_FOUR, LOWER_LIMIT_COL_WIDTH);// lower limit
    reviewRuleSheet.setColumnWidth(ExcelClientConstants.COLUMN_NUM_FIVE, UPPER_LIMIT_COL_WIDTH);// upper limit
    reviewRuleSheet.autoSizeColumn(ExcelClientConstants.COLUMN_NUM_SIX);// reference value
    reviewRuleSheet.setColumnWidth(ExcelClientConstants.COLUMN_NUM_SEVEN, EXACT_MATCH_COL_WIDTH);// Exact match
    reviewRuleSheet.autoSizeColumn(ExcelClientConstants.COLUMN_NUM_EIGHT);// Unit
    reviewRuleSheet.setColumnWidth(ExcelClientConstants.COLUMN_NUM_NINE, READY_FOR_SERIES_COL_WIDTH);// Ready for series
    // ICDM-2537
    reviewRuleSheet.autoSizeColumn(ExcelClientConstants.COLUMN_NUM_TEN);
    reviewRuleSheet.autoSizeColumn(ExcelClientConstants.COLUMN_NUM_ELEVEN);// Created User
    reviewRuleSheet.autoSizeColumn(ExcelClientConstants.COLUMN_NUM_TWELEVE);// Created Date
    reviewRuleSheet.autoSizeColumn(ExcelClientConstants.COLUMN_NUM_THIRTEEN);// Rev Id
    reviewRuleSheet.setColumnWidth(ExcelClientConstants.COLUMN_NUM_FOURTEEN, REMARKS_COL_WIDTH);// Remarks
    if (CommonUtils.isNotEqual(this.objectTypeName, "Function")) {
      reviewRuleSheet.autoSizeColumn(ExcelClientConstants.COLUMN_NUM_FIFTEEN);
      reviewRuleSheet.autoSizeColumn(ExcelClientConstants.COLUMN_NUM_SIXTEEN);
      reviewRuleSheet.autoSizeColumn(ExcelClientConstants.COLUMN_NUM_SEVENTEEN);
      reviewRuleSheet.autoSizeColumn(ExcelClientConstants.COLUMN_NUM_EIGHTEEN);
    }


    // create legend for color coding in excel
    createLegend(workbook);

    // auto adjust column for legend
    // ICDM-2537 - column number incremented
    reviewRuleSheet.autoSizeColumn(ExcelClientConstants.COLUMN_NUM_SIXTEEN);

    // Disable autoFilter when there are no rows
    if (rowCount > 0) {
      ExcelCommon.getInstance().setAutoFilter(reviewRuleSheet,
          "A1:" + ExcelCommon.getInstance().getExcelColName(resultSheetHeader.length - 1), rowCount);
    }
    reviewRuleSheet.createFreezePane(ExcelClientConstants.COLUMN_NUM_TWO, ExcelClientConstants.ROW_NUM_ONE);
  }


  /**
   * Method to check the default rule
   *
   * @param selectedObject
   * @param dependencyRule
   * @param isDefaultRule
   * @return
   */
  private boolean checkIfDefaultRule(final IParameter selectedObject, final ReviewRule dependencyRule) {
    return this.paramDataProvider.isDefaultRule(selectedObject, dependencyRule);
  }


  /**
   * Method to calculate the dependency result and create review rule column
   *
   * @param workbook
   * @param resultSheetHeader
   * @param dependencyRule
   * @param dependencyRow
   * @param isDefaultRule
   */
  private void depndResProcessAndRvwColCreate(final Workbook workbook, final String[] resultSheetHeader,
      final ReviewRule dependencyRule, final Row dependencyRow, final boolean isDefaultRule) {
    for (int col = ExcelClientConstants.COLUMN_NUM_ZERO; col < resultSheetHeader.length; col++) {
      String dependencyResult;
      if (isDefaultRule && (col == ExcelClientConstants.COLUMN_NUM_ONE)) {
        dependencyResult = ExcelClientConstants.DEFAULT_RULE;
      }
      else {
        dependencyResult = setRuleDetails(dependencyRule, col);
      }
      // call switch case
      createReviewRuleColumn(resultSheetHeader, workbook, null, dependencyRule, dependencyRow, col, dependencyResult);
    }
  }

  private void createReviewRuleColumn(final String[] resultSheetHeader, final Workbook workbook,
      final IParameter parameter, final ReviewRule cdrRule, final Row row, final int col, final String result) {
    switch (resultSheetHeader[col]) {
      // ICDM-2444
      case ExcelClientConstants.COMPLIANCE_NAME:
        caseCompliName(parameter, row, col);
        break;

      case ExcelClientConstants.PARAMETER:
        createColumn(result, row, col, parameter == null ? null : parameter.getParamHint());
        break;

      case ExcelClientConstants.LONG_NAME:
      case ExcelClientConstants.CLASS:
      case ExcelClientConstants.IS_CODE_WORD:
      case ExcelClientConstants.EXACT_MATCH:
      case ExcelClientConstants.RH_UNIT:
      case ExcelClientConstants.READY_FOR_SERIES:
        // ICDM-2537
      case ExcelClientConstants.MODIFIED_USER:
        // ICDM-2537
      case ExcelClientConstants.MODIFIED_DATE:
        // ICDM-2537
      case ExcelClientConstants.REVISION_ID:
        // ICDM-2537
      case ExcelClientConstants.REMARKS:
        createColumn(result, row, col, null);
        break;
      case ExcelClientConstants.LOWER_LIMIT:
      case ExcelClientConstants.UPPER_LIMIT:
        createColumnForNumber(workbook, result, row, col, null);
        break;
      case ExcelClientConstants.PARAMETER_TYPE:
      case ExcelClientConstants.PARAMETER_RESP:
      case ExcelClientConstants.SYSTEM_ELEMENT:
      case ExcelClientConstants.HW_COMPONENT:
        createColumnForNumber(workbook, result, row, col, null);
        break;

      case ExcelClientConstants.REFERENCE_VALUE:
        createReferenceCol(workbook, result, cdrRule == null ? null : cdrRule.getHint(), row, col, cdrRule);
        break;


      default:
        break;
    }
  }


  /**
   * @param parameter
   * @param row
   * @param col
   */
  private void caseCompliName(final IParameter parameter, final Row row, final int col) {
    String complianceStr = "";
    if ((null != parameter) && this.paramDataProvider.isComplianceParam(parameter)) {
      complianceStr = "COMPLIANCE";
    }
    createColumn(complianceStr, row, col, null);
  }


  /**
   * This method gives result of cdr rule
   */
  private String setRuleDetails(final Object element, final int columnIndex) {
    final ReviewRule rule = (ReviewRule) element;
    String result = "";
    switch (columnIndex) {
      case ExcelClientConstants.COLUMN_NUM_ONE:
        result = this.paramDataProvider.getAttrValString(rule);

        break;
      case ExcelClientConstants.COLUMN_NUM_FIVE:
        if (null != rule.getLowerLimit()) {
          result = rule.getLowerLimit().toString();
        }
        break;
      case ExcelClientConstants.COLUMN_NUM_SIX:
        if (null != rule.getUpperLimit()) {
          result = rule.getUpperLimit().toString();
        }
        break;
      case ExcelClientConstants.COLUMN_NUM_SEVEN:
        result = rule.getRefValueDispString();
        break;
      case ExcelClientConstants.COLUMN_NUM_EIGHT:
        result = ruleExactMatch(rule);
        break;
      case ExcelClientConstants.COLUMN_NUM_NINE:
        if (null != rule.getUnit()) {
          result = rule.getUnit();
        }
        break;
      case ExcelClientConstants.COLUMN_NUM_TEN:
        result = ReviewRuleUtil.getReadyForSeriesUIVal(rule);
        break;
      // ICDM-2537
      case ExcelClientConstants.COLUMN_NUM_ELEVEN:
        result = getcreatedUser(rule, result);
        break;
      // ICDM-2537
      case ExcelClientConstants.COLUMN_NUM_TWELEVE:
        if (null != rule.getRuleCreatedDate()) {
          result = rule.getRuleCreatedDate().toString();
        }
        break;
      // ICDM-2537
      case ExcelClientConstants.COLUMN_NUM_THIRTEEN:
        if (null != rule.getRevId()) {
          result = rule.getRevId().toString();
        }
        break;
      // ICDM-2537
      case ExcelClientConstants.COLUMN_NUM_FOURTEEN:
        if (null != rule.getHint()) {
          result = rule.getHint().toString();
        }
        break;
      default:
        result = "";
        break;
    }
    return result;
  }


  /**
   * @param rule
   * @param result
   * @return
   */
  private String getcreatedUser(final ReviewRule rule, String result) {
    if (null != rule.getRuleCreatedUser()) {
      try {
        result = new CurrentUserBO().getFullName();
      }
      catch (ApicWebServiceException e) {
        CDMLogger.getInstance().error(e.getMessage(), e, Activator.PLUGIN_ID);
      }
    }
    return result;
  }

  /**
   * Returns the exact match of rule
   *
   * @param rule selceted rule
   * @return exact match
   */
  private String ruleExactMatch(final ReviewRule rule) {
    String result = "";
    if (!rule.getRefValueDispString().isEmpty()) {
      if (rule.isDcm2ssd()) {
        result = ExcelClientConstants.YES_SENTENCE_CASE;
      }
      else {
        result = ExcelClientConstants.NO_SENTENCE_CASE;
      }
    }
    return result;
  }

  /**
   * To create legend for color coding in excel
   *
   * @param workbook
   */
  private void createLegend(final Workbook workbook) {

    CellStyle headerCellStyle = ExcelCommon.getInstance().createHeaderCellStyle(workbook);

    Sheet sheetAt = workbook.getSheetAt(SHEET_TAB_ZERO);

    int legendRow = ExcelClientConstants.LEGEND_START_ROW;
    Row row = sheetAt.getRow(legendRow);
    row = createRowIfNull(sheetAt, row, legendRow);
    // ICDM-2537 Column number incremented
    ExcelCommon.getInstance().createCell(row, "Legend", ExcelClientConstants.COLUMN_NUM_NINETEEN, headerCellStyle);

    // WHITE
    legendRow++;
    row = sheetAt.getRow(legendRow);
    row = createRowIfNull(sheetAt, row, legendRow);
    createLegendInCell(workbook, row, IndexedColors.WHITE.getIndex(), RuleMaturityLevel.NONE.getICDMMaturityLevel());

    // YELLOW
    legendRow++;
    row = sheetAt.getRow(legendRow);
    row = createRowIfNull(sheetAt, row, legendRow);
    createLegendInCell(workbook, row, IndexedColors.YELLOW.getIndex(), RuleMaturityLevel.START.getICDMMaturityLevel());

    // BLUE
    legendRow++;
    row = sheetAt.getRow(legendRow);
    row = createRowIfNull(sheetAt, row, legendRow);
    createLegendInCell(workbook, row, IndexedColors.LIGHT_BLUE.getIndex(),
        RuleMaturityLevel.STANDARD.getICDMMaturityLevel());

    // GREEN
    legendRow++;
    row = sheetAt.getRow(legendRow);
    row = createRowIfNull(sheetAt, row, legendRow);
    createLegendInCell(workbook, row, IndexedColors.LIGHT_GREEN.getIndex(),
        RuleMaturityLevel.FIXED.getICDMMaturityLevel());
  }

  /**
   * This method used to check the null for the given row
   *
   * @param sheetAt
   * @param excelRow
   */
  private Row createRowIfNull(final Sheet sheetAt, final Row excelRow, final int rowCount) {
    Row rowToReturn = excelRow;
    if (rowToReturn == null) {
      rowToReturn = ExcelCommon.getInstance().createExcelRow(sheetAt, rowCount);
    }
    return rowToReturn;
  }

  /**
   * This method is used to create the legend
   *
   * @param workbook
   * @param row
   * @param colorIndex
   * @param ruleMaturityLevel
   */
  private void createLegendInCell(final Workbook workbook, final Row row, final short colorIndex,
      final String ruleMaturityLevel) {
    Cell cell;
    CellStyle style = workbook.createCellStyle();
    // ICDM-2537 Column number incremented
    cell = ExcelCommon.getInstance().createCell(row, "", ExcelClientConstants.COLUMN_NUM_NINETEEN, this.cellStyle);
    style.setFillForegroundColor(colorIndex);
    style.setFillPattern(CellStyle.SOLID_FOREGROUND);
    // applying border
    setBorderStyle(style);
    cell.setCellStyle(style);
    // ICDM-2537 Column number incremented
    ExcelCommon.getInstance().createCell(row, ruleMaturityLevel, ExcelClientConstants.COLUMN_NUM_TWENTY,
        this.cellStyle);
  }

  /**
   * This method apply border at all sides to a cell
   *
   * @param style
   */
  private void setBorderStyle(final CellStyle style) {
    style.setBorderBottom(CellStyle.BORDER_THIN);
    style.setBorderLeft(CellStyle.BORDER_THIN);
    style.setBorderRight(CellStyle.BORDER_THIN);
    style.setBorderTop(CellStyle.BORDER_THIN);
    style.setAlignment(CellStyle.ALIGN_CENTER);
  }

  /**
   * This method set the parameter details
   *
   * @param element
   * @param columnIndex
   * @return result
   */
  private String setParamDetails(final Object element, final int columnIndex) {
    String result = "";
    this.noDependencyFlag = true;
    if (element instanceof RuleSetParameter) {
      final RuleSetParameter funcParam = (RuleSetParameter) element;
      if (this.paramDataProvider.hasDependency(funcParam)) {
        this.noDependencyFlag = false;
      }
      switch (columnIndex) {
        // ICDM-2444
        case ExcelClientConstants.COLUMN_NUM_ONE:
          result = funcParam.getName();
          break;
        case ExcelClientConstants.COLUMN_NUM_TWO:
          result = funcParam.getLongName();
          break;
        case ExcelClientConstants.COLUMN_NUM_THREE:
          result = funcParam.getpClassText();
          break;
        case ExcelClientConstants.COLUMN_NUM_FOUR:
          result = funcParam.getCodeWord();
          break;
        case ExcelClientConstants.COLUMN_NUM_FIVE:
          result = setParmLowerLmt(funcParam);
          break;
        case ExcelClientConstants.COLUMN_NUM_SIX:
          result = setParamUpperLmt(funcParam);
          break;
        case ExcelClientConstants.COLUMN_NUM_FIFTEEN:
          if (CommonUtils.isNotNull(funcParam.getParamType())) {
            result = funcParam.getParamType();
          }
          break;
        case ExcelClientConstants.COLUMN_NUM_SIXTEEN:
          if (CommonUtils.isNotNull(funcParam.getParamResp())) {
            result = funcParam.getParamResp();
          }
          break;
        case ExcelClientConstants.COLUMN_NUM_SEVENTEEN:
          if (CommonUtils.isNotNull(funcParam.getSysElement())) {
            result = funcParam.getSysElement();
          }
          break;
        case ExcelClientConstants.COLUMN_NUM_EIGHTEEN:
          if (CommonUtils.isNotNull(funcParam.getHwComponent())) {
            result = funcParam.getHwComponent();
          }
          break;
        case ExcelClientConstants.COLUMN_NUM_SEVEN:
          if ((null != this.paramDataProvider.getReviewRule(funcParam)) &&
              (null != this.paramDataProvider.getReviewRule(funcParam).getRefValueDispString())) {
            result = this.paramDataProvider.getReviewRule(funcParam).getRefValueDispString();
          }
          break;
        case ExcelClientConstants.COLUMN_NUM_EIGHT:
          result = setParamExactMatch(funcParam);
          break;
        case ExcelClientConstants.COLUMN_NUM_NINE:
          result = setParamUnit(funcParam);
          break;
        case ExcelClientConstants.COLUMN_NUM_TEN:
          result = setParamReviewMethod(funcParam);
          break;
        // ICDM-2537
        case ExcelClientConstants.COLUMN_NUM_ELEVEN:
          result = setParamModifiedUser(funcParam);
          break;
        // ICDM-2537
        case ExcelClientConstants.COLUMN_NUM_TWELEVE:
          result = setParamModifiedDate(funcParam);
          break;
        // ICDM-2537
        case ExcelClientConstants.COLUMN_NUM_THIRTEEN:
          result = setParamRevisionId(funcParam);
          break;
        // ICDM-2537
        case ExcelClientConstants.COLUMN_NUM_FOURTEEN:
          result = setParamRemarks(funcParam);
          break;

        default:
          result = "";
          break;
      }
    }
    else {
      final IParameter funcParam = (IParameter) element;
      if (this.paramDataProvider.hasDependency(funcParam)) {
        this.noDependencyFlag = false;
      }
      switch (columnIndex) {
        // ICDM-2444
        case ExcelClientConstants.COLUMN_NUM_ONE:
          result = funcParam.getName();
          break;
        case ExcelClientConstants.COLUMN_NUM_TWO:
          result = funcParam.getLongName();
          break;
        case ExcelClientConstants.COLUMN_NUM_THREE:
          result = funcParam.getpClassText();
          break;
        case ExcelClientConstants.COLUMN_NUM_FOUR:
          result = funcParam.getCodeWord();
          break;
        case ExcelClientConstants.COLUMN_NUM_FIVE:
          result = setParmLowerLmt(funcParam);
          break;
        case ExcelClientConstants.COLUMN_NUM_SIX:
          result = setParamUpperLmt(funcParam);
          break;
        case ExcelClientConstants.COLUMN_NUM_SEVEN:
          if ((null != this.paramDataProvider.getReviewRule(funcParam)) &&
              (null != this.paramDataProvider.getReviewRule(funcParam).getRefValueDispString())) {
            result = this.paramDataProvider.getReviewRule(funcParam).getRefValueDispString();
          }
          break;
        case ExcelClientConstants.COLUMN_NUM_EIGHT:
          result = setParamExactMatch(funcParam);
          break;
        case ExcelClientConstants.COLUMN_NUM_NINE:
          result = setParamUnit(funcParam);
          break;
        case ExcelClientConstants.COLUMN_NUM_TEN:
          result = setParamReviewMethod(funcParam);
          break;
        // ICDM-2537
        case ExcelClientConstants.COLUMN_NUM_ELEVEN:
          result = setParamModifiedUser(funcParam);
          break;
        // ICDM-2537
        case ExcelClientConstants.COLUMN_NUM_TWELEVE:
          result = setParamModifiedDate(funcParam);
          break;
        // ICDM-2537
        case ExcelClientConstants.COLUMN_NUM_THIRTEEN:
          result = setParamRevisionId(funcParam);
          break;
        // ICDM-2537
        case ExcelClientConstants.COLUMN_NUM_FOURTEEN:
          result = setParamRemarks(funcParam);
          break;
        default:
          result = "";
          break;
      }
    }

    return result;
  }

  // ICDM-2537
  /**
   * @param funcParam
   * @return
   */
  private String setParamRevisionId(final IParameter funcParam) {
    String result = "";
    if (this.noDependencyFlag && (this.paramDataProvider.getReviewRule(funcParam) != null) &&
        ((this.paramDataProvider.getReviewRule(funcParam)).getRevId() != null)) {
      result = this.paramDataProvider.getReviewRule(funcParam).getRevId().toString();
    }
    return result;
  }

  // ICDM-2537
  /**
   * @param funcParam
   * @return
   */
  private String setParamRemarks(final IParameter funcParam) {
    String result = "";
    if (this.noDependencyFlag && (this.paramDataProvider.getReviewRule(funcParam) != null) &&
        (this.paramDataProvider.getReviewRule(funcParam).getHint() != null)) {
      result = this.paramDataProvider.getReviewRule(funcParam).getHint();
    }
    return result;
  }

  // ICDM-2537
  /**
   * @param funcParam
   * @return
   */
  private String setParamModifiedDate(final IParameter funcParam) {
    String result = "";
    if (this.noDependencyFlag && (this.paramDataProvider.getReviewRule(funcParam) != null) &&
        (this.paramDataProvider.getReviewRule(funcParam).getRuleCreatedDate() != null)) {
      result = this.paramDataProvider.getReviewRule(funcParam).getRuleCreatedDate().toString();
    }
    return result;
  }

  // ICDM-2537
  /**
   * @param funcParam
   * @return
   */
  private String setParamModifiedUser(final IParameter funcParam) {
    String result = "";
    if (this.noDependencyFlag && (this.paramDataProvider.getReviewRule(funcParam) != null) &&
        (this.paramDataProvider.getReviewRule(funcParam).getRuleCreatedUser() != null)) {
      try {
        result = new CurrentUserBO().getFullName();
      }
      catch (ApicWebServiceException e) {
        CDMLogger.getInstance().error(e.getMessage(), e, Activator.PLUGIN_ID);
      }
    }
    return result;
  }

  /**
   * Sets param unit
   *
   * @param funcParam sel param
   * @return unit
   */
  private String setParamUnit(final IParameter funcParam) {
    String result = "";
    if (this.noDependencyFlag && (this.paramDataProvider.getReviewRule(funcParam) != null) &&
        (this.paramDataProvider.getReviewRule(funcParam).getUnit() != null)) {
      result = this.paramDataProvider.getReviewRule(funcParam).getUnit();
    }
    return result;
  }


  /**
   * Sets param upper limit
   *
   * @param funcParam sel param
   * @return upper limit
   */
  private String setParamUpperLmt(final IParameter funcParam) {
    String result = "";
    if (this.noDependencyFlag && (this.paramDataProvider.getReviewRule(funcParam) != null) &&
        (this.paramDataProvider.getReviewRule(funcParam).getUpperLimit() != null)) {
      result = this.paramDataProvider.getReviewRule(funcParam).getUpperLimit().toString();
    }
    return result;
  }


  /**
   * Sets param lower limit
   *
   * @param funcParam sel param
   * @return lower limit
   */
  private String setParmLowerLmt(final IParameter funcParam) {
    String result = "";
    if (this.noDependencyFlag && (this.paramDataProvider.getReviewRule(funcParam) != null) &&
        (this.paramDataProvider.getReviewRule(funcParam).getLowerLimit() != null)) {
      result = this.paramDataProvider.getReviewRule(funcParam).getLowerLimit().toString();
    }
    return result;
  }


  /**
   * Sets param ready for series
   *
   * @param funcParam sel param
   * @return ready for series
   */
  private String setParamReviewMethod(final IParameter funcParam) {
    String result = "";
    if (this.noDependencyFlag && (this.paramDataProvider.getReviewRule(funcParam) != null) &&
        (ReviewRuleUtil.getReadyForSeriesUIVal(this.paramDataProvider.getReviewRule(funcParam)) != null)) {
      result = ReviewRuleUtil.getReadyForSeriesUIVal(this.paramDataProvider.getReviewRule(funcParam));
    }
    return result;
  }


  /**
   * Returns the exact match of param rule
   *
   * @param rule selceted param
   * @return exact match
   */
  private String setParamExactMatch(final IParameter funcParam) {
    String result = "";
    if (this.noDependencyFlag && (this.paramDataProvider.getReviewRule(funcParam) != null) &&
        (!this.paramDataProvider.getReviewRule(funcParam).getRefValueDispString().isEmpty()) &&
        (ReviewRuleUtil.getRefValue(this.paramDataProvider.getReviewRule(funcParam)) != null)) {
      if ((!this.paramDataProvider.getReviewRule(funcParam).getRefValueDispString().isEmpty()) &&
          this.paramDataProvider.getReviewRule(funcParam).isDcm2ssd()) {
        result = ExcelClientConstants.YES_SENTENCE_CASE;
      }
      else {
        result = ExcelClientConstants.NO_SENTENCE_CASE;
      }
    }
    return result;
  }

  /**
   * This method create reference columns
   *
   * @param workbook
   * @param refValue
   * @param row
   * @param col
   * @param cdrRule
   */
  private void createReferenceCol(final Workbook workbook, final String refValue, final String cellComment,
      final Row row, final int col, final ReviewRule cdrRule) {
    Cell cell;
    cell = ExcelCommon.getInstance().createCell(row, CommonUtils.checkNull(refValue), col, this.cellStyle);
    if (!CommonUtils.isEmptyString(cellComment)) {
      ExcelCommon.getInstance().createCellComment(cellComment, cell, COMMENT_HEIGHT, COMMENT_WIDTH);
    }
    // new color changes
    final CellStyle style = workbook.createCellStyle();
    style.setFillForegroundColor(getBackground(cdrRule));
    style.setFillPattern(CellStyle.SOLID_FOREGROUND);
    if (IndexedColors.WHITE.getIndex() == getBackground(cdrRule)) {
      style.setFillPattern(CellStyle.NO_FILL);
    }
    // applying border
    setBorderStyle(style);
    cell.setCellStyle(style);
  }

  /**
   * This method get the background color for reference column
   *
   * @param rule CDRRule
   * @return short index value
   */
  private short getBackground(final ReviewRule rule) {
    if (null != rule) {
      if (rule.getMaturityLevel() != null) {
        return getMaturityLevelColor(RuleMaturityLevel.getIcdmMaturityLvlEnumForSsdText(rule.getMaturityLevel()));
      }
    }
    return IndexedColors.WHITE.getIndex();
  }

  /**
   * Get the color of the given maturity level
   *
   * @param maturityLevel CDR rule maturity level
   * @return color
   */
  private static short getMaturityLevelColor(final RuleMaturityLevel maturityLevel) {
    short index;
    switch (maturityLevel) {
      case START:
        index = IndexedColors.YELLOW.getIndex();// yellow
        break;

      case STANDARD:
        index = IndexedColors.LIGHT_BLUE.getIndex();// blue
        break;

      case FIXED:
        index = IndexedColors.LIGHT_GREEN.getIndex();// green
        break;

      default:
        index = IndexedColors.WHITE.getIndex();// white
    }
    return index;
  }


  /**
   * @param valueName
   * @param row
   * @param col
   * @param reviewRuleSheet
   * @param bgColor yellow background for changed values needed or not
   */
  private void createColumn(final String valueName, final Row row, final int col, final String cellComment) {
    Cell cell;
    cell = ExcelCommon.getInstance().createCell(row, CommonUtils.checkNull(valueName), col, this.cellStyle);
    if (!CommonUtils.isEmptyString(cellComment)) {
      ExcelCommon.getInstance().createCellComment(cellComment, cell, ExcelClientConstants.COMMENT_HEIGHT_FIVE,
          ExcelClientConstants.COMMENT_WIDTH_FOUR);
    }
  }

  /**
   * This method creates Right Alignment Cell to improve readability of numbers
   *
   * @param workbook
   * @param valueName
   * @param reviewRuleSheet
   * @param row
   * @param col
   * @param bgColor yellow background for changed values needed or not
   */
  private void createColumnForNumber(final Workbook workbook, final String valueName, final Row row, final int col,
      final String cellComment) {
    Cell cell;
    cell = ExcelCommon.getInstance().createCell(row, CommonUtils.checkNull(valueName), col, this.cellStyle);

    cell.setCellStyle(this.cellStyleForNumber);
    if (!CommonUtils.isEmptyString(cellComment)) {
      ExcelCommon.getInstance().createCellComment(cellComment, cell, COMMENT_HEIGHT, COMMENT_WIDTH);
    }
  }


  /**
   * This method create review result Sheet
   *
   * @param workbook mainworkbook
   * @param cdrResult cdrresult
   */
  private void createSheet(final Workbook workbook) {
    workbook.createSheet("Review Rules");
  }

}
