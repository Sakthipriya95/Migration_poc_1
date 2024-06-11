/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.client.bo.a2l;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import com.bosch.caltool.icdm.common.exception.InvalidInputException;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.a2l.ImportA2lWpRespData;
import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvException;
import com.opencsv.exceptions.CsvValidationException;

/**
 * @author dmr1cob
 */
public class A2lRespImportCSVParser {

  private final String csvFilePath;
  private final String seperator;
  private final A2LWpRespImportParser a2LWpRespImportParser;
  private String encoding;

  /**
   * @param csvFilePath input csv file path
   * @param seperator csv file seperator
   * @param headingRowNum row number indicating start of file
   */
  public A2lRespImportCSVParser(final A2LWPInfoBO a2lWpInfoBo, final String csvFilePath, final String seperator) {
    this.csvFilePath = csvFilePath;
    this.seperator = seperator;
    this.a2LWpRespImportParser = new A2LWpRespImportParser(a2lWpInfoBo);
  }

  public String getEncoding() throws InvalidInputException {
    try (FileInputStream inputStream = new FileInputStream(this.csvFilePath)) {
      this.encoding = new UnicodeBOMInputStream(inputStream).getEncoding();
    }
    catch (IOException p) {
      throw new InvalidInputException("Error while parsing CSV file. Please select a valid CSV File!", p);
    }
    return this.encoding;
  }

  /**
   * @param headingRowNum
   * @return List of columns in csv file as string array
   * @throws InvalidInputException error while parsing csv file
   */
  public List<String[]> parseCsv(final Long headingRowNum) throws InvalidInputException {
    List<String[]> inputCsvData;
    try (FileInputStream inputStream = new FileInputStream(this.csvFilePath);
        Reader filereader = new InputStreamReader(inputStream, this.encoding);) {
      CSVReader csvReader = getCsvReader(headingRowNum, filereader);
      inputCsvData = csvReader.readAll();
    }
    catch (IOException | CsvException exp) {
      throw new InvalidInputException("Error while parsing CSV file. Please select a valid CSV File!", exp);
    }
    return inputCsvData;
  }

  /**
   * @param filePath
   * @param seperator
   * @param startRow
   * @return
   * @throws IOException
   */
  private CSVReader getCsvReader(final Long startRow, final Reader filereader) {
    CSVParser parser = new CSVParserBuilder().withSeparator(this.seperator.charAt(0)).build();
    return new CSVReaderBuilder(filereader).withSkipLines(startRow.intValue()).withCSVParser(parser).build();
  }

  /**
   * @param headingRowNum
   * @return header array List
   * @throws InvalidInputException Error while parsing CSV file
   */
  public Map<String, Integer> getHeaders(final Long headingRowNum) throws InvalidInputException {
    Map<String, Integer> headerMap = new HashMap<>();
    try (FileInputStream inputStream = new FileInputStream(this.csvFilePath);
        Reader filereader = new InputStreamReader(inputStream, this.encoding);) {
      CSVReader csvReader = getCsvReader(headingRowNum - 1, filereader);
      String[] header = csvReader.readNext();
      if (null != header) {
        if (header.length == 1) {
          throw new InvalidInputException(
              "Import is not possible. Selected csv file is having a different separator other than \"" +
                  this.seperator + "\".\r\n" + "Please use a \"" + this.seperator + "\" separated csv file.");
        }
        for (int i = 0; i < header.length; i++) {
          headerMap.put(header[i], i);
        }
      }
    }
    catch (CsvValidationException | IOException exp) {
      throw new InvalidInputException("Please select a valid CSV File!", exp);
    }
    return headerMap;
  }

  /**
   * @param profileData
   * @param prefixForResp
   * @param headRowNum
   * @param string
   * @param functionCol function col number
   * @param labelCol boolean whether label col is selecetd
   * @param wpDefImp boolean whether it is wpdef page
   * @param funcorLabelColNum func or label column
   * @param wpColNum wp col number
   * @param respColNum resp col number
   * @param respTypeColumn resp type col number
   * @param isInputFileVarGrp boolean is var grp
   * @param varGrpColNum var group col number
   * @param a2lVarGrpId a2l var grp id
   * @return {@link ImportA2lWpRespData} obj
   * @throws InvalidInputException Exception
   */
  public ImportA2lWpRespData getImportA2lWpRespData(final ImportA2lWpRespInputProfileData profileData,
      final String prefixForResp, final Long headingRowNum)
      throws InvalidInputException {
    ImportA2lWpRespData importCsvData = new ImportA2lWpRespData();
    List<String[]> inputCsvData = parseCsv(headingRowNum);
    if (profileData.isLabelCol()) {
      labelBasedParsing(profileData, importCsvData, inputCsvData, prefixForResp);
    }
    else if (profileData.isFuncCol()) {
      funcBasedParsing(profileData, importCsvData, inputCsvData, prefixForResp);
    }
    else if (profileData.isWpDefImp()) {
      wpDefBasedParsing(profileData, importCsvData, inputCsvData, prefixForResp);
    }
    return importCsvData;
  }

