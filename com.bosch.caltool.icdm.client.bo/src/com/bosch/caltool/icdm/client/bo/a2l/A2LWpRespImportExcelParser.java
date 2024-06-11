/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.client.bo.a2l;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import com.bosch.caltool.icdm.client.bo.Activator;
import com.bosch.caltool.icdm.common.exception.InvalidInputException;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.a2l.ImportA2lWpRespData;

/**
 * @author bru2cob
 */
public class A2LWpRespImportExcelParser {

  private final String excelFilePath;

  private final A2LWpRespImportParser a2LWpRespImportParser;

  private Workbook workBook;

  /**
   * @param a2lWpInfoBo {@link A2LWPInfoBO}
   * @param excelPath input excel file path
   * @param headingRowNum heding row number
   */
  public A2LWpRespImportExcelParser(final A2LWPInfoBO a2lWpInfoBo, final String excelPath) {
    super();
    this.excelFilePath = excelPath;
    this.a2LWpRespImportParser = new A2LWpRespImportParser(a2lWpInfoBo);
  }


  /**
   * @param sheetName Selected Excel file sheet name
   * @param prefixForResp
   * @param headingRowNum
   * @param functionCol Import type function column
   * @param labelCol Import type label column
   * @param wpDefImp Import type wp def page
   * @param funcorLabelColNum function or label column number
   * @param wpColNum workpakage column name
   * @param respColNum resp column number
   * @param respTypeColumn resp type column number
   * @param isExcelVarGrp whether excel variant grp
   * @param varGrpColNum variant grp colum number
   * @param a2lVarGrpId vriant grp id
   * @return {@link ImportA2lWpRespData}
   * @throws InvalidInputException exception
   */
  public ImportA2lWpRespData parseExcelSheet(final String sheetName, final ImportA2lWpRespInputProfileData profileData,
      final String prefixForResp, final Long headingRowNum)
      throws InvalidInputException {
    ImportA2lWpRespData importExcelData = new ImportA2lWpRespData();
    try (InputStream inp = new FileInputStream(this.excelFilePath)) {
      this.workBook = getWorkBook(inp);
      Sheet selSheet = this.workBook.getSheet(sheetName);
      int rowCount = selSheet.getLastRowNum();
      int dataRowNum = headingRowNum.intValue();
      if (profileData.isLabelCol()) {
        labelBasedParsing(profileData, importExcelData, selSheet, rowCount, dataRowNum, prefixForResp);
      }
      else if (profileData.isFuncCol()) {
        funcBasedParsing(profileData, importExcelData, selSheet, rowCount, dataRowNum, prefixForResp);
      }
      else if (profileData.isWpDefImp()) {
        wpDefBasedParsing(profileData, importExcelData, selSheet, rowCount, dataRowNum, prefixForResp);
      }
    }
    catch (Exception exp) {
      closeWrkBook();
      throw new InvalidInputException(exp.getMessage(), exp);
    }
    return importExcelData;
  }


  /**
   * @param inp
   * @return
   * @throws IOException
   * @throws EncryptedDocumentException
   * @throws InvalidFormatException
   */
  private Workbook getWorkBook(final InputStream inp)
      throws IOException, InvalidFormatException {
    if (this.workBook == null) {
      this.workBook = WorkbookFactory.create(inp);
    }
    return this.workBook;
  }

