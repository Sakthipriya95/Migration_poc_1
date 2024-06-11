/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.client.bo.cdr.jsonparser;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import com.bosch.caltool.icdm.client.bo.cdr.CompliResultComparisionConstants;
import com.bosch.caltool.icdm.common.exception.InvalidInputException;
import com.bosch.caltool.icdm.common.util.JsonUtil;
import com.bosch.caltool.icdm.common.util.ZipUtils;
import com.bosch.caltool.icdm.model.cdr.CompliReviewResponse;

/**
 * @author APJ4COB
 */
public class ComplianceJsonParser {

  /**
   * @return CompliReviewResponse model created out of json file
   * @throws InvalidInputException due to invalid file input
   */
  public CompliReviewResponse getCompliReviewOutput() throws InvalidInputException {

    CompliReviewResponse compliRvwResponse = null;
    try (InputStream compliRvwOutputStream = new FileInputStream(CompliResultComparisionConstants.OUTPUT_PATH)) {

      Map<String, InputStream> mapOfInpStream = ZipUtils.unzipIfZipped(compliRvwOutputStream, null);
      // Parse output json file of compli review service
      InputStream jsonInpStream = mapOfInpStream.get("CompliReviewResult.json");
      compliRvwResponse = new JsonUtil().toModel(jsonInpStream, CompliReviewResponse.class);
    }
    catch (IOException | InvalidInputException exp) {
      throw new InvalidInputException(exp.getMessage(), exp);
    }
    return compliRvwResponse;
  }
}