  /**
   * @param wpColNum
   * @param respColNum
   * @param respTypeColumn
   * @param isInputFileVarGrp
   * @param varGrpColNum
   * @param a2lVarGrpId
   * @param importCsvData
   * @param inputCsvData
   * @param prefixForResp
   * @throws InvalidInputException
   */
  private void wpDefBasedParsing(final ImportA2lWpRespInputProfileData profileData,
      final ImportA2lWpRespData importCsvData, final List<String[]> inputCsvData, final String prefixForResp)
      throws InvalidInputException {
    Map<String, Map<String, String>> varGrp2WpRespMap = new HashMap<>();
    Map<String, String> wpRespMap = new HashMap<>();
    Map<String, String> respAndTypeMap = new HashMap<>();
    SortedSet<String> invalidRespType = new TreeSet<>();
    SortedSet<String> respWithMultipleType = new TreeSet<>();
    SortedSet<String> unmatchedVarGrp = new TreeSet<>();

    for (String[] data : inputCsvData) {
      if (!CommonUtils.isEmptyString(data[profileData.getWpColNum()])) {
        String respTypeVal = null == profileData.getRespTypeColNum() ? null : data[profileData.getRespTypeColNum()];

        ImportA2lWpRespInputFileData inputFileData = new ImportA2lWpRespInputFileData();
        inputFileData.setInputFileVarGrp(profileData.isInputFileVarGrp());
        inputFileData.setA2lVarGrpId(profileData.getA2lVarGrpId());
        inputFileData.setImportA2lWpRespData(importCsvData);
        inputFileData.setWpName(data[profileData.getWpColNum()]);
        inputFileData.setVarGrpName(profileData.getVarGrpColNum() == -1 ? null : data[profileData.getVarGrpColNum()]);
        inputFileData.setRespName(data[profileData.getRespColNum()]);
        inputFileData.setRespTypeName(respTypeVal);

        this.a2LWpRespImportParser.wpDefBasedParsing(inputFileData, varGrp2WpRespMap, wpRespMap, respAndTypeMap,
            invalidRespType, respWithMultipleType, unmatchedVarGrp);
      }
    }
    this.a2LWpRespImportParser.showInvalidRespTypes(invalidRespType);
    this.a2LWpRespImportParser.showMutlipleTypeForSameResp(respWithMultipleType);
    // if there are var group in excel which is not available in a2l file , skip the params and show error msg
    this.a2LWpRespImportParser.showMissingVarGrps(unmatchedVarGrp);
    this.a2LWpRespImportParser.showMisMatchOfRespTypeInDb(respAndTypeMap, prefixForResp);
  }


  /**
   * @param funcorLabelColNum
   * @param wpColNum
   * @param respColNum
   * @param respTypeColumn
   * @param isInputFileVarGrp
   * @param varGrpColNum
   * @param a2lVarGrpId
   * @param importCsvData
   * @param inputCsvData
   * @param prefixForResp
   * @throws InvalidInputException
   */
  private void funcBasedParsing(final ImportA2lWpRespInputProfileData profileData,
      final ImportA2lWpRespData importCsvData, final List<String[]> inputCsvData, final String prefixForResp)
      throws InvalidInputException {
    Map<String, Map<String, String>> funcWpRespMap = new HashMap<>();
    SortedSet<String> unmatchedVarGrp = new TreeSet<>();
    Map<String, String> respAndTypeMap = new HashMap<>();
    SortedSet<String> invalidRespType = new TreeSet<>();
    SortedSet<String> respWithMultipleType = new TreeSet<>();
    for (String[] data : inputCsvData) {
      if (!CommonUtils.isEmptyString(data[profileData.getFuncOrLabelCol()]) &&
          !CommonUtils.isEmptyString(data[profileData.getWpColNum()])) {
        String respTypeVal = null == profileData.getRespTypeColNum() ? null : data[profileData.getRespTypeColNum()];

        ImportA2lWpRespInputFileData inputFileData = new ImportA2lWpRespInputFileData();
        inputFileData.setInputFileVarGrp(profileData.isInputFileVarGrp());
        inputFileData.setA2lVarGrpId(profileData.getA2lVarGrpId());
        inputFileData.setImportA2lWpRespData(importCsvData);
        inputFileData.setFuncName(data[profileData.getFuncOrLabelCol()]);
        inputFileData.setWpName(data[profileData.getWpColNum()]);
        inputFileData.setVarGrpName(profileData.getVarGrpColNum() == -1 ? null : data[profileData.getVarGrpColNum()]);
        inputFileData.setRespName(data[profileData.getRespColNum()]);
        inputFileData.setRespTypeName(respTypeVal);

        this.a2LWpRespImportParser.funcBasedParsing(inputFileData, funcWpRespMap, unmatchedVarGrp, respAndTypeMap,
            invalidRespType, respWithMultipleType);
      }
    }
    this.a2LWpRespImportParser.showMissingVarGrps(unmatchedVarGrp);
    this.a2LWpRespImportParser.showInvalidRespTypes(invalidRespType);
    this.a2LWpRespImportParser.showMutlipleTypeForSameResp(respWithMultipleType);
    this.a2LWpRespImportParser.showMisMatchOfRespTypeInDb(respAndTypeMap, prefixForResp);
  }

