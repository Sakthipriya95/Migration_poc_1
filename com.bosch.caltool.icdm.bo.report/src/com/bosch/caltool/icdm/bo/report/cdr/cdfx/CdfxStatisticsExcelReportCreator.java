/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.report.cdr.cdfx;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import com.bosch.calcomp.adapter.logger.ILoggerAdapter;
import com.bosch.caltool.excel.ExcelFactory;
import com.bosch.caltool.excel.ExcelFile;
import com.bosch.caltool.icdm.bo.report.ReportBOConstants;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.a2l.WpRespLabelResponse;
import com.bosch.caltool.icdm.model.a2l.WpRespType;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.pidc.PidcA2l;
import com.bosch.caltool.icdm.model.cdr.DATA_REVIEW_SCORE;
import com.bosch.caltool.icdm.model.cdr.cdfx.CDFxDeliveryWrapper;
import com.bosch.caltool.icdm.model.cdr.cdfx.CDFxProjInfoModel;
import com.bosch.caltool.icdm.report.common.ExcelCommon;


/**
 * @author and4cob
 */
public class CdfxStatisticsExcelReportCreator {

  /**
   *
   */
  private static final String A2L_FILE_NAME = "A2l file Name";

  /**
   * Excel format for number , with thousnad separators
   */
  private static final String THOUSAND_SEPARATOR_FORMAT = "#,##0";


  private static final String PARAMS_EXPORTED_IN_CDFX = "Parameters Exported";


  private static final String TEXT_SEPERATOR = " --> ";

  private CellStyle headerCellStyle;

  private CellStyle cellStyle;

  private Font font;

  private CellStyle tableHeadingCellStyle;

  private Font headingFont;

  private CellStyle numCellStyle;

  /**
   * key-Param Name, value-WpRespLabelResponse
   */
  private final Map<String, WpRespLabelResponse> relevantWpRespLabelMap;

  /**
   * key="wp name --> resp type --> resp name", value=exported parameter count
   */
  private Map<String, Integer> wpParamCountMap;

  /**
   * Sorted Map to store WP statistics - with customised comparator
   */
  private final SortedSet<String> wpStatistics = new TreeSet<>(ApicUtil::compare);

  private final PidcA2l pidcA2l;

  private final CDFxProjInfoModel cdFxProjInfoModel;

  private final ILoggerAdapter logger;

  private final Map<Integer, Set<String>> paramRvwStatusMap;
  /**
   * key - "work package name --> responsibility name", value - Map<Integer, Integer>->(key - Rating, vaule - count)
   */
  private final Map<String, Map<Integer, Integer>> wpWithRatingNParamCountMap = new HashMap<>();
  /**
   * key -work package name, value - count of never reviewed parameters(missing in CDFx)
   */
  private HashMap<String, Integer> missingParamCountMap;


  /**
   * @param pidcA2l PidcA2l instance
   * @param cdFxDeliveryWrapper CDFxDeliveryWrapper instance
   * @param cdFxProjInfoModel CDFxProjInfoModel
   * @param logger ILoggerAdapter instance
   */
  public CdfxStatisticsExcelReportCreator(final PidcA2l pidcA2l, final CDFxDeliveryWrapper cdFxDeliveryWrapper,
      final CDFxProjInfoModel cdFxProjInfoModel, final ILoggerAdapter logger) {
    this.pidcA2l = pidcA2l;
    this.cdFxProjInfoModel = cdFxProjInfoModel;
    this.logger = logger;
    this.relevantWpRespLabelMap = new HashMap<>(cdFxDeliveryWrapper.getRelevantWpRespLabelMap());
    this.paramRvwStatusMap = new HashMap<>(cdFxDeliveryWrapper.getParamReviewStatusMap());
  }

