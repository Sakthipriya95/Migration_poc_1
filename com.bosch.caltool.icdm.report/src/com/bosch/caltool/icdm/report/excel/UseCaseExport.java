package com.bosch.caltool.icdm.report.excel;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

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
import com.bosch.caltool.icdm.client.bo.general.CommonDataBO;
import com.bosch.caltool.icdm.client.bo.uc.IUseCaseItemClientBO;
import com.bosch.caltool.icdm.client.bo.uc.UseCaseGroupClientBO;
import com.bosch.caltool.icdm.client.bo.uc.UsecaseClientBO;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.common.util.DateFormat;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.attr.Attribute;
import com.bosch.caltool.icdm.model.general.CommonParamKey;
import com.bosch.caltool.icdm.model.uc.UsecaseEditorModel;
import com.bosch.caltool.icdm.report.Activator;
import com.bosch.caltool.icdm.report.common.ExcelCommon;
import com.bosch.caltool.icdm.ws.rest.client.apic.AttributeServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.caltool.icdm.ws.rest.client.uc.UseCaseServiceClient;


/**
 * @author dmo5cob
 */
public class UseCaseExport {

  /**
   *
   */
  private static final String UC_NEVER_UPDATED = "Expired (UC has never been marked as up to date)";
  /**
   *
   */
  private static final int EXPIRY_ROWNUM = 2;
  /**
   *
   */
  private static final int OWNER_ROWNUM = 1;
  /**
   *
   */
  private static final int ATTR_ROWNUM = 3;
  /**
   * The progress monitor instance to which progess information is to be set.
   */
  private final IProgressMonitor monitor;

  /**
   * Constructor
   *
   * @param monitor IProgressMonitor
   */
  public UseCaseExport(final IProgressMonitor monitor) {

    this.monitor = monitor;
  }

  /**
   * Cellstyle for header row
   */
  private static CellStyle headerCellStyle;
  /**
   * Normal cell style
   */
  private static CellStyle cellStyle;
  /**
   * Red cell style
   */
  private static CellStyle redCellStyle;

  /**
   * usecase header cell style
   */
  private static CellStyle useCaseHeaderCellStyle;

  /**
   * usecase header cell style
   */
  private static CellStyle useCaseSectionHeaderCellStyle;
  /**
   * Usecase owners cell style
   */
  private static CellStyle ownerCellStyle;
  /**
   * Usecase owners cell style
   */
  private static CellStyle expiryCellStyle;
  /**
   * Font instance
   */
  private Font font;
  /**
   * Total number of columns
   */
  private int totalCols = 3;
  /**
   * Map which holds the use case items
   */
  Map<Integer, IUseCaseItemClientBO> mapOfUseCaseItems = new HashMap<>();

  private Row attrRow;

  /**
   * Header columns
   */
  // ICDM-2230
  private static final String[] useCaseSheetHeader = new String[] {
      ExcelClientConstants.RH_ATTRIBUTES,
      ExcelClientConstants.RH_ATTRIBUTES_ID,
      ExcelClientConstants.RH_ATTRIBUTES_CREATION_DATE,
      ExcelClientConstants.RH_USAGE };

  private static final int USE_CASE_EXPORT_BEGIN_TIME = 100;

  private static final int USE_CASE_EXPORT_WORK_TIME_30 = 30;

  private static final int USECASE_SHEET_LAST_ROW_NO = 500;

  private static final int USE_CASE_ROW_HEIGHT = 8;

  private static final int USE_CASE_OWNER_ROW_HEIGHT = 15;

  // ICDM-2230
  private static final int ATTR_USAGE_STARTING_COL_NO = 4;

  /**
   * the use case starting column number
   */
  // ICDM-2230
  private static final int USE_CASE_STARTING_COL_NO = 3;


  private static final String SHEET_NAME = "Sheet1";

