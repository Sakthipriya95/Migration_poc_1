/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.report.excel;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.DataValidation;
import org.apache.poi.ss.usermodel.DataValidationConstraint;
import org.apache.poi.ss.usermodel.DataValidationHelper;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.eclipse.core.runtime.IProgressMonitor;

import com.bosch.caltool.excel.ExcelFactory;
import com.bosch.caltool.excel.ExcelFile;
import com.bosch.caltool.icdm.client.bo.apic.PidcSubVariantBO;
import com.bosch.caltool.icdm.client.bo.apic.PidcVariantBO;
import com.bosch.caltool.icdm.client.bo.apic.PidcVersionBO;
import com.bosch.caltool.icdm.client.bo.general.CommonDataBO;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.util.DateFormat;
import com.bosch.caltool.icdm.common.util.Language;
import com.bosch.caltool.icdm.common.util.VersionValidator;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.ApicConstants.CLEARING_STATUS;
import com.bosch.caltool.icdm.model.apic.attr.AttrExportModel;
import com.bosch.caltool.icdm.model.apic.attr.Attribute;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValue;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValueType;
import com.bosch.caltool.icdm.model.apic.pidc.IProjectAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcSubVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcSubVariantAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariantAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersion;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersionAttribute;
import com.bosch.caltool.icdm.report.Activator;
import com.bosch.caltool.icdm.report.common.ExcelCommon;
import com.bosch.caltool.icdm.report.common.ExcelCommonConstants;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * @author rvu1cob
 */
public class ProjectIdCardExport {

  private static final int PIDC_CARD_EXPORT_BEGIN_TIME = 100;

  private static final int PIDC_CARD_EXPORT_TIME_10 = 10;

  private static final int PIDC_CARD_EXPORT_TIME_20 = 20;

  private static final int PIDC_CARD_EXPORT_TIME_30 = 30;

  private static final int PIDC_CARD_EXPORT_SHEET_NO = 3;

  private static final int PIDC_VER_NAME_SIZE = 31;

  private static final int PIDC_VER_END_INDEX = 27;

  private static final int VAR_ATTR_HEADER_COL_2 = 2;

  private static final int PIDC_EXP_SHT_NO_EXT = 2;
  /**
   *
   */
  private static final String RANGE_APPENDER = "$A$";
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
   * Red cell style
   */
  private CellStyle redCellStyle;

  /**
   * Orange cell style
   */
  private CellStyle orangeCellStyle;

  /**
   * Font instance
   */
  private Font font;

  private static final int TEMP_ROW_COUNT = 4;

  /**
   * Temporary sheet row count
   */
  private int tempRowCount = TEMP_ROW_COUNT;

  /**
   * ICDM-485 boolean which is true when the report is external
   */
  private boolean isReportExtrnl;


  private final PidcVersionBO pidcVersHndlr;

  private final AttrExportModel attrExportModel;

  private final Map<Long, String> valueRefMap = new HashMap<>();

  private final Map<Long, String[]> valueAsStrMap = new HashMap<>();

  /**
   * Constructor
   *
   * @param monitor IProgressMonitor
   * @param pidcVersHndlr Pidc Version Handler
   * @param attrExportModel Attribute Export Model
   */
  public ProjectIdCardExport(final IProgressMonitor monitor, final PidcVersionBO pidcVersHndlr,
      final AttrExportModel attrExportModel) {
    this.monitor = monitor;
    this.pidcVersHndlr = pidcVersHndlr;
    this.attrExportModel = attrExportModel;
  }

  /**
   * Export PIDC to Excel
   *
   * @param filePath destination
   * @param fileExtn file extension
   * @param filteredAttrIdSet filtered set of attribute IDs
   * @param filtered Filtered attributes flag
   * @param flagReportExtrnl is external report
   * @return export status
   */
  public boolean exportPIDC(final String filePath, final String fileExtn, final Set<Long> filteredAttrIdSet,
      final boolean filtered, final boolean flagReportExtrnl) {

    getCdmLogger().info("Exporting PIDC Version {} to excel started", this.pidcVersHndlr.getPidcVersion().getName());

    this.isReportExtrnl = flagReportExtrnl;

    this.tempRowCount = TEMP_ROW_COUNT;
    // ICDM-169
    this.monitor.beginTask("", PIDC_CARD_EXPORT_BEGIN_TIME);

    final String fileType = fileExtn;
    final ExcelFactory exFactory = ExcelFactory.getFactory(fileType);
    final ExcelFile xlFile = exFactory.getExcelFile();
    final Workbook workbook = xlFile.createWorkbook();

    // Main sheets are created before Temp sheet
    createSheets(workbook, this.pidcVersHndlr.getPidcVersion());

    this.headerCellStyle = ExcelCommon.getInstance().createHeaderCellStyle(workbook);
    this.cellStyle = ExcelCommon.getInstance().createCellStyle(workbook);
    this.redCellStyle = ExcelCommon.getInstance().createRedCellStyle(workbook);
    this.orangeCellStyle = ExcelCommon.getInstance().createOrangeCellStyle(workbook);
    this.font = ExcelCommon.getInstance().createFont(workbook);


    String fileFullPath;
    // ICDM-199
    if (filePath.contains(".xlsx") || filePath.contains(".xls")) {
      fileFullPath = filePath;
    }
    else {
      fileFullPath = filePath + "." + fileExtn;
    }
    // Temp sheet is created at last to avoid sheet name tab overlap
    final Sheet tempAttrSheet = ExcelCommon.getInstance().createSheet(workbook, ExcelClientConstants.TEMP);
    // ICDM-169
    this.monitor.worked(PIDC_CARD_EXPORT_TIME_10);

    this.monitor.subTask("Exporting PIDC attributes . . .");
    createPIDCAttrWorkSheet(workbook, tempAttrSheet, filteredAttrIdSet, filtered);

    getCdmLogger().debug("PIDC Attribute work sheet created");
    this.monitor.worked(PIDC_CARD_EXPORT_TIME_30);

    this.monitor.subTask("Exporting Variant and Sub-Variant attributes . . .");
    createVarSvarAttrSheet(workbook, ExcelClientConstants.VARIANTS, tempAttrSheet, filteredAttrIdSet);

    getCdmLogger().debug("Variant-SubVariant Attribute work sheet created");
    this.monitor.worked(PIDC_CARD_EXPORT_TIME_30);

    if (this.isReportExtrnl) {
      // Hide the temporary sheet
      workbook.setSheetHidden(PIDC_EXP_SHT_NO_EXT, true);
    }
    else {
      // ICDM-2285
      this.monitor.subTask("Exporting attributes . . .");
      createAttrWorkSheet(workbook, ExcelClientConstants.ATTRIBUTES, filteredAttrIdSet);
      getCdmLogger().debug("Attribute work sheet created");
      this.monitor.worked(PIDC_CARD_EXPORT_TIME_20);
      // Hide the temporary sheet
      workbook.setSheetHidden(PIDC_CARD_EXPORT_SHEET_NO, true);
    }

    this.monitor.subTask("Finishing export . . .");
    workbook.setSelectedTab(0);

    try (OutputStream fileOut = new FileOutputStream(fileFullPath);) {
      getCdmLogger().info("Writing workbook to file. path : {}", fileFullPath);
      workbook.write(fileOut);

      getCdmLogger().info("Exporting PIDC Version {} to excel completed",
          this.pidcVersHndlr.getPidcVersion().getName());

    }
    catch (IOException exp) {
      getCdmLogger().errorDialog(
          exp.getMessage() + ". Please check whether the directory is accessible and file is not already open.", exp,
          Activator.PLUGIN_ID);
      return false;
    }

    this.monitor.worked(PIDC_CARD_EXPORT_TIME_10);// Reached 100
    return true;

  }


