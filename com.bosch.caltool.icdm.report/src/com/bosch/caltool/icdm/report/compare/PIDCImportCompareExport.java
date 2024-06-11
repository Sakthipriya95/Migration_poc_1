/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.report.compare;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;

import com.bosch.caltool.excel.ExcelFactory;
import com.bosch.caltool.excel.ExcelFile;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValueType;
import com.bosch.caltool.icdm.model.apic.pidc.PidcSubVariantAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariantAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersion;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersionAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.ProjectImportAttr;
import com.bosch.caltool.icdm.report.Activator;
import com.bosch.caltool.icdm.report.common.ExcelCommon;
import com.bosch.caltool.icdm.report.common.ExcelCommonConstants;
import com.bosch.caltool.icdm.report.excel.ExcelClientConstants;

/**
 * The Class PIDCImportCompareExport.
 *
 * @author jvi6cob
 */
public class PIDCImportCompareExport {

  /** description column width. */
  private static final int DESCRIPTION_COLUMN_WIDTH = 14 * 256;

  /** partnumber column width. */
  private static final int PARTNUMBER_COLUMN_WIDTH = 16 * 256;

  /** spec link column width. */
  private static final int SPECLINK_COLUMN_WIDTH = 16 * 256;

  /** The header cell style. */
  private CellStyle headerCellStyle;

  /** The cell style. */
  private CellStyle cellStyle;

  /** The workbook. */
  private Workbook workbook;

  /** The font. */
  private Font font;

  /** The pidc import compare results. */
  private final Collection<ProjectImportAttr<?>> pidcImportCompareResults;

  /** The Constant VAR_NAME_COLUMN_WIDTH. */
  private static final int VAR_NAME_COLUMN_WIDTH = 11 * 256;

  /** The Constant SUBVAR_NAME_COLUMN_WIDTH. */
  private static final int SUBVAR_NAME_COLUMN_WIDTH = DESCRIPTION_COLUMN_WIDTH;

  /** The Constant NAME_COLUMN_WIDTH. */
  private static final int NAME_COLUMN_WIDTH = 22 * 256;

  /** The Constant DESC_COLUMN_WIDTH. */
  private static final int DESC_COLUMN_WIDTH = 22 * 256;

  /** The Constant USED_COLUMN_WIDTH. */
  private static final int USED_COLUMN_WIDTH = 9 * 256;

  /** The Constant VAL_COLUMN_WIDTH. */
  private static final int VAL_COLUMN_WIDTH = 11 * 256;

  /** The Constant DIFF_COLUMN_WIDTH. */
  private static final int DIFF_COLUMN_WIDTH = 9 * 256;

  /** The Constant COMMENTS_COLUMN_WIDTH. */
  private static final int COMMENTS_COLUMN_WIDTH = 13 * 256;

  /** The Constant IMPORT_STATUS_COLUMN_WIDTH. */
  private static final int IMPORT_STATUS_COLUMN_WIDTH = 15 * 256;

  /** Columns for PIDC attr compare sheet display. */
  private static final String[] attrCompShtHdr = new String[] {
      ExcelClientConstants.COMP_VARIANT,
      ExcelClientConstants.COMP_SUBVARIANT,
      ExcelClientConstants.RH_NAME,
      ExcelClientConstants.RH_DESCRIPTION,
      ExcelClientConstants.RH_USED,
      ExcelClientConstants.RH_VALUE,
      ExcelClientConstants.RH_PART_NUMBER,
      ExcelClientConstants.RH_SPEC_LINK,
      ExcelClientConstants.RH_DESCRIPTION,
      ExcelClientConstants.RH_USED,
      ExcelClientConstants.RH_VALUE,
      ExcelClientConstants.RH_PART_NUMBER,
      ExcelClientConstants.RH_SPEC_LINK,
      ExcelClientConstants.RH_DESCRIPTION,
      ExcelClientConstants.STATUS,
      ExcelClientConstants.COMMENTS,
      ExcelClientConstants.IMPORT_STATUS };

  /** The red cell style. */
  private CellStyle redCellStyle;

  /** The blue cell style. */
  private CellStyle blueCellStyle;

  /** The orange cell style. */
  private CellStyle orangeCellStyle;

  /** the starting count for the excel sheet of pidc compare. */
  private static final int ROW_STARTING_COUNT = 2;