  /**
   * Parsing for wp definition page
   *
   * @param wpColNum
   * @param respColNum
   * @param respTypeColumn
   * @param varGrpColNum
   * @param isExcelVarGrp
   * @param a2lVarGrpId
   * @param importExcelData
   * @param selSheet
   * @param rowCount
   * @param dataRowNum
   * @param prefixForResp
   * @throws InvalidInputException
   */
  private void wpDefBasedParsing(final ImportA2lWpRespInputProfileData profileData,
      final ImportA2lWpRespData importExcelData, final Sheet selSheet, final int rowCount, final int dataRowNum,
      final String prefixForResp)
      throws InvalidInputException {
    Map<String, Map<String, String>> varGrp2WpRespMap = new HashMap<>();
    Map<String, String> wpRespMap = new HashMap<>();
    Map<String, String> respAndTypeMap = new HashMap<>();
    SortedSet<String> invalidRespType = new TreeSet<>();
    SortedSet<String> respWithMultipleType = new TreeSet<>();
    SortedSet<String> unmatchedVarGrp = new TreeSet<>();

    for (int i = dataRowNum; i <= rowCount; i++) {
      Row row = selSheet.getRow(i);
      if (row != null) {

        Cell wpCell = row.getCell(profileData.getWpColNum());
        Cell respCell = row.getCell(profileData.getRespColNum());
        Cell varGrpCell = profileData.getVarGrpColNum() == -1 ? null : row.getCell(profileData.getVarGrpColNum());

        if (null != wpCell) {
          String respTypeVal = getRespTypeVal(profileData.getRespTypeColNum(), row);

          ImportA2lWpRespInputFileData inputFileData = new ImportA2lWpRespInputFileData();
          inputFileData.setInputFileVarGrp(profileData.isInputFileVarGrp());
          inputFileData.setA2lVarGrpId(profileData.getA2lVarGrpId());
          inputFileData.setImportA2lWpRespData(importExcelData);
          inputFileData.setWpName(wpCell.getStringCellValue());
          inputFileData.setVarGrpName(null == varGrpCell ? null : varGrpCell.getStringCellValue());
          inputFileData.setRespName(null == respCell ? null : respCell.getStringCellValue());
          inputFileData.setRespTypeName(respTypeVal);

          this.a2LWpRespImportParser.wpDefBasedParsing(inputFileData, varGrp2WpRespMap, wpRespMap, respAndTypeMap,
              invalidRespType, respWithMultipleType, unmatchedVarGrp);
        }
      }
    }

    this.a2LWpRespImportParser.showInvalidRespTypes(invalidRespType);
    this.a2LWpRespImportParser.showMutlipleTypeForSameResp(respWithMultipleType);
    // if there are var group in excel which is not available in a2l file , skip the params and show error msg
    this.a2LWpRespImportParser.showMissingVarGrps(unmatchedVarGrp);
    this.a2LWpRespImportParser.showMisMatchOfRespTypeInDb(respAndTypeMap, prefixForResp);

  }

  /**
   * Parsing if function column is selected in excel
   *
   * @param funcColNum
   * @param wpColNum
   * @param respColNum
   * @param respTypeColumn
   * @param isExcelVarGrp
   * @param varGrpColNum
   * @param selA2lVarGrpId
   * @param importExcelData
   * @param selSheet
   * @param rowCount
   * @param dataRowNum
   * @param prefixForResp
   * @throws InvalidInputException
   */
  private void funcBasedParsing(final ImportA2lWpRespInputProfileData profileData,
      final ImportA2lWpRespData importExcelData, final Sheet selSheet, final int rowCount, final int dataRowNum,
      final String prefixForResp)
      throws InvalidInputException {
    // used to check whether label import specific excel is used for func. Only one wp-resp should be available for
    // a func
    Map<String, Map<String, String>> funcWpRespMap = new HashMap<>();
    SortedSet<String> unmatchedVarGrp = new TreeSet<>();
    Map<String, String> respAndTypeMap = new HashMap<>();
    SortedSet<String> invalidRespType = new TreeSet<>();
    SortedSet<String> respWithMultipleType = new TreeSet<>();


    for (int i = dataRowNum; i <= rowCount; i++) {
      Row row = selSheet.getRow(i);
      if (row != null) {
        Cell funCell = row.getCell(profileData.getFuncOrLabelCol());
        Cell wpCell = row.getCell(profileData.getWpColNum());
        Cell respCell = row.getCell(profileData.getRespColNum());
        Cell varGrpCell = profileData.getVarGrpColNum() == -1 ? null : row.getCell(profileData.getVarGrpColNum());

        if ((null != funCell) && (null != wpCell)) {
          String respTypeVal = getRespTypeVal(profileData.getRespTypeColNum(), row);

          ImportA2lWpRespInputFileData inputFileData = new ImportA2lWpRespInputFileData();
          inputFileData.setInputFileVarGrp(profileData.isInputFileVarGrp());
          inputFileData.setA2lVarGrpId(profileData.getA2lVarGrpId());
          inputFileData.setImportA2lWpRespData(importExcelData);
          inputFileData.setFuncName(funCell.getStringCellValue());
          inputFileData.setWpName(wpCell.getStringCellValue());
          inputFileData.setVarGrpName(null == varGrpCell ? null : varGrpCell.getStringCellValue());
          inputFileData.setRespName(null == respCell ? null : respCell.getStringCellValue());
          inputFileData.setRespTypeName(respTypeVal);

          this.a2LWpRespImportParser.funcBasedParsing(inputFileData, funcWpRespMap, unmatchedVarGrp, respAndTypeMap,
              invalidRespType, respWithMultipleType);
        }
      }
    }

    this.a2LWpRespImportParser.showMissingVarGrps(unmatchedVarGrp);
    this.a2LWpRespImportParser.showInvalidRespTypes(invalidRespType);
    this.a2LWpRespImportParser.showMutlipleTypeForSameResp(respWithMultipleType);
    this.a2LWpRespImportParser.showMisMatchOfRespTypeInDb(respAndTypeMap, prefixForResp);
  }