  /**
   * @param pidcVer pidc version
   */
  private void createSheets(final Workbook workbook, final PidcVersion pidcVer) {
    final String sheetName = getSheetName(pidcVer);
    // create pidcAttrSheet
    workbook.createSheet(sheetName);
    // create varAttrSheet
    workbook.createSheet(ExcelClientConstants.VARIANTS);
    if (!this.isReportExtrnl) {
      // ICDM-2285
      // create attributesSheet
      workbook.createSheet(ExcelClientConstants.ATTRIBUTES);
    }
  }

  /**
   * @param pidcVer
   * @return
   */
  private String getSheetName(final PidcVersion pidcVer) {
    String verName = pidcVer.getName().substring(0, pidcVer.getName().length() - 1);
    // ICDM-925
    // If PIDC name greater than 31 characters show just the first 28 signs of the PIDC name
    if (verName.length() > PIDC_VER_NAME_SIZE) {
      // ICDM-1446
      return verName.substring(0, PIDC_VER_END_INDEX).replaceAll("[^a-zA-Z0-9]+", "_") + "...";
    }
    return verName.replaceAll("[^a-zA-Z0-9]+", "_");
  }

  /**
   * Create Project id card attribute worksheet
   *
   * @param workbook
   * @param pidcVer
   * @param tempAttrSheet
   * @param filteredAttrMap
   * @param filtered Filtered attr flag
   * @throws IOException
   */
  private void createPIDCAttrWorkSheet(final Workbook workbook, final Sheet tempAttrSheet,
      final Set<Long> filteredAttrIdSet, final boolean filtered) {

    final String[] pidAttrShtHdr = PIDCExcelColumn.SHT_HDR_PIDC;
    PidcVersion pidcVer = this.pidcVersHndlr.getPidcVersion();
    final String sheetName = getSheetName(pidcVer);
    final Sheet pidcAttrSheet = workbook.getSheet(sheetName);

    final Row headerRow = ExcelCommon.getInstance().createExcelRow(pidcAttrSheet, 0);
    for (int headerCol = 0; headerCol < (pidAttrShtHdr.length + 1); headerCol++) {
      String cellValue;
      if (headerCol == pidAttrShtHdr.length) {
        Language currentLanguage = new CommonDataBO().getLanguage();
        String filterFlag = ApicConstants.NOT_A_FILTERED_ATTR_EXCEL;
        String reportTypeFlag = ApicConstants.INTERNAL_REPORT;
        if (filtered) {
          filterFlag = ApicConstants.FILTERED_ATTR_EXCEL;
        }
        if (this.isReportExtrnl) {
          reportTypeFlag = ApicConstants.EXTERNAL_REPORT;
        }
        VersionValidator validator = new VersionValidator(null, false);
        String icdmVers = "";
        try {
          icdmVers = new CommonDataBO().getIcdmVersion();
        }
        catch (ApicWebServiceException e) {
          getCdmLogger().warn(e.getMessage(), e, Activator.PLUGIN_ID);
        }
        cellValue =
            pidcVer.getId() + "##" + currentLanguage.getText() + "##" + pidcVer.getPidcId() + "##" + filterFlag + "##" +
                reportTypeFlag + "##" + pidcVer.getProRevId() + "##" + validator.getVersionAsNumber(icdmVers); // ICDM-1445
        // ICDM-2229
      }
      else {
        cellValue = pidAttrShtHdr[headerCol];
      }
      ExcelCommon.getInstance().createHeaderCell(headerRow, cellValue, headerCol, this.headerCellStyle, this.font);
    }

    // ICDM-743
    SortedSet<PidcVersionAttribute> pidcAttrSet =
        new TreeSet<>((p1, p2) -> this.pidcVersHndlr.compare(p1, p2, ApicConstants.SORT_SUPERGROUP));

    // Remove invisible attributes
    this.pidcVersHndlr.getPidcDataHandler().getPidcVersAttrMap().entrySet().forEach(entry -> {
      if (!this.pidcVersHndlr.getPidcDataHandler().getPidcVersInvisibleAttrSet()
          .contains(entry.getValue().getAttrId())) {
        pidcAttrSet.add(entry.getValue());
      }
    });


    if (this.isReportExtrnl || ((null != filteredAttrIdSet) && !filteredAttrIdSet.isEmpty())) {
      removeInvisibleAttributes(filteredAttrIdSet, pidcAttrSet);
    }

    int rowCount = 0;

    for (IProjectAttribute pidcAttr : pidcAttrSet) {
      rowCount++;
      final Row row = ExcelCommon.getInstance().createExcelRow(pidcAttrSheet, rowCount);

      for (int col = 0; col <= pidAttrShtHdr.length; col++) {
        createPidcAttrCol(workbook, tempAttrSheet, pidAttrShtHdr, pidcAttrSheet, pidcAttr, row, col);
      }
    }
    // Disable autoFilter when there are no rows
    if (rowCount > 0) {
      ExcelCommon.getInstance().setAutoFilter(pidcAttrSheet, PIDCExcelColumn.PID_AUTOFLTRRNG, rowCount);
    }
    pidcAttrSheet.createFreezePane(0, 1, 0, 1);
    pidcAttrSheet.setColumnHidden(pidAttrShtHdr.length, true);
    // ICdm-1004 moved Set col size below the Group Column
    pidcAttrSheet.groupColumn(ExcelClientConstants.COLUMN_NUM_SEVEN, ExcelClientConstants.COLUMN_NUM_EIGHT);
    pidcAttrSheet.setColumnGroupCollapsed(ExcelClientConstants.COLUMN_NUM_SEVEN, true);
    ExcelCommon.getInstance().setColSize(pidAttrShtHdr, pidcAttrSheet);

  }

