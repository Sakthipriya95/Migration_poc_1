/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.report.excel;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedSet;

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
import com.bosch.caltool.icdm.client.bo.qnaire.QnaireRespEditorDataHandler;
import com.bosch.caltool.icdm.client.bo.qnaire.QnaireRespEditorDataHandler.SortColumns;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.cdr.CDRConstants;
import com.bosch.caltool.icdm.model.cdr.QnaireResponseCombinedModel;
import com.bosch.caltool.icdm.model.cdr.qnaire.RvwQnaireAnswer;
import com.bosch.caltool.icdm.report.Activator;
import com.bosch.caltool.icdm.report.common.ExcelCommon;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

// ICDM-1994
/**
 * This class performs the excel sheet manipulation for the export of questionnaire response editor
 *
 * @author svj7cob
 */
public class QuestResponseExport {

  /**
   * SPACER CONSTANT
   */
  private static final String SPACER = "  ";

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
  private CellStyle cellStyle;

  /**
   * Font instance
   */
  private Font font;

  /**
   * the progress indicator for the export
   */
  private static final int QUEST_RESP_EXPORT_WORK_TIME = 30;

  private QnaireResponseCombinedModel qnaireResponseCombinedModel;

  private CellStyle createHeadingCellStyle;

  /**
   * Constructor
   *
   * @param monitor IProgressMonitor to find the percentage of work
   */
  public QuestResponseExport(final IProgressMonitor monitor) {
    this.monitor = monitor;
  }