  /**
   * @param respTypeColumn
   * @param row
   * @return
   */
  private String getRespTypeVal(final Integer respTypeColumn, final Row row) {
    Cell respTypeCell = null == respTypeColumn ? null : row.getCell(respTypeColumn);
    return null == respTypeCell ? null : respTypeCell.getStringCellValue();
  }


  /**
   * Parsing if label column is selected in excel
   *
   * @param labelColNum
   * @param wpColNum
   * @param respColNum
   * @param respTypeColumn
   * @param isExcelVarGrp
   * @param varGrpColNum
   * @param selA2lVarGrpId
   * @param importExcelData
   * @param selSheet
   * @param rowCount
   * @param dataRowNum
   * @param prefixForResp
   * @throws InvalidInputException
   */
  private void labelBasedParsing(final ImportA2lWpRespInputProfileData profileData,
      final ImportA2lWpRespData importExcelData, final Sheet selSheet, final int rowCount, final int dataRowNum,
      final String prefixForResp)
      throws InvalidInputException {
    SortedSet<String> unmatchedVarGrp = new TreeSet<>();
    Map<String, String> respAndTypeMap = new HashMap<>();
    SortedSet<String> invalidRespType = new TreeSet<>();
    SortedSet<String> respWithMultipleType = new TreeSet<>();
    List<Integer> errorList = new ArrayList<>();
    for (int i = dataRowNum; i <= rowCount; i++) {
      Row row = selSheet.getRow(i);
      if (row != null) {
        Cell labelCell = row.getCell(profileData.getFuncOrLabelCol());
        Cell wpCell = row.getCell(profileData.getWpColNum());
        Cell respCell = row.getCell(profileData.getRespColNum());
        Cell varGrpCell = profileData.getVarGrpColNum() == -1 ? null : row.getCell(profileData.getVarGrpColNum());

        if ((null != labelCell) && (null != wpCell)) {
          String respTypeVal = getRespTypeVal(profileData.getRespTypeColNum(), row);

          ImportA2lWpRespInputFileData inputFileData = new ImportA2lWpRespInputFileData();
          try {
            inputFileData.setParamName(labelCell.getStringCellValue());
            inputFileData.setWpName(wpCell.getStringCellValue());
            inputFileData.setVarGrpName(varGrpCell == null ? null : varGrpCell.getStringCellValue());
            inputFileData.setRespName(null == respCell ? null : respCell.getStringCellValue());
            inputFileData.setRespTypeName(respTypeVal);
            inputFileData.setInputFileVarGrp(profileData.isInputFileVarGrp());
            inputFileData.setA2lVarGrpId(profileData.getA2lVarGrpId());
            inputFileData.setImportA2lWpRespData(importExcelData);
          }
          catch (IllegalStateException exp) {
            errorList.add(row.getRowNum() + 1);
          }

          this.a2LWpRespImportParser.labelBasedParsing(inputFileData, unmatchedVarGrp, respAndTypeMap, invalidRespType,
              respWithMultipleType);
        }
      }
    }
    if (CommonUtils.isNotEmpty(errorList)) {
      throw new InvalidInputException("The Excel sheet contains Invalid data in row number(s): \n" +
          errorList.toString() + "\nPlease modify the data and try again");
    }
    this.a2LWpRespImportParser.showMissingVarGrps(unmatchedVarGrp);
    this.a2LWpRespImportParser.showInvalidRespTypes(invalidRespType);
    this.a2LWpRespImportParser.showMutlipleTypeForSameResp(respWithMultipleType);
    this.a2LWpRespImportParser.showMisMatchOfRespTypeInDb(respAndTypeMap, prefixForResp);
  }