  /**
   * Create columns in PIDC sheet
   *
   * @param workbook
   * @param tempAttrSheet
   * @param pidAttrShtHdr
   * @param pidcAttrSheet
   * @param pidcAttr
   * @param row
   * @param col
   */
  private void createPidcAttrCol(final Workbook workbook, final Sheet tempAttrSheet, final String[] pidAttrShtHdr,
      final Sheet pidcAttrSheet, final IProjectAttribute pidcAttr, final Row row, final int col) {

    Attribute attr = this.pidcVersHndlr.getPidcDataHandler().getAttribute(pidcAttr.getAttrId());

    if ((col < pidAttrShtHdr.length) && pidAttrShtHdr[col].equals(ExcelClientConstants.RH_VALUE)) {
      createPidcAttrValueCol(workbook, pidcAttr, pidcAttrSheet, row, col, tempAttrSheet);
    }
    else if ((col < pidAttrShtHdr.length) && pidAttrShtHdr[col].equals(ExcelClientConstants.RH_USED)) {
      ExcelCommon.getInstance().createCell(row,
          ApicConstants.PROJ_ATTR_USED_FLAG.getType(pidcAttr.getUsedFlag()).getUiType(), col, this.cellStyle);
    }
    else if ((col < pidAttrShtHdr.length) && pidAttrShtHdr[col].equals(ExcelClientConstants.RH_SPEC_LINK)) {
      ExcelCommon.getInstance().createHyperLinkCell(workbook, row, pidcAttr.getSpecLink(), col, this.cellStyle, false);
    }
    else if ((col < pidAttrShtHdr.length) && pidAttrShtHdr[col].equals(ExcelClientConstants.RH_ATTR_CREATED_DATE)) {
      // ICDM-2229
      CellStyle dateCellStyle = ExcelCommon.getInstance().createCellStyle(workbook);
      CreationHelper createHelper = workbook.getCreationHelper();
      dateCellStyle.setDataFormat(createHelper.createDataFormat().getFormat(DateFormat.DATE_FORMAT_09));
      Cell cell = row.createCell(col);
      try {
        Calendar creDate = DateFormat.convertStringToCalendar(DateFormat.DATE_FORMAT_15, attr.getCreatedDate());
        cell.setCellValue(creDate.getTime());
      }
      catch (IcdmException e) {
        getCdmLogger().warn(e.getMessage(), e);
        cell.setCellValue(attr.getCreatedDate());
      }
      cell.setCellStyle(dateCellStyle);
    }

    // Store Hidden Attr Id
    else if (col == pidAttrShtHdr.length) {
      String cellValue = String.valueOf(pidcAttr.getAttrId());
      ExcelCommon.getInstance().createCell(row, cellValue, col, this.cellStyle);
    }
    else {
      CellStyle styleToUse = this.cellStyle;
      final StringBuilder isAttrValDeleted = new StringBuilder();
      // Icdm-833 orange Style
      final StringBuilder isAttrValCleared = new StringBuilder();
      final String result = PIDCExcelColumn.getInstance().getColTxtFromPrjAttr(this.pidcVersHndlr, col, pidcAttr,
          isAttrValDeleted, isAttrValCleared);
      if (Boolean.parseBoolean(isAttrValDeleted.toString())) {
        styleToUse = this.redCellStyle;
      }
      else if ((isAttrValCleared.length() > 0) && !Boolean.parseBoolean(isAttrValCleared.toString())) {
        styleToUse = this.orangeCellStyle;
      }
      ExcelCommon.getInstance().createCell(row, result, col, styleToUse);
    }
  }

