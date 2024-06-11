/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.cdr;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.bosch.caltool.icdm.model.cdr.ReviewDataInputModel;
import com.bosch.caltool.icdm.model.cdr.ReviewDataParamResponse;
import com.bosch.caltool.icdm.model.cdr.ReviewParamResponse;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestClientTest;


/**
 * @author dja7cob Test class for service to fetch work package resources
 */
public class ReviewDataSerClientTest extends AbstractRestClientTest {


  /**
   * Test method for {@link com.bosch.caltool.icdm.ws.rest.client.a2l.FunctionServiceClient#getFunctionsByName(List)}.
   */
  @Test
  public void testGetReviewData() {
    ReviewDataServiceClient service = new ReviewDataServiceClient();
    try {

      ReviewDataInputModel model = new ReviewDataInputModel();
      List<String> paramNames = new ArrayList<>();
      paramNames.add("KFKSTTDST");
      model.setParamName(paramNames);
      model.setVarCodedParam("KFKSTTDST[0]");
      ReviewDataParamResponse reviewDataParamResponse = service.getReviewData(model);
      Map<String, ReviewParamResponse> ruleDescMap = reviewDataParamResponse.getReviewParamResponse();
      assertFalse("response not empty", (ruleDescMap == null) || ruleDescMap.isEmpty());
      testOutput(ruleDescMap);
    }
    catch (Exception e) {
      LOG.error("Error in WS call", e);
      assertNull("Error in WS call", e);
    }


  }

  /**
   * @param string
   * @param wpRes
   */
  private void testOutput(final Map<String, ReviewParamResponse> ruleDescMap) {
    assertFalse("response not empty", (ruleDescMap == null) || ruleDescMap.isEmpty());
    assertTrue("Size of ReviewParamResponse fetched ", (ruleDescMap != null) && (ruleDescMap.size() == 7));
    if (ruleDescMap != null) {
      LOG.debug("Response size  = {}", ruleDescMap.size());
    for (ReviewParamResponse model : ruleDescMap.values()) {
      testOutput("Test First Response item", model);
    }
    }

  }

  /**
   * @param string
   * @param wpRes
   */
  private void testOutput(final String string, final ReviewParamResponse reviewParamResp) {
    assertNotNull(string + ": object not null", reviewParamResp);
    LOG.debug("{}: Function Id = {}; {}", string, reviewParamResp.getLongName(), reviewParamResp.getLongName());

  }
}