  /** the row index for the excel sheet of pidc compare. */
  private static final int ROW_INDEX = 2;

  /** the cell index for the excel sheet of pidc compare. */
  private static final int PIDC_IMP_CMP_SHEET_CELL_INDEX = 4;

  /** the last column index for the excel sheet of pidc compare. */
  private static final int PIDC_IMP_SHEET_LAST_COL_INDEX = 4;

  /** the blank cell column index for the excel sheet of pidc compare. */
  private static final int BLANK_CELL_LAST_COL_INDEX = 9;

  /** the blank cell last column index for the excel sheet of pidc compare. */
  private static final int PIDIMP_BLANKCELL_LAST_COL_INDX = 9;

  /**
   * Instantiates a new PIDC import compare export.
   *
   * @param pidcImportCompareResults the pidc import compare results
   */
  public PIDCImportCompareExport(final Collection<ProjectImportAttr<?>> pidcImportCompareResults) {
    this.pidcImportCompareResults = pidcImportCompareResults;
  }

  /**
   * Export PIDC.
   *
   * @param pidcVer the pidc ver
   * @param filePath the file path
   * @param fileExtn the file extn
   */
  public void exportPIDC(final PidcVersion pidcVer, final String filePath, final String fileExtn) {
    // ICDM-169
    final String fileType = fileExtn;
    final ExcelFactory exFactory = ExcelFactory.getFactory(fileType);
    final ExcelFile xlFile = exFactory.getExcelFile();
    this.workbook = xlFile.createWorkbook();
    this.headerCellStyle = ExcelCommon.getInstance().createHeaderCellStyle(this.workbook);
    this.cellStyle = ExcelCommon.getInstance().createCellStyle(this.workbook);
    this.redCellStyle = ExcelCommon.getInstance().createRedCellStyle(this.workbook);
    this.blueCellStyle = ExcelCommon.getInstance().createBlueCellStyle(this.workbook);
    this.orangeCellStyle = ExcelCommon.getInstance().createOrangeCellStyle(this.workbook);
    this.font = ExcelCommon.getInstance().createFont(this.workbook);

    try {

      final String fileFullPath;
      // ICDM-199
      if (filePath.contains(".xlsx") || filePath.contains(".xls")) {
        fileFullPath = filePath;
      }
      else {
        fileFullPath = filePath + "." + fileExtn;
      }

      final FileOutputStream fileOut = new FileOutputStream(fileFullPath);

      // ICDM-169
      createImportCompareWorkSheet(pidcVer);

      this.workbook.write(fileOut);
      fileOut.flush();
      fileOut.close();

      String info = "Compare Report saved to path : ";
      info += fileFullPath;
      CDMLogger.getInstance().info(info, Activator.PLUGIN_ID);

    }
    catch (FileNotFoundException fnfe) {
      CDMLogger.getInstance().error(fnfe.getLocalizedMessage(), fnfe, Activator.PLUGIN_ID);
    }
    catch (IOException ioe) {
      CDMLogger.getInstance().error(ioe.getLocalizedMessage(), ioe, Activator.PLUGIN_ID);
    }

  }