  /**
   * Create value column in project id card attribute sheet
   *
   * @param workbook
   * @param pidcAttr
   * @param pidcAttrSheet
   * @param row
   * @param col
   * @param tempAttrSheet
   */
  private void createPidcAttrValueCol(final Workbook workbook, final IProjectAttribute pidcAttr,
      final Sheet pidcAttrSheet, final Row row, final int col, final Sheet tempAttrSheet) {

    Attribute attr = this.pidcVersHndlr.getPidcDataHandler().getAttribute(pidcAttr.getAttrId());

    String[] valList = getValuesAsStrArr(attr);

    final DataFormat format = workbook.createDataFormat();
    final DataValidationHelper dvHelper = tempAttrSheet.getDataValidationHelper();

    CellStyle styleToUse = this.cellStyle;
    StringBuilder isAttrValDeleted = new StringBuilder();
    // Icdm-833 orange Style
    StringBuilder isAttrValCleared = new StringBuilder();
    String result = PIDCExcelColumn.getInstance().getColTxtFromPrjAttr(this.pidcVersHndlr, col, pidcAttr,
        isAttrValDeleted, isAttrValCleared);
    if (Boolean.parseBoolean(isAttrValDeleted.toString())) {
      styleToUse = this.redCellStyle;
    }
    else if ((isAttrValCleared.length() > 0) && !Boolean.parseBoolean(isAttrValCleared.toString())) {
      styleToUse = this.orangeCellStyle;
    }

    if (result != null) {
      AttributeValueType valueType = AttributeValueType.getType(attr.getValueType());
      if (((attr.getLevel() > 0) || (attr.getLevel().intValue() == ApicConstants.PROJECT_NAME_ATTR)) ||
          (valList.length == 0) ||
          (result.equalsIgnoreCase(ExcelClientConstants.VARIANT) ||
              result.equalsIgnoreCase(ExcelClientConstants.SUB_VARIANT)) ||
          ((valList.length == 1) && (null != valList[0]) && valList[0].equalsIgnoreCase(result)) ||
          ((valList.length > 0) && (!attr.isExternalValue() && this.isReportExtrnl))) {// ICDM-485
        // ICDM-323
        if ((valueType == AttributeValueType.HYPERLINK) &&
            (!result.equals(ApicConstants.SUB_VARIANT_ATTR_DISPLAY_NAME)) &&
            (!result.equals(ApicConstants.VARIANT_ATTR_DISPLAY_NAME))) {
          ExcelCommon.getInstance().createHyperLinkCell(workbook, row, result, col, styleToUse, false);
        }
        else {
          ExcelCommon.getInstance().createCell(row, result, col, styleToUse);
        }
      }

      else if ((valList.length > 0) && !this.isReportExtrnl/* ICDM-2285 */) {
        if (valueType != AttributeValueType.HYPERLINK) {
          // drop down should not be added for hyperlink type of attr values
          DataValidation dataValidation = createDropdownForVal(row, col, tempAttrSheet, valList, dvHelper, attr);
          ExcelCommon.getInstance().setsuppressDropDown(dataValidation);
          pidcAttrSheet.addValidationData(dataValidation);
        }

        // ICDM-323
        Cell cell = null;
        // Drop down default selection
        if (valueType == AttributeValueType.HYPERLINK) {
          if (result.isEmpty()) {
            cell = row.createCell(col);
            cell.setCellStyle(ExcelCommon.getInstance().hyperLinkStyle(workbook, false));
          }
          else {
            ExcelCommon.getInstance().createHyperLinkCell(workbook, row, result, col, styleToUse, false);
          }
        }
        else {
          cell = ExcelCommon.getInstance().createCell(row, result, col, styleToUse);
        }
        if ((attr.getFormat() != null) && (cell != null)) {
          cell.getCellStyle().setDataFormat(format.getFormat(attr.getFormat()));
        }
      }
      else {
        ExcelCommon.getInstance().createCell(row, "", col, styleToUse);
      }
    }
    else if ((valList.length > 0) && !this.isReportExtrnl) {// ICDM-485
      if (!isHyperLinkAttr(attr)) {
        // drop down should not be added for hyperlink type of attr values
        DataValidation dataValidation = createDropdownForVal(row, col, tempAttrSheet, valList, dvHelper, attr);
        ExcelCommon.getInstance().setsuppressDropDown(dataValidation);
        pidcAttrSheet.addValidationData(dataValidation);
      }
      // Drop down default selection
      Cell cell = ExcelCommon.getInstance().createCell(row, ExcelCommonConstants.EMPTY_STRING, col, styleToUse);
      if (attr.getFormat() != null) {
        cell.getCellStyle().setDataFormat(format.getFormat(attr.getFormat()));
      }
    }
    else {
      ExcelCommon.getInstance().createCell(row, "", col, styleToUse);
    }
  }

  private boolean isHyperLinkAttr(final Attribute attr) {
    // return true if attribute's valuetype is of hyperlink
    AttributeValueType valueType = AttributeValueType.getType(attr.getValueType());
    return valueType == AttributeValueType.HYPERLINK;
  }

  private String[] getValuesAsStrArr(final Attribute attr) {
    if (!this.valueAsStrMap.containsKey(attr.getId())) {
      final SortedSet<AttributeValue> attrValSet = getAttrValues(attr.getId());
      String[] valArr = new String[attrValSet.size()];
      int indx = 0;
      for (AttributeValue val : attrValSet) {
        valArr[indx] = val.getNameRaw();
        indx++;
      }
      this.valueAsStrMap.put(attr.getId(), valArr);
    }
    return this.valueAsStrMap.get(attr.getId());
  }

  /**
   * create variants and sub-variants attributes work sheet
   *
   * @param workbook
   * @param sheetName
   * @param pidcVer
   * @param tempAttrSheet
   * @param filteredAttrMap
   * @throws IOException
   */
  private void createVarSvarAttrSheet(final Workbook workbook, final String sheetName, final Sheet tempAttrSheet,
      final Set<Long> filteredAttrIdSet) {

    final String[] sheetHdr = PIDCExcelColumn.SHT_HDR_VAR_SVAR;

    final Sheet varAttrSheet = workbook.getSheet(sheetName);
    varAttrSheet.setRowSumsBelow(false);

    final Row headerRow = ExcelCommon.getInstance().createExcelRow(varAttrSheet, 0);
    for (int headerCol = 0; headerCol <= (sheetHdr.length + VAR_ATTR_HEADER_COL_2); headerCol++) {
      String headerCellValue = null;
      if (headerCol == (sheetHdr.length)) {
        headerCellValue = "Var/SubVarID";
      }
      else if (headerCol == (sheetHdr.length + 1)) {
        headerCellValue = "Attr_ID";
      }
      if (headerCol < sheetHdr.length) {
        headerCellValue = sheetHdr[headerCol];
      }
      ExcelCommon.getInstance().createHeaderCell(headerRow, headerCellValue, headerCol, this.headerCellStyle,
          this.font);
    }
    int rowCount = 0;
    int varRow;
    int tempVarRowCount;

    final SortedSet<PidcVariant> variantsSet = this.pidcVersHndlr.getVariantsSet(false);

    for (PidcVariant pidcVariant : variantsSet) {

      rowCount++;

      final Row varHeaderRow = ExcelCommon.getInstance().createExcelRow(varAttrSheet, rowCount);
      varRow = 1;
      ExcelCommon.getInstance().varHeaderRow(workbook, sheetHdr, varAttrSheet, varRow, pidcVariant, varHeaderRow);
      tempVarRowCount = rowCount + 1;

      // ICDM-163
      PidcVariantBO varHandler =
          new PidcVariantBO(this.pidcVersHndlr.getPidcVersion(), pidcVariant, this.pidcVersHndlr.getPidcDataHandler());
      // TODO does this provide only valid attributes?
      SortedSet<PidcVariantAttribute> varattributesWithInvisible =
          varHandler.getAttributes(ApicConstants.SORT_SUPERGROUP);

      SortedSet<PidcVariantAttribute> varattributes = new TreeSet<>();
      // Remove invisible attributes
      varattributesWithInvisible.forEach(varAttr -> {
        if (!this.pidcVersHndlr.getPidcDataHandler().getVariantInvisbleAttributeMap().get(pidcVariant.getId())
            .contains(varAttr.getAttrId())) {
          varattributes.add(varAttr);
        }
      });

      if (this.isReportExtrnl || ((null != filteredAttrIdSet) && !filteredAttrIdSet.isEmpty())) {
        removeInvisibleAttributes(filteredAttrIdSet, varattributes);
      }

      for (IProjectAttribute varAttr : varattributes) {
        rowCount++;
        final Row varAttrRow = ExcelCommon.getInstance().createExcelRow(varAttrSheet, rowCount);

        for (int col = 0; col <= (sheetHdr.length + 1); col++) {
          createVarCol(pidcVariant, workbook, varAttrSheet, tempAttrSheet, sheetHdr, varAttrRow, col, varAttr);
        }
      }
      rowCount = createSubVarAndAttr(workbook, pidcVariant, sheetHdr, varAttrSheet, rowCount, tempAttrSheet,
          filteredAttrIdSet);
      varAttrSheet.groupRow(tempVarRowCount, rowCount);
    }


    // Disable autoFilter when there are no rows
    if (rowCount > 0) {
      ExcelCommon.getInstance().setAutoFilter(varAttrSheet, PIDCExcelColumn.SVAR_AUTOFLTRRNG, rowCount);
    }
    varAttrSheet.createFreezePane(0, 1, 0, 1);

    varAttrSheet.setColumnHidden(sheetHdr.length, true);
    varAttrSheet.setColumnHidden(sheetHdr.length + 1, true);

    ExcelCommon.getInstance().setColSize(sheetHdr, varAttrSheet);
    varAttrSheet.groupColumn(ExcelClientConstants.COLUMN_NUM_SEVEN, ExcelClientConstants.COLUMN_NUM_EIGHT);
    varAttrSheet.setColumnGroupCollapsed(ExcelClientConstants.COLUMN_NUM_SEVEN, true);
  }