  /**
   * Export questionnaire response report.
   *
   * @param rvwQnaireAnsSet the set of review questionnaire answer
   * @param dataHandler the data handler
   * @param visibleColmns visible columns in editor
   * @param filePath the file path of excel to store
   * @param fileExtn the type of excel
   */
  public void exportQuestionnaireResponseReport(final SortedSet<RvwQnaireAnswer> rvwQnaireAnsSet,
      final QnaireRespEditorDataHandler dataHandler, final List<String> visibleColmns, final String filePath,
      final String fileExtn) {
    final String fileType = fileExtn;
    final ExcelFactory exFactory = ExcelFactory.getFactory(fileType);
    final ExcelFile xlFile = exFactory.getExcelFile();
    final Workbook workbook = xlFile.createWorkbook();

    workbook.createSheet("Questionnaire Response Results");
    this.createHeadingCellStyle = ExcelCommon.getInstance().createHeadingCellStyle(workbook);
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

      this.monitor.worked(QUEST_RESP_EXPORT_WORK_TIME);
      this.monitor.subTask("Exporting Questionnaire response report . . .");

      if (CDMLogger.getInstance().isDebugEnabled()) {
        CDMLogger.getInstance().debug("Questionnaire response report work sheet creation in export started");
      }
      // remove the rows that are hidden due to dependency
      rvwQnaireAnsSet.removeIf(rvwQnaire -> !dataHandler.isQuestionVisible(rvwQnaire));
      // access first sheet tab in the excel
      final Sheet qnaireSheet = workbook.getSheetAt(0);
      qnaireSheet.setRowSumsBelow(false);
      // manipulation of rows and columns in the excel sheet
      createQuestResponseWorkSheet(workbook, rvwQnaireAnsSet, visibleColmns, dataHandler, qnaireSheet);

      if (CDMLogger.getInstance().isDebugEnabled()) {
        CDMLogger.getInstance().debug("Questionnaire response report work sheet creation in export completed");
      }

      this.monitor.worked(QUEST_RESP_EXPORT_WORK_TIME);
      this.monitor.subTask("Exporting Questionnaire response report information. . .");

      if (CDMLogger.getInstance().isDebugEnabled()) {
        CDMLogger.getInstance().debug("Questionnaire response report info work sheet creation in export completed");
      }

      workbook.setSelectedTab(0);

      workbook.write(fileOut);
    }
    catch (FileNotFoundException fnfe) {
      CDMLogger.getInstance().error(fnfe.getLocalizedMessage(), fnfe, Activator.PLUGIN_ID);
    }
    catch (IOException ioe) {
      CDMLogger.getInstance().error(ioe.getLocalizedMessage(), ioe, Activator.PLUGIN_ID);
    }

  }

  /**
   * Export questionnaire response report along with review result
   *
   * @param rvwQnaireAnsSet
   * @param dataHandler
   * @param visibleColmns
   * @param workbook
   * @param qnaireCounter
   * @param qnaireResponseCombinedModel
   */
  public void exportQuestionnaireResponseReport(final SortedSet<RvwQnaireAnswer> rvwQnaireAnsSet,
      final QnaireRespEditorDataHandler dataHandler, final List<String> visibleColmns, final Workbook workbook,
      final int qnaireCounter, final QnaireResponseCombinedModel qnaireResponseCombinedModel) {

    this.qnaireResponseCombinedModel = qnaireResponseCombinedModel;
    String qnaireSheetName = rmXlxSheetSpecialChar(getQnaireRespSheetName(dataHandler, qnaireCounter));
    // create qnaire sheet
    workbook.createSheet(qnaireSheetName);

    // access first sheet tab in the excel
    final Sheet qnaireSheet = workbook.getSheet(qnaireSheetName);
    qnaireSheet.setRowSumsBelow(false);

    this.createHeadingCellStyle = ExcelCommon.getInstance().createHeadingCellStyle(workbook);
    this.headerCellStyle = ExcelCommon.getInstance().createHeaderCellStyle(workbook);
    this.cellStyle = ExcelCommon.getInstance().createCellStyle(workbook);
    this.font = ExcelCommon.getInstance().createFont(workbook);

    this.monitor.worked(QUEST_RESP_EXPORT_WORK_TIME);
    this.monitor.subTask("Exporting Questionnaire response report . . .");

    CDMLogger.getInstance().debug("Questionnaire response report work sheet creation in export started");
    // remove the rows that are hidden due to dependency
    rvwQnaireAnsSet.removeIf(rvwQnaire -> !dataHandler.isQuestionVisible(rvwQnaire));

    // manipulation of rows and columns in the excel sheet
    createQuestResponseWorkSheet(workbook, rvwQnaireAnsSet, visibleColmns, dataHandler, qnaireSheet);

    CDMLogger.getInstance().debug("Questionnaire response report work sheet creation in export completed");

    this.monitor.worked(QUEST_RESP_EXPORT_WORK_TIME);
    this.monitor.subTask("Exporting Questionnaire response report information. . .");

    CDMLogger.getInstance().debug("Questionnaire response report info work sheet creation in export completed");
  }

  /**
   * @param dataHandler
   * @param qnaireCounter
   * @return
   */
  private String getQnaireRespSheetName(final QnaireRespEditorDataHandler dataHandler, final int qnaireCounter) {
    StringBuilder qnaireName = new StringBuilder();
    qnaireName.append(qnaireCounter);
    qnaireName.append("-");
    qnaireName.append(getQuestionnaireName(dataHandler));
    if (CommonUtils
        .isNotNull(dataHandler.getQnaireDefBo().getQnaireDefModel().getQuestionnaireVersion().getMajorVersionNum())) {
      qnaireName.append("-V");
      qnaireName
          .append(dataHandler.getQnaireDefBo().getQnaireDefModel().getQuestionnaireVersion().getMajorVersionNum());
      if (CommonUtils
          .isNotNull(dataHandler.getQnaireDefBo().getQnaireDefModel().getQuestionnaireVersion().getMinorVersionNum())) {
        qnaireName.append(".")
            .append(dataHandler.getQnaireDefBo().getQnaireDefModel().getQuestionnaireVersion().getMinorVersionNum());
      }
    }
    return checkAndTrimQnaireSheetName(qnaireName);
  }

  /**
   * @param qnaireName as input
   * @return trimmed qnaire name
   */
  private String checkAndTrimQnaireSheetName(final StringBuilder qnaireName) {
    String qnaireSheetName;
    if (qnaireName.length() > 30) {
      qnaireSheetName = qnaireName.substring(0, 30).trim();
    }
    else {
      qnaireSheetName = qnaireName.toString();
    }
    return qnaireSheetName;
  }

  /**
   * @param dataHandler
   * @return
   */
  private String getQuestionnaireName(final QnaireRespEditorDataHandler dataHandler) {
    String nameEng = dataHandler.getQnaireDefBo().getQnaireDefModel().getQuestionnaire().getNameEng();
    if (nameEng.length() > 18) {
      nameEng = nameEng.substring(0, 18).trim() + "..";
    }
    return nameEng;
  }

  /**
   * Method to remove the special characters from excel sheet name
   */
  private String rmXlxSheetSpecialChar(final String sheetName) {
    // for excel sheet name the special charters are not allowed
    // * / \ [ ] ? :
    return sheetName.replace(":", "").replace("*", "").replace("/", "").replace("\\", "").replace("[", "")
        .replace("]", "").replace("?", "");
  }

  /**
   * Method to create the main sheet for the questionnaire response editor
   *
   * @param workbook the required workbook
   * @param rvwQnaireAnsSet the set of review questionnaire answer
   * @param visibleColmns visible columns in resp page
   * @param dataHandler
   */
  private void createQuestResponseWorkSheet(final Workbook workbook, final SortedSet<RvwQnaireAnswer> rvwQnaireAnsSet,
      final List<String> visibleColmns, final QnaireRespEditorDataHandler dataHandler, final Sheet qnaireSheet) {

    int columnSize = visibleColmns.size();
    StringBuilder qnaireNameFul = new StringBuilder();
    StringBuilder responsibilityNameFul = new StringBuilder();
    StringBuilder workPackageNameFul = new StringBuilder();
    StringBuilder qnaireStatusFul = new StringBuilder();

    qnaireNameFul.append(dataHandler.getQuesResponse().getName());
    qnaireNameFul.append(SPACER);
    qnaireNameFul.append("Response Version : ");
    qnaireNameFul.append(dataHandler.getQnaireRespModel().getRvwQnrRespVersion().getName());

    int rowNum = 0;
    createHeadingRow(qnaireSheet, columnSize, qnaireNameFul, rowNum, this.createHeadingCellStyle);
    // incrementing the rownum after creating heading
    rowNum++;
    if (CommonUtils.isNotNull(this.qnaireResponseCombinedModel)) {

      responsibilityNameFul.append("Responsibility : ");
      responsibilityNameFul.append(this.qnaireResponseCombinedModel.getA2lResponsibility().getName());
      createHeadingRow(qnaireSheet, columnSize, responsibilityNameFul, rowNum, this.headerCellStyle);
      // incrementing the rownum after creating heading
      rowNum++;
      workPackageNameFul.append("Work Package : ");
      workPackageNameFul.append(this.qnaireResponseCombinedModel.getA2lWorkPackage().getName());
      createHeadingRow(qnaireSheet, columnSize, workPackageNameFul, rowNum, this.headerCellStyle);
      // incrementing the rownum after creating heading
      rowNum++;
    }
    qnaireStatusFul.append("Status : ");
    qnaireStatusFul.append(CDRConstants.QS_STATUS_TYPE
        .getTypeByDbCode(dataHandler.getQnaireRespModel().getRvwQnrRespVersion().getQnaireRespVersStatus())
        .getUiType());
    createHeadingRow(qnaireSheet, columnSize, qnaireStatusFul, rowNum, this.headerCellStyle);
    // incrementing the rownum after creating heading
    rowNum++;
    // constructs the headers
    final Row headerRow = ExcelCommon.getInstance().createExcelRow(qnaireSheet, rowNum);
    for (int headerCol = 0; headerCol < columnSize; headerCol++) {
      ExcelCommon.getInstance().createHeaderCell(headerRow, visibleColmns.get(headerCol), headerCol,
          this.headerCellStyle, this.font);
    }

    int rowCount =
        createColumnAndRowGrouping(workbook, rvwQnaireAnsSet, dataHandler, visibleColmns, qnaireSheet, rowNum);
    for (int col = 0; col < columnSize; col++) {
      qnaireSheet.autoSizeColumn(col);
    }
    // Disable autoFilter when there are no rows
    if (rowCount > 0) {
      String rowName = CommonUtils.isNotNull(this.qnaireResponseCombinedModel) ? "A5:" : "A3:";
      ExcelCommon.getInstance().setAutoFilter(qnaireSheet,
          rowName + ExcelCommon.getInstance().getExcelColName(columnSize - 1), rowCount);
    }

  }

  /**
   * @param qnaireSheet
   * @param columnSize
   * @param displayText
   * @param rowNum
   * @param headerCellStyle2
   * @return
   */
  private void createHeadingRow(final Sheet qnaireSheet, final int columnSize, final StringBuilder displayText,
      final int rowNum, final CellStyle headerCellStyle) {
    final Row headingRespRow = ExcelCommon.getInstance().createExcelRow(qnaireSheet, rowNum);
    // creating the heading for questionnaire name and status
    ExcelCommon.getInstance().createCell(headingRespRow, CommonUtils.checkNull(displayText.toString()), 0,
        headerCellStyle);
    // creating a merged cell for heading
    qnaireSheet.addMergedRegion(new CellRangeAddress(rowNum, rowNum, 0, columnSize - 1));
  }

  /**
   * Method to create column and apply row grouping for heading and child questions
   *
   * @param workbook the given workbook
   * @param rvwQnaireAnsSet the set of review questionnaire answer
   * @param dataHandler
   * @param visibleColmns the given header
   * @param reviewInfoSheet the sheet
   * @return total rows in the excel sheet
   */
  private int createColumnAndRowGrouping(final Workbook workbook, final SortedSet<RvwQnaireAnswer> rvwQnaireAnsSet,
      final QnaireRespEditorDataHandler dataHandler, final List<String> visibleColmns, final Sheet reviewInfoSheet,
      final int rowNum) {
    boolean isToRowCalculated = false;
    int fromTempRowCount = 0;
    int toTempRowCount = 0;
    Map<Long, String> sortedMap = getData(rvwQnaireAnsSet, dataHandler);

    int rowCount = 0;
    if (!sortedMap.isEmpty()) {
      rowCount = rowNum;
    }

    for (Long questionId : sortedMap.keySet()) {
      rowCount++;
      final Row row = ExcelCommon.getInstance().createExcelRow(reviewInfoSheet, rowCount);
      RvwQnaireAnswer rvwQnaireAnswerObject = dataHandler.getAllQuestionMap().get(questionId);
      createCols(workbook, visibleColmns, rvwQnaireAnswerObject, dataHandler, row);
      SortedSet<RvwQnaireAnswer> children = dataHandler.getChildQuestions(rvwQnaireAnswerObject);

      if (!isToRowCalculated && !children.isEmpty()) {
        fromTempRowCount = rowCount;
        toTempRowCount = rowCounterIfHeadingFound(children, rowCount, dataHandler);
        isToRowCalculated = toTempRowCount > fromTempRowCount;
      }
      else if (isToRowCalculated && (rowCount == toTempRowCount)) {
        fromTempRowCount += 1;
        reviewInfoSheet.groupRow(fromTempRowCount, toTempRowCount);
        reviewInfoSheet.setRowGroupCollapsed(fromTempRowCount, false);
        isToRowCalculated = false;
      }
      CDMLogger.getInstance().debug("Questionnaire Response report work sheet row number {} created", rowCount);
    }
    return rowCount;
  }

  /**
   * @param rvwQnaireAnsSet
   * @param dataHandler
   * @return
   */
  private Map<Long, String> getData(final SortedSet<RvwQnaireAnswer> rvwQnaireAnsSet,
      final QnaireRespEditorDataHandler dataHandler) {
    Map<Long, String> questionIds = new HashMap<>();
    for (RvwQnaireAnswer rvwQnaireAnswerObject : rvwQnaireAnsSet) {
      questionIds.put(rvwQnaireAnswerObject.getQuestionId(),
          dataHandler.getPaddedQuestionNumber(rvwQnaireAnswerObject.getQuestionId()));
    }
    return sortByValue(questionIds);
  }

  /**
   * Sort by value.
   *
   * @param <K> the key type
   * @param <V> the value type
   * @param map the map
   * @return the map
   */
  public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(final Map<K, V> map) {
    List<Entry<K, V>> newList = new ArrayList<>(map.entrySet());
    newList.sort(Entry.comparingByValue());
    Map<K, V> sortedMap = new LinkedHashMap<>();
    for (Entry<K, V> entry : newList) {
      sortedMap.put(entry.getKey(), entry.getValue());
    }
    return sortedMap;
  }

  /**
   * Get rvw answer comparator.
   *
   * @param dataHandler BO
   * @param rvwQnAns1 the rvw qn ans 1
   * @param rvwQnAns2 the rvw qn ans 2
   * @param sortColumn the sort column
   * @return Comparator
   */
  public Comparator<RvwQnaireAnswer> getQuestionsRspComparator(final QnaireRespEditorDataHandler dataHandler,
      final SortColumns sortColumn) {
    return (final RvwQnaireAnswer o1, final RvwQnaireAnswer o2) -> dataHandler.compareTo(o1, o2, sortColumn);
  }

  /**
   * Method to increment the row count if heading type questions found
   *
   * @param children the list of children question
   * @param rowCount the count row to be incremented
   * @param dataHandler
   * @return the incremented row count
   */
  private int rowCounterIfHeadingFound(final SortedSet<RvwQnaireAnswer> children, final int rowCount,
      final QnaireRespEditorDataHandler dataHandler) {
    int toTempRowCount = rowCount;
    for (RvwQnaireAnswer reviewQnaireAnswer : children) {
      if (!dataHandler.checkHeading(reviewQnaireAnswer) &&
          !(dataHandler.getQuestion(reviewQnaireAnswer.getQuestionId()) != null
              ? dataHandler.getQuestion(reviewQnaireAnswer.getQuestionId()).getDeletedFlag() : false)) {
        toTempRowCount++;
      }
    }
    return toTempRowCount;
  }

  /**
   * Method to fill the data in all the column
   *
   * @param workbook the given workbook
   * @param visibleColmns resultSheetHeader
   * @param dataHandler
   * @param parameter parameter
   * @param row row
   */
  private void createCols(final Workbook workbook, final List<String> visibleColmns, final RvwQnaireAnswer ansObj,
      final QnaireRespEditorDataHandler dataHandler, final Row row) {
    for (int col = 0; col < visibleColmns.size(); col++) {
      switch (visibleColmns.get(col)) {
        case "No":
          createCol(workbook, dataHandler.getQuestionNumber(ansObj.getQuestionId()), row, col,
              dataHandler.checkHeading(ansObj));
          break;

        case "Question":
          createCol(workbook, dataHandler.getName(ansObj.getQuestionId()), row, col, dataHandler.checkHeading(ansObj));
          break;

        case "Notes Regarding Question":
        case "Hinweis zur Frage":
          createCol(workbook, dataHandler.getDescription(ansObj), row, col, dataHandler.checkHeading(ansObj));
          break;

        case "Ready For Mass Production (Y/N)":
        case "Serienreife (J/N)":
          createCol(workbook, dataHandler.getSeriesUIString(ansObj), row, col, dataHandler.checkHeading(ansObj));
          break;

        case "Measurement Existing (Y/N)":
        case "Messung vorhanden (J/N)":
          createCol(workbook, dataHandler.getMeasurementUIString(ansObj), row, col, dataHandler.checkHeading(ansObj));
          break;

        case "Link":
          createCol(workbook, dataHandler.getLinkUIString(ansObj), row, col, dataHandler.checkHeading(ansObj));
          break;

        case "Open Issues":
        case "Offene Punkte":
          fillOpenIssues(workbook, ansObj, dataHandler, row, col);
          break;

        case "Comment":
        case "Kommentar":
          createCol(workbook, dataHandler.getRemarksUIString(ansObj), row, col, dataHandler.checkHeading(ansObj));
          break;

        case "Answer":
        case "Ergebnis in Ordnung":
          createCol(workbook,
              dataHandler.getQuestionResultOptionUIString(ansObj.getQuestionId(), ansObj.getSelQnaireResultOptID()),
              row, col, dataHandler.checkHeading(ansObj));
          break;
        case "Result":
          createCol(workbook, dataHandler.getCalculatedResults(ansObj), row, col, dataHandler.checkHeading(ansObj));
          break;
        default:
          createCol(workbook, "", row, col, false);
      }
    }
  }


  /**
   * @param workbook
   * @param ansObj
   * @param dataHandler
   * @param row
   * @param col
   */
  private void fillOpenIssues(final Workbook workbook, final RvwQnaireAnswer ansObj,
      final QnaireRespEditorDataHandler dataHandler, final Row row, final int col) {
    try {
      createCol(workbook, dataHandler.getOpenPointsUIString(ansObj), row, col, dataHandler.checkHeading(ansObj));
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
    }
  }


  /**
   * Method to fill the data in the column considering the heading type. If heading type found, then color would be
   * indicated as gold color
   *
   * @param workbook the given workbook
   * @param valueName data to filled
   * @param reviewResultSheet the sheet
   * @param row given row
   * @param col given column
   * @param bgColor gold background if heading type question found
   */
  private void createCol(final Workbook workbook, final String valueName, final Row row, final int col,
      final boolean bgColor) {
    final CellStyle styleToUse = this.cellStyle;
    Cell cell = ExcelCommon.getInstance().createCell(row, CommonUtils.checkNull(valueName), col, styleToUse);
    if (bgColor) {
      final CellStyle style = workbook.createCellStyle();
      style.setFillForegroundColor(IndexedColors.GOLD.getIndex());
      style.setFillPattern(CellStyle.SOLID_FOREGROUND);
      style.setBorderBottom(CellStyle.BORDER_THIN);
      style.setBorderLeft(CellStyle.BORDER_THIN);
      style.setBorderRight(CellStyle.BORDER_THIN);
      style.setBorderTop(CellStyle.BORDER_THIN);
      style.setAlignment(CellStyle.ALIGN_CENTER);
      cell.setCellStyle(style);
    }
  }

}