  /**
   * Export PIDC
   *
   * @param useCaseItems UsecaseGroup/Usecase
   * @param filePath Path of file
   * @param fileExtn Extn of file
   */
  public void exportUseCase(final SortedSet<IUseCaseItemClientBO> useCaseItems, final String filePath,
      final String fileExtn) {
    this.monitor.beginTask("", USE_CASE_EXPORT_BEGIN_TIME);

    getUseCaseModelMap(useCaseItems);

    final String fileType = fileExtn;
    final ExcelFactory exFactory = ExcelFactory.getFactory(fileType);
    final ExcelFile xlFile = exFactory.getExcelFile();
    final Workbook workbook = xlFile.createWorkbook();

    createSheet(workbook);

    headerCellStyle = ExcelCommon.getInstance().createHeaderCellStyleWithoutBorder(workbook);
    cellStyle = ExcelCommon.getInstance().createCellStyleWithoutBorder(workbook);
    redCellStyle = ExcelCommon.getInstance().createRedCellStyleWithoutBorder(workbook);
    useCaseHeaderCellStyle = ExcelCommon.getInstance().createVerticalCellStyle(workbook);
    useCaseSectionHeaderCellStyle = ExcelCommon.getInstance().createVerticalCellStyle(workbook);
    UseCaseExport.ownerCellStyle = ExcelCommon.getInstance().createOwnerCellStyle(workbook);
    expiryCellStyle = ExcelCommon.getInstance().createExpiryCellStyle(workbook);

    this.font = ExcelCommon.getInstance().createFont(workbook);

    try {

      final String fileFullPath;

      if (filePath.contains(".xlsx") || filePath.contains(".xls")) {
        fileFullPath = filePath;
      }
      else {
        fileFullPath = filePath + "." + fileExtn;
      }

      final FileOutputStream fileOut = new FileOutputStream(fileFullPath);

      this.monitor.worked(USE_CASE_EXPORT_WORK_TIME_30);
      this.monitor.subTask("Exporting Use Case attributes . . .");
      createUseCaseWorkSheet(workbook, useCaseItems);

      workbook.setSelectedTab(0);

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
   * @param useCaseItems
   */
  private void getUseCaseModelMap(final SortedSet<IUseCaseItemClientBO> useCaseItems) {
    Map<Long, UsecaseClientBO> ucBoMap = new HashMap<>();
    Set<Long> usecaseIds = new HashSet<>();
    for (IUseCaseItemClientBO ucItem : useCaseItems) {
      if (ucItem instanceof UseCaseGroupClientBO) {
        addUseCaseIdsToSet(ucItem, usecaseIds, ucBoMap);
      }
      if (ucItem instanceof UsecaseClientBO) {
        usecaseIds.add(ucItem.getID());
        ucBoMap.put(ucItem.getID(), (UsecaseClientBO) ucItem);
      }
    }
    UseCaseServiceClient ucServiceClient = new UseCaseServiceClient();
    try {
      Map<Long, UsecaseEditorModel> ucModelsMap = ucServiceClient.getUsecaseEditorModels(usecaseIds);
      for (Entry<Long, UsecaseEditorModel> ucModelEntry : ucModelsMap.entrySet()) {
        UsecaseClientBO usecaseClientBO = ucBoMap.get(ucModelEntry.getKey());
        usecaseClientBO.setUsecaseEditorModel(ucModelEntry.getValue());
      }
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
    }

  }

  /**
   * @param ucItem
   * @param usecaseIds
   * @param ucBoMap
   */
  private void addUseCaseIdsToSet(final IUseCaseItemClientBO ucItem, final Set<Long> usecaseIds,
      final Map<Long, UsecaseClientBO> ucBoMap) {

    UseCaseGroupClientBO useCaseGroupClientBO = (UseCaseGroupClientBO) ucItem;
    final SortedSet<UseCaseGroupClientBO> subGroupSet = useCaseGroupClientBO.getChildGroupSet(true);
    if (!subGroupSet.isEmpty()) {
      // children are groups
      for (UseCaseGroupClientBO useCaseGroupClient : subGroupSet) {
        addUseCaseIdsToSet(useCaseGroupClient, usecaseIds, ucBoMap);
      }
    }
    else {
      // children are usecases
      for (UsecaseClientBO usecaseBo : useCaseGroupClientBO.getUseCaseSet(true)) {
        usecaseIds.add(usecaseBo.getId());
        ucBoMap.put(usecaseBo.getId(), usecaseBo);
      }
    }

  }

  /**
   *
   */
  private void createSheet(final Workbook workbook) {
    // create first sheet
    workbook.createSheet(SHEET_NAME);
  }

  /**
   * Create UseCaseWorkSheet (First sheet)
   *
   * @param workbook
   * @param useCaseItems
   * @throws IOException
   */
  private void createUseCaseWorkSheet(final Workbook workbook, final SortedSet<IUseCaseItemClientBO> useCaseItems)
      throws IOException {

    final String[] sheetHeader = UseCaseExport.useCaseSheetHeader;
    final Sheet ucSheet = workbook.getSheet(SHEET_NAME);
    ucSheet.setRowSumsRight(false);
    writeUseCaseItemsAsHeader(workbook, useCaseItems, ucSheet);

    // Create header for attributes and usage columns
    writeHeader(sheetHeader, ucSheet);

    // Create cells for attributes and usage columns
    Map<Integer, Integer> colAttrcountMap = writeAttributesAndUsages(workbook, ucSheet);

    for (Entry<Integer, Integer> colAttr : colAttrcountMap.entrySet()) {
      ExcelCommon.getInstance().createCell(this.attrRow, String.valueOf(colAttr.getValue()), colAttr.getKey(),
          UseCaseExport.cellStyle);
    }

    // Group the columns
    groupColumns(ucSheet);

    // Disable autoFilter when there are no rows
    if (this.totalCols > 0) {
      ucSheet.setAutoFilter(new CellRangeAddress(4, USECASE_SHEET_LAST_ROW_NO, 0, this.totalCols));
    }
    ExcelCommon.getInstance().setColSize(sheetHeader, ucSheet);
  }

  /**
   * @param sheetHeader
   * @param ucSheet
   */
  private void writeHeader(final String[] sheetHeader, final Sheet ucSheet) {
    Row headerRow = ExcelCommon.getInstance().createExcelRow(ucSheet, 4);
    for (int headerCol = 0; headerCol < (sheetHeader.length); headerCol++) {
      String cellValue = sheetHeader[headerCol];
      ExcelCommon.getInstance().createHeaderCell(headerRow, cellValue, headerCol, UseCaseExport.headerCellStyle,
          this.font);
    }
  }

  /**
   * @param workbook
   * @param useCaseItems
   * @param ucSheet
   */
  private void writeUseCaseItemsAsHeader(final Workbook workbook, final SortedSet<IUseCaseItemClientBO> useCaseItems,
      final Sheet ucSheet) {
    UseCaseGroupClientBO usecaseGrp;
    UsecaseClientBO useCase = null;
    Row ownersRow = ExcelCommon.getInstance().createExcelRow(ucSheet, OWNER_ROWNUM);
    ownersRow.setHeightInPoints(USE_CASE_OWNER_ROW_HEIGHT * ucSheet.getDefaultRowHeightInPoints());
    ExcelCommon.getInstance().createHeaderCell(ownersRow, "Use case Owner", 0, UseCaseExport.headerCellStyle,
        this.font);
    Row expiryRow = ExcelCommon.getInstance().createExcelRow(ucSheet, EXPIRY_ROWNUM);
    ExcelCommon.getInstance().createHeaderCell(expiryRow, "Use case Validity", 0, UseCaseExport.headerCellStyle,
        this.font);
    this.attrRow = ExcelCommon.getInstance().createExcelRow(ucSheet, ATTR_ROWNUM);
    ExcelCommon.getInstance().createHeaderCell(this.attrRow, "Number of attributes", 0, UseCaseExport.headerCellStyle,
        this.font);

    if (useCaseItems.size() == 1) {
      if (useCaseItems.first() instanceof UseCaseGroupClientBO) {
        usecaseGrp = (UseCaseGroupClientBO) useCaseItems.first();
        int colCount = 1;
        Row row = ExcelCommon.getInstance().createExcelRow(ucSheet, 0);
        writeUseCaseGroups(workbook, ucSheet, usecaseGrp, colCount, row, ownersRow, expiryRow);
      }
      else if (useCaseItems.first() instanceof UsecaseClientBO) {
        useCase = (UsecaseClientBO) useCaseItems.first();
      }
    }
    else {// UseCaseRoot Node
      Row row = ExcelCommon.getInstance().createExcelRow(ucSheet, 0);
      for (IUseCaseItemClientBO item : useCaseItems) {
        usecaseGrp = (UseCaseGroupClientBO) item;
        writeUseCaseGroups(workbook, ucSheet, usecaseGrp, this.totalCols, row, ownersRow, expiryRow);
      }
    }
    // Usecases/Usecase sections
    if (null != useCase) {
      Row row = ExcelCommon.getInstance().createExcelRow(ucSheet, 0);
      row.setHeightInPoints(USE_CASE_ROW_HEIGHT * ucSheet.getDefaultRowHeightInPoints());
      this.totalCols++;
      useCaseHeaderCellStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.index);

      ownersRow.setHeightInPoints(USE_CASE_ROW_HEIGHT * ucSheet.getDefaultRowHeightInPoints());
      ExcelCommon.getInstance().createCell(ownersRow, useCase.getListofOwners().replaceAll("\n\t\t", " ; "),
          this.totalCols, UseCaseExport.ownerCellStyle);

      if (null != useCase.getUseCase().getLastConfirmationDate()) {

        if (useCase.isUpToDate()) {
          ExcelCommon.getInstance().createCell(expiryRow,
              "Valid until\n" +
                  DateFormat.formatDateToString(validUntilDate(useCase).getTime(), DateFormat.DATE_FORMAT_15),
              this.totalCols, UseCaseExport.expiryCellStyle);
        }
        else {
          ExcelCommon.getInstance().createCell(expiryRow,
              "Expired since\n" + useCase.getUseCase().getLastConfirmationDate(), this.totalCols,
              UseCaseExport.expiryCellStyle);
        }

      }
      else {
        ExcelCommon.getInstance().createCell(expiryRow, UC_NEVER_UPDATED, this.totalCols,
            UseCaseExport.expiryCellStyle);
      }

      ExcelCommon.getInstance().createCell(row, "Use Case \n" + useCase.getName(), this.totalCols,
          useCaseHeaderCellStyle);
      this.mapOfUseCaseItems.put(this.totalCols, useCase);
      createSections(useCase, row);

    }


  }

  /**
   * @param usecase
   * @return
   */
  private Calendar validUntilDate(final UsecaseClientBO usecase) {
    Calendar validUntil = Calendar.getInstance();
    try {
      validUntil =
          DateFormat.convertStringToCalendar(DateFormat.DATE_FORMAT_15, usecase.getUseCase().getLastConfirmationDate());
      CommonDataBO dataBO = new CommonDataBO();
      String upToDateInterval = dataBO.getParameterValue(CommonParamKey.USECASE_UP_TO_DATE_INTERVAL);
      Long intervalDays = Long.valueOf(upToDateInterval);
      validUntil.add(Calendar.DATE, intervalDays.intValue());
    }
    catch (IcdmException | ApicWebServiceException exp) {
      CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
    }
    return validUntil;
  }

  /**
   * @param workbook
   * @param ucSheet
   * @param usecaseGrp
   * @param colCount
   * @param row
   * @param expiryRow
   * @param ownersRow
   */
  private void writeUseCaseGroups(final Workbook workbook, final Sheet ucSheet, final UseCaseGroupClientBO usecaseGrp,
      final int colCount, final Row row, final Row ownersRow, final Row expiryRow) {
    if (null != usecaseGrp) {
      row.setHeightInPoints(USE_CASE_ROW_HEIGHT * ucSheet.getDefaultRowHeightInPoints());
      SortedSet<UseCaseGroupClientBO> useCaseChileGrps = usecaseGrp.getChildGroupSet(false);
      createColsForChildGrps(workbook, ucSheet, usecaseGrp, colCount, row, useCaseChileGrps, ownersRow, expiryRow);
    }
  }

  /**
   * @param ucSheet
   */
  private void groupColumns(final Sheet ucSheet) {
    Row firstRow = ucSheet.getRow(0);
    Map<Integer, Integer> myMap = new HashMap<>();
    for (int i = 0; i < this.totalCols; i++) {
      if ((null != firstRow.getCell(i)) && (null != firstRow.getCell(i + 1))) {
        if (firstRow.getCell(i).getStringCellValue().startsWith("Use Case") &&
            firstRow.getCell(i + 1).getStringCellValue().startsWith("Section")) {
          inner: for (int j = i + 1; j <= this.totalCols; j++) {
            if (null != firstRow.getCell(j)) {
              if (firstRow.getCell(j).getStringCellValue().startsWith("Use Case")) {
                myMap.put(i, j - 1);
                break inner;
              }
              else if ((j == this.totalCols) && (firstRow.getCell(j).getStringCellValue().startsWith("Section"))) {
                myMap.put(i, j);
                break inner;
              }
            }
          }
        }
      }
    }

    for (Entry<Integer, Integer> value : myMap.entrySet()) {
      ucSheet.groupColumn(value.getKey() + 1, value.getValue());
    }
  }

  /**
   * @param workbook
   * @param ucSheet
   * @param usecaseGrp
   * @param colCount
   * @param row
   * @param useCaseChileGrps
   * @param expiryRow
   * @param ownersRow
   */
  private void createColsForChildGrps(final Workbook workbook, final Sheet ucSheet,
      final UseCaseGroupClientBO usecaseGrp, final int colCount, final Row row,
      final SortedSet<UseCaseGroupClientBO> useCaseChileGrps, final Row ownersRow, final Row expiryRow) {

    if (useCaseChileGrps.isEmpty()) {
      createUseCase(row, usecaseGrp, ownersRow, expiryRow);
    }
    for (UseCaseGroupClientBO useCaseGroup : useCaseChileGrps) {
      if (!useCaseGroup.getChildGroupSet(false).isEmpty()) {
        createColsForChildGrps(workbook, ucSheet, useCaseGroup, colCount, row, useCaseGroup.getChildGroupSet(false),
            ownersRow, expiryRow);
      }
      createUseCase(row, useCaseGroup, ownersRow, expiryRow);
    }
  }

  /**
   * @param workbook
   * @param ucSheet
   * @return
   */
  private Map<Integer, Integer> writeAttributesAndUsages(final Workbook workbook, final Sheet ucSheet) {
    int rowCount = 4;
    CellStyle cellsStyle;
    CellStyle cellDateStyle;
    final SortedSet<com.bosch.caltool.icdm.model.apic.attr.Attribute> attributes = getSortedAttributes();
    Map<Integer, Integer> colattrCountMap = new HashMap<>();// key - col number, value - attributes count
    for (Attribute attribute : attributes) {
      int ucItemUsageCounter = 0;
      rowCount++;
      cellsStyle = attribute.isDeleted() ? redCellStyle : cellStyle;
      cellDateStyle = attribute.isDeleted() ? ExcelCommon.getInstance().createRedDateCellStyleWithoutBorder(workbook)
          : ExcelCommon.getInstance().createDateCellStyle(workbook);
      final Row row = ExcelCommon.getInstance().createExcelRow(ucSheet, rowCount);

      ExcelCommon.getInstance().createCell(row, attribute.getName(), 0, cellsStyle);
      //for Attribute ID 
      ExcelCommon.getInstance().createCell(row, attribute.getId().toString(), 1, cellsStyle);

      // ICDM-2230
      Cell cell = row.createCell(2);
      cell.setCellValue(attribute.getCreatedDate());
      cell.setCellStyle(cellDateStyle);

      // if mapOfUseCaseItems contains 1 element(i.e. UseCase), then mark 'X' under Use Case column
      int colToMark = this.mapOfUseCaseItems.size() == 1 ? ATTR_USAGE_STARTING_COL_NO
          : ATTR_USAGE_STARTING_COL_NO + (this.mapOfUseCaseItems.size() - 1);
      // Mark the mapped attrs
      for (int colsIterator = ATTR_USAGE_STARTING_COL_NO; colsIterator <= colToMark; colsIterator++) {
        IUseCaseItemClientBO obj = this.mapOfUseCaseItems.get(colsIterator);

        // mark 'X' under Use Case only if it has no sections/sub-sections, otherwise mark under use case sections
        boolean isCaseOfMarkingX = !((obj instanceof UsecaseClientBO) && CommonUtils.isNotEmpty(obj.getChildUCItems()));

        if ((null != obj) && obj.isMapped(attribute) && isCaseOfMarkingX) {
          ExcelCommon.getInstance().createCell(row, "X", colsIterator, cellsStyle);
          ucItemUsageCounter++;

          if (null != colattrCountMap.get(colsIterator)) {
            int count = colattrCountMap.get(colsIterator);
            int attrcount = count + 1;
            colattrCountMap.put(colsIterator, attrcount);
          }
          else {
            colattrCountMap.put(colsIterator, 1);
          }

          ExcelCommon.getInstance().createCell(row, String.valueOf(ucItemUsageCounter), USE_CASE_STARTING_COL_NO,
              cellsStyle);

        }
      }

    }

    return colattrCountMap;
  }


  /**
   * @return
   */
  private SortedSet<com.bosch.caltool.icdm.model.apic.attr.Attribute> getSortedAttributes() {
    SortedSet<com.bosch.caltool.icdm.model.apic.attr.Attribute> attributeSet = new TreeSet<>();
    AttributeServiceClient attrServiceClient = new AttributeServiceClient();
    try {
      Map<Long, com.bosch.caltool.icdm.model.apic.attr.Attribute> attrMap = attrServiceClient.getAll();
      attributeSet.addAll(attrMap.values());
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
    }
    return attributeSet;
  }

  /**
   * @param row
   * @param useCaseGroup
   * @param ucSheet
   * @param expiryRow
   * @param ownersRow
   * @param expiryRow
   * @param ownersRow
   * @param colCount
   */
  private void createUseCase(final Row row, final UseCaseGroupClientBO useCaseGroup, final Row ownersRow,
      final Row expiryRow) {
    SortedSet<UsecaseClientBO> useCases = useCaseGroup.getUseCaseSet(false);


    for (UsecaseClientBO usecase : useCases) {
      this.totalCols++;
      useCaseHeaderCellStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.index);
      ExcelCommon.getInstance().createCell(row, "Use Case \n" + usecase.getName(), this.totalCols,
          useCaseHeaderCellStyle);
      this.mapOfUseCaseItems.put(this.totalCols, usecase);

      ExcelCommon.getInstance().createCell(ownersRow, usecase.getListofOwners().replaceAll("\n\t\t", " ; "),
          this.totalCols, UseCaseExport.ownerCellStyle);

      if (null != usecase.getUseCase().getLastConfirmationDate()) {
        if (usecase.isUpToDate()) {
          ExcelCommon.getInstance().createCell(expiryRow,
              "Valid until\n" +
                  DateFormat.formatDateToString(validUntilDate(usecase).getTime(), DateFormat.DATE_FORMAT_15),
              this.totalCols, UseCaseExport.expiryCellStyle);
        }
        else {
          ExcelCommon.getInstance().createCell(expiryRow,
              "Expired since\n" + usecase.getUseCase().getLastConfirmationDate(), this.totalCols,
              UseCaseExport.expiryCellStyle);
        }
      }
      else {
        ExcelCommon.getInstance().createCell(expiryRow, UC_NEVER_UPDATED, this.totalCols,
            UseCaseExport.expiryCellStyle);
      }
      createSections(usecase, row);
    }
  }


  /**
   * @param usecase
   * @param row
   */
  private void createSections(final UsecaseClientBO usecase, final Row row) {
    // get the uc item ids with parent hierarchy map
    Map<Long, String> ucItemIdsWithHierarchyMap = usecase.getUcItemIdsWithHierarchyMap();

    for (final IUseCaseItemClientBO mappableItem : usecase.getMappableItems()) {
      // get the uc item name with the parent hierarchy from ucItemIdsWithHierarchyMap
      String ucWithHierarchyName = ucItemIdsWithHierarchyMap.get(mappableItem.getUcItem().getId());
      ucWithHierarchyName = CommonUtils.checkNull(ucWithHierarchyName);
      if (!usecase.getUseCaseSectionSet(false).isEmpty()) {
        this.totalCols++;
        useCaseSectionHeaderCellStyle.setFillForegroundColor(IndexedColors.LIGHT_GREEN.index);
        ExcelCommon.getInstance().createCell(row, "Section \n" + ucWithHierarchyName, this.totalCols,
            useCaseSectionHeaderCellStyle);
        this.mapOfUseCaseItems.put(this.totalCols, mappableItem);
      }
    }
  }

}