  /**
   * Create variant cell
   *
   * @param pidcVariant
   * @param workbook
   * @param varAttrSheet
   * @param tempAttrSheet
   * @param sheetHdr
   * @param varAttrRow
   * @param col
   * @param varAttr
   */
  private void createVarCol(final PidcVariant pidcVariant, final Workbook workbook, final Sheet varAttrSheet,
      final Sheet tempAttrSheet, final String[] sheetHdr, final Row varAttrRow, final int col,
      final IProjectAttribute varAttr) {

    CellStyle styleToUse = this.cellStyle;
    StringBuilder isAttrValDeleted = new StringBuilder();
    StringBuilder isAttrValCleared = new StringBuilder();

    if (Boolean.parseBoolean(isAttrValDeleted.toString())) {
      styleToUse = this.redCellStyle;
    }
    else if ((isAttrValCleared.length() > 0) && !(Boolean.parseBoolean(isAttrValCleared.toString()))) {
      styleToUse = this.orangeCellStyle;
    }

    if (col == (sheetHdr.length + 1)) {
      // Hidden column Attribute ID
      ExcelCommon.getInstance().createCell(varAttrRow, String.valueOf(varAttr.getAttrId()), col, this.cellStyle);
    }

    if (col >= sheetHdr.length) {
      return;
    }
    final String result = PIDCExcelColumn.getInstance().getColTxtFromVarAttr(this.pidcVersHndlr, col, varAttr,
        isAttrValDeleted, isAttrValCleared);
    if (col == 0) {
      ExcelCommon.getInstance().createHeaderCell(varAttrRow, pidcVariant.getName(), col, this.headerCellStyle,
          this.font);
    }
    else if (sheetHdr[col].equals(ExcelClientConstants.RH_VALUE)) {
      createVarValueCol(workbook, varAttrSheet, tempAttrSheet, varAttrRow, col, varAttr, styleToUse, result);
    }
    else if (sheetHdr[col].equals(ExcelClientConstants.RH_USED)) {
      ExcelCommon.getInstance().createCell(varAttrRow,
          ApicConstants.PROJ_ATTR_USED_FLAG.getType(varAttr.getUsedFlag()).getUiType(), col, this.cellStyle);
    }
    else if ((col < sheetHdr.length) && sheetHdr[col].equals(ExcelClientConstants.RH_SPEC_LINK)) {
      ExcelCommon.getInstance().createHyperLinkCell(workbook, varAttrRow, result, col, this.cellStyle, false);
    }
    else {
      ExcelCommon.getInstance().createCell(varAttrRow, result, col, this.cellStyle);
    }

  }

  /**
   * Create cell of variant value
   *
   * @param workbook
   * @param varAttrSheet
   * @param tempAttrSheet
   * @param varAttrRow
   * @param col
   * @param varAttr
   * @param styleToUse
   * @param result
   */
  private void createVarValueCol(final Workbook workbook, final Sheet varAttrSheet, final Sheet tempAttrSheet,
      final Row varAttrRow, final int col, final IProjectAttribute varAttr, final CellStyle styleToUse,
      final String result) {
    Attribute attr = this.pidcVersHndlr.getPidcDataHandler().getAttribute(varAttr.getAttrId());
    String[] valList = getValuesAsStrArr(attr);
    final DataValidationHelper dvHelper = varAttrSheet.getDataValidationHelper();
    // ICDM-2285
    if (!this.isReportExtrnl && (valList.length > 0)) {
      if (attr.getLevel() > 0) {
        if ((attr.getLevel().intValue() != ApicConstants.VARIANT_CODE_ATTR) && !isHyperLinkAttr(attr)) {
          final DataValidation dataValidation =
              createDropdownForVal(varAttrRow, col, tempAttrSheet, valList, dvHelper, attr);
          ExcelCommon.getInstance().setsuppressDropDown(dataValidation);
          varAttrSheet.addValidationData(dataValidation);
        }
      }
      else {
        // ICDM-323
        if (!result.equals(ApicConstants.SUB_VARIANT_ATTR_DISPLAY_NAME) && !isHyperLinkAttr(attr)) {
          final DataValidation dataValidation =
              createDropdownForVal(varAttrRow, col, tempAttrSheet, valList, dvHelper, attr);
          ExcelCommon.getInstance().setsuppressDropDown(dataValidation);
          varAttrSheet.addValidationData(dataValidation);
        }
      }
    }

    Cell cell = null;

    if (result == null) {
      cell = ExcelCommon.getInstance().createCell(varAttrRow, ExcelCommonConstants.EMPTY_STRING, col, styleToUse);
    }
    else {
      // ICDM-323
      AttributeValueType valueType = AttributeValueType.getType(attr.getValueType());
      if ((valueType == AttributeValueType.HYPERLINK) && !result.equals(ApicConstants.SUB_VARIANT_ATTR_DISPLAY_NAME)) {
        if (result.isEmpty()) {
          cell = varAttrRow.createCell(col);
          cell.setCellStyle(ExcelCommon.getInstance().hyperLinkStyle(workbook, false));
        }
        else {
          ExcelCommon.getInstance().createHyperLinkCell(workbook, varAttrRow, result, col, styleToUse, false);
        }
      }
      else {
        cell = ExcelCommon.getInstance().createCell(varAttrRow, result, col, styleToUse);
      }
    }

    final String valFormat = attr.getFormat();
    final DataFormat format = workbook.createDataFormat();
    if ((valFormat != null) && (cell != null)) {

      cell.getCellStyle().setDataFormat(format.getFormat(valFormat));
    }
  }