  /**
   * @param filePath string
   * @param missingParamCountMap MissingParamCountMap
   * @param variantName variantName
   * @throws IcdmException IcdmException
   */
  public void exportCdfxStatistics(final String filePath, final Map<String, Integer> missingParamCountMap,
      final String variantName)
      throws IcdmException {

    this.missingParamCountMap = new HashMap<>(missingParamCountMap);
    final ExcelFactory exFactory = ExcelFactory.getFactory("xlsx");
    final ExcelFile xlFile = exFactory.getExcelFile();
    final Workbook workbook = xlFile.createWorkbook();

    // create the first sheet
    workbook.createSheet("CDFx Statistics Report");
    // create the second sheet
    workbook.createSheet("Project Information");

    this.headerCellStyle = ExcelCommon.getInstance().createHeaderCellStyle(workbook);
    this.cellStyle = ExcelCommon.getInstance().createCellStyle(workbook);

    this.headerCellStyle.setFont(ExcelCommon.getInstance().createFont(workbook));

    try (OutputStream fileOut = new FileOutputStream(filePath.toLowerCase(Locale.getDefault()))) {

      // Create a sheet to display CDFx Statistics Information
      getLogger().debug("Creating CDFx Statistics Information Report");
      createCdfxStatisticsReport(workbook, variantName);

      getLogger().debug("CDFx Statistics report creation completed");


      workbook.write(fileOut);
      fileOut.flush();
    }
    catch (IOException ioe) {
      throw new IcdmException(ioe.getMessage(), ioe);
    }

  }


  /**
   * @param workbook
   * @param variantName
   */
  private void createCdfxStatisticsReport(final Workbook workbook, final String variantName) {
    // Create table heading style without borders
    this.tableHeadingCellStyle = ExcelCommon.getInstance().createHeaderCellStyleWithoutBorder(workbook);
    // Create table heading font with font size 12
    this.headingFont = ExcelCommon.getInstance().createFontBig(workbook);

    // create cell style of number format - aligned right and separators for thousands
    this.numCellStyle = ExcelCommon.getInstance().createCellStyle(workbook);
    this.numCellStyle.setAlignment(CellStyle.ALIGN_RIGHT);
    this.numCellStyle.setDataFormat(workbook.createDataFormat().getFormat(THOUSAND_SEPARATOR_FORMAT));
    getLogger().debug("Filling CDFx statistics Report sheet");
    fillCdfxInfoSheet(workbook);
    getLogger().debug("Filling of CDFx statistics Report sheet completed....");

    getLogger().debug("Filling Project Information Sheet");
    fillProjectInfoSheet(workbook, variantName);
    getLogger().debug("Filling of Project Information Sheet completed....");
  }

  /**
   * @param workbook
   * @param variantName
   */
  private void fillProjectInfoSheet(final Workbook workbook, final String variantName) {
    // get the sheet
    final Sheet projectInfoSheet = workbook.getSheetAt(1);
    String[] projectInfoSheetHeader = ReportBOConstants.getProjectinfosheetheader();

    createProjInfoHeaderSheet(projectInfoSheetHeader, projectInfoSheet);
    fillProjInfoHeaderData(projectInfoSheetHeader, projectInfoSheet, variantName);
    final Row atrributesNValHeadingRow = ExcelCommon.getInstance().createExcelRow(projectInfoSheet, 4);

    // Attributes and Values Heading row
    ExcelCommon.getInstance().createHeaderCell(atrributesNValHeadingRow, "Attributes", ApicConstants.COLUMN_INDEX_0,
        this.tableHeadingCellStyle, this.headingFont);
    ExcelCommon.getInstance().createHeaderCell(atrributesNValHeadingRow, "Values", ApicConstants.COLUMN_INDEX_1,
        this.tableHeadingCellStyle, this.headingFont);

    // fill the attribute names and their corresponsing values
    fillAttrNamesNValues(projectInfoSheet);
  }

  /**
   * @param projectInfoSheet
   */
  private void fillAttrNamesNValues(final Sheet projectInfoSheet) {
    int rowNum = 5;

    // sort the attributes in alphabetical order
    Map<String, String> sortedAttrValMap = new TreeMap<>(this.cdFxProjInfoModel.getAttrValueMap());

    for (Map.Entry<String, String> attrNValEntry : sortedAttrValMap.entrySet()) {
      Row attrNValRow = ExcelCommon.getInstance().createExcelRow(projectInfoSheet, rowNum);
      ExcelCommon.getInstance().createCell(attrNValRow, attrNValEntry.getKey(), ApicConstants.COLUMN_INDEX_0,
          this.headerCellStyle);
      ExcelCommon.getInstance().createCell(attrNValRow, attrNValEntry.getValue(), ApicConstants.COLUMN_INDEX_1,
          this.cellStyle);
      rowNum++;
    }
    projectInfoSheet.autoSizeColumn(ApicConstants.COLUMN_INDEX_0);
    projectInfoSheet.autoSizeColumn(ApicConstants.COLUMN_INDEX_1);
  }

