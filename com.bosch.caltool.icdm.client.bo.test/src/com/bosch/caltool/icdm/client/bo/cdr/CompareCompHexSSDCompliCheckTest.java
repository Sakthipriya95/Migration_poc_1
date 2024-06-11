/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.client.bo.cdr;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.junit.Test;

import com.bosch.caltool.icdm.client.bo.cdr.chkssdxlrptparser.CheckSSDExcelParser;
import com.bosch.caltool.icdm.client.bo.cdr.chkssdxlrptparser.CheckSSDRprtXlData;
import com.bosch.caltool.icdm.client.bo.cdr.chkssdxlrptparser.CheckSSDXlRprtRowData;
import com.bosch.caltool.icdm.client.bo.cdr.jsonparser.ComplianceJsonParser;
import com.bosch.caltool.icdm.client.bo.test.AbstractIcdmClientBOTest;
import com.bosch.caltool.icdm.common.exception.InvalidInputException;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.cdr.CompliReviewOutputData;
import com.bosch.caltool.icdm.model.cdr.CompliReviewResponse;
import com.bosch.caltool.icdm.model.comphex.CompHexResponse;
import com.bosch.caltool.icdm.model.comphex.CompHexStatistics;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * @author apj4cob
 */
public class CompareCompHexSSDCompliCheckTest extends AbstractIcdmClientBOTest {


  /**
   * Test to validate responses of compare hex and ssd compli review service
   *
   * @throws IOException errror reading zip file
   * @throws ApicWebServiceException error during service call
   * @throws InvalidInputException invalid excel report file
   */
  @Test
  public void testCompHexSSDCompliCheckJsonOutput() throws IOException, ApicWebServiceException, InvalidInputException {

    ComplianceJsonParser jsonParserBo = new ComplianceJsonParser();

    CompHexResponse compHexResp = CompliCompHexServiceInvoker.getCompHexResponse();

    CompliCompHexServiceInvoker.invokeCompliRvwService();
    CompliReviewResponse compliRvwResponse = jsonParserBo.getCompliReviewOutput();
    // compare compliRvwService with response of comp hex,write assert statements
    compareJsonOutputStats(compHexResp, compliRvwResponse);
  }


  /**
   * @param compHexResp
   * @param compliRvwResult
   */
  private void compareJsonOutputStats(final CompHexResponse compHexResp,
      final CompliReviewResponse compliReviewResponse) {

    CompHexStatistics compHexStats = compHexResp.getCompHexStatistics();

    CompliReviewOutputData compliRvwOutputData = compliReviewResponse.getHexFileOutput().get(1L);
    // Compliance Parameter statistics compare
    assertEquals(
        "Count of Compliance C-SSD Failed Parameters \n Compare Hex Json Output: {} \n Compliance Review Json Output: {}\n ",
        compHexStats.getStatCompliCssdFailed(), compliRvwOutputData.getCssdFailCount());
    assertEquals(
        "Count of Compliance SSD RVW Failed Parameters \n Compare Hex Json Output: {} \n Compliance Review Json Output: {}\n",
        compHexStats.getStatCompliSSDRvFailed(), compliRvwOutputData.getSsd2RvFailCount());
    assertEquals(
        "Count of Compliance NO RULE Failed Parameters \n Compare Hex Json Output: {} \n Compliance Review Json Output: {}\n",
        compHexStats.getStatCompliNoRuleFailed(), compliRvwOutputData.getNoRuleCount());
    assertEquals(
        "Count of Compliance Rule Passed Parameters \n Compare Hex Json Output: {} \n Compliance Review Json Output: {}\n",
        compHexStats.getStatCompliParamPassed(), compliRvwOutputData.getCompliPassCount());
    // Q-SSD parameter statistics compare
    assertEquals(
        "Count of QSSD Rule Passed Parameters \n Compare Hex Json Output: {} \n Compliance Review Json Output: {}\n",
        compHexStats.getStatQSSDParamFailed(), compliRvwOutputData.getQssdFailCount());
    assertEquals(
        "Count of Compliance Parameters \n Compare Hex Json Output: {} \n Compliance Review Json Output: {} \n",
        compHexStats.getStatCompliParamInA2L(), compliRvwOutputData.getCompliCount());

  }

  /**
   * Test to validate excel sheets of compare hex and ssd compli review service
   *
   * @throws ApicWebServiceException exception during webservice call
   * @throws IOException exception while reading excel file
   * @throws InvalidFormatException exception due to invalid format of excel file
   * @throws InvalidInputException inavlid check ssd excel report file path
   */
  @Test
  public void testCompHexSSDCompliCheckExcelOutput()
      throws InvalidFormatException, IOException, ApicWebServiceException, InvalidInputException {

    CheckSSDExcelParser excelParser = new CheckSSDExcelParser(TESTER_LOGGER);
    CompliCompHexServiceInvoker.invokeCompliRvwService();
    CheckSSDRprtXlData compliExcelData = excelParser.getParsedExcelData(excelParser.getCompliReviewOutputExcelStream());

    CheckSSDRprtXlData compHexExcelData = excelParser.getCompHexExcelSheetData(CompliCompHexServiceInvoker
        .getCompHexExcelOutputPath(CompliCompHexServiceInvoker.getCompHexResponse().getReferenceId()));

    if (CommonUtils.isNotNull(compHexExcelData) && CommonUtils.isNotNull(compliExcelData)) {
      compareExcelData(compliExcelData, compHexExcelData);
    }
  }