  /**
   * Create sub-variant and its attributes
   *
   * @param workbook
   * @param pidcVariant
   * @param varNSVarAtrShtHdr
   * @param pidcAttrSheet
   * @param rowCount
   * @param tempAttrSheet
   * @param filteredAttrMap
   * @return
   */
  private int createSubVarAndAttr(final Workbook workbook, final PidcVariant pidcVariant,
      final String[] varNSVarAtrShtHdr, final Sheet pidcAttrSheet, final int rowCnt, final Sheet tempAttrSheet,
      final Set<Long> filteredAttrIdSet) {
    int subVarRow;
    int rowCount = rowCnt;
    int sVarRwCnt;


    PidcVariantBO varHandler =
        new PidcVariantBO(this.pidcVersHndlr.getPidcVersion(), pidcVariant, this.pidcVersHndlr.getPidcDataHandler());
    final SortedSet<PidcSubVariant> subVariantsSet = varHandler.getSubVariantsSet(false);
    for (PidcSubVariant projSubVar : subVariantsSet) {

      rowCount++;

      final Row subVarHeaderRow = ExcelCommon.getInstance().createExcelRow(pidcAttrSheet, rowCount);
      subVarRow = 1;

      ExcelCommon.getInstance().subVarHeaderRow(workbook, varNSVarAtrShtHdr, pidcAttrSheet, subVarRow, projSubVar,
          subVarHeaderRow);

      sVarRwCnt = rowCount + 1;

      // ICDM-163
      // Sorting subvariant attributes
      PidcSubVariantBO subVarHandler = new PidcSubVariantBO(this.pidcVersHndlr.getPidcVersion(), projSubVar,
          this.pidcVersHndlr.getPidcDataHandler());
      // TODO does this provide only valid attributes?
      SortedSet<PidcSubVariantAttribute> subVarAttrSetWithInvisibleAttr =
          subVarHandler.getAttributes(ApicConstants.SORT_SUPERGROUP);

      SortedSet<PidcSubVariantAttribute> subVarAttrSet = new TreeSet<>();
      // Remove invisible attributes
      subVarAttrSetWithInvisibleAttr.forEach(subvarAttr -> {
        if (!this.pidcVersHndlr.getPidcDataHandler().getSubVariantInvisbleAttributeMap().get(projSubVar.getId())
            .contains(subvarAttr.getAttrId())) {
          subVarAttrSet.add(subvarAttr);
        }
      });
      if (this.isReportExtrnl || ((null != filteredAttrIdSet) && !filteredAttrIdSet.isEmpty())) {
        removeInvisibleAttributes(filteredAttrIdSet, subVarAttrSet);
      }

      for (IProjectAttribute subVarAttr : subVarAttrSet) {

        rowCount++;

        final Row subVarAttrRow = ExcelCommon.getInstance().createExcelRow(pidcAttrSheet, rowCount);

        for (int col = 0; col <= (varNSVarAtrShtHdr.length + 1); col++) {

          createSvarCol(workbook, varNSVarAtrShtHdr, pidcAttrSheet, tempAttrSheet, projSubVar, subVarAttr,
              subVarAttrRow, col);
        }
      }
      pidcAttrSheet.groupRow(sVarRwCnt, rowCount);
    }
    return rowCount;
  }

  /**
   * Create sub variant cell
   *
   * @param workbook
   * @param varNSVarAtrShtHdr
   * @param pidcAttrSheet
   * @param tempAttrSheet
   * @param attrValSet
   * @param projSubVar
   * @param subVarAttr
   * @param subVarAttrRow
   * @param col
   */
  private void createSvarCol(final Workbook workbook, final String[] varNSVarAtrShtHdr, final Sheet pidcAttrSheet,
      final Sheet tempAttrSheet, final PidcSubVariant projSubVar, final IProjectAttribute subVarAttr,
      final Row subVarAttrRow, final int col) {
    CellStyle styleToUse = this.cellStyle;
    StringBuilder isAttrValDeleted = new StringBuilder();
    StringBuilder isAttrValCleared = new StringBuilder();

    if (Boolean.parseBoolean(isAttrValDeleted.toString())) {
      styleToUse = this.redCellStyle;
    }
    // Icdm-833 orange Style
    else if ((isAttrValCleared.length() > 0) && !Boolean.parseBoolean(isAttrValCleared.toString())) {
      styleToUse = this.orangeCellStyle;
    }

    if (col == 0) {
      ExcelCommon.getInstance().createHeaderCell(subVarAttrRow, projSubVar.getName(), col, this.headerCellStyle,
          this.font);
    }
    else if (col == (varNSVarAtrShtHdr.length + 1)) {
      // Hidden column Attribute ID
      ExcelCommon.getInstance().createCell(subVarAttrRow, String.valueOf(subVarAttr.getAttrId()), col, this.cellStyle);
    }
    if (col >= varNSVarAtrShtHdr.length) {
      return;
    }
    final String result = PIDCExcelColumn.getInstance().getColTxtFromSubVarAttr(this.pidcVersHndlr, col, subVarAttr,
        isAttrValDeleted, isAttrValCleared);
    if (varNSVarAtrShtHdr[col].equals(ExcelClientConstants.RH_VALUE)) {
      createSvarValueCol(workbook, pidcAttrSheet, tempAttrSheet, subVarAttr, subVarAttrRow, col, styleToUse, result);
    }
    else if (varNSVarAtrShtHdr[col].equals(ExcelClientConstants.RH_USED)) {
      ExcelCommon.getInstance().createCell(subVarAttrRow,
          ApicConstants.PROJ_ATTR_USED_FLAG.getType(subVarAttr.getUsedFlag()).getUiType(), col, this.cellStyle);
    }
    else if ((col < varNSVarAtrShtHdr.length) && varNSVarAtrShtHdr[col].equals(ExcelClientConstants.RH_SPEC_LINK)) {
      ExcelCommon.getInstance().createHyperLinkCell(workbook, subVarAttrRow, result, col, this.cellStyle, false);
    }
    else {
      ExcelCommon.getInstance().createCell(subVarAttrRow, result, col, this.cellStyle);
    }
  }