  /**
   * @param funcorLabelColNum
   * @param wpColNum
   * @param respColNum
   * @param respTypeColumn
   * @param isInputFileVarGrp
   * @param varGrpColNum
   * @param a2lVarGrpId
   * @param importCsvData
   * @param inputCsvData
   * @param prefixForResp
   * @throws InvalidInputException
   */
  private void labelBasedParsing(final ImportA2lWpRespInputProfileData profileData,
      final ImportA2lWpRespData importCsvData, final List<String[]> inputCsvData, final String prefixForResp)
      throws InvalidInputException {
    SortedSet<String> unmatchedVarGrp = new TreeSet<>();
    Map<String, String> respAndTypeMap = new HashMap<>();
    SortedSet<String> invalidRespType = new TreeSet<>();
    SortedSet<String> respWithMultipleType = new TreeSet<>();
    for (String[] data : inputCsvData) {
      if (!CommonUtils.isEmptyString(data[profileData.getFuncOrLabelCol()]) &&
          !CommonUtils.isEmptyString(data[profileData.getWpColNum()])) {
        String respTypeVal = null == profileData.getRespTypeColNum() ? null : data[profileData.getRespTypeColNum()];

        ImportA2lWpRespInputFileData inputFileData = new ImportA2lWpRespInputFileData();
        inputFileData.setParamName(data[profileData.getFuncOrLabelCol()]);
        inputFileData.setWpName(data[profileData.getWpColNum()]);
        inputFileData.setVarGrpName(profileData.getVarGrpColNum() == -1 ? null : data[profileData.getVarGrpColNum()]);
        inputFileData.setRespName(data[profileData.getRespColNum()]);
        inputFileData.setRespTypeName(respTypeVal);
        inputFileData.setInputFileVarGrp(profileData.isInputFileVarGrp());
        inputFileData.setA2lVarGrpId(profileData.getA2lVarGrpId());
        inputFileData.setImportA2lWpRespData(importCsvData);

        this.a2LWpRespImportParser.labelBasedParsing(inputFileData, unmatchedVarGrp, respAndTypeMap, invalidRespType,
            respWithMultipleType);
      }
    }
    this.a2LWpRespImportParser.showMissingVarGrps(unmatchedVarGrp);
    this.a2LWpRespImportParser.showInvalidRespTypes(invalidRespType);
    this.a2LWpRespImportParser.showMutlipleTypeForSameResp(respWithMultipleType);
    this.a2LWpRespImportParser.showMisMatchOfRespTypeInDb(respAndTypeMap, prefixForResp);
  }


  public void setEncoding(final String encoding) {
    this.encoding = encoding;
  }

  /**
   * @param text
   * @return
   * @throws InvalidInputException
   */
  public Map<Integer, String> getRowContentMap() throws InvalidInputException {

    Map<Integer, String> rowNumWithContentMap = new HashMap<>();
    try (FileInputStream inputStream = new FileInputStream(this.csvFilePath);
        Reader filereader = new InputStreamReader(inputStream, this.encoding);) {
      CSVReader csvReader = getCsvReader(Long.valueOf(0), filereader);

      for (int rowIndex = 1; rowIndex <= 20; rowIndex++) {
        String[] rowContentArr = csvReader.readNext();
        if ((null != rowContentArr) && CommonUtils.isNotEmptyString(rowContentArr[0])) {
          constructRowContentMap(rowNumWithContentMap, rowContentArr, rowIndex);
        }
      }
    }
    catch (CsvValidationException | IOException exp) {
      throw new InvalidInputException("Please select a valid CSV File!", exp);
    }
    return rowNumWithContentMap;

  }

  /**
   * @param rowNumWithContentMap
   * @param rowContentArr
   * @param rowIndex
   */
  private void constructRowContentMap(final Map<Integer, String> rowNumWithContentMap, final String[] rowContentArr,
      final int rowIndex) {
    StringBuilder rowContent = new StringBuilder();

    for (String element : rowContentArr) {
      rowContent.append(element).append("  ");
      rowNumWithContentMap.put(rowIndex, rowContent.toString());

      if (rowContent.toString().length() > 15) {
        rowNumWithContentMap.put(rowIndex, rowContent.toString().substring(0, 15) + "......");
        break;
      }
    }
  }
}