  /**
   * @param compliExcelData
   * @param compHexExcelData
   */
  private void compareExcelData(final CheckSSDRprtXlData compliExcelData, final CheckSSDRprtXlData compHexExcelData) {
    // Compare number of SSD Labels in excel sheet
    assertEquals("Count of SSD Label in excel sheet", compliExcelData.getSsdLabelCount(),
        compHexExcelData.getSsdLabelCount());

    // Compare Use Case and status for every SSD Label
    compareRowContentInExcel(compliExcelData, compHexExcelData);
  }

  /**
   * @param compliExcelData
   * @param compHexExcelData
   */
  private void compareRowContentInExcel(final CheckSSDRprtXlData compliExcelData,
      final CheckSSDRprtXlData compHexExcelData) {
    Map<String, List<CheckSSDXlRprtRowData>> compliSSDLabelToUcMap = compliExcelData.getSsdLabelToUcMap();
    Map<String, List<CheckSSDXlRprtRowData>> compHexSSDLabelToUcMap = compHexExcelData.getSsdLabelToUcMap();

    int sizeOfCompliUcMap = compliSSDLabelToUcMap.size();
    int sizeOfCompHexUcMap = compHexSSDLabelToUcMap.size();

    TESTER_LOGGER.debug("Number of ssd labels in check ssd report of compliance review: {}", sizeOfCompliUcMap);
    TESTER_LOGGER.debug("Number of ssd labels in check ssd report of compare hex: {}", sizeOfCompHexUcMap);
    assertEquals("Differences in number of ssd labels in check ssd reports of compare hex and compliance check" +
        Math.abs(sizeOfCompliUcMap - sizeOfCompHexUcMap), sizeOfCompliUcMap, sizeOfCompHexUcMap);

    StringBuilder prefixStr = new StringBuilder(
        "Mismatches in the check ssd reports of compliance check and compare hex with respect to SSD Labels,Usecase and Status. \n");

    StringBuilder rowDataDiffStr = computeDiffInExcelData(compliSSDLabelToUcMap, compHexSSDLabelToUcMap);

    assertTrue(prefixStr.append(rowDataDiffStr).toString(), rowDataDiffStr.toString().isEmpty());
    TESTER_LOGGER.debug(
        "There are no differences in check ssd reports of compare hex and compliance review with respect to ssd label's usecase and status contents");
  }


  /**
   * Check for mismatches in excel row data if any
   *
   * @param compliSSDLabelToUcMap
   * @param compHexSSDLabelToUcMap
   * @param rowDataDiffStr
   */
  private StringBuilder computeDiffInExcelData(final Map<String, List<CheckSSDXlRprtRowData>> compliSSDLabelToUcMap,
      final Map<String, List<CheckSSDXlRprtRowData>> compHexSSDLabelToUcMap) {
    StringBuilder rowDataDiffStr = new StringBuilder();
    String format = "%-30s%30s%n";

    for (Entry<String, List<CheckSSDXlRprtRowData>> entry : compliSSDLabelToUcMap.entrySet()) {
      String ssdLabel = entry.getKey();

      List<CheckSSDXlRprtRowData> compliLabelUcList = compliSSDLabelToUcMap.get(ssdLabel);
      List<CheckSSDXlRprtRowData> compHexLabelUcList = compHexSSDLabelToUcMap.get(ssdLabel);
      // Creating assertion error message string to display ,in case assertion fails
      List<CheckSSDXlRprtRowData> diffInCompliRowData = new ArrayList<>(compliLabelUcList);
      diffInCompliRowData.removeIf(compHexLabelUcList::contains);
      List<CheckSSDXlRprtRowData> diffInCompHexRowData = new ArrayList<>(compHexLabelUcList);
      diffInCompHexRowData.removeIf(compliLabelUcList::contains);
      if (!diffInCompliRowData.isEmpty()) {
        rowDataDiffStr.append(String.format(format, "ComplianceCheck :", diffInCompliRowData));

      }
      if (!diffInCompHexRowData.isEmpty()) {
        rowDataDiffStr.append(String.format(format, "CompareHex :", diffInCompHexRowData));
      }
      if (!diffInCompliRowData.isEmpty() || !diffInCompHexRowData.isEmpty()) {
        rowDataDiffStr.append("Differences in number of row entry for a label" + ssdLabel).append(':')
            .append(Math.abs(compliLabelUcList.size() - compHexLabelUcList.size())).append('\n');
      }
    }
    return rowDataDiffStr;
  }
}