  /**
   * Create sub variant value cell
   *
   * @param workbook
   * @param pidcAttrSheet
   * @param tempAttrSheet
   * @param subVarAttr
   * @param subVarAttrRow
   * @param col
   * @param styleToUse
   * @param result
   */
  private void createSvarValueCol(final Workbook workbook, final Sheet pidcAttrSheet, final Sheet tempAttrSheet,
      final IProjectAttribute subVarAttr, final Row subVarAttrRow, final int col, final CellStyle styleToUse,
      final String result) {

    Attribute attr = this.pidcVersHndlr.getPidcDataHandler().getAttribute(subVarAttr.getAttrId());
    String[] valList = getValuesAsStrArr(attr);

    final DataValidationHelper dvHelper = pidcAttrSheet.getDataValidationHelper();
    // ICDM-2285
    if (!this.isReportExtrnl && (valList.length > 0)) {
      if (attr.getLevel() > 0L) {
        if ((attr.getLevel().intValue() != ApicConstants.SUB_VARIANT_CODE_ATTR) && !isHyperLinkAttr(attr)) {
          final DataValidation dataValidation =
              createDropdownForVal(subVarAttrRow, col, tempAttrSheet, valList, dvHelper, attr);
          ExcelCommon.getInstance().setsuppressDropDown(dataValidation);
          pidcAttrSheet.addValidationData(dataValidation);
        }
      }
      else {
        if (!isHyperLinkAttr(attr)) {
          final DataValidation dataValidation =
              createDropdownForVal(subVarAttrRow, col, tempAttrSheet, valList, dvHelper, attr);
          ExcelCommon.getInstance().setsuppressDropDown(dataValidation);
          pidcAttrSheet.addValidationData(dataValidation);
        }
      }
    }

    Cell cell = null;
    if (result == null) {
      cell = ExcelCommon.getInstance().createCell(subVarAttrRow, ExcelCommonConstants.EMPTY_STRING, col, styleToUse);
    }
    else {
      // ICDM-323
      AttributeValueType valueType = AttributeValueType.getType(attr.getValueType());
      if (valueType == AttributeValueType.HYPERLINK) {
        if (result.isEmpty()) {
          cell = subVarAttrRow.createCell(col);
          cell.setCellStyle(ExcelCommon.getInstance().hyperLinkStyle(workbook, false));
        }
        else {
          ExcelCommon.getInstance().createHyperLinkCell(workbook, subVarAttrRow, result, col, styleToUse, false);
        }
      }
      else {
        cell = ExcelCommon.getInstance().createCell(subVarAttrRow, result, col, styleToUse);
      }
    }

    final String valFormat = attr.getFormat();
    final DataFormat format = workbook.createDataFormat();
    if ((valFormat != null) && (cell != null)) {
      cell.getCellStyle().setDataFormat(format.getFormat(valFormat));
    }
  }

  /**
   * Create attribute value work sheet
   *
   * @param workbook
   * @param sheetName
   * @param filteredAttrMap
   * @throws IOException
   */
  private void createAttrWorkSheet(final Workbook workbook, final String sheetName, final Set<Long> filteredAttrIdSet) {

    PIDCExcelColumn.getInstance();
    final String[] attrSheetHeader = PIDCExcelColumn.SHT_HDR_ATTR;
    final Sheet attributesSheet = workbook.getSheet(sheetName);
    final Row headerRow = ExcelCommon.getInstance().createExcelRow(attributesSheet, 0);
    for (int i = 0; i < attrSheetHeader.length; i++) {

      ExcelCommon.getInstance().createHeaderCell(headerRow, attrSheetHeader[i], i, this.headerCellStyle, this.font);
    }
    int rowCount = 0;


    final Map<Long, Attribute> allAttr = this.pidcVersHndlr.getPidcDataHandler().getAttributeMap();
    // ICDM-163
    SortedSet<Attribute> allAttributes = new TreeSet<>(allAttr.values());
    // ICDM-743
    removeInvisibleAttributes(filteredAttrIdSet, allAttr, allAttributes);


    for (Attribute attr : allAttributes) {
      // ICDM-485
      if ((attr.isExternal() || !this.isReportExtrnl) && (attr.isExternalValue() || !this.isReportExtrnl)) {
        // ICDM-163
        // sorting values
        final SortedSet<AttributeValue> attrValues = getAttrValues(attr.getId());
        for (AttributeValue attrValue : attrValues) {
          rowCount = createAttrRow(workbook, attrSheetHeader, attributesSheet, rowCount, attr, attrValue);
        }
      }
    }
    attributesSheet.createFreezePane(0, 1, 0, 1);
    attributesSheet.setColumnHidden(attrSheetHeader.length - 1, true);
    ExcelCommon.getInstance().setColSize(attrSheetHeader, attributesSheet);
  }

  /**
   * @param workbook
   * @param attrSheetHeader
   * @param attributesSheet
   * @param rowCount
   * @param attr
   * @param attrValue
   * @return int
   */
  private int createAttrRow(final Workbook workbook, final String[] attrSheetHeader, final Sheet attributesSheet,
      final int rowCount, final Attribute attr, final AttributeValue attrValue) {
    int rowCnt = rowCount;
    rowCnt++;
    Row row = ExcelCommon.getInstance().createExcelRow(attributesSheet, rowCnt);

    for (int i = 0; i < attrSheetHeader.length; i++) {
      createAttrHdrCell(workbook, attrSheetHeader, attr, attrValue, row, i);
    }
    return rowCnt;
  }