  /**
   * Creates the import compare work sheet.
   *
   * @param pidcVer the pidc ver
   */
  private void createImportCompareWorkSheet(final PidcVersion pidcVer) {
    Sheet pidcAttrSheet = this.workbook.createSheet(pidcVer.getName() + " Import Compare");

    // construct First Header
    createHeaderForCompare(this.workbook, "Existing", "Imported", null, null, pidcAttrSheet, 0);

    // construct Second Header
    createCommonHeaderForCompare(this.workbook, pidcVer.getName(), null, pidcAttrSheet,
        ExcelClientConstants.INDEX_CONSTANT, 1);

    // construct Third Header
    final String[] sheetHeaders = PIDCImportCompareExport.attrCompShtHdr;
    final Row headerSecondRow = ExcelCommon.getInstance().createExcelRow(pidcAttrSheet, ROW_INDEX);
    for (int headerCol = 0; headerCol < sheetHeaders.length; headerCol++) {
      ExcelCommon.getInstance().createHeaderCell(headerSecondRow, sheetHeaders[headerCol], headerCol,
          this.headerCellStyle, this.font);
    }

    List<ProjectImportAttr<?>> pidcImportCompareResultsList = new ArrayList<>();
    pidcImportCompareResultsList.addAll(this.pidcImportCompareResults);
    Collections.sort(pidcImportCompareResultsList, new VarNameComparator());
    int rowCounter = ROW_STARTING_COUNT;
    for (ProjectImportAttr<?> pidcImportCompareResult : pidcImportCompareResultsList) {
      rowCounter++;
      constructRow(pidcAttrSheet, sheetHeaders, rowCounter, pidcImportCompareResult);
    }

    // set Sheet behavior
    setAutoFilterForPIDAttrCompareWorkSheet(pidcAttrSheet, rowCounter);
    pidcAttrSheet.createFreezePane(ExcelClientConstants.COLUMN_NUM_FOUR, ExcelClientConstants.ROW_NUM_THREE,
        ExcelClientConstants.COLUMN_NUM_FOUR, ExcelClientConstants.ROW_NUM_THREE);
    pidcAttrSheet.setColumnHidden(sheetHeaders.length, true);
    setColumnWidths(pidcAttrSheet);

    pidcAttrSheet.groupColumn(ExcelClientConstants.COLUMN_NUM_SIX, ExcelClientConstants.COLUMN_NUM_SEVEN);
    pidcAttrSheet.setColumnGroupCollapsed(ExcelClientConstants.COLUMN_NUM_SIX, true);

    pidcAttrSheet.groupColumn(ExcelClientConstants.COLUMN_NUM_ELEVEN, ExcelClientConstants.COLUMN_NUM_TWELEVE);
    pidcAttrSheet.setColumnGroupCollapsed(ExcelClientConstants.COLUMN_NUM_ELEVEN, true);
    // fit to page
    pidcAttrSheet.setFitToPage(true);

  }

  /**
   * The Class VarNameComparator.
   */
  private static class VarNameComparator implements Comparator<ProjectImportAttr<?>> {

    /**
     * {@inheritDoc}
     */
    @Override
    public int compare(final ProjectImportAttr<?> arg0, final ProjectImportAttr<?> arg1) {
      return arg0.getPidcAttr().getName().compareTo(arg1.getPidcAttr().getName());
    }

  }

  /**
   * Construct row.
   *
   * @param pidcAttrSheet the pidc attr sheet
   * @param sheetHeader the sheet header
   * @param rowCount the row count
   * @param pidcImportCompareResult the pidc import compare result
   */
  private void constructRow(final Sheet pidcAttrSheet, final String[] sheetHeader, final int rowCount,
      final ProjectImportAttr<?> pidcImportCompareResult) {
    final Row row = ExcelCommon.getInstance().createExcelRow(pidcAttrSheet, rowCount);
    fillColumnData(sheetHeader, pidcImportCompareResult, row);
  }

  /**
   * Fills the column data for the sheet.
   *
   * @param sheetHeader the sheet header
   * @param pidcImportCompareResult the pidc import compare result
   * @param row the row
   */
  private void fillColumnData(final String[] sheetHeader, final ProjectImportAttr<?> pidcImportCompareResult,
      final Row row) {
    for (int col = 0; col < sheetHeader.length; col++) {

      // fetches Data for columns SuperGroup,Group,Name,Description
      String result = colTxtFromPrjCompareAttr(col, pidcImportCompareResult);

      CellStyle localCellStyle = setCellStyle(sheetHeader, pidcImportCompareResult, col);

      // Attribute value type
      String valueType = null;
      if (pidcImportCompareResult.getPidcAttr() instanceof PidcVariantAttribute) {
        valueType = ((PidcVariantAttribute) pidcImportCompareResult.getPidcAttr()).getValueType();
      }
      else if (pidcImportCompareResult.getPidcAttr() instanceof PidcSubVariantAttribute) {
        valueType = ((PidcSubVariantAttribute) pidcImportCompareResult.getPidcAttr()).getValueType();
      }
      else if (pidcImportCompareResult.getPidcAttr() instanceof PidcVersionAttribute) {
        valueType = ((PidcVersionAttribute) pidcImportCompareResult.getPidcAttr()).getValueType();
      }

      if (validateResult(result) &&
          ((valueType != null) && (valueType.equals(AttributeValueType.HYPERLINK.toString()))) &&
          sheetHeader[col].equals(ExcelClientConstants.RH_VALUE)) {
        ExcelCommon.getInstance().createHyperLinkCell(this.workbook, row, result, col, this.cellStyle, false);
      }
      else {
        ExcelCommon.getInstance().createCell(row, result, col, localCellStyle);
      }
    }

  }

