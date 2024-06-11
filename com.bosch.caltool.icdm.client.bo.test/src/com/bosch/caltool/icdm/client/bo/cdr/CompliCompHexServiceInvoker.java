/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.client.bo.cdr;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.cdr.CompliReviewInputMetaData;
import com.bosch.caltool.icdm.model.cdr.review.PidcData;
import com.bosch.caltool.icdm.model.comphex.CompHexMetaData;
import com.bosch.caltool.icdm.model.comphex.CompHexResponse;
import com.bosch.caltool.icdm.ws.rest.client.cdr.CompliReviewServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.comphex.CompHexWithCDFxServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * @author apj4cob
 */
public class CompliCompHexServiceInvoker {

  private CompliCompHexServiceInvoker() {
    throw new IllegalStateException("Utility class");
  }

  /**
   * @param refId reference id
   * @return output check ssd excel report path
   * @throws ApicWebServiceException exception while calling service
   */
  public static String getCompHexExcelOutputPath(final String refId) throws ApicWebServiceException {

    CompHexWithCDFxServiceClient compHexServiceClient = new CompHexWithCDFxServiceClient();
    return compHexServiceClient.downloadCompHexReport(refId, ApicConstants.REPORT_TYPE_EXCEL,
        CompliResultComparisionConstants.COMPARE_HEX_JUNIT_EXCEL_REPORT_NAME,
        CommonUtils.getICDMTmpFileDirectoryPath());

  }

  /**
   * @return CompHexResponse
   * @throws ApicWebServiceException Exception while invoking webservice
   */
  public static CompHexResponse getCompHexResponse() throws ApicWebServiceException {
    CompHexWithCDFxServiceClient compHexServiceClient = new CompHexWithCDFxServiceClient();
    PidcData pidcData = new PidcData();
    pidcData.setPidcA2lId(CompliResultComparisionConstants.PIDC_A2L_ID);
    pidcData.setSelPIDCVariantId(CompliResultComparisionConstants.VAR_ID);
    pidcData.setA2lFileId(CompliResultComparisionConstants.A2L_ID);
    pidcData.setSourcePidcVerId(CompliResultComparisionConstants.PIDC_VERS_ID);

    CompHexMetaData metaData = new CompHexMetaData();
    metaData.setPidcData(pidcData);
    metaData.setHexFileName(CompliResultComparisionConstants.HEX_FILE_NAME);
    metaData.setSrcHexFilePath(CompliResultComparisionConstants.HEX_FILE_PATH);
    return compHexServiceClient.getCompHexResult(metaData);

  }

  /**
   * @throws ApicWebServiceException Exception while invoking the service
   */
  public static void invokeCompliRvwService() throws ApicWebServiceException {
    CompliReviewServiceClient servClient = new CompliReviewServiceClient();
    servClient.executeCompliReviewUsingPidcA2lId(CompliResultComparisionConstants.PIDC_A2L_ID_STR,
        CompliResultComparisionConstants.A2L_FILE_NAME, createCompliInputMetaData(),
        new HashSet<>(Arrays.asList(CompliResultComparisionConstants.HEX_FILE_PATH)),
        CompliResultComparisionConstants.OUTPUT_PATH);
  }

  /**
   * @return CompliReviewInputMetaData
   */
  public static CompliReviewInputMetaData createCompliInputMetaData() {
    CompliReviewInputMetaData input = getCompliInputMetaData();
    Map<Long, Long> hexFilePidcElement = new HashMap<>();
    hexFilePidcElement.put(1L, CompliResultComparisionConstants.VAR_ID);

    input.setHexFilePidcElement(hexFilePidcElement);

    return input;
  }

  /**
   * @return Input meta data for compli review
   */
  private static CompliReviewInputMetaData getCompliInputMetaData() {
    CompliReviewInputMetaData input = new CompliReviewInputMetaData();
    input.setA2lFileName(CompliResultComparisionConstants.A2L_FILE_NAME);
    input.setPverName("DA_MDG1G");
    input.setPverVariant("6R_15B050D");
    input.setPverRevision("0");

    Map<Long, String> hexfileIdxMap = new HashMap<>();
    hexfileIdxMap.put(1L, CompliResultComparisionConstants.HEX_FILE_NAME);

    input.setHexfileIdxMap(hexfileIdxMap);
    return input;
  }
}