  /**
   * @param projectInfoSheetHeader
   * @param projectInfoSheet
   * @param variantName
   */
  private void fillProjInfoHeaderData(final String[] projectInfoSheetHeader, final Sheet projectInfoSheet,
      final String variantName) {
    for (int headerRow = 0; headerRow < projectInfoSheetHeader.length; headerRow++) {

      switch (projectInfoSheetHeader[headerRow]) {
        case ReportBOConstants.PROJECT:
          ExcelCommon.getInstance().createCell(projectInfoSheet.getRow(headerRow),
              CommonUtils.checkNull(this.cdFxProjInfoModel.getProjectName()), ApicConstants.COLUMN_INDEX_1,
              this.cellStyle);
          break;

        case ReportBOConstants.VARIANT:
          ExcelCommon.getInstance().createCell(projectInfoSheet.getRow(headerRow), CommonUtils.checkNull(variantName),
              ApicConstants.COLUMN_INDEX_1, this.cellStyle);
          break;

        case ReportBOConstants.SOFTWARE:
          ExcelCommon.getInstance().createCell(projectInfoSheet.getRow(headerRow),
              CommonUtils.checkNull(this.cdFxProjInfoModel.getSoftwareName()), ApicConstants.COLUMN_INDEX_1,
              this.cellStyle);
          break;
        default:
          break;
      }
    }
  }

  /**
   * @param projectInfoSheetHeader
   * @param projectInfoSheet
   */
  private void createProjInfoHeaderSheet(final String[] projectInfoSheetHeader, final Sheet projectInfoSheet) {

    for (int headerRow = 0; headerRow < (projectInfoSheetHeader.length); headerRow++) {
      final Row excelRow = ExcelCommon.getInstance().createExcelRow(projectInfoSheet, headerRow);
      ExcelCommon.getInstance().createCell(excelRow, projectInfoSheetHeader[headerRow], ApicConstants.COLUMN_INDEX_0,
          this.headerCellStyle);
    }
  }

  /**
   * @return
   */
  private ILoggerAdapter getLogger() {
    return this.logger;
  }

  /**
   * @param workbook
   * @param variant
   */
  private void fillCdfxInfoSheet(final Workbook workbook) {


    // get the sheet
    final Sheet wpRvwStatSheet = workbook.getSheetAt(0);

    int rowCount = 0;

    // Heading for a2l file parameter statistics
    final Row a2lHeadingRow = ExcelCommon.getInstance().createExcelRow(wpRvwStatSheet, rowCount);

    // Heading row
    ExcelCommon.getInstance().createHeaderCell(a2lHeadingRow, "Review Statistics in A2l file", 0,
        this.tableHeadingCellStyle, this.headingFont);

    // Table heading for a2l file parameter statistics
    tableHeadingForParamStat(wpRvwStatSheet, a2lHeadingRow);

    // Table data for a2l file parameter statistics
    final Row a2lDataRow = ExcelCommon.getInstance().createExcelRow(wpRvwStatSheet, 2);
    tableDataForParamStat(a2lDataRow);

    // Heading for WP parameter statistics
    final Row wpHeadingRow = ExcelCommon.getInstance().createExcelRow(wpRvwStatSheet, a2lDataRow.getRowNum() + 2);

    // Heading for WP
    ExcelCommon.getInstance().createHeaderCell(wpHeadingRow, "Review Statistics in Work Packages", 0,
        this.tableHeadingCellStyle, this.headingFont);


    // Table heading for WP parameter statistics
    final Row wpTableHeaderRow = ExcelCommon.getInstance().createExcelRow(wpRvwStatSheet, wpHeadingRow.getRowNum() + 1);
    String[] wpTableHeaderName = {
        ReportBOConstants.WORKPACKAGE_NAME,
        ReportBOConstants.RESP_TYPE,
        ReportBOConstants.RESPONSIBILITY_STR,
        ReportBOConstants.TOTAL,
        ReportBOConstants.RATING_NOT_REVIEWED,
        ReportBOConstants.RATING_PRELIM_CAL,
        ReportBOConstants.RATING_CALIBRATED,
        ReportBOConstants.RATING_CHECKED,
        ReportBOConstants.RATING_COMPLETED,
        ReportBOConstants.MISSING };

    for (int col = 0; col < wpTableHeaderName.length; col++) {
      ExcelCommon.getInstance().createCell(wpTableHeaderRow, wpTableHeaderName[col], col, this.headerCellStyle);
    }

    rowCount = createWorkPackageStatPage(wpRvwStatSheet, wpTableHeaderRow);

    // Auto resize columns in the sheet
    ExcelCommon.getInstance().autoResizeColumns(wpRvwStatSheet, ApicConstants.COLUMN_INDEX_2);

    // set default width to column 4 to column 8
    ExcelCommon.getInstance().setDefaultWidthToColumns(wpRvwStatSheet, ApicConstants.COLUMN_INDEX_3,
        ApicConstants.COLUMN_INDEX_8, 10);

    // Disable autoFilter when there are no rows
    if (rowCount > 0) {
      ExcelCommon.getInstance().setAutoFilter(wpRvwStatSheet,
          "A6:" + ExcelCommon.getInstance().getExcelColName(ApicConstants.COLUMN_INDEX_9), rowCount);
    }
  }