  /**
   * @param result
   * @return
   */
  private boolean validateResult(final String result) {
    return (result != null) && !(result.isEmpty()) && (!result.equals(ApicConstants.SUB_VARIANT_ATTR_DISPLAY_NAME)) &&
        (!result.equals(ApicConstants.VARIANT_ATTR_DISPLAY_NAME));
  }

  /**
   * Icdm-833 set the cell Style.
   *
   * @param sheetHeader the sheet header
   * @param pidcImportCompareResult the pidc import compare result
   * @param col the col
   * @return the cell style
   */
  private CellStyle setCellStyle(final String[] sheetHeader, final ProjectImportAttr<?> pidcImportCompareResult,
      final int col) {
    CellStyle localCellStyle = this.cellStyle;
    if (!pidcImportCompareResult.isValidImport()) {
      localCellStyle = this.redCellStyle;
    }
    else if (pidcImportCompareResult.isCreateAttr()) {
      localCellStyle = this.blueCellStyle;
    }
    else if (!pidcImportCompareResult.isCleared() && sheetHeader[col].equals(ExcelClientConstants.RH_VALUE)) {
      localCellStyle = this.orangeCellStyle;
    }
    return localCellStyle;
  }

  /**
   * Column text of Project id card table viewer from project attribute.
   *
   * @param columnIndex the column index
   * @param item the item
   * @return String
   */
  private String colTxtFromPrjCompareAttr(final int columnIndex, final ProjectImportAttr<?> item) {
    String result = ExcelCommonConstants.EMPTY_STRING;

    switch (columnIndex) {
      case ExcelClientConstants.COLUMN_NUM_ZERO:
        result = setTextForColumnZero(item);
        break;

      case ExcelClientConstants.COLUMN_NUM_ONE:
        if (item.getPidcAttr() instanceof PidcSubVariantAttribute) {
          result = ((PidcSubVariantAttribute) item.getPidcAttr()).getSubVariantName();
        }
        break;

      case ExcelClientConstants.COLUMN_NUM_TWO:
        result = item.getPidcAttr().getName();
        break;

      case ExcelClientConstants.COLUMN_NUM_THREE:
        result = item.getAttr().getDescription();
        break;

      case ExcelClientConstants.COLUMN_NUM_FOUR:
        result = item.getPidcAttr().getUsedFlag();
        break;

      case ExcelClientConstants.COLUMN_NUM_FIVE:
        result = item.getPidcAttr().getValue();
        break;

      case ExcelClientConstants.COLUMN_NUM_SIX:
        result = item.getPidcAttr().getPartNumber();
        break;

      case ExcelClientConstants.COLUMN_NUM_SEVEN:
        result = item.getPidcAttr().getSpecLink();
        break;

      case ExcelClientConstants.COLUMN_NUM_EIGHT:
        result = item.getPidcAttr().getDescription();
        break;

      case ExcelClientConstants.COLUMN_NUM_NINE:
        result = item.getExcelAttr().getUsedFlag();
        break;

      case ExcelClientConstants.COLUMN_NUM_TEN:
        result = item.getExcelAttr().getValue();
        break;

      case ExcelClientConstants.COLUMN_NUM_ELEVEN:
        result = item.getExcelAttr().getPartNumber();
        break;

      case ExcelClientConstants.COLUMN_NUM_TWELEVE:
        result = item.getExcelAttr().getSpecLink();
        break;

      case ExcelClientConstants.COLUMN_NUM_THIRTEEN:
        result = item.getExcelAttr().getDescription();
        break;

      // Data Status
      case ExcelClientConstants.COLUMN_NUM_FOURTEEN:
        result = item.isNewlyAddedVal() ? "NEW VALUE" : "MODIFIED";
        break;

      case ExcelClientConstants.COLUMN_NUM_FIFTEEN:
        result = item.getComment();
        break;

      // Import status
      case ExcelClientConstants.COLUMN_NUM_SIXTEEN:
        result = item.isValidImport() ? "SUCCESS" : "FAILED";
        break;

      default:
        break;
    }
    return result == null ? ApicConstants.EMPTY_STRING : result;
  }

