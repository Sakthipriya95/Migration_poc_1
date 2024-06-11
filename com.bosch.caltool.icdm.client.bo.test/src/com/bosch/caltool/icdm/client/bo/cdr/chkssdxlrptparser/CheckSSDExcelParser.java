/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.client.bo.cdr.chkssdxlrptparser;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import com.bosch.calcomp.adapter.logger.ILoggerAdapter;
import com.bosch.caltool.icdm.client.bo.cdr.CompliResultComparisionConstants;
import com.bosch.caltool.icdm.common.exception.InvalidInputException;
import com.bosch.caltool.icdm.common.util.ZipUtils;

/**
 * @author APJ4COB
 */
public class CheckSSDExcelParser {


  private final ILoggerAdapter testerLogger;


  /**
   * @param testerLogger ILoggerAdapter
   */
  public CheckSSDExcelParser(final ILoggerAdapter testerLogger) {
    super();
    this.testerLogger = testerLogger;
  }

  /**
   * @param compliRvwInputStream Input Stream
   * @return ComplianceResultExcelData
   * @throws IOException exception while reading excel sheet
   * @throws InvalidFormatException exception while parsing excel sheet
   */
  public CheckSSDRprtXlData getParsedExcelData(final InputStream compliRvwInputStream)
      throws IOException, InvalidFormatException {

    CheckSSDRprtXlData parsedExcelData = null;
    Sheet excelSheet = getExcelSheet(compliRvwInputStream);
    int headingRowNum = getHeadingRowNum(excelSheet);

    if (headingRowNum <= 0) {
      this.testerLogger.error("Invalid excel sheet");
    }
    else {
      parsedExcelData = parseExcel(headingRowNum, excelSheet);
    }
    return parsedExcelData;
  }

  /**
   * @return InputStream
   * @throws InvalidInputException invalid ouput file path
   */
  public InputStream getCompliReviewOutputExcelStream() throws InvalidInputException {

    InputStream excelInpStream = null;
    try (InputStream compliRvwOutputStream = new FileInputStream(CompliResultComparisionConstants.OUTPUT_PATH)) {
      Map<String, InputStream> mapOfInpStream = ZipUtils.unzipIfZipped(compliRvwOutputStream, null);
      for (Entry<String, InputStream> entry : mapOfInpStream.entrySet()) {
        if (entry.getKey().startsWith(CompliResultComparisionConstants.CHK_SSD_EXL_REPORT_NAME_PREFIX) &&
            !entry.getKey().contains(CompliResultComparisionConstants.CHK_SSD_EXL_REPORT_EXT_FILE)) {
          excelInpStream = entry.getValue();
        }
      }
    }
    catch (IOException | InvalidInputException exp) {
      throw new InvalidInputException(exp.getMessage(), exp);
    }
    return excelInpStream;
  }


  /**
   * @param compHexChkSsdRprtPath String
   * @return ComplianceResultExcelData
   */
  public CheckSSDRprtXlData getCompHexExcelSheetData(final String compHexChkSsdRprtPath) {

    CheckSSDRprtXlData parsedExcelData = null;
    try (InputStream compliRvwOutputStream = new FileInputStream(compHexChkSsdRprtPath)) {
      parsedExcelData = getParsedExcelData(compliRvwOutputStream);
    }
    catch (Exception exp) {
      this.testerLogger.error("Please select a valid excel sheet!", exp);
    }
    return parsedExcelData;
  }


  /**
   * @param headingRowNum heading row number
   * @param excelSheet excel sheet to be parsed
   * @return ComplianceResultExcelData
   */
  private CheckSSDRprtXlData parseExcel(final int headingRowNum, final Sheet excelSheet) {
    CheckSSDRprtXlData parsedExcelData = new CheckSSDRprtXlData();
    parsedExcelData.setSsdLabelCount(excelSheet.getLastRowNum() - headingRowNum);
    int cellNum = 1;
    for (int rowNum = headingRowNum + 1; rowNum <= excelSheet.getLastRowNum(); rowNum++ ,cellNum = 1) {
      Iterator<Cell> cellIter = excelSheet.getRow(rowNum).cellIterator();
      CheckSSDXlRprtRowData rowData = new CheckSSDXlRprtRowData();
      while (cellIter.hasNext()) {

        if (cellNum > 3) {
          break;
        }
        if (cellNum == 1) {
          rowData.setSsdLabel(cellIter.next().getStringCellValue());
        }
        else if (cellNum == 2) {
          rowData.setUsecase(cellIter.next().getStringCellValue());
        }
        else if (cellNum == 3) {
          rowData.setStatus(cellIter.next().getStringCellValue());

        }
        if (!parsedExcelData.getSsdLabelToUcMap().containsKey(rowData.getSsdLabel())) {
          List<CheckSSDXlRprtRowData> excelRowDataList = new ArrayList<>();
          excelRowDataList.add(rowData);
          parsedExcelData.getSsdLabelToUcMap().put(rowData.getSsdLabel(), excelRowDataList);
        }
        else {
          List<CheckSSDXlRprtRowData> excelRowDataList =
              parsedExcelData.getSsdLabelToUcMap().get(rowData.getSsdLabel());
          excelRowDataList.add(rowData);
        }
        cellNum++;
      }

    }
    return parsedExcelData;
  }

  /**
   * @param compliRvwOutputStream excel sheet input stream
   * @return Sheet excel sheet
   * @throws org.apache.poi.openxml4j.exceptions.InvalidFormatException
   * @throws IOException exception while reading excel sheet
   * @throws InvalidFormatException exception while parsing excel sheet
   * @throws org.apache.poi.openxml4j.exceptions.InvalidFormatException
   * @throws EncryptedDocumentException
   * @throws InvalidInputException
   */
  private Sheet getExcelSheet(final InputStream compliRvwOutputStream) throws InvalidFormatException, IOException {

    try (Workbook workBook = WorkbookFactory.create(compliRvwOutputStream)) {
      return workBook.getSheetAt(CompliResultComparisionConstants.LOG_MESSAGE_SHEET_NUM);
    }
  }


  /**
   * @param excelSheet excel sheet for which heading row number is to be obtained
   * @return heading row number in the excel sheet
   */
  private int getHeadingRowNum(final Sheet excelSheet) {
    for (int rowNum = excelSheet.getFirstRowNum(); rowNum < excelSheet.getLastRowNum(); rowNum++) {
      Iterator<Cell> cellIter = excelSheet.getRow(rowNum).cellIterator();
      while (cellIter.hasNext()) {
        if (cellIter.next().getStringCellValue().equals(CompliResultComparisionConstants.SSD_LABEL_COL_NAME)) {
          return rowNum;
        }
      }
    }
    return -1;

  }
}