  /**
   * @param cellStyle2
   * @param wpRvwStatSheet
   * @param wpTableHeaderRow
   */
  private int createWorkPackageStatPage(final Sheet wpRvwStatSheet, final Row wpTableHeaderRow) {
    int wpRowNum = 0;
    createWpStatisticsAndParamCount();
    createWpParamRatingCountMap();
    wpRowNum = wpTableHeaderRow.getRowNum() + 1;
    // Iterate over the WP statistics in a2l file
    List<String> wpStatsList = new ArrayList<>(this.wpStatistics);
    int index = 0;
    for (String wpStatsInfo : wpStatsList) {
      String[] wpWithResp = wpStatsInfo.split(TEXT_SEPERATOR);
      final Row row = ExcelCommon.getInstance().createExcelRow(wpRvwStatSheet, wpRowNum);
      ExcelCommon.getInstance().createCell(row, wpWithResp[0], ApicConstants.COLUMN_INDEX_0, this.cellStyle);
      ExcelCommon.getInstance().createCell(row, wpWithResp[1], ApicConstants.COLUMN_INDEX_1, this.cellStyle);
      ExcelCommon.getInstance().createCell(row, wpWithResp[2], ApicConstants.COLUMN_INDEX_2, this.cellStyle);

      createCellForNum(row, this.wpParamCountMap.get(wpStatsList.get(index)), ApicConstants.COLUMN_INDEX_3);

      String wpRespKey = wpWithResp[0] + TEXT_SEPERATOR + wpWithResp[2];

      Map<Integer, Integer> ratingCountMap = this.wpWithRatingNParamCountMap.get(wpRespKey);

      createCellForNum(row, CommonUtils.checkNull(ratingCountMap.get(DATA_REVIEW_SCORE.RATING_NOT_REVIEWED)),
          ApicConstants.COLUMN_INDEX_4);
      createCellForNum(row, CommonUtils.checkNull(ratingCountMap.get(DATA_REVIEW_SCORE.RATING_PRELIM_CAL)),
          ApicConstants.COLUMN_INDEX_5);
      createCellForNum(row, CommonUtils.checkNull(ratingCountMap.get(DATA_REVIEW_SCORE.RATING_CALIBRATED)),
          ApicConstants.COLUMN_INDEX_6);
      createCellForNum(row, CommonUtils.checkNull(ratingCountMap.get(DATA_REVIEW_SCORE.RATING_CHECKED)),
          ApicConstants.COLUMN_INDEX_7);
      createCellForNum(row, CommonUtils.checkNull(ratingCountMap.get(DATA_REVIEW_SCORE.RATING_COMPLETED)),
          ApicConstants.COLUMN_INDEX_8);
      createCellForNum(row, CommonUtils.checkNull(this.missingParamCountMap.get(wpRespKey)),
          ApicConstants.COLUMN_INDEX_9);

      index++;
      wpRowNum++;
    }
    return wpRowNum;
  }