  /**
   * @param workbook
   * @param attrSheetHeader
   * @param attr
   * @param attrValue
   * @param row
   * @param colIndx
   */
  private void createAttrHdrCell(final Workbook workbook, final String[] attrSheetHeader, final Attribute attr,
      final AttributeValue attrValue, final Row row, final int colIndx) {
    if (colIndx == (attrSheetHeader.length - 1)) {
      ExcelCommon.getInstance().createCell(row, String.valueOf(attrValue.getId()), colIndx, this.cellStyle);
    }

    else if (attrSheetHeader[colIndx].equals(ExcelClientConstants.ATTRIBUTE_RH_ATTR_NAME)) {
      ExcelCommon.getInstance().createCell(row, attr.getName(), colIndx, this.cellStyle);

    }
    else if (attrSheetHeader[colIndx].equals(ExcelClientConstants.ATTRIBUTE_RH_ATTR_DESCRIPTION)) {
      ExcelCommon.getInstance().createCell(row, attr.getDescription(), colIndx, this.cellStyle);

    }
    else if (attrSheetHeader[colIndx].equals(ExcelClientConstants.ATTRIBUTE_RH_VALUE_DESCRIPTION)) {
      ExcelCommon.getInstance().createCell(row, attrValue.getDescription(), colIndx, this.cellStyle);

    }
    else if (attrSheetHeader[colIndx].equals(ExcelClientConstants.ATTRIBUTE_RH_VALUE_TYPE)) {
      ExcelCommon.getInstance().createCell(row, attr.getValueType(), colIndx, this.cellStyle);
    }

    else if (attrSheetHeader[colIndx].equals(ExcelClientConstants.RH_VALUE)) {
      // ICDM-323
      AttributeValueType valueType = AttributeValueType.getType(attr.getValueType());
      if (valueType == AttributeValueType.HYPERLINK) {
        ExcelCommon.getInstance().createHyperLinkCell(workbook, row, attrValue.getName(), colIndx, this.cellStyle,
            false);
      }
      else {
        // Icdm-833 orange Style
        CLEARING_STATUS clStatus = CLEARING_STATUS.getClearingStatus(attrValue.getClearingStatus());
        if (clStatus == CLEARING_STATUS.CLEARED) {
          ExcelCommon.getInstance().createCell(row, attrValue.getNameRaw(), colIndx, this.cellStyle);
        }
        else {
          ExcelCommon.getInstance().createCell(row, attrValue.getNameRaw(), colIndx, this.orangeCellStyle);
        }

      }
    }
    else if (attrSheetHeader[colIndx].equals(ExcelClientConstants.ATTRIBUTE_NORMALISED)) {

      String isNormalisedStr = attr.isNormalized() ? "YES" : "NO";
      ExcelCommon.getInstance().createCell(row, isNormalisedStr, colIndx, this.cellStyle);
    }
  }

  /**
   * Create drop down for value column
   *
   * @param workbook
   * @param row
   * @param col
   * @param tempAttrSheet
   * @param valList
   * @param dvHelper
   * @return
   */
  private DataValidation createDropdownForVal(final Row row, final int col, final Sheet tempAttrSheet,
      final String[] valList, final DataValidationHelper dvHelper, final Attribute attr) {
    final CellRangeAddressList addressList = new CellRangeAddressList(row.getRowNum(), row.getRowNum(), col, col);
    // Create Value List constraint using createExplicitListConstraint rather than using formula which points to values
    // in temporary hidden sheet

    final DataValidationConstraint dvConstraint = createFormulaListConstraint(tempAttrSheet, valList, dvHelper, attr);

    return dvHelper.createValidation(dvConstraint, addressList);
  }


  /**
   * Create formula list constraint for value column
   *
   * @param tempAttrSheet
   * @param valList
   * @param dvHelper
   * @param attr
   * @return
   */
  private DataValidationConstraint createFormulaListConstraint(final Sheet tempAttrSheet, final String[] valList,
      final DataValidationHelper dvHelper, final Attribute attr) {
    final int temprowStartCnt = this.tempRowCount;
    int cellCol = 0;
    if (!this.valueRefMap.containsKey(attr.getId())) {
      for (String element : valList) {
        final Row tempAttrRow = ExcelCommon.getInstance().createExcelRow(tempAttrSheet, this.tempRowCount);
        ExcelCommon.getInstance().createCell(tempAttrRow, element, cellCol, this.cellStyle);
        this.tempRowCount++;
      }
      String tempSheetName = tempAttrSheet.getSheetName();
      StringBuilder cellRefBuilder = new StringBuilder();
      int startRow = temprowStartCnt + 1;
      cellRefBuilder.append("'").append(tempSheetName).append("'!").append(RANGE_APPENDER).append(startRow).append(" :")
          .append(RANGE_APPENDER).append(this.tempRowCount);
      this.valueRefMap.put(attr.getId(), cellRefBuilder.toString());
    }
    return dvHelper.createFormulaListConstraint(this.valueRefMap.get(attr.getId()));
  }

  /**
   * Remove the attributes which are filtered out ICDM-743
   *
   * @param filteredAttrsMap
   * @param pidcAttrSet
   */
  private <P extends IProjectAttribute> void removeInvisibleAttributes(final Set<Long> filteredAttrIdSet,
      final SortedSet<P> pidcAttrSet) {
    List<P> invsbleAttrs = new ArrayList<>();
    for (P pidcAttr : pidcAttrSet) {
      Attribute attr = this.pidcVersHndlr.getPidcDataHandler().getAttribute(pidcAttr.getAttrId());
      if (((null != filteredAttrIdSet) && !filteredAttrIdSet.isEmpty() &&
          (!filteredAttrIdSet.contains(pidcAttr.getAttrId()))) || (!attr.isExternal() && this.isReportExtrnl)/*
                                                                                                              * ICDM-485
                                                                                                              */) {
        invsbleAttrs.add(pidcAttr);
      }
    }
    // remove all filtered out attributes
    for (P attribute : invsbleAttrs) {
      pidcAttrSet.remove(attribute);
    }
  }

  /**
   * ICDM-743 Remove the attributes which are filtered out
   *
   * @param filteredAttrMap
   * @param allAttr
   * @param attributes
   */
  private void removeInvisibleAttributes(final Set<Long> filteredAttrSet, final Map<Long, Attribute> allAttr,
      final SortedSet<Attribute> attributes) {
    if ((null == filteredAttrSet) || filteredAttrSet.isEmpty()) {
      return;
    }
    List<Attribute> invAttrs = new ArrayList<>();
    for (Attribute attribute : allAttr.values()) {
      if ((!filteredAttrSet.contains(attribute.getId())) || (attribute.isExternal() && this.isReportExtrnl)/*
                                                                                                            * ICDM-485
                                                                                                            */) {
        invAttrs.add(attribute);
      }
    }
    // remove all filtered out attributes
    for (Attribute attribute : invAttrs) {
      attributes.remove(attribute);
    }
  }

  private SortedSet<AttributeValue> getAttrValues(final Long attrId) {
    return this.attrExportModel.getAllAttrValuesMap().get(attrId).values().stream().filter(a -> !a.isDeleted())
        .collect(Collectors.toCollection(TreeSet::new));
  }

  private static CDMLogger getCdmLogger() {
    return CDMLogger.getInstance();
  }
}
