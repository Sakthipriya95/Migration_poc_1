/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.rest.serviceloader.report;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;

import com.bosch.calcomp.adapter.logger.ILoggerAdapter;
import com.bosch.caltool.excel.ExcelFactory;
import com.bosch.caltool.excel.ExcelFile;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.general.WsService;

/**
 * @author elm1cob
 */
public class ServiceReport {

  /**
   * Excel File Format
   */
  private static final String REPORT_FILE_FORMAT = "xlsx";

  private static final String TIME_STAMP =
      new SimpleDateFormat("yyyy-MM-dd_HHmmss").format(Calendar.getInstance().getTime());

  /**
   * Destination excel file location.
   */
  private static final String REP_FILE_PATH = System.getProperty("user.home") + File.separator + "Desktop";
  /**
   * Excel file name
   */
  private static final String REP_FILE_NAME = "iCDM_Service_Report_" + TIME_STAMP;

  // Set of unchanged web services.
  private final SortedSet<WsService> existingServiceSet;
  // Set of web services to be created.
  private final SortedSet<WsService> webServiceCreateSet;
  // Set of web services to be updated.
  private final SortedSet<WsService> webServiceUpdateSet;

  private SortedSet<WsService> completeWsDataSet;

  private final Map<String, WsService> wsServsOldDataMap;

  private final ILoggerAdapter logger;

  private Row row;

  private int rowCount = 0;

  private final Map<Integer, String> columnHeaders = new HashMap<>();

  /**
   * Constructor
   *
   * @param webServsCreate Set
   * @param webServsUpdate Set
   * @param existingServices Set
   * @param oldDataMap map of old services
   * @param logger ILoggerAdapter
   */
  public ServiceReport(final SortedSet<WsService> webServsCreate, final SortedSet<WsService> webServsUpdate,
      final SortedSet<WsService> existingServices, final Map<String, WsService> oldDataMap,
      final ILoggerAdapter logger) {

    this.webServiceCreateSet = webServsCreate;
    this.webServiceUpdateSet = webServsUpdate;
    this.existingServiceSet = existingServices;
    this.wsServsOldDataMap = oldDataMap;
    addColumnHeaders();
    this.logger = logger;
  }

  /**
   * Add Excel column names and its number
   */
  private void addColumnHeaders() {
    this.columnHeaders.put(0, "#");
    this.columnHeaders.put(1, "Module");
    this.columnHeaders.put(2, "Service URI");
    this.columnHeaders.put(3, "Method");
    this.columnHeaders.put(4, "Scope");
    this.columnHeaders.put(5, "Is Deleted?");
    this.columnHeaders.put(6, "Description");
    this.columnHeaders.put(7, "Old Description");
    this.columnHeaders.put(8, "Change");
  }

  /**
   * Create Excel work book and write the web services changes.
   *
   * @throws ServiceReportException error while creating excel
   */
  public void generate() throws ServiceReportException {
    ExcelFile xlFile = ExcelFactory.getFactory(REPORT_FILE_FORMAT).getExcelFile();

    try (Workbook workbook = xlFile.createWorkbook()) {
      Sheet sheet = workbook.createSheet("Services");
      // Create the header row.
      createHeaderRow(workbook, sheet);
      // Combined Web services in sorted order.
      this.completeWsDataSet =
          combineAndSortSets(this.webServiceCreateSet, this.webServiceUpdateSet, this.existingServiceSet);
      // Write the Data
      writeData(sheet);

      // Enable Filter and auto sizing in Excel
      enableFiltering(sheet);
      setColumnProperty(sheet, workbook);
      addFreezePane(sheet);

      writeFile(workbook, xlFile.getFileExtension());

    }
    catch (IOException e) {
      throw new ServiceReportException("Error while creating migration report. " + e.getMessage(), e);
    }
  }

  /**
   * @param sheet
   */
  private void addFreezePane(final Sheet sheet) {
    sheet.createFreezePane(0, 1, 0, 1);
  }

  private void writeFile(final Workbook workbook, final String fileExtn) throws ServiceReportException {
    // Get the current user and desktop path
    String filePathName = REP_FILE_PATH + File.separator + REP_FILE_NAME + "." + fileExtn;
    try (OutputStream fileOut = new FileOutputStream(new File(filePathName))) {
      workbook.write(fileOut);
      this.logger.info("Migration report created. Path : {}", filePathName);
    }
    catch (IOException e) {
      throw new ServiceReportException("Error while creating migration report. " + e.getMessage(), e);
    }
  }