  /**
   * @param item
   * @param result
   * @return
   */
  private String setTextForColumnZero(final ProjectImportAttr<?> item) {
    String result = ExcelCommonConstants.EMPTY_STRING;
    if (item.getPidcAttr() instanceof PidcVariantAttribute) {
      result = ((PidcVariantAttribute) item.getPidcAttr()).getVariantName();
    }
    else if (item.getPidcAttr() instanceof PidcSubVariantAttribute) {
      result = ((PidcSubVariantAttribute) item.getPidcAttr()).getVariantName();
    }
    return result;
  }

  /**
   * Creates the header for compare.
   *
   * @param workbook the workbook
   * @param name1 the name 1
   * @param name2 the name 2
   * @param revision1 the revision 1
   * @param revision2 the revision 2
   * @param pidcAttrSheet the pidc attr sheet
   * @param rowNum the row num
   */
  private void createHeaderForCompare(final Workbook workbook, final String name1, final String name2,
      final String revision1, final String revision2, final Sheet pidcAttrSheet, final int rowNum) {

    CellStyle localCellStyle = workbook.createCellStyle();
    localCellStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.index);
    localCellStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);


    localCellStyle.setBorderLeft(CellStyle.BORDER_THIN);
    localCellStyle.setBorderRight(CellStyle.BORDER_THIN);
    localCellStyle.setBorderBottom(CellStyle.BORDER_THIN);
    localCellStyle.setBorderTop(CellStyle.BORDER_THIN);
    localCellStyle.setAlignment(CellStyle.ALIGN_CENTER);
    localCellStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);

    Row headerRow0 = pidcAttrSheet.createRow(rowNum);
    int index1 = ExcelClientConstants.INDEX_1_CONSTANT;
    int index2 = ExcelClientConstants.INDEX_2_CONSTANT;

    Cell blankCell1 = headerRow0.createCell(index1 + PIDC_IMP_CMP_SHEET_CELL_INDEX);
    blankCell1.setCellStyle(localCellStyle);

    Cell blankCell2 = headerRow0.createCell(index2 + PIDC_IMP_CMP_SHEET_CELL_INDEX);
    blankCell2.setCellStyle(localCellStyle);
    //
    if (revision1 != null) {
      ExcelCommon.getInstance().createHeaderCell(headerRow0, name1 + "(" + revision1 + ")", index1, localCellStyle,
          this.font);
    }
    else {
      ExcelCommon.getInstance().createHeaderCell(headerRow0, name1, index1, localCellStyle, this.font);
    }
    if (revision2 != null) {
      ExcelCommon.getInstance().createHeaderCell(headerRow0, name2 + "(" + revision2 + ")", index2, localCellStyle,
          this.font);
    }
    else {
      ExcelCommon.getInstance().createHeaderCell(headerRow0, name2, index2, localCellStyle, this.font);
    }
    CellRangeAddress region = new CellRangeAddress(rowNum, rowNum, index1, index1 + PIDC_IMP_SHEET_LAST_COL_INDEX);
    pidcAttrSheet.addMergedRegion(region);
    CellRangeAddress region1 = new CellRangeAddress(rowNum, rowNum, index2, index2 + PIDC_IMP_SHEET_LAST_COL_INDEX);
    pidcAttrSheet.addMergedRegion(region1);
  }

  /**
   * Creates the common header for compare.
   *
   * @param workbook the workbook
   * @param name the name
   * @param revision the revision
   * @param pidcAttrSheet the pidc attr sheet
   * @param index the index
   * @param rowNum the row num
   */
  private void createCommonHeaderForCompare(final Workbook workbook, final String name, final String revision,
      final Sheet pidcAttrSheet, final int index, final int rowNum) {

    final CellStyle localCellStyle = workbook.createCellStyle();
    localCellStyle.setBorderLeft(CellStyle.BORDER_THIN);
    localCellStyle.setBorderRight(CellStyle.BORDER_THIN);
    localCellStyle.setBorderBottom(CellStyle.BORDER_THIN);
    localCellStyle.setBorderTop(CellStyle.BORDER_THIN);
    localCellStyle.setAlignment(CellStyle.ALIGN_CENTER);
    localCellStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
    localCellStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.index);
    localCellStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);

    final Row headerRow0 = pidcAttrSheet.createRow(rowNum);
    // Apply cell style to each cell to avoid incorrect border
    for (int j = index + 1; j <= (index + BLANK_CELL_LAST_COL_INDEX); j++) {
      final Cell blankCell = headerRow0.createCell(j);
      blankCell.setCellStyle(localCellStyle);
    }
    if (revision == null) {
      ExcelCommon.getInstance().createHeaderCell(headerRow0, name, index, localCellStyle, this.font);
    }
    else {
      ExcelCommon.getInstance().createHeaderCell(headerRow0, name + "(" + revision + ")", index, localCellStyle,
          this.font);
    }


    // Create merged region
    final CellRangeAddress region = new CellRangeAddress(rowNum, rowNum, index, index + PIDIMP_BLANKCELL_LAST_COL_INDX);
    pidcAttrSheet.addMergedRegion(region);

    headerRow0.getCell(index).setCellStyle(localCellStyle);

  }

  /**
   * Auto filter for project id car attribute work sheet.
   *
   * @param pidcAttrSheet the pidc attr sheet
   * @param rowCount the row count
   */
  private void setAutoFilterForPIDAttrCompareWorkSheet(final Sheet pidcAttrSheet, final int rowCount) {
    final String autoFilterRange = "A3:Q" + rowCount;
    pidcAttrSheet.setAutoFilter(CellRangeAddress.valueOf(autoFilterRange));
  }

  /**
   * Sets the column widths.
   *
   * @param pidcAttrSheet the new column widths
   */
  private void setColumnWidths(final Sheet pidcAttrSheet) {
    pidcAttrSheet.setColumnWidth(ExcelClientConstants.COLUMN_NUM_ZERO, VAR_NAME_COLUMN_WIDTH);
    pidcAttrSheet.setColumnWidth(ExcelClientConstants.COLUMN_NUM_ONE, SUBVAR_NAME_COLUMN_WIDTH);
    pidcAttrSheet.setColumnWidth(ExcelClientConstants.COLUMN_NUM_TWO, NAME_COLUMN_WIDTH);
    pidcAttrSheet.setColumnWidth(ExcelClientConstants.COLUMN_NUM_THREE, DESC_COLUMN_WIDTH);
    pidcAttrSheet.setColumnWidth(ExcelClientConstants.COLUMN_NUM_FOUR, USED_COLUMN_WIDTH);
    pidcAttrSheet.setColumnWidth(ExcelClientConstants.COLUMN_NUM_FIVE, VAL_COLUMN_WIDTH);
    pidcAttrSheet.setColumnWidth(ExcelClientConstants.COLUMN_NUM_SIX, PARTNUMBER_COLUMN_WIDTH);
    pidcAttrSheet.setColumnWidth(ExcelClientConstants.COLUMN_NUM_SEVEN, SPECLINK_COLUMN_WIDTH);
    pidcAttrSheet.setColumnWidth(ExcelClientConstants.COLUMN_NUM_EIGHT, DESCRIPTION_COLUMN_WIDTH);
    pidcAttrSheet.setColumnWidth(ExcelClientConstants.COLUMN_NUM_NINE, USED_COLUMN_WIDTH);
    pidcAttrSheet.setColumnWidth(ExcelClientConstants.COLUMN_NUM_TEN, VAL_COLUMN_WIDTH);
    pidcAttrSheet.setColumnWidth(ExcelClientConstants.COLUMN_NUM_ELEVEN, PARTNUMBER_COLUMN_WIDTH);
    pidcAttrSheet.setColumnWidth(ExcelClientConstants.COLUMN_NUM_TWELEVE, SPECLINK_COLUMN_WIDTH);
    pidcAttrSheet.setColumnWidth(ExcelClientConstants.COLUMN_NUM_THIRTEEN, DESCRIPTION_COLUMN_WIDTH);
    pidcAttrSheet.setColumnWidth(ExcelClientConstants.COLUMN_NUM_FOURTEEN, DIFF_COLUMN_WIDTH);
    pidcAttrSheet.setColumnWidth(ExcelClientConstants.COLUMN_NUM_FIFTEEN, COMMENTS_COLUMN_WIDTH);
    pidcAttrSheet.setColumnWidth(ExcelClientConstants.COLUMN_NUM_SIXTEEN, IMPORT_STATUS_COLUMN_WIDTH);
  }

}