  /**
   * @param sheetName selected sheet
   * @return col names of the sheet
   * @throws InvalidInputException exception
   */
  public Map<String, Integer> getSheetColNames(final String sheetName, final Long headingRowNum)
      throws InvalidInputException {
    Map<String, Integer> colNamesMap = new HashMap<>();
    try (InputStream inp = new FileInputStream(this.excelFilePath)) {
      this.workBook = getWorkBook(inp);
      Sheet selSheet = this.workBook.getSheet(sheetName);
      int colNameRowNum = (int) (headingRowNum - 1);
      Row row = selSheet.getRow(colNameRowNum);
      if (row == null) {
        throw new InvalidInputException(IUIConstants.INVALID_EXCEL_SHEET);
      }
      int colCount = row.getLastCellNum();
      int colIndex;
      for (colIndex = 0; colIndex < colCount; colIndex++) {
        Cell cell = row.getCell(colIndex);
        if (cell.getStringCellValue() != null) {
          colNamesMap.put(cell.getStringCellValue(), colIndex);
        }
      }
    }
    catch (Exception exp) {
      closeWrkBook();
      throw new InvalidInputException(IUIConstants.INVALID_EXCEL_SHEET, exp);
    }
    return colNamesMap;
  }


  /**
   * @returns excel sheet names
   * @throws InvalidInputException exception
   */
  public List<String> getExcelSheetNames() throws InvalidInputException {
    List<String> sheetnames = new ArrayList<>();

    try (InputStream inp = new FileInputStream(this.excelFilePath)) {
      this.workBook = getWorkBook(inp);
      int noOfSheets = this.workBook.getNumberOfSheets();
      int sheetindex = 0;
      while (sheetindex < noOfSheets) {
        sheetnames.add(this.workBook.getSheetAt(sheetindex).getSheetName());
        sheetindex++;
      }
    }
    catch (Exception exp) {
      closeWrkBook();
      throw new InvalidInputException(IUIConstants.INVALID_EXCEL_SHEET, exp);
    }
    return sheetnames;
  }


  /**
   * @param sheetName
   * @return
   * @throws InvalidInputException
   */
  public Map<Integer, String> getFirstColumnContent(final String sheetName) throws InvalidInputException {
    Map<Integer, String> rowContentMap = new HashMap<>();
    try (InputStream inp = new FileInputStream(this.excelFilePath)) {

      this.workBook = getWorkBook(inp);
      Sheet selSheet = this.workBook.getSheet(sheetName);

      for (int rowIndex = 1; rowIndex <= 20; rowIndex++) {
        Row row = selSheet.getRow(rowIndex - 1);
        if (null != row) {
          constructRowContentMap(rowContentMap, rowIndex, row);
        }
      }

    }
    catch (Exception exp) {
      closeWrkBook();
      throw new InvalidInputException(IUIConstants.INVALID_EXCEL_SHEET, exp);
    }
    return rowContentMap;
  }

  /**
   * To close the workbook
   */
  public void closeWrkBook() {
    if (null != this.workBook) {
      try {
        this.workBook.close();
        this.workBook = null;
      }
      catch (IOException exp) {
        CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
      }
    }
  }

  /**
   * @param rowContentMap
   * @param rowIndex
   * @param row
   */
  private void constructRowContentMap(final Map<Integer, String> rowContentMap, final int rowIndex, final Row row) {

    int colCount = row.getLastCellNum();
    StringBuilder rowContent = new StringBuilder();
    for (int colIndex = 0; colIndex < colCount; colIndex++) {

      Cell cell = row.getCell(colIndex);

      if (null != cell) {
        cell.setCellType(Cell.CELL_TYPE_STRING);
        if (null != cell.getStringCellValue()) {
          rowContent.append(cell.getStringCellValue() + "  ");
          rowContentMap.put(rowIndex, rowContent.toString());
        }
      }
      if (rowContent.toString().length() > 15) {
        rowContentMap.put(rowIndex, rowContent.toString().substring(0, 15) + "......");
        break;
      }
    }
  }


}