  /**
   * Auto size excel column and word wrapping
   */
  private void setColumnProperty(final Sheet sheet, final Workbook wrkBook) {
    CellStyle colStyle = wrkBook.createCellStyle();
    colStyle.setWrapText(true);

    this.columnHeaders.keySet().forEach(colNo -> {
      sheet.setDefaultColumnStyle(colNo, colStyle);
      sheet.autoSizeColumn(colNo);
    });
  }

  /**
   * writes the changes in web services to the excel workbook
   */
  private void writeData(final Sheet spreadSheet) {
    this.completeWsDataSet.forEach(obj -> {
      // Write Unchanged Web services.
      if (this.existingServiceSet.contains(obj)) {
        populateRow(spreadSheet, obj, "No Changes", "");
      }
      // Write Newly created Web services.
      else if (this.webServiceCreateSet.contains(obj)) {
        populateRow(spreadSheet, obj, "New", "");
      }
      // Write Modified Web service changes.
      else if (this.webServiceUpdateSet.contains(obj)) {
        if (obj.isDeleted()) {
          populateRow(spreadSheet, obj, "Deleted", "");
        }
        else {
          populateRow(spreadSheet, obj, "Modified", this.wsServsOldDataMap.get(obj.getName()).getDescription());
        }
      }
    });

  }

  /**
   * Creates the header columns.
   *
   * @param excelBook
   * @param headerText String
   */
  private void createHeaderRow(final Workbook excelBook, final Sheet sheet) {

    CellStyle headerStyle = createHeaderStyle(excelBook);

    this.row = sheet.createRow(this.rowCount);
    // create the header column titles.
    this.columnHeaders.forEach((index, title) -> {
      Cell headerCell = this.row.createCell(index);
      headerCell.setCellValue(title);
      headerCell.setCellStyle(headerStyle);
    });

    incrementRowNumber();
  }

  /**
   * Creates the Header Row Style.
   */
  private CellStyle createHeaderStyle(final Workbook workbook) {
    CellStyle headerStyle = workbook.createCellStyle();
    Font font = workbook.createFont();
    font.setBold(true);
    headerStyle.setFont(font);
    return headerStyle;
  }

  /**
   * Enable Auto filter and Sorting options in the sheet.
   */
  private void enableFiltering(final Sheet sheet) {
    sheet.setAutoFilter(new CellRangeAddress(0, this.rowCount - 1, 0, this.columnHeaders.size() - 1));
  }

  /**
   * Sorting the sets
   */
  private SortedSet<WsService> combineAndSortSets(final SortedSet<WsService> createSet,
      final SortedSet<WsService> updateSet, final SortedSet<WsService> existingSet) {

    SortedSet<WsService> combinedSet = new TreeSet<>();
    combinedSet.addAll(createSet);
    combinedSet.addAll(updateSet);
    combinedSet.addAll(existingSet);

    return combinedSet;
  }

  /**
   * Fills the column values of each row.
   *
   * @param servObj WsService
   */
  private void populateRow(final Sheet sheet, final WsService servObj, final String changeText,
      final String oldDescData) {

    this.row = sheet.createRow(this.rowCount);

    this.columnHeaders.keySet().forEach(colIndex -> {
      Cell dataCell = this.row.createCell(colIndex);
      switch (colIndex) {
        case 0:
          dataCell.setCellValue(this.rowCount);
          break;
        case 1:
          dataCell.setCellValue(servObj.getModule());
          break;
        case 2:
          dataCell.setCellValue(servObj.getServUri());
          break;
        case 3:
          dataCell.setCellValue(servObj.getServMethod());
          break;
        case 4:
          dataCell.setCellValue(servObj.getServiceScope());
          break;
        case 5:
          dataCell.setCellValue(CommonUtils.getBooleanCode(servObj.isDeleted()));
          break;
        case 6:
          dataCell.setCellValue(servObj.getDescription());
          break;
        case 7:
          dataCell.setCellValue(oldDescData);
          break;
        case 8:
          dataCell.setCellValue(changeText);
          break;
        default:
          dataCell.setCellValue("");
          break;
      }

    });

    incrementRowNumber();
  }

  /**
   * Increases the row count
   */
  private void incrementRowNumber() {
    this.rowCount++;
  }

}