  /**
   *
   */
  private void createWpParamRatingCountMap() {
    Set<Integer> ratingSet = this.paramRvwStatusMap.keySet();
    Map<Integer, Integer> ratingCountMap;
    for (Integer rating : ratingSet) {

      for (String paramName : this.paramRvwStatusMap.get(rating)) {

        WpRespLabelResponse wpRespLabelResponse = this.relevantWpRespLabelMap.get(paramName);
        // get the work package name for the correspoding parameter
        String wpName = wpRespLabelResponse.getWpRespModel().getWpName();
        String respName = wpRespLabelResponse.getWpRespModel().getWpRespName();
        String wpRespKey = wpName + TEXT_SEPERATOR + respName;
        if (null == this.wpWithRatingNParamCountMap.get(wpRespKey)) {
          // when the work package and resp combination is new, the ratingCountMap should also be new
          ratingCountMap = new HashMap<>();
          ratingCountMap.put(rating, 1);
        }

        else {
          // get the ratingCountMap for the corresponding work package resp combination
          ratingCountMap = this.wpWithRatingNParamCountMap.get(wpRespKey);

          if (null == ratingCountMap.get(rating)) {
            ratingCountMap.put(rating, 1);
          }
          else {
            // increment the count of the rating
            int count = ratingCountMap.get(rating);
            ratingCountMap.put(rating, ++count);
          }
        }
        this.wpWithRatingNParamCountMap.put(wpRespKey, ratingCountMap);


      }
    }
  }

  /**
   *
   */
  private void createWpStatisticsAndParamCount() {

    this.wpParamCountMap = new HashMap<>();

    if (CommonUtils.isNotEmpty(this.relevantWpRespLabelMap)) {
      for (Entry<String, WpRespLabelResponse> entry : this.relevantWpRespLabelMap.entrySet()) {

        WpRespLabelResponse wpRespLabelResponse = entry.getValue();
        StringBuilder builder = new StringBuilder();
        // wp name --> resp type --> resp name
        builder.append(wpRespLabelResponse.getWpRespModel().getWpName());
        builder.append(TEXT_SEPERATOR);

        String respType = wpRespLabelResponse.getWpRespModel().getA2lResponsibility().getRespType();
        builder.append(WpRespType.getType(respType).getDispName());
        builder.append(TEXT_SEPERATOR);

        builder.append(wpRespLabelResponse.getWpRespModel().getWpRespName());

        this.wpStatistics.add(builder.toString());

        if (!this.wpParamCountMap.containsKey(builder.toString())) {
          this.wpParamCountMap.put(builder.toString(), 1);
        }
        else {
          int count = this.wpParamCountMap.get(builder.toString()) + 1;
          this.wpParamCountMap.put(builder.toString(), count);
        }

      }
    }
  }


  /**
   * @param cellStyle2
   * @param a2lDataRow
   */
  private void tableDataForParamStat(final Row a2lDataRow) {
    String a2lFileName = this.pidcA2l.getName();
    ExcelCommon.getInstance().createCell(a2lDataRow, a2lFileName, 0, this.cellStyle);
    createCellForNum(a2lDataRow, this.relevantWpRespLabelMap.size(), ApicConstants.COLUMN_INDEX_1);
  }


  private Cell createCellForNum(final Row row, final double cellValue, final int cellColumn) {
    final Cell cell = row.createCell(cellColumn);
    cell.setCellType(0);
    cell.setCellValue(cellValue);
    cell.setCellStyle(this.numCellStyle);
    return cell;
  }

  /**
   * @param wpRvwStatSheet
   * @param a2lHeadingRow
   */
  private void tableHeadingForParamStat(final Sheet wpRvwStatSheet, final Row a2lHeadingRow) {
    final Row a2lTableHeaderRow =
        ExcelCommon.getInstance().createExcelRow(wpRvwStatSheet, a2lHeadingRow.getRowNum() + 1);

    ExcelCommon.getInstance().createCell(a2lTableHeaderRow, A2L_FILE_NAME, ApicConstants.COLUMN_INDEX_0,
        this.headerCellStyle);
    ExcelCommon.getInstance().createCell(a2lTableHeaderRow, PARAMS_EXPORTED_IN_CDFX, ApicConstants.COLUMN_INDEX_1,
        this.headerCellStyle);
  }

}
